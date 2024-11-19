package com.ciliosencantados.exception;

public class SaveEventException extends RuntimeException {
    public SaveEventException(String message) {
        super(message);
    }

    public SaveEventException(Throwable cause) {
        super(cause);
    }

    public SaveEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
