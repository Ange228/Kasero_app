package com.mercado.altiplano.model;

import java.math.BigDecimal;

/** Un producto con la cantidad que se quiere comprar. Una lista de estos forma una canasta. */
public record ItemCanasta(long productoId, BigDecimal cantidad) {
}
