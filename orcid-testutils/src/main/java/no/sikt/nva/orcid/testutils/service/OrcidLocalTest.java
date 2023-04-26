package no.sikt.nva.orcid.testutils.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import nva.commons.core.JacocoGenerated;
import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static no.sikt.nva.orcid.constants.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;

@JacocoGenerated
public class OrcidLocalTest {

    public static final ScalarAttributeType STRING_TYPE = ScalarAttributeType.S;
    protected AmazonDynamoDB client;
    private String orcidTableName;

    public void setOrcidTableName(String orcidTableName) {
        this.orcidTableName = orcidTableName;
    }

    public void init(String orcidTableName) {
        setOrcidTableName(orcidTableName);
        client = DynamoDBEmbedded.create().amazonDynamoDB();
        CreateTableRequest request = createTableRequest();
        client.createTable(request);
    }

    @AfterEach
    public void shutdown() {
        client.shutdown();
    }

    private CreateTableRequest createTableRequest() {
        return new CreateTableRequest()
                .withTableName(orcidTableName)
                .withAttributeDefinitions(attributeDefinitions())
                .withKeySchema(primaryKeySchema())
                .withBillingMode(BillingMode.PAY_PER_REQUEST);
    }

    private AttributeDefinition[] attributeDefinitions() {
        List<AttributeDefinition> attributesList = new ArrayList<>();
        attributesList.add(newAttribute(ORCID_PRIMARY_PARTITION_KEY));
        AttributeDefinition[] attributesArray = new AttributeDefinition[attributesList.size()];
        attributesList.toArray(attributesArray);
        return attributesArray;
    }

    private Collection<KeySchemaElement> primaryKeySchema() {
        return keySchema(ORCID_PRIMARY_PARTITION_KEY);
    }

    private Collection<KeySchemaElement> keySchema(String hashKey) {
        List<KeySchemaElement> primaryKey = new ArrayList<>();
        primaryKey.add(newKeyElement(hashKey, KeyType.HASH));
        return primaryKey;
    }

    private KeySchemaElement newKeyElement(String primaryKeySortKeyName, KeyType range) {
        return new KeySchemaElement().withAttributeName(primaryKeySortKeyName).withKeyType(range);
    }

    private AttributeDefinition newAttribute(String keyName) {
        return new AttributeDefinition()
                .withAttributeName(keyName)
                .withAttributeType(STRING_TYPE);
    }
}
