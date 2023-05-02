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

    private static final String ORCID = "orcid";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";
    private static final String EXPIRES_IN = "expires_in";
    private static final String TOKEN_VERSION = "tokenVersion";
    private static final String PERSISTENT = "persistent";
    private static final String ID_TOKEN = "id_token";
    private static final String TOKEN_ID = "tokenId";

    @JsonProperty(ORCID)
    private final URI orcid;
    @JsonProperty(ACCESS_TOKEN)
    private final String accessToken;

    @JsonProperty(TOKEN_TYPE)
    private final String tokenType;

    @JsonProperty(EXPIRES_IN)
    private final int expiresIn;

    @JsonProperty(TOKEN_VERSION)
    private final String tokenVersion;

    @JsonProperty(PERSISTENT)
    private final boolean persistent;

    @JsonProperty(ID_TOKEN)
    private final String idToken;

    @JsonProperty(TOKEN_ID)
    private final int tokenId;
}
