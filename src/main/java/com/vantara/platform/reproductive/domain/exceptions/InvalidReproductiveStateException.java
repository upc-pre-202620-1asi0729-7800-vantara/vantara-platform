package com.vantara.platform.reproductive.domain.exceptions;

/**
 * Thrown when a reproductive event is registered on a record whose status does not allow it.
 */
public class InvalidReproductiveStateException extends RuntimeException {

    public InvalidReproductiveStateException(String message) {
        super(message);
    }
}
