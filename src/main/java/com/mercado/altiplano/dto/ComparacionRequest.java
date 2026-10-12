package com.mercado.altiplano.dto;

import java.util.List;

/**
 * Pedido de comparación. Si "items" viene vacío se usa la canasta de referencia.
 * "orden" puede ser "asc" (por defecto, del más barato al más caro) o "desc".
 */
public record ComparacionRequest(List<ItemRequest> items, String orden) {
}
