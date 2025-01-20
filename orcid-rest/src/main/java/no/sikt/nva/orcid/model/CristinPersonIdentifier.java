package no.sikt.nva.orcid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CristinPersonIdentifier(String type, String value) {

    @JsonIgnore
    public boolean isOrcid() {
        return "ORCID".equalsIgnoreCase(type);
    }
}
