package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.commons.service.ServiceWithTransactions.newTransactWriteItemsRequest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import java.net.URI;
import java.time.Clock;
import java.util.function.Supplier;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;

public class OrcidServiceImpl implements OrcidService {

    public static final String ERROR_READING_RESULT = "Error reading result";
    private final Clock clockForTimestamps;
    private final ServiceWithTransactions serviceWithTransactions;
    private final ReadOrcidCredentialsService readOrcidCredentialsService;

    public OrcidServiceImpl(String orcidTableName, AmazonDynamoDB client, Clock clock) {
        this.serviceWithTransactions = new ServiceWithTransactions(client, orcidTableName);
        this.clockForTimestamps = clock;
        this.readOrcidCredentialsService = new ReadOrcidCredentialsService(client, orcidTableName);
    }

    @Override
    public OrcidCredentials createOrcidCredentials(OrcidCredentials orcidCredentials) {
        var currentTime = clockForTimestamps.instant();
        orcidCredentials.setModified(currentTime);
        orcidCredentials.setCreated(currentTime);
        return insertOrcidCredentials(orcidCredentials);
    }

    @Override
    public OrcidCredentials fetchOrcidCredentialsByOrcid(URI orcid) {
        var orcidCredentials = new OrcidCredentials();
        orcidCredentials.setOrcid(orcid);
        return readOrcidCredentialsService.getOrcidCredentials(orcidCredentials);
    }

    private OrcidCredentials insertOrcidCredentials(OrcidCredentials orcidCredentials) {
        var transactionItem = transactionItemsForOrcidCredentialsInsertion(orcidCredentials);
        var putRequest = newTransactWriteItemsRequest(transactionItem);
        serviceWithTransactions.sendTransactionWriteRequest(putRequest);
        return fetchSavedOrcidCredentials(orcidCredentials);
    }

    private TransactWriteItem transactionItemsForOrcidCredentialsInsertion(OrcidCredentials orcidCredentials) {
        return
            serviceWithTransactions.newPutTransactionItem(new OrcidCredentialsDao(orcidCredentials));
    }

    private OrcidCredentials fetchSavedOrcidCredentials(OrcidCredentials orcidCredentials) {
        var retryRegistry = RetryRegistry.ofDefaults();
        var retry = retryRegistry.retry("fetch orcid credentials");
        Supplier<OrcidCredentials> orcidCredentialsCheckedSupplier =
            () -> readOrcidCredentialsService.getOrcidCredentials(orcidCredentials);
        return Try.ofSupplier(Retry.decorateSupplier(retry, orcidCredentialsCheckedSupplier))
                   .getOrElseThrow(this::handleReadFailure);
    }

    private RuntimeException handleReadFailure(Throwable fail) {
        throw new TransactionFailedException(fail, ERROR_READING_RESULT);
    }
}
