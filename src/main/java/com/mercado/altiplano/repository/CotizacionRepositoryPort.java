package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Cotizacion;

import java.util.List;
import java.util.Optional;

public interface CotizacionRepositoryPort {

    /**
     * Guarda la cotización y le asigna su número.
     * Si el mismo usuario ya envió una con la misma "claveSolicitud", devuelve esa y no crea otra:
     * así un doble clic o un reintento por mala conexión no duplica registros (RNF-08).
     */
    Cotizacion guardar(Cotizacion cotizacion, String claveSolicitud);

    /** Las cotizaciones de un usuario, de la más nueva a la más antigua. */
    List<Cotizacion> listarPorUsuario(long usuarioId);

    Optional<Cotizacion> buscarPorId(long id);

    int contarPorComerciante(long comercianteId);
}
