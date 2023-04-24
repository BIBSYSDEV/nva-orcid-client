package no.sikt.nva.orcid.handlers;

import static org.mockito.Mockito.mock;
import com.amazonaws.services.lambda.runtime.Context;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StoreOrcidCredentialsFunctionTest {
    private static final Context CONTEXT = mock(Context.class);

    private StoreOrcidCredentialsFunction handler;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void init() {
        handler = new StoreOrcidCredentialsFunction();
        outputStream = new ByteArrayOutputStream();

    }

    @Test
    public void dummyTest() throws IOException {
        var inputStream = new ByteArrayInputStream("{\"message\": \"hello world\"}".getBytes());
        handler.handleRequest(inputStream, outputStream, CONTEXT);
        var something = "hehe";
    }

}
