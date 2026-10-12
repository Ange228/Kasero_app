package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ComercianteDetalleDto;
import com.mercado.altiplano.dto.ComercianteResumenDto;
import com.mercado.altiplano.dto.ModalidadDto;
import com.mercado.altiplano.dto.ProductoPrecioDto;
import com.mercado.altiplano.dto.PromocionDto;
import com.mercado.altiplano.exception.RecursoNoEncontradoException;
import com.mercado.altiplano.model.Comerciante;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.model.Noticia;
import com.mercado.altiplano.model.PrecioProducto;
import com.mercado.altiplano.model.Producto;
import com.mercado.altiplano.model.Promocion;
import com.mercado.altiplano.model.Sector;
import com.mercado.altiplano.repository.CatalogoRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Consultas del catálogo: sectores, búsqueda con filtros, perfil del comerciante y promociones.
 * Cubre RF-03, RF-04, RF-05, RF-06, RF-07, RF-08 y RF-27.
 */
@Service
public class CatalogoService {

    private final CatalogoRepositoryPort catalogo;
    private final CalculadoraCanasta calculadora;

    public CatalogoService(CatalogoRepositoryPort catalogo, CalculadoraCanasta calculadora) {
        this.catalogo = catalogo;
        this.calculadora = calculadora;
    }

    public List<Sector> sectores() {
        return catalogo.listarSectores();
    }

    public List<Producto> productos(Long sectorId) {
        return catalogo.listarProductos().stream()
                .filter(producto -> sectorId == null || producto.sectorId() == sectorId)
                .toList();
    }

    public List<Noticia> noticias() {
        return catalogo.listarNoticias();
    }

    /**
     * Busca comerciantes por nombre, número de puesto, sector o producto que venden.
     * Si se escriben varias palabras, el comerciante debe coincidir con todas.
     */
    public List<ComercianteResumenDto> buscar(String texto, Long sectorId, String orden) {
        OrdenResultados criterio = OrdenResultados.desde(orden);
        List<String> palabras = palabras(texto);
        Map<Long, String> sectoresPorId = nombresDeSectores();
        Map<Long, String> productosPorId = nombresDeProductos();
        List<ItemCanasta> canasta = catalogo.canastaReferencia();

        List<Comerciante> candidatos = catalogo.listarComerciantes().stream()
                .filter(Comerciante::activo)
                .filter(c -> sectorId == null || c.sectorIds().contains(sectorId))
                .filter(c -> palabras.stream()
                        .allMatch(palabra -> coincide(c, palabra, sectoresPorId, productosPorId)))
                .toList();

        // Solo se puede comparar el precio de quienes tienen toda la canasta.
        Map<Long, BigDecimal> preciosCanasta = new HashMap<>();
        for (Comerciante c : candidatos) {
            CalculadoraCanasta.Resultado resultado = calculadora.calcular(c.id(), canasta);
            if (resultado.completo()) {
                preciosCanasta.put(c.id(), resultado.total());
            }
        }

        BigDecimal minimo = preciosCanasta.values().stream()
                .min(Comparator.naturalOrder())
                .orElse(null);
        boolean hayComparacion = preciosCanasta.size() >= 2;

        return candidatos.stream()
                .map(c -> {
                    BigDecimal precio = preciosCanasta.get(c.id());
                    boolean masEconomico = hayComparacion && precio != null && precio.compareTo(minimo) == 0;
                    return new ComercianteResumenDto(
                            c.id(),
                            c.nombre(),
                            c.calificacion(),
                            c.numResenas(),
                            c.pasillo(),
                            c.puesto(),
                            sectoresDe(c, sectoresPorId),
                            precio,
                            masEconomico,
                            numCotizaciones(c)
                    );
                })
                .sorted(comparador(criterio))
                .toList();
    }

    public ComercianteDetalleDto detalle(long id) {
        Comerciante c = catalogo.buscarComerciante(id)
                .filter(Comerciante::activo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No encontramos a ese comerciante"));

        List<ProductoPrecioDto> productos = catalogo.listarPrecios(c.id()).stream()
                .map(precio -> catalogo.buscarProducto(precio.productoId())
                        .map(producto -> new ProductoPrecioDto(
                                producto.id(),
                                producto.nombre(),
                                producto.icono(),
                                producto.unidad(),
                                precio.precio(),
                                precio.actualizado().toString())))
                .flatMap(Optional::stream)
                .toList();

        return new ComercianteDetalleDto(
                c.id(),
                c.nombre(),
                c.descripcion(),
                c.calificacion(),
                c.numResenas(),
                c.pasillo(),
                c.puesto(),
                sectoresDe(c, nombresDeSectores()),
                c.metodosPago(),
                c.modalidades().stream().map(ModalidadDto::desde).toList(),
                productos,
                numCotizaciones(c)
        );
    }

    public List<PromocionDto> promociones() {
        List<PromocionDto> lista = new ArrayList<>();

        for (Promocion promocion : catalogo.listarPromociones()) {
            Optional<Comerciante> comerciante = catalogo.buscarComerciante(promocion.comercianteId())
                    .filter(Comerciante::activo);
            Optional<Producto> producto = catalogo.buscarProducto(promocion.productoId());
            Optional<PrecioProducto> precio = catalogo.buscarPrecio(promocion.comercianteId(), promocion.productoId());

            if (comerciante.isEmpty() || producto.isEmpty() || precio.isEmpty()) {
                continue;
            }

            BigDecimal original = precio.get().precio();
            BigDecimal conDescuento = original
                    .multiply(BigDecimal.valueOf(100L - promocion.descuento()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            lista.add(new PromocionDto(
                    promocion.id(),
                    promocion.titulo(),
                    comerciante.get().id(),
                    comerciante.get().nombre(),
                    comerciante.get().pasillo(),
                    comerciante.get().puesto(),
                    producto.get().nombre(),
                    producto.get().icono(),
                    promocion.descuento(),
                    original,
                    conDescuento,
                    producto.get().unidad()
            ));
        }

        return lista;
    }

    // ---------- apoyo ----------

    private int numCotizaciones(Comerciante comerciante) {
        return comerciante.cotizacionesBase();
    }

    private Map<Long, String> nombresDeSectores() {
        return catalogo.listarSectores().stream()
                .collect(Collectors.toMap(Sector::id, Sector::nombre));
    }

    private Map<Long, String> nombresDeProductos() {
        return catalogo.listarProductos().stream()
                .collect(Collectors.toMap(Producto::id, Producto::nombre));
    }

    /** Nombres de los sectores en los que vende un comerciante. */
    private static List<String> sectoresDe(Comerciante comerciante, Map<Long, String> sectoresPorId) {
        return comerciante.sectorIds().stream()
                .map(sectoresPorId::get)
                .filter(nombre -> nombre != null)
                .toList();
    }

    private boolean coincide(
            Comerciante comerciante,
            String palabra,
            Map<Long, String> sectoresPorId,
            Map<Long, String> productosPorId
    ) {
        if (normalizar(comerciante.nombre()).contains(palabra)) {
            return true;
        }

        boolean esNumero = palabra.chars().allMatch(Character::isDigit);
        if (esNumero
                && (String.valueOf(comerciante.puesto()).contains(palabra)
                || String.valueOf(comerciante.pasillo()).equals(palabra))) {
            return true;
        }

        boolean coincideSector = comerciante.sectorIds().stream()
                .anyMatch(id -> normalizar(sectoresPorId.get(id)).contains(palabra));
        if (coincideSector) {
            return true;
        }

        return catalogo.listarPrecios(comerciante.id()).stream()
                .anyMatch(precio -> normalizar(productosPorId.get(precio.productoId())).contains(palabra));
    }

    private static Comparator<ComercianteResumenDto> comparador(OrdenResultados orden) {
        Comparator<ComercianteResumenDto> porNombre = Comparator.comparing(ComercianteResumenDto::nombre);

        return switch (orden) {
            case PRECIO_ASC -> Comparator
                    .comparing(ComercianteResumenDto::precioCanasta,
                            Comparator.nullsLast(Comparator.<BigDecimal>naturalOrder()))
                    .thenComparing(porNombre);
            case PRECIO_DESC -> Comparator
                    .comparing(ComercianteResumenDto::precioCanasta,
                            Comparator.nullsLast(Comparator.<BigDecimal>reverseOrder()))
                    .thenComparing(porNombre);
            case CALIFICACION -> Comparator
                    .comparingDouble(ComercianteResumenDto::calificacion).reversed()
                    .thenComparing(Comparator.comparingInt(ComercianteResumenDto::numResenas).reversed())
                    .thenComparing(porNombre);
            case COTIZACIONES -> Comparator
                    .comparingInt(ComercianteResumenDto::numCotizaciones).reversed()
                    .thenComparing(porNombre);
        };
    }

    private static List<String> palabras(String texto) {
        String limpio = normalizar(texto);
        if (limpio.isEmpty()) {
            return List.of();
        }
        return List.of(limpio.split("\\s+"));
    }

    /** Pasa a minúsculas y quita tildes para que "maria" encuentre a "María". */
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
