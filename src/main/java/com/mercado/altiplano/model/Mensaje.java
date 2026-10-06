package com.mercado.altiplano.model;

public class Mensaje {

    private Long id;
    private String contenido;
    private Chat chat;

    public Mensaje() {
    }

    public Mensaje(Long id, String contenido, Chat chat) {
        this.id = id;
        this.contenido = contenido;
        this.chat = chat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}