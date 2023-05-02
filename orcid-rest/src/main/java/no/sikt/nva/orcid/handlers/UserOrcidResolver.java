package no.sikt.nva.orcid.handlers;

import static nva.commons.core.attempt.Try.attempt;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.Optional;
import no.sikt.nva.orcid.model.CristinPersonApiClientException;
import no.sikt.nva.orcid.model.CristinPersonResponse;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.attempt.Failure;
import org.apache.http.client.utils.URIBuilder;

public class UserOrcidResolver {

    public static final String CRISTIN_API_ERROR_MESSAGE = "Cristin api answered with status: ";
    public static final String USERNAME_DELIMITER = "@";
    public static final String HTTPS_SCHEME = "https";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final int MAX_TRIES = 3;

    private final HttpClient httpClient;
    private final String apiHost;

    public UserOrcidResolver(HttpClient client, String apiHost) {
        this.httpClient = client;
        this.apiHost = apiHost;
    }

    @JacocoGenerated
    public static UserOrcidResolver defaultUserOrcidResolver(String apiHost) {
        return new UserOrcidResolver(HttpClient.newBuilder().build(), apiHost);
    }

    public Optional<String> getOrcidForUser(String userName) {
        return attempt(() -> craftUserUri(userName))
                   .map(this::createRequest)
                   .map(this::getCristinPersonResponse)
                   .map(this::getOrcidFromResponse)
                   .orElseThrow(this::handleFailure);
    }

    private URI craftUserUri(String userName) throws URISyntaxException {
        var userId =
            Arrays.stream(userName.split(USERNAME_DELIMITER))
                .findFirst().get();
        return
            new URIBuilder().setHost(apiHost).setPath("/cristin/person/" + userId).setScheme(HTTPS_SCHEME).build();
    }

    private String getBodyFromResponse(HttpResponse<String> response) {
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new CristinPersonApiClientException(CRISTIN_API_ERROR_MESSAGE + response.statusCode());
        }
        return response.body();
    }

    private RuntimeException handleFailure(Failure<Optional<String>> fail) {
        return new CristinPersonApiClientException(fail.getException());
    }

    private Optional<String> getOrcidFromResponse(HttpResponse<String> response) throws JsonProcessingException {
        var responseBody = getBodyFromResponse(response);
        return getOrcidIdentifierFromResponsBody(responseBody);
    }

    private Optional<String> getOrcidIdentifierFromResponsBody(String responseBody) throws JsonProcessingException {
        var cristinPersonResponse = JsonUtils.dtoObjectMapper.readValue(responseBody, CristinPersonResponse.class);
        return cristinPersonResponse.getOrcid();
    }

    private HttpResponse<String> getCristinPersonResponse(HttpRequest httpRequest)
        throws IOException, InterruptedException {
        return getCristinResponseWithRetries(httpRequest);
    }

    private HttpResponse<String> getCristinResponseWithRetries(HttpRequest httpRequest)
        throws IOException, InterruptedException {
        var numberOfAttempts = 0;
        int statusCode;
        HttpResponse<String> response;
        do {
            if (numberOfAttempts != 0) {
                Thread.sleep(500);
            }
            response = httpClient.send(httpRequest, BodyHandlers.ofString());
            statusCode = response.statusCode();
            numberOfAttempts++;
        } while (numberOfAttempts < MAX_TRIES && statusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR);
        return response;
    }

    private HttpRequest createRequest(URI userUri) {
        return HttpRequest.newBuilder()
                   .uri(userUri)
                   .GET()
                   .header(ACCEPT, APPLICATION_JSON)
                   .build();
    }
}
