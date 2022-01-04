package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class CentroOperacion implements Serializable {
    private int codigo;
    private String descripcion;
    private String estado;

    public CentroOperacion() {
    }

    public CentroOperacion(int codigo, String descripcion, String estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
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
}
