package com.example.vg_appcostos.ModuloEquipo.Model;

import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;

import java.io.Serializable;

public class LaborRealizada implements Serializable {
    private String codigo;
    private String descripcion;
    private CentroCostoSubCentro centroCostoSubCentro;
    private String estado;
    private String es_operativa;
    private String es_parada;
    private String bodegaDestino;

    public LaborRealizada() {
    }

    public LaborRealizada(String codigo, String descripcion, CentroCostoSubCentro centroCostoSubCentro, String estado, String es_operativa, String es_parada) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.centroCostoSubCentro = centroCostoSubCentro;
        this.estado = estado;
        this.es_operativa = es_operativa;
        this.es_parada = es_parada;
    }

    public LaborRealizada(String codigo, String descripcion, CentroCostoSubCentro centroCostoSubCentro, String estado, String es_operativa, String es_parada, String bodegaDestino) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.centroCostoSubCentro = centroCostoSubCentro;
        this.estado = estado;
        this.es_operativa = es_operativa;
        this.es_parada = es_parada;
        this.bodegaDestino = bodegaDestino;
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

    public CentroCostoSubCentro getCentroCostoSubCentro() {
        return centroCostoSubCentro;
    }

    public void setCentroCostoSubCentro(CentroCostoSubCentro centroCostoSubCentro) {
        this.centroCostoSubCentro = centroCostoSubCentro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEs_operativa() {
        return es_operativa;
    }

    public void setEs_operativa(String es_operativa) {
        this.es_operativa = es_operativa;
    }

    public String getEs_parada() {
        return es_parada;
    }

    public void setEs_parada(String es_parada) {
        this.es_parada = es_parada;
    }

    public String getBodegaDestino() {
        return bodegaDestino;
    }

    public void setBodegaDestino(String bodegaDestino) {
        this.bodegaDestino = bodegaDestino;
    }
}
