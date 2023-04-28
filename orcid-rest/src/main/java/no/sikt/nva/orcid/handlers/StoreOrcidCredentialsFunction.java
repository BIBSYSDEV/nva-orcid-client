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
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.attempt.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreOrcidCredentialsFunction extends ApiGatewayHandler<OrcidCredentials, Void> {

    private static final String COULD_NOT_STORE_CREDENTIALS = "Could not store credentials";
    private static final Logger logger = LoggerFactory.getLogger(StoreOrcidCredentialsFunction.class);
    private static final String ORCID_CREDENTIALS_ALREADY_EXISTS = "Orcid credentials already exists";
    private static final String TABLE_NAME = "TABLE_NAME";
    private final OrcidService orcidService;

    @JacocoGenerated
    public StoreOrcidCredentialsFunction() {
        this(new OrcidServiceImpl(
            new Environment().readEnv(TABLE_NAME),
            AmazonDynamoDBClientBuilder.defaultClient(),
            Clock.systemDefaultZone()));
    }

    public StoreOrcidCredentialsFunction(OrcidService orcidService) {
        super(OrcidCredentials.class);
        this.orcidService = orcidService;
    }

    @Override
    protected Void processInput(OrcidCredentials input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        logger.info(input.getOrcid().toString());
        attempt(() -> orcidService.createOrcidCredentials(input))
            .orElseThrow(this::convertToCorrectApiGatewayException);
        return null;
    }

    @Override
    protected Integer getSuccessStatusCode(OrcidCredentials input, Void output) {
        return HttpURLConnection.HTTP_CREATED;
    }

    private ApiGatewayException convertToCorrectApiGatewayException(Failure<OrcidCredentials> fail) {
        if (fail.getException() instanceof TransactionFailedException) {
            return new ConflictException(ORCID_CREDENTIALS_ALREADY_EXISTS);
        }
        return new BadGatewayException(COULD_NOT_STORE_CREDENTIALS);
    }
}
