package com.mercado.altiplano.model;

import java.math.BigDecimal;

/**
 * Línea de una cotización. Guarda el precio que tenía el producto en ese momento,
 * así el historial no cambia aunque el comerciante actualice sus precios después (RNF-07).
 */
public record ItemCotizacion(
        long productoId,
        String producto,
        String icono,
        BigDecimal cantidad,
        String unidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
