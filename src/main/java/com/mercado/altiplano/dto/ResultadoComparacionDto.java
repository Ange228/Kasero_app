package com.mercado.altiplano.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado de un comerciante en la comparación.
 * Si no tiene todos los productos ("completo" = false), no tiene total ni diferencia
 * y "faltantes" dice cuáles no vende.
 */
public record ResultadoComparacionDto(
        long comercianteId,
        String nombre,
        double calificacion,
        int pasillo,
        int puesto,
        BigDecimal total,
        BigDecimal diferencia,
        boolean masEconomico,
        boolean completo,
        List<String> faltantes
) {
}
