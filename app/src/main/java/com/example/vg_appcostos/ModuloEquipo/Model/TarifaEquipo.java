package com.example.vg_appcostos.ModuloEquipo.Model;

import java.io.Serializable;

public class TarifaEquipo implements Serializable {
    private String año;
    private String codigoEquipo;
    private String valorHoraOperacion;
    private String valorHoraAlquiler;

    public TarifaEquipo() {
    }

    public TarifaEquipo(String año, String codigoEquipo, String valorHoraOperacion, String valorHoraAlquiler) {
        this.año = año;
        this.codigoEquipo = codigoEquipo;
        this.valorHoraOperacion = valorHoraOperacion;
        this.valorHoraAlquiler = valorHoraAlquiler;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public String getValorHoraOperacion() {
        return valorHoraOperacion;
    }

    public void setValorHoraOperacion(String valorHoraOperacion) {
        this.valorHoraOperacion = valorHoraOperacion;
    }

    public String getValorHoraAlquiler() {
        return valorHoraAlquiler;
    }

    public void setValorHoraAlquiler(String valorHoraAlquiler) {
        this.valorHoraAlquiler = valorHoraAlquiler;
    }
}
