package com.mercado.altiplano.model;

/** Oferta de un comerciante sobre uno de sus productos (el descuento va de 1 a 99). */
public record Promocion(long id, long comercianteId, long productoId, String titulo, int descuento) {
}
