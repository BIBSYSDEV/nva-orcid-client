package no.sikt.nva.orcid.utils;

import java.net.URI;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.service.OrcidService;

public class FakeOrcidServiceImplThrowingException implements OrcidService {

    @Override
    public OrcidCredentials createOrcidCredentials(OrcidCredentials orcidCredentials) {
        throw new RuntimeException("I don't work");
    }

    @Override
    public OrcidCredentials fetchOrcidCredentialsByOrcid(URI orcid) {
        throw new RuntimeException("I don't work");
    }
}
