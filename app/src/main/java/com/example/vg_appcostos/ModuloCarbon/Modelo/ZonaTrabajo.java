package com.example.vg_appcostos.ModuloCarbon.Modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class ZonaTrabajo implements Serializable {
    private String codigo;
    private ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar;
    private String descripcion;
    private String estado;

    public ZonaTrabajo() {
    }

    public ZonaTrabajo(String codigo, ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar, String descripcion, String estado) {
        this.codigo = codigo;
        this.listadoCentroCostoAuxiliar = listadoCentroCostoAuxiliar;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ArrayList<CentroCostoAuxiliar> getListadoCentroCostoAuxiliar() {
        return listadoCentroCostoAuxiliar;
    }

    public void setListadoCentroCostoAuxiliar(ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar) {
        this.listadoCentroCostoAuxiliar = listadoCentroCostoAuxiliar;
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
