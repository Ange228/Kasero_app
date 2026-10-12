package com.mercado.altiplano.service;

import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.model.PrecioProducto;
import com.mercado.altiplano.repository.CatalogoRepositoryPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Calcula cuánto cuesta una canasta en el puesto de un comerciante.
 * Se usa para ordenar la búsqueda y para comparar comerciantes.
 */
@Component
public class CalculadoraCanasta {

    /** Total de la canasta y los productos que el comerciante no tiene registrados. */
    public record Resultado(BigDecimal total, List<String> faltantes) {

        /** Solo se puede comparar con otros si tiene todos los productos de la canasta. */
        public boolean completo() {
            return faltantes.isEmpty();
        }
    }

    private final CatalogoRepositoryPort catalogo;

    public CalculadoraCanasta(CatalogoRepositoryPort catalogo) {
        this.catalogo = catalogo;
    }

    public Resultado calcular(long comercianteId, List<ItemCanasta> canasta) {
        BigDecimal total = BigDecimal.ZERO;
        List<String> faltantes = new ArrayList<>();

        for (ItemCanasta item : canasta) {
            Optional<PrecioProducto> precio = catalogo.buscarPrecio(comercianteId, item.productoId());

            if (precio.isEmpty()) {
                faltantes.add(catalogo.buscarProducto(item.productoId())
                        .map(producto -> producto.nombre())
                        .orElse("Producto " + item.productoId()));
            } else {
                total = total.add(subtotal(precio.get().precio(), item.cantidad()));
            }
        }

        return new Resultado(total.setScale(2, RoundingMode.HALF_UP), List.copyOf(faltantes));
    }

    /** Precio por cantidad, redondeado a céntimos. */
    public static BigDecimal subtotal(BigDecimal precio, BigDecimal cantidad) {
        return precio.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);
    }
}
