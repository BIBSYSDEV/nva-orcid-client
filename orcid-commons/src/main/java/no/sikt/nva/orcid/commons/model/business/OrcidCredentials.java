package no.sikt.nva.orcid.commons.model.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nva.commons.core.JacocoGenerated;

@Data
@Builder(
    builderClassName = "OrcidCredentialsBuilder",
    toBuilder = true,
    builderMethodName = "builder",
    buildMethodName = "build",
    setterPrefix = "with"
)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class OrcidCredentials {

    private static final String ORCID = "orcid";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";
    private static final String EXPIRES_IN = "expires_in";
    private static final String TOKEN_VERSION = "tokenVersion";
    private static final String PERSISTENT = "persistent";
    private static final String ID_TOKEN = "id_token";
    private static final String TOKEN_ID = "tokenId";
    private static final String MODIFIED = "modified";
    private static final String CREATED = "created";

    @JsonProperty(ORCID)
    private URI orcid;

    @JsonProperty(ACCESS_TOKEN)
    private String accessToken;

    @JsonProperty(TOKEN_TYPE)
    private String tokenType;

    @JsonProperty(EXPIRES_IN)
    private int expiresIn;

    @JsonProperty(TOKEN_VERSION)
    private String tokenVersion;

    @JsonProperty(PERSISTENT)
    private boolean persistent;

    @JsonProperty(ID_TOKEN)
    private String idToken;

    @JsonProperty(TOKEN_ID)
    private int tokenId;

    @JsonProperty(MODIFIED)
    private Instant modified;

    @JsonProperty(CREATED)
    private Instant created;

    @JacocoGenerated
    public OrcidCredentials() {
    }

    public OrcidCredentials copy() {
        return builder()
                   .withOrcid(this.getOrcid())
                   .withAccessToken(this.getAccessToken())
                   .withTokenType(this.getTokenType())
                   .withExpiresIn(this.getExpiresIn())
                   .withTokenVersion(this.getTokenVersion())
                   .withPersistent(this.isPersistent())
                   .withIdToken(this.getIdToken())
                   .withTokenId(this.getTokenId())
                   .withModified(this.getModified())
                   .withCreated(this.getCreated())
                   .build();
    }
}
