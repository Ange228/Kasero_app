package com.mercado.altiplano.service;

import com.mercado.altiplano.exception.SolicitudInvalidaException;

import java.util.Locale;

/** Formas de ordenar los resultados de la búsqueda (RF-06). */
public enum OrdenResultados {

    PRECIO_ASC,
    PRECIO_DESC,
    CALIFICACION,
    COTIZACIONES;

    /** Si no se indica nada, se ordena por menor precio de la canasta. */
    public static OrdenResultados desde(String texto) {
        if (texto == null || texto.isBlank()) {
            return PRECIO_ASC;
        }

        return switch (texto.trim().toLowerCase(Locale.ROOT)) {
            case "precio_asc" -> PRECIO_ASC;
            case "precio_desc" -> PRECIO_DESC;
            case "calificacion" -> CALIFICACION;
            case "cotizaciones" -> COTIZACIONES;
            default -> throw new SolicitudInvalidaException(
                    "Orden no válido. Usa: precio_asc, precio_desc, calificacion o cotizaciones");
        };
    }
}
