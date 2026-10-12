package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ComercianteDetalleDto;
import com.mercado.altiplano.dto.ComercianteResumenDto;
import com.mercado.altiplano.dto.ProductoPrecioDto;
import com.mercado.altiplano.dto.PromocionDto;
import com.mercado.altiplano.exception.RecursoNoEncontradoException;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.repository.CatalogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoServiceTest {

    private static final long VERDURAS = 1;
    private static final long PESCADOS = 4;

    private CatalogoService servicio;

    @BeforeEach
    void preparar() {
        CatalogoRepository repositorio = new CatalogoRepository();
        servicio = new CatalogoService(repositorio, new CalculadoraCanasta(repositorio));
    }

    private static List<String> nombres(List<ComercianteResumenDto> resultados) {
        return resultados.stream().map(ComercianteResumenDto::nombre).toList();
    }

    // ---------- Sectores y productos (RF-03) ----------

    @Test
    void haySieteSectoresYElPrimeroEsVerduras() {
        assertEquals(7, servicio.sectores().size());
        assertEquals("Verduras", servicio.sectores().get(0).nombre());
    }

    @Test
    void losProductosSePuedenFiltrarPorSector() {
        assertEquals(17, servicio.productos(null).size());
        assertEquals(5, servicio.productos(VERDURAS).size());
    }

    // ---------- Búsqueda (RF-04) ----------

    @Test
    void buscarSinFiltrosDevuelveATodosLosComerciantes() {
        assertEquals(8, servicio.buscar(null, null, null).size());
    }

    @Test
    void buscarPorNombreNoDistingueMayusculas() {
        assertEquals(List.of("Casera Anita"), nombres(servicio.buscar("ANITA", null, null)));
    }

    @Test
    void buscarPorNombreIgnoraLasTildes() {
        assertEquals(List.of("María"), nombres(servicio.buscar("maria", null, null)));
    }

    @Test
    void buscarPorProductoEncuentraATodosLosQueLoVenden() {
        List<ComercianteResumenDto> resultados = servicio.buscar("tomate", null, null);

        assertEquals(List.of("Casera Anita", "Don José", "María"), nombres(resultados));
    }

    @Test
    void buscarPorNumeroDePuesto() {
        assertEquals(List.of("Casera Anita"), nombres(servicio.buscar("120", null, null)));
    }

    @Test
    void buscarConVariasPalabrasExigeCoincidirConTodas() {
        assertEquals(List.of("Casera Anita"), nombres(servicio.buscar("papa anita", null, null)));
        assertTrue(servicio.buscar("papa trucha", null, null).isEmpty());
    }

    @Test
    void buscarSinResultadosDevuelveListaVacia() {
        assertTrue(servicio.buscar("zzzz", null, null).isEmpty());
    }

    // ---------- Filtro por sector (RF-05) ----------

    @Test
    void filtrarPorSectorDevuelveSoloComerciantesDeEseSector() {
        assertEquals(3, servicio.buscar(null, VERDURAS, null).size());
        assertEquals(List.of("Pescadería Don Lucho"), nombres(servicio.buscar(null, PESCADOS, null)));
    }

    @Test
    void unSectorQueNoExisteNoDevuelveResultados() {
        assertTrue(servicio.buscar(null, 99L, null).isEmpty());
    }

    // ---------- Orden (RF-06) ----------

    @Test
    void ordenarPorMenorPrecioDeLaCanasta() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, VERDURAS, "precio_asc");

        assertEquals(List.of("Casera Anita", "Don José", "María"), nombres(resultados));
        assertEquals(new BigDecimal("14.50"), resultados.get(0).precioCanasta());
        assertEquals(new BigDecimal("15.40"), resultados.get(1).precioCanasta());
        assertEquals(new BigDecimal("16.80"), resultados.get(2).precioCanasta());
    }

    @Test
    void soloElMasBaratoSeMarcaComoMasEconomico() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, VERDURAS, "precio_asc");

        assertTrue(resultados.get(0).masEconomico());
        assertFalse(resultados.get(1).masEconomico());
        assertFalse(resultados.get(2).masEconomico());
    }

    @Test
    void ordenarPorMayorPrecioInvierteElOrden() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, VERDURAS, "precio_desc");

        assertEquals(List.of("María", "Don José", "Casera Anita"), nombres(resultados));
    }

    @Test
    void ordenPorPrecioPoneAlFinalALosQueNoTienenLaCanastaCompleta() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, null, "precio_asc");

        assertEquals(
                List.of("Casera Anita", "Don José", "María",
                        "Abarrotes Doña Rosa", "Carnes Don Pepe", "Don Manuel",
                        "Pescadería Don Lucho", "Puesto El Buen Sabor"),
                nombres(resultados));
        assertNull(resultados.get(3).precioCanasta());
        assertFalse(resultados.get(3).masEconomico());
    }

    @Test
    void sinComparacionPosibleNadieEsElMasEconomico() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, PESCADOS, null);

        assertEquals(1, resultados.size());
        assertFalse(resultados.get(0).masEconomico());
    }

    @Test
    void ordenarPorCalificacionDesempataPorCantidadDeResenas() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, null, "calificacion");

        assertEquals(
                List.of("Casera Anita", "Puesto El Buen Sabor", "María", "Don José",
                        "Abarrotes Doña Rosa", "Don Manuel", "Carnes Don Pepe", "Pescadería Don Lucho"),
                nombres(resultados));
    }

    @Test
    void ordenarPorMasCotizados() {
        List<ComercianteResumenDto> resultados = servicio.buscar(null, null, "cotizaciones");

        assertEquals(
                List.of("Puesto El Buen Sabor", "Casera Anita", "Don José", "María",
                        "Carnes Don Pepe", "Don Manuel", "Abarrotes Doña Rosa", "Pescadería Don Lucho"),
                nombres(resultados));
    }

    @Test
    void unOrdenQueNoExisteSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () -> servicio.buscar(null, null, "al-azar"));
    }

    // ---------- Perfil del comerciante (RF-07, RF-08) ----------

    @Test
    void elPerfilIncluyeUbicacionPagosEntregasYProductos() {
        ComercianteDetalleDto perfil = servicio.detalle(1);

        assertEquals("Casera Anita", perfil.nombre());
        assertEquals(4, perfil.pasillo());
        assertEquals(120, perfil.puesto());
        assertEquals(List.of("Verduras", "Frutas"), perfil.sectores());
        assertTrue(perfil.metodosPago().contains("Yape"));
        assertEquals(3, perfil.modalidades().size());
        assertEquals("ENVIO", perfil.modalidades().get(0).codigo());
        assertEquals(8, perfil.productos().size());
    }

    @Test
    void cadaProductoDelPerfilTienePrecioUnidadYFecha() {
        ProductoPrecioDto papa = servicio.detalle(1).productos().stream()
                .filter(p -> p.nombre().equals("Papa"))
                .findFirst()
                .orElseThrow();

        assertEquals(new BigDecimal("3.00"), papa.precio());
        assertEquals("kg", papa.unidad());
        assertEquals("2026-10-12", papa.actualizado());
    }

    @Test
    void pedirUnComercianteQueNoExisteDaError() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.detalle(99));
    }

    // ---------- Promociones y noticias (RF-27, RF-28) ----------

    @Test
    void laPromocionCalculaElPrecioConDescuento() {
        List<PromocionDto> promociones = servicio.promociones();

        assertEquals(3, promociones.size());

        PromocionDto fresas = promociones.get(0);
        assertEquals("Casera Anita", fresas.comerciante());
        assertEquals(30, fresas.descuento());
        assertEquals(new BigDecimal("6.00"), fresas.precioOriginal());
        assertEquals(new BigDecimal("4.20"), fresas.precioPromocional());

        assertEquals(new BigDecimal("18.00"), promociones.get(1).precioPromocional());
        assertEquals(new BigDecimal("2.52"), promociones.get(2).precioPromocional());
    }

    @Test
    void hayNoticiasDelMercado() {
        assertEquals(2, servicio.noticias().size());
    }
}
