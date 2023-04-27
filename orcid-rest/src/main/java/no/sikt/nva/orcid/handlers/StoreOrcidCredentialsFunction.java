package no.sikt.nva.orcid.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import java.net.HttpURLConnection;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreOrcidCredentialsFunction extends ApiGatewayHandler<String, String> {
    //

    private static final Logger logger = LoggerFactory.getLogger(StoreOrcidCredentialsFunction.class);

    public StoreOrcidCredentialsFunction() {
        super(String.class);
    }

    @Override
    protected String processInput(String input, RequestInfo requestInfo, Context context) throws ApiGatewayException {
        logger.info(input);
        return input;
    }

    @Override
    protected Integer getSuccessStatusCode(String input, String output) {
        return HttpURLConnection.HTTP_OK;
    }
}
