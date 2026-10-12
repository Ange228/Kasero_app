package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ItemRequest;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.repository.CatalogoRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Revisa y ordena los productos que envía el cliente antes de comparar o cotizar:
 * que existan, que las cantidades sean válidas y que un producto repetido se sume.
 */
final class Canastas {

    private static final BigDecimal CANTIDAD_MAXIMA = new BigDecimal("1000");

    private Canastas() {
    }

    static List<ItemCanasta> normalizar(List<ItemRequest> items, CatalogoRepositoryPort catalogo) {
        if (items == null || items.isEmpty()) {
            throw new SolicitudInvalidaException("Agrega al menos un producto");
        }

        Map<Long, BigDecimal> cantidades = new LinkedHashMap<>();

        for (ItemRequest item : items) {
            if (item == null || item.cantidad() == null) {
                throw new SolicitudInvalidaException("Cada producto necesita una cantidad");
            }

            if (catalogo.buscarProducto(item.productoId()).isEmpty()) {
                throw new SolicitudInvalidaException("El producto " + item.productoId() + " no existe");
            }

            BigDecimal cantidad = item.cantidad().setScale(3, RoundingMode.HALF_UP);
            if (cantidad.signum() <= 0) {
                throw new SolicitudInvalidaException("La cantidad debe ser mayor que cero");
            }

            cantidades.merge(item.productoId(), cantidad, BigDecimal::add);
        }

        return cantidades.entrySet().stream()
                .map(entrada -> {
                    if (entrada.getValue().compareTo(CANTIDAD_MAXIMA) > 0) {
                        throw new SolicitudInvalidaException("La cantidad máxima por producto es 1000");
                    }
                    return new ItemCanasta(entrada.getKey(), limpiar(entrada.getValue()));
                })
                .toList();
    }

    /** Quita los ceros que sobran: 2.000 pasa a 2 y 0.500 pasa a 0.5. */
    private static BigDecimal limpiar(BigDecimal cantidad) {
        BigDecimal sinCeros = cantidad.stripTrailingZeros();
        return sinCeros.scale() < 0 ? sinCeros.setScale(0) : sinCeros;
    }
}
