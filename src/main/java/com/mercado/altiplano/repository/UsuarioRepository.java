package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository implements UsuarioRepositoryPort {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioRepository() {
        usuarios.add(
                new Usuario(
                        1L,
                        "Angelica",
                        "angelica@gmail.com",
                        "123456"
                )
        );
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarios.stream()
                .filter(usuario ->
                        usuario.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        usuarios.add(usuario);
        return usuario;
    }
}