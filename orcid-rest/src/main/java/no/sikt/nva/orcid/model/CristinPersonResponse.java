package no.sikt.nva.orcid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nva.commons.core.JacocoGenerated;

@Data
@Builder(
    builderClassName = "CristinPersonResponseBuilder",
    toBuilder = true,
    builderMethodName = "builder",
    buildMethodName = "build",
    setterPrefix = "with"
)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CristinPersonResponse {

    @JsonProperty("identifiers")
    private List<CristinPersonIdentifier> identifiers;

    @JacocoGenerated
    public CristinPersonResponse() {
    }

    @JsonIgnore
    public Optional<String> getOrcid() {
        return identifiers.stream()
                   .filter(CristinPersonIdentifier::isOrcid)
                   .map(CristinPersonIdentifier::getValue)
                   .findFirst();
    }
}
