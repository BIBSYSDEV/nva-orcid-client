package no.sikt.nva.orcid.commons.model.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private final String accessToken;

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonProperty("expires_in")
    private final int expiresIn;

    @JsonProperty("tokenVersion")
    private final String tokenVersion;

    @JsonProperty("persistent")
    private final boolean persistent;

    @JsonProperty("id_token")
    private final String idToken;

    @JsonProperty("tokenId")
    private final int tokenId;
}
