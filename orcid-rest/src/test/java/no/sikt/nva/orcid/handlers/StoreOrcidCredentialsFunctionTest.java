package no.sikt.nva.orcid.handlers;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static no.unit.nva.commons.json.JsonUtils.dtoObjectMapper;
import static no.unit.nva.testutils.RandomDataGenerator.randomBoolean;
import static no.unit.nva.testutils.RandomDataGenerator.randomInteger;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Path;
import java.time.Clock;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.service.OrcidService;
import no.sikt.nva.orcid.commons.service.OrcidServiceImpl;
import no.sikt.nva.orcid.testutils.service.OrcidLocalTestDatabase;
import no.sikt.nva.orcid.utils.FakeOrcidServiceImplThrowingException;
import no.unit.nva.stubs.WiremockHttpClient;
import no.unit.nva.testutils.HandlerRequestBuilder;
import nva.commons.apigateway.GatewayResponse;
import nva.commons.core.Environment;
import nva.commons.core.ioutils.IoUtils;
import nva.commons.core.paths.UriWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WireMockTest(httpsEnabled = true)
class StoreOrcidCredentialsFunctionTest extends OrcidLocalTestDatabase {

    private static final Context CONTEXT = mock(Context.class);
    private static final String ORCID_TABLE_NAME = "someOrcidTable";
    private final String testUserName = "123@185.39.55.0";
    private final URI orcidForTestUser = UriWrapper.fromUri("https://sandbox.orcid.org/0000-0001-3121-1236").getUri();
    private final URI testOrgId = randomUri();
    private final URI topLevelCristinOrgId = randomUri();
    private StoreOrcidCredentialsFunction handler;
    private ByteArrayOutputStream outputStream;
    private OrcidService orcidService;
    private UserOrcidResolver userOrcidResolver;

    @BeforeEach
    void init(WireMockRuntimeInfo wireMockRuntimeInfo) {
        super.init(ORCID_TABLE_NAME);
        stubForPersonResponse();
        this.orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, Clock.systemDefaultZone());
        this.userOrcidResolver = new UserOrcidResolver(WiremockHttpClient.create(),
                                                       wireMockRuntimeInfo.getHttpsBaseUrl().replace(
                                                           "https://", ""));
        this.handler = new StoreOrcidCredentialsFunction(orcidService, userOrcidResolver, new Environment());
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    void shouldStoreOrcidCredentials() throws IOException {

        var orcidCredentials = generateOrcidCredentials(orcidForTestUser);
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, testUserName)) {
            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CREATED)));

        var persistedOrcidCredentials = orcidService.fetchOrcidCredentialsByOrcid(orcidCredentials.orcid());
        assertThat(persistedOrcidCredentials.hasSameCredentials(orcidCredentials), is(true));
    }

    @Test
    void shouldReturnBadGatewayWhenOrcidServiceIsUnreachable() throws IOException {
        var orcidCredentials = generateOrcidCredentials(orcidForTestUser);
        var fakeOrcidServiceThrowingException = new FakeOrcidServiceImplThrowingException();
        handler = new StoreOrcidCredentialsFunction(fakeOrcidServiceThrowingException, userOrcidResolver,
                                                    new Environment());
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, testUserName)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_BAD_GATEWAY)));
    }

    @Test
    void shouldReturnConflictIfOrcidCredentialsAlreadyExists() throws IOException {
        var orcidCredentials = generateOrcidCredentials(orcidForTestUser);
        orcidService.createOrcidCredentials(orcidCredentials);
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, testUserName)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_CONFLICT)));
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotHaveTheOrcidTheyAreTryingToSubmit() throws IOException {
        var orcidCredentials = generateOrcidCredentials(UriWrapper.fromUri("https://sandbox.orcid"
                + ".org/0000-0001-3121-1234").getUri());
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, testUserName)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_FORBIDDEN)));
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotHaveOrcid() throws IOException {
        var orcidCredentials = generateOrcidCredentials(orcidForTestUser);
        var userWithoutOrcid = "someuser@185.39.55.0";
        stubPersonResponseWithouthOrcid("someuser");
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, userWithoutOrcid)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_FORBIDDEN)));
    }

    @Test
    void shouldReturnBadGatewayIfCristinApiIsUnavailable() throws IOException {
        var orcidCredentials = generateOrcidCredentials(orcidForTestUser);
        var user = "someuser@185.39.55.0";
        stubInternalServerResponse("someuser");
        try (var inputStream = createOrcidCredentialsRequestFromString(orcidCredentials, user)) {

            handler.handleRequest(inputStream, outputStream, CONTEXT);
        }
        var response = GatewayResponse.fromOutputStream(outputStream, Void.class);
        assertThat(response.getStatusCode(), is(equalTo(HttpURLConnection.HTTP_BAD_GATEWAY)));
    }

    private InputStream createOrcidCredentialsRequestFromString(OrcidCredentials orcidCredentials, String userName)
            throws JsonProcessingException {
        var request = dtoObjectMapper.writeValueAsString(orcidCredentials);
        return new HandlerRequestBuilder<String>(dtoObjectMapper)
                .withUserName(userName)
                .withCurrentCustomer(testOrgId)
                .withTopLevelCristinOrgId(topLevelCristinOrgId)
                .withBody(request)
                .build();
    }

    private OrcidCredentials generateOrcidCredentials(URI orcid) {
        return OrcidCredentials.builder()
                   .withOrcid(orcid)
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

    private void stubForPersonResponse() {
        var response = IoUtils.stringFromResources(Path.of("cristin_person_sample_response.json"));
        stubFor(WireMock.get(urlPathEqualTo("/cristin/person/123"))
                .willReturn(aResponse().withBody(response).withStatus(HttpURLConnection.HTTP_OK)));
    }

    private void stubPersonResponseWithouthOrcid(String userWithoutCristinId) {
        var response = IoUtils.stringFromResources(Path.of("cristin_person_no_orcid.json"));
        stubFor(WireMock.get(urlPathEqualTo("/cristin/person/" + userWithoutCristinId))
                .willReturn(aResponse().withBody(response).withStatus(HttpURLConnection.HTTP_OK)));
    }

    private void stubInternalServerResponse(String someuser) {
        stubFor(WireMock.get(urlPathEqualTo("/cristin/person/" + someuser))
                .willReturn(aResponse().withStatus(HttpURLConnection.HTTP_INTERNAL_ERROR)));
    }
}
