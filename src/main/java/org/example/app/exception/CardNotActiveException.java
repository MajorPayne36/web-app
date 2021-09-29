package org.example.app.exception;

public class CardNotActiveException extends RuntimeException {
    public CardNotActiveException() {
    }

    public CardNotActiveException(String message) {
        super(message);
    }

    public CardNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardNotActiveException(Throwable cause) {
        super(cause);
    }

    public CardNotActiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
