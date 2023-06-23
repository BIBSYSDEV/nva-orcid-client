package no.sikt.nva.orcid.commons.service;

import java.net.URI;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;

public interface OrcidService {

    OrcidCredentials createOrcidCredentials(OrcidCredentials orcidCredentials);

    OrcidCredentials fetchOrcidCredentialsByOrcid(URI orcid);
}
