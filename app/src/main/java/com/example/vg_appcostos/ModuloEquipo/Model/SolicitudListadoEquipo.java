package com.example.vg_appcostos.ModuloEquipo.Model;

import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Motonave;

import java.io.Serializable;

public class SolicitudListadoEquipo implements Serializable {
    private String codigo;
    private TipoEquipo tipoEquipo;
    private String marcaEquipo;
    private String modeloEquipo;
    private int cantidad;
    private String observacacion;
    private String fechaHoraInicio;
    private String fechaHoraFin;
    private int cantidadMinutos;
    private LaborRealizada laborRealizada;
    private Motonave motonave;
    private CentroCostoAuxiliar centroCostoAuxiliar;
    private Compañia compañia;
    private SolicitudEquipo solicitudEquipo; //Se agrega para poder llegar a la solicitud de Equipo
    private String motonaveBaseDatos;

    public SolicitudListadoEquipo() {
    }

    public SolicitudListadoEquipo(String codigo, TipoEquipo tipoEquipo, String marcaEquipo, String modeloEquipo, int cantidad, String observacacion, String fechaHoraInicio, String fechaHoraFin, int cantidadMinutos, LaborRealizada laborRealizada, Motonave motonave, CentroCostoAuxiliar centroCostoAuxiliar, Compañia compañia, SolicitudEquipo solicitudEquipo) {
        this.codigo = codigo;
        this.tipoEquipo = tipoEquipo;
        this.marcaEquipo = marcaEquipo;
        this.modeloEquipo = modeloEquipo;
        this.cantidad = cantidad;
        this.observacacion = observacacion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.cantidadMinutos = cantidadMinutos;
        this.laborRealizada = laborRealizada;
        this.motonave = motonave;
        this.centroCostoAuxiliar = centroCostoAuxiliar;
        this.compañia = compañia;
        this.solicitudEquipo = solicitudEquipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(TipoEquipo tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarcaEquipo() {
        return marcaEquipo;
    }

    public void setMarcaEquipo(String marcaEquipo) {
        this.marcaEquipo = marcaEquipo;
    }

    public String getModeloEquipo() {
        return modeloEquipo;
    }

    public void setModeloEquipo(String modeloEquipo) {
        this.modeloEquipo = modeloEquipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacacion() {
        return observacacion;
    }

    public void setObservacacion(String observacacion) {
        this.observacacion = observacacion;
    }

    public String getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(String fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public String getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(String fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public int getCantidadMinutos() {
        return cantidadMinutos;
    }

    public void setCantidadMinutos(int cantidadMinutos) {
        this.cantidadMinutos = cantidadMinutos;
    }

    public LaborRealizada getLaborRealizada() {
        return laborRealizada;
    }

    public void setLaborRealizada(LaborRealizada laborRealizada) {
        this.laborRealizada = laborRealizada;
    }

    public Motonave getMotonave() {
        return motonave;
    }

    public void setMotonave(Motonave motonave) {
        this.motonave = motonave;
    }

    public CentroCostoAuxiliar getCentroCostoAuxiliar() {
        return centroCostoAuxiliar;
    }

    public void setCentroCostoAuxiliar(CentroCostoAuxiliar centroCostoAuxiliar) {
        this.centroCostoAuxiliar = centroCostoAuxiliar;
    }

    public Compañia getCompañia() {
        return compañia;
    }

    public void setCompañia(Compañia compañia) {
        this.compañia = compañia;
    }

    public SolicitudEquipo getSolicitudEquipo() {
        return solicitudEquipo;
    }

    public void setSolicitudEquipo(SolicitudEquipo solicitudEquipo) {
        this.solicitudEquipo = solicitudEquipo;
    }

    public String getMotonaveBaseDatos() {
        return motonaveBaseDatos;
    }

    public void setMotonaveBaseDatos(String motonaveBaseDatos) {
        this.motonaveBaseDatos = motonaveBaseDatos;
    }
}
