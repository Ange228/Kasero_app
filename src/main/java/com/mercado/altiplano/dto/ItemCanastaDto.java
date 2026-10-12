package com.mercado.altiplano.dto;

import java.math.BigDecimal;

/** Producto de una canasta listo para mostrar. */
public record ItemCanastaDto(
        long productoId,
        String producto,
        String icono,
        BigDecimal cantidad,
        String unidad
) {
}
