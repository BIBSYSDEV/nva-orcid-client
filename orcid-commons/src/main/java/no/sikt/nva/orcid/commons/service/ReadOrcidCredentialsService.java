package no.sikt.nva.orcid.commons.service;

import static java.util.Objects.isNull;
import static no.sikt.nva.orcid.commons.model.storage.DynamoEntry.parseAttributeValuesMap;
import static no.sikt.nva.orcid.constants.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.kms.model.NotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;

public class ReadOrcidCredentialsService {

    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Could not find orcidCredentials";
    private final AmazonDynamoDB client;
    private final String orcidTableName;

    public ReadOrcidCredentialsService(AmazonDynamoDB client, String orcidTableName) {
        this.client = client;
        this.orcidTableName = orcidTableName;
    }

    public Map<String, AttributeValue> primaryKey(OrcidCredentials orcidCredentials) {
        final Map<String, AttributeValue> map = new ConcurrentHashMap<>();
        AttributeValue partKeyValue = new AttributeValue(orcidCredentials.getOrcid().toString());
        map.put(ORCID_PRIMARY_PARTITION_KEY, partKeyValue);
        return map;
    }

    protected OrcidCredentials getOrcidCredentials(OrcidCredentials orcidCredentials) {
        Map<String, AttributeValue> primaryKey = primaryKey(orcidCredentials);
        GetItemResult getResult = getResourceByPrimaryKey(primaryKey);
        return parseAttributeValuesMap(getResult.getItem());
    }

    private GetItemResult getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey) {
        GetItemResult result = client.getItem(new GetItemRequest()
                                                  .withTableName(orcidTableName)
                                                  .withKey(primaryKey));
        if (isNull(result.getItem())) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result;
    }
}
