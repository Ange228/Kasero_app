package com.mercado.altiplano.service;

import com.mercado.altiplano.exception.AuthenticationException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maneja las sesiones de los usuarios con un token aleatorio.
 * El front lo manda en la cabecera "Authorization: Bearer ..." para usar las funciones protegidas.
 * Por ahora las sesiones viven en memoria; se pierden al reiniciar el servidor.
 */
@Service
public class SesionService {

    private static final Duration DURACION = Duration.ofHours(8);

    private record Sesion(Long usuarioId, Instant expira) {
    }

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();
    private final Clock reloj;

    public SesionService() {
        this(Clock.systemDefaultZone());
    }

    /** Se usa en las pruebas para controlar la hora. */
    public SesionService(Clock reloj) {
        this.reloj = reloj;
    }

    public String abrir(Long usuarioId) {
        byte[] bytes = new byte[32];
        azar.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        sesiones.put(token, new Sesion(usuarioId, reloj.instant().plus(DURACION)));
        return token;
    }

    /** Devuelve el id del usuario dueño del token o lanza error 401 si no es válido. */
    public Long usuarioDe(String cabeceraAutorizacion) {
        String token = extraerToken(cabeceraAutorizacion);
        Sesion sesion = token == null ? null : sesiones.get(token);

        if (sesion == null) {
            throw new AuthenticationException("Inicia sesión para continuar");
        }

        if (reloj.instant().isAfter(sesion.expira())) {
            sesiones.remove(token);
            throw new AuthenticationException("Tu sesión expiró, inicia sesión otra vez");
        }

        return sesion.usuarioId();
    }

    public void cerrar(String cabeceraAutorizacion) {
        String token = extraerToken(cabeceraAutorizacion);
        if (token != null) {
            sesiones.remove(token);
        }
    }

    private static String extraerToken(String cabecera) {
        if (cabecera == null) {
            return null;
        }

        String texto = cabecera.trim();
        if (texto.length() > 7 && texto.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return texto.substring(7).trim();
        }
        return null;
    }
}
