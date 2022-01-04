package com.example.vg_appcostos.ModuloEquipo.Controller;

import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloCarbon.Modelo.BaseDatos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Motonave;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.ClasificadorEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Compañia;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.Pertenencia;
import com.example.vg_appcostos.ModuloEquipo.Model.ProveedorEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.SolicitudListadoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ControlDB_AsignacionEquipo {
    private Connection conexion = null;
    private String tipoConexion;
    public ControlDB_AsignacionEquipo(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }
    //Consultas Optimizadas
    public AsignacionEquipo buscarAsignacionPorEquipo(Equipo equipoInput) throws SQLException {
        AsignacionEquipo asignacionEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT \n" +
                    "    [ae_cdgo]-- 1 \n" +
                    "\t,ae_cntro_oper.[co_cdgo]-- 2 \n" +
                    "    ,ae_cntro_oper.[co_desc]-- 3 \n" +
                    "\t,ae_cntro_cost_subcentro.[ccs_cdgo]-- 4\n" +
                    "    ,ae_cntro_cost_subcentro.[ccs_desc]-- 5\n" +
                    "\t,[cca_cdgo]-- 6\n" +
                    "\t,[cca_desc]-- 7\n" +
                    "\t,[ae_fecha_hora_inicio]-- 8\n" +
                    "    ,[ae_fecha_hora_fin]-- 9\n" +
                    "    ,[ae_cant_minutos]-- 10 \n" +
                    "\t,[eq_cdgo]-- 11 \n" +
                    "\t,eq_tipo_equipo.[te_cdgo]-- 12 \n" +
                    "    ,eq_tipo_equipo.[te_desc]-- 13\n" +
                    "\t,[eq_marca]-- 14\n" +
                    "    ,[eq_modelo]-- 15 \n" +
                    "\t,[eq_desc]-- 16 \n" +
                    "\t,[pe_cdgo]-- 17 \n" +
                    "    ,[pe_desc]-- 18\n" +
                    " FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "    WHERE \n" +
                    "        [ae_equipo_cdgo]=?  AND  \n" +
                    "\t\t([se_estado_solicitud_equipo_cdgo]=3 OR [se_estado_solicitud_equipo_cdgo]=4) AND [se_confirmacion_solicitud_equipo_cdgo]=1 AND\n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin] AND [ae_estad]=1");
            query.setString(1, equipoInput.getCodigo());
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    asignacionEquipo = new AsignacionEquipo();
                    valor=false;
                }
                asignacionEquipo.setCodigo(resultSet.getString(1));
                CentroOperacion CentroOperacionAsignacion= new CentroOperacion();
                CentroOperacionAsignacion.setCodigo(Integer.parseInt(resultSet.getString(2)));
                CentroOperacionAsignacion.setDescripcion(resultSet.getString(3));
                asignacionEquipo.setCentroOperacion(CentroOperacionAsignacion);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(6));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(7));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(8));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(9));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(10));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(11));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(12),resultSet.getString(13),""));
                equipo.setMarca(resultSet.getString(14));
                equipo.setModelo(resultSet.getString(15));
                equipo.setDescripcion(resultSet.getString(16));
                ProveedorEquipo provEquipo = new ProveedorEquipo();
                provEquipo.setCodigo(resultSet.getString(17));
                provEquipo.setDescripcion(resultSet.getString(18));
                equipo.setProveedorEquipo(provEquipo);
                asignacionEquipo.setEquipo(equipo);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return asignacionEquipo;
    }
    public ArrayList<AsignacionEquipo> buscarAsignacionEquiposTodas() throws SQLException {
        ArrayList<AsignacionEquipo> listadoAsignacion = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT \n" +
                    "    [ae_cdgo]-- 1 \n" +
                    "\t,ae_cntro_oper.[co_cdgo]-- 2 \n" +
                    "    ,ae_cntro_oper.[co_desc]-- 3 \n" +
                    "\t,ae_cntro_cost_subcentro.[ccs_cdgo]-- 4\n" +
                    "    ,ae_cntro_cost_subcentro.[ccs_desc]-- 5\n" +
                    "\t,[cca_cdgo]-- 6\n" +
                    "\t,[cca_desc]-- 7\n" +
                    "\t,[ae_fecha_hora_inicio]-- 8\n" +
                    "    ,[ae_fecha_hora_fin]-- 9\n" +
                    "    ,[ae_cant_minutos]-- 10 \n" +
                    "\t,[eq_cdgo]-- 11 \n" +
                    "\t,eq_tipo_equipo.[te_cdgo]-- 12 \n" +
                    "    ,eq_tipo_equipo.[te_desc]-- 13\n" +
                    "\t,[eq_marca]-- 14\n" +
                    "    ,[eq_modelo]-- 15 \n" +
                    "\t,[eq_desc]-- 16 \n" +
                    "\t,[pe_cdgo]-- 17 \n" +
                    "    ,[pe_desc]-- 18\n" +
                    " FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "    WHERE ([se_estado_solicitud_equipo_cdgo]=3 OR [se_estado_solicitud_equipo_cdgo]=4) AND [se_confirmacion_solicitud_equipo_cdgo]=1 AND\n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin] AND [ae_estad]=1");
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    listadoAsignacion= new ArrayList<>();
                    valor=false;
                }
                AsignacionEquipo asignacionEquipo=new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(1));
                CentroOperacion CentroOperacionAsignacion= new CentroOperacion();
                CentroOperacionAsignacion.setCodigo(Integer.parseInt(resultSet.getString(2)));
                CentroOperacionAsignacion.setDescripcion(resultSet.getString(3));
                asignacionEquipo.setCentroOperacion(CentroOperacionAsignacion);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(6));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(7));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(8));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(9));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(10));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(11));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(12),resultSet.getString(13),""));
                equipo.setMarca(resultSet.getString(14));
                equipo.setModelo(resultSet.getString(15));
                equipo.setDescripcion(resultSet.getString(16));
                ProveedorEquipo provEquipo = new ProveedorEquipo();
                provEquipo.setCodigo(resultSet.getString(17));
                provEquipo.setDescripcion(resultSet.getString(18));
                equipo.setProveedorEquipo(provEquipo);
                asignacionEquipo.setEquipo(equipo);
                listadoAsignacion.add(asignacionEquipo);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return listadoAsignacion;
    }

    //Consultas Optimizadas tipoEquipo, MarcaEquipo y Equipos programados de acuerdo a la fecha del sistema.
    public ArrayList<TipoEquipo> buscarTipoEquiposProgramados() throws SQLException {
        ArrayList<TipoEquipo> listadoTipoEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT DISTINCT eq_tipo_equipo.[te_desc]  FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[motonave] ON [mn_cdgo]=[sle_motonave_cdgo] AND [sle_motonave_base_datos_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[base_datos] ON [bd_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "    WHERE ([se_estado_solicitud_equipo_cdgo]=3 OR [se_estado_solicitud_equipo_cdgo]=4) AND [se_confirmacion_solicitud_equipo_cdgo]=1 AND\n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin] AND [ae_estad]=1");
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    listadoTipoEquipo= new ArrayList<>();
                    valor=false;
                }
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setDescripcion(resultSet.getString(1));
                listadoTipoEquipo.add(tipoEquipo);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return listadoTipoEquipo;
    }
    public ArrayList<String> buscarMarcaEquiposProgramados(String tipoEquipo) throws SQLException {
        ArrayList<String> listadoMarcaEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT DISTINCT [eq_marca]  FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[motonave] ON [mn_cdgo]=[sle_motonave_cdgo] AND [sle_motonave_base_datos_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[base_datos] ON [bd_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "    WHERE ([se_estado_solicitud_equipo_cdgo]=3 OR [se_estado_solicitud_equipo_cdgo]=4) AND [se_confirmacion_solicitud_equipo_cdgo]=1 AND\n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin] AND [ae_estad]=1 AND eq_tipo_equipo.[te_desc] LIKE ?");
            query.setString(1, tipoEquipo);
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    listadoMarcaEquipo= new ArrayList<>();
                    valor=false;
                }
                listadoMarcaEquipo.add(resultSet.getString(1));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return listadoMarcaEquipo;
    }
    public ArrayList<AsignacionEquipo> buscarAsignacionEquipos_Por_TipoEquipo_MarcaEquipo(String tipoEquipo, String marcaEquipo) throws SQLException {
        ArrayList<AsignacionEquipo> listadoAsignacion = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT \n" +
                    "    [ae_cdgo]-- 1 \n" +
                    "\t,ae_cntro_oper.[co_cdgo]-- 2 \n" +
                    "    ,ae_cntro_oper.[co_desc]-- 3 \n" +
                    "\t,ae_cntro_cost_subcentro.[ccs_cdgo]-- 4\n" +
                    "    ,ae_cntro_cost_subcentro.[ccs_desc]-- 5\n" +
                    "\t,[cca_cdgo]-- 6\n" +
                    "\t,[cca_desc]-- 7\n" +
                    "\t,[ae_fecha_hora_inicio]-- 8\n" +
                    "    ,[ae_fecha_hora_fin]-- 9\n" +
                    "    ,[ae_cant_minutos]-- 10 \n" +
                    "\t,[eq_cdgo]-- 11 \n" +
                    "\t,eq_tipo_equipo.[te_cdgo]-- 12 \n" +
                    "    ,eq_tipo_equipo.[te_desc]-- 13\n" +
                    "\t,[eq_marca]-- 14\n" +
                    "    ,[eq_modelo]-- 15 \n" +
                    "\t,[eq_desc]-- 16 \n" +
                    "\t,[pe_cdgo]-- 17 \n" +
                    "    ,[pe_desc]-- 18\n" +
                    "    ,[mn_cdgo]-- 19\n" +
                    "    ,[mn_desc]-- 20\n" +
                    "    ,[mn_base_datos_cdgo]-- 21\n" +
                    "    ,[lr_cdgo]-- 22\n" +
                    "    ,[lr_desc]-- 23\n" +
                    " FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[motonave] ON [mn_cdgo]=[sle_motonave_cdgo] AND [sle_motonave_base_datos_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    LEFT JOIN ["+DB+"].[dbo].[base_datos] ON [bd_cdgo]=[mn_base_datos_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "    WHERE ([se_estado_solicitud_equipo_cdgo]=3 OR [se_estado_solicitud_equipo_cdgo]=4) AND [se_confirmacion_solicitud_equipo_cdgo]=1 AND\n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin] AND [ae_estad]=1  AND eq_tipo_equipo.[te_desc] LIKE ? AND [eq_marca] LIKE ?");
            query.setString(1, tipoEquipo);
            query.setString(2, marcaEquipo);
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()) {
                if (valor) {
                    listadoAsignacion = new ArrayList<>();
                    valor = false;
                }
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(1));
                CentroOperacion CentroOperacionAsignacion = new CentroOperacion();
                CentroOperacionAsignacion.setCodigo(Integer.parseInt(resultSet.getString(2)));
                CentroOperacionAsignacion.setDescripcion(resultSet.getString(3));
                asignacionEquipo.setCentroOperacion(CentroOperacionAsignacion);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(6));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(7));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(22));
                laborRealizada.setDescripcion(resultSet.getString(23));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(8));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(9));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(10));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(11));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(12), resultSet.getString(13), ""));
                equipo.setMarca(resultSet.getString(14));
                equipo.setModelo(resultSet.getString(15));
                equipo.setDescripcion(resultSet.getString(16));
                ProveedorEquipo provEquipo = new ProveedorEquipo();
                provEquipo.setCodigo(resultSet.getString(17));
                provEquipo.setDescripcion(resultSet.getString(18));
                equipo.setProveedorEquipo(provEquipo);

                Motonave motonave = new Motonave();
                if (resultSet.getString(19) != null) {//validamos si hay motonaves
                    motonave.setCodigo(resultSet.getString(19));
                    motonave.setDescripcion(resultSet.getString(20));
                    motonave.setBaseDatos(new BaseDatos(resultSet.getString(21)));
                } else {
                    motonave.setCodigo(null);
                    motonave.setDescripcion(null);
                    motonave.setBaseDatos(new BaseDatos(null));
                }
                asignacionEquipo.getSolicitudListadoEquipo().setMotonave(motonave);
                asignacionEquipo.setEquipo(equipo);
                listadoAsignacion.add(asignacionEquipo);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return listadoAsignacion;
    }



    //Buscar Asignación de un Equipo Segun Fecha actual del sistema
    /*public AsignacionEquipo buscarAsignacionPorEquipo2(Equipo equipoInput) throws SQLException {
        // Equipo Objeto = null;
        AsignacionEquipo asignacionEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT "+
                    "       [ae_cdgo]-- 1\n" +
                    "      ,[ae_cntro_oper_cdgo]-- 2\n" +
                    "			--Centro Operacion\n" +
                    "			,ae_cntro_oper.[co_cdgo]-- 3\n" +
                    "			,ae_cntro_oper.[co_desc]-- 4\n" +
                    "			,ae_cntro_oper.[co_estad]-- 5\n" +
                    "      ,[ae_solicitud_listado_equipo_cdgo]-- 6\n" +
                    "			-- Solicitud Listado Equipo\n" +
                    "			,[sle_cdgo]-- 7\n" +
                    "			,[sle_solicitud_equipo_cdgo]-- 8\n" +
                    "				  --Solicitud Equipo\n" +
                    "				  ,[se_cdgo]-- 9\n" +
                    "				  ,[se_cntro_oper_cdgo]-- 10\n" +
                    "						--CentroOperación SolicitudEquipo\n" +
                    "						,se_cntro_oper.[co_cdgo]-- 11\n" +
                    "						,se_cntro_oper.[co_desc]-- 12\n" +
                    "						,se_cntro_oper.[co_estad]-- 13\n" +
                    "				  ,[se_fecha]-- 14\n" +
                    "				  ,[se_usuario_realiza_cdgo]-- 15\n" +
                    "						--Usuario SolicitudEquipo\n" +
                    "						,se_usuario_realiza.[us_cdgo]-- 16\n" +
                    "						,se_usuario_realiza.[us_clave]-- 17\n" +
                    "						,se_usuario_realiza.[us_nombres]-- 18\n" +
                    "						,se_usuario_realiza.[us_apellidos]-- 19\n" +
                    "						,se_usuario_realiza.[us_perfil_cdgo]-- 20\n" +
                    "						,se_usuario_realiza.[us_correo]-- 21\n" +
                    "						,se_usuario_realiza.[us_estad]-- 22\n" +
                    "				  ,[se_fecha_registro]-- 23\n" +
                    "				  ,[se_estado_solicitud_equipo_cdgo]-- 24\n" +
                    "						--Estado de la solicitud\n" +
                    "						,[ese_cdgo]-- 25\n" +
                    "						,[ese_desc]-- 26\n" +
                    "						,[ese_estad]-- 27\n" +
                    "				  ,[se_fecha_confirmacion]-- 28\n" +
                    "				  ,[se_usuario_confirma_cdgo]-- 29\n" +
                    "						--Usuario SolicitudEquipo\n" +
                    "						,se_usuario_confirma.[us_cdgo]--30 \n" +
                    "						,se_usuario_confirma.[us_clave]-- 31\n" +
                    "						,se_usuario_confirma.[us_nombres]-- 32\n" +
                    "						,se_usuario_confirma.[us_apellidos]-- 33\n" +
                    "						,se_usuario_confirma.[us_perfil_cdgo]-- 34\n" +
                    "						,se_usuario_confirma.[us_correo]-- 35\n" +
                    "						,se_usuario_confirma.[us_estad]-- 36\n" +
                    "				  ,[se_confirmacion_solicitud_equipo_cdgo]-- 37\n" +
                    "						--Confirmacion solicitudEquipo\n" +
                    "						,[cse_cdgo]-- 38\n" +
                    "						,[cse_desc]-- 39\n" +
                    "						,[cse_estad]-- 40\n" +
                    "			,[sle_tipo_equipo_cdgo]-- 41\n" +
                    "				--Tipo de Equipo\n" +
                    "				,sle_tipoEquipo.[te_cdgo]-- 42\n" +
                    "				,sle_tipoEquipo.[te_desc]-- 43\n" +
                    "				,sle_tipoEquipo.[te_estad]-- 44\n" +
                    "			,[sle_marca_equipo]-- 45\n" +
                    "			,[sle_modelo_equipo]-- 46\n" +
                    "			,[sle_cant_equip]-- 47\n" +
                    "			,[sle_observ]-- 48\n" +
                    "			,[sle_fecha_hora_inicio]-- 49\n" +
                    "			,[sle_fecha_hora_fin]-- 50\n" +
                    "			,[sle_cant_minutos]-- 51\n" +
                    "			,[sle_labor_realizada_cdgo]-- 52\n" +
                    "			-- Labor Realizada\n" +
                    "				  ,[lr_cdgo]-- 53\n" +
                    "				  ,[lr_desc]-- 54\n" +
                    "				  ,[lr_estad]-- 55\n" +
                    "			,[sle_motonave_cdgo]-- 56\n" +
                    "			--Motonave\n" +
                    "				  ,[mn_cdgo]-- 57\n" +
                    "				  ,[mn_desc]-- 58\n" +
                    "				  ,[mn_estad]-- 59\n" +
                    "			,[sle_cntro_cost_auxiliar_cdgo]-- 60\n" +
                    "				--Centro Costo Auxiliar\n" +
                    "				,[cca_cdgo]-- 61\n" +
                    "				,[cca_cntro_cost_subcentro_cdgo]-- 62\n" +
                    "					-- SubCentro de Costo\n" +
                    "					,ae_cntro_cost_subcentro.[ccs_cdgo]-- 63\n" +
                    "					,ae_cntro_cost_subcentro.[ccs_desc]-- 64\n" +
                    "					,ae_cntro_cost_subcentro.[ccs_estad]-- 65\n" +
                    "				,[cca_desc]-- 66\n" +
                    "				,[cca_estad]-- 67\n" +
                    "			,[sle_compania_cdgo]-- 68\n" +
                    "				--Compañia\n" +
                    "				,[cp_cdgo]-- 69\n" +
                    "				,[cp_desc]-- 70\n" +
                    "				,[cp_estad]-- 71\n" +
                    "      ,[ae_fecha_registro]-- 72\n" +
                    "      ,[ae_fecha_hora_inicio]-- 73\n" +
                    "      ,[ae_fecha_hora_fin]-- 74\n" +
                    "      ,[ae_cant_minutos]-- 75\n" +
                    "      ,[ae_equipo_cdgo]-- 76\n" +
                    "			--Equipo\n" +
                    "			,[eq_cdgo]-- 77\n" +
                    "			,[eq_tipo_equipo_cdgo]-- 78\n" +
                    "				--Tipo Equipo\n" +
                    "				,eq_tipo_equipo.[te_cdgo]-- 79\n" +
                    "				,eq_tipo_equipo.[te_desc]-- 80\n" +
                    "				,eq_tipo_equipo.[te_estad]-- 81\n" +
                    "			,[eq_codigo_barra]-- 82\n" +
                    "			,[eq_referencia]-- 83\n" +
                    "			,[eq_producto]-- 84\n" +
                    "			,[eq_capacidad]-- 85\n" +
                    "			,[eq_marca]-- 86\n" +
                    "			,[eq_modelo]-- 87\n" +
                    "			,[eq_serial]-- 88\n" +
                    "			,[eq_desc]-- 89\n" +
                    "			,[eq_clasificador1_cdgo]-- 90\n" +
                    "				-- Clasificador 1\n" +
                    "				,eq_clasificador1.[ce_cdgo]-- 91\n" +
                    "			    ,eq_clasificador1.[ce_desc]-- 92\n" +
                    "			    ,eq_clasificador1.[ce_estad]-- 93\n" +
                    "			,[eq_clasificador2_cdgo]-- 94\n" +
                    "			    -- Clasificador 2\n" +
                    "				,eq_clasificador2.[ce_cdgo]-- 95\n" +
                    "			    ,eq_clasificador2.[ce_desc]-- 96\n" +
                    "			    ,eq_clasificador2.[ce_estad]-- 97\n" +
                    "			,[eq_proveedor_equipo_cdgo]-- 98\n" +
                    "				--Proveedor Equipo\n" +
                    "				,[pe_cdgo]-- 99\n" +
                    "				,[pe_nit]-- 100\n" +
                    "				,[pe_desc]-- 101\n" +
                    "				,[pe_estad]-- 102\n" +
                    "			,[eq_equipo_pertenencia_cdgo]-- 103\n" +
                    "				-- Equipo Pertenencia\n" +
                    "				,eq_pertenencia.[ep_cdgo]-- 104\n" +
                    "				,eq_pertenencia.[ep_desc]-- 105\n" +
                    "				,eq_pertenencia.[ep_estad]-- 106\n" +
                    "			,[eq_observ]-- 107\n" +
                    "			,[eq_estad]-- 108\n" +
                    "			,[eq_actvo_fijo_id]-- 109\n" +
                    "			,[eq_actvo_fijo_referencia]-- 110\n" +
                    "			,[eq_actvo_fijo_desc]-- 111\n" +
                    "      ,[ae_equipo_pertenencia_cdgo]-- 112\n" +
                    "		-- Equipo Pertenencia\n" +
                    "				,ae_pertenencia.[ep_cdgo]-- 113\n" +
                    "				,ae_pertenencia.[ep_desc]-- 114\n" +
                    "				,ae_pertenencia.[ep_estad]-- 115\n" +
                    "      ,[ae_cant_minutos_operativo]--116\n" +
                    "      ,[ae_cant_minutos_parada]-- 117\n" +
                    "      ,[ae_cant_minutos_total]-- 118\n" +
                    "      ,[ae_estad]-- 119\n" +
                    "       ,[lr_cntro_cost_subcentro_cdgo]-- 120\n" +
                    "					-- SubCentro de Costo\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_cdgo] as ccs_cdgo -- 121\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_desc] as ccs_desc -- 122\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_estad] as ccs_estad -- 123\n" +
                    "      ,[lr_operativa]-- 124\n" +
                    "      ,[lr_parada]-- 125\n"+
                    "  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "		LEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "		LEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "		LEFT  JOIN["+DB+"].[dbo].[motonave] ON [sle_motonave_cdgo]=[mn_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ae_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]=ae_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]\n" +
                    "		LEFT JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] lr_cntro_cost_subcentro ON [lr_cntro_cost_subcentro_cdgo]=lr_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "		WHERE --[se_cdgo]=? ORDER BY   [ae_cdgo] ASC\n" +
                    "       [ae_equipo_cdgo]=?  AND " +
                    "       (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin]");
            query.setString(1, equipoInput.getCodigo());
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    asignacionEquipo = new AsignacionEquipo();
                    valor=false;
                }
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(7));
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(42));
                tipoEquipo.setDescripcion(resultSet.getString(43));
                tipoEquipo.setEstado(resultSet.getString(44));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(45));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(46));
                solicitudListadoEquipo.setCantidad(resultSet.getInt(47));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(48));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(49));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(50));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(51));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(53));
                laborRealizada.setDescripcion(resultSet.getString(54));
                laborRealizada.setEstado(resultSet.getString(55));
                laborRealizada.setCentroCostoSubCentro(new CentroCostoSubCentro(resultSet.getInt(121),
                        resultSet.getString(122),resultSet.getString(123)));
                laborRealizada.setEs_operativa(resultSet.getString(124));
                laborRealizada.setEs_parada(resultSet.getString(125));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave= null;
                if(!(resultSet.getString(57)== null)){
                    System.out.println("No fue nulo");
                    motonave= new Motonave();
                    motonave.setCodigo(resultSet.getString(57));
                    motonave.setDescripcion(resultSet.getString(58));
                    motonave.setEstado(resultSet.getString(59));
                }else{
                    System.out.println("fue nulo");
                    motonave= new Motonave();
                    motonave.setCodigo("NULL");
                    motonave.setDescripcion("NULL");
                    motonave.setEstado("NULL");
                }
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                //System.out.println("ccs_>"+resultSet.getString(49));
                //centroCostoSubCentro.setCodigo(3);
                centroCostoSubCentro.setCodigo(resultSet.getInt(63));
                centroCostoSubCentro.setDescripcion(resultSet.getString(64));
                centroCostoSubCentro.setEstado(resultSet.getString(65));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(61));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(66));
                centroCostoAuxiliar.setEstado(resultSet.getString(67));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compañia = new Compañia();
                compañia.setCodigo(resultSet.getString(69));
                compañia.setDescripcion(resultSet.getString(70));
                compañia.setEstado(resultSet.getString(71));
                //compañia.setEstado("2");
                solicitudListadoEquipo.setCompañia(compañia);

                asignacionEquipo.setCodigo(resultSet.getString(1));
                CentroOperacion CentroOperacionAsignacion= new CentroOperacion();
                CentroOperacionAsignacion.setCodigo(Integer.parseInt(resultSet.getString(3)));
                CentroOperacionAsignacion.setDescripcion(resultSet.getString(4));
                CentroOperacionAsignacion.setEstado(resultSet.getString(5));
                asignacionEquipo.setCentroOperacion(CentroOperacionAsignacion);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(72));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(73));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(74));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(75));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(77));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(79),resultSet.getString(80),resultSet.getString(81)));
                equipo.setCodigo_barra(resultSet.getString(82));
                equipo.setReferencia(resultSet.getString(83));
                equipo.setProducto(resultSet.getString(84));
                equipo.setCapacidad(resultSet.getString(85));
                equipo.setMarca(resultSet.getString(86));
                equipo.setModelo(resultSet.getString(87));
                equipo.setSerial(resultSet.getString(88));
                equipo.setDescripcion(resultSet.getString(89));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(91),resultSet.getString(92),resultSet.getString(93)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(95),resultSet.getString(96),resultSet.getString(97)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(99),resultSet.getString(100),resultSet.getString(101),resultSet.getString(102)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(104),resultSet.getString(105),resultSet.getString(106)));
                equipo.setObservacion(resultSet.getString(107));
                equipo.setEstado(resultSet.getString(108));
                equipo.setActivoFijo_codigo(resultSet.getString(109));
                equipo.setActivoFijo_referencia(resultSet.getString(110));
                equipo.setActivoFijo_descripcion(resultSet.getString(111));
                asignacionEquipo.setEquipo(equipo);
                Pertenencia pertenencia = new Pertenencia();
                pertenencia.setCodigo(resultSet.getString(113));
                pertenencia.setDescripcion(resultSet.getString(114));
                pertenencia.setEstado(resultSet.getString(115));
                asignacionEquipo.setPertenencia(pertenencia);
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(116));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(117));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(118));
                asignacionEquipo.setEstado(resultSet.getString(119));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        //System.out.println(""+asignacionEquipo.getEquipo().getCodigo());
        return asignacionEquipo;
    }
    public AsignacionEquipo validarAsignacionEquipo(Equipo ObjetoI){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        AsignacionEquipo asignacionEquipo = null;
        try{
            ResultSet resultSetBuscar;
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT [ae_cdgo]-- 1\n" +
                    "      ,[ae_cntro_oper_cdgo]-- 2\n" +
                    " ,[co_cdgo]-- 3\n" +
                    " ,[co_desc]-- 4\n" +
                    " ,[co_estad]-- 5\n" +
                    "      ,[ae_solicitud_listado_equipo_cdgo]-- 6\n" +
                    "      ,[ae_fecha_registro]-- 7\n" +
                    "      ,[ae_fecha_hora_inicio]-- 8\n" +
                    "      ,[ae_fecha_hora_fin]-- 9\n" +
                    "      ,[ae_cant_minutos]-- 10\n" +
                    "      ,[ae_equipo_cdgo]-- 11\n" +
                    " ,[eq_cdgo]-- 12\n" +
                    " ,[eq_tipo_equipo_cdgo]-- 13\n" +
                    " ,[te_cdgo]-- 14\n" +
                    " ,[te_desc]-- 15\n" +
                    " ,[te_estad]-- 16\n" +
                    " ,[eq_codigo_barra]-- 17\n" +
                    " ,[eq_referencia]-- 18\n" +
                    " ,[eq_producto]-- 19\n" +
                    " ,[eq_capacidad]-- 20\n" +
                    " ,[eq_marca]-- 21\n" +
                    " ,[eq_modelo]-- 22\n" +
                    " ,[eq_serial]-- 23\n" +
                    " ,[eq_desc]-- 24\n" +
                    " ,[eq_clasificador1_cdgo]-- 25\n" +
                    " ,[eq_clasificador2_cdgo]-- 26\n" +
                    " ,[eq_proveedor_equipo_cdgo]-- 27\n" +
                    " ,[eq_equipo_pertenencia_cdgo]-- 28\n" +
                    " ,[eq_observ]-- 29\n" +
                    " ,[eq_estad]-- 30\n" +
                    " ,[eq_actvo_fijo_id]-- 31\n" +
                    " ,[eq_actvo_fijo_referencia]-- 32\n" +
                    " ,[eq_actvo_fijo_desc]-- 33\n" +
                    "      ,[ae_equipo_pertenencia_cdgo]-- 34\n" +
                    "           ,[ep_cdgo]              -- 35\n" +
                    "           ,[ep_desc]              -- 36\n" +
                    "           ,[ep_estad]             -- 37\n" +
                    "      ,[ae_cant_minutos_operativo]-- 38\n" +
                    "      ,[ae_cant_minutos_parada]-- 39\n" +
                    "      ,[ae_cant_minutos_total]-- 40\n" +
                    "      ,[ae_estad]-- 41\n" +
                    "  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON  [ae_cntro_oper_cdgo]=[co_cdgo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[equipo] ON [eq_cdgo]=[ae_equipo_cdgo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[tipo_equipo] ON [eq_tipo_equipo_cdgo]=[te_cdgo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] ON [ae_equipo_pertenencia_cdgo]=[ep_cdgo]\n" +
                    "  WHERE [ae_equipo_cdgo]=669 AND \n" +
                    "        (SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) BETWEEN [ae_fecha_hora_inicio] AND [ae_fecha_hora_fin]  ");
            queryBuscar.setString(1, ObjetoI.getCodigo());
            resultSetBuscar= queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                asignacionEquipo= new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSetBuscar.getString(1));
                    CentroOperacion centroOperacion= new CentroOperacion();
                    centroOperacion.setCodigo(resultSetBuscar.getInt(3));
                    centroOperacion.setDescripcion(resultSetBuscar.getString(4));
                    centroOperacion.setEstado(resultSetBuscar.getString(5));
                asignacionEquipo.setCentroOperacion(centroOperacion);
                    SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                    solicitudListadoEquipo.setCodigo(resultSetBuscar.getString(6));
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSetBuscar.getString(7));
                asignacionEquipo.setFechaHoraInicio(resultSetBuscar.getString(8));
                asignacionEquipo.setFechaHoraFin(resultSetBuscar.getString(9));
                asignacionEquipo.setCantidadMinutosProgramados(resultSetBuscar.getString(10));
                    Equipo equipo = new Equipo();
                    equipo.setCodigo(resultSetBuscar.getString(12));
                        TipoEquipo tipoEquipo= new TipoEquipo();
                        tipoEquipo.setCodigo(resultSetBuscar.getString(14));
                        tipoEquipo.setDescripcion(resultSetBuscar.getString(15));
                        tipoEquipo.setEstado(resultSetBuscar.getString(16));
                    equipo.setCodigo_barra(resultSetBuscar.getString(17));
                    equipo.setReferencia(resultSetBuscar.getString(18));
                    equipo.setProducto(resultSetBuscar.getString(19));
                    equipo.setCapacidad(resultSetBuscar.getString(20));
                    equipo.setMarca(resultSetBuscar.getString(21));
                    equipo.setModelo(resultSetBuscar.getString(22));
                    equipo.setSerial(resultSetBuscar.getString(23));
                    equipo.setDescripcion(resultSetBuscar.getString(24));
                asignacionEquipo.setEquipo(equipo);
                    Pertenencia pertenencia= new Pertenencia();
                    pertenencia.setCodigo(resultSetBuscar.getString(35));
                    pertenencia.setDescripcion(resultSetBuscar.getString(36));
                    pertenencia.setEstado(resultSetBuscar.getString(37));
                asignacionEquipo.setPertenencia(pertenencia);
                asignacionEquipo.setCantidadMinutosOperativo(resultSetBuscar.getString(38));
                asignacionEquipo.setCantidadMinutosParada(resultSetBuscar.getString(39));
                asignacionEquipo.setCantidadMinutosTotal(resultSetBuscar.getString(40));
                asignacionEquipo.setEstado(resultSetBuscar.getString(41));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return asignacionEquipo;
    }
    public AsignacionEquipo buscarAsignacionPorCodigoAsignacion(String CódigoAsignacion) throws SQLException {
        // Equipo Objeto = null;
        AsignacionEquipo asignacionEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT "+
                    "       [ae_cdgo]-- 1\n" +
                    "      ,[ae_cntro_oper_cdgo]-- 2\n" +
                    "			--Centro Operacion\n" +
                    "			,ae_cntro_oper.[co_cdgo]-- 3\n" +
                    "			,ae_cntro_oper.[co_desc]-- 4\n" +
                    "			,ae_cntro_oper.[co_estad]-- 5\n" +
                    "      ,[ae_solicitud_listado_equipo_cdgo]-- 6\n" +
                    "			-- Solicitud Listado Equipo\n" +
                    "			,[sle_cdgo]-- 7\n" +
                    "			,[sle_solicitud_equipo_cdgo]-- 8\n" +
                    "				  --Solicitud Equipo\n" +
                    "				  ,[se_cdgo]-- 9\n" +
                    "				  ,[se_cntro_oper_cdgo]-- 10\n" +
                    "						--CentroOperación SolicitudEquipo\n" +
                    "						,se_cntro_oper.[co_cdgo]-- 11\n" +
                    "						,se_cntro_oper.[co_desc]-- 12\n" +
                    "						,se_cntro_oper.[co_estad]-- 13\n" +
                    "				  ,[se_fecha]-- 14\n" +
                    "				  ,[se_usuario_realiza_cdgo]-- 15\n" +
                    "						--Usuario SolicitudEquipo\n" +
                    "						,se_usuario_realiza.[us_cdgo]-- 16\n" +
                    "						,se_usuario_realiza.[us_clave]-- 17\n" +
                    "						,se_usuario_realiza.[us_nombres]-- 18\n" +
                    "						,se_usuario_realiza.[us_apellidos]-- 19\n" +
                    "						,se_usuario_realiza.[us_perfil_cdgo]-- 20\n" +
                    "						,se_usuario_realiza.[us_correo]-- 21\n" +
                    "						,se_usuario_realiza.[us_estad]-- 22\n" +
                    "				  ,[se_fecha_registro]-- 23\n" +
                    "				  ,[se_estado_solicitud_equipo_cdgo]-- 24\n" +
                    "						--Estado de la solicitud\n" +
                    "						,[ese_cdgo]-- 25\n" +
                    "						,[ese_desc]-- 26\n" +
                    "						,[ese_estad]-- 27\n" +
                    "				  ,[se_fecha_confirmacion]-- 28\n" +
                    "				  ,[se_usuario_confirma_cdgo]-- 29\n" +
                    "						--Usuario SolicitudEquipo\n" +
                    "						,se_usuario_confirma.[us_cdgo]--30 \n" +
                    "						,se_usuario_confirma.[us_clave]-- 31\n" +
                    "						,se_usuario_confirma.[us_nombres]-- 32\n" +
                    "						,se_usuario_confirma.[us_apellidos]-- 33\n" +
                    "						,se_usuario_confirma.[us_perfil_cdgo]-- 34\n" +
                    "						,se_usuario_confirma.[us_correo]-- 35\n" +
                    "						,se_usuario_confirma.[us_estad]-- 36\n" +
                    "				  ,[se_confirmacion_solicitud_equipo_cdgo]-- 37\n" +
                    "						--Confirmacion solicitudEquipo\n" +
                    "						,[cse_cdgo]-- 38\n" +
                    "						,[cse_desc]-- 39\n" +
                    "						,[cse_estad]-- 40\n" +
                    "			,[sle_tipo_equipo_cdgo]-- 41\n" +
                    "				--Tipo de Equipo\n" +
                    "				,sle_tipoEquipo.[te_cdgo]-- 42\n" +
                    "				,sle_tipoEquipo.[te_desc]-- 43\n" +
                    "				,sle_tipoEquipo.[te_estad]-- 44\n" +
                    "			,[sle_marca_equipo]-- 45\n" +
                    "			,[sle_modelo_equipo]-- 46\n" +
                    "			,[sle_cant_equip]-- 47\n" +
                    "			,[sle_observ]-- 48\n" +
                    "			,[sle_fecha_hora_inicio]-- 49\n" +
                    "			,[sle_fecha_hora_fin]-- 50\n" +
                    "			,[sle_cant_minutos]-- 51\n" +
                    "			,[sle_labor_realizada_cdgo]-- 52\n" +
                    "			-- Labor Realizada\n" +
                    "				  ,[lr_cdgo]-- 53\n" +
                    "				  ,[lr_desc]-- 54\n" +
                    "				  ,[lr_estad]-- 55\n" +
                    "			,[sle_motonave_cdgo]-- 56\n" +
                    "			--Motonave\n" +
                    "				  ,[mn_cdgo]-- 57\n" +
                    "				  ,[mn_desc]-- 58\n" +
                    "				  ,[mn_estad]-- 59\n" +
                    "			,[sle_cntro_cost_auxiliar_cdgo]-- 60\n" +
                    "				--Centro Costo Auxiliar\n" +
                    "				,[cca_cdgo]-- 61\n" +
                    "				,[cca_cntro_cost_subcentro_cdgo]-- 62\n" +
                    "					-- SubCentro de Costo\n" +
                    "					,[ccs_cdgo]-- 63\n" +
                    "					,[ccs_desc]-- 64\n" +
                    "					,[ccs_estad]-- 65\n" +
                    "				,[cca_desc]-- 66\n" +
                    "				,[cca_estad]-- 67\n" +
                    "			,[sle_compania_cdgo]-- 68\n" +
                    "				--Compañia\n" +
                    "				,[cp_cdgo]-- 69\n" +
                    "				,[cp_desc]-- 70\n" +
                    "				,[cp_estad]-- 71\n" +
                    "      ,[ae_fecha_registro]-- 72\n" +
                    "      ,[ae_fecha_hora_inicio]-- 73\n" +
                    "      ,[ae_fecha_hora_fin]-- 74\n" +
                    "      ,[ae_cant_minutos]-- 75\n" +
                    "      ,[ae_equipo_cdgo]-- 76\n" +
                    "			--Equipo\n" +
                    "			,[eq_cdgo]-- 77\n" +
                    "			,[eq_tipo_equipo_cdgo]-- 78\n" +
                    "				--Tipo Equipo\n" +
                    "				,eq_tipo_equipo.[te_cdgo]-- 79\n" +
                    "				,eq_tipo_equipo.[te_desc]-- 80\n" +
                    "				,eq_tipo_equipo.[te_estad]-- 81\n" +
                    "			,[eq_codigo_barra]-- 82\n" +
                    "			,[eq_referencia]-- 83\n" +
                    "			,[eq_producto]-- 84\n" +
                    "			,[eq_capacidad]-- 85\n" +
                    "			,[eq_marca]-- 86\n" +
                    "			,[eq_modelo]-- 87\n" +
                    "			,[eq_serial]-- 88\n" +
                    "			,[eq_desc]-- 89\n" +
                    "			,[eq_clasificador1_cdgo]-- 90\n" +
                    "				-- Clasificador 1\n" +
                    "				,eq_clasificador1.[ce_cdgo]-- 91\n" +
                    "			    ,eq_clasificador1.[ce_desc]-- 92\n" +
                    "			    ,eq_clasificador1.[ce_estad]-- 93\n" +
                    "			,[eq_clasificador2_cdgo]-- 94\n" +
                    "			    -- Clasificador 2\n" +
                    "				,eq_clasificador2.[ce_cdgo]-- 95\n" +
                    "			    ,eq_clasificador2.[ce_desc]-- 96\n" +
                    "			    ,eq_clasificador2.[ce_estad]-- 97\n" +
                    "			,[eq_proveedor_equipo_cdgo]-- 98\n" +
                    "				--Proveedor Equipo\n" +
                    "				,[pe_cdgo]-- 99\n" +
                    "				,[pe_nit]-- 100\n" +
                    "				,[pe_desc]-- 101\n" +
                    "				,[pe_estad]-- 102\n" +
                    "			,[eq_equipo_pertenencia_cdgo]-- 103\n" +
                    "				-- Equipo Pertenencia\n" +
                    "				,eq_pertenencia.[ep_cdgo]-- 104\n" +
                    "				,eq_pertenencia.[ep_desc]-- 105\n" +
                    "				,eq_pertenencia.[ep_estad]-- 106\n" +
                    "			,[eq_observ]-- 107\n" +
                    "			,[eq_estad]-- 108\n" +
                    "			,[eq_actvo_fijo_id]-- 109\n" +
                    "			,[eq_actvo_fijo_referencia]-- 110\n" +
                    "			,[eq_actvo_fijo_desc]-- 111\n" +
                    "      ,[ae_equipo_pertenencia_cdgo]-- 112\n" +
                    "		-- Equipo Pertenencia\n" +
                    "				,ae_pertenencia.[ep_cdgo]-- 113\n" +
                    "				,ae_pertenencia.[ep_desc]-- 114\n" +
                    "				,ae_pertenencia.[ep_estad]-- 115\n" +
                    "      ,[ae_cant_minutos_operativo]--116\n" +
                    "      ,[ae_cant_minutos_parada]-- 117\n" +
                    "      ,[ae_cant_minutos_total]-- 118\n" +
                    "      ,[ae_estad]-- 119\n" +
                    "       ,[lr_cntro_cost_subcentro_cdgo]-- 120\n" +
                    "					-- SubCentro de Costo\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_cdgo] as ccs_cdgo -- 121\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_desc] as ccs_desc -- 122\n" +
                    "					,lr_cntro_cost_subcentro.[ccs_estad] as ccs_estad -- 123\n" +
                    "      ,[lr_operativa]-- 124\n" +
                    "      ,[lr_parada]-- 125\n"+
                    "  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "		LEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "		LEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "		LEFT  JOIN["+DB+"].[dbo].[motonave] ON [sle_motonave_cdgo]=[mn_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]\n" +
                    "		LEFT JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] lr_cntro_cost_subcentro ON [lr_cntro_cost_subcentro_cdgo]=lr_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "		WHERE --[se_cdgo]=? ORDER BY   [ae_cdgo] ASC\n" +
                    "        [ae_cdgo]=?");
            query.setString(1, CódigoAsignacion);
            resultSet= query.executeQuery();
            boolean valor=true;
            while(resultSet.next()){
                if(valor) {
                    asignacionEquipo = new AsignacionEquipo();
                    valor=false;
                }
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(7));
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(42));
                tipoEquipo.setDescripcion(resultSet.getString(43));
                tipoEquipo.setEstado(resultSet.getString(44));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(45));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(46));
                solicitudListadoEquipo.setCantidad(resultSet.getInt(47));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(48));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(49));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(50));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(51));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(53));
                laborRealizada.setDescripcion(resultSet.getString(54));
                laborRealizada.setEstado(resultSet.getString(55));
                laborRealizada.setCentroCostoSubCentro(new CentroCostoSubCentro(resultSet.getInt(121),
                        resultSet.getString(122),resultSet.getString(123)));
                laborRealizada.setEs_operativa(resultSet.getString(124));
                laborRealizada.setEs_parada(resultSet.getString(125));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave= null;
                if(!(resultSet.getString(57).equals("NULL"))){
                    System.out.println("No fue nulo");
                    motonave= new Motonave();
                    motonave.setCodigo(resultSet.getString(57));
                    motonave.setDescripcion(resultSet.getString(58));
                    motonave.setEstado(resultSet.getString(59));
                }else{
                    System.out.println("fue nulo");
                    motonave= new Motonave();
                    motonave.setCodigo("NULL");
                    motonave.setDescripcion("NULL");
                    motonave.setEstado("NULL");
                }
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                //System.out.println("ccs_>"+resultSet.getString(49));
                //centroCostoSubCentro.setCodigo(3);
                centroCostoSubCentro.setCodigo(resultSet.getInt(63));
                centroCostoSubCentro.setDescripcion(resultSet.getString(64));
                centroCostoSubCentro.setEstado(resultSet.getString(65));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(61));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(66));
                centroCostoAuxiliar.setEstado(resultSet.getString(67));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compañia = new Compañia();
                compañia.setCodigo(resultSet.getString(69));
                compañia.setDescripcion(resultSet.getString(70));
                compañia.setEstado(resultSet.getString(71));
                //compañia.setEstado("2");
                solicitudListadoEquipo.setCompañia(compañia);

                asignacionEquipo.setCodigo(resultSet.getString(1));
                CentroOperacion CentroOperacionAsignacion= new CentroOperacion();
                CentroOperacionAsignacion.setCodigo(Integer.parseInt(resultSet.getString(3)));
                CentroOperacionAsignacion.setDescripcion(resultSet.getString(4));
                CentroOperacionAsignacion.setEstado(resultSet.getString(5));
                asignacionEquipo.setCentroOperacion(CentroOperacionAsignacion);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(72));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(73));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(74));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(75));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(77));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(79),resultSet.getString(80),resultSet.getString(81)));
                equipo.setCodigo_barra(resultSet.getString(82));
                equipo.setReferencia(resultSet.getString(83));
                equipo.setProducto(resultSet.getString(84));
                equipo.setCapacidad(resultSet.getString(85));
                equipo.setMarca(resultSet.getString(86));
                equipo.setModelo(resultSet.getString(87));
                equipo.setSerial(resultSet.getString(88));
                equipo.setDescripcion(resultSet.getString(89));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(91),resultSet.getString(92),resultSet.getString(93)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(95),resultSet.getString(96),resultSet.getString(97)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(99),resultSet.getString(100),resultSet.getString(101),resultSet.getString(102)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(104),resultSet.getString(105),resultSet.getString(106)));
                equipo.setObservacion(resultSet.getString(107));
                equipo.setEstado(resultSet.getString(108));
                equipo.setActivoFijo_codigo(resultSet.getString(109));
                equipo.setActivoFijo_referencia(resultSet.getString(110));
                equipo.setActivoFijo_descripcion(resultSet.getString(111));
                asignacionEquipo.setEquipo(equipo);
                Pertenencia pertenencia = new Pertenencia();
                pertenencia.setCodigo(resultSet.getString(113));
                pertenencia.setDescripcion(resultSet.getString(114));
                pertenencia.setEstado(resultSet.getString(115));
                asignacionEquipo.setPertenencia(pertenencia);
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(116));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(117));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(118));
                asignacionEquipo.setEstado(resultSet.getString(119));
            }
        }catch (SQLException sqlException) {

        }
        control.cerrarConexionBaseDatos();
        return asignacionEquipo;
    }
*/
}
