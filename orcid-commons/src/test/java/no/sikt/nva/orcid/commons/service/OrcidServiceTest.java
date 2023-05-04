package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.commons.utils.RandomOrcidCredentialsGenerator.randomOrcidCredentials;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.kms.model.NotFoundException;
import java.time.Clock;
import java.util.Map;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;
import no.sikt.nva.orcid.testutils.service.OrcidLocalTestDatabase;
import nva.commons.core.paths.UriWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrcidServiceTest extends OrcidLocalTestDatabase {

    private static final String ORCID_TABLE_NAME = "someOrcidTableName";

    private OrcidServiceImpl orcidService;
    private Clock clock;

    @BeforeEach
    public void initialize() {
        super.init(ORCID_TABLE_NAME);
        this.clock = Clock.systemDefaultZone();
        this.orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
    }

    @Test
    public void shouldBePossibleToStoreOrcidCredentials() {
        var orcidCredentials = randomOrcidCredentials();
        var persistedOrcidCredentials = orcidService.createOrcidCredentials(orcidCredentials.copy());
        assertThat(persistedOrcidCredentials.getModified(), is(notNullValue()));
        assertThat(persistedOrcidCredentials.getCreated(), is(notNullValue()));
        assertThat(persistedOrcidCredentials.getCreated(), is(equalTo(persistedOrcidCredentials.getModified())));
        persistedOrcidCredentials.setCreated(null);
        persistedOrcidCredentials.setModified(null);
        assertThat(persistedOrcidCredentials, is(equalTo(orcidCredentials)));
    }

    @Test
    public void shouldThrowExceptionIfCredentialsAlreadyExists() {
        var orcidCredentials = randomOrcidCredentials();
        orcidService.createOrcidCredentials(orcidCredentials.copy());
        assertThrows(TransactionFailedException.class, () -> orcidService.createOrcidCredentials(orcidCredentials));
    }

    @Test
    public void shouldThrowExceptionIfDynamoIsNotWorking() {
        client = mock(AmazonDynamoDB.class);
        doThrow(RuntimeException.class).when(client).transactWriteItems(any());
        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
        var orcidCredentials = randomOrcidCredentials();
        assertThrows(TransactionFailedException.class, () -> orcidService.createOrcidCredentials(orcidCredentials));
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenTryingToFetchNonExistingOrcidCredentials() {
        var orcid = randomUri();
        assertThrows(NotFoundException.class, () -> orcidService.fetchOrcidCredentialsByOrcid(orcid));
    }

    @Test
    public void shouldThrowExceptionWhenOrcidIsNull() {
        var orcid = UriWrapper.fromUri("").getUri();
        assertThrows(AmazonServiceException.class, () -> orcidService.fetchOrcidCredentialsByOrcid(orcid));
    }

    @Test
    public void shouldBePossibleToRetrieveOrcidCredentials() {
        var persistedCredentials = orcidService.createOrcidCredentials(randomOrcidCredentials().copy());
        var retrievedCredentials = orcidService.fetchOrcidCredentialsByOrcid(persistedCredentials.getOrcid());
        assertThat(retrievedCredentials, is(equalTo(retrievedCredentials)));
    }

    @Test
    public void shouldNotGiveUpOnFirstTryToSaveCredentials() {
        var orcidCredentials = randomOrcidCredentials();
        var itemResult = generateItemResult(orcidCredentials);
        client = mock(AmazonDynamoDB.class);
        when(client.getItem(any()))
            .thenThrow(RuntimeException.class)
            .thenReturn(itemResult);
        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);

        var persistedCredentials = orcidService.createOrcidCredentials(orcidCredentials.copy());
        assertThat(persistedCredentials, is(equalTo(orcidCredentials)));
    }

    private GetItemResult generateItemResult(OrcidCredentials orcidCredentials) {
        var getItemResult = new GetItemResult();
        getItemResult.withItem(generateAttributes(orcidCredentials));
        return getItemResult;
    }

    private Map<String, AttributeValue> generateAttributes(OrcidCredentials orcidCredentials) {
        var orcidCredentialsDao = new OrcidCredentialsDao(orcidCredentials);
        return orcidCredentialsDao.toDynamoFormat();
    }
}
