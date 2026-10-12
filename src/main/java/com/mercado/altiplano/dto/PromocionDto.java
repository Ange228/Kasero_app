package com.mercado.altiplano.dto;

import java.math.BigDecimal;

/** Promoción lista para mostrar en el inicio, con el precio original y el precio con descuento. */
public record PromocionDto(
        long id,
        String titulo,
        long comercianteId,
        String comerciante,
        int pasillo,
        int puesto,
        String producto,
        String icono,
        int descuento,
        BigDecimal precioOriginal,
        BigDecimal precioPromocional,
        String unidad
) {
}
