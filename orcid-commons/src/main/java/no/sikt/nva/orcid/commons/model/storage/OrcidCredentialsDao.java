package no.sikt.nva.orcid.commons.model.storage;

import static nva.commons.core.attempt.Try.attempt;
import java.util.Map;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.unit.nva.commons.json.JsonUtils;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class OrcidCredentialsDao {

    private final OrcidCredentials orcidCredentials;

    public OrcidCredentialsDao(OrcidCredentials orcidCredentials) {
        this.orcidCredentials = orcidCredentials;
    }

    public OrcidCredentialsDao(Map<String, AttributeValue> valuesMap) {
        this.orcidCredentials = fromDynamoFormat(valuesMap);
    }

    public OrcidCredentials getOrcidCredentials() {
        return orcidCredentials;
    }

    public Map<String, AttributeValue> toDynamoFormat() {
        return attempt(() -> EnhancedDocument.fromJson(
            JsonUtils.dynamoObjectMapper.writeValueAsString(this.getOrcidCredentials())).toMap()).orElseThrow();
    }

    private static OrcidCredentials fromDynamoFormat(Map<String, AttributeValue> valuesMap) {
        var json = EnhancedDocument.fromAttributeValueMap(valuesMap).toJson();
        return attempt(() -> JsonUtils.dynamoObjectMapper.readValue(json, OrcidCredentials.class)).orElseThrow();
    }
}
