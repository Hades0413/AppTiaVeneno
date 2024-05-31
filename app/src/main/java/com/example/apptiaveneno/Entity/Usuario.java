package com.example.apptiaveneno.Entity;

public class Usuario {

    private int IdUsuario;
    private String NombreCompleto;
    private String Correo;
    private String Clave;

    public Usuario(int idUsuario, String nombreCompleto, String correo, String clave) {
        IdUsuario = idUsuario;
        NombreCompleto = nombreCompleto;
        Correo = correo;
        Clave = clave;
    }

    public int getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        IdUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return NombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        NombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String clave) {
        Clave = clave;
    }
}