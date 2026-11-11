package com.vantara.platform.reproductive.domain.exceptions;

/**
 * Thrown when the date of a reproductive event is in the future or breaks the chronological order of the record.
 */
public class InvalidReproductiveEventDateException extends RuntimeException {

    public InvalidReproductiveEventDateException(String message) {
        super(message);
    }
}
