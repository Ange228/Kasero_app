package com.mercado.altiplano.service;

import com.mercado.altiplano.model.ItemCanasta;
import com.mercado.altiplano.repository.CatalogoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraCanastaTest {

    private CatalogoRepository repositorio;
    private CalculadoraCanasta calculadora;

    @BeforeEach
    void preparar() {
        repositorio = new CatalogoRepository();
        calculadora = new CalculadoraCanasta(repositorio);
    }

    @Test
    void calculaElTotalDeLaCanastaDeReferenciaPorComerciante() {
        List<ItemCanasta> canasta = repositorio.canastaReferencia();

        assertEquals(new BigDecimal("14.50"), calculadora.calcular(1, canasta).total());
        assertEquals(new BigDecimal("15.40"), calculadora.calcular(2, canasta).total());
        assertEquals(new BigDecimal("16.80"), calculadora.calcular(3, canasta).total());
    }

    @Test
    void unaCanastaCompletaNoTieneFaltantes() {
        CalculadoraCanasta.Resultado resultado = calculadora.calcular(1, repositorio.canastaReferencia());

        assertTrue(resultado.completo());
        assertTrue(resultado.faltantes().isEmpty());
    }

    @Test
    void informaQueProductosNoVendeElComerciante() {
        // Don Manuel solo vende frutas.
        CalculadoraCanasta.Resultado resultado = calculadora.calcular(4, repositorio.canastaReferencia());

        assertFalse(resultado.completo());
        assertEquals(List.of("Papa", "Cebolla", "Tomate", "Zanahoria"), resultado.faltantes());
        assertEquals(new BigDecimal("0.00"), resultado.total());
    }

    @Test
    void sumaLosProductosQueTieneYListaLosQueFaltan() {
        List<ItemCanasta> canasta = List.of(
                new ItemCanasta(1, new BigDecimal("1")),   // papa
                new ItemCanasta(12, new BigDecimal("1"))); // trucha

        CalculadoraCanasta.Resultado resultado = calculadora.calcular(1, canasta);

        assertEquals(new BigDecimal("3.00"), resultado.total());
        assertEquals(List.of("Trucha"), resultado.faltantes());
    }

    @Test
    void elSubtotalSeRedondeaACentimos() {
        assertEquals(new BigDecimal("0.92"),
                CalculadoraCanasta.subtotal(new BigDecimal("2.75"), new BigDecimal("0.333")));
        assertEquals(new BigDecimal("6.00"),
                CalculadoraCanasta.subtotal(new BigDecimal("3.00"), new BigDecimal("2")));
    }
}
