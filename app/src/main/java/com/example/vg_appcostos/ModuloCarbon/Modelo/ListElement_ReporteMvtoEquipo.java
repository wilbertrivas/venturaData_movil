package com.example.vg_appcostos.ModuloCarbon.Modelo;

import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;

public class ListElement_ReporteMvtoEquipo {
    private String contador;
    private Equipo equipo;
    private String numeroOrden;
    private CentroCostoSubCentro centroCostoSubCentro;
    private CentroCostoAuxiliar centroCostoAuxiliarOrigen;
    private CentroCostoAuxiliar centroCostoAuxiliarDestino;
    private LaborRealizada laborRealizada;
    private Cliente cliente;
    private Articulo articulo;
    private Motonave motonave;
    private String fechaInicio;
    private String fechaFin;
    private String parada;
    private MotivoParada motivoParada;
    private String estado;

    public ListElement_ReporteMvtoEquipo() {
    }

    public String getContador() {
        return contador;
    }

    public void setContador(String contador) {
        this.contador = contador;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public CentroCostoSubCentro getCentroCostoSubCentro() {
        return centroCostoSubCentro;
    }

    public void setCentroCostoSubCentro(CentroCostoSubCentro centroCostoSubCentro) {
        this.centroCostoSubCentro = centroCostoSubCentro;
    }

    public CentroCostoAuxiliar getCentroCostoAuxiliarOrigen() {
        return centroCostoAuxiliarOrigen;
    }

    public void setCentroCostoAuxiliarOrigen(CentroCostoAuxiliar centroCostoAuxiliarOrigen) {
        this.centroCostoAuxiliarOrigen = centroCostoAuxiliarOrigen;
    }

    public CentroCostoAuxiliar getCentroCostoAuxiliarDestino() {
        return centroCostoAuxiliarDestino;
    }

    public void setCentroCostoAuxiliarDestino(CentroCostoAuxiliar centroCostoAuxiliarDestino) {
        this.centroCostoAuxiliarDestino = centroCostoAuxiliarDestino;
    }

    public LaborRealizada getLaborRealizada() {
        return laborRealizada;
    }

    public void setLaborRealizada(LaborRealizada laborRealizada) {
        this.laborRealizada = laborRealizada;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Motonave getMotonave() {
        return motonave;
    }

    public void setMotonave(Motonave motonave) {
        this.motonave = motonave;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getParada() {
        return parada;
    }

    public void setParada(String parada) {
        this.parada = parada;
    }

    public MotivoParada getMotivoParada() {
        return motivoParada;
    }

    public void setMotivoParada(MotivoParada motivoParada) {
        this.motivoParada = motivoParada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
