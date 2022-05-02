package com.little.pet.model;

public class Mascota {
    String mascotaid,nombre,descripcion,ubicacion,fecha,estado;
    String categoria,edad,tiempo,sexo,raza,vacuna,foto1,foto2,foto3;
    String estadoeliminacion,user,verificacion;

    public Mascota(String mascotaid, String nombre, String descripcion, String ubicacion, String fecha, String estado, String categoria, String edad, String tiempo, String sexo, String raza, String vacuna, String foto1, String foto2, String foto3, String estadoeliminacion, String user, String verificacion) {
        this.mascotaid = mascotaid;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.estado = estado;
        this.categoria = categoria;
        this.edad = edad;
        this.tiempo = tiempo;
        this.sexo = sexo;
        this.raza = raza;
        this.vacuna = vacuna;
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
        this.estadoeliminacion = estadoeliminacion;
        this.user = user;
        this.verificacion = verificacion;
    }

    public String getMascotaid() {
        return mascotaid;
    }

    public void setMascotaid(String mascotaid) {
        this.mascotaid = mascotaid;
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

    public void setDescripcion(String descipcion) {
        this.descripcion = descipcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getVacuna() {
        return vacuna;
    }

    public void setVacuna(String vacuna) {
        this.vacuna = vacuna;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getEstadoeliminacion() {
        return estadoeliminacion;
    }

    public void setEstadoeliminacion(String estadoeliminacion) {
        this.estadoeliminacion = estadoeliminacion;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVerificacion() {
        return verificacion;
    }

    public void setVerificacion(String verificacion) {
        this.verificacion = verificacion;
    }
}
