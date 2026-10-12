package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.CotizacionRequest;
import com.mercado.altiplano.exception.RecursoNoEncontradoException;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.Comerciante;
import com.mercado.altiplano.model.Cotizacion;
import com.mercado.altiplano.model.EstadoCotizacion;
import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.model.ItemCotizacion;
import com.mercado.altiplano.model.PrecioProducto;
import com.mercado.altiplano.model.Producto;
import com.mercado.altiplano.repository.CatalogoRepositoryPort;
import com.mercado.altiplano.repository.CotizacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Cotizaciones de los clientes (RF-09, RF-10, RF-12 y RF-13).
 * El precio de cada producto queda guardado tal como estaba al cotizar.
 * El comerciante confirma después el precio final y la disponibilidad.
 */
@Service
public class CotizacionService {

    private final CotizacionRepositoryPort cotizaciones;
    private final CatalogoRepositoryPort catalogo;

    public CotizacionService(CotizacionRepositoryPort cotizaciones, CatalogoRepositoryPort catalogo) {
        this.cotizaciones = cotizaciones;
        this.catalogo = catalogo;
    }

    public Cotizacion crear(long usuarioId, CotizacionRequest request) {
        if (request == null) {
            throw new SolicitudInvalidaException("Faltan los datos de la cotización");
        }

        Comerciante comerciante = catalogo.buscarComerciante(request.comercianteId())
                .filter(Comerciante::activo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No encontramos a ese comerciante"));

        List<ItemCanasta> canasta = Canastas.normalizar(request.items(), catalogo);

        List<ItemCotizacion> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemCanasta item : canasta) {
            Producto producto = catalogo.buscarProducto(item.productoId())
                    .orElseThrow(() -> new SolicitudInvalidaException(
                            "El producto " + item.productoId() + " no existe"));

            PrecioProducto precio = catalogo.buscarPrecio(comerciante.id(), producto.id())
                    .orElseThrow(() -> new SolicitudInvalidaException(
                            comerciante.nombre() + " no ofrece " + producto.nombre()));

            BigDecimal subtotal = CalculadoraCanasta.subtotal(precio.precio(), item.cantidad());
            total = total.add(subtotal);

            items.add(new ItemCotizacion(
                    producto.id(),
                    producto.nombre(),
                    producto.icono(),
                    item.cantidad(),
                    producto.unidad(),
                    precio.precio(),
                    subtotal
            ));
        }

        Cotizacion nueva = new Cotizacion(
                0,
                usuarioId,
                comerciante.id(),
                comerciante.nombre(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                EstadoCotizacion.PENDIENTE,
                List.copyOf(items),
                total.setScale(2, RoundingMode.HALF_UP)
        );

        return cotizaciones.guardar(nueva, request.claveSolicitud());
    }

    /** Historial del usuario, de la más reciente a la más antigua. */
    public List<Cotizacion> listar(long usuarioId) {
        return cotizaciones.listarPorUsuario(usuarioId);
    }
}
