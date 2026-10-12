package com.mercado.altiplano.model;

/** Producto que se puede cotizar. Todos los precios del producto están dados por su "unidad" (kg, litro, unidad). */
public record Producto(long id, String nombre, long sectorId, String unidad, String icono) {
}
