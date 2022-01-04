package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;

public class CentroCostoAuxiliar implements Serializable {
    private String codigo;
    private CentroCostoSubCentro centroCostoSubCentro;
    private String descripcion;
    private String estado;

    public CentroCostoAuxiliar(String codigo, CentroCostoSubCentro centroCostoSubCentro, String descripcion, String estado) {
        this.codigo = codigo;
        this.centroCostoSubCentro = centroCostoSubCentro;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public CentroCostoAuxiliar() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public CentroCostoSubCentro getCentroCostoSubCentro() {
        return centroCostoSubCentro;
    }

    public void setCentroCostoSubCentro(CentroCostoSubCentro centroCostoSubCentro) {
        this.centroCostoSubCentro = centroCostoSubCentro;
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
