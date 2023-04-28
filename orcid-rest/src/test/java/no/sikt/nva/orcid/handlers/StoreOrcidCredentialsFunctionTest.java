package no.sikt.nva.orcid.handlers;

import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomBoolean;
import static no.unit.nva.testutils.RandomDataGenerator.randomInteger;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Clock;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.service.OrcidService;
import no.sikt.nva.orcid.commons.service.OrcidServiceImpl;
import no.sikt.nva.orcid.testutils.service.OrcidLocalTestDatabase;
import no.sikt.nva.orcid.utils.FakeOrcidServiceImplThrowingException;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StoreOrcidCredentialsFunctionTest extends OrcidLocalTestDatabase {

    private static final Context CONTEXT = mock(Context.class);
    private static final String ORCID_TABLE_NAME = "someOrcidTable";
    private final String testUserName = randomString();
    private final URI testOrgId = randomUri();
    private final URI topLevelCristinOrgId = randomUri();
    private StoreOrcidCredentialsFunction handler;
    private ByteArrayOutputStream outputStream;
    private OrcidService orcidService;
    private Clock clock;

    @BeforeEach
    public void init() {
        super.init(ORCID_TABLE_NAME);
        this.clock = Clock.systemDefaultZone();
        this.orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
        handler = new StoreOrcidCredentialsFunction(orcidService);
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void shouldStoreOrcidCredentials() throws IOException {
        var orcidCredentials = randomOrcidCredentials();
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials)) {
            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CREATED)));

        var persistedOrcidCredentials = orcidService.fetchOrcidCredentialsByOrcid(orcidCredentials.getOrcid());
        assertThat(persistedOrcidCredentials.getModified(), is(notNullValue()));
        assertThat(persistedOrcidCredentials.getCreated(), is(notNullValue()));
        assertThat(persistedOrcidCredentials.getCreated(), is(equalTo(persistedOrcidCredentials.getModified())));

        persistedOrcidCredentials.setCreated(null);
        persistedOrcidCredentials.setModified(null);

        assertThat(persistedOrcidCredentials, is(equalTo(orcidCredentials)));
    }

    @Test
    public void shouldReturnBadGatewayWhenOrcidServiceIsUnreachable() throws IOException {
        var orcidCredentials = randomOrcidCredentials();
        var fakeOrcidServiceThrowingException = new FakeOrcidServiceImplThrowingException();
        handler = new StoreOrcidCredentialsFunction(fakeOrcidServiceThrowingException);
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_BAD_GATEWAY)));
    }

    @Test
    public void shouldReturnConflictIfOrcidCredentialsAlreadyExists() throws IOException {
        var orcidCredentials = randomOrcidCredentials();
        orcidService.createOrcidCredentials(orcidCredentials);
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CONFLICT)));
    }

    private InputStream createOrcidCredentialsRequestFromString(OrcidCredentials orcidCredentials)
        throws JsonProcessingException {
        var request = dtoObjectMapper.writeValueAsString(orcidCredentials);
        return new HandlerRequestBuilder<String>(dtoObjectMapper)
                   .withUserName(testUserName)
                   .withCurrentCustomer(testOrgId)
                   .withTopLevelCristinOrgId(topLevelCristinOrgId)
                   .withBody(request)
                   .build();
    }

    private OrcidCredentials randomOrcidCredentials() {
        return OrcidCredentials.builder()
                   .withOrcid(randomUri())
                   .withCreated(null)
                   .withModified(null)
                   .withTokenId(randomInteger())
                   .withTokenVersion(randomString())
                   .withExpiresIn(randomInteger())
                   .withPersistent(randomBoolean())
                   .withIdToken(randomString())
                   .withTokenType(randomString())
                   .withAccessToken(randomString())
                   .build();
    }
}
