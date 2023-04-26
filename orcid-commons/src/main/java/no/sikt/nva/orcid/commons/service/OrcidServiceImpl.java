package no.sikt.nva.orcid.commons.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;

import java.time.Clock;

public class OrcidServiceImpl implements OrcidService {

    private final String orcidTableName;
    private final AmazonDynamoDB client;
    private final Clock clockForTimestamps;
    

    public OrcidServiceImpl(String orcidTableName, AmazonDynamoDB client, Clock clock) {
        this.orcidTableName = orcidTableName;
        this.client = client;
        this.clockForTimestamps = clock;
    }

    @Override
    public OrcidCredentials insertOrcidCredentials(OrcidCredentials orcidCredentials) {
        return null;
    }
}
