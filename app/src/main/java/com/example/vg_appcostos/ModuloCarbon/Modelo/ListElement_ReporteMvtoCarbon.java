package com.example.vg_appcostos.ModuloCarbon.Modelo;

import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;

public class ListElement_ReporteMvtoCarbon {
    private String contador;
    private String placa;
    private Cliente cliente;
    private Articulo articulo;
    private String deposito;
    private CentroCostoSubCentro centroCostoSubCentro;
    private CentroCostoAuxiliar centroCostoAuxiliarOrigen;
    private CentroCostoAuxiliar centroCostoAuxiliarDestino;
    private LaborRealizada laborRealizada;
    private String lavadoVehiculo;
    private MotivoNoLavado motivoNoLavado;
    private Equipo equipoLavadoVehiculo;
    private String recaudoEmpresa;
    private String fechaInicioDescargue;
    private String fechaFinDescargue;
    private String equipoEncargadosDescargue;
    private String estadoVehiculo;


    private String estado;

    public ListElement_ReporteMvtoCarbon() {
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
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

    public String getDeposito() {
        return deposito;
    }

    public void setDeposito(String deposito) {
        this.deposito = deposito;
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

    public void setCentroCostoAuxiliarOriente(CentroCostoAuxiliar centroCostoAuxiliarOrigen) {
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

    public String getLavadoVehiculo() {
        return lavadoVehiculo;
    }

    public void setLavadoVehiculo(String lavadoVehiculo) {
        this.lavadoVehiculo = lavadoVehiculo;
    }

    public Equipo getEquipoLavadoVehiculo() {
        return equipoLavadoVehiculo;
    }

    public void setEquipoLavadoVehiculo(Equipo equipoLavadoVehiculo) {
        this.equipoLavadoVehiculo = equipoLavadoVehiculo;
    }

    public String getFechaInicioDescargue() {
        return fechaInicioDescargue;
    }

    public void setFechaInicioDescargue(String fechaInicioDescargue) {
        this.fechaInicioDescargue = fechaInicioDescargue;
    }

    public String getFechaFinDescargue() {
        return fechaFinDescargue;
    }

    public void setFechaFinDescargue(String fechaFinDescargue) {
        this.fechaFinDescargue = fechaFinDescargue;
    }

    public String getEquipoEncargadosDescargue() {
        return equipoEncargadosDescargue;
    }

    public void setEquipoEncargadosDescargue(String equipoEncargadosDescargue) {
        this.equipoEncargadosDescargue = equipoEncargadosDescargue;
    }

    public String getEstadoVehiculo() {
        return estadoVehiculo;
    }

    public void setEstadoVehiculo(String estadoVehiculo) {
        this.estadoVehiculo = estadoVehiculo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCentroCostoAuxiliarOrigen(CentroCostoAuxiliar centroCostoAuxiliarOrigen) {
        this.centroCostoAuxiliarOrigen = centroCostoAuxiliarOrigen;
    }

    public MotivoNoLavado getMotivoNoLavado() {
        return motivoNoLavado;
    }

    public void setMotivoNoLavado(MotivoNoLavado motivoNoLavado) {
        this.motivoNoLavado = motivoNoLavado;
    }

    public String getContador() {
        return contador;
    }

    public void setContador(String contador) {
        this.contador = contador;
    }

    public String getRecaudoEmpresa() {
        return recaudoEmpresa;
    }

    public void setRecaudoEmpresa(String recaudoEmpresa) {
        this.recaudoEmpresa = recaudoEmpresa;
    }
}
