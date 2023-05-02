package no.sikt.nva.orcid.commons.utils;

import static no.unit.nva.testutils.RandomDataGenerator.randomBoolean;
import static no.unit.nva.testutils.RandomDataGenerator.randomInteger;
import static no.unit.nva.testutils.RandomDataGenerator.randomString;
import static no.unit.nva.testutils.RandomDataGenerator.randomUri;
import no.sikt.nva.orcid.commons.model.business.OrcidCredentials;

public final class RandomOrcidCredentialsGenerator {

    public static OrcidCredentials randomOrcidCredentials() {
        return OrcidCredentials
                   .builder()
                   .withOrcid(randomUri())
                   .withAccessToken(randomString())
                   .withPersistent(randomBoolean())
                   .withExpiresIn(randomInteger())
                   .withIdToken(randomString())
                   .withTokenId(randomInteger())
                   .withTokenType(randomString())
                   .withTokenVersion(randomString())
                   .build();
    }
}
