package no.sikt.nva.orcid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Optional;

public record CristinPersonResponse(Collection<CristinPersonIdentifier> identifiers) {

    @JsonIgnore
    public Optional<String> getOrcid() {
        return identifiers
                   .stream()
                   .filter(CristinPersonIdentifier::isOrcid)
                   .map(CristinPersonIdentifier::value)
                   .findFirst();
    }
}
