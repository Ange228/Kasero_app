package com.mercado.altiplano.model;

/** Sección del mercado: verduras, frutas, carnes, etc. El "icono" es una clave que el front convierte en dibujo. */
public record Sector(long id, String nombre, String icono) {
}
