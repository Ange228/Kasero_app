package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ComercianteResumenDto;
import com.mercado.altiplano.dto.CotizacionRequest;
import com.mercado.altiplano.dto.ItemRequest;
import com.mercado.altiplano.exception.RecursoNoEncontradoException;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.Cotizacion;
import com.mercado.altiplano.model.EstadoCotizacion;
import com.mercado.altiplano.repository.CatalogoRepository;
import com.mercado.altiplano.repository.CotizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CotizacionServiceTest {

    private static final long CASERA_ANITA = 1;
    private static final long DON_JOSE = 2;
    private static final long PAPA = 1;
    private static final long CEBOLLA = 2;
    private static final long TOMATE = 3;
    private static final long ZANAHORIA = 4;
    private static final long FRESA = 6;

    private CatalogoService catalogo;
    private CotizacionService servicio;

    @BeforeEach
    void preparar() {
        CatalogoRepository repositorioCatalogo = new CatalogoRepository();
        CotizacionRepository repositorioCotizaciones = new CotizacionRepository();
        servicio = new CotizacionService(repositorioCotizaciones, repositorioCatalogo);
        catalogo = new CatalogoService(
                repositorioCatalogo,
                new CalculadoraCanasta(repositorioCatalogo),
                repositorioCotizaciones);
    }

    private static ItemRequest item(long productoId, String cantidad) {
        return new ItemRequest(productoId, new BigDecimal(cantidad));
    }

    private static CotizacionRequest canastaDeReferencia(long comercianteId, String clave) {
        return new CotizacionRequest(comercianteId, List.of(
                item(PAPA, "2"), item(CEBOLLA, "1"), item(TOMATE, "1"), item(ZANAHORIA, "1")), clave);
    }

    // ---------- Crear cotización (RF-09, RF-10) ----------

    @Test
    void calculaElSubtotalDeCadaProductoYElTotal() {
        Cotizacion cotizacion = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));

        assertEquals(4, cotizacion.items().size());
        assertEquals(new BigDecimal("6.00"), cotizacion.items().get(0).subtotal());
        assertEquals(new BigDecimal("2.50"), cotizacion.items().get(1).subtotal());
        assertEquals(new BigDecimal("4.00"), cotizacion.items().get(2).subtotal());
        assertEquals(new BigDecimal("2.00"), cotizacion.items().get(3).subtotal());
        assertEquals(new BigDecimal("14.50"), cotizacion.total());
    }

    @Test
    void laCotizacionNaceConNumeroFechaYEstadoPendiente() {
        Cotizacion cotizacion = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));

        assertEquals(1L, cotizacion.id());
        assertEquals(10L, cotizacion.usuarioId());
        assertEquals("Casera Anita", cotizacion.comercianteNombre());
        assertEquals(EstadoCotizacion.PENDIENTE, cotizacion.estado());
        assertNotNull(cotizacion.fecha());
    }

    @Test
    void guardaElPrecioUnitarioDelMomentoDeCotizar() {
        Cotizacion cotizacion = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));

        assertEquals(new BigDecimal("3.00"), cotizacion.items().get(0).precioUnitario());
        assertEquals("Papa", cotizacion.items().get(0).producto());
        assertEquals("kg", cotizacion.items().get(0).unidad());
    }

    @Test
    void aceptaCantidadesConDecimales() {
        Cotizacion cotizacion = servicio.crear(10,
                new CotizacionRequest(CASERA_ANITA, List.of(item(PAPA, "0.5")), null));

        assertEquals(new BigDecimal("1.50"), cotizacion.total());
    }

    // ---------- Validaciones ----------

    @Test
    void noSePuedeCotizarUnProductoQueElComercianteNoOfrece() {
        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class, () ->
                servicio.crear(10, new CotizacionRequest(DON_JOSE, List.of(item(FRESA, "1")), null)));

        assertEquals("Don José no ofrece Fresa", error.getMessage());
        assertTrue(servicio.listar(10).isEmpty(), "No debe quedar nada guardado si hubo error");
    }

    @Test
    void unComercianteQueNoExisteDaError() {
        assertThrows(RecursoNoEncontradoException.class, () ->
                servicio.crear(10, canastaDeReferencia(99, null)));
    }

    @Test
    void sinProductosSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.crear(10, new CotizacionRequest(CASERA_ANITA, List.of(), null)));
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.crear(10, new CotizacionRequest(CASERA_ANITA, null, null)));
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.crear(10, null));
    }

    @Test
    void unaCantidadCeroSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.crear(10, new CotizacionRequest(CASERA_ANITA, List.of(item(PAPA, "0")), null)));
    }

    // ---------- Sin duplicados (RNF-08) ----------

    @Test
    void repetirLaMismaSolicitudNoDuplicaLaCotizacion() {
        Cotizacion primera = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, "clave-1"));
        Cotizacion repetida = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, "clave-1"));

        assertEquals(primera.id(), repetida.id());
        assertEquals(1, servicio.listar(10).size());
    }

    @Test
    void clavesDistintasCreanCotizacionesDistintas() {
        Cotizacion primera = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, "clave-1"));
        Cotizacion segunda = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, "clave-2"));

        assertNotEquals(primera.id(), segunda.id());
        assertEquals(2, servicio.listar(10).size());
    }

    @Test
    void laMismaClaveEnOtroUsuarioNoSeConfunde() {
        Cotizacion deUno = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, "clave-1"));
        Cotizacion deOtro = servicio.crear(20, canastaDeReferencia(CASERA_ANITA, "clave-1"));

        assertNotEquals(deUno.id(), deOtro.id());
    }

    // ---------- Historial (RF-12, RF-13) ----------

    @Test
    void cadaUsuarioVeSoloSusCotizacionesYLaMasNuevaPrimero() {
        Cotizacion primera = servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));
        Cotizacion segunda = servicio.crear(10, canastaDeReferencia(DON_JOSE, null));
        servicio.crear(20, canastaDeReferencia(CASERA_ANITA, null));

        List<Cotizacion> historial = servicio.listar(10);

        assertEquals(2, historial.size());
        assertEquals(segunda.id(), historial.get(0).id());
        assertEquals(primera.id(), historial.get(1).id());
        assertEquals(1, servicio.listar(20).size());
        assertTrue(servicio.listar(30).isEmpty());
    }

    @Test
    void elHistorialConservaElTotalAunqueSeConsulteDespues() {
        servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));

        assertEquals(new BigDecimal("14.50"), servicio.listar(10).get(0).total());
    }

    // ---------- Más cotizados (RF-06) ----------

    @Test
    void cadaCotizacionHaceSubirElContadorDelComerciante() {
        int antes = cotizacionesDe("Casera Anita");

        servicio.crear(10, canastaDeReferencia(CASERA_ANITA, null));
        servicio.crear(20, canastaDeReferencia(CASERA_ANITA, null));

        assertEquals(antes + 2, cotizacionesDe("Casera Anita"));
    }

    private int cotizacionesDe(String nombre) {
        return catalogo.buscar(null, null, "cotizaciones").stream()
                .filter(c -> c.nombre().equals(nombre))
                .map(ComercianteResumenDto::numCotizaciones)
                .findFirst()
                .orElseThrow();
    }
}
