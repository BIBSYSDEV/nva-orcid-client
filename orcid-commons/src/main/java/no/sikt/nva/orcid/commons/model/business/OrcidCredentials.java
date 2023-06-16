package no.sikt.nva.orcid.commons.model.business;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import no.unit.nva.commons.json.JsonSerializable;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;


@Builder(
        builderClassName = "OrcidCredentialsBuilder",
        toBuilder = true,
        builderMethodName = "builder",
        buildMethodName = "build",
        setterPrefix = "with"
)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record OrcidCredentials(URI orcid,
                               String accessToken,
                               String tokenType,
                               int expiresIn,
                               String tokenVersion,
                               boolean persistent,
                               String idToken,
                               int tokenId,
                               Instant modified,
                               Instant created) implements JsonSerializable {

    public boolean hasSameCredentials(OrcidCredentials credentials) {
        return expiresIn == credentials.expiresIn
                && persistent == credentials.persistent
                && tokenId == credentials.tokenId
                && Objects.equals(orcid, credentials.orcid)
                && Objects.equals(accessToken, credentials.accessToken)
                && Objects.equals(tokenType, credentials.tokenType)
                && Objects.equals(tokenVersion, credentials.tokenVersion)
                && Objects.equals(idToken, credentials.idToken);
    }
}
