package no.sikt.nva.orcid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nva.commons.core.JacocoGenerated;

@Data
@Builder(
    builderClassName = "CristinPersonIdentifierBuilder",
    toBuilder = true,
    builderMethodName = "builder",
    buildMethodName = "build",
    setterPrefix = "with"
)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CristinPersonIdentifier {

    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private String value;

    @JacocoGenerated
    public CristinPersonIdentifier() {
    }

    @JsonIgnore
    public boolean isOrcid() {
        return "ORCID".equalsIgnoreCase(type);
    }
}
