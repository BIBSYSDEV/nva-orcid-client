package no.sikt.nva.orcid.commons.model.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import no.unit.nva.commons.json.JsonSerializable;
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
public class OrcidCredentials implements JsonSerializable {

    private static final String ORCID = "orcid";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String TOKEN_TYPE = "tokenType";
    private static final String EXPIRES_IN = "expiresIn";
    private static final String TOKEN_VERSION = "tokenVersion";
    private static final String PERSISTENT = "persistent";
    private static final String ID_TOKEN = "idToken";
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

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getOrcid(), getAccessToken(), getTokenType(), getExpiresIn(), getTokenVersion(),
                            isPersistent(),
                            getIdToken(), getTokenId(), getModified(), getCreated());
    }

    @Override
    @JacocoGenerated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrcidCredentials)) {
            return false;
        }
        OrcidCredentials that = (OrcidCredentials) o;
        return hasSameCredentials(that)
               && Objects.equals(getModified(), that.getModified())
               && Objects.equals(getCreated(), that.getCreated());
    }

    public boolean hasSameCredentials(OrcidCredentials credentials) {
        return getExpiresIn() == credentials.getExpiresIn()
               && isPersistent() == credentials.isPersistent()
               && getTokenId() == credentials.getTokenId()
               && Objects.equals(getOrcid(), credentials.getOrcid())
               && Objects.equals(getAccessToken(), credentials.getAccessToken())
               && Objects.equals(getTokenType(), credentials.getTokenType())
               && Objects.equals(getTokenVersion(), credentials.getTokenVersion())
               && Objects.equals(getIdToken(), credentials.getIdToken());
    }
}
