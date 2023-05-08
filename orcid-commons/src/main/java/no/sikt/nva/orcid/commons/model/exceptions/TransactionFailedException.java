package no.sikt.nva.orcid.commons.model.exceptions;

public class TransactionFailedException extends RuntimeException {

    public static final String ERROR_MESSAGE = "Conflict: This error is thrown when the transaction could not be "
                                               + "completed. In most cases this is because uniqueness conditions did "
                                               + "not hold (Typically a duplicate OrcidCredentials)";

    public TransactionFailedException(Exception exception) {
        super(ERROR_MESSAGE, exception);
    }

    public TransactionFailedException(Throwable throwable, String message) {
        super(message, throwable);
    }
}
