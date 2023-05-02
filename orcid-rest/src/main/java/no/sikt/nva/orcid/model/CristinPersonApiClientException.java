package no.sikt.nva.orcid.model;

public class CristinPersonApiClientException extends RuntimeException {

    public CristinPersonApiClientException(Exception exception) {
        super(exception);
    }

    public CristinPersonApiClientException(String message) {
        super(message);
    }
}
