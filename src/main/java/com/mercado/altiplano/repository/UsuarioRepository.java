package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarda los usuarios en memoria (se pierden al reiniciar).
 * Más adelante se cambia por PostgreSQL sin tocar el servicio, gracias al puerto.
 */
@Repository
public class UsuarioRepository implements UsuarioRepositoryPort {

    private final List<Usuario> usuarios = new CopyOnWriteArrayList<>();
    private final AtomicLong siguienteId = new AtomicLong(1);

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        if (correo == null) {
            return Optional.empty();
        }

        return usuarios.stream()
                .filter(usuario ->
                        usuario.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(siguienteId.getAndIncrement());
        }
        usuarios.add(usuario);
        return usuario;
    }
}
