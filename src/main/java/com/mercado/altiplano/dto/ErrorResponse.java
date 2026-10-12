package com.mercado.altiplano.dto;

/** Respuesta estándar cuando algo sale mal. El front solo necesita leer "mensaje". */
public record ErrorResponse(String mensaje) {
}
