package no.sikt.nva.orcid.commons.model.business;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.Objects;

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

    @JsonProperty("orcid")
    private final URI orcid;
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("tokenVersion")
    private String tokenVersion;

    @JsonProperty("persistent")
    private boolean persistent;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("tokenId")
    private int tokenId;


    @JacocoGenerated
    public URI getOrcid() {
        return orcid;
    }

    @JacocoGenerated
    public String getAccessToken() {
        return accessToken;
    }

    @JacocoGenerated
    public String getTokenType() {
        return tokenType;
    }

    @JacocoGenerated
    public int getExpiresIn() {
        return expiresIn;
    }

    @JacocoGenerated
    public String getTokenVersion() {
        return tokenVersion;
    }

    @JacocoGenerated
    public boolean isPersistent() {
        return persistent;
    }

    @JacocoGenerated
    public String getIdToken() {
        return idToken;
    }

    @JacocoGenerated
    public int getTokenId() {
        return tokenId;
    }

    @JacocoGenerated
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrcidCredentials)) {
            return false;
        }
        OrcidCredentials that = (OrcidCredentials) o;
        return getExpiresIn() == that.getExpiresIn()
                && isPersistent() == that.isPersistent()
                && getTokenId() == that.getTokenId()
                && Objects.equals(getOrcid(), that.getOrcid())
                && Objects.equals(getAccessToken(), that.getAccessToken())
                && Objects.equals(getTokenType(), that.getTokenType())
                && Objects.equals(getTokenVersion(), that.getTokenVersion())
                && Objects.equals(getIdToken(), that.getIdToken());
    }

    @JacocoGenerated
    @Override
    public int hashCode() {
        return Objects.hash(
                getOrcid(),
                getAccessToken(),
                getTokenType(),
                getExpiresIn(),
                getTokenVersion(),
                isPersistent(),
                getIdToken(),
                getTokenId());
    }
}
