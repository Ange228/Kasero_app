package com.mercado.altiplano.dto;

import java.math.BigDecimal;

/** Producto de un comerciante con su precio y la fecha de actualización (formato AAAA-MM-DD). */
public record ProductoPrecioDto(
        long productoId,
        String nombre,
        String icono,
        String unidad,
        BigDecimal precio,
        String actualizado
) {
}
