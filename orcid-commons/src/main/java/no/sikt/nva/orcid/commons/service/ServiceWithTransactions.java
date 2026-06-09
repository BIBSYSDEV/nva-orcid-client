package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.commons.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import static nva.commons.core.attempt.Try.attempt;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;
import nva.commons.core.attempt.Failure;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.Put;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;

public class ServiceWithTransactions {

    public static final String PARTITION_KEY_NAME_PLACEHOLDER = "#partitionKey";
    public static final String KEY_NOT_EXISTS_CONDITION = keyNotExistsCondition();
    public static final Map<String, String> PRIMARY_KEY_EQUALITY_CONDITION_ATTRIBUTE_NAMES =
        primaryKeyEqualityConditionAttributeNames();
    private final DynamoDbClient client;
    private final String tableName;

    protected ServiceWithTransactions(DynamoDbClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    protected static TransactWriteItemsRequest newTransactWriteItemsRequest(TransactWriteItem... transaction) {
        return newTransactWriteItemsRequest(Arrays.asList(transaction));
    }

    protected static TransactWriteItemsRequest newTransactWriteItemsRequest(List<TransactWriteItem> transactionItems) {
        return TransactWriteItemsRequest.builder().transactItems(transactionItems).build();
    }

    protected final DynamoDbClient getClient() {
        return client;
    }

    protected void sendTransactionWriteRequest(TransactWriteItemsRequest transactWriteItemsRequest) {
        attempt(() -> getClient().transactWriteItems(transactWriteItemsRequest))
            .orElseThrow(this::handleTransactionFailure);
    }

    protected TransactWriteItem newPutTransactionItem(OrcidCredentialsDao data) {
        var put = Put.builder()
                      .item(data.toDynamoFormat())
                      .tableName(tableName)
                      .conditionExpression(KEY_NOT_EXISTS_CONDITION)
                      .expressionAttributeNames(PRIMARY_KEY_EQUALITY_CONDITION_ATTRIBUTE_NAMES)
                      .build();
        return TransactWriteItem.builder().put(put).build();
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

    private TransactionFailedException handleTransactionFailure(Failure<TransactWriteItemsResponse> fail) {
        return new TransactionFailedException(fail.getException());
    }
}
