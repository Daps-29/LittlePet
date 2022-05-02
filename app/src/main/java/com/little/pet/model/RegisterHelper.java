package com.little.pet.model;

public class RegisterHelper {
    String nombres, apellidos, correo, contraseña,id,direccion,foto,telefono,sexo;
    String tipou;
    public RegisterHelper() {

    }

    public RegisterHelper(String nombres, String apellidos, String correo, String contra,String id,String tipou,String direccion,String foto, String telefono, String sexo) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contraseña = contra;
        this.tipou = tipou;
        this.id = id;
        this.direccion = direccion;
        this.foto = foto;
        this.telefono = telefono;
        this.sexo = sexo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellido(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getTipou() {
        return tipou;
    }

    public void setTipou(String tipou) {
        this.tipou = tipou;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}
