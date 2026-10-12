package com.mercado.altiplano.dto;

public class LoginResponse {

    private boolean exitoso;
    private String mensaje;
    private Long usuarioId;
    private String nombre;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(
            boolean exitoso,
            String mensaje,
            Long usuarioId,
            String nombre
    ) {
        this(exitoso, mensaje, usuarioId, nombre, null);
    }

    public LoginResponse(
            boolean exitoso,
            String mensaje,
            Long usuarioId,
            String nombre,
            String token
    ) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.token = token;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
