package com.mercado.altiplano.dto;

import java.util.List;

/** Perfil completo del comerciante (RF-07 y RF-08). */
public record ComercianteDetalleDto(
        long id,
        String nombre,
        String descripcion,
        double calificacion,
        int numResenas,
        int pasillo,
        int puesto,
        List<String> sectores,
        List<String> metodosPago,
        List<ModalidadDto> modalidades,
        List<ProductoPrecioDto> productos,
        int numCotizaciones
) {
}
