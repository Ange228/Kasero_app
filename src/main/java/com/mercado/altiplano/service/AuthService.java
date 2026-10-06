package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.LoginRequest;
import com.mercado.altiplano.dto.LoginResponse;
import com.mercado.altiplano.model.Usuario;
import com.mercado.altiplano.repository.UsuarioRepositoryPort;
import com.mercado.altiplano.exception.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepositoryPort usuarioRepository;

    public AuthService(UsuarioRepositoryPort usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Optional<Usuario> usuarioEncontrado =
                usuarioRepository.buscarPorCorreo(request.getCorreo());

        if (usuarioEncontrado.isEmpty()) {
            throw new AuthenticationException("El usuario no existe");
        }

        Usuario usuario = usuarioEncontrado.get();

        if (!usuario.getPassword().equals(request.getPassword())) {
            throw new AuthenticationException("Contraseña incorrecta");
        }

        return new LoginResponse(
                true,
                "Inicio de sesión exitoso",
                usuario.getId(),
                usuario.getNombre()
        );
    }
}