package com.mercado.altiplano.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Reloj controlable para probar cosas que dependen del tiempo, como la expiración de sesiones. */
class RelojFalso extends Clock {

    private Instant ahora;

    RelojFalso(Instant inicio) {
        this.ahora = inicio;
    }

    void avanzar(Duration duracion) {
        ahora = ahora.plus(duracion);
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zona) {
        return this;
    }

    @Override
    public Instant instant() {
        return ahora;
    }
}
