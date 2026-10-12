package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Cotizacion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Guarda las cotizaciones en memoria. Todos los métodos son sincronizados porque varios usuarios pueden cotizar a la vez. */
@Repository
public class CotizacionRepository implements CotizacionRepositoryPort {

    private final List<Cotizacion> cotizaciones = new ArrayList<>();
    private final Map<String, Cotizacion> porClave = new HashMap<>();
    private long siguienteId = 1;

    @Override
    public synchronized Cotizacion guardar(Cotizacion cotizacion, String claveSolicitud) {
        String llave = (claveSolicitud == null || claveSolicitud.isBlank())
                ? null
                : cotizacion.usuarioId() + "|" + claveSolicitud.trim();

        if (llave != null && porClave.containsKey(llave)) {
            return porClave.get(llave);
        }

        Cotizacion guardada = new Cotizacion(
                siguienteId++,
                cotizacion.usuarioId(),
                cotizacion.comercianteId(),
                cotizacion.comercianteNombre(),
                cotizacion.fecha(),
                cotizacion.estado(),
                cotizacion.items(),
                cotizacion.total()
        );

        cotizaciones.add(guardada);
        if (llave != null) {
            porClave.put(llave, guardada);
        }
        return guardada;
    }

    @Override
    public synchronized List<Cotizacion> listarPorUsuario(long usuarioId) {
        return cotizaciones.stream()
                .filter(c -> c.usuarioId() == usuarioId)
                .sorted(Comparator.comparingLong(Cotizacion::id).reversed())
                .toList();
    }

    @Override
    public synchronized Optional<Cotizacion> buscarPorId(long id) {
        return cotizaciones.stream().filter(c -> c.id() == id).findFirst();
    }

    @Override
    public synchronized int contarPorComerciante(long comercianteId) {
        return (int) cotizaciones.stream().filter(c -> c.comercianteId() == comercianteId).count();
    }
}
