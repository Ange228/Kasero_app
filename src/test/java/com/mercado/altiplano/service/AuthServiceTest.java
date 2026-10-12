package com.mercado.altiplano.service;

import com.mercado.altiplano.dto.LoginRequest;
import com.mercado.altiplano.dto.LoginResponse;
import com.mercado.altiplano.dto.RegistroRequest;
import com.mercado.altiplano.exception.AuthenticationException;
import com.mercado.altiplano.exception.SolicitudInvalidaException;
import com.mercado.altiplano.model.Usuario;
import com.mercado.altiplano.repository.UsuarioRepository;
import com.mercado.altiplano.security.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private UsuarioRepository repositorio;
    private SesionService sesiones;
    private AuthService auth;

    @BeforeEach
    void preparar() {
        repositorio = new UsuarioRepository();
        sesiones = new SesionService();
        auth = new AuthService(repositorio, new PasswordHasher(1_000), sesiones);
    }

    private static LoginRequest credenciales(String correo, String password) {
        LoginRequest request = new LoginRequest();
        request.setCorreo(correo);
        request.setPassword(password);
        return request;
    }

    // ---------- Registro (RF-01) ----------

    @Test
    void registrarCreaLaCuentaYDevuelveUnToken() {
        LoginResponse respuesta = auth.registrar(
                new RegistroRequest("Lucia Quispe", "lucia@correo.com", "clave12345"));

        assertTrue(respuesta.isExitoso());
        assertNotNull(respuesta.getUsuarioId());
        assertEquals("Lucia Quispe", respuesta.getNombre());
        assertNotNull(respuesta.getToken());
    }

    @Test
    void registrarGuardaLaContrasenaCifrada() {
        auth.registrar(new RegistroRequest("Lucia Quispe", "lucia@correo.com", "clave12345"));

        Usuario guardado = repositorio.buscarPorCorreo("lucia@correo.com").orElseThrow();
        assertNotEquals("clave12345", guardado.getPasswordHash());
        assertFalse(guardado.getPasswordHash().contains("clave12345"));
    }

    @Test
    void registrarRechazaUnCorreoYaRegistradoSinImportarMayusculas() {
        auth.registrar(new RegistroRequest("Lucia", "lucia@correo.com", "clave12345"));

        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(new RegistroRequest("Otra Lucia", "LUCIA@correo.com", "otraClave99")));

        assertEquals("Ya existe una cuenta con ese correo", error.getMessage());
    }

    @Test
    void registrarRechazaContrasenasCortas() {
        assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(new RegistroRequest("Lucia", "lucia@correo.com", "corta")));
    }

    @Test
    void registrarRechazaCorreosInvalidos() {
        assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(new RegistroRequest("Lucia", "sin-arroba", "clave12345")));
        assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(new RegistroRequest("Lucia", "", "clave12345")));
    }

    @Test
    void registrarRechazaNombresVacios() {
        assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(new RegistroRequest("  ", "lucia@correo.com", "clave12345")));
        assertThrows(SolicitudInvalidaException.class, () ->
                auth.registrar(null));
    }

    // ---------- Inicio de sesión ----------

    @Test
    void loginCorrectoDevuelveElUsuarioYUnToken() {
        auth.registrar(new RegistroRequest("Lucia Quispe", "lucia@correo.com", "clave12345"));

        LoginResponse respuesta = auth.login(credenciales("lucia@correo.com", "clave12345"));

        assertTrue(respuesta.isExitoso());
        assertEquals("Lucia Quispe", respuesta.getNombre());
        assertNotNull(respuesta.getToken());
    }

    @Test
    void elTokenDelLoginIdentificaAlUsuario() {
        LoginResponse registro = auth.registrar(
                new RegistroRequest("Lucia Quispe", "lucia@correo.com", "clave12345"));

        LoginResponse login = auth.login(credenciales("lucia@correo.com", "clave12345"));

        assertEquals(registro.getUsuarioId(), sesiones.usuarioDe("Bearer " + login.getToken()));
    }

    @Test
    void loginConClaveIncorrectaNoRevelaSiElCorreoExiste() {
        auth.registrar(new RegistroRequest("Lucia", "lucia@correo.com", "clave12345"));

        AuthenticationException claveMala = assertThrows(AuthenticationException.class, () ->
                auth.login(credenciales("lucia@correo.com", "equivocada")));
        AuthenticationException correoMalo = assertThrows(AuthenticationException.class, () ->
                auth.login(credenciales("nadie@correo.com", "clave12345")));

        assertEquals("Correo o contraseña incorrectos", claveMala.getMessage());
        assertEquals(claveMala.getMessage(), correoMalo.getMessage());
    }

    @Test
    void loginConCamposVaciosSeRechaza() {
        assertThrows(AuthenticationException.class, () -> auth.login(credenciales("", "")));
        assertThrows(AuthenticationException.class, () -> auth.login(credenciales(null, "x")));
        assertThrows(AuthenticationException.class, () -> auth.login(null));
    }

    @Test
    void cerrarSesionInvalidaElToken() {
        auth.registrar(new RegistroRequest("Lucia", "lucia@correo.com", "clave12345"));
        LoginResponse login = auth.login(credenciales("lucia@correo.com", "clave12345"));

        auth.cerrarSesion("Bearer " + login.getToken());

        assertThrows(AuthenticationException.class, () ->
                sesiones.usuarioDe("Bearer " + login.getToken()));
    }
}
