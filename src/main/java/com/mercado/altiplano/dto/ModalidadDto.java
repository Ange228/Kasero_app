package com.mercado.altiplano.dto;

import com.mercado.altiplano.model.ModalidadEntrega;

/** Modalidad de entrega lista para mostrar: el código que usa la API y su texto. */
public record ModalidadDto(String codigo, String etiqueta, String descripcion) {

    public static ModalidadDto desde(ModalidadEntrega modalidad) {
        return new ModalidadDto(modalidad.name(), modalidad.getEtiqueta(), modalidad.getDescripcion());
    }
}
