package com.mercado.altiplano.model;

import java.util.List;

/**
 * Comerciante con puesto en el mercado.
 * "cotizacionesBase" son las cotizaciones que ya tenía antes de usar la plataforma (dato de ejemplo).
 * "activo" permite que el administrador lo oculte sin borrarlo.
 */
public record Comerciante(
        long id,
        String nombre,
        String descripcion,
        List<Long> sectorIds,
        int pasillo,
        int puesto,
        double calificacion,
        int numResenas,
        int cotizacionesBase,
        List<String> metodosPago,
        List<ModalidadEntrega> modalidades,
        boolean activo
) {
}
