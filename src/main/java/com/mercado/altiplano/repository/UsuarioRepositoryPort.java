package com.mercado.altiplano.repository;

import com.mercado.altiplano.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorCorreo(String correo);

    Usuario guardar(Usuario usuario);
}