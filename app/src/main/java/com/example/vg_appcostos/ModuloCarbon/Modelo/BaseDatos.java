package com.example.vg_appcostos.ModuloCarbon.Modelo;

public class BaseDatos {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String estado;

    public BaseDatos() {
    }

    public BaseDatos(String codigo) {
        this.codigo = codigo;
    }

    public BaseDatos(String codigo, String nombre, String descripcion, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
