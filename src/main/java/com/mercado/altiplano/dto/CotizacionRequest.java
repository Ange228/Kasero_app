package com.mercado.altiplano.dto;

import java.util.List;

/**
 * Pedido de cotización a un comerciante.
 * "claveSolicitud" es opcional: el front genera una por cada cotización para que un reintento no la duplique.
 */
public record CotizacionRequest(long comercianteId, List<ItemRequest> items, String claveSolicitud) {
}
