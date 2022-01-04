package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class Transportadora implements Serializable {
    private String codigo;
    private String nit;
    private String descripcion;
    private String observacion;
    private String estado;
    private BaseDatos baseDatos;

    public Transportadora() {
    }

    public Transportadora(String codigo, String nit, String descripcion, String observacion, String estado) {
        this.codigo = codigo;
        this.nit = nit;
        this.descripcion = descripcion;
        this.observacion = observacion;
        this.estado = estado;
    }

    public Transportadora(String codigo, String nit, String descripcion, String observacion, String estado, BaseDatos baseDatos) {
        this.codigo = codigo;
        this.nit = nit;
        this.descripcion = descripcion;
        this.observacion = observacion;
        this.estado = estado;
        this.baseDatos = baseDatos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
