package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class UsuarioW implements Serializable {
    private String codigo;
    private String clave;
    private String nombres;
    private String apellidos;
    private Perfil perfilUsuario;
    private String correo;
    private String estado;

    public UsuarioW() {
    }
    public UsuarioW(String codigo, String clave) {
        this.codigo = codigo;
        this.clave = clave;
    }

    public UsuarioW(String codigo, String clave, String nombres, String apellidos, Perfil perfilUsuario, String correo, String estado) {
        this.codigo = codigo;
        this.clave = clave;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.perfilUsuario = perfilUsuario;
        this.correo = correo;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Perfil getPerfilUsuario() {
        return perfilUsuario;
    }

    public void setPerfilUsuario(Perfil perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
