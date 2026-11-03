package org.romaric.accountservice.exceptions;

public class BalanceNotsifficientException extends RuntimeException {
    public BalanceNotsifficientException(String message) {
        super(message);
    }
}
