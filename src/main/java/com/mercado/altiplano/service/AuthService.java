package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.LoginRequest;
import com.mercado.altiplano.dto.LoginResponse;
import com.mercado.altiplano.dto.RegistroRequest;
import com.mercado.altiplano.exception.AuthenticationException;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.Usuario;
import com.mercado.altiplano.repository.UsuarioRepositoryPort;
import com.mercado.altiplano.security.PasswordHasher;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int LARGO_MINIMO_PASSWORD = 8;

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final SesionService sesionService;

    public AuthService(
            UsuarioRepositoryPort usuarioRepository,
            PasswordHasher passwordHasher,
            SesionService sesionService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.sesionService = sesionService;
    }

    public LoginResponse login(LoginRequest request) {

        if (request == null || estaVacio(request.getCorreo()) || estaVacio(request.getPassword())) {
            throw new AuthenticationException("Ingresa tu correo y tu contraseña");
        }

        // Un solo mensaje para "no existe" y "clave incorrecta": así no se revela quién tiene cuenta.
        Usuario usuario = usuarioRepository.buscarPorCorreo(request.getCorreo().trim())
                .orElseThrow(() -> new AuthenticationException("Correo o contraseña incorrectos"));

        if (!passwordHasher.coincide(request.getPassword(), usuario.getPasswordHash())) {
            throw new AuthenticationException("Correo o contraseña incorrectos");
        }

        return new LoginResponse(
                true,
                "Inicio de sesión exitoso",
                usuario.getId(),
                usuario.getNombre(),
                sesionService.abrir(usuario.getId())
        );
    }

    /** Sincronizado para que dos registros a la vez no creen el mismo correo dos veces. */
    public synchronized LoginResponse registrar(RegistroRequest request) {

        if (request == null || estaVacio(request.nombre())) {
            throw new SolicitudInvalidaException("Escribe tu nombre");
        }

        String nombre = request.nombre().trim();
        if (nombre.length() < 2 || nombre.length() > 60) {
            throw new SolicitudInvalidaException("El nombre debe tener entre 2 y 60 caracteres");
        }

        if (estaVacio(request.correo()) || !CORREO.matcher(request.correo().trim()).matches()) {
            throw new SolicitudInvalidaException("Escribe un correo válido");
        }

        if (request.password() == null || request.password().length() < LARGO_MINIMO_PASSWORD) {
            throw new SolicitudInvalidaException(
                    "La contraseña debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres");
        }

        String correo = request.correo().trim();
        if (usuarioRepository.buscarPorCorreo(correo).isPresent()) {
            throw new SolicitudInvalidaException("Ya existe una cuenta con ese correo");
        }

        Usuario nuevo = usuarioRepository.guardar(
                new Usuario(null, nombre, correo, passwordHasher.cifrar(request.password())));

        return new LoginResponse(
                true,
                "Cuenta creada correctamente",
                nuevo.getId(),
                nuevo.getNombre(),
                sesionService.abrir(nuevo.getId())
        );
    }

    public void cerrarSesion(String cabeceraAutorizacion) {
        sesionService.cerrar(cabeceraAutorizacion);
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
