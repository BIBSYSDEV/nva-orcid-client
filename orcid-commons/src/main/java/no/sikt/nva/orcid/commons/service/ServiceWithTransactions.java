package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.constants.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.Put;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItem;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsRequest;
import com.amazonaws.services.dynamodbv2.model.TransactWriteItemsResult;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;
import nva.commons.core.attempt.Failure;
import nva.commons.core.attempt.FunctionWithException;

public class ServiceWithTransactions {

    public static final String PARTITION_KEY_NAME_PLACEHOLDER = "#partitionKey";
    public static final String KEY_NOT_EXISTS_CONDITION = keyNotExistsCondition();
    public static final Map<String, String> PRIMARY_KEY_EQUALITY_CONDITION_ATTRIBUTE_NAMES =
        primaryKeyEqualityConditionAttributeNames();
    public static final int AWAIT_TIME_BEFORE_FETCH_RETRY = 50;
    private static final Integer MAX_FETCH_ATTEMPTS = 3;
    private final AmazonDynamoDB client;
    private final String tableName;

    protected ServiceWithTransactions(AmazonDynamoDB client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    protected static TransactWriteItemsRequest newTransactWriteItemsRequest(TransactWriteItem... transaction) {
        return newTransactWriteItemsRequest(Arrays.asList(transaction));
    }

    protected static TransactWriteItemsRequest newTransactWriteItemsRequest(List<TransactWriteItem> transactionItems) {
        return new TransactWriteItemsRequest().withTransactItems(transactionItems);
    }

    protected final AmazonDynamoDB getClient() {
        return client;
    }

    protected void sendTransactionWriteRequest(TransactWriteItemsRequest transactWriteItemsRequest) {
        attempt(() -> getClient().transactWriteItems(transactWriteItemsRequest))
            .orElseThrow(this::handleTransactionFailure);
    }

    protected TransactWriteItem newPutTransactionItem(OrcidCredentialsDao data) {
        Put put = new Put()
                      .withItem(data.toDynamoFormat())
                      .withTableName(tableName)
                      .withConditionExpression(KEY_NOT_EXISTS_CONDITION)
                      .withExpressionAttributeNames(PRIMARY_KEY_EQUALITY_CONDITION_ATTRIBUTE_NAMES);
        return new TransactWriteItem().withPut(put);
    }

    protected <T extends OrcidCredentials, E extends Exception> Optional<T> fetchEventualConsistentDataEntry(
        T dynamoEntry,
        FunctionWithException<T, T, E> nonEventuallyConsistentFetch) {
        T savedEntry = null;
        for (int times = 0; times < MAX_FETCH_ATTEMPTS && savedEntry == null; times++) {
            savedEntry = attempt(() -> nonEventuallyConsistentFetch.apply(dynamoEntry)).orElse(fail -> null);
            attempt(this::waitBeforeFetching).orElseThrow();
        }
        return Optional.ofNullable(savedEntry);
    }

    private static String keyNotExistsCondition() {
        return String.format("attribute_not_exists(%s)",
                             PARTITION_KEY_NAME_PLACEHOLDER);
    }

    private static Map<String, String> primaryKeyEqualityConditionAttributeNames() {
        return Map.of(
            PARTITION_KEY_NAME_PLACEHOLDER, ORCID_PRIMARY_PARTITION_KEY
        );
    }

    private TransactionFailedException handleTransactionFailure(Failure<TransactWriteItemsResult> fail) {
        return new TransactionFailedException(fail.getException());
    }

    private Void waitBeforeFetching() throws InterruptedException {
        Thread.sleep(AWAIT_TIME_BEFORE_FETCH_RETRY);
        return null;
    }
}
