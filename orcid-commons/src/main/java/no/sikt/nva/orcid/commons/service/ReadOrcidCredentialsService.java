package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.commons.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import java.util.Map;
import java.util.NoSuchElementException;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

public class ReadOrcidCredentialsService {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find orcidCredentials";
    private final DynamoDbClient client;
    private final String orcidTableName;

    public ReadOrcidCredentialsService(DynamoDbClient client, String orcidTableName) {
        this.client = client;
        this.orcidTableName = orcidTableName;
    }

    public Map<String, AttributeValue> primaryKey(OrcidCredentials orcidCredentials) {
        var partKeyValue = AttributeValue.builder().s(orcidCredentials.orcid().toString()).build();
        return Map.of(ORCID_PRIMARY_PARTITION_KEY, partKeyValue);
    }

    protected OrcidCredentials getOrcidCredentials(OrcidCredentials orcidCredentials) {
        var primaryKey = primaryKey(orcidCredentials);
        var item = getResourceByPrimaryKey(primaryKey);
        return new OrcidCredentialsDao(item).getOrcidCredentials();
    }

    private Map<String, AttributeValue> getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey) {
        var result = client.getItem(GetItemRequest.builder()
                                        .tableName(orcidTableName)
                                        .key(primaryKey)
                                        .build());
        if (!result.hasItem()) {
            throw new NoSuchElementException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.item();
    }
}
