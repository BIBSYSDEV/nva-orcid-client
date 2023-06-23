package no.sikt.nva.orcid.commons.service;

import static java.util.Objects.isNull;
import static no.sikt.nva.orcid.constants.OrcidConstants.ORCID_PRIMARY_PARTITION_KEY;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.kms.model.NotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;

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
        var partKeyValue = new AttributeValue(orcidCredentials.orcid().toString());
        map.put(ORCID_PRIMARY_PARTITION_KEY, partKeyValue);
        return map;
    }

    protected OrcidCredentials getOrcidCredentials(OrcidCredentials orcidCredentials) {
        var primaryKey = primaryKey(orcidCredentials);
        var item = getResourceByPrimaryKey(primaryKey);
        return new OrcidCredentialsDao(item).getOrcidCredentials();
    }

    private Map<String, AttributeValue> getResourceByPrimaryKey(Map<String, AttributeValue> primaryKey) {
        var result = client.getItem(new GetItemRequest()
                                                  .withTableName(orcidTableName)
                                                  .withKey(primaryKey));
        if (isNull(result.getItem())) {
            throw new NotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return result.getItem();
    }
}
