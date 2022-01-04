package com.example.vg_appcostos.ModuloEquipo.Model;

import java.io.Serializable;

public class ProveedorEquipo implements Serializable {

    private String codigo;
    private String nit;
    private String descripcion;
    private String estado;

    public ProveedorEquipo() {
    }

    public ProveedorEquipo(String codigo, String nit, String descripcion, String estado) {
        this.codigo = codigo;
        this.nit = nit;
        this.descripcion = descripcion;
        this.estado = estado;
    }
    public ProveedorEquipo(String codigo, String descripcion, String estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
