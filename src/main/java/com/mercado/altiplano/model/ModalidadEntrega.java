package com.mercado.altiplano.model;

/** Formas en que el cliente puede recibir su pedido. */
public enum ModalidadEntrega {

    ENVIO("Envío", "El pedido llega a tu dirección."),
    RECOJO_EN_PUESTO("Recojo en puesto", "Recoge tu pedido en el mercado."),
    TERCERO_AUTORIZADO("Tercero autorizado", "Alguien de tu confianza lo recoge.");

    private final String etiqueta;
    private final String descripcion;

    ModalidadEntrega(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
