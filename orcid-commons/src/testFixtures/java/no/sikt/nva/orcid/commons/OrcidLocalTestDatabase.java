package no.sikt.nva.orcid.commons;

import static no.sikt.nva.orcid.commons.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import java.util.Collection;
import java.util.List;
import nva.commons.core.JacocoGenerated;
import org.junit.jupiter.api.AfterEach;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@JacocoGenerated
public class OrcidLocalTestDatabase {

    public static final ScalarAttributeType STRING_TYPE = ScalarAttributeType.S;
    protected DynamoDbClient client;
    private String orcidTableName;

    public void setOrcidTableName(String orcidTableName) {
        this.orcidTableName = orcidTableName;
    }

    public void init(String orcidTableName) {
        setOrcidTableName(orcidTableName);
        client = DynamoDBEmbedded.create(null, true).dynamoDbClient();
        var request = createTableRequest();
        client.createTable(request);
    }

    @AfterEach
    public void shutdown() {
        client.close();
    }

    private CreateTableRequest createTableRequest() {
        return CreateTableRequest.builder()
                   .tableName(orcidTableName)
                   .attributeDefinitions(attributeDefinitions())
                   .keySchema(primaryKeySchema())
                   .billingMode(BillingMode.PAY_PER_REQUEST)
                   .build();
    }

    private Collection<AttributeDefinition> attributeDefinitions() {
        return List.of(newAttribute(ORCID_PRIMARY_PARTITION_KEY));
    }

    private Collection<KeySchemaElement> primaryKeySchema() {
        return keySchema(ORCID_PRIMARY_PARTITION_KEY);
    }

    private Collection<KeySchemaElement> keySchema(String hashKey) {
        return List.of(newKeyElement(hashKey, KeyType.HASH));
    }

    private KeySchemaElement newKeyElement(String primaryKeySortKeyName, KeyType range) {
        return KeySchemaElement.builder().attributeName(primaryKeySortKeyName).keyType(range).build();
    }

    private AttributeDefinition newAttribute(String keyName) {
        return AttributeDefinition.builder()
                   .attributeName(keyName)
                   .attributeType(STRING_TYPE)
                   .build();
    }
}
