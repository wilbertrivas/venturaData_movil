package com.example.vg_appcostos.ModuloEquipo.Model;

import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;

import java.io.Serializable;

public class AsignacionEquipo implements Serializable {
    private String codigo;
    private CentroOperacion centroOperacion;
    private SolicitudListadoEquipo solicitudListadoEquipo;
    private String fechaRegistro;
    private String fechaHoraInicio;
    private String fechaHoraFin;
    private String cantidadMinutosProgramados;
    private Equipo equipo;
    private Pertenencia pertenencia;
    private String cantidadMinutosOperativo;
    private String cantidadMinutosParada;
    private String cantidadMinutosTotal;
    private String estado;

    public AsignacionEquipo() {
    }

    public AsignacionEquipo(String codigo, CentroOperacion centroOperacion, SolicitudListadoEquipo solicitudListadoEquipo, String fechaRegistro, String fechaHoraInicio, String fechaHoraFin, String cantidadMinutosProgramados, Equipo equipo, Pertenencia pertenencia, String cantidadMinutosOperativo, String cantidadMinutosParada, String cantidadMinutosTotal, String estado) {
        this.codigo = codigo;
        this.centroOperacion = centroOperacion;
        this.solicitudListadoEquipo = solicitudListadoEquipo;
        this.fechaRegistro = fechaRegistro;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.cantidadMinutosProgramados = cantidadMinutosProgramados;
        this.equipo = equipo;
        this.pertenencia = pertenencia;
        this.cantidadMinutosOperativo = cantidadMinutosOperativo;
        this.cantidadMinutosParada = cantidadMinutosParada;
        this.cantidadMinutosTotal = cantidadMinutosTotal;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public CentroOperacion getCentroOperacion() {
        return centroOperacion;
    }

    public void setCentroOperacion(CentroOperacion centroOperacion) {
        this.centroOperacion = centroOperacion;
    }

    public SolicitudListadoEquipo getSolicitudListadoEquipo() {
        return solicitudListadoEquipo;
    }

    public void setSolicitudListadoEquipo(SolicitudListadoEquipo solicitudListadoEquipo) {
        this.solicitudListadoEquipo = solicitudListadoEquipo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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

    public String getCantidadMinutosProgramados() {
        return cantidadMinutosProgramados;
    }

    public void setCantidadMinutosProgramados(String cantidadMinutosProgramados) {
        this.cantidadMinutosProgramados = cantidadMinutosProgramados;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Pertenencia getPertenencia() {
        return pertenencia;
    }

    public void setPertenencia(Pertenencia pertenencia) {
        this.pertenencia = pertenencia;
    }

    public String getCantidadMinutosOperativo() {
        return cantidadMinutosOperativo;
    }

    public void setCantidadMinutosOperativo(String cantidadMinutosOperativo) {
        this.cantidadMinutosOperativo = cantidadMinutosOperativo;
    }

    public String getCantidadMinutosParada() {
        return cantidadMinutosParada;
    }

    public void setCantidadMinutosParada(String cantidadMinutosParada) {
        this.cantidadMinutosParada = cantidadMinutosParada;
    }

    public String getCantidadMinutosTotal() {
        return cantidadMinutosTotal;
    }

    public void setCantidadMinutosTotal(String cantidadMinutosTotal) {
        this.cantidadMinutosTotal = cantidadMinutosTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
