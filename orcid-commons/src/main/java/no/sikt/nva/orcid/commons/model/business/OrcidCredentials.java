package no.sikt.nva.orcid.commons.model.business;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import no.unit.nva.commons.json.JsonSerializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record OrcidCredentials(URI orcid, String accessToken, String tokenType, int expiresIn, String tokenVersion,
                               boolean persistent, String idToken, int tokenId, Instant modified, Instant created)
    implements JsonSerializable {

    public static Builder builder() {
        return new Builder();
    }

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

    public static final class Builder {

        private URI orcid;
        private String accessToken;
        private String tokenType;
        private int expiresIn;
        private String tokenVersion;
        private boolean persistent;
        private String idToken;
        private int tokenId;
        private Instant modified;
        private Instant created;

        private Builder() {
        }

        public Builder withOrcid(URI orcid) {
            this.orcid = orcid;
            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withTokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder withExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder withTokenVersion(String tokenVersion) {
            this.tokenVersion = tokenVersion;
            return this;
        }

        public Builder withPersistent(boolean persistent) {
            this.persistent = persistent;
            return this;
        }

        public Builder withIdToken(String idToken) {
            this.idToken = idToken;
            return this;
        }

        public Builder withTokenId(int tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        public Builder withModified(Instant modified) {
            this.modified = modified;
            return this;
        }

        public Builder withCreated(Instant created) {
            this.created = created;
            return this;
        }

        public OrcidCredentials build() {
            return new OrcidCredentials(orcid,
                                        accessToken,
                                        tokenType,
                                        expiresIn,
                                        tokenVersion,
                                        persistent,
                                        idToken,
                                        tokenId,
                                        modified,
                                        created);
        }
    }
}
