package com.mercado.altiplano.config;

import com.mercado.altiplano.model.Usuario;
import com.mercado.altiplano.repository.UsuarioRepositoryPort;
import com.mercado.altiplano.security.PasswordHasher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Crea el usuario de prueba al iniciar la aplicación (la contraseña se guarda cifrada).
 * Es solo para desarrollo y demostración.
 */
@Component
public class UsuarioDemoInicial implements CommandLineRunner {

    public static final String CORREO_DEMO = "demo@kasero.local";
    public static final String PASSWORD_DEMO = "123456";

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasher passwordHasher;

    public UsuarioDemoInicial(
            UsuarioRepositoryPort usuarioRepository,
            PasswordHasher passwordHasher
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.buscarPorCorreo(CORREO_DEMO).isEmpty()) {
            usuarioRepository.guardar(new Usuario(
                    null,
                    "Usuario Demo",
                    CORREO_DEMO,
                    passwordHasher.cifrar(PASSWORD_DEMO)
            ));
        }
    }
}
