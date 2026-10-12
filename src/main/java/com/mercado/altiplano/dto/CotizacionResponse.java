package com.mercado.altiplano.dto;

import com.mercado.altiplano.model.Cotizacion;
import com.mercado.altiplano.model.ItemCotizacion;

import java.math.BigDecimal;
import java.util.List;

/** Cotización lista para mostrar. La fecha va como texto ISO (AAAA-MM-DDThh:mm:ss). */
public record CotizacionResponse(
        long id,
        String fecha,
        String estado,
        String estadoEtiqueta,
        long comercianteId,
        String comerciante,
        List<ItemCotizacion> items,
        BigDecimal total
) {

    public static CotizacionResponse desde(Cotizacion cotizacion) {
        return new CotizacionResponse(
                cotizacion.id(),
                cotizacion.fecha().toString(),
                cotizacion.estado().name(),
                cotizacion.estado().getEtiqueta(),
                cotizacion.comercianteId(),
                cotizacion.comercianteNombre(),
                cotizacion.items(),
                cotizacion.total()
        );
    }
}
