package com.mercado.altiplano.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    // Pocas iteraciones para que la prueba sea rápida; en la app real se usan 600 000.
    private final PasswordHasher hasher = new PasswordHasher(1_000);

    @Test
    void cifrarNoGuardaLaClaveEnTextoPlano() {
        String hash = hasher.cifrar("miClave123");

        assertFalse(hash.contains("miClave123"));
        assertTrue(hash.startsWith("pbkdf2_sha256$"));
    }

    @Test
    void laClaveCorrectaCoincide() {
        String hash = hasher.cifrar("miClave123");

        assertTrue(hasher.coincide("miClave123", hash));
    }

    @Test
    void unaClaveIncorrectaNoCoincide() {
        String hash = hasher.cifrar("miClave123");

        assertFalse(hasher.coincide("otraClave", hash));
    }

    @Test
    void dosCifradosDeLaMismaClaveSonDistintosPorLaSal() {
        String primero = hasher.cifrar("miClave123");
        String segundo = hasher.cifrar("miClave123");

        assertNotEquals(primero, segundo);
        assertTrue(hasher.coincide("miClave123", primero));
        assertTrue(hasher.coincide("miClave123", segundo));
    }

    @Test
    void unFormatoRaroNoCoincideNiLanzaError() {
        String hash = hasher.cifrar("miClave123");

        assertFalse(hasher.coincide("x", "texto-cualquiera"));
        assertFalse(hasher.coincide("x", "pbkdf2_sha256$no-numero$xx$yy"));
        assertFalse(hasher.coincide(null, hash));
        assertFalse(hasher.coincide("x", null));
    }
}
