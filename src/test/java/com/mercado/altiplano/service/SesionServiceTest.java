package com.mercado.altiplano.service;

import com.mercado.altiplano.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SesionServiceTest {

    private RelojFalso reloj;
    private SesionService sesiones;

    @BeforeEach
    void preparar() {
        reloj = new RelojFalso(Instant.parse("2026-10-12T12:00:00Z"));
        sesiones = new SesionService(reloj);
    }

    @Test
    void elTokenAbiertoIdentificaAlUsuario() {
        String token = sesiones.abrir(7L);

        assertEquals(7L, sesiones.usuarioDe("Bearer " + token));
    }

    @Test
    void laPalabraBearerNoDistingueMayusculas() {
        String token = sesiones.abrir(7L);

        assertEquals(7L, sesiones.usuarioDe("bearer " + token));
    }

    @Test
    void sinCabeceraNoHaySesion() {
        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe(null));
        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe("   "));
    }

    @Test
    void unTokenInventadoSeRechaza() {
        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe("Bearer inventado"));
    }

    @Test
    void unaCabeceraSinBearerSeRechaza() {
        String token = sesiones.abrir(7L);

        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe(token));
    }

    @Test
    void laSesionExpiraDespuesDeOchoHoras() {
        String token = sesiones.abrir(7L);

        reloj.avanzar(Duration.ofHours(7));
        assertEquals(7L, sesiones.usuarioDe("Bearer " + token));

        reloj.avanzar(Duration.ofHours(2));
        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe("Bearer " + token));
    }

    @Test
    void alCerrarSesionElTokenYaNoSirve() {
        String token = sesiones.abrir(7L);

        sesiones.cerrar("Bearer " + token);

        assertThrows(AuthenticationException.class, () -> sesiones.usuarioDe("Bearer " + token));
    }

    @Test
    void cadaSesionTieneUnTokenDistinto() {
        assertNotEquals(sesiones.abrir(1L), sesiones.abrir(1L));
    }
}
