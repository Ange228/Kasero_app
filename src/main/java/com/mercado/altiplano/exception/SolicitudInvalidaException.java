package com.mercado.altiplano.exception;

/** Se usa cuando los datos que envía el usuario no son válidos (error 400). */
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String message) {
        super(message);
    }
}
