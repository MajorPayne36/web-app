package org.example.app.exception;

public class UnsupportedResetConfirmException extends RuntimeException {
    public UnsupportedResetConfirmException() {
    }

    public UnsupportedResetConfirmException(String message) {
        super(message);
    }

    public UnsupportedResetConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedResetConfirmException(Throwable cause) {
        super(cause);
    }

    public UnsupportedResetConfirmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
