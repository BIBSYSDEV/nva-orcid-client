package no.sikt.nva.orcid.commons.model.storage;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import java.util.Map;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.unit.nva.commons.json.JsonUtils;

public interface DynamoEntry {

    static OrcidCredentials parseAttributeValuesMap(Map<String, AttributeValue> valuesMap) {

        Item item = ItemUtils.toItem(valuesMap);
        return attempt(() -> JsonUtils.dtoObjectMapper.readValue(item.toJSON(), OrcidCredentials.class)).orElseThrow();
    }
}
