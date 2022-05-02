package com.little.pet.model;

public class OrganizacionDto {


    /* COMIENZO BASE DE DATOS */

    private int estado_organizacion, contacto;
    private String id_organizacion, descripcion, direccion, direccion_literal;
    private String foto, foto_portada;
    private String horaen, horafin;
    private String nombre, latitud, longitud, referencia;
    private String lunes, martes, miercoles, jueves, viernes, sabado, domingo;

    //CONSTRUCTOR
    public OrganizacionDto(){}

    public OrganizacionDto(int estado_organizacion, int contacto, String id_organizacion, String descripcion, String direccion, String direccion_literal, String foto, String foto_portada, String horaen, String horafin, String nombre, String latitud, String longitud, String referencia, String lunes, String martes, String miercoles, String jueves, String viernes, String sabado, String domingo) {
        this.estado_organizacion = estado_organizacion;
        this.contacto = contacto;
        this.id_organizacion = id_organizacion;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.direccion_literal = direccion_literal;
        this.foto = foto;
        this.foto_portada = foto_portada;
        this.horaen = horaen;
        this.horafin = horafin;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.referencia = referencia;
        this.lunes = lunes;
        this.martes = martes;
        this.miercoles = miercoles;
        this.jueves = jueves;
        this.viernes = viernes;
        this.sabado = sabado;
        this.domingo = domingo;
    }


    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getLunes() {
        return lunes;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public String getMartes() {
        return martes;
    }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public String getViernes() {
        return viernes;
    }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public String getSabado() {
        return sabado;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public String getDomingo() {
        return domingo;
    }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }


    public String getId_organizacion() {
        return id_organizacion;
    }

    public void setId_organizacion(String id_organizacion) {
        this.id_organizacion = id_organizacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion_literal() {
        return direccion_literal;
    }

    public void setDireccion_literal(String direccion_literal) {
        this.direccion_literal = direccion_literal;
    }

    public int getContacto() {
        return contacto;
    }

    public void setContacto(int contacto) {
        this.contacto = contacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHoraen() {
        return horaen;
    }

    public void setHoraen(String horaen) {
        this.horaen = horaen;
    }

    public String getHorafin() {
        return horafin;
    }

    public void setHorafin(String horafin) {
        this.horafin = horafin;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto_lista) {
        this.foto = foto_lista;
    }

    public String getFoto_portada() {
        return foto_portada;
    }

    public void setFoto_portada(String foto_portada) {
        this.foto_portada = foto_portada;
    }

    public int getEstado_organizacion() {
        return estado_organizacion;
    }

    public void setEstado_organizacion(int estado_organizacion) {
        this.estado_organizacion = estado_organizacion;
    }







}
