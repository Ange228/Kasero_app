package com.mercado.altiplano.model;

public class Chat {

    private Long id;
    private Usuario usuario;

    public Chat() {
    }

    public Chat(Long id, Usuario usuario) {
        this.id = id;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}