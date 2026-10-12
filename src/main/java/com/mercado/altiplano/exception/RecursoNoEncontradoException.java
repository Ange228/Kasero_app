package com.mercado.altiplano.exception;

/** Se usa cuando se pide algo que no existe, por ejemplo un comerciante (error 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
