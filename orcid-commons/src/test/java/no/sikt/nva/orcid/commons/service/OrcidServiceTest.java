package no.sikt.nva.orcid.commons.service;

import no.sikt.nva.orcid.testutils.service.OrcidLocalTestDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static no.sikt.nva.orcid.commons.utils.RandomOrcidCredentialsGenerator.randomOrcidCredentials;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class OrcidServiceTest extends OrcidLocalTestDatabase {

    private static final String ORCID_TABLE_NAME = "someOrcidTableName";

    private OrcidServiceImpl orcidService;


    @BeforeEach
    public void initialize() {
        super.init(ORCID_TABLE_NAME);
        this.orcidService = new OrcidServiceImpl();

    }

    @Test
    public void dummyTest(){
        var orcidCredentials = randomOrcidCredentials();
        var persistedOrcidCredentials = orcidService.insertOrcidCredentials(orcidCredentials);
        assertThat(persistedOrcidCredentials, is(nullValue()));
    }
}
