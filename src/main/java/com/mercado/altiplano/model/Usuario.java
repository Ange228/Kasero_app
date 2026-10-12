package com.mercado.altiplano.model;

public class Usuario {

    private Long id;
    private String nombre;
    private String correo;

    /** Nunca se guarda la contraseña real, solo su hash (ver PasswordHasher). */
    private String passwordHash;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String correo, String passwordHash) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
