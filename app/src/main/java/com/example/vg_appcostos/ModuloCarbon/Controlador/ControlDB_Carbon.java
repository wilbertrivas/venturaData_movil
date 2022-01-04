package com.example.vg_appcostos.ModuloCarbon.Controlador;
import android.view.View;

import com.example.vg_appcostos.ConexionesDB.Ccarga_GP;
import com.example.vg_appcostos.ConexionesDB.Ccarga_OPP;
import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Articulo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.BaseDatos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Cliente;
import com.example.vg_appcostos.ModuloCarbon.Modelo.EstadoMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MotivoNoLavado;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon_ListadoEquipos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.AutorizacionRecobro;
import com.example.vg_appcostos.ModuloEquipo.Model.CausaInactividad;
import com.example.vg_appcostos.ModuloEquipo.Model.ClasificadorEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.ClienteRecobro;
import com.example.vg_appcostos.ModuloEquipo.Model.Compañia;
import com.example.vg_appcostos.ModuloEquipo.Model.ConfirmacionSolicitudEquipos;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.EstadoSolicitudEquipos;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Pertenencia;
import com.example.vg_appcostos.ModuloEquipo.Model.ProveedorEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Recobro;
import com.example.vg_appcostos.ModuloEquipo.Model.SolicitudEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.SolicitudListadoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Motonave;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Transportadora;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import com.example.vg_appcostos.Sistemas.Modelo.Perfil;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Config;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ControlDB_Carbon {
    private Connection conexion = null;
    private String tipoConexion;

    public ControlDB_Carbon(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }
    //Optimizados
    public ArrayList<MvtoCarbon> buscarMtvoCarbon_Optimzado(String placa)  {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT \n" +
                    "     [mc_placa_vehiculo]--1 \n" +
                    "    ,[mc_peso_vacio]--2 \n" +
                    "    ,[mc_fecha_entrad]--3 \n" +
                    "    ,[mc_fecha_inicio_descargue]--4                  \n" +
                    "FROM  ["+DB+"].[dbo].[mvto_carbon]  \n" +
                    "WHERE [mc_fecha_fin_descargue] IS NULL  AND\n" +
                    "      [mc_estad_mvto_carbon_cdgo]=1 \n" +
                    "\t  AND [mc_placa_vehiculo] LIKE ? \n" +
                    "ORDER BY mc_placa_vehiculo ASC");

            String d= ""+placa+"%";
            queryBuscar.setString(1, d);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setPlaca(resultSetBuscar.getString(1));
                Objeto.setPesoVacio(resultSetBuscar.getString(2));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(3));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(4));
                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }
    public MvtoCarbon buscarMtvo_CarbonParticular(String placa) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        MvtoCarbon Objeto= null;
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1 \n" +
                    "\t\t,[co_cdgo]--2 \n" +
                    "\t\t,[co_desc]--3 \n" +
                    "\t\t,cca_origen.[cca_cdgo]--4 \n" +
                    "\t\t,cca_origen.[cca_cntro_cost_subcentro_cdgo]--5 \n" +
                    "\t\t,[ccs_cdgo]--6\n" +
                    "\t\t,[ccs_desc]--7 \n" +
                    "\t\t,cca_origen.[cca_desc]--8 \n" +
                    "\t\t,[ar_cdgo]--9 \n" +
                    "\t\t,[ar_desc]--10 \n" +
                    "\t\t,[cl_cdgo]--11 \n" +
                    "\t\t,[cl_desc]--12 \n" +
                    "\t\t,[tr_desc]--13 \n" +
                    "\t\t,[mc_num_orden]--14 \n" +
                    "\t\t,[mc_deposito]--15 \n" +
                    "\t\t,[mc_placa_vehiculo]--16 \n" +
                    "\t\t,[mc_peso_vacio]--17 \n" +
                    "\t\t,[mc_peso_lleno]--18 \n" +
                    "\t\t,[mc_peso_neto]--19\n" +
                    "\t\t,[mc_fecha_entrad]--20 \n" +
                    "\t\t,[mc_fecha_salid]--21 \n" +
                    "\t\t,[mc_fecha_inicio_descargue]--22 \n" +
                    "\t\t,[mc_fecha_fin_descargue]--23      \n" +
                    "        ,us_registro_app.[us_nombres] AS us_registro_app_nombres--24 \n" +
                    "        ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--25 \n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--26 \n" +
                    "        ,cca_destino.[cca_desc]--27 \n" +
                    "        ,[lr_cdgo]--28 \n" +
                    "        ,[lr_desc]--29 \n" +
                    "        ,[mc_cliente_base_datos_cdgo]--30 \n" +
                    "        ,[mc_transprtdora_base_datos_cdgo]--31 \n" +
                    "        ,[mc_articulo_base_datos_cdgo]--32 \n" +
                    "   FROM ["+DB+"].[dbo].[mvto_carbon]  \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_origen ON [mc_cntro_cost_auxiliar_cdgo]=cca_origen.[cca_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON cca_origen.[cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo] AND [cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]             \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] " +
                    "    WHERE [mc_fecha_fin_descargue] IS NULL  \n" +
                    "    AND [mc_estad_mvto_carbon_cdgo]=1 AND [mc_placa_vehiculo] LIKE ?\n" +
                    "\t\tORDER BY mc_placa_vehiculo ASC");

            String d= ""+placa+"%";
            queryBuscar.setString(1, d);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                Objeto=new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(2));
                centroOperacion.setDescripcion(resultSetBuscar.getString(3));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(6));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(7));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(4));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(8));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(9),resultSetBuscar.getString(10),""));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(11),resultSetBuscar.getString(12),""));
                Objeto.setTransportadora(new Transportadora("","",
                        resultSetBuscar.getString(13),"",
                        ""));
                Objeto.getCliente().setBaseDatos(new BaseDatos(resultSetBuscar.getString(30)));
                Objeto.getTransportadora().setBaseDatos(new BaseDatos(resultSetBuscar.getString(31)));
                Objeto.getArticulo().setBaseDatos(new BaseDatos(resultSetBuscar.getString(32)));
                Objeto.setNumero_orden(resultSetBuscar.getString(14));
                Objeto.setDeposito(resultSetBuscar.getString(15));
                Objeto.setPlaca(resultSetBuscar.getString(16));
                Objeto.setPesoVacio(resultSetBuscar.getString(17));
                Objeto.setPesoLleno(resultSetBuscar.getString(18));
                Objeto.setPesoNeto(resultSetBuscar.getString(19));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(20));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(21));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(22));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(23));
                Usuario usuario = new Usuario();
                usuario.setNombres(resultSetBuscar.getString(24));
                usuario.setApellidos(resultSetBuscar.getString(25));
                Objeto.setUsuarioRegistroMovil(usuario);

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSetBuscar.getString(26));
                centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(27));
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSetBuscar.getString(28));
                laborRealizada.setDescripcion(resultSetBuscar.getString(29));
                Objeto.setLaborRealizada(laborRealizada);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public MvtoCarbon buscarMtvo_CarbonParticular(String placa,String pesoVacio,String fechaEntrada,String fechaInicioDescargue) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        MvtoCarbon Objeto= null;
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1 \n" +
                    "\t\t,[co_cdgo]--2 \n" +
                    "\t\t,[co_desc]--3 \n" +
                    "\t\t,cca_origen.[cca_cdgo]--4 \n" +
                    "\t\t,cca_origen.[cca_cntro_cost_subcentro_cdgo]--5 \n" +
                    "\t\t,[ccs_cdgo]--6\n" +
                    "\t\t,[ccs_desc]--7 \n" +
                    "\t\t,cca_origen.[cca_desc]--8 \n" +
                    "\t\t,[ar_cdgo]--9 \n" +
                    "\t\t,[ar_desc]--10 \n" +
                    "\t\t,[cl_cdgo]--11 \n" +
                    "\t\t,[cl_desc]--12 \n" +
                    "\t\t,[tr_desc]--13 \n" +
                    "\t\t,[mc_num_orden]--14 \n" +
                    "\t\t,[mc_deposito]--15 \n" +
                    "\t\t,[mc_placa_vehiculo]--16 \n" +
                    "\t\t,[mc_peso_vacio]--17 \n" +
                    "\t\t,[mc_peso_lleno]--18 \n" +
                    "\t\t,[mc_peso_neto]--19\n" +
                    "\t\t,[mc_fecha_entrad]--20 \n" +
                    "\t\t,[mc_fecha_salid]--21 \n" +
                    "\t\t,[mc_fecha_inicio_descargue]--22 \n" +
                    "\t\t,[mc_fecha_fin_descargue]--23      \n" +
                    "        ,us_registro_app.[us_nombres] AS us_registro_app_nombres--24 \n" +
                    "        ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--25 \n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--26 \n" +
                    "        ,cca_destino.[cca_desc]--27 \n" +
                    "        ,[lr_cdgo]--28 \n" +
                    "        ,[lr_desc]--29 \n" +
                "        ,[mc_cliente_base_datos_cdgo]--30 \n" +
                "        ,[mc_transprtdora_base_datos_cdgo]--31 \n" +
                "        ,[mc_articulo_base_datos_cdgo]--32 \n" +
                    "FROM ["+DB+"].[dbo].[mvto_carbon]  \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_origen ON [mc_cntro_cost_auxiliar_cdgo]=cca_origen.[cca_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON cca_origen.[cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo] AND [cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]             \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] " +
                    "    WHERE [mc_fecha_fin_descargue] IS NULL  \n" +
                    "    AND [mc_estad_mvto_carbon_cdgo]=1 AND [mc_placa_vehiculo] LIKE ?  AND [mc_peso_vacio]=? AND [mc_fecha_entrad]=? AND [mc_fecha_inicio_descargue]=?\n" +
                    "\t\tORDER BY mc_placa_vehiculo ASC");

            queryBuscar.setString(1, placa);
            queryBuscar.setString(2, pesoVacio);
            queryBuscar.setString(3, fechaEntrada);
            queryBuscar.setString(4, fechaInicioDescargue);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(2));
                centroOperacion.setDescripcion(resultSetBuscar.getString(3));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(6));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(7));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(4));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(8));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(9),resultSetBuscar.getString(10),""));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(11),resultSetBuscar.getString(12),""));
                Objeto.setTransportadora(new Transportadora("","",
                        resultSetBuscar.getString(13),"",
                        ""));
                Objeto.getCliente().setBaseDatos(new BaseDatos(resultSetBuscar.getString(30)));
                Objeto.getTransportadora().setBaseDatos(new BaseDatos(resultSetBuscar.getString(31)));
                Objeto.getArticulo().setBaseDatos(new BaseDatos(resultSetBuscar.getString(32)));


                Objeto.setClienteBaseDatos(resultSetBuscar.getString(30));
                Objeto.setTransportadorBaseDatos(resultSetBuscar.getString(31));
                Objeto.setArticuloBaseDatos(resultSetBuscar.getString(32));
                Objeto.setNumero_orden(resultSetBuscar.getString(14));
                Objeto.setDeposito(resultSetBuscar.getString(15));
                Objeto.setPlaca(resultSetBuscar.getString(16));
                Objeto.setPesoVacio(resultSetBuscar.getString(17));
                Objeto.setPesoLleno(resultSetBuscar.getString(18));
                Objeto.setPesoNeto(resultSetBuscar.getString(19));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(20));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(21));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(22));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(23));
                Usuario usuario = new Usuario();
                usuario.setNombres(resultSetBuscar.getString(24));
                usuario.setApellidos(resultSetBuscar.getString(25));
                Objeto.setUsuarioRegistroMovil(usuario);

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSetBuscar.getString(26));
                centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(27));
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSetBuscar.getString(28));
                laborRealizada.setDescripcion(resultSetBuscar.getString(29));
                Objeto.setLaborRealizada(laborRealizada);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<MvtoCarbon_ListadoEquipos> buscarEquipo_EnMovimientoCarbon_Activos(Equipo equipoI,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<MvtoCarbon_ListadoEquipos> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT TOP 100 \n" +
                    "        [co_desc] --1 \n" +
                    "        ,mc_cntro_cost_auxiliar.[cca_cdgo] --2 \n" +
                    "                ,mc_cntro_cost_subcentro.[ccs_cdgo] --3 \n" +
                    "                ,mc_cntro_cost_subcentro.[ccs_desc]  --4\n" +
                    "        ,mc_cntro_cost_auxiliar.[cca_desc] --5\n" +
                    "        ,mc_articulo.[ar_desc] --6\n" +
                    "        ,mc_cliente.[cl_desc] --7\n" +
                    "        ,[tr_desc] --8\n" +
                    "    ,[mc_deposito] --9\n" +
                    "    ,[mc_placa_vehiculo] --10\n" +
                    "    ,[mc_peso_vacio] --11\n" +
                    "    ,[mc_fecha_entrad] --12 \n" +
                    "    ,[mc_fecha_inicio_descargue] --13 \n" +
                    "            ,ae_eq_cdgo --14\n" +
                    "                ,ae_eq_te_desc --15 \n" +
                    "            ,ae_eq_marca --16\n" +
                    "            ,ae_eq_modelo --17 \n" +
                    "            ,ae_eq_desc --18 \n" +
                    "                ,ae_eq_pe_desc --19 \n" +
                    "            ,lr_me.[lr_desc] --20 \n" +
                    "            ,me_us_registro.[us_nombres] --21 \n" +
                    "            ,me_us_registro.[us_apellidos] --22 \n" +
                    "            ,[me_cdgo] --23 \n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--24 \n" +
                    "        ,cca_destino.[cca_desc]--25 \n" +
                    "        ,lr_mc.[lr_cdgo]--26 \n" +
                    "        ,lr_mc.[lr_desc]--27 \n" +
                    "        ,[mc_cliente_base_datos_cdgo]--28 \n" +
                    "        ,[mc_transprtdora_base_datos_cdgo]--29 \n" +
                    "        ,[mc_articulo_base_datos_cdgo]--30 \n" +
                    "        FROM ["+DB+"].[dbo].[mvto_carbon] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliar ON [mc_cntro_cost_auxiliar_cdgo]=mc_cntro_cost_auxiliar.[cca_cdgo] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] mc_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]= mc_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[articulo] mc_articulo ON [mc_articulo_cdgo]=mc_articulo.[ar_cdgo] AND mc_articulo.[ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cliente] mc_cliente ON [mc_cliente_cdgo]=mc_cliente.[cl_cdgo] AND mc_cliente.[cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[labor_realizada] lr_mc ON [mc_labor_realizada_cdgo]=lr_mc.[lr_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] " +
                    "        INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [mc_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "        INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[mvto_carbon_listado_equipo] ON [mcle_mvto_carbon_cdgo]=[mc_cdgo] \n" +
                    "        INNER JOIN (SELECT [ae_cdgo] AS ae_cdgo \n" +
                    "                    \t\t\t--Equipo \n" +
                    "                    \t\t\t,[eq_cdgo] AS ae_eq_cdgo \n" +
                    "                    \t\t\t\t,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc \n" +
                    "                    \t\t\t,[eq_marca] AS ae_eq_marca \n" +
                    "                    \t\t\t,[eq_modelo] AS ae_eq_modelo \n" +
                    "                    \t\t\t,[eq_desc] AS ae_eq_desc \n" +
                    "                    \t\t\t\t,[pe_desc] AS ae_eq_pe_desc \n" +
                    "                    \t\tFROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "                    \t\t\tINNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "                    \t\t\tINNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "                    \t\t\tINNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "        ) asignacion_equipo ON [mcle_asignacion_equipo_cdgo]=[ae_cdgo] \n" +
                    "        --Movimiento Equipo \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[mvto_equipo] ON [mcle_mvto_equipo_cdgo]=[me_cdgo] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[labor_realizada] lr_me ON [me_labor_realizada_cdgo]=lr_me.[lr_cdgo]  \n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo] AND me_cliente.[cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "        LEFT JOIN  ["+DB+"].[dbo].[articulo] me_articulo ON [me_articulo_cdgo]=me_articulo.[ar_cdgo] AND me_articulo.[ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "        LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo] \n" +
                    "        WHERE \n" +
                    "\t\t\tae_eq_cdgo =?  \n" +
                    "\t\t\tAND  \n" +
                    "\t\t\t[me_inactividad]=0 AND  [mc_estad_mvto_carbon_cdgo]=1 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" AND [me_fecha_hora_fin] IS NULL ORDER BY [me_cdgo] DESC");
            query.setString(1, equipoI.getCodigo());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                MvtoCarbon_ListadoEquipos mvto_listEquipo = new MvtoCarbon_ListadoEquipos();
                MvtoCarbon mvtoCarbon = new MvtoCarbon();
                mvtoCarbon.setCentroOperacion(new CentroOperacion(1,resultSet.getString(1),""));
                mvtoCarbon.setCentroCostoAuxiliar(new CentroCostoAuxiliar(resultSet.getString(2),new CentroCostoSubCentro(Integer.parseInt(resultSet.getString(3)),resultSet.getString(4),""),resultSet.getString(5),""));
                mvtoCarbon.setArticulo(new Articulo("",resultSet.getString(6),""));
                mvtoCarbon.setCliente(new Cliente("",resultSet.getString(7),""));
                mvtoCarbon.setTransportadora(new Transportadora("","",resultSet.getString(8),"",""));

                mvtoCarbon.getCliente().setBaseDatos(new BaseDatos(resultSet.getString(28)));
                mvtoCarbon.getTransportadora().setBaseDatos(new BaseDatos(resultSet.getString(29)));
                mvtoCarbon.getArticulo().setBaseDatos(new BaseDatos(resultSet.getString(30)));

                mvtoCarbon.setClienteBaseDatos(resultSet.getString(28));
                mvtoCarbon.setTransportadorBaseDatos(resultSet.getString(29));
                mvtoCarbon.setArticuloBaseDatos(resultSet.getString(30));

                mvtoCarbon.setDeposito(resultSet.getString(9));
                mvtoCarbon.setPlaca(resultSet.getString(10));
                mvtoCarbon.setPesoVacio(resultSet.getString(11));
                mvtoCarbon.setFechaEntradaVehiculo(resultSet.getString(12));
                mvtoCarbon.setFechaInicioDescargue(resultSet.getString(13));

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSet.getString(24));
                centroCostoAuxiliarDestino.setDescripcion(resultSet.getString(25));
                mvtoCarbon.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(26));
                laborRealizada.setDescripcion(resultSet.getString(27));
                mvtoCarbon.setLaborRealizada(laborRealizada);

                mvto_listEquipo.setMvtoCarbon(mvtoCarbon);
                    AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                        Equipo equipo = new Equipo();
                        equipo.setCodigo(resultSet.getString(14));
                        equipo.setTipoEquipo(new TipoEquipo("",resultSet.getString(15),""));
                        equipo.setMarca(resultSet.getString(16));
                        equipo.setModelo(resultSet.getString(17));
                        equipo.setDescripcion(resultSet.getString(18));
                        equipo.setProveedorEquipo(new ProveedorEquipo("","",resultSet.getString(19),""));
                    asignacionEquipo.setEquipo(equipo);
                mvto_listEquipo.setAsignacionEquipo(asignacionEquipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(23));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                    LaborRealizada laborRealizadaT = new LaborRealizada();
                    laborRealizadaT.setDescripcion(resultSet.getString(20));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                    Usuario usuario_me_registra = new Usuario();
                    usuario_me_registra.setNombres(resultSet.getString(21));
                    usuario_me_registra.setApellidos(resultSet.getString(22));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                mvto_listEquipo.setMvtoEquipo(mvtoEquipo);
                listadoObjetos.add(mvto_listEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }


    public ArrayList<MvtoCarbon> cargarPlacaTransito(String placa) {
        Ccarga_GP control_gp = new Ccarga_GP(tipoConexion);
        String DB_gp=control_gp.getBaseDeDatos();
        Ccarga_OPP control_opp = new Ccarga_OPP(tipoConexion);
        String DB_opp=control_opp.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoObjeto = new ArrayList();
        conexion = control_gp.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("DECLARE @placa VARCHAR(45);\n" +
                    "SET @placa=?;\n" +
                    "\tSELECT  PRODUCTO_CODIGO=ar_cdgo, --1\n" +
                    "\t\t\tPRODUCTO=ar_nmbre,--2\n" +
                    "\t\t\tCLIENTE_CODIGO=cl_cdgo,--3\n" +
                    "\t\t\tCLIENTE=cl_nmbre, --4\n" +
                    "\t\t\tTRANSPORTADORA_CODIGO=tu_trnsprtdra,--5\n" +
                    "\t\t\tTRANSPORTADORA_NOMBRE=tr_nmbre,--6\n" +
                    "\t\t\tPLACA=tu_plca,--7\n" +
                    "\t\t\tTARA=tu_tra,--8\n" +
                    "\t\t\tTARADO=tu_fcha,--9\n" +
                    "\t\t\t'',--MN_CODIGO=mo_cdgo,--10\n" +
                    "\t\t\t'',--MN=mo_nmbre ,--11\n" +
                    "\t\t\tDEPOSITO=[tu_dpsto] --12\n" +
                    "\t\t\t,1 as cliente_transprtadora_product_db --13\n" +
                    "\t\t FROM ["+DB_gp+"].[dbo].[tra_urbno]    \n" +
                    "\t\t\tINNER JOIN ["+DB_gp+"].[dbo].dpsto  ON de_cdgo=[tu_dpsto]\n" +
                    "\t\t\tINNER JOIN ["+DB_gp+"].[dbo].clnte ON de_clnte= cl_cdgo\n" +
                    "\t\t\tINNER JOIN ["+DB_gp+"].[dbo].[arrbo_mtnve] ON de_arrbo_mtnve=[am_cdgo]\n" +
                    "\t\t\tINNER JOIN ["+DB_gp+"].[dbo].mtnve ON [am_mtnve]=mo_cdgo\n" +
                    "\t\t\t\t\t\t\t\tINNER JOIN ["+DB_gp+"].[dbo].artclo ON de_artclo = ar_cdgo\n" +
                    "\t\t\t\t\t\t\t\tLEFT JOIN  ["+DB_gp+"].[dbo].trnsprtdra ON tr_cdgo=[tu_trnsprtdra]\n" +
                    "\t\t\t\t\t\t\t\tLEFT JOIN ["+DB_gp+"].[dbo].cncpto ON  cn_cdgo= [tu_cncpto]\n" +
                    "\t\tWHERE [tu_psdas] >0 AND tu_fcha >= CONVERT (date, GETDATE()-30) \n" +
                    "\t\t\tAND  tu_plca LIKE @placa \n" +
                    "UNION \n" +
                    "\tSELECT  PRODUCTO_CODIGO=ar_cdgo,\n" +
                    "        PRODUCTO=ar_nmbre,\n" +
                    "        CLIENTE_CODIGO=cl_cdgo,\n" +
                    "        CLIENTE=cl_nmbre, \n" +
                    "        TRANSPORTADORA_CODIGO=tu_trnsprtdra,\n" +
                    "        TRANSPORTADORA_NOMBRE=tr_nmbre,\n" +
                    "        PLACA=tu_plca,\n" +
                    "        TARA=tu_tra,\n" +
                    "        TARADO=tu_fcha,\n" +
                    "        '',--MN_CODIGO=mo_cdgo,\n" +
                    "        '',--MN=mo_nmbre ,\n" +
                    "        DEPOSITO=[tu_dpsto] \n" +
                    "        ,2 as cliente_transprtadora_product_db --13\n" +
                    "    FROM ["+DB_opp+"].[dbo].[tra_urbno]    \n" +
                    "\t\tINNER JOIN ["+DB_opp+"].[dbo].dpsto  ON de_cdgo=[tu_dpsto]\n" +
                    "\t\tINNER JOIN ["+DB_opp+"].[dbo].clnte ON de_clnte= cl_cdgo\n" +
                    "\t\tINNER JOIN ["+DB_opp+"].[dbo].[arrbo_mtnve] ON de_arrbo_mtnve=[am_cdgo]\n" +
                    "\t\tINNER JOIN ["+DB_opp+"].[dbo].mtnve ON [am_mtnve]=mo_cdgo\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB_opp+"].[dbo].artclo ON de_artclo = ar_cdgo\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN  ["+DB_opp+"].[dbo].trnsprtdra ON tr_cdgo=[tu_trnsprtdra]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB_opp+"].[dbo].cncpto ON  cn_cdgo= [tu_cncpto]\n" +
                    "    WHERE [tu_psdas] >0 AND tu_fcha >= CONVERT (date, GETDATE()-30) \n" +
                    "\t\tAND  tu_plca LIKE @placa \n" +
                    "ORDER BY tu_fcha ASC");
            String d= ""+placa+"%";
            queryBuscar.setString(1, d);
            ResultSet resultSetBuscar = queryBuscar.executeQuery();
            System.out.println("Llegamos "+placa);
            while (resultSetBuscar.next()) {
                System.out.println(resultSetBuscar.getString(7));
                MvtoCarbon mvtoCarbon = new MvtoCarbon();
                if(!(resultSetBuscar.getString(1)== null || resultSetBuscar.getString(1).equals(""))) {
                    mvtoCarbon.setArticulo(new Articulo(resultSetBuscar.getString(1), resultSetBuscar.getString(2), "1"));
                    mvtoCarbon.getArticulo().setBaseDatos(new BaseDatos(resultSetBuscar.getString(13)));
                }else{
                    mvtoCarbon.setArticulo(new Articulo("NULL", "NULL", "NULL"));
                    mvtoCarbon.getArticulo().setBaseDatos(new BaseDatos("NULL"));
                }
                if(!(resultSetBuscar.getString(3)== null || resultSetBuscar.getString(3).equals(""))) {
                    mvtoCarbon.setCliente(new Cliente(resultSetBuscar.getString(3), resultSetBuscar.getString(4), "1", 0));
                    mvtoCarbon.getCliente().setBaseDatos(new BaseDatos(resultSetBuscar.getString(13)));
                }else{
                    mvtoCarbon.setCliente(new Cliente("NULL", "NULL", "NULL", 0));
                    mvtoCarbon.getCliente().setBaseDatos(new BaseDatos("NULL"));

                }
                if(!(resultSetBuscar.getString(5)== null || resultSetBuscar.getString(5).equals(""))){
                    mvtoCarbon.setTransportadora(new Transportadora(resultSetBuscar.getString(5),"",resultSetBuscar.getString(6),"","1"));
                    mvtoCarbon.getTransportadora().setBaseDatos(new BaseDatos(resultSetBuscar.getString(13)));
                }else{
                    mvtoCarbon.setTransportadora(new Transportadora("NULL","NULL","NULL","NULL","NULL"));
                    mvtoCarbon.getTransportadora().setBaseDatos(new BaseDatos("NULL"));
                }
                mvtoCarbon.setPlaca(resultSetBuscar.getString(7));
                mvtoCarbon.setPesoVacio(resultSetBuscar.getString(8));
                mvtoCarbon.setFechaEntradaVehiculo(resultSetBuscar.getString(9));
                if(!(resultSetBuscar.getString(1)== null || resultSetBuscar.getString(1).equals(""))) {
                    mvtoCarbon.setMotonave(new Motonave(resultSetBuscar.getString(10), resultSetBuscar.getString(11), "1"));
                    mvtoCarbon.getMotonave().setBaseDatos(new BaseDatos(resultSetBuscar.getString(13)));
                }else{
                    mvtoCarbon.setMotonave(new Motonave("NULL", "NULL", "NULL"));
                    mvtoCarbon.getMotonave().setBaseDatos(new BaseDatos("NULL"));
                }
                mvtoCarbon.setDeposito(resultSetBuscar.getString(12));
                listadoObjeto.add(mvtoCarbon);
            }
        } catch (Exception e) {
        }
        control_gp.cerrarConexionBaseDatos();
        return listadoObjeto;
    }

    //Registramos El Movimiento Carbon
    public int registrarMvtoCarbonCompleto(MvtoCarbon Objeto, AsignacionEquipo asignacionEquipo, Usuario usuario, MvtoEquipo mvtoEquipo) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
            String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                String namePc=new ControlDB_Config().getNamePC();
                String ipPc=new ControlDB_Config().getIpPc();
                String macPC=new ControlDB_Config().getMacAddress();
                try {
                    if (mvtoEquipo.getRecobro().getCodigo().equals("1")) {
                        mvtoEquipo.getRecobro().setCodigo("2");
                        mvtoEquipo.getRecobro().setDescripcion("PENDIENTE CONFIRMACIÓN");
                    }
                }catch(Exception e){
                    System.out.println("Error al procesar el recobro");
                }
                try {
                    if(!Objeto.getArticulo().getCodigo().equals("NULL")) {
                        if (!validarExistenciaArticulo(Objeto.getArticulo())) {
                            int n = registrarArticulo(Objeto.getArticulo(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un articulo nuevo en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getCliente().getCodigo().equals("NULL")) {
                        if (!validarExistenciaCliente(Objeto.getCliente())) {
                            int n = registrarCliente(Objeto.getCliente(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un nuevo cliente en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getTransportadora().getCodigo().equals("NULL")) {
                        if (!validarExistenciaTransportadora(Objeto.getTransportadora())) {
                            int n = registrarTransportadora(Objeto.getTransportadora(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva transportadora en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getMotonave().getCodigo().equals("NULL")) {
                        if (!validarExistenciaMotonave(Objeto.getMotonave())) {
                            int n = registrarMotonave(Objeto.getMotonave(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva motonave en el sistema");
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Error validandos datos y registrados nuevos Items, Cliente, Motonave, Transportadora, Articulo");
                }
                conexion= control.ConectarBaseDatos();
                String codigoArticulo="",codigoCliente="",codigoTransportadora="";
                String codigoArticulo_BD="",codigoCliente_BD="",codigoTransportadora_BD="";

                if(!Objeto.getArticulo().getCodigo().equals("NULL")) {
                    codigoArticulo = "'" + Objeto.getArticulo().getCodigo() + "'";
                    codigoArticulo_BD=Objeto.getArticulo().getBaseDatos().getCodigo();
                }else{
                    codigoArticulo = Objeto.getArticulo().getCodigo();
                    codigoArticulo_BD="NULL";
                }

                if(!Objeto.getCliente().getCodigo().equals("NULL")) {
                    codigoCliente = "'" + Objeto.getCliente().getCodigo() + "'";
                    codigoCliente_BD=Objeto.getArticulo().getBaseDatos().getCodigo();
                }else{
                    codigoCliente = Objeto.getCliente().getCodigo();
                    codigoCliente_BD="NULL";
                }

                if(!Objeto.getTransportadora().getCodigo().equals("NULL")) {
                    codigoTransportadora= "'" + Objeto.getTransportadora().getCodigo() + "'";
                    codigoTransportadora_BD=Objeto.getArticulo().getBaseDatos().getCodigo();
                }else{
                    codigoTransportadora = Objeto.getCliente().getCodigo();
                    codigoTransportadora_BD="NULL";
                }
                PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                        "INSERT INTO ["+DB+"].[dbo].[mvto_carbon]" +
                        "           ([mc_cdgo],[mc_cntro_oper_cdgo],[mc_cntro_cost_auxiliar_cdgo],[mc_labor_realizada_cdgo],[mc_articulo_cdgo],[mc_cliente_cdgo],[mc_transprtdora_cdgo]" +
                        "           ,[mc_fecha],[mc_num_orden],[mc_deposito],[mc_consecutivo_tqute],[mc_placa_vehiculo],[mc_peso_vacio],[mc_peso_lleno]" +
                        "           ,[mc_peso_neto],[mc_fecha_entrad],[mc_fecha_salid],[mc_fecha_inicio_descargue],[mc_fecha_fin_descargue],[mc_usuario_cdgo],[mc_observ]" +
                        "           ,[mc_estad_mvto_carbon_cdgo],[mc_conexion_peso_ccarga],[mc_registro_manual],[mc_usuario_registro_manual_cdgo],[mc_cntro_cost_auxiliarDestino_cdgo],[mc_lavado_vehiculo],[mc_lavado_vehiculo_observacion]," +
                        "[mc_cliente_base_datos_cdgo],[mc_transprtdora_base_datos_cdgo],[mc_articulo_base_datos_cdgo])" +
                        "     VALUES ((SELECT (CASE WHEN (MAX([mc_cdgo]) IS NULL) THEN 1 ELSE (MAX([mc_cdgo])+1) END)AS [mc_cdgo] FROM ["+DB+"].[dbo].[mvto_carbon])," + Objeto.getCentroOperacion().getCodigo() + "," + Objeto.getCentroCostoAuxiliar().getCodigo() +"," + Objeto.getLaborRealizada().getCodigo() +
                        "," + codigoArticulo + "," + codigoCliente + "," + codigoTransportadora+ ",(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))" +
                        ",'" + Objeto.getNumero_orden() + "','" + Objeto.getDeposito() + "'," + Objeto.getConsecutivo() + ",'" + Objeto.getPlaca() + "'," + Objeto.getPesoVacio() + "" +
                        "," + Objeto.getPesoLleno() + "," + Objeto.getPesoNeto() + ",'"+Objeto.getFechaEntradaVehiculo()+"',"+Objeto.getFecha_SalidaVehiculo()+
                        "," +Objeto.getFechaInicioDescargue()+ ",NULL," +Objeto.getUsuarioRegistroMovil().getCodigo() + "," + Objeto.getObservacion() + "," + Objeto.getEstadoMvtoCarbon().getCodigo() + "," + Objeto.getConexionPesoCcarga() + ",NULL,NULL,"+Objeto.getCentroCostoAuxiliarDestino().getCodigo()+","+Objeto.getLavadoVehiculo()+","+Objeto.getLavadorVehiculoObservacion()+"" +
                        ","+codigoCliente_BD+","+codigoTransportadora_BD+","+codigoArticulo_BD+");\n" +
                        " DECLARE @consecutivoMvtoCarbon BIGINT=(SELECT MAX(mc_cdgo) FROM ["+DB+"].[dbo].[mvto_carbon]); \n" +
                        " INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]    ,[au_fecha],[au_usuario_cdgo_registro] ,[au_nombre_dispositivo_registro]\n" +
                        "                                                    ,[au_ip_dispositivo_registro],[au_mac_dispositivo_registro],[au_cdgo_mtvo],[au_desc_mtvo]\n" +
                        "                                                    ,[au_detalle_mtvo])" +
                        "   VALUES((SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])" +
                        ",(SELECT SYSDATETIME()) ,?,?,?,?,(SELECT @consecutivoMvtoCarbon),'DESCARGUE_CARBON',CONCAT ((SELECT @consecutivoMvtoCarbon),?,?));\n" +
                        " DECLARE @consecutivo BIGINT=(SELECT (CASE WHEN (MAX([me_cdgo]) IS NULL) THEN 1 ELSE (MAX([me_cdgo])+1) END)AS [mc_cdgo] FROM ["+DB+"].[dbo].[mvto_equipo]);\n" +
                        " INSERT INTO ["+DB+"].[dbo].[mvto_equipo] \n" +
                        "                                   ([me_cdgo]\n" +
                        "                                 ,[me_asignacion_equipo_cdgo] \n" +
                        "                                 ,[me_fecha]\n" +
                        "                              ,[me_proveedor_equipo_cdgo]\n" +
                        "                              ,[me_cntro_oper_cdgo]\n" +
                        "                                  ,[me_cntro_cost_auxiliar_cdgo]\n" +
                        "                                   ,[me_labor_realizada_cdgo]\n" +
                        "                                   ,[me_cliente_cdgo]\n" +
                        "                                   ,[me_articulo_cdgo]\n" +
                        "                                   ,[me_fecha_hora_inicio]\n" +
                        "                                   ,[me_recobro_cdgo]\n" +
                        "                                   ,[me_usuario_registro_cdgo]\n" +
                        "                                   ,[me_inactividad]\n" +
                        "                                   ,[me_motivo_parada_estado]\n" +
                        "                                   ,[me_estado]\n" +
                        "                                   ,[me_desde_mvto_carbon],[me_cntro_cost_auxiliarDestino_cdgo],[me_cliente_base_datos_cdgo],[me_articulo_base_datos_cdgo])\n" +
                        "                             VALUES ((select @consecutivo) --me_cdgo\n" +
                        "                                   ,"+asignacionEquipo.getCodigo()+" --me_asignacion_equipo_cdgo\n" +
                        "                                   ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) --me_fecha\n" +
                        "                                  ,'"+mvtoEquipo.getProveedorEquipo().getCodigo()+"'--me_proveedor_equipo_cdgo\n" +
                        "                                   ,"+mvtoEquipo.getCentroOperacion().getCodigo()+"--me_cntro_oper_cdgo\n" +
                        "                                   ,"+mvtoEquipo.getCentroCostoAuxiliar().getCodigo()+"--me_cntro_cost_auxiliar_cdgo\n" +
                        "                                   ,"+mvtoEquipo.getLaborRealizada().getCodigo()+"--me_labor_realizada_cdgo\n" +
                        "                                   ,'"+mvtoEquipo.getCliente().getCodigo()+"'--me_cliente_cdgo\n" +
                        "                                   ,'"+Objeto.getArticulo().getCodigo()+"'--me_articulo_cdgo\n" +
                        "                                   ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))--me_fecha_hora_inicio\n" +
                        "                                   ,"+mvtoEquipo.getRecobro().getCodigo()+"--me_recobro_cdgo\n" +
                        "                                   ,"+mvtoEquipo.getUsuarioQuieRegistra().getCodigo()+"--me_usuario_registro_cdgo\n" +
                        "                                   ,0   --me_inactividad\n" +
                        "                                  ,0--me_motivo_parada_estado\n" +
                        "                                   ,"+mvtoEquipo.getEstado()+"--me_estado\n" +
                        "                                   ,"+mvtoEquipo.getDesdeCarbon()+"--me_desde_mvto_carbon\n" +
                        "                                   ,"+mvtoEquipo.getCentroCostoAuxiliarDestino().getCodigo()+","+codigoCliente_BD+","+codigoArticulo_BD+"); \n" +
                        " INSERT INTO ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                        "                                       ([mcle_cdgo]\n" +
                        "                                       ,[mcle_mvto_carbon_cdgo]\n" +
                        "                                       ,[mcle_asignacion_equipo_cdgo]\n" +
                        "                                      ,[mcle_mvto_equipo_cdgo]\n" +
                        "                                       ,[mcle_estado])\n" +
                        "                                 VALUES\n" +
                        "                                       ((SELECT (CASE WHEN (MAX([mcle_cdgo]) IS NULL)\n" +
                        "                                                         THEN 1 ELSE (MAX([mcle_cdgo])+1) END)AS [mcle_cdgo] \n" +
                        "                                                        FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]) --mcle_cdgo\n" +
                        "                                       , (SELECT @consecutivoMvtoCarbon) --mcle_mvto_carbon_cdgo\n" +
                        "                                       , ? --mcle_asignacion_equipo_cdgo\n" +
                        "                                       , (SELECT @consecutivo)--mcle_mvto_equipo_cdgo\n" +
                        "                                       , 1 --mcle_estado\n" +
                        "                            );\n");
                queryRegistrarT.setString(1, Objeto.getUsuarioRegistroMovil().getCodigo());
                queryRegistrarT.setString(2, namePc);
                queryRegistrarT.setString(3, ipPc);
                queryRegistrarT.setString(4, macPC);
                queryRegistrarT.setString(5, "Se registró un nuevo Movimiento de Carbon en el sistema desde un Dispositivo Movil, con código: ");
                queryRegistrarT.setString(6, " PLACA: " + Objeto.getPlaca() + " Orden: " + Objeto.getNumero_orden() + " Deposito: " + Objeto.getDeposito() + " Articulo: " + Objeto.getArticulo().getDescripcion() + " Peso Vacio: " + Objeto.getPesoVacio());
                queryRegistrarT.setString(7, asignacionEquipo.getCodigo());
                queryRegistrarT.execute();

                result = 1;
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
   public int mvtoCarbon_cerrarCiclo_mtvoEquipo(MvtoCarbon_ListadoEquipos Objeto, Usuario us,MotivoParada motivoParada) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            conexion= control.ConectarBaseDatos();
            PreparedStatement query= conexion.prepareStatement("DECLARE @fechaHoraFin DATETIME=(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))); \n" +
                    "UPDATE ["+DB+"].[dbo].[mvto_equipo] \n" +
                    "SET [me_fecha_hora_fin]=@fechaHoraFin, \n" +
                    "[me_motivo_parada_estado]=1, \n" +
                    "[me_motivo_parada_cdgo]=?,\n" +
                    "[me_total_minutos]=(SELECT DATEDIFF(minute, (SELECT [me_fecha_hora_inicio]  \n" +
                    " FROM ["+DB+"].[dbo].[mvto_equipo] \n" +
                    " WHERE [me_cdgo]=?), @fechaHoraFin)) , [me_usuario_cierre_cdgo]='"+Objeto.getMvtoEquipo().getUsuarioQuienCierra().getCodigo()+"' WHERE [me_cdgo]=?");
            query.setString(1, motivoParada.getCodigo());
            query.setString(2, Objeto.getMvtoEquipo().getCodigo());
            query.setString(3, Objeto.getMvtoEquipo().getCodigo());
            query.execute();
            result=1;
            if(result==1){
                result=0;
                //Extraemos el nombre del Equipo y la IP
                String namePc=new ControlDB_Config().getNamePC();
                String ipPc=new ControlDB_Config().getIpPc();
                String macPC=new ControlDB_Config().getMacAddress();

                PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                        "      ,[au_fecha]\n" +
                        "      ,[au_usuario_cdgo_registro]\n" +
                        "      ,[au_nombre_dispositivo_registro]\n" +
                        "      ,[au_ip_dispositivo_registro]\n" +
                        "      ,[au_mac_dispositivo_registro]\n" +
                        "      ,[au_cdgo_mtvo]\n" +
                        "      ,[au_desc_mtvo]\n" +
                        "      ,[au_detalle_mtvo])\n" +
                        "     VALUES("+
                        "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                        "           ,(SELECT SYSDATETIME())"+
                        "           ,?"+
                        "           ,?"+
                        "           ,?"+
                        "           ,?"+
                        "           ,?"+
                        "           ,'DESCARGUE_CIERRE_CICLO_EQUIPO'" +
                        "           ,CONCAT('Se registró un cierre de ciclo de Equipo en el sistema sobre un Descargue ',?,'Código: ',?,' Placa Vehículo Descargado: ',?));");
                Query_Auditoria.setString(1, us.getCodigo());
                Query_Auditoria.setString(2, namePc);
                Query_Auditoria.setString(3, ipPc);
                Query_Auditoria.setString(4, macPC);
                Query_Auditoria.setString(5, Objeto.getMvtoEquipo().getCodigo());
                Query_Auditoria.setString(6, " MvtoEquipo ");
                Query_Auditoria.setString(7, Objeto.getMvtoEquipo().getCodigo());
                Query_Auditoria.setString(8, Objeto.getMvtoCarbon().getPlaca());
                Query_Auditoria.execute();
                result=1;
            }
        }
        catch (SQLException sqlException ){
            result=0;
            System.out.println("ERROR al querer insertar los datos.");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }

    //Revisado ············································___>
    public ArrayList<String> MvtoCarbon_ListadoEquipo(String codigoMvtoCarbon) throws SQLException{
        ArrayList<String> listadoObjetos = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT CONCAT([eq_cdgo],' ',[eq_desc],' ',[eq_marca],' ',[eq_modelo])\n" +
                                                        "  FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                                                        "  INNER JOIN ["+DB+"].[dbo].[asignacion_equipo] ON [ae_cdgo]=[mcle_asignacion_equipo_cdgo]\n" +
                                                        "  INNER JOIN ["+DB+"].[dbo].[equipo] ON [eq_cdgo]=[ae_equipo_cdgo] \n" +
                                                        "  WHERE [mcle_mvto_carbon_cdgo]=? AND [mcle_estado]=1 ORDER BY [mcle_cdgo] DESC;");

            queryBuscar.setString(1, codigoMvtoCarbon);
            ResultSet resultSetBuscar=queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                listadoObjetos.add(resultSetBuscar.getString(1));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }

    public int MvtoCarbon_finalizarDescargue(MvtoCarbon Objeto, Usuario user) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                PreparedStatement query= null;
                try {
                    query = conexion.prepareStatement("UPDATE ["+DB+"].[dbo].[mvto_carbon] set [mc_fecha_fin_descargue]=(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) " +
                            ",[mc_lavado_vehiculo]="+Objeto.getLavadoVehiculo()+" ,[mc_lavado_vehiculo_observacion]=? ,[mc_motivo_nolavado_vehiculo_cdgo]="+Objeto.getMotivoNoLavado().getCodigo()+", [mc_equipo_lavado_cdgo]="+Objeto.getEquipoLavadoVehiculo().getCodigo()+" " +
                            " ,[mc_usuario_cierre_cdgo]='"+Objeto.getUsuarioQuienCierra().getCodigo()+"' WHERE [mc_cdgo]=?");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                query.setString(1, Objeto.getLavadorVehiculoObservacion());
                query.setString(2, Objeto.getCodigo());
                query.execute();
                result=1;
                if(result==1) {
                    result = 0;
                    //Extraemos el nombre del Dispositivo y la IP
                    String namePc = new ControlDB_Config().getNamePC();
                    String ipPc = new ControlDB_Config().getIpPc();
                    String macPC = new ControlDB_Config().getMacAddress();
                    PreparedStatement Query_AuditoriaInsert = conexion.prepareStatement("INSERT INTO [" + DB + "].[dbo].[auditoria]([au_cdgo]\n" +
                            "      ,[au_fecha]\n" +
                            "      ,[au_usuario_cdgo_registro]\n" +
                            "      ,[au_nombre_dispositivo_registro]\n" +
                            "      ,[au_ip_dispositivo_registro]\n" +
                            "      ,[au_mac_dispositivo_registro]\n" +
                            "      ,[au_cdgo_mtvo]\n" +
                            "      ,[au_desc_mtvo]\n" +
                            "      ,[au_detalle_mtvo])\n" +
                            "     VALUES(" +
                            "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM [" + DB + "].[dbo].[auditoria])" +
                            "           ,(SELECT SYSDATETIME())" +
                            "           ,?" +
                            "           ,?" +
                            "           ,?" +
                            "           ,?" +
                            "           ,?" +
                            "           ,'DESCARGUE_CARBON'" +
                            "           ,CONCAT (?,?,?));");
                    Query_AuditoriaInsert.setString(1, user.getCodigo());
                    Query_AuditoriaInsert.setString(2, namePc);
                    Query_AuditoriaInsert.setString(3, ipPc);
                    Query_AuditoriaInsert.setString(4, macPC);
                    Query_AuditoriaInsert.setString(5, Objeto.getCodigo());
                    Query_AuditoriaInsert.setString(6, "Se finalizó el descargue del vehículo con Código: ");
                    Query_AuditoriaInsert.setString(7, Objeto.getCodigo());
                    Query_AuditoriaInsert.setString(8, " PLACA: " + Objeto.getPlaca() + " Orden: " + Objeto.getNumero_orden() + " Deposito: " + Objeto.getDeposito() + " Articulo: " + Objeto.getArticulo().getDescripcion() + " Peso Vacio: " + Objeto.getPesoVacio());
                    Query_AuditoriaInsert.execute();
                    result = 1;

                }
                System.out.println("Resultado de result->"+result);
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
    public ArrayList<MvtoCarbon_ListadoEquipos> buscarPlaca_EnMovimientoCarbon_Activos(MvtoCarbon Objeto) throws SQLException{
        ArrayList<MvtoCarbon_ListadoEquipos> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT TOP 100\n" +
                    "[mcle_cdgo] --1\n" +
                    "      ,[mcle_mvto_carbon_cdgo] --2\n" +
                    "		  ,[mc_cdgo] --3\n" +
                    "		  ,[mc_cntro_oper_cdgo] --4\n" +
                    "				--Centro Operacion\n" +
                    "				,[co_cdgo] --5\n" +
                    "				,[co_desc] --6\n" +
                    "				,CASE [co_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [co_estad] --7\n" +
                    "		  ,[mc_cntro_cost_auxiliar_cdgo] --8\n" +
                    "				--Centro Costo Auxiliar\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cdgo] --9\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] --10\n" +
                    "						--Subcentro de Costo\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_cdgo] --11\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_desc]  --12\n" +
                    "						,CASE mc_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [ccs_estad] --13\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_desc] --14\n" +
                    "				,CASE mc_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [cca_estad] --15\n" +
                    "		  ,[mc_articulo_cdgo] --16\n" +
                    "				--Articulo\n" +
                    "				,mc_articulo.[ar_cdgo] --17\n" +
                    "				,mc_articulo.[ar_desc] --18\n" +
                    "				,CASE mc_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --19\n" +
                    "		  ,[mc_cliente_cdgo] --20\n" +
                    "				--Cliente\n" +
                    "				,mc_cliente.[cl_cdgo] --21\n" +
                    "				,mc_cliente.[cl_desc] --22\n" +
                    "				,CASE mc_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --23\n" +
                    "		  ,[mc_transprtdora_cdgo] --24\n" +
                    "				--Transportadora\n" +
                    "				,[tr_cdgo] --25\n" +
                    "				,[tr_nit] --26\n" +
                    "				,[tr_desc] --27\n" +
                    "				,[tr_observ] --28\n" +
                    "				,CASE [tr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [tr_estad] --29\n" +
                    "		  ,[mc_fecha] --30\n" +
                    "		  ,[mc_num_orden] --31\n" +
                    "		  ,[mc_deposito] --32\n" +
                    "		  ,[mc_consecutivo_tqute] --33\n" +
                    "		  ,[mc_placa_vehiculo] --34\n" +
                    "		  ,[mc_peso_vacio] --35\n" +
                    "		  ,[mc_peso_lleno] --36\n" +
                    "		  ,[mc_peso_neto] --37\n" +
                    "		  ,[mc_fecha_entrad] --38\n" +
                    "		  ,[mc_fecha_salid] --39\n" +
                    "		  ,[mc_fecha_inicio_descargue] --40\n" +
                    "		  ,[mc_fecha_fin_descargue] --41\n" +
                    "		  ,[mc_usuario_cdgo] --42\n" +
                    "				--Usuario Quien Registra desde App Movil\n" +
                    "				,user_registra.[us_cdgo] --43\n" +
                    "				,user_registra.[us_clave] --44\n" +
                    "				,user_registra.[us_nombres] --45\n" +
                    "				,user_registra.[us_apellidos] --46\n" +
                    "				,user_registra.[us_perfil_cdgo] --47\n" +
                    "					--Perfil Usuario Quien Registra\n" +
                    "					,prf_registra.[prf_cdgo] --48\n" +
                    "					,prf_registra.[prf_desc] --49\n" +
                    "					,CASE prf_registra.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --50\n" +
                    "				,user_registra.[us_correo] --51\n" +
                    "				,CASE user_registra.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --52\n" +
                    "		  ,[mc_observ] --53\n" +
                    "		  ,[mc_estad_mvto_carbon_cdgo] --54\n" +
                    "				--Estado MvtoCarbon\n" +
                    "				,[emc_cdgo] --55\n" +
                    "				,[emc_desc] --56\n" +
                    "				,CASE [emc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [emc_estad] --57\n" +
                    "		  ,CASE [mc_conexion_peso_ccarga] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [mc_conexion_peso_ccarga] --58\n" +
                    "		  ,[mc_registro_manual] --59\n" +
                    "		  ,[mc_usuario_registro_manual_cdgo] --60\n" +
                    "				--Usuario Quien Registra Manual\n" +
                    "				,user_registro_manual.[us_cdgo] --61\n" +
                    "				,user_registro_manual.[us_clave] --62\n" +
                    "				,user_registro_manual.[us_nombres] --63\n" +
                    "				,user_registro_manual.[us_apellidos] --64\n" +
                    "				,user_registro_manual.[us_perfil_cdgo] --65\n" +
                    "					--Perfil Usuario Quien Registra Manual\n" +
                    "					,prf_registra_manual.[prf_cdgo] --66\n" +
                    "					,prf_registra_manual.[prf_desc] --67\n" +
                    "					,CASE prf_registra_manual.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --68\n" +
                    "				,user_registro_manual.[us_correo] --69\n" +
                    "				,CASE user_registro_manual.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --70\n" +
                    "		,[mcle_asignacion_equipo_cdgo] --71\n" +
                    "				,[ae_cdgo] --72\n" +
                    "				,ae_cntro_oper_cdgo --73\n" +
                    "					--Centro Operacion\n" +
                    "					,ae_co_cdgo --74\n" +
                    "					,ae_co_desc --75\n" +
                    "					,CASE ae_co_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_co_estado --76\n" +
                    "				,ae_solicitud_listado_equipo_cdgo --77\n" +
                    "					-- Solicitud Listado Equipo\n" +
                    "					,ae_sle_cdgo --78\n" +
                    "					,ae_sle_solicitud_equipo_cdgo --79\n" +
                    "							--Solicitud Equipo\n" +
                    "							,ae_sle_se_cdgo --80\n" +
                    "							,ae_sle_se_cntro_oper_cdgo --81\n" +
                    "								--CentroOperación SolicitudEquipo\n" +
                    "								,ae_sle_se_co_cdgo --82\n" +
                    "								,ae_sle_se_co_desc --83\n" +
                    "								,CASE ae_sle_se_co_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_co_estad --84\n" +
                    "							,ae_sle_se_fecha --85\n" +
                    "							,ae_sle_se_usuario_realiza_cdgo --86\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_registra_cdgo --87\n" +
                    "								,ae_sle_se_us_registra_clave --88\n" +
                    "								,ae_sle_se_us_registra_nombres --89\n" +
                    "								,ae_sle_se_us_registra_apellidos --90\n" +
                    "								,ae_sle_se_us_registra_perfil_cdgo --91\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_registra_cdgo --92\n" +
                    "										,ae_sle_se_prf_us_registra_desc --93\n" +
                    "										,CASE ae_sle_se_prf_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_registra_estad --94\n" +
                    "								,ae_sle_se_us_registra_correo --95\n" +
                    "								,CASE ae_sle_se_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_registra_estad --96\n" +
                    "							,ae_sle_se_fecha_registro --97\n" +
                    "							,ae_sle_se_estado_solicitud_equipo_cdgo --98\n" +
                    "								--Estado de la solicitud\n" +
                    "								,ae_sle_se_ese_cdgo --99\n" +
                    "								,ae_sle_se_ese_desc --100\n" +
                    "								,CASE ae_sle_se_ese_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ese_estad --101\n" +
                    "							,ae_sle_se_fecha_confirmacion --102\n" +
                    "							,ae_se_usuario_confirma_cdgo --103\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_confirma_cdgo --104\n" +
                    "								,ae_sle_se_us_confirma_clave --105\n" +
                    "								,ae_sle_se_us_confirma_nombres --106\n" +
                    "								,ae_sle_se_us_confirma_apellidos --107\n" +
                    "								,ae_sle_se_us_confirma_perfil_cdgo --108\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_confirma_cdgo --109\n" +
                    "										,ae_sle_se_prf_us_confirma_desc --110\n" +
                    "										,CASE ae_sle_se_prf_us_confirma_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_confirma_estad --111\n" +
                    "								,ae_sle_se_us_confirma_correo --112\n" +
                    "								,CASE ae_sle_se_us_confirma_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_confirma_estado --113\n" +
                    "							,ae_sle_se_confirmacion_solicitud_equipo_cdgo --114\n" +
                    "								--Confirmacion solicitudEquipo\n" +
                    "								,ae_sle_se_cse_cdgo --115\n" +
                    "								,ae_sle_se_ces_desc --116\n" +
                    "								,CASE ae_sle_se_ces_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ces_estado --117\n" +
                    "					,ae_sle_tipo_equipo_cdgo --118\n" +
                    "						--Tipo de Equipo\n" +
                    "						,ae_sle_te_cdgo --119\n" +
                    "						,ae_sle_te_desc --120\n" +
                    "						,CASE ae_sle_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_te_estad --121\n" +
                    "					,ae_sle_marca_equipo --122\n" +
                    "					,ae_sle_modelo_equipo --123\n" +
                    "					,ae_sle_cant_equip --124\n" +
                    "					,ae_sle_observ --125\n" +
                    "					,ae_sle_fecha_hora_inicio --126\n" +
                    "					,ae_sle_fecha_hora_fin --127\n" +
                    "					,ae_sle_cant_minutos --128\n" +
                    "					,ae_sle_labor_realizada_cdgo --129\n" +
                    "					-- Labor Realizada\n" +
                    "							,ae_sle_lr_cdgo --130\n" +
                    "							,ae_sle_lr_desc --131\n" +
                    "							,CASE ae_sle_lr_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_lr_estad --132\n" +
                    "					,ae_sle_sle_motonave_cdgo --133\n" +
                    "					--Motonave\n" +
                    "							,ae_sle_mn_cdgo --134\n" +
                    "							,ae_sle_mn_desc --135\n" +
                    "							,CASE ae_sle_mn_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_mn_estad --136\n" +
                    "					,ae_sle_cntro_cost_auxiliar_cdgo --137\n" +
                    "						--Centro Costo Auxiliar\n" +
                    "						,ae_sle_cca_cdgo --138\n" +
                    "						,ae_sle_cca_cntro_cost_subcentro_cdgo --139\n" +
                    "							-- SubCentro de Costo\n" +
                    "							,ae_sle_ccs_cdgo --140\n" +
                    "							,ae_sle_ccs_desc --141\n" +
                    "							,CASE ae_sle_ccs_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_ccs_estad --142\n" +
                    "						,ae_sle_cca_desc --143\n" +
                    "						,CASE ae_sle_cca_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cca_estad --144\n" +
                    "					,ae_sle_compania_cdgo --145\n" +
                    "						--Compañia\n" +
                    "						,ae_sle_cp_cdgo --146\n" +
                    "						,ae_sle_cp_desc --147\n" +
                    "						,CASE ae_sle_cp_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cp_estad --148\n" +
                    "				,ae_fecha_registro --149\n" +
                    "				,ae_fecha_hora_inicio --150\n" +
                    "				,ae_fecha_hora_fin --151\n" +
                    "				,ae_cant_minutos --152\n" +
                    "				,ae_equipo_cdgo --153\n" +
                    "					--Equipo\n" +
                    "					,ae_eq_cdgo --154\n" +
                    "					,ae_eq_tipo_equipo_cdgo --155\n" +
                    "						--Tipo Equipo\n" +
                    "						,ae_eq_te_cdgo --156\n" +
                    "						,ae_eq_te_desc --157\n" +
                    "						,CASE ae_eq_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_te_estad --158\n" +
                    "					,ae_eq_codigo_barra --159\n" +
                    "					,ae_eq_referencia --160\n" +
                    "					,ae_eq_producto --161\n" +
                    "					,ae_eq_capacidad --162\n" +
                    "					,ae_eq_marca --163\n" +
                    "					,ae_eq_modelo --164\n" +
                    "					,ae_eq_serial --165\n" +
                    "					,ae_eq_desc --166\n" +
                    "					,ae_eq_clasificador1_cdgo --167\n" +
                    "						-- Clasificador 1\n" +
                    "						,ae_eq_ce1_cdgo --168\n" +
                    "						,ae_eq_ce1_desc --169\n" +
                    "						,CASE ae_eq_ce1_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce1_estad --170\n" +
                    "					,ae_eq_clasificador2_cdgo --171\n" +
                    "						-- Clasificador 2\n" +
                    "						,ae_eq_ce2_cdgo --172\n" +
                    "						,ae_eq_ce2_desc --173\n" +
                    "						,CASE ae_eq_ce2_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce2_estad --174\n" +
                    "					,ae_eq_proveedor_equipo_cdgo --175\n" +
                    "						--Proveedor Equipo\n" +
                    "						,ae_eq_pe_cdgo --176\n" +
                    "						,ae_eq_pe_nit --177\n" +
                    "						,ae_eq_pe_desc --178\n" +
                    "						,CASE ae_eq_pe_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_pe_estad --179\n" +
                    "					,ae_eq_equipo_pertenencia_cdgo --180\n" +
                    "						-- Equipo Pertenencia\n" +
                    "						,ae_eq_ep_cdgo --181\n" +
                    "						,ae_eq_ep_desc --182\n" +
                    "						,CASE ae_eq_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ep_estad --183\n" +
                    "					,ae_eq_eq_observ --184\n" +
                    "					,CASE ae_eq_eq_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_eq_estad --185\n" +
                    "					,ae_eq_actvo_fijo_id --186\n" +
                    "					,ae_eq_actvo_fijo_referencia --187\n" +
                    "					,ae_eq_actvo_fijo_desc --188\n" +
                    "				,ae_equipo_pertenencia_cdgo --189\n" +
                    "				-- Equipo Pertenencia\n" +
                    "						,ae_ep_cdgo --190\n" +
                    "						,ae_ep_desc --191\n" +
                    "						,CASE ae_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_ep_estad --192\n" +
                    "				,ae_cant_minutos_operativo --193\n" +
                    "				,ae_cant_minutos_parada --194\n" +
                    "				,ae_cant_minutos_total --195\n" +
                    "				,CASE ae_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_estad --196\n" +
                    "		,[mcle_mvto_equipo_cdgo] --197\n" +
                    "				--Movimiento Equipo\n" +
                    "				,[me_cdgo] --198\n" +
                    "				,[me_asignacion_equipo_cdgo] --199\n" +
                    "				,[me_fecha] --200\n" +
                    "				,[me_proveedor_equipo_cdgo] --201\n" +
                    "					--Proveedor Equipo\n" +
                    "					,[pe_cdgo] AS me_pe_cdgo --202\n" +
                    "					,[pe_nit] AS me_pe_nit --203\n" +
                    "					,[pe_desc] AS me_pe_desc --204\n" +
                    "					,CASE [pe_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS me_pe_estad --205\n" +
                    "				,[me_num_orden] --206\n" +
                    "				,[me_cntro_cost_auxiliar_cdgo] --207\n" +
                    "					-- Centro Costo Auxiliar\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cdgo] AS me_cca_cdgo --208\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] AS me_cca_cntro_cost_subcentro_cdgo --209\n" +
                    "						--Centro Costo Subcentro\n" +
                    "						,me_cntro_cost_subcentro.[ccs_cdgo] --210\n" +
                    "						,me_cntro_cost_subcentro.[ccs_desc] --211\n" +
                    "						,CASE me_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ccs_estad] --212\n" +
                    "					,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc --213\n" +
                    "					,CASE me_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cca_estad] --214\n" +
                    "				,[me_labor_realizada_cdgo] --215\n" +
                    "					--Labor Realizada\n" +
                    "					,[lr_cdgo]  --216\n" +
                    "					,[lr_desc] --217\n" +
                    "					,CASE [lr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [lr_estad] --218\n" +
                    "				,[me_cliente_cdgo] --219\n" +
                    "					--Cliente \n" +
                    "					,me_cliente.[cl_cdgo] --220\n" +
                    "					,me_cliente.[cl_desc] --221\n" +
                    "					,CASE me_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --222\n" +
                    "				,[me_articulo_cdgo] --223\n" +
                    "					--articulo\n" +
                    "					,me_articulo.[ar_cdgo] --224\n" +
                    "					,me_articulo.[ar_desc] --225\n" +
                    "					,CASE me_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --226\n" +
                    "				,[me_fecha_hora_inicio] --227\n" +
                    "				,[me_fecha_hora_fin] --228\n" +
                    "				,[me_total_minutos] --229\n" +
                    "				,[me_valor_hora] --230\n" +
                    "				,[me_costo_total] --231\n" +
                    "				,[me_recobro_cdgo] --232\n" +
                    "					--Recobro\n" +
                    "					,[rc_cdgo] --233\n" +
                    "					,[rc_desc] --234\n" +
                    "					,CASE [rc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [rc_estad] --235\n" +
                    "				,[me_cliente_recobro_cdgo] --236\n" +
                    "					--Cliente Recobro\n" +
                    "					,[clr_cdgo] --237\n" +
                    "					,[clr_cliente_cdgo] --238\n" +
                    "						--Cliente\n" +
                    "						,me_clr_cliente.[cl_cdgo] --239\n" +
                    "						,me_clr_cliente.[cl_desc] --240\n" +
                    "						,CASE me_clr_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --241\n" +
                    "					,[clr_usuario_cdgo] --242\n" +
                    "						--Usuario Quien Registra Recobro\n" +
                    "						,me_clr_usuario.[us_cdgo] --243\n" +
                    "						,me_clr_usuario.[us_clave] --244\n" +
                    "						,me_clr_usuario.[us_nombres] --245\n" +
                    "						,me_clr_usuario.[us_apellidos] --246\n" +
                    "						,me_clr_usuario.[us_perfil_cdgo] --247\n" +
                    "							--Perfil Usuario Registra recobro\n" +
                    "							,me_clr_us_perfil.[prf_cdgo] --248\n" +
                    "							,me_clr_us_perfil.[prf_desc] --249\n" +
                    "							,CASE me_clr_us_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --250\n" +
                    "						,me_clr_usuario.[us_correo] --251\n" +
                    "						,CASE me_clr_usuario.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --252\n" +
                    "					,[clr_valor_recobro] --253\n" +
                    "					,[clr_fecha_registro] --254\n" +
                    "				,[me_costo_total_recobro_cliente] --255\n" +
                    "				,[me_usuario_registro_cdgo] --256\n" +
                    "					--Usuario Quien Registra Equipo\n" +
                    "					,me_us_registro.[us_cdgo] --257\n" +
                    "					,me_us_registro.[us_clave] --258\n" +
                    "					,me_us_registro.[us_nombres] --259\n" +
                    "					,me_us_registro.[us_apellidos] --260\n" +
                    "					,me_us_registro.[us_perfil_cdgo] --261\n" +
                    "						--Perfil de Usuario Quien Registra Equipo\n" +
                    "						,me_us_regist_perfil.[prf_cdgo] --262\n" +
                    "						,me_us_regist_perfil.[prf_desc] --263\n" +
                    "						,CASE me_us_regist_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --264\n" +
                    "					,me_us_registro.[us_correo] --265\n" +
                    "					,CASE me_us_registro.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --266\n" +
                    "				,[me_usuario_autorizacion_cdgo] --267\n" +
                    "					,me_us_autorizacion.[us_cdgo] --268\n" +
                    "					,me_us_autorizacion.[us_clave] --269\n" +
                    "					,me_us_autorizacion.[us_nombres] --270\n" +
                    "					,me_us_autorizacion.[us_apellidos] --271\n" +
                    "					,me_us_autorizacion.[us_perfil_cdgo] --272\n" +
                    "						,me_us_autoriza_perfil.[prf_cdgo] --273\n" +
                    "						,me_us_autoriza_perfil.[prf_desc] --274\n" +
                    "						,CASE me_us_autoriza_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --275\n" +
                    "					,me_us_autorizacion.[us_correo] --276\n" +
                    "					,CASE me_us_autorizacion.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --277\n" +
                    "				,[me_autorizacion_recobro_cdgo] --278\n" +
                    "					,[are_cdgo] --279\n" +
                    "					,[are_desc] --280\n" +
                    "					,CASE [are_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [are_estad] --281\n" +
                    "				,[me_observ_autorizacion] --282\n" +
                    "				,[me_inactividad] --283\n" +
                    "				,[me_causa_inactividad_cdgo] --284\n" +
                    "					,[ci_cdgo] --285\n" +
                    "					,[ci_desc] --286\n" +
                    "					,CASE [ci_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ci_estad] --287\n" +
                    "				,[me_usuario_inactividad_cdgo] --288\n" +
                    "					,me_us_inactividad.[us_cdgo] --289\n" +
                    "					,me_us_inactividad.[us_clave] --290\n" +
                    "					,me_us_inactividad.[us_nombres] --291\n" +
                    "					,me_us_inactividad.[us_apellidos] --292\n" +
                    "					,me_us_inactividad.[us_perfil_cdgo] --293\n" +
                    "						,me_us_inactvdad_perfil.[prf_cdgo] --294\n" +
                    "						,me_us_inactvdad_perfil.[prf_desc] --295\n" +
                    "						,CASE me_us_inactvdad_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --296\n" +
                    "					,me_us_inactividad.[us_correo] --297\n" +
                    "					,CASE me_us_inactividad.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]	 --298\n" +
                    "                           ,[me_motivo_parada_cdgo]--299\n" +
                    "                                       ,[mpa_cdgo]--300\n" +
                    "                                       ,[mpa_desc]--301\n" +
                    "                                       ,[mpa_estad]--302\n" +
                    "                         ,[me_observ] --303\n" +
                    "				,CASE [me_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [me_estado]	 --304\n" +
                    "				,CASE [me_desde_mvto_carbon] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [me_desde_mvto_carbon]	 --305\n" +
                    "		,CASE [mcle_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [mcle_estado]	 --306\n" +
                    "            ,Tiempo_Vehiculo_Descargue=(SELECT (DATEDIFF (MINUTE,  [mc_fecha_inicio_descargue], [mc_fecha_fin_descargue])))--307\n" +
                    "           ,Tiempo_Equipo_Descargue=(SELECT (DATEDIFF (MINUTE,  [me_fecha_hora_inicio], [me_fecha_hora_fin]))) --308\n" +

                    "        ,[mc_cliente_base_datos_cdgo]--309 \n" +
                    "        ,[mc_articulo_base_datos_cdgo]--310 \n" +
                    "        ,[mc_transprtdora_base_datos_cdgo]--311 \n" +
                    "        ,[me_cliente_base_datos_cdgo]--312 \n" +
                    "        ,[me_articulo_base_datos_cdgo]--313 \n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliar ON [mc_cntro_cost_auxiliar_cdgo]=mc_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] mc_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]= mc_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[articulo] mc_articulo ON [mc_articulo_cdgo]=mc_articulo.[ar_cdgo] AND mc_articulo.[ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[cliente] mc_cliente ON [mc_cliente_cdgo]=mc_cliente.[cl_cdgo] AND mc_cliente.[cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[usuario] user_registra ON [mc_usuario_cdgo] = user_registra.[us_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[perfil] prf_registra ON user_registra.[us_perfil_cdgo]=prf_registra.[prf_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[usuario] user_registro_manual ON [mc_usuario_registro_manual_cdgo] = user_registro_manual.[us_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[perfil] prf_registra_manual ON user_registro_manual.[us_perfil_cdgo]=prf_registra_manual.[prf_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[mvto_carbon_listado_equipo] ON [mcle_mvto_carbon_cdgo]=[mc_cdgo]\n" +
                    "	INNER JOIN (SELECT [ae_cdgo] AS ae_cdgo\n" +
                    "					  ,[ae_cntro_oper_cdgo] AS ae_cntro_oper_cdgo\n" +
                    "							--Centro Operacion\n" +
                    "							,ae_cntro_oper.[co_cdgo] AS ae_co_cdgo\n" +
                    "							,ae_cntro_oper.[co_desc] AS ae_co_desc\n" +
                    "							,ae_cntro_oper.[co_estad] AS ae_co_estado\n" +
                    "					  ,[ae_solicitud_listado_equipo_cdgo] AS ae_solicitud_listado_equipo_cdgo\n" +
                    "							-- Solicitud Listado Equipo\n" +
                    "							,[sle_cdgo] AS ae_sle_cdgo\n" +
                    "							,[sle_solicitud_equipo_cdgo] AS ae_sle_solicitud_equipo_cdgo\n" +
                    "								  --Solicitud Equipo\n" +
                    "								  ,[se_cdgo] AS ae_sle_se_cdgo\n" +
                    "								  ,[se_cntro_oper_cdgo] AS ae_sle_se_cntro_oper_cdgo\n" +
                    "										--CentroOperación SolicitudEquipo\n" +
                    "										,se_cntro_oper.[co_cdgo] AS ae_sle_se_co_cdgo\n" +
                    "										,se_cntro_oper.[co_desc] AS ae_sle_se_co_desc\n" +
                    "										,se_cntro_oper.[co_estad] AS ae_sle_se_co_estad\n" +
                    "								  ,[se_fecha] AS ae_sle_se_fecha\n" +
                    "								  ,[se_usuario_realiza_cdgo] AS ae_sle_se_usuario_realiza_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_realiza.[us_cdgo] AS ae_sle_se_us_registra_cdgo\n" +
                    "										,se_usuario_realiza.[us_clave] AS ae_sle_se_us_registra_clave\n" +
                    "										,se_usuario_realiza.[us_nombres] AS ae_sle_se_us_registra_nombres\n" +
                    "										,se_usuario_realiza.[us_apellidos] AS ae_sle_se_us_registra_apellidos\n" +
                    "										,se_usuario_realiza.[us_perfil_cdgo] AS ae_sle_se_us_registra_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra.[prf_cdgo] AS ae_sle_se_prf_us_registra_cdgo\n" +
                    "											,ae_prf_registra.[prf_desc] AS ae_sle_se_prf_us_registra_desc\n" +
                    "											,ae_prf_registra.[prf_estad] AS ae_sle_se_prf_us_registra_estad\n" +
                    "										,se_usuario_realiza.[us_correo] AS ae_sle_se_us_registra_correo\n" +
                    "										,se_usuario_realiza.[us_estad] AS ae_sle_se_us_registra_estad\n" +
                    "								  ,[se_fecha_registro] AS ae_sle_se_fecha_registro\n" +
                    "								  ,[se_estado_solicitud_equipo_cdgo] AS ae_sle_se_estado_solicitud_equipo_cdgo\n" +
                    "										--Estado de la solicitud\n" +
                    "										,[ese_cdgo] AS ae_sle_se_ese_cdgo\n" +
                    "										,[ese_desc] AS ae_sle_se_ese_desc\n" +
                    "										,[ese_estad] AS ae_sle_se_ese_estad\n" +
                    "								  ,[se_fecha_confirmacion] AS ae_sle_se_fecha_confirmacion\n" +
                    "								  ,[se_usuario_confirma_cdgo] AS ae_se_usuario_confirma_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_confirma.[us_cdgo] AS ae_sle_se_us_confirma_cdgo\n" +
                    "										,se_usuario_confirma.[us_clave] AS ae_sle_se_us_confirma_clave\n" +
                    "										,se_usuario_confirma.[us_nombres] AS ae_sle_se_us_confirma_nombres\n" +
                    "										,se_usuario_confirma.[us_apellidos] AS ae_sle_se_us_confirma_apellidos\n" +
                    "										,se_usuario_confirma.[us_perfil_cdgo] AS ae_sle_se_us_confirma_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra_confirma.[prf_cdgo] AS ae_sle_se_prf_us_confirma_cdgo\n" +
                    "											,ae_prf_registra_confirma.[prf_desc] AS ae_sle_se_prf_us_confirma_desc\n" +
                    "											,ae_prf_registra_confirma.[prf_estad] AS ae_sle_se_prf_us_confirma_estad\n" +
                    "										,se_usuario_confirma.[us_correo] AS ae_sle_se_us_confirma_correo\n" +
                    "										,se_usuario_confirma.[us_estad] AS ae_sle_se_us_confirma_estado\n" +
                    "								  ,[se_confirmacion_solicitud_equipo_cdgo] AS ae_sle_se_confirmacion_solicitud_equipo_cdgo\n" +
                    "										--Confirmacion solicitudEquipo\n" +
                    "										,[cse_cdgo] AS ae_sle_se_cse_cdgo\n" +
                    "										,[cse_desc] AS ae_sle_se_ces_desc\n" +
                    "										,[cse_estad] AS ae_sle_se_ces_estado\n" +
                    "							,[sle_tipo_equipo_cdgo] AS ae_sle_tipo_equipo_cdgo\n" +
                    "								--Tipo de Equipo\n" +
                    "								,sle_tipoEquipo.[te_cdgo] AS ae_sle_te_cdgo\n" +
                    "								,sle_tipoEquipo.[te_desc] AS ae_sle_te_desc\n" +
                    "								,sle_tipoEquipo.[te_estad] AS ae_sle_te_estad\n" +
                    "							,[sle_marca_equipo] AS ae_sle_marca_equipo\n" +
                    "							,[sle_modelo_equipo] AS ae_sle_modelo_equipo\n" +
                    "							,[sle_cant_equip] AS ae_sle_cant_equip\n" +
                    "							,[sle_observ] AS ae_sle_observ\n" +
                    "							,[sle_fecha_hora_inicio] AS ae_sle_fecha_hora_inicio\n" +
                    "							,[sle_fecha_hora_fin] AS ae_sle_fecha_hora_fin\n" +
                    "							,[sle_cant_minutos] AS ae_sle_cant_minutos\n" +
                    "							,[sle_labor_realizada_cdgo] AS ae_sle_labor_realizada_cdgo\n" +
                    "							-- Labor Realizada\n" +
                    "								  ,[lr_cdgo] AS ae_sle_lr_cdgo\n" +
                    "								  ,[lr_desc] AS ae_sle_lr_desc\n" +
                    "								  ,[lr_estad] AS ae_sle_lr_estad\n" +
                    "							,[sle_motonave_cdgo] AS ae_sle_sle_motonave_cdgo\n" +
                    "							--Motonave\n" +
                    "								  ,[mn_cdgo] AS ae_sle_mn_cdgo\n" +
                    "								  ,[mn_desc] AS ae_sle_mn_desc\n" +
                    "								  ,[mn_estad] AS ae_sle_mn_estad\n" +
                    "							,[sle_cntro_cost_auxiliar_cdgo] AS ae_sle_cntro_cost_auxiliar_cdgo\n" +
                    "								--Centro Costo Auxiliar\n" +
                    "								,[cca_cdgo] AS ae_sle_cca_cdgo\n" +
                    "								,[cca_cntro_cost_subcentro_cdgo] AS ae_sle_cca_cntro_cost_subcentro_cdgo\n" +
                    "									-- SubCentro de Costo\n" +
                    "									,[ccs_cdgo] AS ae_sle_ccs_cdgo\n" +
                    "									,[ccs_desc] AS ae_sle_ccs_desc\n" +
                    "									,[ccs_estad] AS ae_sle_ccs_estad\n" +
                    "								,[cca_desc] AS ae_sle_cca_desc\n" +
                    "								,[cca_estad] AS ae_sle_cca_estad\n" +
                    "							,[sle_compania_cdgo] AS ae_sle_compania_cdgo\n" +
                    "								--Compañia\n" +
                    "								,[cp_cdgo] AS ae_sle_cp_cdgo\n" +
                    "								,[cp_desc] AS ae_sle_cp_desc\n" +
                    "								,[cp_estad] AS ae_sle_cp_estad\n" +
                    "					  ,[ae_fecha_registro] AS ae_fecha_registro\n" +
                    "					  ,[ae_fecha_hora_inicio] AS ae_fecha_hora_inicio\n" +
                    "					  ,[ae_fecha_hora_fin] AS ae_fecha_hora_fin\n" +
                    "					  ,[ae_cant_minutos] AS ae_cant_minutos\n" +
                    "					  ,[ae_equipo_cdgo] AS ae_equipo_cdgo\n" +
                    "							--Equipo\n" +
                    "							,[eq_cdgo] AS ae_eq_cdgo\n" +
                    "							,[eq_tipo_equipo_cdgo] AS ae_eq_tipo_equipo_cdgo\n" +
                    "								--Tipo Equipo\n" +
                    "								,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "								,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc\n" +
                    "								,eq_tipo_equipo.[te_estad] AS ae_eq_te_estad\n" +
                    "							,[eq_codigo_barra] AS ae_eq_codigo_barra\n" +
                    "							,[eq_referencia] AS ae_eq_referencia\n" +
                    "							,[eq_producto] AS ae_eq_producto\n" +
                    "							,[eq_capacidad] AS ae_eq_capacidad\n" +
                    "							,[eq_marca] AS ae_eq_marca\n" +
                    "							,[eq_modelo] AS ae_eq_modelo\n" +
                    "							,[eq_serial] AS ae_eq_serial\n" +
                    "							,[eq_desc] AS ae_eq_desc\n" +
                    "							,[eq_clasificador1_cdgo] AS ae_eq_clasificador1_cdgo\n" +
                    "								-- Clasificador 1\n" +
                    "								,eq_clasificador1.[ce_cdgo] AS ae_eq_ce1_cdgo\n" +
                    "								,eq_clasificador1.[ce_desc] AS ae_eq_ce1_desc\n" +
                    "								,eq_clasificador1.[ce_estad] AS ae_eq_ce1_estad\n" +
                    "							,[eq_clasificador2_cdgo] AS ae_eq_clasificador2_cdgo\n" +
                    "								-- Clasificador 2\n" +
                    "								,eq_clasificador2.[ce_cdgo] AS ae_eq_ce2_cdgo\n" +
                    "								,eq_clasificador2.[ce_desc] AS ae_eq_ce2_desc\n" +
                    "								,eq_clasificador2.[ce_estad] AS ae_eq_ce2_estad\n" +
                    "							,[eq_proveedor_equipo_cdgo] AS ae_eq_proveedor_equipo_cdgo\n" +
                    "								--Proveedor Equipo\n" +
                    "								,[pe_cdgo] AS ae_eq_pe_cdgo\n" +
                    "								,[pe_nit] AS ae_eq_pe_nit\n" +
                    "								,[pe_desc] AS ae_eq_pe_desc\n" +
                    "								,[pe_estad] AS ae_eq_pe_estad\n" +
                    "							,[eq_equipo_pertenencia_cdgo] AS ae_eq_equipo_pertenencia_cdgo\n" +
                    "								-- Equipo Pertenencia\n" +
                    "								,eq_pertenencia.[ep_cdgo] AS ae_eq_ep_cdgo\n" +
                    "								,eq_pertenencia.[ep_desc] AS ae_eq_ep_desc\n" +
                    "								,eq_pertenencia.[ep_estad] AS ae_eq_ep_estad\n" +
                    "							,[eq_observ] AS ae_eq_eq_observ\n" +
                    "							,[eq_estad] AS ae_eq_eq_estad\n" +
                    "							,[eq_actvo_fijo_id] AS ae_eq_actvo_fijo_id\n" +
                    "							,[eq_actvo_fijo_referencia] AS ae_eq_actvo_fijo_referencia\n" +
                    "							,[eq_actvo_fijo_desc] AS ae_eq_actvo_fijo_desc\n" +
                    "					  ,[ae_equipo_pertenencia_cdgo] AS ae_equipo_pertenencia_cdgo\n" +
                    "						-- Equipo Pertenencia\n" +
                    "								,ae_pertenencia.[ep_cdgo] AS ae_ep_cdgo\n" +
                    "								,ae_pertenencia.[ep_desc] AS ae_ep_desc\n" +
                    "								,ae_pertenencia.[ep_estad]	 AS ae_ep_estad\n" +
                    "					  ,[ae_cant_minutos_operativo] AS ae_cant_minutos_operativo\n" +
                    "					  ,[ae_cant_minutos_parada] AS ae_cant_minutos_parada\n" +
                    "					  ,[ae_cant_minutos_total] AS ae_cant_minutos_total\n" +
                    "					  ,[ae_estad] AS ae_estad\n" +
                    "					  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra ON se_usuario_realiza.[us_perfil_cdgo]=ae_prf_registra.[prf_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra_confirma ON se_usuario_confirma.[us_perfil_cdgo]=ae_prf_registra_confirma.[prf_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[motonave] ON [sle_motonave_cdgo]=[mn_cdgo] AND [mn_base_datos_cdgo]=[sle_motonave_base_datos_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]	\n" +
                    "	) asignacion_equipo ON [mcle_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                    "	--Movimiento Equipo\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[mvto_equipo] ON [mcle_mvto_equipo_cdgo]=[me_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo] AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[articulo] me_articulo ON [me_articulo_cdgo]=me_articulo.[ar_cdgo] AND me_articulo.[ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[recobro] ON [me_recobro_cdgo]=[rc_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente_recobro] ON [me_cliente_recobro_cdgo]=[clr_cdgo] \n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente] me_clr_cliente ON [clr_cliente_cdgo]=me_clr_cliente.[cl_cdgo] AND [clr_cliente_base_datos_cdgo]= me_clr_cliente.[cl_base_datos_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_clr_usuario ON [clr_usuario_cdgo]=me_clr_usuario.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_clr_us_perfil ON me_clr_usuario.[us_perfil_cdgo]=me_clr_us_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_regist_perfil ON me_us_registro.[us_perfil_cdgo]=me_us_regist_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_autorizacion ON [me_usuario_autorizacion_cdgo]=me_us_autorizacion.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_autoriza_perfil ON me_us_autorizacion.[us_perfil_cdgo]=me_us_autoriza_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[autorizacion_recobro] ON [me_autorizacion_recobro_cdgo]=[are_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[causa_inactividad] ON [me_causa_inactividad_cdgo]=[ci_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_inactividad ON [me_usuario_inactividad_cdgo]=me_us_inactividad.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_inactvdad_perfil ON me_us_inactividad.[us_perfil_cdgo]=me_us_inactvdad_perfil.[prf_cdgo]\n" +
                    "   LEFT JOIN  ["+DB+"].[dbo].[motivo_parada] ON [me_motivo_parada_cdgo]= [mpa_cdgo]\n"  +
                    "	WHERE [mc_cdgo]=? AND mc_placa_vehiculo =?  AND [mc_fecha_entrad]=? AND  [me_inactividad]=0 AND  [mc_estad_mvto_carbon_cdgo]=1 AND [me_fecha_hora_fin] IS NULL ORDER BY [me_cdgo] DESC");
            query.setString(1, Objeto.getCodigo());
            query.setString(2, Objeto.getPlaca());
            query.setString(3, Objeto.getFechaEntradaVehiculo());
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                MvtoCarbon_ListadoEquipos mvto_listEquipo = new MvtoCarbon_ListadoEquipos();
                mvto_listEquipo.setCodigo(resultSet.getString(1));
                MvtoCarbon mvtoCarbon = new MvtoCarbon();
                mvtoCarbon.setCodigo(resultSet.getString(3));
                mvtoCarbon.setCentroOperacion(new CentroOperacion(Integer.parseInt(resultSet.getString(5)),resultSet.getString(6),resultSet.getString(7)));
                mvtoCarbon.setCentroCostoAuxiliar(new CentroCostoAuxiliar(resultSet.getString(9),new CentroCostoSubCentro(Integer.parseInt(resultSet.getString(11)),resultSet.getString(12),resultSet.getString(13)),resultSet.getString(14),resultSet.getString(15)));
                mvtoCarbon.setArticulo(new Articulo(resultSet.getString(17),resultSet.getString(18),resultSet.getString(19)));
                mvtoCarbon.setCliente(new Cliente(resultSet.getString(21),resultSet.getString(22),resultSet.getString(23)));
                mvtoCarbon.setTransportadora(new Transportadora(resultSet.getString(25),resultSet.getString(26),resultSet.getString(27),resultSet.getString(28),resultSet.getString(29)));

                mvtoCarbon.getCliente().setBaseDatos(new BaseDatos(resultSet.getString(309)));
                mvtoCarbon.getArticulo().setBaseDatos(new BaseDatos(resultSet.getString(310)));
                mvtoCarbon.getTransportadora().setBaseDatos(new BaseDatos(resultSet.getString(311)));

                mvtoCarbon.setClienteBaseDatos(resultSet.getString(309));
                mvtoCarbon.setArticuloBaseDatos(resultSet.getString(310));
                mvtoCarbon.setTransportadorBaseDatos(resultSet.getString(311));

                mvtoCarbon.setFechaRegistro(resultSet.getString(30));
                mvtoCarbon.setNumero_orden(resultSet.getString(31));
                mvtoCarbon.setDeposito(resultSet.getString(32));
                mvtoCarbon.setConsecutivo(resultSet.getString(33));
                mvtoCarbon.setPlaca(resultSet.getString(34));
                mvtoCarbon.setPesoVacio(resultSet.getString(35));
                mvtoCarbon.setPesoLleno(resultSet.getString(36));
                mvtoCarbon.setPesoNeto(resultSet.getString(37));
                mvtoCarbon.setFechaEntradaVehiculo(resultSet.getString(38));
                mvtoCarbon.setFecha_SalidaVehiculo(resultSet.getString(39));
                mvtoCarbon.setFechaInicioDescargue(resultSet.getString(40));
                mvtoCarbon.setFechaFinDescargue(resultSet.getString(41));
                Usuario us = new Usuario();
                us.setCodigo(resultSet.getString(43));
                //us.setClave(resultSet.getString(44));
                us.setNombres(resultSet.getString(45));
                us.setApellidos(resultSet.getString(46));
                us.setPerfilUsuario(new Perfil(resultSet.getString(48),resultSet.getString(49),resultSet.getString(50)));
                us.setCorreo(resultSet.getString(51));
                us.setEstado(resultSet.getString(52));
                mvtoCarbon.setUsuarioRegistroMovil(us);
                mvtoCarbon.setObservacion(resultSet.getString(53));
                EstadoMvtoCarbon estadMvtoCarbon = new EstadoMvtoCarbon();
                estadMvtoCarbon.setCodigo(resultSet.getString(55));
                estadMvtoCarbon.setDescripcion(resultSet.getString(56));
                estadMvtoCarbon.setEstado(resultSet.getString(57));
                mvtoCarbon.setEstadoMvtoCarbon(estadMvtoCarbon);
                mvtoCarbon.setConexionPesoCcarga(resultSet.getString(58));
                mvtoCarbon.setRegistroManual(resultSet.getString(59));
                Usuario usRegManual = new Usuario();
                usRegManual.setCodigo(resultSet.getString(61));
                //usRegManual.setClave(resultSet.getString(62));
                usRegManual.setNombres(resultSet.getString(63));
                usRegManual.setApellidos(resultSet.getString(64));
                usRegManual.setPerfilUsuario(new Perfil(resultSet.getString(66),resultSet.getString(67),resultSet.getString(68)));
                usRegManual.setCorreo(resultSet.getString(69));
                usRegManual.setEstado(resultSet.getString(70));
                mvtoCarbon.setUsuarioRegistraManual(usRegManual);
                mvtoCarbon.setCantidadHorasDescargue(resultSet.getString(303));
                mvto_listEquipo.setMvtoCarbon(mvtoCarbon);
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(72));
                CentroOperacion co= new CentroOperacion();
                co.setCodigo(Integer.parseInt(resultSet.getString(74)));
                co.setDescripcion(resultSet.getString(75));
                co.setEstado(resultSet.getString(76));
                asignacionEquipo.setCentroOperacion(co);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(78));
                SolicitudEquipo solicitudEquipo= new SolicitudEquipo();
                solicitudEquipo.setCodigo(resultSet.getString(80));
                CentroOperacion co_se= new CentroOperacion();
                co_se.setCodigo(Integer.parseInt(resultSet.getString(82)));
                co_se.setDescripcion(resultSet.getString(83));
                co_se.setEstado(resultSet.getString(84));
                solicitudEquipo.setCentroOperacion(co_se);
                solicitudEquipo.setFechaSolicitud(resultSet.getString(85));
                Usuario us_se = new Usuario();
                us_se.setCodigo(resultSet.getString(87));
                //us_se.setClave(resultSet.getString(88));
                us_se.setNombres(resultSet.getString(89));
                us_se.setApellidos(resultSet.getString(90));
                us_se.setPerfilUsuario(new Perfil(resultSet.getString(92),resultSet.getString(93),resultSet.getString(94)));
                us_se.setCorreo(resultSet.getString(95));
                us_se.setEstado(resultSet.getString(96));
                solicitudEquipo.setUsuarioRealizaSolicitud(us_se);
                solicitudEquipo.setFechaRegistro(resultSet.getString(97));
                EstadoSolicitudEquipos estadoSolicitudEquipos = new EstadoSolicitudEquipos();
                estadoSolicitudEquipos.setCodigo(resultSet.getString(99));
                estadoSolicitudEquipos.setDescripcion(resultSet.getString(100));
                estadoSolicitudEquipos.setEstado(resultSet.getString(101));
                solicitudEquipo.setEstadoSolicitudEquipo(estadoSolicitudEquipos);
                solicitudEquipo.setFechaConfirmacion(resultSet.getString(102));
                Usuario us_se_confirm = new Usuario();
                us_se_confirm.setCodigo(resultSet.getString(104));
                //us_se_confirm.setClave(resultSet.getString(105));
                us_se_confirm.setNombres(resultSet.getString(106));
                us_se_confirm.setApellidos(resultSet.getString(107));
                us_se_confirm.setPerfilUsuario(new Perfil(resultSet.getString(109),resultSet.getString(110),resultSet.getString(111)));
                us_se_confirm.setCorreo(resultSet.getString(112));
                us_se_confirm.setEstado(resultSet.getString(113));
                solicitudEquipo.setUsuarioConfirmacionSolicitud(us_se_confirm);
                ConfirmacionSolicitudEquipos confirmacionSolicitudEquipos = new ConfirmacionSolicitudEquipos();
                confirmacionSolicitudEquipos.setCodigo(resultSet.getString(115));
                confirmacionSolicitudEquipos.setDescripcion(resultSet.getString(116));
                confirmacionSolicitudEquipos.setEstado(resultSet.getString(117));
                solicitudEquipo.setConfirmacionSolicitudEquipo(confirmacionSolicitudEquipos);
                solicitudListadoEquipo.setSolicitudEquipo(solicitudEquipo);
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(119));
                tipoEquipo.setDescripcion(resultSet.getString(120));
                tipoEquipo.setEstado(resultSet.getString(121));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(122));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(123));
                solicitudListadoEquipo.setCantidad(Integer.parseInt(resultSet.getString(124)));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(125));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(126));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(127));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(128));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(130));
                laborRealizada.setDescripcion(resultSet.getString(131));
                laborRealizada.setEstado(resultSet.getString(132));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave = new Motonave();
                motonave.setCodigo(resultSet.getString(134));
                motonave.setDescripcion(resultSet.getString(135));
                motonave.setEstado(resultSet.getString(136));
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(140));
                centroCostoSubCentro.setDescripcion(resultSet.getString(141));
                centroCostoSubCentro.setEstado(resultSet.getString(142));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(138));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(143));
                centroCostoAuxiliar.setEstado(resultSet.getString(144));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compania = new Compañia();
                compania.setCodigo(resultSet.getString(146));
                compania.setDescripcion(resultSet.getString(147));
                compania.setEstado(resultSet.getString(148));
                solicitudListadoEquipo.setCompañia(compania);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(149));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(150));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(151));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(152));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(154));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(156),resultSet.getString(157),resultSet.getString(158)));
                equipo.setCodigo_barra(resultSet.getString(159));
                equipo.setReferencia(resultSet.getString(160));
                equipo.setProducto(resultSet.getString(161));
                equipo.setCapacidad(resultSet.getString(162));
                equipo.setMarca(resultSet.getString(163));
                equipo.setModelo(resultSet.getString(164));
                equipo.setSerial(resultSet.getString(165));
                equipo.setDescripcion(resultSet.getString(166));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(168),resultSet.getString(169),resultSet.getString(170)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(172),resultSet.getString(173),resultSet.getString(174)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(176),resultSet.getString(177),resultSet.getString(178),resultSet.getString(179)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(181),resultSet.getString(182),resultSet.getString(183)));
                equipo.setObservacion(resultSet.getString(184));
                equipo.setEstado(resultSet.getString(185));
                equipo.setActivoFijo_codigo(resultSet.getString(186));
                equipo.setActivoFijo_referencia(resultSet.getString(187));
                equipo.setActivoFijo_descripcion(resultSet.getString(188));
                asignacionEquipo.setEquipo(equipo);
                asignacionEquipo.setPertenencia(new Pertenencia(resultSet.getString(190),resultSet.getString(191),resultSet.getString(192)));
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(193));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(194));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(195));
                asignacionEquipo.setEstado(resultSet.getString(196));
                mvto_listEquipo.setAsignacionEquipo(asignacionEquipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(198));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaRegistro(resultSet.getString(200));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(202),resultSet.getString(203),resultSet.getString(204),resultSet.getString(205)));
                mvtoEquipo.setNumeroOrden(resultSet.getString(206));
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setCodigo(resultSet.getInt(210));
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(211));
                centroCostoSubCentro_mvtoEquipo.setEstado(resultSet.getString(212));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setCodigo(resultSet.getString(208));
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(213));
                centroCostoAuxiliar_mvtoEquipo.setEstado(resultSet.getString(214));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setCodigo(resultSet.getString(216));
                laborRealizadaT.setDescripcion(resultSet.getString(217));
                laborRealizadaT.setEstado(resultSet.getString(218));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente(resultSet.getString(220),resultSet.getString(221),resultSet.getString(222)));
                mvtoEquipo.setArticulo(new Articulo(resultSet.getString(224),resultSet.getString(225),resultSet.getString(226)));
                mvtoEquipo.getCliente().setBaseDatos(new BaseDatos(resultSet.getString(312)));
                mvtoEquipo.getArticulo().setBaseDatos(new BaseDatos(resultSet.getString(313)));
                mvtoEquipo.setClienteBaseDatos(resultSet.getString(309));
                mvtoEquipo.setArticuloBaseDatos(resultSet.getString(310));
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(227));
                mvtoEquipo.setFechaHoraFin(resultSet.getString(228));
                mvtoEquipo.setTotalMinutos(resultSet.getString(229));
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(230));
                Recobro recobro = new Recobro();
                recobro.setCodigo(resultSet.getString(233));
                recobro.setDescripcion(resultSet.getString(234));
                recobro.setEstado(resultSet.getString(235));
                mvtoEquipo.setRecobro(recobro);
                ClienteRecobro ClienteRecobro = new ClienteRecobro();
                ClienteRecobro.setCodigo(resultSet.getString(235));
                Cliente cliente_recobro = new Cliente();
                cliente_recobro.setCodigo(resultSet.getString(239));
                cliente_recobro.setDescripcion(resultSet.getString(240));
                cliente_recobro.setEstado(resultSet.getString(241));
                ClienteRecobro.setCliente(cliente_recobro);
                Usuario usuario_recobre = new Usuario();
                usuario_recobre.setCodigo(resultSet.getString(243));
                //usuario_recobre.setClave(resultSet.getString(244));
                usuario_recobre.setNombres(resultSet.getString(245));
                usuario_recobre.setApellidos(resultSet.getString(246));
                usuario_recobre.setPerfilUsuario(new Perfil(resultSet.getString(248),resultSet.getString(249),resultSet.getString(250)));
                usuario_recobre.setCorreo(resultSet.getString(251));
                usuario_recobre.setEstado(resultSet.getString(252));
                ClienteRecobro.setUsuario(usuario_recobre);
                ClienteRecobro.setValorRecobro(resultSet.getString(253));
                ClienteRecobro.setFechaRegistro(resultSet.getString(254));
                mvtoEquipo.setClienteRecobro(ClienteRecobro);
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(255));
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setCodigo(resultSet.getString(257));
                //usuario_me_registra.setClave(resultSet.getString(258));
                usuario_me_registra.setNombres(resultSet.getString(259));
                usuario_me_registra.setApellidos(resultSet.getString(260));
                usuario_me_registra.setPerfilUsuario(new Perfil(resultSet.getString(262),resultSet.getString(263),resultSet.getString(264)));
                usuario_me_registra.setCorreo(resultSet.getString(265));
                usuario_me_registra.setEstado(resultSet.getString(266));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                Usuario usuario_me_autoriza = new Usuario();
                usuario_me_autoriza.setCodigo(resultSet.getString(268));
                //usuario_me_autoriza.setClave(resultSet.getString(269));
                usuario_me_autoriza.setNombres(resultSet.getString(270));
                usuario_me_autoriza.setApellidos(resultSet.getString(271));
                usuario_me_autoriza.setPerfilUsuario(new Perfil(resultSet.getString(273),resultSet.getString(274),resultSet.getString(275)));
                usuario_me_autoriza.setCorreo(resultSet.getString(276));
                usuario_me_autoriza.setEstado(resultSet.getString(277));
                mvtoEquipo.setUsuarioAutorizaRecobro(usuario_me_autoriza);
                AutorizacionRecobro autorizacionRecobro = new AutorizacionRecobro();
                autorizacionRecobro.setCodigo(resultSet.getString(279));
                autorizacionRecobro.setDescripcion(resultSet.getString(280));
                autorizacionRecobro.setEstado(resultSet.getString(281));
                mvtoEquipo.setAutorizacionRecobro(autorizacionRecobro);
                mvtoEquipo.setObservacionAutorizacion(resultSet.getString(282));
                mvtoEquipo.setInactividad(resultSet.getString(283));
                CausaInactividad causaInactividad = new CausaInactividad();
                causaInactividad.setCodigo(resultSet.getString(285));
                causaInactividad.setDescripcion(resultSet.getString(286));
                causaInactividad.setEstado(resultSet.getString(287));
                mvtoEquipo.setCausaInactividad(causaInactividad);
                Usuario usuario_me_us_inactividad = new Usuario();
                usuario_me_us_inactividad.setCodigo(resultSet.getString(289));
                //usuario_me_us_inactividad.setClave(resultSet.getString(290));
                usuario_me_us_inactividad.setNombres(resultSet.getString(291));
                usuario_me_us_inactividad.setApellidos(resultSet.getString(292));
                usuario_me_us_inactividad.setPerfilUsuario(new Perfil(resultSet.getString(294),resultSet.getString(295),resultSet.getString(296)));
                usuario_me_us_inactividad.setCorreo(resultSet.getString(297));
                usuario_me_us_inactividad.setEstado(resultSet.getString(298));
                mvtoEquipo.setUsuarioInactividad(usuario_me_us_inactividad);
                MotivoParada motivoParada= new MotivoParada();
                motivoParada.setCodigo(resultSet.getString(300));
                motivoParada.setDescripcion(resultSet.getString(301));
                motivoParada.setEstado(resultSet.getString(302));
                mvtoEquipo.setMotivoParada(motivoParada);
                mvtoEquipo.setObservacionMvtoEquipo(resultSet.getString(303));
                mvtoEquipo.setEstado(resultSet.getString(304));
                mvtoEquipo.setDesdeCarbon(resultSet.getString(305));
                mvtoEquipo.setTotalMinutos(resultSet.getString(307));
                mvto_listEquipo.setMvtoEquipo(mvtoEquipo);
                mvto_listEquipo.setEstado(resultSet.getString(306));
                listadoObjetos.add(mvto_listEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }

    //Busquedas de MVTO_CARBON
    public ArrayList<MvtoCarbon> buscarMtvo_CarbonEnTransito(ZonaTrabajo zonaTrabajo) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1\n" +
                    "      ,[mc_cntro_oper_cdgo]--2\n" +
                    "-- Centro Operacion\n" +
                    ",[co_cdgo]--3\n" +
                    ",[co_desc]--4\n" +
                    ",[co_estad]--5\n" +
                    "      ,[mc_cntro_cost_auxiliar_cdgo]--6\n" +
                    "--Auxiliar Centro de Costo\n" +
                    ",cca_origen.[cca_cdgo]--7\n" +
                    ",cca_origen.[cca_cntro_cost_subcentro_cdgo]--8\n" +
                    "--Subcentro de Costo\n" +
                    ",[ccs_cdgo]--9\n" +
                    ",[ccs_desc]--10\n" +
                    ",[ccs_estad]--11\n" +
                    ",cca_origen.[cca_desc]--12\n" +
                    ",cca_origen.[cca_estad]--13\n" +
                    "      ,[mc_articulo_cdgo]--14\n" +
                    "  --Articulo\n" +
                    "  ,[ar_cdgo]--15\n" +
                    "  ,[ar_desc]--16\n" +
                    "  ,[ar_estad]--17\n" +
                    "      ,[mc_cliente_cdgo]--18\n" +
                    "--Cliente\n" +
                    ",[cl_cdgo]--19\n" +
                    ",[cl_desc]--20\n" +
                    ",[cl_estad]--21\n" +
                    "      ,[mc_transprtdora_cdgo]--22\n" +
                    "--Transportadora\n" +
                    "  ,[tr_cdgo]--23\n" +
                    "  ,[tr_nit]--24\n" +
                    "  ,[tr_desc]--25\n" +
                    "  ,[tr_observ]--26\n" +
                    "  ,[tr_estad]--27\n" +
                    "      ,[mc_fecha]--28\n" +
                    "      ,[mc_num_orden]--29\n" +
                    "      ,[mc_deposito]--30\n" +
                    "      ,[mc_consecutivo_tqute]--31\n" +
                    "      ,[mc_placa_vehiculo]--32\n" +
                    "      ,[mc_peso_vacio]--33\n" +
                    "      ,[mc_peso_lleno]--34\n" +
                    "      ,[mc_peso_neto]--35\n" +
                    "      ,[mc_fecha_entrad]--36\n" +
                    "      ,[mc_fecha_salid]--37\n" +
                    "      ,[mc_fecha_inicio_descargue]--38\n" +
                    "      ,[mc_fecha_fin_descargue]--39\n" +
                    "      ,[mc_usuario_cdgo]--40\n" +
                    "      --Usuario Quien registra desde APP\n" +
                    "  ,us_registro_app.[us_cdgo] AS us_registro_app_cdgo--41\n" +
                    "  --,us_registro_app.[us_clave] AS us_registro_app_clave\n" +
                    "  ,us_registro_app.[us_nombres] AS us_registro_app_nombres--42\n" +
                    "  ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--43\n" +
                    "  ,us_registro_app.[us_perfil_cdgo] AS us_registro_app_perfil--44\n" +
                    "--Perfil Usuario Registra desde APP\n" +
                    ",prf_us_registro_app.[prf_cdgo] AS prf_us_registro_app_cdgo--45\n" +
                    ",prf_us_registro_app.[prf_desc] AS prf_us_registro_app_desc--46\n" +
                    ",prf_us_registro_app.[prf_estad] AS prf_us_registro_app_estad--47\n" +
                    "  ,us_registro_app.[us_correo] AS us_registro_app_correo--48\n" +
                    "  ,us_registro_app.[us_estad] AS us_registro_app_estad--49\n" +
                    "      ,[mc_observ]--50\n" +
                    "      ,[mc_estad_mvto_carbon_cdgo]--51\n" +
                    "  ,[emc_cdgo]--52\n" +
                    "  ,[emc_desc]--53\n" +
                    "  ,[emc_estad]--54\n" +
                    "      ,[mc_conexion_peso_ccarga]--55\n" +
                    "      ,[mc_registro_manual]--56\n" +
                    "      ,[mc_usuario_registro_manual_cdgo]--57\n" +
                    "  --Usuario Quien Registra Manual\n" +
                    "  ,us_registro_manual.[us_cdgo] AS us_registro_manual_cdgo--58\n" +
                    "  --,us_registro_manual.[us_clave] AS us_registro_manual_clave\n" +
                    "  ,us_registro_manual.[us_nombres] AS us_registro_manual_nombres--59\n" +
                    "  ,us_registro_manual.[us_apellidos] AS us_registro_manual_apellidos--60\n" +
                    "  ,us_registro_manual.[us_perfil_cdgo] AS us_registro_manual_perfil_cdgo--61\n" +
                    "--Perfil Usuario Registra Manual\n" +
                    ",prf_us_registro_manual.[prf_cdgo] AS prf_us_registro_manual_cdgo--62\n" +
                    ",prf_us_registro_manual.[prf_desc] AS prf_us_registro_manual_desc--63\n" +
                    ",prf_us_registro_manual.[prf_estad] AS prf_us_registro_manual_estad--64\n" +
                    "  ,us_registro_manual.[us_correo] AS us_registro_manual_correo--65\n" +
                    "  ,us_registro_manual.[us_estad] AS us_registro_manual_estad--66\n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--67 \n" +
                    "        ,cca_destino.[cca_desc]--68 \n" +
                    "        ,[lr_cdgo]--69 \n" +
                    "        ,[lr_desc]--70 \n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon] \n" +
                    "INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "        INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_origen ON [mc_cntro_cost_auxiliar_cdgo]= cca_origen.[cca_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo] AND [cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_app ON  us_registro_app.[us_perfil_cdgo] =prf_us_registro_app.[prf_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_manual ON [mc_usuario_registro_manual_cdgo]=us_registro_manual.[us_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_manual ON  us_registro_manual.[us_perfil_cdgo] =prf_us_registro_manual.[prf_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] \n" +
                    "INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [mc_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "  WHERE [mc_fecha_fin_descargue] IS NULL \n" +
                    //"AND [mc_estad_mvto_carbon_cdgo]=1  ORDER BY mc_placa_vehiculo ASC");
                    "AND [mc_estad_mvto_carbon_cdgo]=1  AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" ORDER BY [mc_cdgo] DESC");
            //String d= ""+placa+"%";
            //queryBuscar.setString(1, d);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(3));
                centroOperacion.setDescripcion(resultSetBuscar.getString(4));
                centroOperacion.setEstado(resultSetBuscar.getString(5));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(9));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(10));
                centroCostoSubCentro.setEstado(resultSetBuscar.getString(11));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(7));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(12));
                centroCostoAuxiliar.setEstado(resultSetBuscar.getString(13));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(15),resultSetBuscar.getString(16),resultSetBuscar.getString(17)));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(19),resultSetBuscar.getString(20),resultSetBuscar.getString(21)));
                Objeto.setTransportadora(new Transportadora(resultSetBuscar.getString(23),resultSetBuscar.getString(24),
                        resultSetBuscar.getString(25),resultSetBuscar.getString(26),
                        resultSetBuscar.getString(27)));
                Objeto.setFechaRegistro(resultSetBuscar.getString(28));
                Objeto.setNumero_orden(resultSetBuscar.getString(29));
                Objeto.setDeposito(resultSetBuscar.getString(30));
                Objeto.setConsecutivo(resultSetBuscar.getString(31));
                Objeto.setPlaca(resultSetBuscar.getString(32));
                Objeto.setPesoVacio(resultSetBuscar.getString(33));
                Objeto.setPesoLleno(resultSetBuscar.getString(34));
                Objeto.setPesoNeto(resultSetBuscar.getString(35));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(36));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(37));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(38));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(39));
                Perfil prf_Us_registroApp = new Perfil();
                prf_Us_registroApp.setCodigo(resultSetBuscar.getString(45));
                prf_Us_registroApp.setDescripcion(resultSetBuscar.getString(46));
                prf_Us_registroApp.setEstado(resultSetBuscar.getString(47));
                Usuario usuario = new Usuario();
                usuario.setCodigo(resultSetBuscar.getString(41));
                usuario.setNombres(resultSetBuscar.getString(42));
                usuario.setApellidos(resultSetBuscar.getString(43));
                usuario.setPerfilUsuario(prf_Us_registroApp);
                usuario.setCorreo(resultSetBuscar.getString(48));
                usuario.setEstado(resultSetBuscar.getString(49));
                Objeto.setUsuarioRegistroMovil(usuario);
                Objeto.setObservacion(resultSetBuscar.getString(50));
                EstadoMvtoCarbon estadoMvtoCarbon= new EstadoMvtoCarbon(resultSetBuscar.getString(52),resultSetBuscar.getString(53),resultSetBuscar.getString(54));
                Objeto.setEstadoMvtoCarbon(estadoMvtoCarbon);
                Objeto.setConexionPesoCcarga(resultSetBuscar.getString(55));

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSetBuscar.getString(67));
                centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(68));
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSetBuscar.getString(69));
                laborRealizada.setDescripcion(resultSetBuscar.getString(70));
                Objeto.setLaborRealizada(laborRealizada);

                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }



    public ArrayList<MvtoCarbon> reporteCarbon_VehiculosTrabajado(Usuario us, String fechaInicio, String fechaFinal) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        if((fechaInicio.equals("")) && (fechaFinal.equals(""))){
            fechaInicio="(SELECT CONCAT (CONVERT (date, GETDATE()),' 00:00:00.0'))";
            fechaFinal="(SELECT CONCAT (CONVERT (date, GETDATE()),' 23:59:59.999'))";
        }else{
            fechaInicio= "'"+fechaInicio+"'";
            fechaFinal= "'"+fechaFinal+"'";
        }

        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo] FROM ["+DB+"].[dbo].[mvto_carbon] WHERE  [mc_fecha] BETWEEN "+fechaInicio+" AND "+fechaFinal+" AND [mc_estad_mvto_carbon_cdgo]=1  AND [mc_usuario_cdgo]=? ORDER BY [mc_cdgo] DESC");
            queryBuscar.setString(1,us.getCodigo());
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                PreparedStatement buscarListado = conexion.prepareStatement("SELECT  [mcle_cdgo]--1\n" +
                        "      ,[mcle_mvto_carbon_cdgo]--2\n" +
                        "\t\t\t  ,[mc_cdgo]--3\n" +
                        "\t\t\t  ,[mc_cntro_oper_cdgo]--4\n" +
                        "              ,[co_desc] --5\n" +
                        "\t\t\t  ,[mc_cntro_cost_auxiliar_cdgo] --6\n" +
                        "                    ,mc_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] --7\n" +
                        "                   \t\t,mc_cntro_cost_subcentro.[ccs_desc]  --8\n" +
                        "                    ,mc_cntro_cost_auxiliar.[cca_desc] --9\n" +
                        "\t\t\t,[mc_labor_realizada_cdgo]--10\n" +
                        "\t\t\t\t,[lr_desc]--11\n" +
                        "\t\t\t,[mc_articulo_cdgo] --12\n" +
                        "\t\t\t\t,mc_articulo.[ar_desc] --13\n" +
                        "\t\t\t,[mc_cliente_cdgo]--14\n" +
                        "\t\t\t\t,mc_cliente.[cl_desc] --15\n" +
                        "\t\t\t,[mc_transprtdora_cdgo] --16\n" +
                        "\t\t\t\t,[tr_desc] --17\n" +
                        "\t\t\t,[mc_num_orden]--18\n" +
                        "\t\t\t,[mc_deposito]--19\n" +
                        "\t\t\t,[mc_consecutivo_tqute]--20\n" +
                        "\t\t\t,[mc_placa_vehiculo]--21\n" +
                        "\t\t\t,[mc_peso_neto]--22\n" +
                        "\t\t\t,[mc_fecha_inicio_descargue]--23\n" +
                        "\t\t\t,[mc_fecha_fin_descargue]--24\n" +
                        "\t\t\t,[mc_usuario_cdgo]--25\n" +
                        "\t\t\t\t,ms_usuario_inicia.[us_nombres]--26\n" +
                        "\t\t\t\t,ms_usuario_inicia.[us_apellidos]--27\n" +
                        "\t\t\t,[mc_observ]--28\n" +
                        "\t\t\t,[mc_cntro_cost_auxiliarDestino_cdgo]--29\n" +
                        "                  ,mc_cntro_cost_auxiliarDestino.[cca_desc] --30\n" +
                        "\t\t\t,CASE [mc_lavado_vehiculo] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [mc_lavado_vehiculo] --31\n" +
                        "\t\t\t,[mc_lavado_vehiculo_observacion]--32\n" +
                        "\t\t\t,[mc_motivo_nolavado_vehiculo_cdgo]--33\n" +
                        "\t\t\t\t,[mnlv_desc]--34\n" +
                        "\t\t\t\t,[mnlv_estad]--35\n" +
                        "\t\t\t,[mc_equipo_lavado_cdgo]--36\n" +
                        "\t\t\t\t,mvtoCarbon_equip_lavado.[eq_desc]--37\n" +
                        "\t\t\t\t,mvtoCarbon_equip_lavado.[eq_modelo]--38\n" +
                        "\t\t\t,[mc_usuario_cierre_cdgo]--39\n" +
                        "\t\t\t\t,ms_usuario_cierra.[us_nombres]--40\n" +
                        "\t\t\t\t,ms_usuario_cierra.[us_apellidos]--41\n" +
                        "\t\t\t,[mc_costoLavadoVehiculo]--42\n" +
                        "\t\t\t,[mc_valorRecaudoEmpresa_lavadoVehiculo]--43\n" +
                        "\t\t\t,[mc_valorRecaudoEquipo_lavadoVehiculo]--44\n" +
                        "    ,[mcle_asignacion_equipo_cdgo]--45\n" +
                        "\t\t,[ae_equipo_cdgo]--46\n" +
                        "\t\t,equipoAsignacion.[eq_desc]--47\n" +
                        "\t\t,equipoAsignacion.[eq_modelo]--48\n" +
                        "      ,[mcle_mvto_equipo_cdgo]--49\n" +
                        "      ,[mcle_estado]--50\n" +
                        "       ,[zt_cdgo] --51\n" +
                        "      ,[zt_desc]--52\n" +
                        "  FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[mvto_carbon] ON [mc_cdgo]=[mcle_mvto_carbon_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliar ON [mc_cntro_cost_auxiliar_cdgo]=mc_cntro_cost_auxiliar.[cca_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliarDestino ON [mc_cntro_cost_auxiliarDestino_cdgo]=mc_cntro_cost_auxiliarDestino.[cca_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] mc_cntro_cost_subcentro ON mc_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]= mc_cntro_cost_subcentro.[ccs_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[articulo] mc_articulo ON [mc_articulo_cdgo]=mc_articulo.[ar_cdgo] AND mc_articulo.[ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[cliente] mc_cliente ON [mc_cliente_cdgo]=mc_cliente.[cl_cdgo] AND mc_cliente.[cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[usuario] ms_usuario_inicia ON [mc_usuario_cdgo]=ms_usuario_inicia.[us_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[usuario] ms_usuario_cierra ON [mc_usuario_cierre_cdgo]=ms_usuario_cierra.[us_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[motivo_nolavado_vehiculo] ON [mc_motivo_nolavado_vehiculo_cdgo]=[mnlv_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                        "  LEFT JOIN ["+DB+"].[dbo].[equipo] mvtoCarbon_equip_lavado    ON [mc_equipo_lavado_cdgo]= mvtoCarbon_equip_lavado.[eq_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[asignacion_equipo] ON [mcle_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[equipo] equipoAsignacion ON equipoAsignacion.[eq_cdgo]=[ae_equipo_cdgo]" +
                        "  INNER JOIN ["+DB+"].[dbo].[mvto_equipo] ON [mcle_mvto_equipo_cdgo]=[me_cdgo] " +
                        "  INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [lzt_cntro_cost_auxiliar_cdgo]=[mc_cntro_cost_auxiliar_cdgo] " +
                        "  INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]=[zt_cdgo]" +
                        " WHERE  [mc_cdgo] =? AND [me_estado]= 1 ORDER BY [mc_cdgo] DESC");

                buscarListado.setString(1,resultSetBuscar.getString(1));
                ResultSet resultSetBuscarListado= buscarListado.executeQuery();
                boolean validador= true;
                ArrayList<MvtoCarbon_ListadoEquipos> listadoMvtoCarbon_listadoEquipos= null;
                while (resultSetBuscarListado.next()) {
                    if(validador){//Ejecutamos este condicional una única vez
                        listadoMvtoCarbon_listadoEquipos= new ArrayList<>();
                        validador=false;
                            CentroOperacion centroOperacion= new CentroOperacion();
                                centroOperacion.setCodigo(resultSetBuscarListado.getInt(4));
                                centroOperacion.setDescripcion(resultSetBuscarListado.getString(5));
                        Objeto.setCentroOperacion(centroOperacion);
                            CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                                centroCostoSubCentro.setCodigo(resultSetBuscarListado.getInt(7));
                                centroCostoSubCentro.setDescripcion(resultSetBuscarListado.getString(8));

                            CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                                centroCostoAuxiliar.setCodigo(resultSetBuscarListado.getString(6));
                                centroCostoAuxiliar.setDescripcion(resultSetBuscarListado.getString(9));

                            centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                        Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                        Objeto.setArticulo(new Articulo(resultSetBuscarListado.getString(12),resultSetBuscarListado.getString(13),""));
                        Objeto.setCliente(new Cliente(resultSetBuscarListado.getString(14),resultSetBuscarListado.getString(15),""));
                            Transportadora transportadora = new Transportadora();
                                transportadora.setCodigo(resultSetBuscarListado.getString(16));
                                transportadora.setDescripcion(resultSetBuscarListado.getString(17));
                        Objeto.setTransportadora(transportadora);
                        Objeto.setNumero_orden(resultSetBuscarListado.getString(18));
                        Objeto.setDeposito(resultSetBuscarListado.getString(19));
                        Objeto.setConsecutivo(resultSetBuscarListado.getString(20));
                        Objeto.setPlaca(resultSetBuscarListado.getString(21));
                        Objeto.setPesoNeto(resultSetBuscarListado.getString(22));
                        Objeto.setFechaInicioDescargue(resultSetBuscarListado.getString(23));
                        Objeto.setFechaFinDescargue(resultSetBuscarListado.getString(24));
                            Usuario usuarioInicia = new Usuario();
                                usuarioInicia.setCodigo(resultSetBuscarListado.getString(25));
                                usuarioInicia.setNombres(resultSetBuscarListado.getString(26));
                                usuarioInicia.setApellidos(resultSetBuscarListado.getString(27));
                        Objeto.setUsuarioRegistroMovil(usuarioInicia);
                        Objeto.setObservacion(resultSetBuscarListado.getString(28));
                            CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                                if(resultSetBuscarListado.getString(28) != null){
                                    centroCostoAuxiliarDestino.setCodigo(resultSetBuscarListado.getString(29));
                                    centroCostoAuxiliarDestino.setDescripcion(resultSetBuscarListado.getString(30));
                                }else{
                                    centroCostoAuxiliarDestino.setCodigo(resultSetBuscarListado.getString(""));
                                    centroCostoAuxiliarDestino.setDescripcion(resultSetBuscarListado.getString(""));
                                }
                        Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                            LaborRealizada laborRealizada = new LaborRealizada();
                                laborRealizada.setCodigo(resultSetBuscarListado.getString(10));
                                laborRealizada.setDescripcion(resultSetBuscarListado.getString(11));
                        Objeto.setLaborRealizada(laborRealizada);
                        Objeto.setLavadoVehiculo(resultSetBuscarListado.getString(31));
                        Objeto.setLavadorVehiculoObservacion(resultSetBuscarListado.getString(32));
                        MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                if(resultSetBuscarListado.getString(33) !=null){
                                    motivoNoLavado.setCodigo(resultSetBuscarListado.getString(33));
                                    motivoNoLavado.setDescripcion(resultSetBuscarListado.getString(34));
                                }else{
                                    motivoNoLavado.setCodigo("");
                                    motivoNoLavado.setDescripcion("");
                                }
                        Objeto.setMotivoNoLavado(motivoNoLavado);
                            Equipo equipoLavado = new Equipo();
                            if(resultSetBuscarListado.getString(36) !=null){
                                equipoLavado.setCodigo(resultSetBuscarListado.getString(36));
                                equipoLavado.setDescripcion(resultSetBuscarListado.getString(37));
                                equipoLavado.setModelo(resultSetBuscarListado.getString(38));
                            }else{
                                equipoLavado.setCodigo("");
                                equipoLavado.setDescripcion("");
                                equipoLavado.setModelo("");
                            }
                        Objeto.setEquipoLavadoVehiculo(equipoLavado);
                        Usuario usuarioCierra = new Usuario();
                        if(resultSetBuscarListado.getString(39) !=null){
                            usuarioCierra.setCodigo(resultSetBuscarListado.getString(39));
                            usuarioCierra.setNombres(resultSetBuscarListado.getString(40));
                            usuarioCierra.setApellidos(resultSetBuscarListado.getString(41));
                        }else{
                            usuarioCierra.setCodigo("");
                            usuarioCierra.setNombres("");
                            usuarioCierra.setApellidos("");
                        }
                        Objeto.setUsuarioQuienCierra(usuarioCierra);
                        Objeto.setCostoLavadoVehiculo(resultSetBuscarListado.getString(42));
                        Objeto.setValorRecaudoEmpresa(resultSetBuscarListado.getString(43));
                        Objeto.setValorRecaudoEquipo(resultSetBuscarListado.getString(44));
                            ZonaTrabajo zonaTrabajo = new ZonaTrabajo();
                                zonaTrabajo.setCodigo(resultSetBuscarListado.getString(51));
                                zonaTrabajo.setDescripcion(resultSetBuscarListado.getString(52));
                        Objeto.setZonaTrabajo(zonaTrabajo);
                    }
                    MvtoCarbon_ListadoEquipos mvtoCarbon_listadoEquipos = new MvtoCarbon_ListadoEquipos();
                    Equipo equipoAsignacion = new Equipo();
                    equipoAsignacion.setCodigo(resultSetBuscarListado.getString(46));
                    equipoAsignacion.setDescripcion(resultSetBuscarListado.getString(47));
                    equipoAsignacion.setModelo(resultSetBuscarListado.getString(48));
                    AsignacionEquipo asignacionEquipo= new AsignacionEquipo();
                    mvtoCarbon_listadoEquipos.setAsignacionEquipo(asignacionEquipo);
                    mvtoCarbon_listadoEquipos.getAsignacionEquipo().setEquipo(equipoAsignacion);
                    listadoMvtoCarbon_listadoEquipos.add(mvtoCarbon_listadoEquipos);
                }
                Objeto.setListadoMvtoCarbon_ListadoEquipos(listadoMvtoCarbon_listadoEquipos);
                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }



    public ArrayList<MvtoCarbon> buscarMtvo_CarbonEnTransito(String placa, ZonaTrabajo zonaTrabajo) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1\n" +
                    "      ,[mc_cntro_oper_cdgo]--2\n" +
                    "-- Centro Operacion\n" +
                    ",[co_cdgo]--3\n" +
                    ",[co_desc]--4\n" +
                    ",[co_estad]--5\n" +
                    "      ,[mc_cntro_cost_auxiliar_cdgo]--6\n" +
                    "--Auxiliar Centro de Costo\n" +
                    ",cca_origen.[cca_cdgo]--7\n" +
                    ",cca_origen.[cca_cntro_cost_subcentro_cdgo]--8\n" +
                    "--Subcentro de Costo\n" +
                    ",[ccs_cdgo]--9\n" +
                    ",[ccs_desc]--10\n" +
                    ",[ccs_estad]--11\n" +
                    ",cca_origen.[cca_desc]--12\n" +
                    ",cca_origen.[cca_estad]--13\n" +
                    "      ,[mc_articulo_cdgo]--14\n" +
                    "  --Articulo\n" +
                    "  ,[ar_cdgo]--15\n" +
                    "  ,[ar_desc]--16\n" +
                    "  ,[ar_estad]--17\n" +
                    "      ,[mc_cliente_cdgo]--18\n" +
                    "--Cliente\n" +
                    ",[cl_cdgo]--19\n" +
                    ",[cl_desc]--20\n" +
                    ",[cl_estad]--21\n" +
                    "      ,[mc_transprtdora_cdgo]--22\n" +
                    "--Transportadora\n" +
                    "  ,[tr_cdgo]--23\n" +
                    "  ,[tr_nit]--24\n" +
                    "  ,[tr_desc]--25\n" +
                    "  ,[tr_observ]--26\n" +
                    "  ,[tr_estad]--27\n" +
                    "      ,[mc_fecha]--28\n" +
                    "      ,[mc_num_orden]--29\n" +
                    "      ,[mc_deposito]--30\n" +
                    "      ,[mc_consecutivo_tqute]--31\n" +
                    "      ,[mc_placa_vehiculo]--32\n" +
                    "      ,[mc_peso_vacio]--33\n" +
                    "      ,[mc_peso_lleno]--34\n" +
                    "      ,[mc_peso_neto]--35\n" +
                    "      ,[mc_fecha_entrad]--36\n" +
                    "      ,[mc_fecha_salid]--37\n" +
                    "      ,[mc_fecha_inicio_descargue]--38\n" +
                    "      ,[mc_fecha_fin_descargue]--39\n" +
                    "      ,[mc_usuario_cdgo]--40\n" +
                    "      --Usuario Quien registra desde APP\n" +
                    "  ,us_registro_app.[us_cdgo] AS us_registro_app_cdgo--41\n" +
                    "  --,us_registro_app.[us_clave] AS us_registro_app_clave\n" +
                    "  ,us_registro_app.[us_nombres] AS us_registro_app_nombres--42\n" +
                    "  ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--43\n" +
                    "  ,us_registro_app.[us_perfil_cdgo] AS us_registro_app_perfil--44\n" +
                    "--Perfil Usuario Registra desde APP\n" +
                    ",prf_us_registro_app.[prf_cdgo] AS prf_us_registro_app_cdgo--45\n" +
                    ",prf_us_registro_app.[prf_desc] AS prf_us_registro_app_desc--46\n" +
                    ",prf_us_registro_app.[prf_estad] AS prf_us_registro_app_estad--47\n" +
                    "  ,us_registro_app.[us_correo] AS us_registro_app_correo--48\n" +
                    "  ,us_registro_app.[us_estad] AS us_registro_app_estad--49\n" +
                    "      ,[mc_observ]--50\n" +
                    "      ,[mc_estad_mvto_carbon_cdgo]--51\n" +
                    "  ,[emc_cdgo]--52\n" +
                    "  ,[emc_desc]--53\n" +
                    "  ,[emc_estad]--54\n" +
                    "      ,[mc_conexion_peso_ccarga]--55\n" +
                    "      ,[mc_registro_manual]--56\n" +
                    "      ,[mc_usuario_registro_manual_cdgo]--57\n" +
                    "  --Usuario Quien Registra Manual\n" +
                    "  ,us_registro_manual.[us_cdgo] AS us_registro_manual_cdgo--58\n" +
                    "  --,us_registro_manual.[us_clave] AS us_registro_manual_clave\n" +
                    "  ,us_registro_manual.[us_nombres] AS us_registro_manual_nombres--59\n" +
                    "  ,us_registro_manual.[us_apellidos] AS us_registro_manual_apellidos--60\n" +
                    "  ,us_registro_manual.[us_perfil_cdgo] AS us_registro_manual_perfil_cdgo--61\n" +
                    "--Perfil Usuario Registra Manual\n" +
                    ",prf_us_registro_manual.[prf_cdgo] AS prf_us_registro_manual_cdgo--62\n" +
                    ",prf_us_registro_manual.[prf_desc] AS prf_us_registro_manual_desc--63\n" +
                    ",prf_us_registro_manual.[prf_estad] AS prf_us_registro_manual_estad--64\n" +
                    "  ,us_registro_manual.[us_correo] AS us_registro_manual_correo--65\n" +
                    "  ,us_registro_manual.[us_estad] AS us_registro_manual_estad--66\n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--67 \n" +
                    "        ,cca_destino.[cca_desc]--68 \n" +
                    "        ,[lr_cdgo]--69 \n" +
                    "        ,[lr_desc]--70 \n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon] \n" +
                    " INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_origen ON [mc_cntro_cost_auxiliar_cdgo]= cca_origen.[cca_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[mc_articulo_base_datos_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo] AND [cl_base_datos_cdgo]=[mc_cliente_base_datos_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] AND [tr_base_datos_cdgo]=[mc_transprtdora_base_datos_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_app ON  us_registro_app.[us_perfil_cdgo] =prf_us_registro_app.[prf_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_manual ON [mc_usuario_registro_manual_cdgo]=us_registro_manual.[us_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_manual ON  us_registro_manual.[us_perfil_cdgo] =prf_us_registro_manual.[prf_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] " +
                    "INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [mc_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "  WHERE [mc_fecha_fin_descargue] IS NULL \n" +
                    //"AND [mc_estad_mvto_carbon_cdgo]=1  ORDER BY mc_placa_vehiculo ASC");
                    "AND [mc_estad_mvto_carbon_cdgo]=1 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" AND  [mc_placa_vehiculo] LIKE ? ORDER BY [mc_cdgo] DESC");

            String d= "%"+placa+"%";
            queryBuscar.setString(1, d);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(3));
                centroOperacion.setDescripcion(resultSetBuscar.getString(4));
                centroOperacion.setEstado(resultSetBuscar.getString(5));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(9));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(10));
                centroCostoSubCentro.setEstado(resultSetBuscar.getString(11));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(7));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(12));
                centroCostoAuxiliar.setEstado(resultSetBuscar.getString(13));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(15),resultSetBuscar.getString(16),resultSetBuscar.getString(17)));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(19),resultSetBuscar.getString(20),resultSetBuscar.getString(21)));
                Objeto.setTransportadora(new Transportadora(resultSetBuscar.getString(23),resultSetBuscar.getString(24),
                        resultSetBuscar.getString(25),resultSetBuscar.getString(26),
                        resultSetBuscar.getString(27)));
                Objeto.setFechaRegistro(resultSetBuscar.getString(28));
                Objeto.setNumero_orden(resultSetBuscar.getString(29));
                Objeto.setDeposito(resultSetBuscar.getString(30));
                Objeto.setConsecutivo(resultSetBuscar.getString(31));
                Objeto.setPlaca(resultSetBuscar.getString(32));
                Objeto.setPesoVacio(resultSetBuscar.getString(33));
                Objeto.setPesoLleno(resultSetBuscar.getString(34));
                Objeto.setPesoNeto(resultSetBuscar.getString(35));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(36));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(37));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(38));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(39));
                Perfil prf_Us_registroApp = new Perfil();
                prf_Us_registroApp.setCodigo(resultSetBuscar.getString(45));
                prf_Us_registroApp.setDescripcion(resultSetBuscar.getString(46));
                prf_Us_registroApp.setEstado(resultSetBuscar.getString(47));
                Usuario usuario = new Usuario();
                usuario.setCodigo(resultSetBuscar.getString(41));
                usuario.setNombres(resultSetBuscar.getString(42));
                usuario.setApellidos(resultSetBuscar.getString(43));
                usuario.setPerfilUsuario(prf_Us_registroApp);
                usuario.setCorreo(resultSetBuscar.getString(48));
                usuario.setEstado(resultSetBuscar.getString(49));
                Objeto.setUsuarioRegistroMovil(usuario);
                Objeto.setObservacion(resultSetBuscar.getString(50));
                EstadoMvtoCarbon estadoMvtoCarbon= new EstadoMvtoCarbon(resultSetBuscar.getString(52),resultSetBuscar.getString(53),resultSetBuscar.getString(54));
                Objeto.setEstadoMvtoCarbon(estadoMvtoCarbon);
                Objeto.setConexionPesoCcarga(resultSetBuscar.getString(55));

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSetBuscar.getString(67));
                centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(68));
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSetBuscar.getString(69));
                laborRealizada.setDescripcion(resultSetBuscar.getString(70));
                Objeto.setLaborRealizada(laborRealizada);

                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }


    public ArrayList<MotivoNoLavado> listarMotivoNoLavado_Activos() throws SQLException{
        ArrayList<MotivoNoLavado> ListadoMotivoNoLavado =null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT [mnlv_cdgo]\n" +
                                                    "      ,[mnlv_desc]\n" +
                                                    "      ,[mnlv_estad]\n" +
                                                    "  FROM ["+DB+"].[dbo].[motivo_nolavado_vehiculo] WHERE [mnlv_estad]=1;");
            ResultSet resultSetBuscar=queryBuscar.executeQuery();
            boolean validar=true;
            while(resultSetBuscar.next()){
                if(validar){
                    ListadoMotivoNoLavado = new ArrayList();
                    validar=false;
                }
                MotivoNoLavado objeto = new MotivoNoLavado();
                objeto.setCodigo(resultSetBuscar.getString(1));
                objeto.setDescripcion(resultSetBuscar.getString(2));
                objeto.setEstado(resultSetBuscar.getString(3));
                ListadoMotivoNoLavado.add(objeto);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return ListadoMotivoNoLavado;
    }
    public ArrayList<Recobro> listarRecobro() throws SQLException{
        ArrayList<Recobro> listadoRecobro =null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[recobro] WHERE [rc_cdgo] <> 2;");
            ResultSet resultSetBuscar=queryBuscar.executeQuery();
            boolean validar=true;
            while(resultSetBuscar.next()){
                if(validar){
                    listadoRecobro = new ArrayList();
                    validar=false;
                }
                Recobro rec = new Recobro();
                rec.setCodigo(resultSetBuscar.getString(1));
                rec.setDescripcion(resultSetBuscar.getString(2));
                rec.setEstado(resultSetBuscar.getString(3));
                listadoRecobro.add(rec);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoRecobro; }

    public ArrayList<CentroOperacion> buscarCentroOperacion(String valorConsulta) throws SQLException{
        ArrayList<CentroOperacion> listadoCentroOperacion = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            ResultSet resultSetBuscar;
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT co_cdgo, co_desc, CASE WHEN (co_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS co_estad FROM ["+DB+"].[dbo].[cntro_oper] WHERE [co_estad]=1 AND [co_desc] like ?;");
            queryBuscar.setString(1, "%"+valorConsulta+"%");
            resultSetBuscar=queryBuscar.executeQuery();
            boolean valitador = true;
            while(resultSetBuscar.next()){
                if(valitador){
                    listadoCentroOperacion =new ArrayList<>();
                    valitador=false;
                }
                CentroOperacion co = new CentroOperacion();
                co.setCodigo(resultSetBuscar.getInt(1));
                co.setDescripcion(resultSetBuscar.getString(2));
                co.setEstado(resultSetBuscar.getString(3));
                listadoCentroOperacion.add(co);
            }
        }catch (SQLException sqlException) {
            //JOptionPane.showMessageDialog(null, "Error al tratar de consultar los centros de operaciones");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoCentroOperacion;
    }
    public ArrayList<CentroCostoSubCentro> listarCentroCostoSubCentro(CentroOperacion centroOperacion) throws SQLException{
       ArrayList<CentroCostoSubCentro> listadoObjetos = null;
       Costos_VG control = new Costos_VG(tipoConexion);
       String DB=control.getBaseDeDatos();
       conexion= control.ConectarBaseDatos();
       try{
           PreparedStatement query= conexion.prepareStatement("SELECT ccs_cdgo, ccs_desc, CASE WHEN (ccs_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS ccs_estad FROM ["+DB+"].[dbo].[cntro_cost_subcentro] WHERE ccs_estad=1 AND [ccs_cntro_oper_cdgo]=?;");
           query.setString(1, ""+centroOperacion.getCodigo());
           //resultSetBuscar=queryBuscar.executeQuery();
           ResultSet resultSet= query.executeQuery();
           boolean validator= true;
           while(resultSet.next()){
               if(validator){
                   listadoObjetos = new ArrayList<>();
                   validator= false;
               }
               CentroCostoSubCentro Objeto = new CentroCostoSubCentro();
               Objeto.setCodigo(resultSet.getInt(1));
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
    public ArrayList<CentroCostoAuxiliar> buscarCentroCostoAuxiliar(String SubCentroCosto) throws SQLException{
       ArrayList<CentroCostoAuxiliar> listadoObjeto = null;
       Costos_VG control = new Costos_VG(tipoConexion);
       String DB=control.getBaseDeDatos();
       conexion= control.ConectarBaseDatos();
       try{
           PreparedStatement queryBuscar= conexion.prepareStatement("SELECT cca_cdgo, cca_desc, ccs_cdgo, ccs_desc, ccs_estad, CASE WHEN (cca_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS cca_estad "
                   + " FROM ["+DB+"].[dbo].[cntro_cost_auxiliar] "
                   + " INNER JOIN [cntro_cost_subcentro]  ON ccs_cdgo=cca_cntro_cost_subcentro_cdgo WHERE [cca_estad]=1 AND ccs_cdgo=?;");
           queryBuscar.setString(1, SubCentroCosto);
           ResultSet resultSetBuscar= queryBuscar.executeQuery();
           boolean validator= true;
           while(resultSetBuscar.next()){
               if(validator){
                   listadoObjeto = new ArrayList<>();
                   validator= false;
               }
               CentroCostoAuxiliar Objeto = new CentroCostoAuxiliar();
               Objeto.setCodigo(resultSetBuscar.getString(1));
               Objeto.setDescripcion(resultSetBuscar.getString(2));
               Objeto.setCentroCostoSubCentro(new CentroCostoSubCentro(resultSetBuscar.getInt(3),resultSetBuscar.getString(4),resultSetBuscar.getString(5)));
               Objeto.setEstado(resultSetBuscar.getString(6));
               listadoObjeto.add(Objeto);
           }
       }catch (SQLException sqlException) {
           //JOptionPane.showMessageDialog(null, "Error al tratar de consultar los centro de costros Auxiliares");
           sqlException.printStackTrace();
       }
       control.cerrarConexionBaseDatos();
       return listadoObjeto;
   }


    //Validaciones de existencias

    public boolean validarExistenciaMvtoCarbon(MvtoCarbon Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            System.out.println("SELECT * FROM ["+DB+"].[dbo].[mvto_carbon] WHERE  [mc_estad_mvto_carbon_cdgo] =1 AND [mc_placa_vehiculo]= '"+Objeto.getPlaca()+"' AND [mc_peso_vacio]="+Objeto.getPesoVacio()+" AND [mc_fecha_entrad]=(SELECT CONVERT(smalldatetime, '"+Objeto.getFechaEntradaVehiculo()+"'));");
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[mvto_carbon] WHERE  [mc_estad_mvto_carbon_cdgo] =1 AND [mc_placa_vehiculo]= '"+Objeto.getPlaca()+"' AND [mc_peso_vacio]="+Objeto.getPesoVacio()+" AND [mc_fecha_entrad]=(SELECT CONVERT(smalldatetime, '"+Objeto.getFechaEntradaVehiculo()+"'));");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia del Descargue de carbón");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        System.out.println(""+retorno);
        return retorno;
    }
    public boolean validarVehiculoEnTransitoMvtoCarbon(MvtoCarbon Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[mvto_carbon] WHERE  [mc_estad_mvto_carbon_cdgo] =1 AND [mc_placa_vehiculo]= '"+Objeto.getPlaca()+"' AND [mc_peso_vacio]="+Objeto.getPesoVacio()+" AND [mc_fecha_entrad]=(SELECT CONVERT(smalldatetime, '"+Objeto.getFechaEntradaVehiculo()+"')) AND [mc_fecha_fin_descargue] IS NULL;");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia del Descargue de carbón");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        System.out.println(""+retorno);
        return retorno;
    }

    public boolean validarStandByPendiente(String codigoEquipo){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
           PreparedStatement query= conexion.prepareStatement("SELECT [me_cdgo]\n" +
                   "  FROM ["+DB+"].[dbo].[mvto_equipo]\n" +
                   "\t  INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo] =[lr_cdgo] \n" +
                   "\t  INNER JOIN ["+DB+"].[dbo].[asignacion_equipo] ON [me_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                   "  WHERE  \n" +
                   "  [me_fecha_hora_fin] IS NULL AND  [lr_parada]=1 AND [ae_equipo_cdgo]="+codigoEquipo+";");
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar Stand By pendientes");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        System.out.println(""+retorno);
        return retorno;
    }


    public boolean validarExistenciaCliente(Cliente Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[cliente] WHERE [cl_cdgo] like ? AND [cl_base_datos_cdgo] = ?;");
            query.setString(1, Objeto.getCodigo());
            query.setInt(2, Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia del Cliente");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public boolean validarExistenciaTransportadora(Transportadora Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[transprtdora] WHERE [tr_cdgo] like ? AND [tr_base_datos_cdgo] = ?;");
            query.setString(1, Objeto.getCodigo());
            query.setInt(2, Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia de la Transportadora");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public boolean validarExistenciaMotonave(Motonave Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motonave] WHERE [mn_cdgo] like ? AND [mn_base_datos_cdgo] = ?;");
            query.setString(1, Objeto.getCodigo());
            query.setInt(2, Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia de la Motonave");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public boolean validarExistenciaArticulo(Articulo Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[articulo] WHERE [ar_cdgo] like ? AND [ar_base_datos_cdgo] = ?;");
            query.setString(1, Objeto.getCodigo());
            query.setInt(2, Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al tratar de Buscar la Existencia del Articulo");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }

    //Registros que no poseen existencia
    public int registrarCliente(Cliente Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try{
            conexion= control.ConectarBaseDatos();
            String estadoObjeto="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estadoObjeto="ACTIVO";
            }else{
                if(Objeto.getEstado().equalsIgnoreCase("0")){
                    estadoObjeto="INACTIVO";
                }else{
                    estadoObjeto="NULL";
                }
            }
            if(!validarExistenciaCliente(Objeto)){
                conexion= control.ConectarBaseDatos();
                if(Objeto.getCodigo().equalsIgnoreCase("") || Objeto.getDescripcion().equalsIgnoreCase("") ||
                        Objeto.getEstado() ==null || Objeto.getEstado().equalsIgnoreCase("")){
                }else{
                    PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[cliente] ([cl_cdgo],[cl_desc],[cl_estad],[cl_base_datos_cdgo]) VALUES (?,?,?,?);");
                    Query.setString(1, Objeto.getCodigo());
                    Query.setString(2, Objeto.getDescripcion());
                    Query.setString(3, Objeto.getEstado());
                    Query.setInt(4,  Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
                    Query.execute();
                    result=1;
                    if(result==1){
                        System.out.println("Registrado");
                        try{
                            //Extraemos el nombre del Equipo y la IP
                            String namePc=new ControlDB_Config().getNamePC();
                            String ipPc=new ControlDB_Config().getIpPc();
                            String macPC=new ControlDB_Config().getMacAddress();

                            PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                    "      ,[au_fecha]\n" +
                                    "      ,[au_usuario_cdgo_registro]\n" +
                                    "      ,[au_nombre_dispositivo_registro]\n" +
                                    "      ,[au_ip_dispositivo_registro]\n" +
                                    "      ,[au_mac_dispositivo_registro]\n" +
                                    "      ,[au_cdgo_mtvo]\n" +
                                    "      ,[au_desc_mtvo]\n" +
                                    "      ,[au_detalle_mtvo])\n" +
                                    "     VALUES("+
                                    "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                    "           ,(SELECT SYSDATETIME())"+
                                    "           ,?"+
                                    "           ,?"+
                                    "           ,?"+
                                    "           ,?"+
                                    "           ,?"+
                                    "           ,'CLIENTE'" +
                                    "           ,CONCAT (?,?,' Nombre: ',?,' Estado: ',?,'Base Datos: ',?));");
                            System.out.println("Registro de cliente exitoso");
                            Query_Auditoria.setString(1, us.getCodigo());
                            Query_Auditoria.setString(2, namePc);
                            Query_Auditoria.setString(3, ipPc);
                            Query_Auditoria.setString(4, macPC);
                            Query_Auditoria.setString(5, Objeto.getCodigo());
                            Query_Auditoria.setString(6, "Se registró un nuevo cliente en el sistema desde un dispositivo Movil, con Código: ");
                            Query_Auditoria.setString(7, Objeto.getCodigo());
                            Query_Auditoria.setString(8, Objeto.getDescripcion());
                            Query_Auditoria.setString(9, estadoObjeto);
                            Query_Auditoria.setString(10, Objeto.getBaseDatos().getCodigo());
                            Query_Auditoria.execute();
                            result=1;
                            if(result==1){
                                registrarCliente_recobro(Objeto,us);
                            }
                        }catch (SQLException sqlException){
                            //JOptionPane.showMessageDialog(null, "Error al Tratar de buscar");
                            sqlException.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }
    public int registrarTransportadora(Transportadora Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try{
            conexion= control.ConectarBaseDatos();
            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            if(!validarExistenciaTransportadora(Objeto)){
                conexion= control.ConectarBaseDatos();
                PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[transprtdora] ([tr_cdgo],[tr_nit],[tr_desc],[tr_observ],[tr_estad],[tr_base_datos_cdgo]) VALUES (?,?,?,?,?,?);");
                Query.setString(1, Objeto.getCodigo());
                Query.setString(2, Objeto.getNit());
                Query.setString(3, Objeto.getDescripcion());
                Query.setString(4, Objeto.getObservacion());
                Query.setString(5, Objeto.getEstado());
                Query.setInt(6,  Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
                Query.execute();
                result=1;
                if(result==1){
                    result=0;

                    //Extraemos el nombre del Equipo y la IP
                    String namePc=new ControlDB_Config().getNamePC();
                    String ipPc=new ControlDB_Config().getIpPc();
                    String macPC=new ControlDB_Config().getMacAddress();

                    PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                            "      ,[au_fecha]\n" +
                            "      ,[au_usuario_cdgo_registro]\n" +
                            "      ,[au_nombre_dispositivo_registro]\n" +
                            "      ,[au_ip_dispositivo_registro]\n" +
                            "      ,[au_mac_dispositivo_registro]\n" +
                            "      ,[au_cdgo_mtvo]\n" +
                            "      ,[au_desc_mtvo]\n" +
                            "      ,[au_detalle_mtvo])\n" +
                            "     VALUES("+
                            "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                            "           ,(SELECT SYSDATETIME())"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,'TRANSPORTADORA'" +
                            "           ,CONCAT (?,?,' Nit: ',?,' Estado: ',?,'Base Datos: ',?));");
                    Query_Auditoria.setString(1, us.getCodigo());
                    Query_Auditoria.setString(2, namePc);
                    Query_Auditoria.setString(3, ipPc);
                    Query_Auditoria.setString(4, macPC);
                    Query_Auditoria.setString(5, Objeto.getCodigo());
                    Query_Auditoria.setString(6, "Se registró una nueva transportadora en el sistema desde un Dispositivo Movil, con Código: ");
                    Query_Auditoria.setString(7, Objeto.getCodigo());
                    Query_Auditoria.setString(8, Objeto.getNit()+" Nombre: "+Objeto.getDescripcion()+" Obervación:"+Objeto.getObservacion());
                    Query_Auditoria.setString(9, estado);
                    Query_Auditoria.setString(10, Objeto.getBaseDatos().getCodigo());
                    Query_Auditoria.execute();
                    result=1;
                }
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }
    public int registrarMotonave(Motonave Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try{
            conexion= control.ConectarBaseDatos();
            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            if(!validarExistenciaMotonave(Objeto)){
                conexion= control.ConectarBaseDatos();
                PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[motonave] ([mn_cdgo],[mn_desc],[mn_estad],[mn_base_datos_cdgo]) VALUES (?,?,?,?);");
                Query.setString(1, Objeto.getCodigo());
                Query.setString(2, Objeto.getDescripcion());
                Query.setString(3, Objeto.getEstado());
                Query.setInt(4,  Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
                Query.execute();
                result=1;
                if(result==1){
                    result=0;

                    //Extraemos el nombre del Equipo y la IP
                    String namePc=new ControlDB_Config().getNamePC();
                    String ipPc=new ControlDB_Config().getIpPc();
                    String macPC=new ControlDB_Config().getMacAddress();

                    PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                            "      ,[au_fecha]\n" +
                            "      ,[au_usuario_cdgo_registro]\n" +
                            "      ,[au_nombre_dispositivo_registro]\n" +
                            "      ,[au_ip_dispositivo_registro]\n" +
                            "      ,[au_mac_dispositivo_registro]\n" +
                            "      ,[au_cdgo_mtvo]\n" +
                            "      ,[au_desc_mtvo]\n" +
                            "      ,[au_detalle_mtvo])\n" +
                            "     VALUES("+
                            "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                            "           ,(SELECT SYSDATETIME())"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,'MOTONAVE'" +
                            "           ,CONCAT (?,?,' Nombre: ',?,' Estado: ',?,'Base Datos: ',?));");
                    Query_Auditoria.setString(1, us.getCodigo());
                    Query_Auditoria.setString(2, namePc);
                    Query_Auditoria.setString(3, ipPc);
                    Query_Auditoria.setString(4, macPC);
                    Query_Auditoria.setString(5, Objeto.getCodigo());
                    Query_Auditoria.setString(6, "Se registró una nueva motonave en el sistema desde un dispositivo Movil, con Código: ");
                    Query_Auditoria.setString(7, Objeto.getCodigo());
                    Query_Auditoria.setString(8, Objeto.getDescripcion());
                    Query_Auditoria.setString(9, estado);
                    Query_Auditoria.setString(10, Objeto.getBaseDatos().getCodigo());
                    Query_Auditoria.execute();
                    result=1;
                }
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }
    public int registrarArticulo(Articulo Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try{
            conexion= control.ConectarBaseDatos();
            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            if(!validarExistenciaArticulo(Objeto)){
                conexion= control.ConectarBaseDatos();
                PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[articulo] ([ar_cdgo],[ar_desc],[ar_estad],[ar_base_datos_cdgo]) VALUES (?,?,?,?);");
                Query.setString(1, Objeto.getCodigo());
                Query.setString(2, Objeto.getDescripcion());
                Query.setString(3, Objeto.getEstado());
                Query.setInt(4,  Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
                Query.execute();
                result=1;
                if(result==1){
                    result=0;
                    //Extraemos el nombre del Dispositivo y la IP
                    String namePc=new ControlDB_Config().getNamePC();
                    String ipPc=new ControlDB_Config().getIpPc();
                    String macPC=new ControlDB_Config().getMacAddress();
                    PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                                                                    "      ,[au_fecha]\n" +
                                                                                    "      ,[au_usuario_cdgo_registro]\n" +
                                                                                    "      ,[au_nombre_dispositivo_registro]\n" +
                                                                                    "      ,[au_ip_dispositivo_registro]\n" +
                                                                                    "      ,[au_mac_dispositivo_registro]\n" +
                                                                                    "      ,[au_cdgo_mtvo]\n" +
                                                                                    "      ,[au_desc_mtvo]\n" +
                                                                                    "      ,[au_detalle_mtvo])\n" +
                                                                                    "     VALUES("+
                                                                                    "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                                                                    "           ,(SELECT SYSDATETIME())"+
                                                                                    "           ,?"+
                                                                                    "           ,?"+
                                                                                    "           ,?"+
                                                                                    "           ,?"+
                                                                                    "           ,?"+
                                                                                    "           ,'ARTICULO'" +
                                                                                    "           , CONCAT (?,?,' Nombre: ',?,' Estado: ',?,'Base Datos: ',?));");
                    Query_Auditoria.setString(1, us.getCodigo());
                    Query_Auditoria.setString(2, namePc);
                    Query_Auditoria.setString(3, ipPc);
                    Query_Auditoria.setString(4, macPC);
                    Query_Auditoria.setString(5, Objeto.getCodigo());
                    Query_Auditoria.setString(6, "Se registró un nuevo articulo en el sistema desde un dispositivo Movil, con Código: ");
                    Query_Auditoria.setString(7, Objeto.getCodigo());
                    Query_Auditoria.setString(8, Objeto.getDescripcion());
                    Query_Auditoria.setString(9, estado);
                    Query_Auditoria.setString(10, Objeto.getBaseDatos().getCodigo());
                    Query_Auditoria.execute();
                    result=1;
                }
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }



    public int registrarCliente_recobro(Cliente Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try{
            conexion= control.ConectarBaseDatos();
            PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[cliente_recobro] ([clr_cdgo],[clr_cliente_cdgo],[clr_usuario_cdgo],[clr_valor_recobro],[clr_fecha_registro],[clr_cliente_base_datos_cdgo]) VALUES ((SELECT (CASE WHEN (MAX([clr_cdgo]) IS NULL) THEN 1 ELSE (MAX([clr_cdgo])+1) END)AS [clr_cdgo] FROM ["+DB+"].[dbo].[cliente_recobro]),?,?,?,(SELECT SYSDATETIME()),?);");
            Query.setString(1, Objeto.getCodigo());
            Query.setString(2, us.getCodigo());
            Query.setInt(3, Objeto.getValorRecobro());
            Query.setInt(4, Integer.parseInt(Objeto.getBaseDatos().getCodigo()));
            Query.execute();
            result=1;
            //Procedemos a registrar en la tabla Auditoria
            if(result==1){
                result=0;

                //Extraemos el nombre del Equipo y la IP
                String namePc=new ControlDB_Config().getNamePC();
                String ipPc=new ControlDB_Config().getIpPc();
                String macPC=new ControlDB_Config().getMacAddress();

                PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                                                            "      ,[au_fecha]\n" +
                                                                            "      ,[au_usuario_cdgo_registro]\n" +
                                                                            "      ,[au_nombre_dispositivo_registro]\n" +
                                                                            "      ,[au_ip_dispositivo_registro]\n" +
                                                                            "      ,[au_mac_dispositivo_registro]\n" +
                                                                            "      ,[au_cdgo_mtvo]\n" +
                                                                            "      ,[au_desc_mtvo]\n" +
                                                                            "      ,[au_detalle_mtvo])\n" +
                                                                            "     VALUES("+
                                                                            "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                                                            "           ,(SELECT SYSDATETIME())"+
                                                                            "           ,?"+
                                                                            "           ,?"+
                                                                            "           ,?"+
                                                                            "           ,?"+
                                                                            "           ,?"+
                                                                            "           ,'CLIENTE'" +
                                                                            "           ,CONCAT (?,?,' Nombre: ',?,' Estado: ',?,'Base Datos: ',?));");
                Query_Auditoria.setString(1, us.getCodigo());
                Query_Auditoria.setString(2, namePc);
                Query_Auditoria.setString(3, ipPc);
                Query_Auditoria.setString(4, macPC);
                Query_Auditoria.setString(5, Objeto.getCodigo());
                Query_Auditoria.setString(6, "Se registró un recobro en el sistema desde un dispositivo Movil para el cliente Código: ");
                Query_Auditoria.setString(7, Objeto.getCodigo());
                Query_Auditoria.setString(8, Objeto.getDescripcion());
                Query_Auditoria.setInt(9, Objeto.getValorRecobro());
                Query_Auditoria.setString(10, Objeto.getBaseDatos().getCodigo());
                Query_Auditoria.execute();
                result=1;
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }

    public int registrarEnListadoMvtoCarbon(MvtoCarbon Objeto, AsignacionEquipo asignacionEquipo, Usuario usuario, MvtoEquipo mvtoEquipo) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                try {
                    if (mvtoEquipo.getRecobro().getCodigo().equals("1")) {
                        mvtoEquipo.getRecobro().setCodigo("2");
                        mvtoEquipo.getRecobro().setDescripcion("PENDIENTE CONFIRMACIÓN");
                    }
                }catch(Exception e){
                    System.out.println("Error al procesar el recobro");
                }
                conexion= control.ConectarBaseDatos();
                //Objeto.getCliente().setCodigo("'" + Objeto.getCliente().getCodigo() + "'");
                //Objeto.getTransportadora().setCodigo("'" + Objeto.getTransportadora().getCodigo() + "'");
                //Objeto.getArticulo().setCodigo("'" + Objeto.getArticulo().getCodigo() + "'");
                //Objeto.setNumero_orden("'" + Objeto.getNumero_orden() + "'");
                //Objeto.setDeposito("'" + Objeto.getDeposito() + "'");
                //Objeto.setPlaca("'" + Objeto.getPlaca() + "'");
                conexion= control.ConectarBaseDatos();
                PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                        "DECLARE @consecutivo BIGINT=(SELECT (CASE WHEN (MAX([me_cdgo]) IS NULL) \n" +
                        " THEN 1 ELSE (MAX([me_cdgo])+1) END)AS [mc_cdgo] \n" +
                        " FROM ["+DB+"].[dbo].[mvto_equipo]) " +
                        "  INSERT INTO ["+DB+"].[dbo].[mvto_equipo]\n" +
                        "           ([me_cdgo]\n" +
                        "           ,[me_asignacion_equipo_cdgo]\n" +
                        "           ,[me_fecha]\n" +
                        "           ,[me_proveedor_equipo_cdgo]\n" +
                        "           ,[me_cntro_oper_cdgo]\n" +
                        "           ,[me_cntro_cost_auxiliar_cdgo]\n" +
                        "           ,[me_labor_realizada_cdgo]\n" +
                        "           ,[me_cliente_cdgo]\n" +
                        "           ,[me_articulo_cdgo]\n" +
                        "           ,[me_fecha_hora_inicio]\n" +
                        "           ,[me_recobro_cdgo]\n" +
                        "           ,[me_usuario_registro_cdgo]\n" +
                        "           ,[me_inactividad]\n" +
                        "           ,[me_motivo_parada_estado]\n" +
                        "           ,[me_estado]\n" +
                        "           ,[me_desde_mvto_carbon],[me_cntro_cost_auxiliarDestino_cdgo],[me_cliente_base_datos_cdgo],[me_articulo_base_datos_cdgo])\n" +
                        "     VALUES ((select @consecutivo) --me_cdgo\n" +
                        "           ,"+asignacionEquipo.getCodigo()+" --me_asignacion_equipo_cdgo\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) --me_fecha\n" +
                        "           ,'"+mvtoEquipo.getProveedorEquipo().getCodigo()+"'--me_proveedor_equipo_cdgo\n" +
                        "           ,"+mvtoEquipo.getCentroOperacion().getCodigo()+"--me_cntro_oper_cdgo\n" +
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliar().getCodigo()+"--me_cntro_cost_auxiliar_cdgo\n" +
                        "           ,"+mvtoEquipo.getLaborRealizada().getCodigo()+"--me_labor_realizada_cdgo\n" +
                        "           ,'"+mvtoEquipo.getCliente().getCodigo()+"'--me_cliente_cdgo\n" +
                        "           ,'"+Objeto.getArticulo().getCodigo()+"'--me_articulo_cdgo\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))--me_fecha_hora_inicio\n" +
                        "           ,"+mvtoEquipo.getRecobro().getCodigo()+"--me_recobro_cdgo\n" +
                        "           ,"+mvtoEquipo.getUsuarioQuieRegistra().getCodigo()+"--me_usuario_registro_cdgo\n" +
                        "           ,0   --me_inactividad\n" +
                        "           ,0--me_motivo_parada_estado\n" +
                        "           ,"+mvtoEquipo.getEstado()+"--me_estado\n" +
                        "           ,"+mvtoEquipo.getDesdeCarbon()+"--me_desde_mvto_carbon\n" +
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliarDestino().getCodigo()+","+Objeto.getCliente().getBaseDatos().getCodigo()+","+Objeto.getArticulo().getBaseDatos().getCodigo()+"); \n" +
                        "SELECT @consecutivo;");
                result=1;
                ResultSet resultSet= queryRegistrarT.executeQuery();
                while(resultSet.next()){
                    if(result==1){
                        mvtoEquipo.setCodigo(resultSet.getString(1));
                    }
                }
                if (result == 1) {
                    PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                            "           ([mcle_cdgo]\n" +
                            "           ,[mcle_mvto_carbon_cdgo]\n" +
                            "           ,[mcle_asignacion_equipo_cdgo]\n" +
                            "           ,[mcle_mvto_equipo_cdgo]\n" +
                            "           ,[mcle_estado])\n" +
                            "     VALUES\n" +
                            "           ((SELECT (CASE WHEN (MAX([mcle_cdgo]) IS NULL)\n" +
                            "                             THEN 1 ELSE (MAX([mcle_cdgo])+1) END)AS [mcle_cdgo] \n" +
                            "                            FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]) --mcle_cdgo\n" +
                            "           , ? --mcle_mvto_carbon_cdgo\n" +
                            "           , ? --mcle_asignacion_equipo_cdgo\n" +
                            "           , ?--mcle_mvto_equipo_cdgo\n" +
                            "           , ? --mcle_estado\n" +
                            ");");
                    Query.setString(1, Objeto.getCodigo());
                    Query.setString(2, asignacionEquipo.getCodigo());
                    Query.setString(3, mvtoEquipo.getCodigo());
                    Query.setInt(4, 1);
                    Query.execute();
                    result=1;
                    if(result==1){
                        result=0;
                        //Extraemos el nombre del Dispositivo y la IP
                        String namePc=new ControlDB_Config().getNamePC();
                        String ipPc=new ControlDB_Config().getIpPc();
                        String macPC=new ControlDB_Config().getMacAddress();
                        PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                "      ,[au_fecha]\n" +
                                "      ,[au_usuario_cdgo_registro]\n" +
                                "      ,[au_nombre_dispositivo_registro]\n" +
                                "      ,[au_ip_dispositivo_registro]\n" +
                                "      ,[au_mac_dispositivo_registro]\n" +
                                "      ,[au_cdgo_mtvo]\n" +
                                "      ,[au_desc_mtvo]\n" +
                                "      ,[au_detalle_mtvo])\n" +
                                "     VALUES("+
                                "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                "           ,(SELECT SYSDATETIME())"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,'DESCARGUE'" +
                                "           , CONCAT (?,?,' Asignacion Código: ',?,' Mvto_Carbon Código: ',?));");
                        Query_Auditoria.setString(1, usuario.getCodigo());
                        Query_Auditoria.setString(2, namePc);
                        Query_Auditoria.setString(3, ipPc);
                        Query_Auditoria.setString(4, macPC);
                        Query_Auditoria.setString(5, mvtoEquipo.getCodigo());
                        Query_Auditoria.setString(6, "Se registró un nuevo inicio de descargue de un vehiculo por medio de un equipo en el sistema desde un dispositivo Movil, con Mvto_Equipo Código: ");
                        Query_Auditoria.setString(7, mvtoEquipo.getCodigo());
                        Query_Auditoria.setString(8, asignacionEquipo.getCodigo());
                        Query_Auditoria.setString(9, Objeto.getCodigo());
                        Query_Auditoria.execute();
                        result=1;
                    }
                }
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
    public String compardorEntreDosFechas(String fechaInicio, String FechaFinal) throws SQLException {
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String retorno="";
        try{
            ResultSet resultSetBuscar;

            PreparedStatement queryBuscar= conexion.prepareStatement(" SELECT DATEDIFF(minute,?, ?) as DiferenciaFecha;");
            queryBuscar.setString(1, fechaInicio);
            queryBuscar.setString(2, FechaFinal);
            resultSetBuscar= queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                retorno=resultSetBuscar.getString(1);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }

    /*Borrar para abajo el metodo registrarEnListadoMvtoCarbon_Backup lo usa el metodo  registrarMvtoCarbonCompleto_backup ambos están abajo*/
    /*public int registrarMvtoCarbonCompleto_backup(MvtoCarbon Objeto, AsignacionEquipo asignacionEquipo, Usuario usuario, MvtoEquipo mvtoEquipo) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                String estado;
                if (Objeto.getEstadoMvtoCarbon().getEstado().equalsIgnoreCase("1")) {
                    estado = "ACTIVO";
                } else {
                    estado = "INACTIVO";
                }
                try {
                    if(!Objeto.getArticulo().getCodigo().equals("NULL")) {
                        if (!validarExistenciaArticulo(Objeto.getArticulo())) {
                            int n = registrarArticulo(Objeto.getArticulo(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un articulo nuevo en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getCliente().getCodigo().equals("NULL")) {
                        if (!validarExistenciaCliente(Objeto.getCliente())) {
                            int n = registrarCliente(Objeto.getCliente(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un nuevo cliente en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getTransportadora().getCodigo().equals("NULL")) {
                        if (!validarExistenciaTransportadora(Objeto.getTransportadora())) {
                            int n = registrarTransportadora(Objeto.getTransportadora(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva transportadora en el sistema");
                            }
                        }
                    }
                    if(!Objeto.getMotonave().getCodigo().equals("NULL")) {
                        if (!validarExistenciaMotonave(Objeto.getMotonave())) {
                            int n = registrarMotonave(Objeto.getMotonave(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva motonave en el sistema");
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Error validandos datos y registrados nuevos Items, Cliente, Motonave, Transportadora, Articulo");
                }
                conexion= control.ConectarBaseDatos();
                String codigoArticulo="",codigoCliente="",codigoTransportadora="";

                if(!Objeto.getArticulo().getCodigo().equals("NULL")) {
                    codigoArticulo = "'" + Objeto.getArticulo().getCodigo() + "'";
                }else{
                    codigoArticulo = Objeto.getArticulo().getCodigo();
                }

                if(!Objeto.getCliente().getCodigo().equals("NULL")) {
                    codigoCliente = "'" + Objeto.getCliente().getCodigo() + "'";
                }else{
                    codigoCliente = Objeto.getCliente().getCodigo();
                }

                if(!Objeto.getTransportadora().getCodigo().equals("NULL")) {
                    codigoTransportadora= "'" + Objeto.getTransportadora().getCodigo() + "'";
                }else{
                    codigoTransportadora = null;
                }




                PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                        //"DECLARE @fechaEntrada smalldatetime = "+Objeto.getFechaEntrada()+";" +
                        //"DECLARE @fechaSalida smalldatetime = "+Objeto.getFechaSalida()+";    " +
                        "INSERT INTO [" + DB + "].[dbo].[mvto_carbon]" +
                        "           ([mc_cdgo],[mc_cntro_oper_cdgo],[mc_cntro_cost_auxiliar_cdgo],[mc_labor_realizada_cdgo],[mc_articulo_cdgo],[mc_cliente_cdgo],[mc_transprtdora_cdgo]" +
                        "           ,[mc_fecha],[mc_num_orden],[mc_deposito],[mc_consecutivo_tqute],[mc_placa_vehiculo],[mc_peso_vacio],[mc_peso_lleno]" +
                        "           ,[mc_peso_neto],[mc_fecha_entrad],[mc_fecha_salid],[mc_fecha_inicio_descargue],[mc_fecha_fin_descargue],[mc_usuario_cdgo],[mc_observ]" +
                        "           ,[mc_estad_mvto_carbon_cdgo],[mc_conexion_peso_ccarga],[mc_registro_manual],[mc_usuario_registro_manual_cdgo],[mc_cntro_cost_auxiliarDestino_cdgo],[mc_lavado_vehiculo],[mc_lavado_vehiculo_observacion])" +
                        "     VALUES ((SELECT (CASE WHEN (MAX([mc_cdgo]) IS NULL) THEN 1 ELSE (MAX([mc_cdgo])+1) END)AS [mc_cdgo] FROM ["+DB+"].[dbo].[mvto_carbon])," + Objeto.getCentroOperacion().getCodigo() + "," + Objeto.getCentroCostoAuxiliar().getCodigo() +"," + Objeto.getLaborRealizada().getCodigo() +
                        "," + codigoArticulo + "," + codigoCliente + "," + codigoTransportadora+ ",(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))" +
                        ",'" + Objeto.getNumero_orden() + "','" + Objeto.getDeposito() + "'," + Objeto.getConsecutivo() + ",'" + Objeto.getPlaca() + "'," + Objeto.getPesoVacio() + "" +
                        "," + Objeto.getPesoLleno() + "," + Objeto.getPesoNeto() + ",'"+Objeto.getFechaEntradaVehiculo()+"',"+Objeto.getFecha_SalidaVehiculo()+
                        "," +Objeto.getFechaInicioDescargue()+ ",NULL," +Objeto.getUsuarioRegistroMovil().getCodigo() + "," + Objeto.getObservacion() + "," + Objeto.getEstadoMvtoCarbon().getCodigo() + "," + Objeto.getConexionPesoCcarga() + ",NULL,NULL,"+Objeto.getCentroCostoAuxiliarDestino().getCodigo()+","+Objeto.getLavadoVehiculo()+","+Objeto.getLavadorVehiculoObservacion()+");");
                queryRegistrarT.execute();

                result = 1;
                if (result == 1) {
                    result = 0;
                    //Extraemos el nombre del Dispositivo y la IP
                    String namePc=new ControlDB_Config().getNamePC();
                    String ipPc=new ControlDB_Config().getIpPc();
                    String macPC=new ControlDB_Config().getMacAddress();

                    PreparedStatement queryMaximo = conexion.prepareStatement("SELECT MAX(mc_cdgo) FROM [" + DB + "].[dbo].[mvto_carbon];");
                    ResultSet resultSetMaximo = queryMaximo.executeQuery();
                    while (resultSetMaximo.next()) {
                        if (resultSetMaximo.getString(1) != null) {
                            Objeto.setCodigo(resultSetMaximo.getString(1));
                        }
                    }
                    PreparedStatement Query_AuditoriaInsert = conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                            "      ,[au_fecha]\n" +
                            "      ,[au_usuario_cdgo_registro]\n" +
                            "      ,[au_nombre_dispositivo_registro]\n" +
                            "      ,[au_ip_dispositivo_registro]\n" +
                            "      ,[au_mac_dispositivo_registro]\n" +
                            "      ,[au_cdgo_mtvo]\n" +
                            "      ,[au_desc_mtvo]\n" +
                            "      ,[au_detalle_mtvo])\n" +
                            "     VALUES("+
                            "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                            "           ,(SELECT SYSDATETIME())"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,?"+
                            "           ,'DESCARGUE_CARBON'" +
                            "           ,CONCAT (?,?,?));");
                    Query_AuditoriaInsert.setString(1, Objeto.getUsuarioRegistroMovil().getCodigo());
                    Query_AuditoriaInsert.setString(2, namePc);
                    Query_AuditoriaInsert.setString(3, ipPc);
                    Query_AuditoriaInsert.setString(4, macPC);
                    Query_AuditoriaInsert.setString(5, Objeto.getCodigo());
                    Query_AuditoriaInsert.setString(6, "Se registró un nuevo Movimiento de Carbon en el sistema desde un Dispositivo Movil, con código: ");
                    Query_AuditoriaInsert.setString(7, Objeto.getCodigo());
                    Query_AuditoriaInsert.setString(8, " PLACA: " + Objeto.getPlaca() + " Orden: " + Objeto.getNumero_orden() + " Deposito: " + Objeto.getDeposito() + " Articulo: " + Objeto.getArticulo().getDescripcion() + " Peso Vacio: " + Objeto.getPesoVacio());
                    Query_AuditoriaInsert.execute();
                    result = 1;
                    if(result==1){
                        registrarEnListadoMvtoCarbon_Backup(Objeto,asignacionEquipo,usuario,mvtoEquipo);
                        result=1;
                    }
                }
                System.out.println("Resultado de result->"+result);
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
    public int registrarEnListadoMvtoCarbon_Backup(MvtoCarbon Objeto, AsignacionEquipo asignacionEquipo, Usuario usuario, MvtoEquipo mvtoEquipo) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                try {
                    if (mvtoEquipo.getRecobro().getCodigo().equals("1")) {
                        mvtoEquipo.getRecobro().setCodigo("2");
                        mvtoEquipo.getRecobro().setDescripcion("PENDIENTE CONFIRMACIÓN");
                    }
                }catch(Exception e){
                    System.out.println("Error al procesar el recobro");
                }
                conexion= control.ConectarBaseDatos();
                //Objeto.getCliente().setCodigo("'" + Objeto.getCliente().getCodigo() + "'");
                //Objeto.getTransportadora().setCodigo("'" + Objeto.getTransportadora().getCodigo() + "'");
                //Objeto.getArticulo().setCodigo("'" + Objeto.getArticulo().getCodigo() + "'");
                //Objeto.setNumero_orden("'" + Objeto.getNumero_orden() + "'");
                //Objeto.setDeposito("'" + Objeto.getDeposito() + "'");
                //Objeto.setPlaca("'" + Objeto.getPlaca() + "'");
                conexion= control.ConectarBaseDatos();
                PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                        "DECLARE @consecutivo BIGINT=(SELECT (CASE WHEN (MAX([me_cdgo]) IS NULL) \n" +
                        " THEN 1 ELSE (MAX([me_cdgo])+1) END)AS [mc_cdgo] \n" +
                        " FROM ["+DB+"].[dbo].[mvto_equipo]) " +
                        "  INSERT INTO ["+DB+"].[dbo].[mvto_equipo]\n" +
                        "           ([me_cdgo]\n" +
                        "           ,[me_asignacion_equipo_cdgo]\n" +
                        "           ,[me_fecha]\n" +
                        "           ,[me_proveedor_equipo_cdgo]\n" +
                        "           ,[me_cntro_oper_cdgo]\n" +
                        "           ,[me_cntro_cost_auxiliar_cdgo]\n" +
                        "           ,[me_labor_realizada_cdgo]\n" +
                        "           ,[me_cliente_cdgo]\n" +
                        "           ,[me_articulo_cdgo]\n" +
                        "           ,[me_fecha_hora_inicio]\n" +
                        "           ,[me_recobro_cdgo]\n" +
                        "           ,[me_usuario_registro_cdgo]\n" +
                        "           ,[me_inactividad]\n" +
                        "           ,[me_motivo_parada_estado]\n" +
                        "           ,[me_estado]\n" +
                        "           ,[me_desde_mvto_carbon],[me_cntro_cost_auxiliarDestino_cdgo])\n" +
                        "     VALUES ((select @consecutivo) --me_cdgo\n" +
                        "           ,"+asignacionEquipo.getCodigo()+" --me_asignacion_equipo_cdgo\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) --me_fecha\n" +
                        "           ,'"+mvtoEquipo.getProveedorEquipo().getCodigo()+"'--me_proveedor_equipo_cdgo\n" +
                        "           ,"+mvtoEquipo.getCentroOperacion().getCodigo()+"--me_cntro_oper_cdgo\n" +
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliar().getCodigo()+"--me_cntro_cost_auxiliar_cdgo\n" +
                        "           ,"+mvtoEquipo.getLaborRealizada().getCodigo()+"--me_labor_realizada_cdgo\n" +
                        "           ,'"+mvtoEquipo.getCliente().getCodigo()+"'--me_cliente_cdgo\n" +
                        "           ,'"+Objeto.getArticulo().getCodigo()+"'--me_articulo_cdgo\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))--me_fecha_hora_inicio\n" +
                        "           ,"+mvtoEquipo.getRecobro().getCodigo()+"--me_recobro_cdgo\n" +
                        "           ,"+mvtoEquipo.getUsuarioQuieRegistra().getCodigo()+"--me_usuario_registro_cdgo\n" +
                        "           ,0   --me_inactividad\n" +
                        "           ,0--me_motivo_parada_estado\n" +
                        "           ,"+mvtoEquipo.getEstado()+"--me_estado\n" +
                        "           ,"+mvtoEquipo.getDesdeCarbon()+"--me_desde_mvto_carbon\n" +
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliarDestino().getCodigo()+"); \n" +
                        "SELECT @consecutivo;");
                result=1;
                ResultSet resultSet= queryRegistrarT.executeQuery();
                while(resultSet.next()){
                    if(result==1){
                        mvtoEquipo.setCodigo(resultSet.getString(1));
                    }
                }
                if (result == 1) {
                    PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                            "           ([mcle_cdgo]\n" +
                            "           ,[mcle_mvto_carbon_cdgo]\n" +
                            "           ,[mcle_asignacion_equipo_cdgo]\n" +
                            "           ,[mcle_mvto_equipo_cdgo]\n" +
                            "           ,[mcle_estado])\n" +
                            "     VALUES\n" +
                            "           ((SELECT (CASE WHEN (MAX([mcle_cdgo]) IS NULL)\n" +
                            "                             THEN 1 ELSE (MAX([mcle_cdgo])+1) END)AS [mcle_cdgo] \n" +
                            "                            FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]) --mcle_cdgo\n" +
                            "           , ? --mcle_mvto_carbon_cdgo\n" +
                            "           , ? --mcle_asignacion_equipo_cdgo\n" +
                            "           , ?--mcle_mvto_equipo_cdgo\n" +
                            "           , ? --mcle_estado\n" +
                            ");");
                    Query.setString(1, Objeto.getCodigo());
                    Query.setString(2, asignacionEquipo.getCodigo());
                    Query.setString(3, mvtoEquipo.getCodigo());
                    Query.setInt(4, 1);
                    Query.execute();
                    result=1;
                    if(result==1){
                        result=0;
                        //Extraemos el nombre del Dispositivo y la IP
                        String namePc=new ControlDB_Config().getNamePC();
                        String ipPc=new ControlDB_Config().getIpPc();
                        String macPC=new ControlDB_Config().getMacAddress();
                        PreparedStatement Query_Auditoria= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                "      ,[au_fecha]\n" +
                                "      ,[au_usuario_cdgo_registro]\n" +
                                "      ,[au_nombre_dispositivo_registro]\n" +
                                "      ,[au_ip_dispositivo_registro]\n" +
                                "      ,[au_mac_dispositivo_registro]\n" +
                                "      ,[au_cdgo_mtvo]\n" +
                                "      ,[au_desc_mtvo]\n" +
                                "      ,[au_detalle_mtvo])\n" +
                                "     VALUES("+
                                "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                "           ,(SELECT SYSDATETIME())"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,'DESCARGUE'" +
                                "           , CONCAT (?,?,' Asignacion Código: ',?,' Mvto_Carbon Código: ',?));");
                        Query_Auditoria.setString(1, usuario.getCodigo());
                        Query_Auditoria.setString(2, namePc);
                        Query_Auditoria.setString(3, ipPc);
                        Query_Auditoria.setString(4, macPC);
                        Query_Auditoria.setString(5, mvtoEquipo.getCodigo());
                        Query_Auditoria.setString(6, "Se registró un nuevo inicio de descargue de un vehiculo por medio de un equipo en el sistema desde un dispositivo Movil, con Mvto_Equipo Código: ");
                        Query_Auditoria.setString(7, mvtoEquipo.getCodigo());
                        Query_Auditoria.setString(8, asignacionEquipo.getCodigo());
                        Query_Auditoria.setString(9, Objeto.getCodigo());
                        Query_Auditoria.execute();
                        result=1;
                    }
                }
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
    public MvtoCarbon cargarPlacaTransitoEspecifica(String placa) {
        Ccarga_GP control = new Ccarga_GP(tipoConexion);
        String DB=control.getBaseDeDatos();
        MvtoCarbon Objeto = null;
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT   " +
                    "                     PRODUCTO_CODIGO=ar_cdgo, " +
                    "                            PRODUCTO=ar_nmbre, " +
                    "                            CLIENTE_CODIGO=cl_cdgo, " +
                    "                            CLIENTE=cl_nmbre,  " +
                    "                            TRANSPORTADORA_CODIGO=tu_trnsprtdra, " +
                    "                            TRANSPORTADORA_NOMBRE=tr_nmbre, " +
                    "                            PLACA=tu_plca, " +
                    "                            TARA=tu_tra, " +
                    "                            TARADO=tu_fcha, " +
                    "                            MN_CODIGO=mo_cdgo, " +
                    "                            MN=mo_nmbre , " +
                    "                            DEPOSITO=[tu_dpsto], " +
                    "                            [tu_plca] " +
                    "                          ,[tu_tra] " +
                    "                          ,[tu_psdas] " +
                    "                          ,[tu_fcha] " +
                    "                          ,[tu_nmbre_cndctor] " +
                    "                          ,[tu_cdla] " +
                    "                          ,[tu_trnsprtdra] " +
                    "                          ,[tu_dpsto] " +
                    "                          ,[tu_cncpto] " +
                    "                          ,[tu_bdga] " +
                    "                          ,[tu_mdldad_dscrgue] " +
                    "                          ,[tu_esctlla] " +
                    "                          ,[tu_fcha_fin_crgue] " +
                    "                          ,[tu_empque] " +
                    "                      FROM ["+DB+"].[dbo].[tra_urbno]     " +
                    "                    INNER JOIN ["+DB+"].[dbo].dpsto  ON de_cdgo=[tu_dpsto] " +
                    "                                         INNER JOIN ["+DB+"].[dbo].clnte ON de_clnte= cl_cdgo " +
                    "                                          " +
                    "                     INNER JOIN ["+DB+"].[dbo].[arrbo_mtnve] ON de_arrbo_mtnve=[am_cdgo] " +
                    "                     INNER JOIN  ["+DB+"].[dbo].mtnve ON [am_mtnve]=mo_cdgo " +
                    "                                         INNER JOIN ["+DB+"].[dbo].artclo ON de_artclo = ar_cdgo " +
                    "                                         INNER JOIN  ["+DB+"].[dbo].trnsprtdra ON tr_cdgo=[tu_trnsprtdra] " +
                    "                                         INNER JOIN ["+DB+"].[dbo].cncpto ON  cn_cdgo= [tu_cncpto]  " +
                    " WHERE  tu_plca=? AND tu_fcha >= CONVERT (date, GETDATE()-7) ORDER BY tu_plca ASC");
            queryBuscar.setString(1, placa);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                System.out.println(resultSetBuscar.getString(1));
                Objeto = new MvtoCarbon();
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(1),resultSetBuscar.getString(2),"1"));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(3),resultSetBuscar.getString(4),"1",0));
                Objeto.setTransportadora(new Transportadora(resultSetBuscar.getString(5),"",resultSetBuscar.getString(6),"","1"));
                Objeto.setPlaca(resultSetBuscar.getString(7));
                Objeto.setPesoVacio(resultSetBuscar.getString(8));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(9));
                Objeto.setMotonave(new Motonave(resultSetBuscar.getString(10),resultSetBuscar.getString(11),"1"));
                Objeto.setDeposito(resultSetBuscar.getString(12));
                //Objeto.setOrden(resultSetBuscar.getString(13));
            }
        } catch (Exception e) {

        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<MvtoCarbon> reporteCarbon_VehiculosTrabajado_Backup(Usuario us, String fechaInicio, String fechaFinal) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1\n" +
                    "      ,[mc_cntro_oper_cdgo]--2\n" +
                    "-- Centro Operacion\n" +
                    ",[co_cdgo]--3\n" +
                    ",[co_desc]--4\n" +
                    ",[co_estad]--5\n" +
                    "      ,[mc_cntro_cost_auxiliar_cdgo]--6\n" +
                    "--Auxiliar Centro de Costo\n" +
                    ",cca_origen.[cca_cdgo]--7\n" +
                    ",cca_origen.[cca_cntro_cost_subcentro_cdgo]--8\n" +
                    "--Subcentro de Costo\n" +
                    ",[ccs_cdgo]--9\n" +
                    ",[ccs_desc]--10\n" +
                    ",[ccs_estad]--11\n" +
                    ",cca_origen.[cca_desc]--12\n" +
                    ",cca_origen.[cca_estad]--13\n" +
                    "      ,[mc_articulo_cdgo]--14\n" +
                    "  --Articulo\n" +
                    "  ,[ar_cdgo]--15\n" +
                    "  ,[ar_desc]--16\n" +
                    "  ,[ar_estad]--17\n" +
                    "      ,[mc_cliente_cdgo]--18\n" +
                    "--Cliente\n" +
                    ",[cl_cdgo]--19\n" +
                    ",[cl_desc]--20\n" +
                    ",[cl_estad]--21\n" +
                    "      ,[mc_transprtdora_cdgo]--22\n" +
                    "--Transportadora\n" +
                    "  ,[tr_cdgo]--23\n" +
                    "  ,[tr_nit]--24\n" +
                    "  ,[tr_desc]--25\n" +
                    "  ,[tr_observ]--26\n" +
                    "  ,[tr_estad]--27\n" +
                    "      ,[mc_fecha]--28\n" +
                    "      ,[mc_num_orden]--29\n" +
                    "      ,[mc_deposito]--30\n" +
                    "      ,[mc_consecutivo_tqute]--31\n" +
                    "      ,[mc_placa_vehiculo]--32\n" +
                    "      ,[mc_peso_vacio]--33\n" +
                    "      ,[mc_peso_lleno]--34\n" +
                    "      ,[mc_peso_neto]--35\n" +
                    "      ,[mc_fecha_entrad]--36\n" +
                    "      ,[mc_fecha_salid]--37\n" +
                    "      ,[mc_fecha_inicio_descargue]--38\n" +
                    "      ,[mc_fecha_fin_descargue]--39\n" +
                    "      ,[mc_usuario_cdgo]--40\n" +
                    "      --Usuario Quien registra desde APP\n" +
                    "  ,us_registro_app.[us_cdgo] AS us_registro_app_cdgo--41\n" +
                    "  --,us_registro_app.[us_clave] AS us_registro_app_clave\n" +
                    "  ,us_registro_app.[us_nombres] AS us_registro_app_nombres--42\n" +
                    "  ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--43\n" +
                    "  ,us_registro_app.[us_perfil_cdgo] AS us_registro_app_perfil--44\n" +
                    "--Perfil Usuario Registra desde APP\n" +
                    ",prf_us_registro_app.[prf_cdgo] AS prf_us_registro_app_cdgo--45\n" +
                    ",prf_us_registro_app.[prf_desc] AS prf_us_registro_app_desc--46\n" +
                    ",prf_us_registro_app.[prf_estad] AS prf_us_registro_app_estad--47\n" +
                    "  ,us_registro_app.[us_correo] AS us_registro_app_correo--48\n" +
                    "  ,us_registro_app.[us_estad] AS us_registro_app_estad--49\n" +
                    "      ,[mc_observ]--50\n" +
                    "      ,[mc_estad_mvto_carbon_cdgo]--51\n" +
                    "  ,[emc_cdgo]--52\n" +
                    "  ,[emc_desc]--53\n" +
                    "  ,[emc_estad]--54\n" +
                    "      ,[mc_conexion_peso_ccarga]--55\n" +
                    "      ,[mc_registro_manual]--56\n" +
                    "      ,[mc_usuario_registro_manual_cdgo]--57\n" +
                    "  --Usuario Quien Registra Manual\n" +
                    "  ,us_registro_manual.[us_cdgo] AS us_registro_manual_cdgo--58\n" +
                    "  --,us_registro_manual.[us_clave] AS us_registro_manual_clave\n" +
                    "  ,us_registro_manual.[us_nombres] AS us_registro_manual_nombres--59\n" +
                    "  ,us_registro_manual.[us_apellidos] AS us_registro_manual_apellidos--60\n" +
                    "  ,us_registro_manual.[us_perfil_cdgo] AS us_registro_manual_perfil_cdgo--61\n" +
                    "--Perfil Usuario Registra Manual\n" +
                    ",prf_us_registro_manual.[prf_cdgo] AS prf_us_registro_manual_cdgo--62\n" +
                    ",prf_us_registro_manual.[prf_desc] AS prf_us_registro_manual_desc--63\n" +
                    ",prf_us_registro_manual.[prf_estad] AS prf_us_registro_manual_estad--64\n" +
                    "  ,us_registro_manual.[us_correo] AS us_registro_manual_correo--65\n" +
                    "  ,us_registro_manual.[us_estad] AS us_registro_manual_estad--66\n" +
                    "        ,[mc_cntro_cost_auxiliarDestino_cdgo]--67 \n" +
                    "        ,cca_destino.[cca_desc]--68 \n" +
                    "        ,[lr_cdgo]--69 \n" +
                    "        ,[lr_desc]--70 \n" +
                    ",[zt_cdgo]--71\n" +
                    "      ,[zt_desc]--72\n" +
                    "      ,[eq_cdgo]--73\n" +
                    "      ,[eq_desc]--74\n" +
                    "      ,[eq_modelo]--75\n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon] \n" +
                    " INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    " INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_origen ON [mc_cntro_cost_auxiliar_cdgo]= cca_origen.[cca_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_app ON  us_registro_app.[us_perfil_cdgo] =prf_us_registro_app.[prf_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_manual ON [mc_usuario_registro_manual_cdgo]=us_registro_manual.[us_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[perfil] prf_us_registro_manual ON  us_registro_manual.[us_perfil_cdgo] =prf_us_registro_manual.[prf_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[labor_realizada] ON [mc_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] cca_destino  ON [mc_cntro_cost_auxiliarDestino_cdgo] =cca_destino.[cca_cdgo] \n" +
                    "INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [mc_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    " LEFT JOIN ["+DB+"].[dbo].[equipo] ON [mc_equipo_lavado_cdgo] = [eq_cdgo] \n" +
                    "  WHERE  [mc_fecha] BETWEEN ? AND ? AND [mc_estad_mvto_carbon_cdgo]=1  AND [mc_usuario_cdgo]=? ORDER BY [mc_cdgo] DESC");

            queryBuscar.setString(1,fechaInicio);
            queryBuscar.setString(2,fechaFinal);
            queryBuscar.setString(3,us.getCodigo());
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(3));
                centroOperacion.setDescripcion(resultSetBuscar.getString(4));
                centroOperacion.setEstado(resultSetBuscar.getString(5));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(9));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(10));
                centroCostoSubCentro.setEstado(resultSetBuscar.getString(11));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(7));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(12));
                centroCostoAuxiliar.setEstado(resultSetBuscar.getString(13));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(15),resultSetBuscar.getString(16),resultSetBuscar.getString(17)));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(19),resultSetBuscar.getString(20),resultSetBuscar.getString(21)));
                Objeto.setTransportadora(new Transportadora(resultSetBuscar.getString(23),resultSetBuscar.getString(24),
                        resultSetBuscar.getString(25),resultSetBuscar.getString(26),
                        resultSetBuscar.getString(27)));
                Objeto.setFechaRegistro(resultSetBuscar.getString(28));
                Objeto.setNumero_orden(resultSetBuscar.getString(29));
                Objeto.setDeposito(resultSetBuscar.getString(30));
                Objeto.setConsecutivo(resultSetBuscar.getString(31));
                Objeto.setPlaca(resultSetBuscar.getString(32));
                Objeto.setPesoVacio(resultSetBuscar.getString(33));
                Objeto.setPesoLleno(resultSetBuscar.getString(34));
                Objeto.setPesoNeto(resultSetBuscar.getString(35));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(36));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(37));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(38));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(39));
                Perfil prf_Us_registroApp = new Perfil();
                prf_Us_registroApp.setCodigo(resultSetBuscar.getString(45));
                prf_Us_registroApp.setDescripcion(resultSetBuscar.getString(46));
                prf_Us_registroApp.setEstado(resultSetBuscar.getString(47));
                Usuario usuario = new Usuario();
                usuario.setCodigo(resultSetBuscar.getString(41));
                usuario.setNombres(resultSetBuscar.getString(42));
                usuario.setApellidos(resultSetBuscar.getString(43));
                usuario.setPerfilUsuario(prf_Us_registroApp);
                usuario.setCorreo(resultSetBuscar.getString(48));
                usuario.setEstado(resultSetBuscar.getString(49));
                Objeto.setUsuarioRegistroMovil(usuario);
                Objeto.setObservacion(resultSetBuscar.getString(50));
                EstadoMvtoCarbon estadoMvtoCarbon= new EstadoMvtoCarbon(resultSetBuscar.getString(52),resultSetBuscar.getString(53),resultSetBuscar.getString(54));
                Objeto.setEstadoMvtoCarbon(estadoMvtoCarbon);
                Objeto.setConexionPesoCcarga(resultSetBuscar.getString(55));

                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                centroCostoAuxiliarDestino.setCodigo(resultSetBuscar.getString(67));
                centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(68));
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSetBuscar.getString(69));
                laborRealizada.setDescripcion(resultSetBuscar.getString(70));
                Objeto.setLaborRealizada(laborRealizada);
                ZonaTrabajo zonaTrabajo = new ZonaTrabajo();
                zonaTrabajo.setCodigo(resultSetBuscar.getString(71));
                zonaTrabajo.setDescripcion(resultSetBuscar.getString(72));
                Objeto.setZonaTrabajo(zonaTrabajo);
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSetBuscar.getString(73));
                equipo.setDescripcion(resultSetBuscar.getString(74));
                equipo.setModelo(resultSetBuscar.getString(75));
                Objeto.setEquipoLavadoVehiculo(equipo);
                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }
    public ArrayList<MvtoCarbon_ListadoEquipos> reporteListadoEquiposMvtoCarbonPorPlaca_Backup(MvtoCarbon Objeto) throws SQLException{
        ArrayList<MvtoCarbon_ListadoEquipos> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [mcle_cdgo] --1\n" +
                    "      ,[mcle_mvto_carbon_cdgo] --2\n" +
                    "		  ,[mc_cdgo] --3\n" +
                    "		  ,[mc_cntro_oper_cdgo] --4\n" +
                    "				--Centro Operacion\n" +
                    "				,[co_cdgo] --5\n" +
                    "				,[co_desc] --6\n" +
                    "				,CASE [co_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [co_estad] --7\n" +
                    "		  ,[mc_cntro_cost_auxiliar_cdgo] --8\n" +
                    "				--Centro Costo Auxiliar\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cdgo] --9\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] --10\n" +
                    "						--Subcentro de Costo\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_cdgo] --11\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_desc]  --12\n" +
                    "						,CASE mc_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [ccs_estad] --13\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_desc] --14\n" +
                    "				,CASE mc_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [cca_estad] --15\n" +
                    "		  ,[mc_articulo_cdgo] --16\n" +
                    "				--Articulo\n" +
                    "				,mc_articulo.[ar_cdgo] --17\n" +
                    "				,mc_articulo.[ar_desc] --18\n" +
                    "				,CASE mc_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --19\n" +
                    "		  ,[mc_cliente_cdgo] --20\n" +
                    "				--Cliente\n" +
                    "				,mc_cliente.[cl_cdgo] --21\n" +
                    "				,mc_cliente.[cl_desc] --22\n" +
                    "				,CASE mc_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --23\n" +
                    "		  ,[mc_transprtdora_cdgo] --24\n" +
                    "				--Transportadora\n" +
                    "				,[tr_cdgo] --25\n" +
                    "				,[tr_nit] --26\n" +
                    "				,[tr_desc] --27\n" +
                    "				,[tr_observ] --28\n" +
                    "				,CASE [tr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [tr_estad] --29\n" +
                    "		  ,[mc_fecha] --30\n" +
                    "		  ,[mc_num_orden] --31\n" +
                    "		  ,[mc_deposito] --32\n" +
                    "		  ,[mc_consecutivo_tqute] --33\n" +
                    "		  ,[mc_placa_vehiculo] --34\n" +
                    "		  ,[mc_peso_vacio] --35\n" +
                    "		  ,[mc_peso_lleno] --36\n" +
                    "		  ,[mc_peso_neto] --37\n" +
                    "		  ,[mc_fecha_entrad] --38\n" +
                    "		  ,[mc_fecha_salid] --39\n" +
                    "		  ,[mc_fecha_inicio_descargue] --40\n" +
                    "		  ,[mc_fecha_fin_descargue] --41\n" +
                    "		  ,[mc_usuario_cdgo] --42\n" +
                    "				--Usuario Quien Registra desde App Movil\n" +
                    "				,user_registra.[us_cdgo] --43\n" +
                    "				,user_registra.[us_clave] --44\n" +
                    "				,user_registra.[us_nombres] --45\n" +
                    "				,user_registra.[us_apellidos] --46\n" +
                    "				,user_registra.[us_perfil_cdgo] --47\n" +
                    "					--Perfil Usuario Quien Registra\n" +
                    "					,prf_registra.[prf_cdgo] --48\n" +
                    "					,prf_registra.[prf_desc] --49\n" +
                    "					,CASE prf_registra.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --50\n" +
                    "				,user_registra.[us_correo] --51\n" +
                    "				,CASE user_registra.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --52\n" +
                    "		  ,[mc_observ] --53\n" +
                    "		  ,[mc_estad_mvto_carbon_cdgo] --54\n" +
                    "				--Estado MvtoCarbon\n" +
                    "				,[emc_cdgo] --55\n" +
                    "				,[emc_desc] --56\n" +
                    "				,CASE [emc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [emc_estad] --57\n" +
                    "		  ,CASE [mc_conexion_peso_ccarga] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [mc_conexion_peso_ccarga] --58\n" +
                    "		  ,[mc_registro_manual] --59\n" +
                    "		  ,[mc_usuario_registro_manual_cdgo] --60\n" +
                    "				--Usuario Quien Registra Manual\n" +
                    "				,user_registro_manual.[us_cdgo] --61\n" +
                    "				,user_registro_manual.[us_clave] --62\n" +
                    "				,user_registro_manual.[us_nombres] --63\n" +
                    "				,user_registro_manual.[us_apellidos] --64\n" +
                    "				,user_registro_manual.[us_perfil_cdgo] --65\n" +
                    "					--Perfil Usuario Quien Registra Manual\n" +
                    "					,prf_registra_manual.[prf_cdgo] --66\n" +
                    "					,prf_registra_manual.[prf_desc] --67\n" +
                    "					,CASE prf_registra_manual.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --68\n" +
                    "				,user_registro_manual.[us_correo] --69\n" +
                    "				,CASE user_registro_manual.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --70\n" +
                    "		,[mcle_asignacion_equipo_cdgo] --71\n" +
                    "				,[ae_cdgo] --72\n" +
                    "				,ae_cntro_oper_cdgo --73\n" +
                    "					--Centro Operacion\n" +
                    "					,ae_co_cdgo --74\n" +
                    "					,ae_co_desc --75\n" +
                    "					,CASE ae_co_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_co_estado --76\n" +
                    "				,ae_solicitud_listado_equipo_cdgo --77\n" +
                    "					-- Solicitud Listado Equipo\n" +
                    "					,ae_sle_cdgo --78\n" +
                    "					,ae_sle_solicitud_equipo_cdgo --79\n" +
                    "							--Solicitud Equipo\n" +
                    "							,ae_sle_se_cdgo --80\n" +
                    "							,ae_sle_se_cntro_oper_cdgo --81\n" +
                    "								--CentroOperación SolicitudEquipo\n" +
                    "								,ae_sle_se_co_cdgo --82\n" +
                    "								,ae_sle_se_co_desc --83\n" +
                    "								,CASE ae_sle_se_co_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_co_estad --84\n" +
                    "							,ae_sle_se_fecha --85\n" +
                    "							,ae_sle_se_usuario_realiza_cdgo --86\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_registra_cdgo --87\n" +
                    "								,ae_sle_se_us_registra_clave --88\n" +
                    "								,ae_sle_se_us_registra_nombres --89\n" +
                    "								,ae_sle_se_us_registra_apellidos --90\n" +
                    "								,ae_sle_se_us_registra_perfil_cdgo --91\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_registra_cdgo --92\n" +
                    "										,ae_sle_se_prf_us_registra_desc --93\n" +
                    "										,CASE ae_sle_se_prf_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_registra_estad --94\n" +
                    "								,ae_sle_se_us_registra_correo --95\n" +
                    "								,CASE ae_sle_se_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_registra_estad --96\n" +
                    "							,ae_sle_se_fecha_registro --97\n" +
                    "							,ae_sle_se_estado_solicitud_equipo_cdgo --98\n" +
                    "								--Estado de la solicitud\n" +
                    "								,ae_sle_se_ese_cdgo --99\n" +
                    "								,ae_sle_se_ese_desc --100\n" +
                    "								,CASE ae_sle_se_ese_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ese_estad --101\n" +
                    "							,ae_sle_se_fecha_confirmacion --102\n" +
                    "							,ae_se_usuario_confirma_cdgo --103\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_confirma_cdgo --104\n" +
                    "								,ae_sle_se_us_confirma_clave --105\n" +
                    "								,ae_sle_se_us_confirma_nombres --106\n" +
                    "								,ae_sle_se_us_confirma_apellidos --107\n" +
                    "								,ae_sle_se_us_confirma_perfil_cdgo --108\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_confirma_cdgo --109\n" +
                    "										,ae_sle_se_prf_us_confirma_desc --110\n" +
                    "										,CASE ae_sle_se_prf_us_confirma_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_confirma_estad --111\n" +
                    "								,ae_sle_se_us_confirma_correo --112\n" +
                    "								,CASE ae_sle_se_us_confirma_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_confirma_estado --113\n" +
                    "							,ae_sle_se_confirmacion_solicitud_equipo_cdgo --114\n" +
                    "								--Confirmacion solicitudEquipo\n" +
                    "								,ae_sle_se_cse_cdgo --115\n" +
                    "								,ae_sle_se_ces_desc --116\n" +
                    "								,CASE ae_sle_se_ces_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ces_estado --117\n" +
                    "					,ae_sle_tipo_equipo_cdgo --118\n" +
                    "						--Tipo de Equipo\n" +
                    "						,ae_sle_te_cdgo --119\n" +
                    "						,ae_sle_te_desc --120\n" +
                    "						,CASE ae_sle_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_te_estad --121\n" +
                    "					,ae_sle_marca_equipo --122\n" +
                    "					,ae_sle_modelo_equipo --123\n" +
                    "					,ae_sle_cant_equip --124\n" +
                    "					,ae_sle_observ --125\n" +
                    "					,ae_sle_fecha_hora_inicio --126\n" +
                    "					,ae_sle_fecha_hora_fin --127\n" +
                    "					,ae_sle_cant_minutos --128\n" +
                    "					,ae_sle_labor_realizada_cdgo --129\n" +
                    "					-- Labor Realizada\n" +
                    "							,ae_sle_lr_cdgo --130\n" +
                    "							,ae_sle_lr_desc --131\n" +
                    "							,CASE ae_sle_lr_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_lr_estad --132\n" +
                    "					,ae_sle_sle_motonave_cdgo --133\n" +
                    "					--Motonave\n" +
                    "							,ae_sle_mn_cdgo --134\n" +
                    "							,ae_sle_mn_desc --135\n" +
                    "							,CASE ae_sle_mn_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_mn_estad --136\n" +
                    "					,ae_sle_cntro_cost_auxiliar_cdgo --137\n" +
                    "						--Centro Costo Auxiliar\n" +
                    "						,ae_sle_cca_cdgo --138\n" +
                    "						,ae_sle_cca_cntro_cost_subcentro_cdgo --139\n" +
                    "							-- SubCentro de Costo\n" +
                    "							,ae_sle_ccs_cdgo --140\n" +
                    "							,ae_sle_ccs_desc --141\n" +
                    "							,CASE ae_sle_ccs_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_ccs_estad --142\n" +
                    "						,ae_sle_cca_desc --143\n" +
                    "						,CASE ae_sle_cca_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cca_estad --144\n" +
                    "					,ae_sle_compania_cdgo --145\n" +
                    "						--Compañia\n" +
                    "						,ae_sle_cp_cdgo --146\n" +
                    "						,ae_sle_cp_desc --147\n" +
                    "						,CASE ae_sle_cp_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cp_estad --148\n" +
                    "				,ae_fecha_registro --149\n" +
                    "				,ae_fecha_hora_inicio --150\n" +
                    "				,ae_fecha_hora_fin --151\n" +
                    "				,ae_cant_minutos --152\n" +
                    "				,ae_equipo_cdgo --153\n" +
                    "					--Equipo\n" +
                    "					,ae_eq_cdgo --154\n" +
                    "					,ae_eq_tipo_equipo_cdgo --155\n" +
                    "						--Tipo Equipo\n" +
                    "						,ae_eq_te_cdgo --156\n" +
                    "						,ae_eq_te_desc --157\n" +
                    "						,CASE ae_eq_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_te_estad --158\n" +
                    "					,ae_eq_codigo_barra --159\n" +
                    "					,ae_eq_referencia --160\n" +
                    "					,ae_eq_producto --161\n" +
                    "					,ae_eq_capacidad --162\n" +
                    "					,ae_eq_marca --163\n" +
                    "					,ae_eq_modelo --164\n" +
                    "					,ae_eq_serial --165\n" +
                    "					,ae_eq_desc --166\n" +
                    "					,ae_eq_clasificador1_cdgo --167\n" +
                    "						-- Clasificador 1\n" +
                    "						,ae_eq_ce1_cdgo --168\n" +
                    "						,ae_eq_ce1_desc --169\n" +
                    "						,CASE ae_eq_ce1_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce1_estad --170\n" +
                    "					,ae_eq_clasificador2_cdgo --171\n" +
                    "						-- Clasificador 2\n" +
                    "						,ae_eq_ce2_cdgo --172\n" +
                    "						,ae_eq_ce2_desc --173\n" +
                    "						,CASE ae_eq_ce2_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce2_estad --174\n" +
                    "					,ae_eq_proveedor_equipo_cdgo --175\n" +
                    "						--Proveedor Equipo\n" +
                    "						,ae_eq_pe_cdgo --176\n" +
                    "						,ae_eq_pe_nit --177\n" +
                    "						,ae_eq_pe_desc --178\n" +
                    "						,CASE ae_eq_pe_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_pe_estad --179\n" +
                    "					,ae_eq_equipo_pertenencia_cdgo --180\n" +
                    "						-- Equipo Pertenencia\n" +
                    "						,ae_eq_ep_cdgo --181\n" +
                    "						,ae_eq_ep_desc --182\n" +
                    "						,CASE ae_eq_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ep_estad --183\n" +
                    "					,ae_eq_eq_observ --184\n" +
                    "					,CASE ae_eq_eq_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_eq_estad --185\n" +
                    "					,ae_eq_actvo_fijo_id --186\n" +
                    "					,ae_eq_actvo_fijo_referencia --187\n" +
                    "					,ae_eq_actvo_fijo_desc --188\n" +
                    "				,ae_equipo_pertenencia_cdgo --189\n" +
                    "				-- Equipo Pertenencia\n" +
                    "						,ae_ep_cdgo --190\n" +
                    "						,ae_ep_desc --191\n" +
                    "						,CASE ae_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_ep_estad --192\n" +
                    "				,ae_cant_minutos_operativo --193\n" +
                    "				,ae_cant_minutos_parada --194\n" +
                    "				,ae_cant_minutos_total --195\n" +
                    "				,CASE ae_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_estad --196\n" +
                    "		,[mcle_mvto_equipo_cdgo] --197\n" +
                    "				--Movimiento Equipo\n" +
                    "				,[me_cdgo] --198\n" +
                    "				,[me_asignacion_equipo_cdgo] --199\n" +
                    "				,[me_fecha] --200\n" +
                    "				,[me_proveedor_equipo_cdgo] --201\n" +
                    "					--Proveedor Equipo\n" +
                    "					,[pe_cdgo] AS me_pe_cdgo --202\n" +
                    "					,[pe_nit] AS me_pe_nit --203\n" +
                    "					,[pe_desc] AS me_pe_desc --204\n" +
                    "					,CASE [pe_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS me_pe_estad --205\n" +
                    "				,[me_num_orden] --206\n" +
                    "				,[me_cntro_cost_auxiliar_cdgo] --207\n" +
                    "					-- Centro Costo Auxiliar\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cdgo] AS me_cca_cdgo --208\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] AS me_cca_cntro_cost_subcentro_cdgo --209\n" +
                    "						--Centro Costo Subcentro\n" +
                    "						,me_cntro_cost_subcentro.[ccs_cdgo] --210\n" +
                    "						,me_cntro_cost_subcentro.[ccs_desc] --211\n" +
                    "						,CASE me_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ccs_estad] --212\n" +
                    "					,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc --213\n" +
                    "					,CASE me_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cca_estad] --214\n" +
                    "				,[me_labor_realizada_cdgo] --215\n" +
                    "					--Labor Realizada\n" +
                    "					,[lr_cdgo]  --216\n" +
                    "					,[lr_desc] --217\n" +
                    "					,CASE [lr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [lr_estad] --218\n" +
                    "				,[me_cliente_cdgo] --219\n" +
                    "					--Cliente \n" +
                    "					,me_cliente.[cl_cdgo] --220\n" +
                    "					,me_cliente.[cl_desc] --221\n" +
                    "					,CASE me_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --222\n" +
                    "				,[me_articulo_cdgo] --223\n" +
                    "					--articulo\n" +
                    "					,me_articulo.[ar_cdgo] --224\n" +
                    "					,me_articulo.[ar_desc] --225\n" +
                    "					,CASE me_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --226\n" +
                    "				,[me_fecha_hora_inicio] --227\n" +
                    "				,[me_fecha_hora_fin] --228\n" +
                    "				,[me_total_minutos] --229\n" +
                    "				,[me_valor_hora] --230\n" +
                    "				,[me_costo_total] --231\n" +
                    "				,[me_recobro_cdgo] --232\n" +
                    "					--Recobro\n" +
                    "					,[rc_cdgo] --233\n" +
                    "					,[rc_desc] --234\n" +
                    "					,CASE [rc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [rc_estad] --235\n" +
                    "				,[me_cliente_recobro_cdgo] --236\n" +
                    "					--Cliente Recobro\n" +
                    "					,[clr_cdgo] --237\n" +
                    "					,[clr_cliente_cdgo] --238\n" +
                    "						--Cliente\n" +
                    "						,me_clr_cliente.[cl_cdgo] --239\n" +
                    "						,me_clr_cliente.[cl_desc] --240\n" +
                    "						,CASE me_clr_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --241\n" +
                    "					,[clr_usuario_cdgo] --242\n" +
                    "						--Usuario Quien Registra Recobro\n" +
                    "						,me_clr_usuario.[us_cdgo] --243\n" +
                    "						,me_clr_usuario.[us_clave] --244\n" +
                    "						,me_clr_usuario.[us_nombres] --245\n" +
                    "						,me_clr_usuario.[us_apellidos] --246\n" +
                    "						,me_clr_usuario.[us_perfil_cdgo] --247\n" +
                    "							--Perfil Usuario Registra recobro\n" +
                    "							,me_clr_us_perfil.[prf_cdgo] --248\n" +
                    "							,me_clr_us_perfil.[prf_desc] --249\n" +
                    "							,CASE me_clr_us_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --250\n" +
                    "						,me_clr_usuario.[us_correo] --251\n" +
                    "						,CASE me_clr_usuario.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --252\n" +
                    "					,[clr_valor_recobro] --253\n" +
                    "					,[clr_fecha_registro] --254\n" +
                    "				,[me_costo_total_recobro_cliente] --255\n" +
                    "				,[me_usuario_registro_cdgo] --256\n" +
                    "					--Usuario Quien Registra Equipo\n" +
                    "					,me_us_registro.[us_cdgo] --257\n" +
                    "					,me_us_registro.[us_clave] --258\n" +
                    "					,me_us_registro.[us_nombres] --259\n" +
                    "					,me_us_registro.[us_apellidos] --260\n" +
                    "					,me_us_registro.[us_perfil_cdgo] --261\n" +
                    "						--Perfil de Usuario Quien Registra Equipo\n" +
                    "						,me_us_regist_perfil.[prf_cdgo] --262\n" +
                    "						,me_us_regist_perfil.[prf_desc] --263\n" +
                    "						,CASE me_us_regist_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --264\n" +
                    "					,me_us_registro.[us_correo] --265\n" +
                    "					,CASE me_us_registro.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --266\n" +
                    "				,[me_usuario_autorizacion_cdgo] --267\n" +
                    "					,me_us_autorizacion.[us_cdgo] --268\n" +
                    "					,me_us_autorizacion.[us_clave] --269\n" +
                    "					,me_us_autorizacion.[us_nombres] --270\n" +
                    "					,me_us_autorizacion.[us_apellidos] --271\n" +
                    "					,me_us_autorizacion.[us_perfil_cdgo] --272\n" +
                    "						,me_us_autoriza_perfil.[prf_cdgo] --273\n" +
                    "						,me_us_autoriza_perfil.[prf_desc] --274\n" +
                    "						,CASE me_us_autoriza_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --275\n" +
                    "					,me_us_autorizacion.[us_correo] --276\n" +
                    "					,CASE me_us_autorizacion.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --277\n" +
                    "				,[me_autorizacion_recobro_cdgo] --278\n" +
                    "					,[are_cdgo] --279\n" +
                    "					,[are_desc] --280\n" +
                    "					,CASE [are_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [are_estad] --281\n" +
                    "				,[me_observ_autorizacion] --282\n" +
                    "				,[me_inactividad] --283\n" +
                    "				,[me_causa_inactividad_cdgo] --284\n" +
                    "					,[ci_cdgo] --285\n" +
                    "					,[ci_desc] --286\n" +
                    "					,CASE [ci_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ci_estad] --287\n" +
                    "				,[me_usuario_inactividad_cdgo] --288\n" +
                    "					,me_us_inactividad.[us_cdgo] --289\n" +
                    "					,me_us_inactividad.[us_clave] --290\n" +
                    "					,me_us_inactividad.[us_nombres] --291\n" +
                    "					,me_us_inactividad.[us_apellidos] --292\n" +
                    "					,me_us_inactividad.[us_perfil_cdgo] --293\n" +
                    "						,me_us_inactvdad_perfil.[prf_cdgo] --294\n" +
                    "						,me_us_inactvdad_perfil.[prf_desc] --295\n" +
                    "						,CASE me_us_inactvdad_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --296\n" +
                    "					,me_us_inactividad.[us_correo] --297\n" +
                    "					,CASE me_us_inactividad.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]	 --298\n" +
                    "                           ,[me_motivo_parada_cdgo]--299\n" +
                    "                                       ,[mpa_cdgo]--300\n" +
                    "                                       ,[mpa_desc]--301\n" +
                    "                                       ,[mpa_estad]--302\n" +
                    "                         ,[me_observ] --303\n" +
                    "				,CASE [me_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [me_estado]	 --304\n" +
                    "				,CASE [me_desde_mvto_carbon] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [me_desde_mvto_carbon]	 --305\n" +
                    "		,CASE [mcle_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [mcle_estado]	 --306\n" +
                    "            ,Tiempo_Vehiculo_Descargue=(SELECT (DATEDIFF (MINUTE,  [mc_fecha_inicio_descargue], [mc_fecha_fin_descargue])))--307\n" +
                    "           ,Tiempo_Equipo_Descargue=(SELECT (DATEDIFF (MINUTE,  [me_fecha_hora_inicio], [me_fecha_hora_fin]))) --308\n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliar ON [mc_cntro_cost_auxiliar_cdgo]=mc_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] mc_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]= mc_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[articulo] mc_articulo ON [mc_articulo_cdgo]=mc_articulo.[ar_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[cliente] mc_cliente ON [mc_cliente_cdgo]=mc_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[usuario] user_registra ON [mc_usuario_cdgo] = user_registra.[us_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[perfil] prf_registra ON user_registra.[us_perfil_cdgo]=prf_registra.[prf_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[usuario] user_registro_manual ON [mc_usuario_registro_manual_cdgo] = user_registro_manual.[us_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[perfil] prf_registra_manual ON user_registro_manual.[us_perfil_cdgo]=prf_registra_manual.[prf_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[mvto_carbon_listado_equipo] ON [mcle_mvto_carbon_cdgo]=[mc_cdgo]\n" +
                    "	INNER JOIN (SELECT [ae_cdgo] AS ae_cdgo\n" +
                    "					  ,[ae_cntro_oper_cdgo] AS ae_cntro_oper_cdgo\n" +
                    "							--Centro Operacion\n" +
                    "							,ae_cntro_oper.[co_cdgo] AS ae_co_cdgo\n" +
                    "							,ae_cntro_oper.[co_desc] AS ae_co_desc\n" +
                    "							,ae_cntro_oper.[co_estad] AS ae_co_estado\n" +
                    "					  ,[ae_solicitud_listado_equipo_cdgo] AS ae_solicitud_listado_equipo_cdgo\n" +
                    "							-- Solicitud Listado Equipo\n" +
                    "							,[sle_cdgo] AS ae_sle_cdgo\n" +
                    "							,[sle_solicitud_equipo_cdgo] AS ae_sle_solicitud_equipo_cdgo\n" +
                    "								  --Solicitud Equipo\n" +
                    "								  ,[se_cdgo] AS ae_sle_se_cdgo\n" +
                    "								  ,[se_cntro_oper_cdgo] AS ae_sle_se_cntro_oper_cdgo\n" +
                    "										--CentroOperación SolicitudEquipo\n" +
                    "										,se_cntro_oper.[co_cdgo] AS ae_sle_se_co_cdgo\n" +
                    "										,se_cntro_oper.[co_desc] AS ae_sle_se_co_desc\n" +
                    "										,se_cntro_oper.[co_estad] AS ae_sle_se_co_estad\n" +
                    "								  ,[se_fecha] AS ae_sle_se_fecha\n" +
                    "								  ,[se_usuario_realiza_cdgo] AS ae_sle_se_usuario_realiza_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_realiza.[us_cdgo] AS ae_sle_se_us_registra_cdgo\n" +
                    "										,se_usuario_realiza.[us_clave] AS ae_sle_se_us_registra_clave\n" +
                    "										,se_usuario_realiza.[us_nombres] AS ae_sle_se_us_registra_nombres\n" +
                    "										,se_usuario_realiza.[us_apellidos] AS ae_sle_se_us_registra_apellidos\n" +
                    "										,se_usuario_realiza.[us_perfil_cdgo] AS ae_sle_se_us_registra_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra.[prf_cdgo] AS ae_sle_se_prf_us_registra_cdgo\n" +
                    "											,ae_prf_registra.[prf_desc] AS ae_sle_se_prf_us_registra_desc\n" +
                    "											,ae_prf_registra.[prf_estad] AS ae_sle_se_prf_us_registra_estad\n" +
                    "										,se_usuario_realiza.[us_correo] AS ae_sle_se_us_registra_correo\n" +
                    "										,se_usuario_realiza.[us_estad] AS ae_sle_se_us_registra_estad\n" +
                    "								  ,[se_fecha_registro] AS ae_sle_se_fecha_registro\n" +
                    "								  ,[se_estado_solicitud_equipo_cdgo] AS ae_sle_se_estado_solicitud_equipo_cdgo\n" +
                    "										--Estado de la solicitud\n" +
                    "										,[ese_cdgo] AS ae_sle_se_ese_cdgo\n" +
                    "										,[ese_desc] AS ae_sle_se_ese_desc\n" +
                    "										,[ese_estad] AS ae_sle_se_ese_estad\n" +
                    "								  ,[se_fecha_confirmacion] AS ae_sle_se_fecha_confirmacion\n" +
                    "								  ,[se_usuario_confirma_cdgo] AS ae_se_usuario_confirma_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_confirma.[us_cdgo] AS ae_sle_se_us_confirma_cdgo\n" +
                    "										,se_usuario_confirma.[us_clave] AS ae_sle_se_us_confirma_clave\n" +
                    "										,se_usuario_confirma.[us_nombres] AS ae_sle_se_us_confirma_nombres\n" +
                    "										,se_usuario_confirma.[us_apellidos] AS ae_sle_se_us_confirma_apellidos\n" +
                    "										,se_usuario_confirma.[us_perfil_cdgo] AS ae_sle_se_us_confirma_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra_confirma.[prf_cdgo] AS ae_sle_se_prf_us_confirma_cdgo\n" +
                    "											,ae_prf_registra_confirma.[prf_desc] AS ae_sle_se_prf_us_confirma_desc\n" +
                    "											,ae_prf_registra_confirma.[prf_estad] AS ae_sle_se_prf_us_confirma_estad\n" +
                    "										,se_usuario_confirma.[us_correo] AS ae_sle_se_us_confirma_correo\n" +
                    "										,se_usuario_confirma.[us_estad] AS ae_sle_se_us_confirma_estado\n" +
                    "								  ,[se_confirmacion_solicitud_equipo_cdgo] AS ae_sle_se_confirmacion_solicitud_equipo_cdgo\n" +
                    "										--Confirmacion solicitudEquipo\n" +
                    "										,[cse_cdgo] AS ae_sle_se_cse_cdgo\n" +
                    "										,[cse_desc] AS ae_sle_se_ces_desc\n" +
                    "										,[cse_estad] AS ae_sle_se_ces_estado\n" +
                    "							,[sle_tipo_equipo_cdgo] AS ae_sle_tipo_equipo_cdgo\n" +
                    "								--Tipo de Equipo\n" +
                    "								,sle_tipoEquipo.[te_cdgo] AS ae_sle_te_cdgo\n" +
                    "								,sle_tipoEquipo.[te_desc] AS ae_sle_te_desc\n" +
                    "								,sle_tipoEquipo.[te_estad] AS ae_sle_te_estad\n" +
                    "							,[sle_marca_equipo] AS ae_sle_marca_equipo\n" +
                    "							,[sle_modelo_equipo] AS ae_sle_modelo_equipo\n" +
                    "							,[sle_cant_equip] AS ae_sle_cant_equip\n" +
                    "							,[sle_observ] AS ae_sle_observ\n" +
                    "							,[sle_fecha_hora_inicio] AS ae_sle_fecha_hora_inicio\n" +
                    "							,[sle_fecha_hora_fin] AS ae_sle_fecha_hora_fin\n" +
                    "							,[sle_cant_minutos] AS ae_sle_cant_minutos\n" +
                    "							,[sle_labor_realizada_cdgo] AS ae_sle_labor_realizada_cdgo\n" +
                    "							-- Labor Realizada\n" +
                    "								  ,[lr_cdgo] AS ae_sle_lr_cdgo\n" +
                    "								  ,[lr_desc] AS ae_sle_lr_desc\n" +
                    "								  ,[lr_estad] AS ae_sle_lr_estad\n" +
                    "							,[sle_motonave_cdgo] AS ae_sle_sle_motonave_cdgo\n" +
                    "							--Motonave\n" +
                    "								  ,[mn_cdgo] AS ae_sle_mn_cdgo\n" +
                    "								  ,[mn_desc] AS ae_sle_mn_desc\n" +
                    "								  ,[mn_estad] AS ae_sle_mn_estad\n" +
                    "							,[sle_cntro_cost_auxiliar_cdgo] AS ae_sle_cntro_cost_auxiliar_cdgo\n" +
                    "								--Centro Costo Auxiliar\n" +
                    "								,[cca_cdgo] AS ae_sle_cca_cdgo\n" +
                    "								,[cca_cntro_cost_subcentro_cdgo] AS ae_sle_cca_cntro_cost_subcentro_cdgo\n" +
                    "									-- SubCentro de Costo\n" +
                    "									,[ccs_cdgo] AS ae_sle_ccs_cdgo\n" +
                    "									,[ccs_desc] AS ae_sle_ccs_desc\n" +
                    "									,[ccs_estad] AS ae_sle_ccs_estad\n" +
                    "								,[cca_desc] AS ae_sle_cca_desc\n" +
                    "								,[cca_estad] AS ae_sle_cca_estad\n" +
                    "							,[sle_compania_cdgo] AS ae_sle_compania_cdgo\n" +
                    "								--Compañia\n" +
                    "								,[cp_cdgo] AS ae_sle_cp_cdgo\n" +
                    "								,[cp_desc] AS ae_sle_cp_desc\n" +
                    "								,[cp_estad] AS ae_sle_cp_estad\n" +
                    "					  ,[ae_fecha_registro] AS ae_fecha_registro\n" +
                    "					  ,[ae_fecha_hora_inicio] AS ae_fecha_hora_inicio\n" +
                    "					  ,[ae_fecha_hora_fin] AS ae_fecha_hora_fin\n" +
                    "					  ,[ae_cant_minutos] AS ae_cant_minutos\n" +
                    "					  ,[ae_equipo_cdgo] AS ae_equipo_cdgo\n" +
                    "							--Equipo\n" +
                    "							,[eq_cdgo] AS ae_eq_cdgo\n" +
                    "							,[eq_tipo_equipo_cdgo] AS ae_eq_tipo_equipo_cdgo\n" +
                    "								--Tipo Equipo\n" +
                    "								,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "								,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc\n" +
                    "								,eq_tipo_equipo.[te_estad] AS ae_eq_te_estad\n" +
                    "							,[eq_codigo_barra] AS ae_eq_codigo_barra\n" +
                    "							,[eq_referencia] AS ae_eq_referencia\n" +
                    "							,[eq_producto] AS ae_eq_producto\n" +
                    "							,[eq_capacidad] AS ae_eq_capacidad\n" +
                    "							,[eq_marca] AS ae_eq_marca\n" +
                    "							,[eq_modelo] AS ae_eq_modelo\n" +
                    "							,[eq_serial] AS ae_eq_serial\n" +
                    "							,[eq_desc] AS ae_eq_desc\n" +
                    "							,[eq_clasificador1_cdgo] AS ae_eq_clasificador1_cdgo\n" +
                    "								-- Clasificador 1\n" +
                    "								,eq_clasificador1.[ce_cdgo] AS ae_eq_ce1_cdgo\n" +
                    "								,eq_clasificador1.[ce_desc] AS ae_eq_ce1_desc\n" +
                    "								,eq_clasificador1.[ce_estad] AS ae_eq_ce1_estad\n" +
                    "							,[eq_clasificador2_cdgo] AS ae_eq_clasificador2_cdgo\n" +
                    "								-- Clasificador 2\n" +
                    "								,eq_clasificador2.[ce_cdgo] AS ae_eq_ce2_cdgo\n" +
                    "								,eq_clasificador2.[ce_desc] AS ae_eq_ce2_desc\n" +
                    "								,eq_clasificador2.[ce_estad] AS ae_eq_ce2_estad\n" +
                    "							,[eq_proveedor_equipo_cdgo] AS ae_eq_proveedor_equipo_cdgo\n" +
                    "								--Proveedor Equipo\n" +
                    "								,[pe_cdgo] AS ae_eq_pe_cdgo\n" +
                    "								,[pe_nit] AS ae_eq_pe_nit\n" +
                    "								,[pe_desc] AS ae_eq_pe_desc\n" +
                    "								,[pe_estad] AS ae_eq_pe_estad\n" +
                    "							,[eq_equipo_pertenencia_cdgo] AS ae_eq_equipo_pertenencia_cdgo\n" +
                    "								-- Equipo Pertenencia\n" +
                    "								,eq_pertenencia.[ep_cdgo] AS ae_eq_ep_cdgo\n" +
                    "								,eq_pertenencia.[ep_desc] AS ae_eq_ep_desc\n" +
                    "								,eq_pertenencia.[ep_estad] AS ae_eq_ep_estad\n" +
                    "							,[eq_observ] AS ae_eq_eq_observ\n" +
                    "							,[eq_estad] AS ae_eq_eq_estad\n" +
                    "							,[eq_actvo_fijo_id] AS ae_eq_actvo_fijo_id\n" +
                    "							,[eq_actvo_fijo_referencia] AS ae_eq_actvo_fijo_referencia\n" +
                    "							,[eq_actvo_fijo_desc] AS ae_eq_actvo_fijo_desc\n" +
                    "					  ,[ae_equipo_pertenencia_cdgo] AS ae_equipo_pertenencia_cdgo\n" +
                    "						-- Equipo Pertenencia\n" +
                    "								,ae_pertenencia.[ep_cdgo] AS ae_ep_cdgo\n" +
                    "								,ae_pertenencia.[ep_desc] AS ae_ep_desc\n" +
                    "								,ae_pertenencia.[ep_estad]	 AS ae_ep_estad\n" +
                    "					  ,[ae_cant_minutos_operativo] AS ae_cant_minutos_operativo\n" +
                    "					  ,[ae_cant_minutos_parada] AS ae_cant_minutos_parada\n" +
                    "					  ,[ae_cant_minutos_total] AS ae_cant_minutos_total\n" +
                    "					  ,[ae_estad] AS ae_estad\n" +
                    "					  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra ON se_usuario_realiza.[us_perfil_cdgo]=ae_prf_registra.[prf_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra_confirma ON se_usuario_confirma.[us_perfil_cdgo]=ae_prf_registra_confirma.[prf_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "							LEFT  JOIN["+DB+"].[dbo].[motonave] ON [sle_motonave_cdgo]=[mn_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]	\n" +
                    "	) asignacion_equipo ON [mcle_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                    "	--Movimiento Equipo\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[mvto_equipo] ON [mcle_mvto_equipo_cdgo]=[me_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[articulo] me_articulo ON [me_articulo_cdgo]=me_articulo.[ar_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[recobro] ON [me_recobro_cdgo]=[rc_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente_recobro] ON [me_cliente_recobro_cdgo]=[clr_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente] me_clr_cliente ON [clr_cliente_cdgo]=me_clr_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_clr_usuario ON [clr_usuario_cdgo]=me_clr_usuario.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_clr_us_perfil ON me_clr_usuario.[us_perfil_cdgo]=me_clr_us_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_regist_perfil ON me_us_registro.[us_perfil_cdgo]=me_us_regist_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_autorizacion ON [me_usuario_autorizacion_cdgo]=me_us_autorizacion.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_autoriza_perfil ON me_us_autorizacion.[us_perfil_cdgo]=me_us_autoriza_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[autorizacion_recobro] ON [me_autorizacion_recobro_cdgo]=[are_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[causa_inactividad] ON [me_causa_inactividad_cdgo]=[ci_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_inactividad ON [me_usuario_inactividad_cdgo]=me_us_inactividad.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_inactvdad_perfil ON me_us_inactividad.[us_perfil_cdgo]=me_us_inactvdad_perfil.[prf_cdgo]\n" +
                    "   LEFT JOIN  ["+DB+"].[dbo].[motivo_parada] ON [me_motivo_parada_cdgo]= [mpa_cdgo]\n"  +
                    "	WHERE [mc_cdgo]=? AND mc_placa_vehiculo =?  AND [mc_fecha_entrad]=? AND  [me_inactividad]=0 AND  [mc_estad_mvto_carbon_cdgo]=1  ORDER BY [me_cdgo] DESC");
            query.setString(1, Objeto.getCodigo());
            query.setString(2, Objeto.getPlaca());
            query.setString(3, Objeto.getFechaEntradaVehiculo());
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                MvtoCarbon_ListadoEquipos mvto_listEquipo = new MvtoCarbon_ListadoEquipos();
                mvto_listEquipo.setCodigo(resultSet.getString(1));
                MvtoCarbon mvtoCarbon = new MvtoCarbon();
                mvtoCarbon.setCodigo(resultSet.getString(3));
                mvtoCarbon.setCentroOperacion(new CentroOperacion(Integer.parseInt(resultSet.getString(5)),resultSet.getString(6),resultSet.getString(7)));
                mvtoCarbon.setCentroCostoAuxiliar(new CentroCostoAuxiliar(resultSet.getString(9),new CentroCostoSubCentro(Integer.parseInt(resultSet.getString(11)),resultSet.getString(12),resultSet.getString(13)),resultSet.getString(14),resultSet.getString(15)));
                mvtoCarbon.setArticulo(new Articulo(resultSet.getString(17),resultSet.getString(18),resultSet.getString(19)));
                mvtoCarbon.setCliente(new Cliente(resultSet.getString(21),resultSet.getString(22),resultSet.getString(23)));
                mvtoCarbon.setTransportadora(new Transportadora(resultSet.getString(25),resultSet.getString(26),resultSet.getString(27),resultSet.getString(28),resultSet.getString(29)));
                mvtoCarbon.setFechaRegistro(resultSet.getString(30));
                mvtoCarbon.setNumero_orden(resultSet.getString(31));
                mvtoCarbon.setDeposito(resultSet.getString(32));
                mvtoCarbon.setConsecutivo(resultSet.getString(33));
                mvtoCarbon.setPlaca(resultSet.getString(34));
                mvtoCarbon.setPesoVacio(resultSet.getString(35));
                mvtoCarbon.setPesoLleno(resultSet.getString(36));
                mvtoCarbon.setPesoNeto(resultSet.getString(37));
                mvtoCarbon.setFechaEntradaVehiculo(resultSet.getString(38));
                mvtoCarbon.setFecha_SalidaVehiculo(resultSet.getString(39));
                mvtoCarbon.setFechaInicioDescargue(resultSet.getString(40));
                mvtoCarbon.setFechaFinDescargue(resultSet.getString(41));
                Usuario us = new Usuario();
                us.setCodigo(resultSet.getString(43));
                //us.setClave(resultSet.getString(44));
                us.setNombres(resultSet.getString(45));
                us.setApellidos(resultSet.getString(46));
                us.setPerfilUsuario(new Perfil(resultSet.getString(48),resultSet.getString(49),resultSet.getString(50)));
                us.setCorreo(resultSet.getString(51));
                us.setEstado(resultSet.getString(52));
                mvtoCarbon.setUsuarioRegistroMovil(us);
                mvtoCarbon.setObservacion(resultSet.getString(53));
                EstadoMvtoCarbon estadMvtoCarbon = new EstadoMvtoCarbon();
                estadMvtoCarbon.setCodigo(resultSet.getString(55));
                estadMvtoCarbon.setDescripcion(resultSet.getString(56));
                estadMvtoCarbon.setEstado(resultSet.getString(57));
                mvtoCarbon.setEstadoMvtoCarbon(estadMvtoCarbon);
                mvtoCarbon.setConexionPesoCcarga(resultSet.getString(58));
                mvtoCarbon.setRegistroManual(resultSet.getString(59));
                Usuario usRegManual = new Usuario();
                usRegManual.setCodigo(resultSet.getString(61));
                //usRegManual.setClave(resultSet.getString(62));
                usRegManual.setNombres(resultSet.getString(63));
                usRegManual.setApellidos(resultSet.getString(64));
                usRegManual.setPerfilUsuario(new Perfil(resultSet.getString(66),resultSet.getString(67),resultSet.getString(68)));
                usRegManual.setCorreo(resultSet.getString(69));
                usRegManual.setEstado(resultSet.getString(70));
                mvtoCarbon.setUsuarioRegistraManual(usRegManual);
                mvtoCarbon.setCantidadHorasDescargue(resultSet.getString(303));
                mvto_listEquipo.setMvtoCarbon(mvtoCarbon);
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(72));
                CentroOperacion co= new CentroOperacion();
                co.setCodigo(Integer.parseInt(resultSet.getString(74)));
                co.setDescripcion(resultSet.getString(75));
                co.setEstado(resultSet.getString(76));
                asignacionEquipo.setCentroOperacion(co);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(78));
                SolicitudEquipo solicitudEquipo= new SolicitudEquipo();
                solicitudEquipo.setCodigo(resultSet.getString(80));
                CentroOperacion co_se= new CentroOperacion();
                co_se.setCodigo(Integer.parseInt(resultSet.getString(82)));
                co_se.setDescripcion(resultSet.getString(83));
                co_se.setEstado(resultSet.getString(84));
                solicitudEquipo.setCentroOperacion(co_se);
                solicitudEquipo.setFechaSolicitud(resultSet.getString(85));
                Usuario us_se = new Usuario();
                us_se.setCodigo(resultSet.getString(87));
                //us_se.setClave(resultSet.getString(88));
                us_se.setNombres(resultSet.getString(89));
                us_se.setApellidos(resultSet.getString(90));
                us_se.setPerfilUsuario(new Perfil(resultSet.getString(92),resultSet.getString(93),resultSet.getString(94)));
                us_se.setCorreo(resultSet.getString(95));
                us_se.setEstado(resultSet.getString(96));
                solicitudEquipo.setUsuarioRealizaSolicitud(us_se);
                solicitudEquipo.setFechaRegistro(resultSet.getString(97));
                EstadoSolicitudEquipos estadoSolicitudEquipos = new EstadoSolicitudEquipos();
                estadoSolicitudEquipos.setCodigo(resultSet.getString(99));
                estadoSolicitudEquipos.setDescripcion(resultSet.getString(100));
                estadoSolicitudEquipos.setEstado(resultSet.getString(101));
                solicitudEquipo.setEstadoSolicitudEquipo(estadoSolicitudEquipos);
                solicitudEquipo.setFechaConfirmacion(resultSet.getString(102));
                Usuario us_se_confirm = new Usuario();
                us_se_confirm.setCodigo(resultSet.getString(104));
                //us_se_confirm.setClave(resultSet.getString(105));
                us_se_confirm.setNombres(resultSet.getString(106));
                us_se_confirm.setApellidos(resultSet.getString(107));
                us_se_confirm.setPerfilUsuario(new Perfil(resultSet.getString(109),resultSet.getString(110),resultSet.getString(111)));
                us_se_confirm.setCorreo(resultSet.getString(112));
                us_se_confirm.setEstado(resultSet.getString(113));
                solicitudEquipo.setUsuarioConfirmacionSolicitud(us_se_confirm);
                ConfirmacionSolicitudEquipos confirmacionSolicitudEquipos = new ConfirmacionSolicitudEquipos();
                confirmacionSolicitudEquipos.setCodigo(resultSet.getString(115));
                confirmacionSolicitudEquipos.setDescripcion(resultSet.getString(116));
                confirmacionSolicitudEquipos.setEstado(resultSet.getString(117));
                solicitudEquipo.setConfirmacionSolicitudEquipo(confirmacionSolicitudEquipos);
                solicitudListadoEquipo.setSolicitudEquipo(solicitudEquipo);
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(119));
                tipoEquipo.setDescripcion(resultSet.getString(120));
                tipoEquipo.setEstado(resultSet.getString(121));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(122));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(123));
                solicitudListadoEquipo.setCantidad(Integer.parseInt(resultSet.getString(124)));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(125));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(126));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(127));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(128));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(130));
                laborRealizada.setDescripcion(resultSet.getString(131));
                laborRealizada.setEstado(resultSet.getString(132));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave = new Motonave();
                motonave.setCodigo(resultSet.getString(134));
                motonave.setDescripcion(resultSet.getString(135));
                motonave.setEstado(resultSet.getString(136));
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(140));
                centroCostoSubCentro.setDescripcion(resultSet.getString(141));
                centroCostoSubCentro.setEstado(resultSet.getString(142));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(138));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(143));
                centroCostoAuxiliar.setEstado(resultSet.getString(144));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compania = new Compañia();
                compania.setCodigo(resultSet.getString(146));
                compania.setDescripcion(resultSet.getString(147));
                compania.setEstado(resultSet.getString(148));
                solicitudListadoEquipo.setCompañia(compania);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(149));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(150));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(151));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(152));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(154));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(156),resultSet.getString(157),resultSet.getString(158)));
                equipo.setCodigo_barra(resultSet.getString(159));
                equipo.setReferencia(resultSet.getString(160));
                equipo.setProducto(resultSet.getString(161));
                equipo.setCapacidad(resultSet.getString(162));
                equipo.setMarca(resultSet.getString(163));
                equipo.setModelo(resultSet.getString(164));
                equipo.setSerial(resultSet.getString(165));
                equipo.setDescripcion(resultSet.getString(166));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(168),resultSet.getString(169),resultSet.getString(170)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(172),resultSet.getString(173),resultSet.getString(174)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(176),resultSet.getString(177),resultSet.getString(178),resultSet.getString(179)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(181),resultSet.getString(182),resultSet.getString(183)));
                equipo.setObservacion(resultSet.getString(184));
                equipo.setEstado(resultSet.getString(185));
                equipo.setActivoFijo_codigo(resultSet.getString(186));
                equipo.setActivoFijo_referencia(resultSet.getString(187));
                equipo.setActivoFijo_descripcion(resultSet.getString(188));
                asignacionEquipo.setEquipo(equipo);
                asignacionEquipo.setPertenencia(new Pertenencia(resultSet.getString(190),resultSet.getString(191),resultSet.getString(192)));
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(193));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(194));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(195));
                asignacionEquipo.setEstado(resultSet.getString(196));
                mvto_listEquipo.setAsignacionEquipo(asignacionEquipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(198));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaRegistro(resultSet.getString(200));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(202),resultSet.getString(203),resultSet.getString(204),resultSet.getString(205)));
                mvtoEquipo.setNumeroOrden(resultSet.getString(206));
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setCodigo(resultSet.getInt(210));
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(211));
                centroCostoSubCentro_mvtoEquipo.setEstado(resultSet.getString(212));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setCodigo(resultSet.getString(208));
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(213));
                centroCostoAuxiliar_mvtoEquipo.setEstado(resultSet.getString(214));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setCodigo(resultSet.getString(216));
                laborRealizadaT.setDescripcion(resultSet.getString(217));
                laborRealizadaT.setEstado(resultSet.getString(218));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente(resultSet.getString(220),resultSet.getString(221),resultSet.getString(222)));
                mvtoEquipo.setArticulo(new Articulo(resultSet.getString(224),resultSet.getString(225),resultSet.getString(226)));
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(227));
                mvtoEquipo.setFechaHoraFin(resultSet.getString(228));
                mvtoEquipo.setTotalMinutos(resultSet.getString(229));
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(230));
                Recobro recobro = new Recobro();
                recobro.setCodigo(resultSet.getString(233));
                recobro.setDescripcion(resultSet.getString(234));
                recobro.setEstado(resultSet.getString(235));
                mvtoEquipo.setRecobro(recobro);
                ClienteRecobro ClienteRecobro = new ClienteRecobro();
                ClienteRecobro.setCodigo(resultSet.getString(235));
                Cliente cliente_recobro = new Cliente();
                cliente_recobro.setCodigo(resultSet.getString(239));
                cliente_recobro.setDescripcion(resultSet.getString(240));
                cliente_recobro.setEstado(resultSet.getString(241));
                ClienteRecobro.setCliente(cliente_recobro);
                Usuario usuario_recobre = new Usuario();
                usuario_recobre.setCodigo(resultSet.getString(243));
                //usuario_recobre.setClave(resultSet.getString(244));
                usuario_recobre.setNombres(resultSet.getString(245));
                usuario_recobre.setApellidos(resultSet.getString(246));
                usuario_recobre.setPerfilUsuario(new Perfil(resultSet.getString(248),resultSet.getString(249),resultSet.getString(250)));
                usuario_recobre.setCorreo(resultSet.getString(251));
                usuario_recobre.setEstado(resultSet.getString(252));
                ClienteRecobro.setUsuario(usuario_recobre);
                ClienteRecobro.setValorRecobro(resultSet.getString(253));
                ClienteRecobro.setFechaRegistro(resultSet.getString(254));
                mvtoEquipo.setClienteRecobro(ClienteRecobro);
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(255));
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setCodigo(resultSet.getString(257));
                //usuario_me_registra.setClave(resultSet.getString(258));
                usuario_me_registra.setNombres(resultSet.getString(259));
                usuario_me_registra.setApellidos(resultSet.getString(260));
                usuario_me_registra.setPerfilUsuario(new Perfil(resultSet.getString(262),resultSet.getString(263),resultSet.getString(264)));
                usuario_me_registra.setCorreo(resultSet.getString(265));
                usuario_me_registra.setEstado(resultSet.getString(266));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                Usuario usuario_me_autoriza = new Usuario();
                usuario_me_autoriza.setCodigo(resultSet.getString(268));
                //usuario_me_autoriza.setClave(resultSet.getString(269));
                usuario_me_autoriza.setNombres(resultSet.getString(270));
                usuario_me_autoriza.setApellidos(resultSet.getString(271));
                usuario_me_autoriza.setPerfilUsuario(new Perfil(resultSet.getString(273),resultSet.getString(274),resultSet.getString(275)));
                usuario_me_autoriza.setCorreo(resultSet.getString(276));
                usuario_me_autoriza.setEstado(resultSet.getString(277));
                mvtoEquipo.setUsuarioAutorizaRecobro(usuario_me_autoriza);
                AutorizacionRecobro autorizacionRecobro = new AutorizacionRecobro();
                autorizacionRecobro.setCodigo(resultSet.getString(279));
                autorizacionRecobro.setDescripcion(resultSet.getString(280));
                autorizacionRecobro.setEstado(resultSet.getString(281));
                mvtoEquipo.setAutorizacionRecobro(autorizacionRecobro);
                mvtoEquipo.setObservacionAutorizacion(resultSet.getString(282));
                mvtoEquipo.setInactividad(resultSet.getString(283));
                CausaInactividad causaInactividad = new CausaInactividad();
                causaInactividad.setCodigo(resultSet.getString(285));
                causaInactividad.setDescripcion(resultSet.getString(286));
                causaInactividad.setEstado(resultSet.getString(287));
                mvtoEquipo.setCausaInactividad(causaInactividad);
                Usuario usuario_me_us_inactividad = new Usuario();
                usuario_me_us_inactividad.setCodigo(resultSet.getString(289));
                //usuario_me_us_inactividad.setClave(resultSet.getString(290));
                usuario_me_us_inactividad.setNombres(resultSet.getString(291));
                usuario_me_us_inactividad.setApellidos(resultSet.getString(292));
                usuario_me_us_inactividad.setPerfilUsuario(new Perfil(resultSet.getString(294),resultSet.getString(295),resultSet.getString(296)));
                usuario_me_us_inactividad.setCorreo(resultSet.getString(297));
                usuario_me_us_inactividad.setEstado(resultSet.getString(298));
                mvtoEquipo.setUsuarioInactividad(usuario_me_us_inactividad);
                MotivoParada motivoParada= new MotivoParada();
                motivoParada.setCodigo(resultSet.getString(300));
                motivoParada.setDescripcion(resultSet.getString(301));
                motivoParada.setEstado(resultSet.getString(302));
                mvtoEquipo.setMotivoParada(motivoParada);
                mvtoEquipo.setObservacionMvtoEquipo(resultSet.getString(303));
                mvtoEquipo.setEstado(resultSet.getString(304));
                mvtoEquipo.setDesdeCarbon(resultSet.getString(305));
                mvtoEquipo.setTotalMinutos(resultSet.getString(307));
                mvto_listEquipo.setMvtoEquipo(mvtoEquipo);
                mvto_listEquipo.setEstado(resultSet.getString(306));
                listadoObjetos.add(mvto_listEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public int registrarMvtoCarbon(MvtoCarbon Objeto) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                String estado;
                if (Objeto.getEstadoMvtoCarbon().getEstado().equalsIgnoreCase("1")) {
                    estado = "ACTIVO";
                } else {
                    estado = "INACTIVO";
                }
                try {
                    if(Objeto.getArticulo() != null) {
                        if (!validarExistenciaArticulo(Objeto.getArticulo())) {
                            int n = registrarArticulo(Objeto.getArticulo(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un articulo nuevo en el sistema");
                            }
                        }
                    }
                    if(Objeto.getCliente() != null) {
                        if (!validarExistenciaCliente(Objeto.getCliente())) {
                            int n = registrarCliente(Objeto.getCliente(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado un nuevo cliente en el sistema");
                            }
                        }
                    }
                    if(Objeto.getTransportadora() != null) {
                        if (!validarExistenciaTransportadora(Objeto.getTransportadora())) {
                            int n = registrarTransportadora(Objeto.getTransportadora(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva transportadora en el sistema");
                            }
                        }
                    }
                    if(Objeto.getMotonave() != null) {
                        if (!validarExistenciaMotonave(Objeto.getMotonave())) {
                            int n = registrarMotonave(Objeto.getMotonave(), Objeto.getUsuarioRegistroMovil());
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva motonave en el sistema");
                            }
                        }
                    }
                }catch(Exception e){
                    System.out.println("Error validandos datos y registrados nuevos Items, Cliente, Motonave, Transportadora, Articulo");
                }
                conexion= control.ConectarBaseDatos();
                //Objeto.getCliente().setCodigo("'" + Objeto.getCliente().getCodigo() + "'");
                //Objeto.getTransportadora().setCodigo("'" + Objeto.getTransportadora().getCodigo() + "'");
                //Objeto.getArticulo().setCodigo("'" + Objeto.getArticulo().getCodigo() + "'");
                //Objeto.setNumero_orden("'" + Objeto.getNumero_orden() + "'");
                //Objeto.setDeposito("'" + Objeto.getDeposito() + "'");
                //Objeto.setPlaca("'" + Objeto.getPlaca() + "'");
                //Funcional Codigo
                if(validarExistenciaMvtoCarbon(Objeto)){
                    result = 3;
                }else {
                    conexion= control.ConectarBaseDatos();
                    PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                            //"DECLARE @fechaEntrada smalldatetime = "+Objeto.getFechaEntrada()+";" +
                            //"DECLARE @fechaSalida smalldatetime = "+Objeto.getFechaSalida()+";    " +
                            "INSERT INTO [" + DB + "].[dbo].[mvto_carbon]" +
                            "           ([mc_cdgo],[mc_cntro_oper_cdgo],[mc_cntro_cost_auxiliar_cdgo],[mc_labor_realizada_cdgo],[mc_articulo_cdgo],[mc_cliente_cdgo],[mc_transprtdora_cdgo]" +
                            "           ,[mc_fecha],[mc_num_orden],[mc_deposito],[mc_consecutivo_tqute],[mc_placa_vehiculo],[mc_peso_vacio],[mc_peso_lleno]" +
                            "           ,[mc_peso_neto],[mc_fecha_entrad],[mc_fecha_salid],[mc_fecha_inicio_descargue],[mc_fecha_fin_descargue],[mc_usuario_cdgo],[mc_observ]" +
                            "           ,[mc_estad_mvto_carbon_cdgo],[mc_conexion_peso_ccarga],[mc_registro_manual],[mc_usuario_registro_manual_cdgo])" +
                            "     VALUES ((SELECT (CASE WHEN (MAX([mc_cdgo]) IS NULL) THEN 1 ELSE (MAX([mc_cdgo])+1) END)AS [mc_cdgo] FROM ["+DB+"].[dbo].[mvto_carbon])," + Objeto.getCentroOperacion().getCodigo() + "," + Objeto.getCentroCostoAuxiliar().getCodigo() +"," + Objeto.getLaborRealizada().getCodigo() +
                            ",'" + Objeto.getArticulo().getCodigo() + "','" + Objeto.getCliente().getCodigo() + "','" + Objeto.getTransportadora().getCodigo() + "',(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))" +
                            ",'" + Objeto.getNumero_orden() + "','" + Objeto.getDeposito() + "'," + Objeto.getConsecutivo() + ",'" + Objeto.getPlaca() + "'," + Objeto.getPesoVacio() + "" +
                            "," + Objeto.getPesoLleno() + "," + Objeto.getPesoNeto() + ",'"+Objeto.getFechaEntradaVehiculo()+"',"+Objeto.getFecha_SalidaVehiculo()+
                            "," +Objeto.getFechaInicioDescargue()+ ",NULL," +Objeto.getUsuarioRegistroMovil().getCodigo() + "," + Objeto.getObservacion() + "," + Objeto.getEstadoMvtoCarbon().getCodigo() + "," + Objeto.getConexionPesoCcarga() + ",NULL,NULL);");
                    queryRegistrarT.execute();

                    result = 1;
                    if (result == 1) {
                        result = 0;
                        //Extraemos el nombre del Dispositivo y la IP
                        String namePc=new ControlDB_Config().getNamePC();
                        String ipPc=new ControlDB_Config().getIpPc();
                        String macPC=new ControlDB_Config().getMacAddress();

                        PreparedStatement queryMaximo = conexion.prepareStatement("SELECT MAX(mc_cdgo) FROM [" + DB + "].[dbo].[mvto_carbon];");
                        ResultSet resultSetMaximo = queryMaximo.executeQuery();
                        while (resultSetMaximo.next()) {
                            if (resultSetMaximo.getString(1) != null) {
                                Objeto.setCodigo(resultSetMaximo.getString(1));
                            }
                        }
                        PreparedStatement Query_AuditoriaInsert = conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[auditoria]([au_cdgo]\n" +
                                "      ,[au_fecha]\n" +
                                "      ,[au_usuario_cdgo_registro]\n" +
                                "      ,[au_nombre_dispositivo_registro]\n" +
                                "      ,[au_ip_dispositivo_registro]\n" +
                                "      ,[au_mac_dispositivo_registro]\n" +
                                "      ,[au_cdgo_mtvo]\n" +
                                "      ,[au_desc_mtvo]\n" +
                                "      ,[au_detalle_mtvo])\n" +
                                "     VALUES("+
                                "           (SELECT (CASE WHEN (MAX([au_cdgo]) IS NULL) THEN 1 ELSE (MAX([au_cdgo])+1) END)AS [au_cdgo] FROM ["+DB+"].[dbo].[auditoria])"+
                                "           ,(SELECT SYSDATETIME())"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,?"+
                                "           ,'DESCARGUE_CARBON'" +
                                "           ,CONCAT (?,?,?));");
                        Query_AuditoriaInsert.setString(1, Objeto.getUsuarioRegistroMovil().getCodigo());
                        Query_AuditoriaInsert.setString(2, namePc);
                        Query_AuditoriaInsert.setString(3, ipPc);
                        Query_AuditoriaInsert.setString(4, macPC);
                        Query_AuditoriaInsert.setString(5, Objeto.getCodigo());
                        Query_AuditoriaInsert.setString(6, "Se registró un nuevo Movimiento de Carbon en el sistema desde un Dispositivo Movil, con código: ");
                        Query_AuditoriaInsert.setString(7, Objeto.getCodigo());
                        Query_AuditoriaInsert.setString(8, " PLACA: " + Objeto.getPlaca() + " Orden: " + Objeto.getNumero_orden() + " Deposito: " + Objeto.getDeposito() + " Articulo: " + Objeto.getArticulo().getDescripcion() + " Peso Vacio: " + Objeto.getPesoVacio());
                        Query_AuditoriaInsert.execute();
                        result = 1;
                    }
                }
                System.out.println("Resultado de result->"+result);
            } catch (SQLException sqlException) {
                result = 0;
                sqlException.printStackTrace();
            }
            control.cerrarConexionBaseDatos();
        }else{
            return 2;
        }
        return result;
    }
    public ArrayList<Equipo> buscarEquipos(String tipoEquipo, String marcaEquipo) throws SQLException {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<Equipo> listadoObjeto = null;
        conexion= control.ConectarBaseDatos();
        try{
            ResultSet resultSetBuscar;

            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT \n" +
                    "	   [eq_cdgo] --1\n" +
                    "      ,[eq_tipo_equipo_cdgo] --2\n" +
                    "           ,[te_cdgo] --3\n" +
                    "           ,[te_desc] --4\n" +
                    "           ,[te_estad] --5\n" +
                    "      ,[eq_referencia] --6\n" +
                    "      ,[eq_producto] --7\n" +
                    "      ,[eq_capacidad] --8\n" +
                    "      ,[eq_marca] --9\n" +
                    "      ,[eq_modelo] --10\n" +
                    "      ,[eq_serial] --11\n" +
                    "      ,[eq_desc]  --12\n" +
                    "      ,[eq_clasificador1_cdgo] --13\n" +
                    "           ,clasificador1.[ce_cdgo] AS clasificador1_cdgo --14\n" +
                    "           ,clasificador1.[ce_desc] AS clasificador1_desc  --15\n" +
                    "           ,clasificador1.[ce_estad] AS clasificador1_estad --16\n" +
                    "      ,[eq_clasificador2_cdgo] --17\n" +
                    "           ,clasificador2.[ce_cdgo] AS clasificador2_cdgo --18\n" +
                    "           ,clasificador2.[ce_desc] AS clasificador2_desc --19\n" +
                    "           ,clasificador2.[ce_estad] AS clasificador2_estad --20\n" +
                    "      ,[eq_proveedor_equipo_cdgo] --21\n" +
                    "      ,[eq_proveedor_equipo_cdgo] --22\n" +
                    "           ,[pe_cdgo] --23\n" +
                    "           ,[pe_desc] --24\n" +
                    "           ,[pe_estad] --25\n" +
                    "      ,[eq_equipo_pertenencia_cdgo] --26\n" +
                    "           ,[ep_cdgo] --27\n" +
                    "           ,[ep_desc] --28\n" +
                    "           ,[ep_estad] --29\n" +
                    "      ,[eq_observ] --30\n" +
                    "      ,[eq_codigo_barra] --31\n" +
                    "      ,CASE WHEN (eq_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS eq_estad --32\n" +
                    "  FROM ["+DB+"].[dbo].[equipo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] ON te_cdgo = eq_tipo_equipo_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON pe_cdgo= eq_proveedor_equipo_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] clasificador1 ON clasificador1.ce_cdgo = eq_clasificador1_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] clasificador2 ON clasificador2.ce_cdgo = eq_clasificador2_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] ON ep_cdgo=eq_equipo_pertenencia_cdgo "+
                    " WHERE eq_estad=1 AND te_cdgo=? AND eq_marca LIKE ?;");
            queryBuscar.setString(1, tipoEquipo);
            queryBuscar.setString(2, marcaEquipo);
            resultSetBuscar= queryBuscar.executeQuery();
            listadoObjeto= new ArrayList();
            while(resultSetBuscar.next()){
                Equipo Objeto = new Equipo();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                Objeto.setTipoEquipo(new TipoEquipo(resultSetBuscar.getString(3),resultSetBuscar.getString(4),resultSetBuscar.getString(5)));
                Objeto.setCodigo_barra(resultSetBuscar.getString(31));
                Objeto.setReferencia(resultSetBuscar.getString(6));
                Objeto.setProducto(resultSetBuscar.getString(7));
                Objeto.setCapacidad(resultSetBuscar.getString(8));
                Objeto.setMarca(resultSetBuscar.getString(9));
                Objeto.setModelo(resultSetBuscar.getString(10));
                Objeto.setSerial(resultSetBuscar.getString(11));
                Objeto.setDescripcion(resultSetBuscar.getString(12));
                Objeto.setClasificador1(new ClasificadorEquipo(resultSetBuscar.getString(14),resultSetBuscar.getString(15),resultSetBuscar.getString(16)));
                Objeto.setClasificador2(new ClasificadorEquipo(resultSetBuscar.getString(18),resultSetBuscar.getString(19),resultSetBuscar.getString(20)));
                Objeto.setProveedorEquipo(new ProveedorEquipo(resultSetBuscar.getString(23),resultSetBuscar.getString(24),resultSetBuscar.getString(25)));
                Objeto.setPertenenciaEquipo(new Pertenencia(resultSetBuscar.getString(27),resultSetBuscar.getString(28),resultSetBuscar.getString(29)));
                Objeto.setObservacion(resultSetBuscar.getString(30));
                Objeto.setEstado(resultSetBuscar.getString(32));
                listadoObjeto.add(Objeto);
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjeto;
    }
    public ArrayList<TipoEquipo> listarTipoEquipo() throws SQLException{
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
    }
    public ArrayList<String> listarMarcaEquipo() throws SQLException{
        ArrayList<String> listadoMarcaEquipo = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT [eq_marca] FROM ["+DB+"].[dbo].[equipo] GROUP BY [eq_marca];");
            ResultSet resultSetBuscar=queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                listadoMarcaEquipo.add(resultSetBuscar.getString(1));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMarcaEquipo;
    }
    public ArrayList<MvtoCarbon> buscarMtvo_Carbon(String placa) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<MvtoCarbon> listadoMvtoCarbon= new ArrayList<>();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [mc_cdgo]--1 \n" +
                    "\t\t,[co_cdgo]--2 \n" +
                    "\t\t,[co_desc]--3 \n" +
                    "\t\t,[cca_cdgo]--4 \n" +
                    "\t\t,[cca_cntro_cost_subcentro_cdgo]--5 \n" +
                    "\t\t,[ccs_cdgo]--6\n" +
                    "\t\t,[ccs_desc]--7 \n" +
                    "\t\t,[cca_desc]--8 \n" +
                    "\t\t,[ar_cdgo]--9 \n" +
                    "\t\t,[ar_desc]--10 \n" +
                    "\t\t,[cl_cdgo]--11 \n" +
                    "\t\t,[cl_desc]--12 \n" +
                    "\t\t,[tr_desc]--13 \n" +
                    "\t\t,[mc_num_orden]--14 \n" +
                    "\t\t,[mc_deposito]--15 \n" +
                    "\t\t,[mc_placa_vehiculo]--16 \n" +
                    "\t\t,[mc_peso_vacio]--17 \n" +
                    "\t\t,[mc_peso_lleno]--18 \n" +
                    "\t\t,[mc_peso_neto]--19\n" +
                    "\t\t,[mc_fecha_entrad]--20 \n" +
                    "\t\t,[mc_fecha_salid]--21 \n" +
                    "\t\t,[mc_fecha_inicio_descargue]--22 \n" +
                    "\t\t,[mc_fecha_fin_descargue]--23      \n" +
                    "        ,us_registro_app.[us_nombres] AS us_registro_app_nombres--24 \n" +
                    "        ,us_registro_app.[us_apellidos] AS us_registro_app_apellidos--25 \n" +
                    "FROM ["+DB+"].[dbo].[mvto_carbon]  \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [mc_cntro_cost_auxiliar_cdgo]=[cca_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[articulo] ON [mc_articulo_cdgo]=[ar_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[cliente] ON [mc_cliente_cdgo]=[cl_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo] \n" +
                    "        LEFT JOIN ["+DB+"].[dbo].[usuario] us_registro_app ON [mc_usuario_cdgo]=us_registro_app.[us_cdgo]             \n" +
                    "    WHERE [mc_fecha_fin_descargue] IS NULL  \n" +
                    "    AND [mc_estad_mvto_carbon_cdgo]=1 AND [mc_placa_vehiculo] LIKE ?\n" +
                    "\t\tORDER BY mc_placa_vehiculo ASC");

            String d= ""+placa+"%";
            queryBuscar.setString(1, d);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                MvtoCarbon Objeto= new MvtoCarbon();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                CentroOperacion centroOperacion= new CentroOperacion();
                centroOperacion.setCodigo(resultSetBuscar.getInt(2));
                centroOperacion.setDescripcion(resultSetBuscar.getString(3));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSetBuscar.getInt(6));
                centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(7));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(4));
                centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(8));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Objeto.setArticulo(new Articulo(resultSetBuscar.getString(9),resultSetBuscar.getString(10),""));
                Objeto.setCliente(new Cliente(resultSetBuscar.getString(11),resultSetBuscar.getString(12),""));
                Objeto.setTransportadora(new Transportadora("","",
                        resultSetBuscar.getString(13),"",
                        ""));
                Objeto.setNumero_orden(resultSetBuscar.getString(14));
                Objeto.setDeposito(resultSetBuscar.getString(15));
                Objeto.setPlaca(resultSetBuscar.getString(16));
                Objeto.setPesoVacio(resultSetBuscar.getString(17));
                Objeto.setPesoLleno(resultSetBuscar.getString(18));
                Objeto.setPesoNeto(resultSetBuscar.getString(19));
                Objeto.setFechaEntradaVehiculo(resultSetBuscar.getString(20));
                Objeto.setFecha_SalidaVehiculo(resultSetBuscar.getString(21));
                Objeto.setFechaInicioDescargue(resultSetBuscar.getString(22));
                Objeto.setFechaFinDescargue(resultSetBuscar.getString(23));
                Usuario usuario = new Usuario();
                usuario.setNombres(resultSetBuscar.getString(24));
                usuario.setApellidos(resultSetBuscar.getString(25));
                Objeto.setUsuarioRegistroMovil(usuario);
                listadoMvtoCarbon.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoCarbon;
    }
    public ArrayList<MvtoCarbon_ListadoEquipos> buscarEquipo_EnMovimientoCarbon_Activos_old(Equipo equipoI) throws SQLException{
        ArrayList<MvtoCarbon_ListadoEquipos> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT TOP 100\n" +
                    "[mcle_cdgo] --1\n" +
                    "      ,[mcle_mvto_carbon_cdgo] --2\n" +
                    "		  ,[mc_cdgo] --3\n" +
                    "		  ,[mc_cntro_oper_cdgo] --4\n" +
                    "				--Centro Operacion\n" +
                    "				,[co_cdgo] --5\n" +
                    "				,[co_desc] --6\n" +
                    "				,CASE [co_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [co_estad] --7\n" +
                    "		  ,[mc_cntro_cost_auxiliar_cdgo] --8\n" +
                    "				--Centro Costo Auxiliar\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cdgo] --9\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] --10\n" +
                    "						--Subcentro de Costo\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_cdgo] --11\n" +
                    "						,mc_cntro_cost_subcentro.[ccs_desc]  --12\n" +
                    "						,CASE mc_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [ccs_estad] --13\n" +
                    "				,mc_cntro_cost_auxiliar.[cca_desc] --14\n" +
                    "				,CASE mc_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN NULL ELSE NULL END AS [cca_estad] --15\n" +
                    "		  ,[mc_articulo_cdgo] --16\n" +
                    "				--Articulo\n" +
                    "				,mc_articulo.[ar_cdgo] --17\n" +
                    "				,mc_articulo.[ar_desc] --18\n" +
                    "				,CASE mc_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --19\n" +
                    "		  ,[mc_cliente_cdgo] --20\n" +
                    "				--Cliente\n" +
                    "				,mc_cliente.[cl_cdgo] --21\n" +
                    "				,mc_cliente.[cl_desc] --22\n" +
                    "				,CASE mc_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --23\n" +
                    "		  ,[mc_transprtdora_cdgo] --24\n" +
                    "				--Transportadora\n" +
                    "				,[tr_cdgo] --25\n" +
                    "				,[tr_nit] --26\n" +
                    "				,[tr_desc] --27\n" +
                    "				,[tr_observ] --28\n" +
                    "				,CASE [tr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [tr_estad] --29\n" +
                    "		  ,[mc_fecha] --30\n" +
                    "		  ,[mc_num_orden] --31\n" +
                    "		  ,[mc_deposito] --32\n" +
                    "		  ,[mc_consecutivo_tqute] --33\n" +
                    "		  ,[mc_placa_vehiculo] --34\n" +
                    "		  ,[mc_peso_vacio] --35\n" +
                    "		  ,[mc_peso_lleno] --36\n" +
                    "		  ,[mc_peso_neto] --37\n" +
                    "		  ,[mc_fecha_entrad] --38\n" +
                    "		  ,[mc_fecha_salid] --39\n" +
                    "		  ,[mc_fecha_inicio_descargue] --40\n" +
                    "		  ,[mc_fecha_fin_descargue] --41\n" +
                    "		  ,[mc_usuario_cdgo] --42\n" +
                    "				--Usuario Quien Registra desde App Movil\n" +
                    "				,user_registra.[us_cdgo] --43\n" +
                    "				,user_registra.[us_clave] --44\n" +
                    "				,user_registra.[us_nombres] --45\n" +
                    "				,user_registra.[us_apellidos] --46\n" +
                    "				,user_registra.[us_perfil_cdgo] --47\n" +
                    "					--Perfil Usuario Quien Registra\n" +
                    "					,prf_registra.[prf_cdgo] --48\n" +
                    "					,prf_registra.[prf_desc] --49\n" +
                    "					,CASE prf_registra.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --50\n" +
                    "				,user_registra.[us_correo] --51\n" +
                    "				,CASE user_registra.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --52\n" +
                    "		  ,[mc_observ] --53\n" +
                    "		  ,[mc_estad_mvto_carbon_cdgo] --54\n" +
                    "				--Estado MvtoCarbon\n" +
                    "				,[emc_cdgo] --55\n" +
                    "				,[emc_desc] --56\n" +
                    "				,CASE [emc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [emc_estad] --57\n" +
                    "		  ,CASE [mc_conexion_peso_ccarga] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [mc_conexion_peso_ccarga] --58\n" +
                    "		  ,[mc_registro_manual] --59\n" +
                    "		  ,[mc_usuario_registro_manual_cdgo] --60\n" +
                    "				--Usuario Quien Registra Manual\n" +
                    "				,user_registro_manual.[us_cdgo] --61\n" +
                    "				,user_registro_manual.[us_clave] --62\n" +
                    "				,user_registro_manual.[us_nombres] --63\n" +
                    "				,user_registro_manual.[us_apellidos] --64\n" +
                    "				,user_registro_manual.[us_perfil_cdgo] --65\n" +
                    "					--Perfil Usuario Quien Registra Manual\n" +
                    "					,prf_registra_manual.[prf_cdgo] --66\n" +
                    "					,prf_registra_manual.[prf_desc] --67\n" +
                    "					,CASE prf_registra_manual.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --68\n" +
                    "				,user_registro_manual.[us_correo] --69\n" +
                    "				,CASE user_registro_manual.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --70\n" +
                    "		,[mcle_asignacion_equipo_cdgo] --71\n" +
                    "				,[ae_cdgo] --72\n" +
                    "				,ae_cntro_oper_cdgo --73\n" +
                    "					--Centro Operacion\n" +
                    "					,ae_co_cdgo --74\n" +
                    "					,ae_co_desc --75\n" +
                    "					,CASE ae_co_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_co_estado --76\n" +
                    "				,ae_solicitud_listado_equipo_cdgo --77\n" +
                    "					-- Solicitud Listado Equipo\n" +
                    "					,ae_sle_cdgo --78\n" +
                    "					,ae_sle_solicitud_equipo_cdgo --79\n" +
                    "							--Solicitud Equipo\n" +
                    "							,ae_sle_se_cdgo --80\n" +
                    "							,ae_sle_se_cntro_oper_cdgo --81\n" +
                    "								--CentroOperación SolicitudEquipo\n" +
                    "								,ae_sle_se_co_cdgo --82\n" +
                    "								,ae_sle_se_co_desc --83\n" +
                    "								,CASE ae_sle_se_co_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_co_estad --84\n" +
                    "							,ae_sle_se_fecha --85\n" +
                    "							,ae_sle_se_usuario_realiza_cdgo --86\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_registra_cdgo --87\n" +
                    "								,ae_sle_se_us_registra_clave --88\n" +
                    "								,ae_sle_se_us_registra_nombres --89\n" +
                    "								,ae_sle_se_us_registra_apellidos --90\n" +
                    "								,ae_sle_se_us_registra_perfil_cdgo --91\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_registra_cdgo --92\n" +
                    "										,ae_sle_se_prf_us_registra_desc --93\n" +
                    "										,CASE ae_sle_se_prf_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_registra_estad --94\n" +
                    "								,ae_sle_se_us_registra_correo --95\n" +
                    "								,CASE ae_sle_se_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_registra_estad --96\n" +
                    "							,ae_sle_se_fecha_registro --97\n" +
                    "							,ae_sle_se_estado_solicitud_equipo_cdgo --98\n" +
                    "								--Estado de la solicitud\n" +
                    "								,ae_sle_se_ese_cdgo --99\n" +
                    "								,ae_sle_se_ese_desc --100\n" +
                    "								,CASE ae_sle_se_ese_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ese_estad --101\n" +
                    "							,ae_sle_se_fecha_confirmacion --102\n" +
                    "							,ae_se_usuario_confirma_cdgo --103\n" +
                    "								--Usuario SolicitudEquipo\n" +
                    "								,ae_sle_se_us_confirma_cdgo --104\n" +
                    "								,ae_sle_se_us_confirma_clave --105\n" +
                    "								,ae_sle_se_us_confirma_nombres --106\n" +
                    "								,ae_sle_se_us_confirma_apellidos --107\n" +
                    "								,ae_sle_se_us_confirma_perfil_cdgo --108\n" +
                    "										--Perfil Usuario Quien Registra Manual\n" +
                    "										,ae_sle_se_prf_us_confirma_cdgo --109\n" +
                    "										,ae_sle_se_prf_us_confirma_desc --110\n" +
                    "										,CASE ae_sle_se_prf_us_confirma_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_confirma_estad --111\n" +
                    "								,ae_sle_se_us_confirma_correo --112\n" +
                    "								,CASE ae_sle_se_us_confirma_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_confirma_estado --113\n" +
                    "							,ae_sle_se_confirmacion_solicitud_equipo_cdgo --114\n" +
                    "								--Confirmacion solicitudEquipo\n" +
                    "								,ae_sle_se_cse_cdgo --115\n" +
                    "								,ae_sle_se_ces_desc --116\n" +
                    "								,CASE ae_sle_se_ces_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ces_estado --117\n" +
                    "					,ae_sle_tipo_equipo_cdgo --118\n" +
                    "						--Tipo de Equipo\n" +
                    "						,ae_sle_te_cdgo --119\n" +
                    "						,ae_sle_te_desc --120\n" +
                    "						,CASE ae_sle_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_te_estad --121\n" +
                    "					,ae_sle_marca_equipo --122\n" +
                    "					,ae_sle_modelo_equipo --123\n" +
                    "					,ae_sle_cant_equip --124\n" +
                    "					,ae_sle_observ --125\n" +
                    "					,ae_sle_fecha_hora_inicio --126\n" +
                    "					,ae_sle_fecha_hora_fin --127\n" +
                    "					,ae_sle_cant_minutos --128\n" +
                    "					,ae_sle_labor_realizada_cdgo --129\n" +
                    "					-- Labor Realizada\n" +
                    "							,ae_sle_lr_cdgo --130\n" +
                    "							,ae_sle_lr_desc --131\n" +
                    "							,CASE ae_sle_lr_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_lr_estad --132\n" +
                    "					,ae_sle_sle_motonave_cdgo --133\n" +
                    "					--Motonave\n" +
                    "							,ae_sle_mn_cdgo --134\n" +
                    "							,ae_sle_mn_desc --135\n" +
                    "							,CASE ae_sle_mn_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_mn_estad --136\n" +
                    "					,ae_sle_cntro_cost_auxiliar_cdgo --137\n" +
                    "						--Centro Costo Auxiliar\n" +
                    "						,ae_sle_cca_cdgo --138\n" +
                    "						,ae_sle_cca_cntro_cost_subcentro_cdgo --139\n" +
                    "							-- SubCentro de Costo\n" +
                    "							,ae_sle_ccs_cdgo --140\n" +
                    "							,ae_sle_ccs_desc --141\n" +
                    "							,CASE ae_sle_ccs_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_ccs_estad --142\n" +
                    "						,ae_sle_cca_desc --143\n" +
                    "						,CASE ae_sle_cca_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cca_estad --144\n" +
                    "					,ae_sle_compania_cdgo --145\n" +
                    "						--Compañia\n" +
                    "						,ae_sle_cp_cdgo --146\n" +
                    "						,ae_sle_cp_desc --147\n" +
                    "						,CASE ae_sle_cp_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cp_estad --148\n" +
                    "				,ae_fecha_registro --149\n" +
                    "				,ae_fecha_hora_inicio --150\n" +
                    "				,ae_fecha_hora_fin --151\n" +
                    "				,ae_cant_minutos --152\n" +
                    "				,ae_equipo_cdgo --153\n" +
                    "					--Equipo\n" +
                    "					,ae_eq_cdgo --154\n" +
                    "					,ae_eq_tipo_equipo_cdgo --155\n" +
                    "						--Tipo Equipo\n" +
                    "						,ae_eq_te_cdgo --156\n" +
                    "						,ae_eq_te_desc --157\n" +
                    "						,CASE ae_eq_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_te_estad --158\n" +
                    "					,ae_eq_codigo_barra --159\n" +
                    "					,ae_eq_referencia --160\n" +
                    "					,ae_eq_producto --161\n" +
                    "					,ae_eq_capacidad --162\n" +
                    "					,ae_eq_marca --163\n" +
                    "					,ae_eq_modelo --164\n" +
                    "					,ae_eq_serial --165\n" +
                    "					,ae_eq_desc --166\n" +
                    "					,ae_eq_clasificador1_cdgo --167\n" +
                    "						-- Clasificador 1\n" +
                    "						,ae_eq_ce1_cdgo --168\n" +
                    "						,ae_eq_ce1_desc --169\n" +
                    "						,CASE ae_eq_ce1_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce1_estad --170\n" +
                    "					,ae_eq_clasificador2_cdgo --171\n" +
                    "						-- Clasificador 2\n" +
                    "						,ae_eq_ce2_cdgo --172\n" +
                    "						,ae_eq_ce2_desc --173\n" +
                    "						,CASE ae_eq_ce2_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce2_estad --174\n" +
                    "					,ae_eq_proveedor_equipo_cdgo --175\n" +
                    "						--Proveedor Equipo\n" +
                    "						,ae_eq_pe_cdgo --176\n" +
                    "						,ae_eq_pe_nit --177\n" +
                    "						,ae_eq_pe_desc --178\n" +
                    "						,CASE ae_eq_pe_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_pe_estad --179\n" +
                    "					,ae_eq_equipo_pertenencia_cdgo --180\n" +
                    "						-- Equipo Pertenencia\n" +
                    "						,ae_eq_ep_cdgo --181\n" +
                    "						,ae_eq_ep_desc --182\n" +
                    "						,CASE ae_eq_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ep_estad --183\n" +
                    "					,ae_eq_eq_observ --184\n" +
                    "					,CASE ae_eq_eq_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_eq_estad --185\n" +
                    "					,ae_eq_actvo_fijo_id --186\n" +
                    "					,ae_eq_actvo_fijo_referencia --187\n" +
                    "					,ae_eq_actvo_fijo_desc --188\n" +
                    "				,ae_equipo_pertenencia_cdgo --189\n" +
                    "				-- Equipo Pertenencia\n" +
                    "						,ae_ep_cdgo --190\n" +
                    "						,ae_ep_desc --191\n" +
                    "						,CASE ae_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_ep_estad --192\n" +
                    "				,ae_cant_minutos_operativo --193\n" +
                    "				,ae_cant_minutos_parada --194\n" +
                    "				,ae_cant_minutos_total --195\n" +
                    "				,CASE ae_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_estad --196\n" +
                    "		,[mcle_mvto_equipo_cdgo] --197\n" +
                    "				--Movimiento Equipo\n" +
                    "				,[me_cdgo] --198\n" +
                    "				,[me_asignacion_equipo_cdgo] --199\n" +
                    "				,[me_fecha] --200\n" +
                    "				,[me_proveedor_equipo_cdgo] --201\n" +
                    "					--Proveedor Equipo\n" +
                    "					,[pe_cdgo] AS me_pe_cdgo --202\n" +
                    "					,[pe_nit] AS me_pe_nit --203\n" +
                    "					,[pe_desc] AS me_pe_desc --204\n" +
                    "					,CASE [pe_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS me_pe_estad --205\n" +
                    "				,[me_num_orden] --206\n" +
                    "				,[me_cntro_cost_auxiliar_cdgo] --207\n" +
                    "					-- Centro Costo Auxiliar\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cdgo] AS me_cca_cdgo --208\n" +
                    "					,me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] AS me_cca_cntro_cost_subcentro_cdgo --209\n" +
                    "						--Centro Costo Subcentro\n" +
                    "						,me_cntro_cost_subcentro.[ccs_cdgo] --210\n" +
                    "						,me_cntro_cost_subcentro.[ccs_desc] --211\n" +
                    "						,CASE me_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ccs_estad] --212\n" +
                    "					,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc --213\n" +
                    "					,CASE me_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cca_estad] --214\n" +
                    "				,[me_labor_realizada_cdgo] --215\n" +
                    "					--Labor Realizada\n" +
                    "					,[lr_cdgo]  --216\n" +
                    "					,[lr_desc] --217\n" +
                    "					,CASE [lr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [lr_estad] --218\n" +
                    "				,[me_cliente_cdgo] --219\n" +
                    "					--Cliente \n" +
                    "					,me_cliente.[cl_cdgo] --220\n" +
                    "					,me_cliente.[cl_desc] --221\n" +
                    "					,CASE me_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --222\n" +
                    "				,[me_articulo_cdgo] --223\n" +
                    "					--articulo\n" +
                    "					,me_articulo.[ar_cdgo] --224\n" +
                    "					,me_articulo.[ar_desc] --225\n" +
                    "					,CASE me_articulo.[ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad] --226\n" +
                    "				,[me_fecha_hora_inicio] --227\n" +
                    "				,[me_fecha_hora_fin] --228\n" +
                    "				,[me_total_minutos] --229\n" +
                    "				,[me_valor_hora] --230\n" +
                    "				,[me_costo_total] --231\n" +
                    "				,[me_recobro_cdgo] --232\n" +
                    "					--Recobro\n" +
                    "					,[rc_cdgo] --233\n" +
                    "					,[rc_desc] --234\n" +
                    "					,CASE [rc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [rc_estad] --235\n" +
                    "				,[me_cliente_recobro_cdgo] --236\n" +
                    "					--Cliente Recobro\n" +
                    "					,[clr_cdgo] --237\n" +
                    "					,[clr_cliente_cdgo] --238\n" +
                    "						--Cliente\n" +
                    "						,me_clr_cliente.[cl_cdgo] --239\n" +
                    "						,me_clr_cliente.[cl_desc] --240\n" +
                    "						,CASE me_clr_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad] --241\n" +
                    "					,[clr_usuario_cdgo] --242\n" +
                    "						--Usuario Quien Registra Recobro\n" +
                    "						,me_clr_usuario.[us_cdgo] --243\n" +
                    "						,me_clr_usuario.[us_clave] --244\n" +
                    "						,me_clr_usuario.[us_nombres] --245\n" +
                    "						,me_clr_usuario.[us_apellidos] --246\n" +
                    "						,me_clr_usuario.[us_perfil_cdgo] --247\n" +
                    "							--Perfil Usuario Registra recobro\n" +
                    "							,me_clr_us_perfil.[prf_cdgo] --248\n" +
                    "							,me_clr_us_perfil.[prf_desc] --249\n" +
                    "							,CASE me_clr_us_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --250\n" +
                    "						,me_clr_usuario.[us_correo] --251\n" +
                    "						,CASE me_clr_usuario.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --252\n" +
                    "					,[clr_valor_recobro] --253\n" +
                    "					,[clr_fecha_registro] --254\n" +
                    "				,[me_costo_total_recobro_cliente] --255\n" +
                    "				,[me_usuario_registro_cdgo] --256\n" +
                    "					--Usuario Quien Registra Equipo\n" +
                    "					,me_us_registro.[us_cdgo] --257\n" +
                    "					,me_us_registro.[us_clave] --258\n" +
                    "					,me_us_registro.[us_nombres] --259\n" +
                    "					,me_us_registro.[us_apellidos] --260\n" +
                    "					,me_us_registro.[us_perfil_cdgo] --261\n" +
                    "						--Perfil de Usuario Quien Registra Equipo\n" +
                    "						,me_us_regist_perfil.[prf_cdgo] --262\n" +
                    "						,me_us_regist_perfil.[prf_desc] --263\n" +
                    "						,CASE me_us_regist_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --264\n" +
                    "					,me_us_registro.[us_correo] --265\n" +
                    "					,CASE me_us_registro.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --266\n" +
                    "				,[me_usuario_autorizacion_cdgo] --267\n" +
                    "					,me_us_autorizacion.[us_cdgo] --268\n" +
                    "					,me_us_autorizacion.[us_clave] --269\n" +
                    "					,me_us_autorizacion.[us_nombres] --270\n" +
                    "					,me_us_autorizacion.[us_apellidos] --271\n" +
                    "					,me_us_autorizacion.[us_perfil_cdgo] --272\n" +
                    "						,me_us_autoriza_perfil.[prf_cdgo] --273\n" +
                    "						,me_us_autoriza_perfil.[prf_desc] --274\n" +
                    "						,CASE me_us_autoriza_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --275\n" +
                    "					,me_us_autorizacion.[us_correo] --276\n" +
                    "					,CASE me_us_autorizacion.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad] --277\n" +
                    "				,[me_autorizacion_recobro_cdgo] --278\n" +
                    "					,[are_cdgo] --279\n" +
                    "					,[are_desc] --280\n" +
                    "					,CASE [are_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [are_estad] --281\n" +
                    "				,[me_observ_autorizacion] --282\n" +
                    "				,[me_inactividad] --283\n" +
                    "				,[me_causa_inactividad_cdgo] --284\n" +
                    "					,[ci_cdgo] --285\n" +
                    "					,[ci_desc] --286\n" +
                    "					,CASE [ci_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ci_estad] --287\n" +
                    "				,[me_usuario_inactividad_cdgo] --288\n" +
                    "					,me_us_inactividad.[us_cdgo] --289\n" +
                    "					,me_us_inactividad.[us_clave] --290\n" +
                    "					,me_us_inactividad.[us_nombres] --291\n" +
                    "					,me_us_inactividad.[us_apellidos] --292\n" +
                    "					,me_us_inactividad.[us_perfil_cdgo] --293\n" +
                    "						,me_us_inactvdad_perfil.[prf_cdgo] --294\n" +
                    "						,me_us_inactvdad_perfil.[prf_desc] --295\n" +
                    "						,CASE me_us_inactvdad_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad] --296\n" +
                    "					,me_us_inactividad.[us_correo] --297\n" +
                    "					,CASE me_us_inactividad.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]	 --298\n" +
                    "                           ,[me_motivo_parada_cdgo]--299\n" +
                    "                                       ,[mpa_cdgo]--300\n" +
                    "                                       ,[mpa_desc]--301\n" +
                    "                                       ,[mpa_estad]--302\n" +
                    "                         ,[me_observ] --303\n" +
                    "				,CASE [me_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [me_estado]	 --304\n" +
                    "				,CASE [me_desde_mvto_carbon] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [me_desde_mvto_carbon]	 --305\n" +
                    "		,CASE [mcle_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [mcle_estado]	 --306\n" +
                    "            ,Tiempo_Vehiculo_Descargue=(SELECT (DATEDIFF (MINUTE,  [mc_fecha_inicio_descargue], [mc_fecha_fin_descargue])))--307\n" +
                    "           ,Tiempo_Equipo_Descargue=(SELECT (DATEDIFF (MINUTE,  [me_fecha_hora_inicio], [me_fecha_hora_fin]))) --308\n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [mc_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] mc_cntro_cost_auxiliar ON [mc_cntro_cost_auxiliar_cdgo]=mc_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] mc_cntro_cost_subcentro ON [cca_cntro_cost_subcentro_cdgo]= mc_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[articulo] mc_articulo ON [mc_articulo_cdgo]=mc_articulo.[ar_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[cliente] mc_cliente ON [mc_cliente_cdgo]=mc_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[transprtdora] ON [mc_transprtdora_cdgo]=[tr_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[usuario] user_registra ON [mc_usuario_cdgo] = user_registra.[us_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[perfil] prf_registra ON user_registra.[us_perfil_cdgo]=prf_registra.[prf_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[estad_mvto_carbon] ON [mc_estad_mvto_carbon_cdgo]=[emc_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[usuario] user_registro_manual ON [mc_usuario_registro_manual_cdgo] = user_registro_manual.[us_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[perfil] prf_registra_manual ON user_registro_manual.[us_perfil_cdgo]=prf_registra_manual.[prf_cdgo]\n" +
                    "	LEFT JOIN ["+DB+"].[dbo].[mvto_carbon_listado_equipo] ON [mcle_mvto_carbon_cdgo]=[mc_cdgo]\n" +
                    "	INNER JOIN (SELECT [ae_cdgo] AS ae_cdgo\n" +
                    "					  ,[ae_cntro_oper_cdgo] AS ae_cntro_oper_cdgo\n" +
                    "							--Centro Operacion\n" +
                    "							,ae_cntro_oper.[co_cdgo] AS ae_co_cdgo\n" +
                    "							,ae_cntro_oper.[co_desc] AS ae_co_desc\n" +
                    "							,ae_cntro_oper.[co_estad] AS ae_co_estado\n" +
                    "					  ,[ae_solicitud_listado_equipo_cdgo] AS ae_solicitud_listado_equipo_cdgo\n" +
                    "							-- Solicitud Listado Equipo\n" +
                    "							,[sle_cdgo] AS ae_sle_cdgo\n" +
                    "							,[sle_solicitud_equipo_cdgo] AS ae_sle_solicitud_equipo_cdgo\n" +
                    "								  --Solicitud Equipo\n" +
                    "								  ,[se_cdgo] AS ae_sle_se_cdgo\n" +
                    "								  ,[se_cntro_oper_cdgo] AS ae_sle_se_cntro_oper_cdgo\n" +
                    "										--CentroOperación SolicitudEquipo\n" +
                    "										,se_cntro_oper.[co_cdgo] AS ae_sle_se_co_cdgo\n" +
                    "										,se_cntro_oper.[co_desc] AS ae_sle_se_co_desc\n" +
                    "										,se_cntro_oper.[co_estad] AS ae_sle_se_co_estad\n" +
                    "								  ,[se_fecha] AS ae_sle_se_fecha\n" +
                    "								  ,[se_usuario_realiza_cdgo] AS ae_sle_se_usuario_realiza_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_realiza.[us_cdgo] AS ae_sle_se_us_registra_cdgo\n" +
                    "										,se_usuario_realiza.[us_clave] AS ae_sle_se_us_registra_clave\n" +
                    "										,se_usuario_realiza.[us_nombres] AS ae_sle_se_us_registra_nombres\n" +
                    "										,se_usuario_realiza.[us_apellidos] AS ae_sle_se_us_registra_apellidos\n" +
                    "										,se_usuario_realiza.[us_perfil_cdgo] AS ae_sle_se_us_registra_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra.[prf_cdgo] AS ae_sle_se_prf_us_registra_cdgo\n" +
                    "											,ae_prf_registra.[prf_desc] AS ae_sle_se_prf_us_registra_desc\n" +
                    "											,ae_prf_registra.[prf_estad] AS ae_sle_se_prf_us_registra_estad\n" +
                    "										,se_usuario_realiza.[us_correo] AS ae_sle_se_us_registra_correo\n" +
                    "										,se_usuario_realiza.[us_estad] AS ae_sle_se_us_registra_estad\n" +
                    "								  ,[se_fecha_registro] AS ae_sle_se_fecha_registro\n" +
                    "								  ,[se_estado_solicitud_equipo_cdgo] AS ae_sle_se_estado_solicitud_equipo_cdgo\n" +
                    "										--Estado de la solicitud\n" +
                    "										,[ese_cdgo] AS ae_sle_se_ese_cdgo\n" +
                    "										,[ese_desc] AS ae_sle_se_ese_desc\n" +
                    "										,[ese_estad] AS ae_sle_se_ese_estad\n" +
                    "								  ,[se_fecha_confirmacion] AS ae_sle_se_fecha_confirmacion\n" +
                    "								  ,[se_usuario_confirma_cdgo] AS ae_se_usuario_confirma_cdgo\n" +
                    "										--Usuario SolicitudEquipo\n" +
                    "										,se_usuario_confirma.[us_cdgo] AS ae_sle_se_us_confirma_cdgo\n" +
                    "										,se_usuario_confirma.[us_clave] AS ae_sle_se_us_confirma_clave\n" +
                    "										,se_usuario_confirma.[us_nombres] AS ae_sle_se_us_confirma_nombres\n" +
                    "										,se_usuario_confirma.[us_apellidos] AS ae_sle_se_us_confirma_apellidos\n" +
                    "										,se_usuario_confirma.[us_perfil_cdgo] AS ae_sle_se_us_confirma_perfil_cdgo\n" +
                    "											--Perfil Usuario Quien Registra Manual\n" +
                    "											,ae_prf_registra_confirma.[prf_cdgo] AS ae_sle_se_prf_us_confirma_cdgo\n" +
                    "											,ae_prf_registra_confirma.[prf_desc] AS ae_sle_se_prf_us_confirma_desc\n" +
                    "											,ae_prf_registra_confirma.[prf_estad] AS ae_sle_se_prf_us_confirma_estad\n" +
                    "										,se_usuario_confirma.[us_correo] AS ae_sle_se_us_confirma_correo\n" +
                    "										,se_usuario_confirma.[us_estad] AS ae_sle_se_us_confirma_estado\n" +
                    "								  ,[se_confirmacion_solicitud_equipo_cdgo] AS ae_sle_se_confirmacion_solicitud_equipo_cdgo\n" +
                    "										--Confirmacion solicitudEquipo\n" +
                    "										,[cse_cdgo] AS ae_sle_se_cse_cdgo\n" +
                    "										,[cse_desc] AS ae_sle_se_ces_desc\n" +
                    "										,[cse_estad] AS ae_sle_se_ces_estado\n" +
                    "							,[sle_tipo_equipo_cdgo] AS ae_sle_tipo_equipo_cdgo\n" +
                    "								--Tipo de Equipo\n" +
                    "								,sle_tipoEquipo.[te_cdgo] AS ae_sle_te_cdgo\n" +
                    "								,sle_tipoEquipo.[te_desc] AS ae_sle_te_desc\n" +
                    "								,sle_tipoEquipo.[te_estad] AS ae_sle_te_estad\n" +
                    "							,[sle_marca_equipo] AS ae_sle_marca_equipo\n" +
                    "							,[sle_modelo_equipo] AS ae_sle_modelo_equipo\n" +
                    "							,[sle_cant_equip] AS ae_sle_cant_equip\n" +
                    "							,[sle_observ] AS ae_sle_observ\n" +
                    "							,[sle_fecha_hora_inicio] AS ae_sle_fecha_hora_inicio\n" +
                    "							,[sle_fecha_hora_fin] AS ae_sle_fecha_hora_fin\n" +
                    "							,[sle_cant_minutos] AS ae_sle_cant_minutos\n" +
                    "							,[sle_labor_realizada_cdgo] AS ae_sle_labor_realizada_cdgo\n" +
                    "							-- Labor Realizada\n" +
                    "								  ,[lr_cdgo] AS ae_sle_lr_cdgo\n" +
                    "								  ,[lr_desc] AS ae_sle_lr_desc\n" +
                    "								  ,[lr_estad] AS ae_sle_lr_estad\n" +
                    "							,[sle_motonave_cdgo] AS ae_sle_sle_motonave_cdgo\n" +
                    "							--Motonave\n" +
                    "								  ,[mn_cdgo] AS ae_sle_mn_cdgo\n" +
                    "								  ,[mn_desc] AS ae_sle_mn_desc\n" +
                    "								  ,[mn_estad] AS ae_sle_mn_estad\n" +
                    "							,[sle_cntro_cost_auxiliar_cdgo] AS ae_sle_cntro_cost_auxiliar_cdgo\n" +
                    "								--Centro Costo Auxiliar\n" +
                    "								,[cca_cdgo] AS ae_sle_cca_cdgo\n" +
                    "								,[cca_cntro_cost_subcentro_cdgo] AS ae_sle_cca_cntro_cost_subcentro_cdgo\n" +
                    "									-- SubCentro de Costo\n" +
                    "									,[ccs_cdgo] AS ae_sle_ccs_cdgo\n" +
                    "									,[ccs_desc] AS ae_sle_ccs_desc\n" +
                    "									,[ccs_estad] AS ae_sle_ccs_estad\n" +
                    "								,[cca_desc] AS ae_sle_cca_desc\n" +
                    "								,[cca_estad] AS ae_sle_cca_estad\n" +
                    "							,[sle_compania_cdgo] AS ae_sle_compania_cdgo\n" +
                    "								--Compañia\n" +
                    "								,[cp_cdgo] AS ae_sle_cp_cdgo\n" +
                    "								,[cp_desc] AS ae_sle_cp_desc\n" +
                    "								,[cp_estad] AS ae_sle_cp_estad\n" +
                    "					  ,[ae_fecha_registro] AS ae_fecha_registro\n" +
                    "					  ,[ae_fecha_hora_inicio] AS ae_fecha_hora_inicio\n" +
                    "					  ,[ae_fecha_hora_fin] AS ae_fecha_hora_fin\n" +
                    "					  ,[ae_cant_minutos] AS ae_cant_minutos\n" +
                    "					  ,[ae_equipo_cdgo] AS ae_equipo_cdgo\n" +
                    "							--Equipo\n" +
                    "							,[eq_cdgo] AS ae_eq_cdgo\n" +
                    "							,[eq_tipo_equipo_cdgo] AS ae_eq_tipo_equipo_cdgo\n" +
                    "								--Tipo Equipo\n" +
                    "								,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "								,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc\n" +
                    "								,eq_tipo_equipo.[te_estad] AS ae_eq_te_estad\n" +
                    "							,[eq_codigo_barra] AS ae_eq_codigo_barra\n" +
                    "							,[eq_referencia] AS ae_eq_referencia\n" +
                    "							,[eq_producto] AS ae_eq_producto\n" +
                    "							,[eq_capacidad] AS ae_eq_capacidad\n" +
                    "							,[eq_marca] AS ae_eq_marca\n" +
                    "							,[eq_modelo] AS ae_eq_modelo\n" +
                    "							,[eq_serial] AS ae_eq_serial\n" +
                    "							,[eq_desc] AS ae_eq_desc\n" +
                    "							,[eq_clasificador1_cdgo] AS ae_eq_clasificador1_cdgo\n" +
                    "								-- Clasificador 1\n" +
                    "								,eq_clasificador1.[ce_cdgo] AS ae_eq_ce1_cdgo\n" +
                    "								,eq_clasificador1.[ce_desc] AS ae_eq_ce1_desc\n" +
                    "								,eq_clasificador1.[ce_estad] AS ae_eq_ce1_estad\n" +
                    "							,[eq_clasificador2_cdgo] AS ae_eq_clasificador2_cdgo\n" +
                    "								-- Clasificador 2\n" +
                    "								,eq_clasificador2.[ce_cdgo] AS ae_eq_ce2_cdgo\n" +
                    "								,eq_clasificador2.[ce_desc] AS ae_eq_ce2_desc\n" +
                    "								,eq_clasificador2.[ce_estad] AS ae_eq_ce2_estad\n" +
                    "							,[eq_proveedor_equipo_cdgo] AS ae_eq_proveedor_equipo_cdgo\n" +
                    "								--Proveedor Equipo\n" +
                    "								,[pe_cdgo] AS ae_eq_pe_cdgo\n" +
                    "								,[pe_nit] AS ae_eq_pe_nit\n" +
                    "								,[pe_desc] AS ae_eq_pe_desc\n" +
                    "								,[pe_estad] AS ae_eq_pe_estad\n" +
                    "							,[eq_equipo_pertenencia_cdgo] AS ae_eq_equipo_pertenencia_cdgo\n" +
                    "								-- Equipo Pertenencia\n" +
                    "								,eq_pertenencia.[ep_cdgo] AS ae_eq_ep_cdgo\n" +
                    "								,eq_pertenencia.[ep_desc] AS ae_eq_ep_desc\n" +
                    "								,eq_pertenencia.[ep_estad] AS ae_eq_ep_estad\n" +
                    "							,[eq_observ] AS ae_eq_eq_observ\n" +
                    "							,[eq_estad] AS ae_eq_eq_estad\n" +
                    "							,[eq_actvo_fijo_id] AS ae_eq_actvo_fijo_id\n" +
                    "							,[eq_actvo_fijo_referencia] AS ae_eq_actvo_fijo_referencia\n" +
                    "							,[eq_actvo_fijo_desc] AS ae_eq_actvo_fijo_desc\n" +
                    "					  ,[ae_equipo_pertenencia_cdgo] AS ae_equipo_pertenencia_cdgo\n" +
                    "						-- Equipo Pertenencia\n" +
                    "								,ae_pertenencia.[ep_cdgo] AS ae_ep_cdgo\n" +
                    "								,ae_pertenencia.[ep_desc] AS ae_ep_desc\n" +
                    "								,ae_pertenencia.[ep_estad]	 AS ae_ep_estad\n" +
                    "					  ,[ae_cant_minutos_operativo] AS ae_cant_minutos_operativo\n" +
                    "					  ,[ae_cant_minutos_parada] AS ae_cant_minutos_parada\n" +
                    "					  ,[ae_cant_minutos_total] AS ae_cant_minutos_total\n" +
                    "					  ,[ae_estad] AS ae_estad\n" +
                    "					  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra ON se_usuario_realiza.[us_perfil_cdgo]=ae_prf_registra.[prf_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra_confirma ON se_usuario_confirma.[us_perfil_cdgo]=ae_prf_registra_confirma.[prf_cdgo]\n" +
                    "							LEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "							LEFT  JOIN["+DB+"].[dbo].[motonave] ON [sle_motonave_cdgo]=[mn_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "							INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "							LEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]	\n" +
                    "	) asignacion_equipo ON [mcle_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                    "	--Movimiento Equipo\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[mvto_equipo] ON [mcle_mvto_equipo_cdgo]=[me_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "	INNER JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[articulo] me_articulo ON [me_articulo_cdgo]=me_articulo.[ar_cdgo]\n" +
                    "	INNER JOIN ["+DB+"].[dbo].[recobro] ON [me_recobro_cdgo]=[rc_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente_recobro] ON [me_cliente_recobro_cdgo]=[clr_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[cliente] me_clr_cliente ON [clr_cliente_cdgo]=me_clr_cliente.[cl_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_clr_usuario ON [clr_usuario_cdgo]=me_clr_usuario.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_clr_us_perfil ON me_clr_usuario.[us_perfil_cdgo]=me_clr_us_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_regist_perfil ON me_us_registro.[us_perfil_cdgo]=me_us_regist_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_autorizacion ON [me_usuario_autorizacion_cdgo]=me_us_autorizacion.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_autoriza_perfil ON me_us_autorizacion.[us_perfil_cdgo]=me_us_autoriza_perfil.[prf_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[autorizacion_recobro] ON [me_autorizacion_recobro_cdgo]=[are_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[causa_inactividad] ON [me_causa_inactividad_cdgo]=[ci_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_inactividad ON [me_usuario_inactividad_cdgo]=me_us_inactividad.[us_cdgo]\n" +
                    "	LEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_inactvdad_perfil ON me_us_inactividad.[us_perfil_cdgo]=me_us_inactvdad_perfil.[prf_cdgo]\n" +
                    "   LEFT JOIN  ["+DB+"].[dbo].[motivo_parada] ON [me_motivo_parada_cdgo]= [mpa_cdgo]\n"  +
                    "	WHERE ae_equipo_cdgo =?  AND  [me_inactividad]=0 AND  [mc_estad_mvto_carbon_cdgo]=1 AND [me_fecha_hora_fin] IS NULL ORDER BY [me_cdgo] DESC");
            query.setString(1, equipoI.getCodigo());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                MvtoCarbon_ListadoEquipos mvto_listEquipo = new MvtoCarbon_ListadoEquipos();
                mvto_listEquipo.setCodigo(resultSet.getString(1));
                MvtoCarbon mvtoCarbon = new MvtoCarbon();
                mvtoCarbon.setCodigo(resultSet.getString(3));
                mvtoCarbon.setCentroOperacion(new CentroOperacion(Integer.parseInt(resultSet.getString(5)),resultSet.getString(6),resultSet.getString(7)));
                mvtoCarbon.setCentroCostoAuxiliar(new CentroCostoAuxiliar(resultSet.getString(9),new CentroCostoSubCentro(Integer.parseInt(resultSet.getString(11)),resultSet.getString(12),resultSet.getString(13)),resultSet.getString(14),resultSet.getString(15)));
                mvtoCarbon.setArticulo(new Articulo(resultSet.getString(17),resultSet.getString(18),resultSet.getString(19)));
                mvtoCarbon.setCliente(new Cliente(resultSet.getString(21),resultSet.getString(22),resultSet.getString(23)));
                mvtoCarbon.setTransportadora(new Transportadora(resultSet.getString(25),resultSet.getString(26),resultSet.getString(27),resultSet.getString(28),resultSet.getString(29)));
                mvtoCarbon.setFechaRegistro(resultSet.getString(30));
                mvtoCarbon.setNumero_orden(resultSet.getString(31));
                mvtoCarbon.setDeposito(resultSet.getString(32));
                mvtoCarbon.setConsecutivo(resultSet.getString(33));
                mvtoCarbon.setPlaca(resultSet.getString(34));
                mvtoCarbon.setPesoVacio(resultSet.getString(35));
                mvtoCarbon.setPesoLleno(resultSet.getString(36));
                mvtoCarbon.setPesoNeto(resultSet.getString(37));
                mvtoCarbon.setFechaEntradaVehiculo(resultSet.getString(38));
                mvtoCarbon.setFecha_SalidaVehiculo(resultSet.getString(39));
                mvtoCarbon.setFechaInicioDescargue(resultSet.getString(40));
                mvtoCarbon.setFechaFinDescargue(resultSet.getString(41));
                Usuario us = new Usuario();
                us.setCodigo(resultSet.getString(43));
                //us.setClave(resultSet.getString(44));
                us.setNombres(resultSet.getString(45));
                us.setApellidos(resultSet.getString(46));
                us.setPerfilUsuario(new Perfil(resultSet.getString(48),resultSet.getString(49),resultSet.getString(50)));
                us.setCorreo(resultSet.getString(51));
                us.setEstado(resultSet.getString(52));
                mvtoCarbon.setUsuarioRegistroMovil(us);
                mvtoCarbon.setObservacion(resultSet.getString(53));
                EstadoMvtoCarbon estadMvtoCarbon = new EstadoMvtoCarbon();
                estadMvtoCarbon.setCodigo(resultSet.getString(55));
                estadMvtoCarbon.setDescripcion(resultSet.getString(56));
                estadMvtoCarbon.setEstado(resultSet.getString(57));
                mvtoCarbon.setEstadoMvtoCarbon(estadMvtoCarbon);
                mvtoCarbon.setConexionPesoCcarga(resultSet.getString(58));
                mvtoCarbon.setRegistroManual(resultSet.getString(59));
                Usuario usRegManual = new Usuario();
                usRegManual.setCodigo(resultSet.getString(61));
                //usRegManual.setClave(resultSet.getString(62));
                usRegManual.setNombres(resultSet.getString(63));
                usRegManual.setApellidos(resultSet.getString(64));
                usRegManual.setPerfilUsuario(new Perfil(resultSet.getString(66),resultSet.getString(67),resultSet.getString(68)));
                usRegManual.setCorreo(resultSet.getString(69));
                usRegManual.setEstado(resultSet.getString(70));
                mvtoCarbon.setUsuarioRegistraManual(usRegManual);
                mvtoCarbon.setCantidadHorasDescargue(resultSet.getString(303));
                mvto_listEquipo.setMvtoCarbon(mvtoCarbon);
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(72));
                CentroOperacion co= new CentroOperacion();
                co.setCodigo(Integer.parseInt(resultSet.getString(74)));
                co.setDescripcion(resultSet.getString(75));
                co.setEstado(resultSet.getString(76));
                asignacionEquipo.setCentroOperacion(co);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(78));
                SolicitudEquipo solicitudEquipo= new SolicitudEquipo();
                solicitudEquipo.setCodigo(resultSet.getString(80));
                CentroOperacion co_se= new CentroOperacion();
                co_se.setCodigo(Integer.parseInt(resultSet.getString(82)));
                co_se.setDescripcion(resultSet.getString(83));
                co_se.setEstado(resultSet.getString(84));
                solicitudEquipo.setCentroOperacion(co_se);
                solicitudEquipo.setFechaSolicitud(resultSet.getString(85));
                Usuario us_se = new Usuario();
                us_se.setCodigo(resultSet.getString(87));
                //us_se.setClave(resultSet.getString(88));
                us_se.setNombres(resultSet.getString(89));
                us_se.setApellidos(resultSet.getString(90));
                us_se.setPerfilUsuario(new Perfil(resultSet.getString(92),resultSet.getString(93),resultSet.getString(94)));
                us_se.setCorreo(resultSet.getString(95));
                us_se.setEstado(resultSet.getString(96));
                solicitudEquipo.setUsuarioRealizaSolicitud(us_se);
                solicitudEquipo.setFechaRegistro(resultSet.getString(97));
                EstadoSolicitudEquipos estadoSolicitudEquipos = new EstadoSolicitudEquipos();
                estadoSolicitudEquipos.setCodigo(resultSet.getString(99));
                estadoSolicitudEquipos.setDescripcion(resultSet.getString(100));
                estadoSolicitudEquipos.setEstado(resultSet.getString(101));
                solicitudEquipo.setEstadoSolicitudEquipo(estadoSolicitudEquipos);
                solicitudEquipo.setFechaConfirmacion(resultSet.getString(102));
                Usuario us_se_confirm = new Usuario();
                us_se_confirm.setCodigo(resultSet.getString(104));
                //us_se_confirm.setClave(resultSet.getString(105));
                us_se_confirm.setNombres(resultSet.getString(106));
                us_se_confirm.setApellidos(resultSet.getString(107));
                us_se_confirm.setPerfilUsuario(new Perfil(resultSet.getString(109),resultSet.getString(110),resultSet.getString(111)));
                us_se_confirm.setCorreo(resultSet.getString(112));
                us_se_confirm.setEstado(resultSet.getString(113));
                solicitudEquipo.setUsuarioConfirmacionSolicitud(us_se_confirm);
                ConfirmacionSolicitudEquipos confirmacionSolicitudEquipos = new ConfirmacionSolicitudEquipos();
                confirmacionSolicitudEquipos.setCodigo(resultSet.getString(115));
                confirmacionSolicitudEquipos.setDescripcion(resultSet.getString(116));
                confirmacionSolicitudEquipos.setEstado(resultSet.getString(117));
                solicitudEquipo.setConfirmacionSolicitudEquipo(confirmacionSolicitudEquipos);
                solicitudListadoEquipo.setSolicitudEquipo(solicitudEquipo);
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(119));
                tipoEquipo.setDescripcion(resultSet.getString(120));
                tipoEquipo.setEstado(resultSet.getString(121));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(122));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(123));
                solicitudListadoEquipo.setCantidad(Integer.parseInt(resultSet.getString(124)));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(125));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(126));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(127));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(128));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(130));
                laborRealizada.setDescripcion(resultSet.getString(131));
                laborRealizada.setEstado(resultSet.getString(132));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave = new Motonave();
                motonave.setCodigo(resultSet.getString(134));
                motonave.setDescripcion(resultSet.getString(135));
                motonave.setEstado(resultSet.getString(136));
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(140));
                centroCostoSubCentro.setDescripcion(resultSet.getString(141));
                centroCostoSubCentro.setEstado(resultSet.getString(142));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(138));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(143));
                centroCostoAuxiliar.setEstado(resultSet.getString(144));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compania = new Compañia();
                compania.setCodigo(resultSet.getString(146));
                compania.setDescripcion(resultSet.getString(147));
                compania.setEstado(resultSet.getString(148));
                solicitudListadoEquipo.setCompañia(compania);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(149));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(150));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(151));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(152));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(154));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(156),resultSet.getString(157),resultSet.getString(158)));
                equipo.setCodigo_barra(resultSet.getString(159));
                equipo.setReferencia(resultSet.getString(160));
                equipo.setProducto(resultSet.getString(161));
                equipo.setCapacidad(resultSet.getString(162));
                equipo.setMarca(resultSet.getString(163));
                equipo.setModelo(resultSet.getString(164));
                equipo.setSerial(resultSet.getString(165));
                equipo.setDescripcion(resultSet.getString(166));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(168),resultSet.getString(169),resultSet.getString(170)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(172),resultSet.getString(173),resultSet.getString(174)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(176),resultSet.getString(177),resultSet.getString(178),resultSet.getString(179)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(181),resultSet.getString(182),resultSet.getString(183)));
                equipo.setObservacion(resultSet.getString(184));
                equipo.setEstado(resultSet.getString(185));
                equipo.setActivoFijo_codigo(resultSet.getString(186));
                equipo.setActivoFijo_referencia(resultSet.getString(187));
                equipo.setActivoFijo_descripcion(resultSet.getString(188));
                asignacionEquipo.setEquipo(equipo);
                asignacionEquipo.setPertenencia(new Pertenencia(resultSet.getString(190),resultSet.getString(191),resultSet.getString(192)));
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(193));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(194));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(195));
                asignacionEquipo.setEstado(resultSet.getString(196));
                mvto_listEquipo.setAsignacionEquipo(asignacionEquipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(198));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaRegistro(resultSet.getString(200));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(202),resultSet.getString(203),resultSet.getString(204),resultSet.getString(205)));
                mvtoEquipo.setNumeroOrden(resultSet.getString(206));
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setCodigo(resultSet.getInt(210));
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(211));
                centroCostoSubCentro_mvtoEquipo.setEstado(resultSet.getString(212));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setCodigo(resultSet.getString(208));
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(213));
                centroCostoAuxiliar_mvtoEquipo.setEstado(resultSet.getString(214));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setCodigo(resultSet.getString(216));
                laborRealizadaT.setDescripcion(resultSet.getString(217));
                laborRealizadaT.setEstado(resultSet.getString(218));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente(resultSet.getString(220),resultSet.getString(221),resultSet.getString(222)));
                mvtoEquipo.setArticulo(new Articulo(resultSet.getString(224),resultSet.getString(225),resultSet.getString(226)));
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(227));
                mvtoEquipo.setFechaHoraFin(resultSet.getString(228));
                mvtoEquipo.setTotalMinutos(resultSet.getString(229));
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(230));
                Recobro recobro = new Recobro();
                recobro.setCodigo(resultSet.getString(233));
                recobro.setDescripcion(resultSet.getString(234));
                recobro.setEstado(resultSet.getString(235));
                mvtoEquipo.setRecobro(recobro);
                ClienteRecobro ClienteRecobro = new ClienteRecobro();
                ClienteRecobro.setCodigo(resultSet.getString(235));
                Cliente cliente_recobro = new Cliente();
                cliente_recobro.setCodigo(resultSet.getString(239));
                cliente_recobro.setDescripcion(resultSet.getString(240));
                cliente_recobro.setEstado(resultSet.getString(241));
                ClienteRecobro.setCliente(cliente_recobro);
                Usuario usuario_recobre = new Usuario();
                usuario_recobre.setCodigo(resultSet.getString(243));
                //usuario_recobre.setClave(resultSet.getString(244));
                usuario_recobre.setNombres(resultSet.getString(245));
                usuario_recobre.setApellidos(resultSet.getString(246));
                usuario_recobre.setPerfilUsuario(new Perfil(resultSet.getString(248),resultSet.getString(249),resultSet.getString(250)));
                usuario_recobre.setCorreo(resultSet.getString(251));
                usuario_recobre.setEstado(resultSet.getString(252));
                ClienteRecobro.setUsuario(usuario_recobre);
                ClienteRecobro.setValorRecobro(resultSet.getString(253));
                ClienteRecobro.setFechaRegistro(resultSet.getString(254));
                mvtoEquipo.setClienteRecobro(ClienteRecobro);
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(255));
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setCodigo(resultSet.getString(257));
                //usuario_me_registra.setClave(resultSet.getString(258));
                usuario_me_registra.setNombres(resultSet.getString(259));
                usuario_me_registra.setApellidos(resultSet.getString(260));
                usuario_me_registra.setPerfilUsuario(new Perfil(resultSet.getString(262),resultSet.getString(263),resultSet.getString(264)));
                usuario_me_registra.setCorreo(resultSet.getString(265));
                usuario_me_registra.setEstado(resultSet.getString(266));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                Usuario usuario_me_autoriza = new Usuario();
                usuario_me_autoriza.setCodigo(resultSet.getString(268));
                //usuario_me_autoriza.setClave(resultSet.getString(269));
                usuario_me_autoriza.setNombres(resultSet.getString(270));
                usuario_me_autoriza.setApellidos(resultSet.getString(271));
                usuario_me_autoriza.setPerfilUsuario(new Perfil(resultSet.getString(273),resultSet.getString(274),resultSet.getString(275)));
                usuario_me_autoriza.setCorreo(resultSet.getString(276));
                usuario_me_autoriza.setEstado(resultSet.getString(277));
                mvtoEquipo.setUsuarioAutorizaRecobro(usuario_me_autoriza);
                AutorizacionRecobro autorizacionRecobro = new AutorizacionRecobro();
                autorizacionRecobro.setCodigo(resultSet.getString(279));
                autorizacionRecobro.setDescripcion(resultSet.getString(280));
                autorizacionRecobro.setEstado(resultSet.getString(281));
                mvtoEquipo.setAutorizacionRecobro(autorizacionRecobro);
                mvtoEquipo.setObservacionAutorizacion(resultSet.getString(282));
                mvtoEquipo.setInactividad(resultSet.getString(283));
                CausaInactividad causaInactividad = new CausaInactividad();
                causaInactividad.setCodigo(resultSet.getString(285));
                causaInactividad.setDescripcion(resultSet.getString(286));
                causaInactividad.setEstado(resultSet.getString(287));
                mvtoEquipo.setCausaInactividad(causaInactividad);
                Usuario usuario_me_us_inactividad = new Usuario();
                usuario_me_us_inactividad.setCodigo(resultSet.getString(289));
                //usuario_me_us_inactividad.setClave(resultSet.getString(290));
                usuario_me_us_inactividad.setNombres(resultSet.getString(291));
                usuario_me_us_inactividad.setApellidos(resultSet.getString(292));
                usuario_me_us_inactividad.setPerfilUsuario(new Perfil(resultSet.getString(294),resultSet.getString(295),resultSet.getString(296)));
                usuario_me_us_inactividad.setCorreo(resultSet.getString(297));
                usuario_me_us_inactividad.setEstado(resultSet.getString(298));
                mvtoEquipo.setUsuarioInactividad(usuario_me_us_inactividad);
                MotivoParada motivoParada= new MotivoParada();
                motivoParada.setCodigo(resultSet.getString(300));
                motivoParada.setDescripcion(resultSet.getString(301));
                motivoParada.setEstado(resultSet.getString(302));
                mvtoEquipo.setMotivoParada(motivoParada);
                mvtoEquipo.setObservacionMvtoEquipo(resultSet.getString(303));
                mvtoEquipo.setEstado(resultSet.getString(304));
                mvtoEquipo.setDesdeCarbon(resultSet.getString(305));
                mvtoEquipo.setTotalMinutos(resultSet.getString(307));
                mvto_listEquipo.setMvtoEquipo(mvtoEquipo);
                mvto_listEquipo.setEstado(resultSet.getString(306));
                listadoObjetos.add(mvto_listEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
/*    public ArrayList<MvtoCarbon_ListadoEquipos> MvtoCarbon_ListadoEquipo2(String codigoMvtoCarbon) throws SQLException{
        ArrayList<MvtoCarbon_ListadoEquipos> listadoObjetos = null;//new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT CONCAT([eq_desc],' ',[eq_marca],' ',[eq_modelo])\n" +
                    "  FROM ["+DB+"].[dbo].[mvto_carbon_listado_equipo]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[asignacion_equipo] ON [ae_cdgo]=[mcle_asignacion_equipo_cdgo]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[equipo] ON [eq_cdgo]=[ae_equipo_cdgo] \n" +
                    "  WHERE [mcle_mvto_carbon_cdgo]=? AND [mcle_estado]=1 ORDER BY [mcle_cdgo] DESC;");

            queryBuscar.setString(1, codigoMvtoCarbon);
            ResultSet resultSetBuscar=queryBuscar.executeQuery();
            boolean validar = true;
            while(resultSetBuscar.next()){
                if(validar){
                    listadoObjetos = new ArrayList();
                    validar=false;
                }
                listadoObjetos.add(resultSetBuscar.getString(1));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }*/
}
