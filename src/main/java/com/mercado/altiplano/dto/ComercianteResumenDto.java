package com.mercado.altiplano.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Datos de un comerciante en la lista de resultados.
 * "precioCanasta" es null si el comerciante no tiene todos los productos de la canasta de referencia.
 */
public record ComercianteResumenDto(
        long id,
        String nombre,
        double calificacion,
        int numResenas,
        int pasillo,
        int puesto,
        List<String> sectores,
        BigDecimal precioCanasta,
        boolean masEconomico,
        int numCotizaciones
) {
}
