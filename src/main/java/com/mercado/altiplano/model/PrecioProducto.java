package com.mercado.altiplano.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Precio que un comerciante tiene registrado para un producto, con la fecha en que se actualizó. */
public record PrecioProducto(long comercianteId, long productoId, BigDecimal precio, LocalDate actualizado) {
}
