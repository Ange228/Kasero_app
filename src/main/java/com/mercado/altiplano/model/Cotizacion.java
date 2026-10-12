package com.mercado.altiplano.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Cotización que un cliente le pide a un comerciante. */
public record Cotizacion(
        long id,
        long usuarioId,
        long comercianteId,
        String comercianteNombre,
        LocalDateTime fecha,
        EstadoCotizacion estado,
        List<ItemCotizacion> items,
        BigDecimal total
) {
}
