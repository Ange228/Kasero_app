package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.ComparacionRequest;
import com.mercado.altiplano.dto.ComparacionResponse;
import com.mercado.altiplano.dto.ItemRequest;
import com.mercado.altiplano.dto.ResultadoComparacionDto;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.repository.CatalogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparadorServiceTest {

    private static final long PAPA = 1;
    private static final long TOMATE = 3;
    private static final long FRESA = 6;
    private static final long TRUCHA = 12;

    private ComparadorService servicio;

    @BeforeEach
    void preparar() {
        CatalogoRepository repositorio = new CatalogoRepository();
        servicio = new ComparadorService(repositorio, new CalculadoraCanasta(repositorio));
    }

    private static ItemRequest item(long productoId, String cantidad) {
        return new ItemRequest(productoId, new BigDecimal(cantidad));
    }

    private static List<String> nombres(ComparacionResponse respuesta) {
        return respuesta.resultados().stream().map(ResultadoComparacionDto::nombre).toList();
    }

    // ---------- Canasta de referencia ----------

    @Test
    void laCanastaDeReferenciaTieneCuatroProductos() {
        var canasta = servicio.canastaReferencia();

        assertEquals(4, canasta.size());
        assertEquals("Papa", canasta.get(0).producto());
        assertEquals(new BigDecimal("2"), canasta.get(0).cantidad());
        assertEquals("kg", canasta.get(0).unidad());
    }

    // ---------- Comparación (RF-14) ----------

    @Test
    void sinProductosSeComparaLaCanastaDeReferencia() {
        ComparacionResponse respuesta = servicio.comparar(null);

        assertEquals(List.of("Casera Anita", "Don José", "María"), nombres(respuesta));
        assertEquals(4, respuesta.canasta().size());
    }

    @Test
    void cadaResultadoTieneTotalYDiferenciaConElMasBarato() {
        List<ResultadoComparacionDto> resultados = servicio.comparar(null).resultados();

        assertEquals(new BigDecimal("14.50"), resultados.get(0).total());
        assertEquals(new BigDecimal("0.00"), resultados.get(0).diferencia());
        assertEquals(new BigDecimal("15.40"), resultados.get(1).total());
        assertEquals(new BigDecimal("0.90"), resultados.get(1).diferencia());
        assertEquals(new BigDecimal("16.80"), resultados.get(2).total());
        assertEquals(new BigDecimal("2.30"), resultados.get(2).diferencia());
    }

    @Test
    void soloElMasBaratoQuedaMarcadoComoMasEconomico() {
        List<ResultadoComparacionDto> resultados = servicio.comparar(null).resultados();

        assertTrue(resultados.get(0).masEconomico());
        assertFalse(resultados.get(1).masEconomico());
        assertFalse(resultados.get(2).masEconomico());
    }

    @Test
    void unaCanastaPropiaSeCalculaConSusCantidades() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(
                List.of(item(PAPA, "3"), item(TOMATE, "2")), null));

        // Anita: 3 x 3.00 + 2 x 4.00 | José: 3 x 3.20 + 2 x 4.20 | María: 3 x 3.50 + 2 x 4.50
        assertEquals(new BigDecimal("17.00"), respuesta.resultados().get(0).total());
        assertEquals(new BigDecimal("18.00"), respuesta.resultados().get(1).total());
        assertEquals(new BigDecimal("19.50"), respuesta.resultados().get(2).total());
    }

    @Test
    void unProductoRepetidoSeSumaEnLugarDeDuplicarse() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(
                List.of(item(PAPA, "1"), item(PAPA, "1")), null));

        assertEquals(1, respuesta.canasta().size());
        assertEquals(new BigDecimal("2"), respuesta.canasta().get(0).cantidad());
        assertEquals(new BigDecimal("6.00"), respuesta.resultados().get(0).total());
    }

    @Test
    void lasCantidadesConDecimalesSeRespetan() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(
                List.of(item(PAPA, "0.5")), null));

        assertEquals(new BigDecimal("0.5"), respuesta.canasta().get(0).cantidad());
        assertEquals(new BigDecimal("1.50"), respuesta.resultados().get(0).total());
    }

    // ---------- Orden (RF-15) ----------

    @Test
    void sePuedeOrdenarDelMasCaroAlMasBarato() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(null, "desc"));

        assertEquals(List.of("María", "Don José", "Casera Anita"), nombres(respuesta));
        // El más económico sigue siendo Anita, aunque quede al final.
        assertTrue(respuesta.resultados().get(2).masEconomico());
    }

    @Test
    void unOrdenInvalidoSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(null, "al-azar")));
    }

    // ---------- Comerciantes sin todos los productos (RNF-14) ----------

    @Test
    void quienNoTieneTodosLosProductosVaAlFinalSinTotalYConSusFaltantes() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(
                List.of(item(PAPA, "1"), item(FRESA, "1")), null));

        // Anita (3.00 + 6.00) y María (3.50 + 6.50) tienen ambos; Don José no tiene fresa y Don Manuel no tiene papa.
        assertEquals(List.of("Casera Anita", "María", "Don José", "Don Manuel"), nombres(respuesta));

        ResultadoComparacionDto donJose = respuesta.resultados().get(2);
        assertFalse(donJose.completo());
        assertNull(donJose.total());
        assertNull(donJose.diferencia());
        assertFalse(donJose.masEconomico());
        assertEquals(List.of("Fresa"), donJose.faltantes());
        assertEquals(List.of("Papa"), respuesta.resultados().get(3).faltantes());
    }

    @Test
    void conUnSoloComparableNadieEsElMasEconomico() {
        ComparacionResponse respuesta = servicio.comparar(new ComparacionRequest(
                List.of(item(TRUCHA, "1")), null));

        assertEquals(List.of("Pescadería Don Lucho"), nombres(respuesta));
        assertTrue(respuesta.resultados().get(0).completo());
        assertFalse(respuesta.resultados().get(0).masEconomico());
    }

    // ---------- Validación de productos y cantidades ----------

    @Test
    void unaCantidadCeroONegativaSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(List.of(item(PAPA, "0")), null)));
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(List.of(item(PAPA, "-2")), null)));
    }

    @Test
    void unaCantidadDemasiadoGrandeSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(List.of(item(PAPA, "1000.5")), null)));
    }

    @Test
    void unProductoQueNoExisteSeRechaza() {
        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(List.of(item(999, "1")), null)));

        assertEquals("El producto 999 no existe", error.getMessage());
    }

    @Test
    void unaCantidadFaltanteSeRechaza() {
        assertThrows(SolicitudInvalidaException.class, () ->
                servicio.comparar(new ComparacionRequest(List.of(new ItemRequest(PAPA, null)), null)));
    }
}
