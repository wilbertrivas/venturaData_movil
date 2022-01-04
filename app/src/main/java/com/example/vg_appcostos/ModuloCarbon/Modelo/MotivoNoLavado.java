package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class MotivoNoLavado implements Serializable {
    private String codigo;
    private String descripcion;
    private String estado;

    public MotivoNoLavado() {
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
}
