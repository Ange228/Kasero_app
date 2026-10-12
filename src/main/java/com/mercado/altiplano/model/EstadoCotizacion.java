package com.mercado.altiplano.model;

/** Estados de una cotización: nace pendiente hasta que el comerciante confirma precio y disponibilidad. */
public enum EstadoCotizacion {

    PENDIENTE("Pendiente de confirmación"),
    CONFIRMADA("Confirmada"),
    RECHAZADA("Rechazada");

    private final String etiqueta;

    EstadoCotizacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
