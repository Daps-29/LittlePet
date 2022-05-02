package com.little.pet.model;

public class Masco {
    String nombre;
    String descripcion;
    String foto1;
    String user;

    public Masco(String nombre, String descripcion, String foto1,String user) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.foto1 = foto1;
        this.user = user;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    Masco(){}
}
