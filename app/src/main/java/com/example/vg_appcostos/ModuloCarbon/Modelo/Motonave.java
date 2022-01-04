package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class Motonave implements Serializable {
    private String codigo;
    private String descripcion;
    private String estado;
    private BaseDatos baseDatos;

    public Motonave() {
    }

    public Motonave(String codigo, String descripcion, String estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Motonave(String codigo, String descripcion, String estado, BaseDatos baseDatos) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.baseDatos = baseDatos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public BaseDatos getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }
}
