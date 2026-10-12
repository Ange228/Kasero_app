package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ComparacionRequest;
import com.mercado.altiplano.dto.ComparacionResponse;
import com.mercado.altiplano.dto.ItemCanastaDto;
import com.mercado.altiplano.dto.ResultadoComparacionDto;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.Comerciante;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.repository.CatalogoRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Compara el precio de una misma canasta entre comerciantes (RF-14 y RF-15).
 *
 * Solo se comparan los totales de quienes tienen TODOS los productos de la canasta,
 * porque comparar canastas distintas daría una diferencia que no significa nada (RNF-14).
 * Los que no tienen todo se muestran aparte indicando qué les falta.
 */
@Service
public class ComparadorService {

    private final CatalogoRepositoryPort catalogo;
    private final CalculadoraCanasta calculadora;

    public ComparadorService(CatalogoRepositoryPort catalogo, CalculadoraCanasta calculadora) {
        this.catalogo = catalogo;
        this.calculadora = calculadora;
    }

    public List<ItemCanastaDto> canastaReferencia() {
        return aDtos(catalogo.canastaReferencia());
    }

    public ComparacionResponse comparar(ComparacionRequest request) {
        boolean descendente = esDescendente(request == null ? null : request.orden());

        boolean sinProductos = request == null || request.items() == null || request.items().isEmpty();
        List<ItemCanasta> canasta = sinProductos
                ? catalogo.canastaReferencia()
                : Canastas.normalizar(request.items(), catalogo);

        List<Fila> filas = catalogo.listarComerciantes().stream()
                .filter(Comerciante::activo)
                .map(c -> new Fila(c, calculadora.calcular(c.id(), canasta)))
                // Si no vende ninguno de los productos no aporta nada a la comparación.
                .filter(fila -> fila.resultado().faltantes().size() < canasta.size())
                .toList();

        Comparator<Fila> porNombre = Comparator.comparing((Fila fila) -> fila.comerciante().nombre());
        Comparator<Fila> porTotal = Comparator.comparing((Fila fila) -> fila.resultado().total());

        List<Fila> completas = filas.stream()
                .filter(fila -> fila.resultado().completo())
                .sorted(descendente ? porTotal.reversed().thenComparing(porNombre) : porTotal.thenComparing(porNombre))
                .toList();

        List<Fila> incompletas = filas.stream()
                .filter(fila -> !fila.resultado().completo())
                .sorted(porNombre)
                .toList();

        BigDecimal minimo = completas.stream()
                .map(fila -> fila.resultado().total())
                .min(Comparator.naturalOrder())
                .orElse(null);
        boolean hayComparacion = completas.size() >= 2;

        List<ResultadoComparacionDto> resultados = new ArrayList<>();

        for (Fila fila : completas) {
            BigDecimal total = fila.resultado().total();
            resultados.add(crearResultado(
                    fila,
                    total,
                    total.subtract(minimo),
                    hayComparacion && total.compareTo(minimo) == 0,
                    true));
        }

        for (Fila fila : incompletas) {
            resultados.add(crearResultado(fila, null, null, false, false));
        }

        return new ComparacionResponse(aDtos(canasta), resultados);
    }

    // ---------- apoyo ----------

    private record Fila(Comerciante comerciante, CalculadoraCanasta.Resultado resultado) {
    }

    private static ResultadoComparacionDto crearResultado(
            Fila fila,
            BigDecimal total,
            BigDecimal diferencia,
            boolean masEconomico,
            boolean completo
    ) {
        Comerciante c = fila.comerciante();
        return new ResultadoComparacionDto(
                c.id(),
                c.nombre(),
                c.calificacion(),
                c.pasillo(),
                c.puesto(),
                total,
                diferencia,
                masEconomico,
                completo,
                fila.resultado().faltantes()
        );
    }

    private List<ItemCanastaDto> aDtos(List<ItemCanasta> canasta) {
        return canasta.stream()
                .map(item -> catalogo.buscarProducto(item.productoId())
                        .map(producto -> new ItemCanastaDto(
                                producto.id(),
                                producto.nombre(),
                                producto.icono(),
                                item.cantidad(),
                                producto.unidad()))
                        .orElseThrow(() -> new SolicitudInvalidaException(
                                "El producto " + item.productoId() + " no existe")))
                .toList();
    }

    private static boolean esDescendente(String orden) {
        if (orden == null || orden.isBlank()) {
            return false;
        }

        return switch (orden.trim().toLowerCase(Locale.ROOT)) {
            case "asc" -> false;
            case "desc" -> true;
            default -> throw new SolicitudInvalidaException("Orden no válido. Usa asc o desc");
        };
    }
}
