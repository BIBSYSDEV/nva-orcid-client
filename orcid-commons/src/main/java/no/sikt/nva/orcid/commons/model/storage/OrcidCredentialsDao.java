package no.sikt.nva.orcid.commons.model.storage;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.Map;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.unit.nva.commons.json.JsonUtils;

public class OrcidCredentialsDao implements DynamoEntry {

    private final OrcidCredentials orcidCredentials;

    public OrcidCredentialsDao(OrcidCredentials orcidCredentials) {
        this.orcidCredentials = orcidCredentials;
    }

    public OrcidCredentials getOrcidCredentials() {
        return orcidCredentials;
    }

    public Map<String, AttributeValue> toDynamoFormat() {
        Item item = attempt(() -> Item.fromJSON(JsonUtils.dynamoObjectMapper.writeValueAsString(this.getOrcidCredentials()))).orElseThrow();
        return ItemUtils.toAttributeValues(item);
    }
}
