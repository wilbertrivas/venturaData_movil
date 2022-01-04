package com.example.vg_appcostos.ModuloCarbon.Controlador;

import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ControlDB_ZonaTrabajo {

    private Connection conexion = null;
    private String tipoConexion;

    public ControlDB_ZonaTrabajo(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }

    public ArrayList<ZonaTrabajo> buscarZonasTrabajosActivas(){
        ArrayList<ZonaTrabajo> listadoObjetos=null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean validador=true;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [zt_cdgo]\n" +
                                                                    "      ,[zt_desc]\n" +
                                                                    "  FROM ["+DB+"].[dbo].[zona_trabajo] WHERE [zt_estad]=1");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                if(validador){
                    validador=false;
                    listadoObjetos= new ArrayList<>();
                }
                ZonaTrabajo zonaTrabajo = new ZonaTrabajo();
                zonaTrabajo.setCodigo(resultSet.getString(1));
                zonaTrabajo.setDescripcion(resultSet.getString(2));
                listadoObjetos.add(zonaTrabajo);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }


    /*public ArrayList<CentroCostoAuxiliar> buscarCentroCostoAuxiliar_SegunZonaTrabajo(String codigoZonaTrabajo){
        ArrayList<TipoEquipo> listadoObjetos=null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT te_cdgo, te_desc, CASE WHEN (te_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS te_estad FROM ["+DB+"].[dbo].[tipo_equipo] WHERE te_estad=1;");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                TipoEquipo Objeto = new TipoEquipo();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }


    public ArrayList<TipoEquipo> listarTipoEquipo() throws SQLException {
        ArrayList<TipoEquipo> listadoObjetos = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT te_cdgo, te_desc, CASE WHEN (te_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS te_estad FROM ["+DB+"].[dbo].[tipo_equipo] WHERE te_estad=1;");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                TipoEquipo Objeto = new TipoEquipo();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }*/


}
