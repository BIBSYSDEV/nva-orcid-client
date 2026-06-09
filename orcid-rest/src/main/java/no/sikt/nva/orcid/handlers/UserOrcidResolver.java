package no.sikt.nva.orcid.handlers;

import static nva.commons.core.attempt.Try.attempt;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.function.Supplier;
import no.sikt.nva.orcid.model.CristinPersonApiClientException;
import no.sikt.nva.orcid.model.CristinPersonResponse;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.attempt.Failure;
import nva.commons.core.paths.UriWrapper;

public class UserOrcidResolver {

    public static final String CRISTIN_API_ERROR_MESSAGE = "Cristin api answered with status: ";
    private static final String ACCEPT = "Accept";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CRISTIN_PATH = "cristin";
    private static final String PERSON_PATH = "person";

    private final HttpClient httpClient;
    private final URI apiUri;

    public UserOrcidResolver(HttpClient client, URI apiUri) {
        this.httpClient = client;
        this.apiUri = apiUri;
    }

    @JacocoGenerated
    public static UserOrcidResolver defaultUserOrcidResolver(String apiHost) {
        return new UserOrcidResolver(HttpClient.newBuilder().build(), UriWrapper.fromHost(apiHost).getUri());
    }

    public Optional<String> extractOrcidForUser(Integer cristinId) {
        return attempt(() -> craftUserUri(cristinId))
                   .map(this::createRequest)
                   .map(this::extractCristinPersonResponse)
                   .map(this::extractOrcidFromResponse)
                   .orElseThrow(this::handleFailure);
    }

    private URI craftUserUri(Integer cristinId) {
        return UriWrapper.fromUri(apiUri)
                   .addChild(CRISTIN_PATH, PERSON_PATH, String.valueOf(cristinId))
                   .getUri();
    }

    private RuntimeException handleFailure(Failure<Optional<String>> fail) {
        return new CristinPersonApiClientException(fail.getException());
    }

    private Optional<String> extractOrcidFromResponse(String responseBody) throws JsonProcessingException {
        return extractOrcidIdentifierFromResponseBody(responseBody);
    }

    private Optional<String> extractOrcidIdentifierFromResponseBody(String responseBody)
        throws JsonProcessingException {
        var cristinPersonResponse = JsonUtils.dtoObjectMapper.readValue(responseBody, CristinPersonResponse.class);
        return cristinPersonResponse.getOrcid();
    }

    private String extractCristinPersonResponse(HttpRequest httpRequest) {
        return extractCristinResponseWithRetries(httpRequest);
    }

    private String extractCristinResponseWithRetries(HttpRequest httpRequest) {
        var retryRegistry = RetryRegistry.ofDefaults();
        var retry = retryRegistry.retry("get cristin person");
        Supplier<String> supplier = () -> sendGetCristinPersonRequest(httpRequest);
        return Try.ofSupplier(Retry.decorateSupplier(retry, supplier)).get();
    }

    private String sendGetCristinPersonRequest(HttpRequest httpRequest) {
        return attempt(() -> httpClient.send(httpRequest, BodyHandlers.ofString()))
                   .map(this::extractResponseBody)
                   .orElseThrow(
                       this::handleCristinApiFail);
    }

    private RuntimeException handleCristinApiFail(Failure<String> fail) {
        return new CristinPersonApiClientException(fail.getException());
    }

    private String extractResponseBody(HttpResponse<String> response) {
        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new CristinPersonApiClientException(CRISTIN_API_ERROR_MESSAGE + response.statusCode());
        }
        return response.body();
    }

    private HttpRequest createRequest(URI userUri) {
        return HttpRequest.newBuilder()
                   .uri(userUri)
                   .GET()
                   .header(ACCEPT, APPLICATION_JSON)
                   .build();
    }
}
