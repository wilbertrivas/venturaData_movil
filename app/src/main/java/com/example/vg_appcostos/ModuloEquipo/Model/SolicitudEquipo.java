package com.example.vg_appcostos.ModuloEquipo.Model;

import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.UsuarioW;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import java.io.Serializable;
import java.util.ArrayList;

public class SolicitudEquipo implements Serializable {
    private String codigo;
    private CentroOperacion centroOperacion;
    private String fechaSolicitud;
    private Usuario usuarioRealizaSolicitud;
    private String fechaRegistro;
    private EstadoSolicitudEquipos estadoSolicitudEquipo;
    private String fechaConfirmacion;
    private Usuario usuarioConfirmacionSolicitud;
    private ConfirmacionSolicitudEquipos confirmacionSolicitudEquipo;
    private ArrayList<SolicitudListadoEquipo> ListadoSolicitudesEquipos;

    public SolicitudEquipo() {
    }

    public SolicitudEquipo(String codigo, CentroOperacion centroOperacion, String fechaSolicitud, Usuario usuarioRealizaSolicitud, String fechaRegistro, EstadoSolicitudEquipos estadoSolicitudEquipo, String fechaConfirmacion, Usuario usuarioConfirmacionSolicitud, ConfirmacionSolicitudEquipos confirmacionSolicitudEquipo, ArrayList<SolicitudListadoEquipo> listadoSolicitudesEquipos) {
        this.codigo = codigo;
        this.centroOperacion = centroOperacion;
        this.fechaSolicitud = fechaSolicitud;
        this.usuarioRealizaSolicitud = usuarioRealizaSolicitud;
        this.fechaRegistro = fechaRegistro;
        this.estadoSolicitudEquipo = estadoSolicitudEquipo;
        this.fechaConfirmacion = fechaConfirmacion;
        this.usuarioConfirmacionSolicitud = usuarioConfirmacionSolicitud;
        this.confirmacionSolicitudEquipo = confirmacionSolicitudEquipo;
        ListadoSolicitudesEquipos = listadoSolicitudesEquipos;
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

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Usuario getUsuarioRealizaSolicitud() {
        return usuarioRealizaSolicitud;
    }

    public void setUsuarioRealizaSolicitud(Usuario usuarioRealizaSolicitud) {
        this.usuarioRealizaSolicitud = usuarioRealizaSolicitud;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public EstadoSolicitudEquipos getEstadoSolicitudEquipo() {
        return estadoSolicitudEquipo;
    }

    public void setEstadoSolicitudEquipo(EstadoSolicitudEquipos estadoSolicitudEquipo) {
        this.estadoSolicitudEquipo = estadoSolicitudEquipo;
    }

    public String getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(String fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public Usuario getUsuarioConfirmacionSolicitud() {
        return usuarioConfirmacionSolicitud;
    }

    public void setUsuarioConfirmacionSolicitud(Usuario usuarioConfirmacionSolicitud) {
        this.usuarioConfirmacionSolicitud = usuarioConfirmacionSolicitud;
    }

    public ConfirmacionSolicitudEquipos getConfirmacionSolicitudEquipo() {
        return confirmacionSolicitudEquipo;
    }

    public void setConfirmacionSolicitudEquipo(ConfirmacionSolicitudEquipos confirmacionSolicitudEquipo) {
        this.confirmacionSolicitudEquipo = confirmacionSolicitudEquipo;
    }

    public ArrayList<SolicitudListadoEquipo> getListadoSolicitudesEquipos() {
        return ListadoSolicitudesEquipos;
    }

    public void setListadoSolicitudesEquipos(ArrayList<SolicitudListadoEquipo> listadoSolicitudesEquipos) {
        ListadoSolicitudesEquipos = listadoSolicitudesEquipos;
    }
}
