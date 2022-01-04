package com.example.vg_appcostos.Sistemas.Modelo;

import java.io.Serializable;

public class Permisos implements Serializable {
    private String codigo;
    private String Descripcion;

    public Permisos(String codigo, String Descripcion) {
        this.codigo = codigo;
        this.Descripcion = Descripcion;
    }

    public Permisos() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
}
