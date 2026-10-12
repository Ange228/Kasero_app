package com.mercado.altiplano.dto;

import java.math.BigDecimal;

/** Un producto con su cantidad, tal como lo envía el cliente (para comparar o cotizar). */
public record ItemRequest(long productoId, BigDecimal cantidad) {
}
