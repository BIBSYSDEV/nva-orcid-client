package no.sikt.nva.orcid.handlers;

import static nva.commons.core.attempt.Try.attempt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import java.time.Clock;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.service.OrcidService;
import no.sikt.nva.orcid.commons.service.OrcidServiceImpl;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.BadGatewayException;
import nva.commons.apigateway.exceptions.ConflictException;
import nva.commons.apigateway.exceptions.ForbiddenException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.attempt.Failure;
import nva.commons.core.paths.UriWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreOrcidCredentialsFunction extends ApiGatewayHandler<OrcidCredentials, Void> {

    private static final String COULD_NOT_STORE_CREDENTIALS = "Could not store credentials";
    private static final Logger logger = LoggerFactory.getLogger(StoreOrcidCredentialsFunction.class);
    private static final String ORCID_CREDENTIALS_ALREADY_EXISTS = "Orcid credentials already exists";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String API_HOST = "API_HOST";
    private static final String EXTRACT_ORCID_BAD_GATEWAY = "Could not contact cristin API for %s. Exception: %s";
    private final OrcidService orcidService;
    private final UserOrcidResolver userOrcidResolver;

    @JacocoGenerated
    public StoreOrcidCredentialsFunction() {
        this(new OrcidServiceImpl(
                 new Environment().readEnv(TABLE_NAME),
                 AmazonDynamoDBClientBuilder.defaultClient(),
                 Clock.systemDefaultZone()),
             UserOrcidResolver.defaultUserOrcidResolver(
                 new Environment().readEnv(API_HOST)), new Environment());
    }

    public StoreOrcidCredentialsFunction(OrcidService orcidService, UserOrcidResolver userOrcidResolver,
                                         Environment environment) {
        super(OrcidCredentials.class, environment);
        this.orcidService = orcidService;
        this.userOrcidResolver = userOrcidResolver;
    }

    @Override
    protected void validateRequest(OrcidCredentials orcidCredentials, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        logger.info("Attempting to validate orcid: {} for {}", orcidCredentials.orcid(),
                    attempt(requestInfo::getPersonCristinId).orElse(e -> null));
        validateInput(orcidCredentials, requestInfo);
    }

    @Override
    protected Void processInput(OrcidCredentials input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {

        attempt(() -> orcidService.createOrcidCredentials(input))
            .orElseThrow(this::convertToCorrectApiGatewayException);
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(OrcidCredentials input, Void output) {
        return HttpURLConnection.HTTP_CREATED;
    }

    private void validateInput(OrcidCredentials input, RequestInfo requestInfo)
        throws ForbiddenException, BadGatewayException {
        var cristinId =
            attempt(requestInfo::getPersonCristinId)
                .map(UriWrapper::fromUri)
                .map(UriWrapper::getLastPathElement)
                .map(Integer::valueOf)
                .orElseThrow(e -> new ForbiddenException());

        logger.info("Attempting to store orcid: {} for {}", input.orcid(), cristinId);
        var orcidFromCristin =
            attempt(() -> userOrcidResolver.extractOrcidForUser(cristinId)).orElseThrow(
                fail -> new BadGatewayException(
                    String.format(EXTRACT_ORCID_BAD_GATEWAY, cristinId, fail.getException())));
        if (orcidFromCristin.isEmpty()) {
            throw new ForbiddenException();
        }
        if (!input.orcid().toString().contains(orcidFromCristin.get())) {
            throw new ForbiddenException();
        }
    }

    private ApiGatewayException convertToCorrectApiGatewayException(Failure<OrcidCredentials> fail) {
        if (fail.getException() instanceof TransactionFailedException) {
            return new ConflictException(ORCID_CREDENTIALS_ALREADY_EXISTS);
        }
        return new BadGatewayException(COULD_NOT_STORE_CREDENTIALS);
    }
}
