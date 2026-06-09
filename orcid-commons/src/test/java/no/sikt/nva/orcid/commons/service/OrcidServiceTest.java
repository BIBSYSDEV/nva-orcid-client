package no.sikt.nva.orcid.commons.service;

import static no.sikt.nva.orcid.commons.utils.RandomOrcidCredentialsGenerator.randomOrcidCredentials;
import static no.unit.nva.hamcrest.DoesNotHaveEmptyValues.doesNotHaveEmptyValues;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.time.Clock;
import java.util.Map;
import java.util.NoSuchElementException;

import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;
import no.sikt.nva.orcid.commons.model.exceptions.TransactionFailedException;
import no.sikt.nva.orcid.commons.model.storage.OrcidCredentialsDao;
import no.sikt.nva.orcid.commons.OrcidLocalTestDatabase;
import nva.commons.core.paths.UriWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;

public class OrcidServiceTest extends OrcidLocalTestDatabase {

    private static final String ORCID_TABLE_NAME = "someOrcidTableName";

    private OrcidServiceImpl orcidService;
    private Clock clock;

    @BeforeEach
    void initialize() {
        super.init(ORCID_TABLE_NAME);
        this.clock = Clock.systemDefaultZone();
        this.orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
    }

    @Test
    void shouldBePossibleToStoreOrcidCredentials() {
        var orcidCredentials = randomOrcidCredentials();
        var actual = orcidService.createOrcidCredentials(orcidCredentials);
        assertThat(actual.modified(), doesNotHaveEmptyValues());
        assertThat(actual.created(), is(equalTo(actual.modified())));
        assertThat(actual.hasSameCredentials(orcidCredentials), is(true));
    }

    @Test
    void shouldThrowExceptionIfCredentialsAlreadyExists() {
        var orcidCredentials = randomOrcidCredentials();
        orcidService.createOrcidCredentials(orcidCredentials);
        assertThrows(TransactionFailedException.class, () -> orcidService.createOrcidCredentials(orcidCredentials));
    }

    @Test
    void shouldThrowExceptionIfDynamoIsNotWorking() {
        client = mock(DynamoDbClient.class);
        doThrow(RuntimeException.class).when(client).transactWriteItems(any(TransactWriteItemsRequest.class));
        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
        var orcidCredentials = randomOrcidCredentials();
        assertThrows(TransactionFailedException.class, () -> orcidService.createOrcidCredentials(orcidCredentials));
    }

    @Test
    void shouldThrowNoSuchElementExceptionWhenTryingToFetchNonExistingOrcidCredentials() {
        var orcid = randomUri();
        assertThrows(NoSuchElementException.class, () -> orcidService.fetchOrcidCredentialsByOrcid(orcid));
    }

    @Test
    void shouldThrowExceptionWhenOrcidIsNull() {
        var orcid = UriWrapper.fromUri("").getUri();
        assertThrows(DynamoDbException.class, () -> orcidService.fetchOrcidCredentialsByOrcid(orcid));
    }

    @Test
    void shouldBePossibleToRetrieveOrcidCredentials() {
        var persistedCredentials = orcidService.createOrcidCredentials(randomOrcidCredentials());
        var retrievedCredentials = orcidService.fetchOrcidCredentialsByOrcid(persistedCredentials.orcid());
        assertThat(retrievedCredentials, is(equalTo(persistedCredentials)));
    }

    @Test
    void shouldNotGiveUpOnFirstTryToSaveCredentials() {
        var orcidCredentials = randomOrcidCredentials();
        var itemResult = generateItemResult(orcidCredentials);
        client = mock(DynamoDbClient.class);
        when(client.getItem(any(GetItemRequest.class)))
            .thenThrow(RuntimeException.class)
            .thenReturn(itemResult);
        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);

        var persistedCredentials = orcidService.createOrcidCredentials(orcidCredentials);
        assertThat(persistedCredentials, is(equalTo(orcidCredentials)));
    }

    @Test
    void shouldThrowExceptionIfOrcidCredentialsCannotBeFoundAfterSave() {
        client = mock(DynamoDbClient.class);
        when(client.getItem(any(GetItemRequest.class)))
            .thenThrow(RuntimeException.class);
        orcidService = new OrcidServiceImpl(ORCID_TABLE_NAME, client, clock);
        var orcidCredentials = randomOrcidCredentials();
        var exception = assertThrows(TransactionFailedException.class,
                                     () -> orcidService.createOrcidCredentials(orcidCredentials));
        assertThat(exception.getMessage(), containsString("Error reading result"));
    }

    private GetItemResponse generateItemResult(OrcidCredentials orcidCredentials) {
        return GetItemResponse.builder().item(generateAttributes(orcidCredentials)).build();
    }

    private Map<String, AttributeValue> generateAttributes(OrcidCredentials orcidCredentials) {
        var orcidCredentialsDao = new OrcidCredentialsDao(orcidCredentials);
        return orcidCredentialsDao.toDynamoFormat();
    }
}
