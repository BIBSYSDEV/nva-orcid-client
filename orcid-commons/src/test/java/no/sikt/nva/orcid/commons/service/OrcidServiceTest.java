package no.sikt.nva.orcid.commons.service;

import java.time.Clock;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import no.sikt.nva.orcid.testutils.service.OrcidLocalTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static no.sikt.nva.orcid.commons.utils.RandomOrcidCredentialsGenerator.randomOrcidCredentials;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class OrcidServiceTest extends OrcidLocalTest {

    private static final String ORCID_TABLE_NAME = "someOrcidTableName";

    private Clock clock;
    private OrcidServiceImpl orcidService;


    @BeforeEach
    public void initialize() {
        super.init(ORCID_TABLE_NAME);
        this.clock = Clock.systemDefaultZone();
//        this.orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
        this.orcidService = new OrcidServiceImpl();

    }

    @Test
    public void dummyTest(){
        var orcidCredentials = randomOrcidCredentials();
        var persistedOrcidCredentials = orcidService.insertOrcidCredentials(orcidCredentials);
        assertThat(persistedOrcidCredentials, is(nullValue()));
    }

//    @Test
//    public void shouldBePossibleToStoreOrcidCredentials() {
//        var orcidCredentials = randomOrcidCredentials();
//        var persistedOrcidCredentials = orcidService.insertOrcidCredentials(orcidCredentials);
//        assertThat(persistedOrcidCredentials, is(equalTo(orcidCredentials)));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfCredentialsAlreadyExists() {
//        var orcidCredentials = randomOrcidCredentials();
//        orcidService.insertOrcidCredentials(orcidCredentials);
//        assertThrows(Exception.class, () -> orcidService.insertOrcidCredentials(orcidCredentials));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfDynamoIsWorking() {
//        var orcidCredentials = randomOrcidCredentials();
//        client = mock(AmazonDynamoDB.class);
//        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
//        assertThrows(Exception.class, () -> orcidService.insertOrcidCredentials(orcidCredentials));
//    }
}
