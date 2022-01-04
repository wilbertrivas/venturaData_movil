package com.example.vg_appcostos.ModuloEquipo.Controller;

import android.app.UiAutomation;

import com.example.vg_appcostos.ConexionesDB.Ccarga_GP;
import com.example.vg_appcostos.ConexionesDB.Ccarga_OPP;
import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Articulo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.BaseDatos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Cliente;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Motonave;
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
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Config;
import com.example.vg_appcostos.Sistemas.Modelo.Perfil;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ControlDB_Equipo {
    private Connection conexion = null;
    private String tipoConexion;
    public ControlDB_Equipo(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }

    //Optimizado
    public Equipo consultarEquipo(String codigo){
        Equipo Objeto = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT [eq_cdgo] --1\n" +
                    "           ,[te_desc] --2\n" +
                    "      ,[eq_marca] --3\n" +
                    "      ,[eq_modelo] --4\n" +
                    "      ,[eq_desc]  --5\n" +
                    "  FROM ["+DB+"].[dbo].[equipo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] ON te_cdgo = eq_tipo_equipo_cdgo\n"
                    + "  WHERE [eq_cdgo] =?;");
            queryBuscar.setString(1, codigo);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                Objeto = new Equipo();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                Objeto.setTipoEquipo(new TipoEquipo("",resultSetBuscar.getString(2),""));
                Objeto.setMarca(resultSetBuscar.getString(3));
                Objeto.setModelo(resultSetBuscar.getString(4));
                Objeto.setDescripcion(resultSetBuscar.getString(5));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<MvtoEquipo> buscar_MvtoEquipoActivos(Equipo equipoI,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<MvtoEquipo> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT   TOP 30    \n" +
                    "        [me_cdgo]   -- 1 \n" +
                    "        ,ae_eq_cdgo --2\n" +
                    "        ,ae_eq_te_desc --3 \n" +
                    "        ,ae_eq_marca --4\n" +
                    "        ,ae_eq_modelo --5 \n" +
                    "        ,ae_eq_desc --6\n" +
                    "        ,[pe_desc] AS me_pe_desc   --7\n" +
                    "        ,me_cntro_cost_subcentro.[ccs_desc]   --8\n" +
                    "        ,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc   --9\n" +
                    "        ,[lr_desc] --10\n" +
                    "        ,me_cliente.[cl_desc]   --11\n" +
                    "        ,[ar_desc]   --12\n" +
                    "        ,[me_fecha_hora_inicio]   --13 \n" +
                    "        ,me_us_registro.[us_nombres]   --14 \n" +
                    "        ,me_us_registro.[us_apellidos]   --15 \n" +
                    "            ,[co_desc] --16\n" +
                    "            ,me_motonave.[mn_desc] --17\n" +
                    "            ,[me_cntro_cost_auxiliarDestino_cdgo] --18\n" +
                    "            ,me_ccaDestino.[cca_desc] --19\n" +
                    " FROM ["+DB+"].[dbo].[mvto_equipo] \n" +
                    "\t\tINNER JOIN (\n" +
                    "\t\t\t\tSELECT [ae_cdgo] AS ae_cdgo \n" +
                    "\t\t\t\t\t\t,[ae_equipo_cdgo] AS ae_equipo_cdgo \n" +
                    "\t\t\t\t\t\t--Equipo \n" +
                    "\t\t\t\t\t\t,[eq_cdgo] AS ae_eq_cdgo \n" +
                    "\t\t\t\t\t\t,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc \n" +
                    "\t\t\t\t\t\t,[eq_marca] AS ae_eq_marca \n" +
                    "\t\t\t\t\t\t,[eq_modelo] AS ae_eq_modelo \n" +
                    "\t\t\t\t\t\t,[eq_desc] AS ae_eq_desc \n" +
                    "                    FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "                    )\n" +
                    "\t\t\t\tasignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo] \n" +
                    "                    LEFT JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo] \n" +
                    "                    LEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_ccaDestino ON [me_cntro_cost_auxiliarDestino_cdgo]=me_ccaDestino.[cca_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]  \n" +
                    "                    LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo] AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "\t\t\tWHERE  [me_inactividad]=0 AND \n" +
                    "\t\t\t\t\t[me_desde_mvto_carbon]=0 AND \n" +
                    "\t\t\t\t\t[me_fecha_hora_fin] IS NULL AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" \n" +
                    "\t\t\t\t\t\t AND ae_equipo_cdgo =?\n" +
                    " ORDER BY [me_cdgo] DESC\n");
            query.setString(1, equipoI.getCodigo());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(2));
                equipo.setTipoEquipo(new TipoEquipo("",resultSet.getString(3),""));
                equipo.setMarca(resultSet.getString(4));
                equipo.setModelo(resultSet.getString(5));
                equipo.setDescripcion(resultSet.getString(6));
                asignacionEquipo.setEquipo(equipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(1));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(13));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo("","",resultSet.getString(7),""));
                CentroOperacion me_co= new CentroOperacion();
                me_co.setDescripcion(resultSet.getString(16));
                mvtoEquipo.setCentroOperacion(me_co);
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(8));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(9));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setDescripcion(resultSet.getString(10));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente("",resultSet.getString(11),""));
                mvtoEquipo.setArticulo(new Articulo("",resultSet.getString(12),""));
                Motonave me_motonave = new Motonave();
                me_motonave.setDescripcion(resultSet.getString(17));
                mvtoEquipo.setMotonave(me_motonave);
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setNombres(resultSet.getString(14));
                usuario_me_registra.setApellidos(resultSet.getString(15));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);

                CentroCostoAuxiliar ccaDestino = new CentroCostoAuxiliar();
                ccaDestino.setCodigo(resultSet.getString(18));
                ccaDestino.setDescripcion(resultSet.getString(19));
                mvtoEquipo.setCentroCostoAuxiliarDestino(ccaDestino);
                listadoObjetos.add(mvtoEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<MvtoEquipo> buscar_MvtoEquipoActivos_StandBy(Equipo equipoI) throws SQLException{
        ArrayList<MvtoEquipo> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT    \n" +
                    "        [me_cdgo]   -- 1 \n" +
                    "        ,ae_eq_cdgo --2\n" +
                    "        ,ae_eq_te_desc --3 \n" +
                    "        ,ae_eq_marca --4\n" +
                    "        ,ae_eq_modelo --5 \n" +
                    "        ,ae_eq_desc --6\n" +
                    "        ,[pe_desc] AS me_pe_desc   --7\n" +
                    "        ,me_cntro_cost_subcentro.[ccs_desc]   --8\n" +
                    "        ,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc   --9\n" +
                    "        ,[lr_desc] --10\n" +
                    "        ,me_cliente.[cl_desc]   --11\n" +
                    "        ,[ar_desc]   --12\n" +
                    "        ,[me_fecha_hora_inicio]   --13 \n" +
                    "        ,me_us_registro.[us_nombres]   --14 \n" +
                    "        ,me_us_registro.[us_apellidos]   --15 \n" +
                    "            ,[co_desc] --16\n" +
                    "            ,me_motonave.[mn_desc] --17\n" +
                    "FROM ["+DB+"].[dbo].[mvto_equipo] \n" +
                    "\t\tINNER JOIN (\n" +
                    "\t\t\t\tSELECT [ae_cdgo] AS ae_cdgo \n" +
                    "\t\t\t\t\t\t,[ae_equipo_cdgo] AS ae_equipo_cdgo \n" +
                    "\t\t\t\t\t\t--Equipo \n" +
                    "\t\t\t\t\t\t,[eq_cdgo] AS ae_eq_cdgo \n" +
                    "\t\t\t\t\t\t,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc \n" +
                    "\t\t\t\t\t\t,[eq_marca] AS ae_eq_marca \n" +
                    "\t\t\t\t\t\t,[eq_modelo] AS ae_eq_modelo \n" +
                    "\t\t\t\t\t\t,[eq_desc] AS ae_eq_desc \n" +
                    "                    FROM ["+DB+"].[dbo].[asignacion_equipo] \n" +
                    "\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo] \n" +
                    "\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo] \n" +
                    "                    )\n" +
                    "\t\t\t\tasignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo] \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]  \n" +
                    "                    LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo] AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                    LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo] \n" +
                    "\t\t\tWHERE  [me_inactividad]=0 AND \n" +
                    "\t\t[me_desde_mvto_carbon]=0 AND \n" +
                    "\t\t[me_fecha_hora_fin] IS NULL \n" +
                    "\t\tAND ae_equipo_cdgo =?\n" +
                    "\t\tAND  [lr_parada]=1 \n" +
                    "ORDER BY [me_cdgo] DESC");
            query.setString(1, equipoI.getCodigo());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(2));
                equipo.setTipoEquipo(new TipoEquipo("",resultSet.getString(3),""));
                equipo.setMarca(resultSet.getString(4));
                equipo.setModelo(resultSet.getString(5));
                equipo.setDescripcion(resultSet.getString(6));
                asignacionEquipo.setEquipo(equipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(1));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(13));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo("","",resultSet.getString(7),""));
                CentroOperacion me_co= new CentroOperacion();
                me_co.setDescripcion(resultSet.getString(16));
                mvtoEquipo.setCentroOperacion(me_co);
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(8));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(9));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setDescripcion(resultSet.getString(10));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente("",resultSet.getString(11),""));
                mvtoEquipo.setArticulo(new Articulo("",resultSet.getString(12),""));
                Motonave me_motonave = new Motonave();
                me_motonave.setDescripcion(resultSet.getString(17));
                mvtoEquipo.setMotonave(me_motonave);
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setNombres(resultSet.getString(14));
                usuario_me_registra.setApellidos(resultSet.getString(15));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                listadoObjetos.add(mvtoEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<TipoEquipo> buscar_MvtoEquipoActivosTipoEquipo(ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<TipoEquipo> listadoTipoEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [te_cdgo]\n" +
                    "      ,[te_desc]\n" +
                    "\t  FROM ["+DB+"].[dbo].[tipo_equipo] c\n" +
                    "\t  INNER JOIN (       \n" +
                    "\tSELECT DISTINCT ae_eq_te_cdgo\n" +
                    "FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "INNER JOIN ( \n" +
                    "SELECT [ae_cdgo] AS ae_cdgo   \n" +
                    ",eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "                FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                ) \n" +
                    "asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                LEFT JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
                    "                LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]  AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo]  AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo]\n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "WHERE  [me_inactividad]=0 AND  \n" +
                    "[me_desde_mvto_carbon]=0 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+"  AND  \n" +
                    "[me_fecha_hora_fin] IS NULL  \n" +
                    "--ORDER BY ae_eq_te_cdgo DESC\n" +
                    ") as me ON me.ae_eq_te_cdgo=c.[te_cdgo]");
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoTipoEquipo = new ArrayList();
                    validator=false;
                }
                listadoTipoEquipo.add(new TipoEquipo(resultSet.getString(1),resultSet.getString(2),""));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los tipos de equipos en transitos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoTipoEquipo;
    }
    public ArrayList<String> buscar_MvtoEquipoActivosMarcaEquipo(String codigoTipoEquipo,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<String> listadoMarcaEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT       \n" +
                    "    DISTINCT ae_eq_marca \n" +
                    "FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "INNER JOIN ( \n" +
                    "SELECT [ae_cdgo] AS ae_cdgo  \n" +
                    ",[ae_equipo_cdgo] AS ae_equipo_cdgo  \n" +
                    "--Equipo  \n" +
                    ",[eq_cdgo] AS ae_eq_cdgo  \n" +
                    ",eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    ",eq_tipo_equipo.[te_desc] AS ae_eq_te_desc \n" +
                    ",[eq_marca] AS ae_eq_marca  \n" +
                    ",[eq_modelo] AS ae_eq_modelo  \n" +
                    ",[eq_desc] AS ae_eq_desc  \n" +
                    "                FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                ) \n" +
                    "asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
                    "                LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]  AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo] \n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "WHERE  [me_inactividad]=0 AND  \n" +
                    "[me_desde_mvto_carbon]=0 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+"  AND  \n" +
                    "[me_fecha_hora_fin] IS NULL  AND \n" +
                    "ae_eq_te_cdgo=?\n" +
                    "ORDER BY ae_eq_marca DESC");
            query.setString(1, codigoTipoEquipo);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoMarcaEquipo = new ArrayList();
                    validator=false;
                }
                listadoMarcaEquipo.add(resultSet.getString(1));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los tipos de equipos en transitos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMarcaEquipo;
    }
    public ArrayList<MvtoEquipo> buscar_MvtoEquipoActivosEquipos(Equipo equipoI,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<MvtoEquipo> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [eq_cdgo]\n" +
                    "      ,[eq_modelo]\n" +
                    "      ,[eq_desc]\n" +
                    "  FROM ["+DB+"].[dbo].[equipo]\n" +
                    "  INNER JOIN ( SELECT  DISTINCT ae_eq_cdgo \n" +
                    "                    FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "                    INNER JOIN ( \n" +
                    "                    SELECT [ae_cdgo] AS ae_cdgo  \n" +
                    "                    ,[ae_equipo_cdgo] AS ae_equipo_cdgo   \n" +
                    "                    ,[eq_cdgo] AS ae_eq_cdgo  \n" +
                    "                    ,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo  \n" +
                    "                    ,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc  \n" +
                    "                    ,[eq_marca] AS ae_eq_marca  \n" +
                    "                    ,[eq_modelo] AS ae_eq_modelo  \n" +
                    "                    ,[eq_desc] AS ae_eq_desc  \n" +
                    "                                        FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                                        ) \n" +
                    "                    asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
                    "                                        LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]   AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo] \n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                                       INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                                       INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "                    WHERE  [me_inactividad]=0 AND  \n" +
                    "                    [me_desde_mvto_carbon]=0 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+"  AND  \n" +
                    "                    [me_fecha_hora_fin] IS NULL  \n" +
                    "                    AND ae_eq_te_cdgo = ? AND ae_eq_marca LIKE ? \n" +
                    "\t\t\t\t\t) AS me ON me.ae_eq_cdgo=[equipo].[eq_cdgo]");
            query.setString(1, equipoI.getTipoEquipo().getCodigo());
            query.setString(2, equipoI.getMarca());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(1));
                equipo.setModelo(resultSet.getString(2));
                equipo.setDescripcion(resultSet.getString(3));
                asignacionEquipo.setEquipo(equipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                listadoObjetos.add(mvtoEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<TipoEquipo> buscar_MvtoCarbonActivosTipoEquipo(ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<TipoEquipo> listadoTipoEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [te_cdgo]\n" +
                    "      ,[te_desc]\n" +
                    "\t  FROM ["+DB+"].[dbo].[tipo_equipo] c\n" +
                    "\t  INNER JOIN (       \n" +
                    "\tSELECT DISTINCT ae_eq_te_cdgo\n" +
                    "FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "INNER JOIN ( \n" +
                    "SELECT [ae_cdgo] AS ae_cdgo   \n" +
                    ",eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "                FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                ) \n" +
                    "asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                LEFT JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
"                                    LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
"                                    LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]   AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo] \n" +
"                                    LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "WHERE  [me_inactividad]=0 AND  \n" +
                    "[me_desde_mvto_carbon]=1 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" AND  \n" +
                    "[me_fecha_hora_fin] IS NULL  \n" +
                    "--ORDER BY ae_eq_te_cdgo DESC\n" +
                    ") as me ON me.ae_eq_te_cdgo=c.[te_cdgo]");
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoTipoEquipo = new ArrayList();
                    validator=false;
                }
                listadoTipoEquipo.add(new TipoEquipo(resultSet.getString(1),resultSet.getString(2),""));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los tipos de equipos en transitos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoTipoEquipo;
    }
    public ArrayList<String> buscar_MvtoEquipoCarbonMarcaEquipo(String codigoTipoEquipo,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<String> listadoMarcaEquipo = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT       \n" +
                    "    DISTINCT ae_eq_marca \n" +
                    "FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "INNER JOIN ( \n" +
                    "SELECT [ae_cdgo] AS ae_cdgo  \n" +
                    ",[ae_equipo_cdgo] AS ae_equipo_cdgo  \n" +
                    "--Equipo  \n" +
                    ",[eq_cdgo] AS ae_eq_cdgo  \n" +
                    ",eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    ",eq_tipo_equipo.[te_desc] AS ae_eq_te_desc \n" +
                    ",[eq_marca] AS ae_eq_marca  \n" +
                    ",[eq_modelo] AS ae_eq_modelo  \n" +
                    ",[eq_desc] AS ae_eq_desc  \n" +
                    "                FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                ) \n" +
                    "asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
                    "                LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]   AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo] \n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo] AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo] \n" +
                    "                LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "WHERE  [me_inactividad]=0 AND  \n" +
                    "[me_desde_mvto_carbon]=1 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" AND  \n" +
                    "[me_fecha_hora_fin] IS NULL  AND \n" +
                    "ae_eq_te_cdgo=?\n" +
                    "ORDER BY ae_eq_marca DESC");
            query.setString(1, codigoTipoEquipo);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoMarcaEquipo = new ArrayList();
                    validator=false;
                }
                listadoMarcaEquipo.add(resultSet.getString(1));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los tipos de equipos en transitos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMarcaEquipo;
    }
    public ArrayList<MvtoEquipo> buscar_MvtoCarbonActivosEquipos(Equipo equipoI,ZonaTrabajo zonaTrabajo) throws SQLException{
        ArrayList<MvtoEquipo> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT [eq_cdgo]\n" +
                    "      ,[eq_modelo]\n" +
                    "      ,[eq_desc]\n" +
                    "  FROM ["+DB+"].[dbo].[equipo]\n" +
                    "  INNER JOIN ( SELECT  DISTINCT ae_eq_cdgo \n" +
                    "                    FROM ["+DB+"].[dbo].[mvto_equipo]  \n" +
                    "                    INNER JOIN ( \n" +
                    "                    SELECT [ae_cdgo] AS ae_cdgo  \n" +
                    "                    ,[ae_equipo_cdgo] AS ae_equipo_cdgo   \n" +
                    "                    ,[eq_cdgo] AS ae_eq_cdgo  \n" +
                    "                    ,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo  \n" +
                    "                    ,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc  \n" +
                    "                    ,[eq_marca] AS ae_eq_marca  \n" +
                    "                    ,[eq_modelo] AS ae_eq_modelo  \n" +
                    "                    ,[eq_desc] AS ae_eq_desc  \n" +
                    "                                        FROM ["+DB+"].[dbo].[asignacion_equipo]  \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]  \n" +
                    "                    INNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]  \n" +
                    "                                        ) \n" +
                    "                    asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]  \n" +
                    "                                        INNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]   \n" +
                    "                                        LEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]  AND me_cliente.[cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]  AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo]  AND me_motonave.[mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo]\n" +
                    "                                        LEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]  \n" +
                    "                INNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [me_cntro_cost_auxiliar_cdgo]=[lzt_cntro_cost_auxiliar_cdgo]\n" +
                    "                INNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]= [zt_cdgo] \n" +
                    "                    WHERE  [me_inactividad]=0 AND  \n" +
                    "[me_desde_mvto_carbon]=1 AND [zt_cdgo]="+zonaTrabajo.getCodigo()+" AND  \n" +
                    "                    [me_fecha_hora_fin] IS NULL  \n" +
                    "                    AND ae_eq_te_cdgo = ? AND ae_eq_marca LIKE ? \n" +
                    "\t\t\t\t\t) AS me ON me.ae_eq_cdgo=[equipo].[eq_cdgo]");
            query.setString(1, equipoI.getTipoEquipo().getCodigo());
            query.setString(2, equipoI.getMarca());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(1));
                equipo.setModelo(resultSet.getString(2));
                equipo.setDescripcion(resultSet.getString(3));
                asignacionEquipo.setEquipo(equipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                listadoObjetos.add(mvtoEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<MvtoEquipo> reporteMvtoEquipo_EquiposTrabajado(Usuario us, String fechaInicio, String fechaFinal) {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        if((fechaInicio.equals("")) && (fechaFinal.equals(""))){
            fechaInicio="(SELECT CONCAT (CONVERT (date, GETDATE()),' 00:00:00.0'))";
            fechaFinal="(SELECT CONCAT (CONVERT (date, GETDATE()),' 23:59:59.999'))";
        }else{
            fechaInicio= "'"+fechaInicio+"'";
            fechaFinal= "'"+fechaFinal+"'";
        }

        ArrayList<MvtoEquipo> listadoMvtoEquipo = null;
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [me_cdgo]--1\n" +
                    "      ,[me_asignacion_equipo_cdgo]--2\n" +
                    "\t\t,[ae_equipo_cdgo]--3\n" +
                    "\t\t\t,[eq_tipo_equipo_cdgo]\t--4\n" +
                    "\t\t\t\t,[te_desc]--5\n" +
                    "\t\t\t,[eq_desc]--6\n" +
                    "\t\t\t,[eq_modelo]--7\n" +
                    "      ,[me_fecha]--8\n" +
                    "      ,[me_num_orden]--9\n" +
                    "      ,[me_cntro_oper_cdgo]--10\n" +
                    "\t\t\t,[co_desc]--11\n" +
                    "      ,[me_cntro_cost_auxiliar_cdgo]--12\n" +
                    "\t\t\t\t,me_ccs_origen.[ccs_desc]--13\n" +
                    "\t\t\t, me_cc_auxiliar_origen.[cca_desc]--14\n" +
                    "      ,[me_labor_realizada_cdgo]--15\n" +
                    "\t\t\t,[lr_desc]--16\n" +
                    "      ,[me_cliente_cdgo]--17\n" +
                    "\t\t\t,[cl_desc]--18\n" +
                    "      ,[me_articulo_cdgo]--19\n" +
                    "\t\t\t,[ar_desc]--20\n" +
                    "      ,[me_motonave_cdgo]--21\n" +
                    "\t\t\t,[mn_desc]--22\n" +
                    "      ,[me_fecha_hora_inicio]--23\n" +
                    "      ,[me_fecha_hora_fin]--24\n" +
                    "      ,[me_total_minutos]--25\n" +
                    "      ,[me_valor_hora]--26\n" +
                    "      ,[me_costo_total]--27\n" +
                    "      ,[me_recobro_cdgo]--28\n" +
                    "\t\t\t,[rc_desc]--29\n" +
                    "      ,[me_usuario_registro_cdgo]--30\n" +
                    "\t\t\t,me_us_registro.[us_cdgo]--31\n" +
                    "\t\t\t,me_us_registro.[us_nombres]--32\n" +
                    "\t\t\t,me_us_registro.[us_apellidos]--33\n" +
                    "      ,[me_usuario_autorizacion_cdgo]--34\n" +
                    "\t\t\t,me_us_autoriza.[us_cdgo]--35\n" +
                    "\t\t\t,me_us_autoriza.[us_nombres]--36\n" +
                    "\t\t\t,me_us_autoriza.[us_apellidos]--37\n" +
                    "      ,[me_autorizacion_recobro_cdgo]--38\n" +
                    "\t\t  ,[are_desc]--39\n" +
                    "      ,[me_observ_autorizacion]--40\n" +
                    "      ,[me_inactividad]--41\n" +
                    "      ,[me_causa_inactividad_cdgo]--42\n" +
                    "      ,[me_usuario_inactividad_cdgo]--43\n" +
                    "      ,[me_motivo_parada_estado]--44\n" +
                    "      ,[me_motivo_parada_cdgo]--45\n" +
                    "\t\t\t,[mpa_desc]--46\n" +
                    "      ,[me_observ]--47\n" +
                    "      ,[me_estado]--48\n" +
                    "      ,[me_desde_mvto_carbon]--49\n" +
                    "      ,[me_cntro_cost]--50\n" +
                    "      ,[me_cntro_cost_auxiliarDestino_cdgo]--51\n" +
                    "\t\t  ,me_cc_auxiliar_destino.[cca_desc]--52\n" +
                    "      ,[me_cntro_cost_mayor_cdgo]--53\n" +
                    "      ,[me_usuario_cierre_cdgo]--54\n" +
                    "\t\t\t,me_us_cierre.[us_cdgo]--55\n" +
                    "\t\t\t,me_us_cierre.[us_nombres]--56\n" +
                    "\t\t\t,me_us_cierre.[us_apellidos]--57\n" +
                    "\t\t\t,[zt_cdgo] --58\n" +
                    "\t\t\t,[zt_desc]--59\n" +
                    "  FROM ["+DB+"].[dbo].[mvto_equipo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[asignacion_equipo] ON [me_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[equipo] ON [eq_cdgo]=[ae_equipo_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[tipo_equipo] ON [eq_tipo_equipo_cdgo]=[te_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cc_auxiliar_origen ON [me_cntro_cost_auxiliar_cdgo] = me_cc_auxiliar_origen.[cca_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] me_ccs_origen ON [cca_cntro_cost_subcentro_cdgo]= me_ccs_origen.[ccs_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[cliente] ON [me_cliente_cdgo]= [cl_cdgo] AND [cl_base_datos_cdgo]=[me_cliente_base_datos_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]= [ar_cdgo] AND [ar_base_datos_cdgo]=[me_articulo_base_datos_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[motonave] ON [me_motonave_cdgo]=[mn_cdgo] AND [mn_base_datos_cdgo]=[me_motonave_base_datos_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[usuario] me_us_autoriza ON [me_usuario_autorizacion_cdgo]=me_us_autoriza.[us_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[autorizacion_recobro] ON [me_autorizacion_recobro_cdgo]=[are_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[motivo_parada] ON [me_motivo_parada_cdgo]=[mpa_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cc_auxiliar_destino ON [me_cntro_cost_auxiliarDestino_cdgo]=me_cc_auxiliar_destino.[cca_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[recobro] ON [me_recobro_cdgo] =[rc_cdgo]\n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[usuario] me_us_cierre ON [me_usuario_cierre_cdgo]=me_us_cierre.[us_cdgo]\n " +
                    "\tINNER JOIN ["+DB+"].[dbo].[listado_zona_trabajo] ON [lzt_cntro_cost_auxiliar_cdgo]=[me_cntro_cost_auxiliar_cdgo]\n " +
                    "\tINNER JOIN ["+DB+"].[dbo].[zona_trabajo] ON [lzt_zona_trabajo_cdgo]=[zt_cdgo]\n " +
                    "  WHERE  [me_fecha] BETWEEN "+fechaInicio+" AND "+fechaFinal+" AND [me_estado]=1  AND [me_desde_mvto_carbon]=0 AND [me_usuario_registro_cdgo]=? ORDER BY [me_cdgo] DESC");
            queryBuscar.setString(1,us.getCodigo());
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            Boolean validador=true;
            while (resultSetBuscar.next()) {
                if(validador){
                    listadoMvtoEquipo= new ArrayList<>();
                    validador=false;
                }
                MvtoEquipo Objeto= new MvtoEquipo();
                Objeto.setCodigo(resultSetBuscar.getString(1));
                        Equipo equipoAsignacion = new Equipo();
                        equipoAsignacion.setCodigo(resultSetBuscar.getString(3));
                        equipoAsignacion.setDescripcion(resultSetBuscar.getString(6));
                        equipoAsignacion.setModelo(resultSetBuscar.getString(7));
                            TipoEquipo tipoEquipo = new TipoEquipo();
                            tipoEquipo.setCodigo(resultSetBuscar.getString(4));
                            tipoEquipo.setDescripcion(resultSetBuscar.getString(5));
                        equipoAsignacion.setTipoEquipo(tipoEquipo);
                    AsignacionEquipo asignacionEquipo= new AsignacionEquipo();
                    asignacionEquipo.setEquipo(equipoAsignacion);
                Objeto.setAsignacionEquipo(asignacionEquipo);
                    CentroOperacion centroOperacion= new CentroOperacion();
                    centroOperacion.setCodigo(resultSetBuscar.getInt(10));
                    centroOperacion.setDescripcion(resultSetBuscar.getString(11));
                Objeto.setCentroOperacion(centroOperacion);
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                        if(resultSetBuscar.getString(12) != null){
                            centroCostoAuxiliar.setCodigo(resultSetBuscar.getString(12));
                            centroCostoAuxiliar.setDescripcion(resultSetBuscar.getString(14));
                            CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                            centroCostoSubCentro.setDescripcion(resultSetBuscar.getString(13));
                            centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                        }else{
                            centroCostoAuxiliar.setCodigo("");
                            centroCostoAuxiliar.setDescripcion("");
                            CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                            centroCostoSubCentro.setDescripcion("");
                            centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                        }
                Objeto.setCentroCostoAuxiliar(centroCostoAuxiliar);
                    LaborRealizada laborRealizada = new LaborRealizada();
                        laborRealizada.setCodigo(resultSetBuscar.getString(15));
                        laborRealizada.setDescripcion(resultSetBuscar.getString(16));
                Objeto.setLaborRealizada(laborRealizada);

                if(resultSetBuscar.getString(17) !=null){
                    Objeto.setCliente(new Cliente(resultSetBuscar.getString(17),resultSetBuscar.getString(18),""));
                }else{
                    Objeto.setCliente(new Cliente("","",""));
                }

                if(resultSetBuscar.getString(19) !=null){
                    Objeto.setArticulo(new Articulo(resultSetBuscar.getString(19),resultSetBuscar.getString(20),""));
                }else{
                    Objeto.setArticulo(new Articulo("","",""));
                }

                if(resultSetBuscar.getString(21) !=null){
                    Objeto.setMotonave(new Motonave(resultSetBuscar.getString(21),resultSetBuscar.getString(22),""));
                }else{
                    Objeto.setMotonave(new Motonave("","",""));
                }
                Objeto.setFechaHoraInicio(resultSetBuscar.getString(23));
                Objeto.setFechaHoraFin(resultSetBuscar.getString(24));
                Objeto.setTotalMinutos(resultSetBuscar.getString(25));
                Recobro recobro= new Recobro();
                if(resultSetBuscar.getString(28) != null) {
                    recobro.setCodigo(resultSetBuscar.getString(28));
                    recobro.setDescripcion(resultSetBuscar.getString(29));
                }else{
                    recobro.setCodigo("");
                    recobro.setDescripcion("");
                }
                Objeto.setRecobro(recobro);
                Usuario usuarioRegistra = new Usuario();
                if(resultSetBuscar.getString(30) !=null){
                    usuarioRegistra.setCodigo(resultSetBuscar.getString(31));
                    usuarioRegistra.setNombres(resultSetBuscar.getString(32));
                    usuarioRegistra.setApellidos(resultSetBuscar.getString(33));
                }else{
                    usuarioRegistra.setCodigo("");
                    usuarioRegistra.setNombres("");
                    usuarioRegistra.setApellidos("");
                }
                Objeto.setUsuarioQuieRegistra(usuarioRegistra);
                Usuario usuarioAutoriza = new Usuario();
                if(resultSetBuscar.getString(34) !=null){
                    usuarioAutoriza.setCodigo(resultSetBuscar.getString(35));
                    usuarioAutoriza.setNombres(resultSetBuscar.getString(36));
                    usuarioAutoriza.setApellidos(resultSetBuscar.getString(37));
                }else{
                    usuarioAutoriza.setCodigo("");
                    usuarioAutoriza.setNombres("");
                    usuarioAutoriza.setApellidos("");
                }
                Objeto.setUsuarioAutorizaRecobro(usuarioAutoriza);
                AutorizacionRecobro autorizacionRecobro = new AutorizacionRecobro();
                if(resultSetBuscar.getString(38) !=null){
                    autorizacionRecobro.setDescripcion(resultSetBuscar.getString(39));
                }else{
                    autorizacionRecobro.setDescripcion("");
                }
                Objeto.setAutorizacionRecobro(autorizacionRecobro);
                Objeto.setObservacionAutorizacion(resultSetBuscar.getString(40));
                if(resultSetBuscar.getString(44).equals("1")){
                    Objeto.setMotivoParadaEstado("SI");
                }else{
                    Objeto.setMotivoParadaEstado("NO");
                }
                MotivoParada motivoParada= new MotivoParada();
                if(resultSetBuscar.getString(45) !=null){
                    motivoParada.setDescripcion(resultSetBuscar.getString(46));
                }else{
                    motivoParada.setDescripcion("");
                }
                Objeto.setMotivoParada(motivoParada);
                Objeto.setObservacionMvtoEquipo(resultSetBuscar.getString(47));
                CentroCostoAuxiliar centroCostoAuxiliarDestino = new CentroCostoAuxiliar();
                if(resultSetBuscar.getString(51) != null){
                    centroCostoAuxiliarDestino.setDescripcion(resultSetBuscar.getString(52));
                }else{
                    centroCostoAuxiliarDestino.setDescripcion("");
                }
                Objeto.setCentroCostoAuxiliarDestino(centroCostoAuxiliarDestino);
                Usuario usuarioCierra = new Usuario();
                if(resultSetBuscar.getString(54) !=null){
                    usuarioCierra.setCodigo(resultSetBuscar.getString(55));
                    usuarioCierra.setNombres(resultSetBuscar.getString(56));
                    usuarioCierra.setApellidos(resultSetBuscar.getString(57));
                }else{
                    usuarioCierra.setCodigo("");
                    usuarioCierra.setNombres("");
                    usuarioCierra.setApellidos("");
                }
                Objeto.setUsuarioQuienCierra(usuarioCierra);
                ZonaTrabajo zonaTrabajo = new ZonaTrabajo();
                zonaTrabajo.setCodigo(resultSetBuscar.getString(58));
                zonaTrabajo.setDescripcion(resultSetBuscar.getString(59));
                Objeto.setZonaTrabajo(zonaTrabajo);
                listadoMvtoEquipo.add(Objeto);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMvtoEquipo;
    }




    public Equipo buscarEspecifico(String codigo) throws SQLException {
        Equipo Objeto = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        try{
            PreparedStatement queryBuscar= conexion.prepareStatement("SELECT [eq_cdgo] --1\n" +
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
                    "  ,[eq_actvo_fijo_id]"+
                    "  ,[eq_actvo_fijo_referencia]"+
                    "  ,[eq_actvo_fijo_desc]"+
                    "  FROM ["+DB+"].[dbo].[equipo]\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[tipo_equipo] ON te_cdgo = eq_tipo_equipo_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON pe_cdgo= eq_proveedor_equipo_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] clasificador1 ON clasificador1.ce_cdgo = eq_clasificador1_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[clasificador_equipo] clasificador2 ON clasificador2.ce_cdgo = eq_clasificador2_cdgo\n" +
                    "		INNER JOIN ["+DB+"].[dbo].[equipo_pertenencia] ON ep_cdgo=eq_equipo_pertenencia_cdgo "
                    + "  WHERE [eq_cdgo] =?;");
            queryBuscar.setString(1, codigo);
            ResultSet resultSetBuscar= queryBuscar.executeQuery();
            while(resultSetBuscar.next()){
                Objeto = new Equipo();
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
                Objeto.setActivoFijo_codigo(resultSetBuscar.getString(33));
                Objeto.setActivoFijo_referencia(resultSetBuscar.getString(34));
                Objeto.setActivoFijo_descripcion(resultSetBuscar.getString(35));
            }
        }catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<Cliente> buscarClientes(String valorConsulta) throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        ArrayList<Cliente> listadoCliente = new ArrayList();
        Statement statement = null;
        try{
            statement = conexion.createStatement();
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT [cl_cdgo]\n" +
                                                                    "      ,[cl_desc]\n" +
                                                                    "      ,[cl_estad],[cl_base_datos_cdgo]"+
                                                                     " FROM ["+DB+"].[dbo].[cliente] "+
                                                                    " WHERE  [cl_estad]=1 AND [cl_desc] like ? ORDER BY [cl_desc] ASC;");

            query.setString(1, "%"+valorConsulta+"%");
            resultSet= query.executeQuery();
            while(resultSet.next()){
                Cliente Objeto = new Cliente();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                Objeto.setBaseDatos(new BaseDatos(resultSet.getString(4)));
                //Objeto.setValorRecobro(resultSet.getInt(4));
                listadoCliente.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los clientes");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoCliente;
    }
    public ArrayList<Motonave> buscarMotonaves(String valorConsulta) throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();
        ArrayList<Motonave> listadoMotonave = new ArrayList();
        Statement statement = null;
        try{
            statement = conexion.createStatement();
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT [mn_cdgo]\n" +
                    "      ,[mn_desc]\n" +
                    "      ,[mn_estad],[mn_base_datos_cdgo]"+
                    " FROM ["+DB+"].[dbo].[motonave] "+
                    " WHERE  [mn_estad]=1 AND [mn_desc] like ? ORDER BY [mn_desc] ASC;");

            query.setString(1, "%"+valorConsulta+"%");
            resultSet= query.executeQuery();
            while(resultSet.next()){
                Motonave Objeto = new Motonave();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                Objeto.setBaseDatos(new BaseDatos(resultSet.getString(4)));
                //Objeto.setValorRecobro(resultSet.getInt(4));
                listadoMotonave.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los clientes");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoMotonave;
    }
    public ArrayList<Motonave> buscarMotonavesControlCarga(String valorConsulta) throws SQLException{
        Ccarga_GP control_gp = new Ccarga_GP(tipoConexion);
        Ccarga_OPP control_opp = new Ccarga_OPP(tipoConexion);
        Connection conexionGP= control_gp.ConectarBaseDatos();
        String DB_gp=control_gp.getBaseDeDatos();
        String DB_opp=control_opp.getBaseDeDatos();
        ArrayList<Motonave> listadoMotonave = new ArrayList();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexionGP.prepareStatement("SELECT  [mo_cdgo]\n" +
                                                                    "      ,[mo_nmbre]\n" +
                                                                    "  ,1 AS [mo_estad]  \n" +
                                                                    "  ,1 AS [mo_database]  \n" +
                                                                    "  FROM ["+DB_gp+"].[dbo].[mtnve]" +
                    "                                                   UNION " +
                    "                                               SELECT [mo_cdgo]\n" +
                    "                                                     ,[mo_nmbre]\n" +
                    "                                                     ,1 AS [mo_estad]\n" +
                "                                                         ,2 AS [mo_database]  \n" +
                    "                                                FROM ["+DB_opp+"].[dbo].[mtnve]" +
                                                                    "  WHERE [mo_nmbre] like ? ORDER BY [mo_nmbre] ASC;");
            query.setString(1, "%"+valorConsulta+"%");
            resultSet= query.executeQuery();
            while(resultSet.next()){
                Motonave Objeto = new Motonave();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                Objeto.setBaseDatos(new BaseDatos(resultSet.getString(4)));
                listadoMotonave.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los clientes");
            sqlException.printStackTrace();
        }
        control_gp.cerrarConexionBaseDatos();
        return listadoMotonave;
    }
    public ArrayList<Articulo> buscarArticulos(String valorConsulta) throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();

        ArrayList<Articulo> listadoObjetos = new ArrayList();
        try{
            ResultSet resultSet;
            if(valorConsulta.equals("")){
                PreparedStatement query= conexion.prepareStatement("SELECT ar_cdgo," +
                        " ar_desc, " +
                        "CASE WHEN (ar_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS ar_estad ,[ar_base_datos_cdgo]" +
                        "  FROM ["+DB+"].[dbo].[articulo] WHERE ar_estad=1 ORDER BY ar_desc ASC;");
                resultSet= query.executeQuery();
            }else{
                PreparedStatement query= conexion.prepareStatement("SELECT ar_cdgo, " +
                        "ar_desc, " +
                        " CASE WHEN (ar_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS ar_estad ,[ar_base_datos_cdgo]" +
                        " FROM ["+DB+"].[dbo].[articulo] WHERE ar_estad=1 AND ([ar_cdgo] LIKE ? OR [ar_desc] like ?) ORDER BY ar_desc ASC;");
                query.setString(1, "%"+valorConsulta+"%");
                query.setString(2, "%"+valorConsulta+"%");
                resultSet= query.executeQuery();
            }
            while(resultSet.next()){
                Articulo Objeto = new Articulo();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                Objeto.setBaseDatos(new BaseDatos(resultSet.getString(4)));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los articulos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<ProveedorEquipo> buscarProveedoresEquipos() throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        conexion= control.ConectarBaseDatos();
        String DB=control.getBaseDeDatos();

        ArrayList<ProveedorEquipo> listadoObjetos = new ArrayList();
        try{
            ResultSet resultSet;
            PreparedStatement query= conexion.prepareStatement("SELECT pe_cdgo,pe_nit, pe_desc, CASE WHEN (pe_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS pe_estad FROM ["+DB+"].[dbo].[proveedor_equipo] WHERE pe_estad=1;");
            //query.setString(1, "%"+valorConsulta+"%");
            resultSet= query.executeQuery();
            while(resultSet.next()){
                ProveedorEquipo Objeto = new ProveedorEquipo();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setNit(resultSet.getString(2));
                Objeto.setDescripcion(resultSet.getString(3));
                Objeto.setEstado(resultSet.getString(4));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los proveedores de Equipos");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public int registrarMvtoEquipo(Usuario usuario, MvtoEquipo mvtoEquipo) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try {
            if (mvtoEquipo.getRecobro().getCodigo().equals("1")) {
                mvtoEquipo.getRecobro().setCodigo("2");
                mvtoEquipo.getRecobro().setDescripcion("PENDIENTE CONFIRMACIÓN");
            }
        }catch(Exception e){
            System.out.println("Error al procesar el recobro");
        }
        conexion= control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                String cdgo_cliente;
                String cdgo_articulo;
                String cdgo_motonave;
                String cdgo_cliente_db;
                String cdgo_articulo_db;
                String cdgo_motonave_db;
                if(mvtoEquipo.getCliente().getCodigo()==null){
                    cdgo_cliente=null;
                    cdgo_cliente_db=null;
                }else{
                    cdgo_cliente="'"+mvtoEquipo.getCliente().getCodigo()+"'";
                    cdgo_cliente_db=mvtoEquipo.getCliente().getBaseDatos().getCodigo();
                }

                if(mvtoEquipo.getArticulo().getCodigo()==null){
                    cdgo_articulo=null;
                    cdgo_articulo_db=null;
                }else{
                    cdgo_articulo="'"+mvtoEquipo.getArticulo().getCodigo()+"'";
                    cdgo_articulo_db=mvtoEquipo.getArticulo().getBaseDatos().getCodigo();
                }

                if(mvtoEquipo.getMotonave().getCodigo()==null){
                    cdgo_motonave=null;
                    cdgo_motonave_db=null;
                }else{
                    if(mvtoEquipo.getMotonave().getCodigo() != null) {
                        if (!(new ControlDB_Carbon(tipoConexion).validarExistenciaMotonave(mvtoEquipo.getMotonave()))) {
                            int n = new ControlDB_Carbon(tipoConexion).registrarMotonave(mvtoEquipo.getMotonave(), usuario);
                            if (n == 1) {
                                System.out.println("Hemos registrado una nueva motonave en el sistema");
                            }
                        }
                    }
                    cdgo_motonave="'"+mvtoEquipo.getMotonave().getCodigo()+"'";
                    cdgo_motonave_db=mvtoEquipo.getMotonave().getBaseDatos().getCodigo();
                }


                PreparedStatement queryRegistrarT = conexion.prepareStatement("" +
                        "DECLARE @consecutivo BIGINT=(SELECT (CASE WHEN (MAX([me_cdgo]) IS NULL) \n" +
                        " THEN 1 ELSE (MAX([me_cdgo])+1) END)AS [mc_cdgo] \n" +
                        " FROM ["+DB+"].[dbo].[mvto_equipo]) " +
                        "  INSERT INTO ["+DB+"].[dbo].[mvto_equipo]\n" +
                        "           ([me_cdgo],[me_asignacion_equipo_cdgo],[me_fecha],[me_proveedor_equipo_cdgo],[me_num_orden],[me_cntro_oper_cdgo],[me_cntro_cost_auxiliar_cdgo]\n" +
                        "           ,[me_labor_realizada_cdgo],[me_cliente_cdgo],[me_articulo_cdgo],[me_motonave_cdgo],[me_fecha_hora_inicio],[me_fecha_hora_fin],[me_total_minutos]\n" +
                        "           ,[me_valor_hora],[me_costo_total],[me_recobro_cdgo],[me_cliente_recobro_cdgo],[me_costo_total_recobro_cliente],[me_usuario_registro_cdgo]\n" +
                        "           ,[me_usuario_autorizacion_cdgo],[me_autorizacion_recobro_cdgo],[me_observ_autorizacion],[me_inactividad],[me_causa_inactividad_cdgo]\n" +
                        "           ,[me_usuario_inactividad_cdgo],[me_motivo_parada_estado],[me_motivo_parada_cdgo],[me_observ],[me_estado],[me_desde_mvto_carbon]," +
                        "[me_cntro_cost_auxiliarDestino_cdgo],[me_cliente_base_datos_cdgo],[me_motonave_base_datos_cdgo],[me_articulo_base_datos_cdgo])\n" +
                        "     VALUES ((select @consecutivo) --me_cdgo\n" +
                        "           ,"+mvtoEquipo.getAsignacionEquipo().getCodigo()+"\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))) \n" +
                        "           ,"+mvtoEquipo.getProveedorEquipo().getCodigo()+"\n" +
                        "           ,NULL--me_num_orden\n" +
                        "           ,"+mvtoEquipo.getCentroOperacion().getCodigo()+"\n" +
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliar().getCodigo()+"\n" +
                        "           ,"+mvtoEquipo.getLaborRealizada().getCodigo()+"\n" +
                        "           ,"+cdgo_cliente+"\n" +
                        "           ,"+cdgo_articulo+"\n" +
                        "           ,"+cdgo_motonave+"--me_motonave_cdgo\n" +
                        "           ,(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME())))--me_fecha_hora_inicio\n" +
                        "           ,NULL\n" +//--me_fecha_hora_fin
                        "           ,NULL-- \n" +//--me_total_minutos
                        "           ,NULL--\n" +//--me_valor_hora
                        "           ,NULL--\n" +//--me_costo_total
                        "           ,"+mvtoEquipo.getRecobro().getCodigo()+"--\n" +//--me_recobro_cdgo
                        "           ,NULL--\n" +//--me_cliente_recobro_cdgo
                        "           ,NULL--\n" +//--me_costo_total_recobro_cliente
                        "           ,"+mvtoEquipo.getUsuarioQuieRegistra().getCodigo()+"--\n" +//--me_usuario_registro_cdgo
                        "           ,NULL--\n" +//--me_usuario_autorizacion_cdgo
                        "           ,NULL--\n" +//--me_autorizacion_recobro_cdgo
                        "           ,NULL--\n" +//--me_observ_autorizacion
                        "           ,0   --\n" +//--me_inactividad
                        "           ,NULL--\n" +//--me_causa_inactividad_cdgo
                        "           ,NULL--\n" +//--me_usuario_inactividad_cdgo
                        "           ,0--\n" +//--me_motivo_parada_estado
                        "           ,NULL--\n" +//--me_motivo_parada_cdgo
                        "           ,NULL--\n" +//--me_observ
                        "           ,"+mvtoEquipo.getEstado()+"--\n" +//--me_estado
                        "           ,"+mvtoEquipo.getDesdeCarbon()+"--\n" +//--me_desde_mvto_carbon
                        "           ,"+mvtoEquipo.getCentroCostoAuxiliarDestino().getCodigo()+"\n" +//--me_centrocostoAuxiliar
                        "           ,"+cdgo_cliente_db+"\n" +
                        "           ,"+cdgo_motonave_db+"\n" +
                        "           ,"+cdgo_articulo_db+"\n" +
                        "); \n" +
                        "SELECT @consecutivo;");
                result=1;
                ResultSet resultSet= queryRegistrarT.executeQuery();
                while(resultSet.next()){
                    if(result==1){
                        mvtoEquipo.setCodigo(resultSet.getString(1));
                    }
                }
                if (result == 1) {
                    conexion= control.ConectarBaseDatos();
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
                            "           ,'MVTO_EQUIPO'" +
                            "           , CONCAT (?,?,' Asignacion Código: ',?,' Equipo Tipo: ',?,' Equipo Marca: ',?,' Equipo Modelo: ',?,' Equipo Descripción: ',?));");
                    Query_Auditoria.setString(1, usuario.getCodigo());
                    Query_Auditoria.setString(2, namePc);
                    Query_Auditoria.setString(3, ipPc);
                    Query_Auditoria.setString(4, macPC);
                    Query_Auditoria.setString(5, mvtoEquipo.getCodigo());
                    Query_Auditoria.setString(6, "Se registró un nuevo inicio de el modulo de equipo desde un dispositivo Movil, con Mvto_Equipo Código: ");
                    Query_Auditoria.setString(7, mvtoEquipo.getCodigo());
                    Query_Auditoria.setString(8, mvtoEquipo.getAsignacionEquipo().getCodigo());
                    Query_Auditoria.setString(9, mvtoEquipo.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
                    Query_Auditoria.setString(10, mvtoEquipo.getAsignacionEquipo().getEquipo().getMarca());
                    Query_Auditoria.setString(11, mvtoEquipo.getAsignacionEquipo().getEquipo().getModelo());
                    Query_Auditoria.setString(12, mvtoEquipo.getAsignacionEquipo().getEquipo().getDescripcion());
                    Query_Auditoria.execute();
                    result=1;
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
    public int cerrarCiclo_mtvoEquipo(MvtoEquipo Objeto, Usuario us,MotivoParada motivoParada) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        try {
            if (Objeto.getRecobro().getCodigo().equals("1")) {
                Objeto.getRecobro().setCodigo("2");
                Objeto.getRecobro().setDescripcion("PENDIENTE CONFIRMACIÓN");
            }
        }catch(Exception e){
            System.out.println("Error al procesar el recobro");
        }
        conexion= control.ConectarBaseDatos();
        try{
            conexion= control.ConectarBaseDatos();
            PreparedStatement query= conexion.prepareStatement("DECLARE @fechaHoraFin DATETIME=(SELECT CONVERT (DATETIME,(SELECT SYSDATETIME()))); \n" +
                    "UPDATE ["+DB+"].[dbo].[mvto_equipo] \n" +
                    "SET [me_fecha_hora_fin]=@fechaHoraFin, \n" +
                    "[me_motivo_parada_estado]=1, \n" +
                    "[me_motivo_parada_cdgo]=?,\n" +
                    "[me_num_orden]=?,\n" +
                    "[me_observ]=?,\n " +
                    "[me_recobro_cdgo]=?,\n" +
                    "[me_total_minutos]=(SELECT DATEDIFF(minute, (SELECT [me_fecha_hora_inicio]  \n" +
                    " FROM ["+DB+"].[dbo].[mvto_equipo] \n" +
                    " WHERE [me_cdgo]=?), @fechaHoraFin)), [me_usuario_cierre_cdgo]='"+Objeto.getUsuarioQuienCierra().getCodigo()+"' WHERE [me_cdgo]=?");
            query.setString(1, motivoParada.getCodigo());
            query.setString(2, Objeto.getNumeroOrden());
            query.setString(3, Objeto.getObservacionMvtoEquipo());
            query.setString(4, Objeto.getRecobro().getCodigo());
            query.setString(5, Objeto.getCodigo());
            query.setString(6, Objeto.getCodigo());
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
                        "           ,'CIERRE_CICLO_EQUIPO'" +
                        "           ,CONCAT('Se registró un cierre de ciclo de Equipo en el sistema sobre MvtoEquipo ',?,'Código: ',?));");
                Query_Auditoria.setString(1, us.getCodigo());
                Query_Auditoria.setString(2, namePc);
                Query_Auditoria.setString(3, ipPc);
                Query_Auditoria.setString(4, macPC);
                Query_Auditoria.setString(5, Objeto.getCodigo());
                Query_Auditoria.setString(6, " MvtoEquipo ");
                Query_Auditoria.setString(7, Objeto.getCodigo());
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
    /*public ArrayList<MvtoEquipo> buscar_MvtoEquipoActivosOld(Equipo equipoI) throws SQLException{
        ArrayList<MvtoEquipo> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT\t TOP 15 [me_cdgo]   -- 1\n" +
                    "\t\t\t,[me_asignacion_equipo_cdgo]   --2\n" +
                    "\t\t\t,[ae_cdgo] --3\n" +
                    "\t\t\t\t,ae_cntro_oper_cdgo --4\n" +
                    "\t\t\t\t\t--Centro Operacion\n" +
                    "\t\t\t\t\t,ae_co_cdgo --5\n" +
                    "\t\t\t\t\t,ae_co_desc --6\n" +
                    "\t\t\t\t\t,CASE ae_co_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_co_estado --7\n" +
                    "\t\t\t\t,ae_solicitud_listado_equipo_cdgo --8\n" +
                    "\t\t\t\t\t-- Solicitud Listado Equipo\n" +
                    "\t\t\t\t\t,ae_sle_cdgo --9\n" +
                    "\t\t\t\t\t,ae_sle_solicitud_equipo_cdgo --10\n" +
                    "\t\t\t\t\t\t\t--Solicitud Equipo\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_cdgo --11\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_cntro_oper_cdgo --12\n" +
                    "\t\t\t\t\t\t\t\t--CentroOperación SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_co_cdgo --13\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_co_desc --14\n" +
                    "\t\t\t\t\t\t\t\t,CASE ae_sle_se_co_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_co_estad --15\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_fecha --16\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_usuario_realiza_cdgo --17\n" +
                    "\t\t\t\t\t\t\t\t--Usuario SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_cdgo --18\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_clave --19\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_nombres --20\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_apellidos --21\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_perfil_cdgo --22\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Perfil Usuario Quien Registra Manual\n" +
                    "\t\t\t\t\t\t\t\t\t\t,ae_sle_se_prf_us_registra_cdgo --23\n" +
                    "\t\t\t\t\t\t\t\t\t\t,ae_sle_se_prf_us_registra_desc --24\n" +
                    "\t\t\t\t\t\t\t\t\t\t,CASE ae_sle_se_prf_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_registra_estad --25\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_registra_correo --26\n" +
                    "\t\t\t\t\t\t\t\t,CASE ae_sle_se_us_registra_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_registra_estad --27\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_fecha_registro --28\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_estado_solicitud_equipo_cdgo --29\n" +
                    "\t\t\t\t\t\t\t\t--Estado de la solicitud\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_ese_cdgo --30\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_ese_desc --31\n" +
                    "\t\t\t\t\t\t\t\t,CASE ae_sle_se_ese_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ese_estad --32\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_fecha_confirmacion --33\n" +
                    "\t\t\t\t\t\t\t,ae_se_usuario_confirma_cdgo --34\n" +
                    "\t\t\t\t\t\t\t\t--Usuario SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_cdgo --35\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_clave --36\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_nombres --37\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_apellidos --38\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_perfil_cdgo --39\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Perfil Usuario Quien Registra Manual\n" +
                    "\t\t\t\t\t\t\t\t\t\t,ae_sle_se_prf_us_confirma_cdgo --40\n" +
                    "\t\t\t\t\t\t\t\t\t\t,ae_sle_se_prf_us_confirma_desc --41\n" +
                    "\t\t\t\t\t\t\t\t\t\t,CASE ae_sle_se_prf_us_confirma_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_prf_us_confirma_estad --42\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_us_confirma_correo --43\n" +
                    "\t\t\t\t\t\t\t\t,CASE ae_sle_se_us_confirma_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_us_confirma_estado --44\n" +
                    "\t\t\t\t\t\t\t,ae_sle_se_confirmacion_solicitud_equipo_cdgo --45\n" +
                    "\t\t\t\t\t\t\t\t--Confirmacion solicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_cse_cdgo --46\n" +
                    "\t\t\t\t\t\t\t\t,ae_sle_se_ces_desc --47\n" +
                    "\t\t\t\t\t\t\t\t,CASE ae_sle_se_ces_estado WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_se_ces_estado --48\n" +
                    "\t\t\t\t\t,ae_sle_tipo_equipo_cdgo --49\n" +
                    "\t\t\t\t\t\t--Tipo de Equipo\n" +
                    "\t\t\t\t\t\t,ae_sle_te_cdgo --50\n" +
                    "\t\t\t\t\t\t,ae_sle_te_desc --51\n" +
                    "\t\t\t\t\t\t,CASE ae_sle_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_te_estad --52\n" +
                    "\t\t\t\t\t,ae_sle_marca_equipo --53\n" +
                    "\t\t\t\t\t,ae_sle_modelo_equipo --54\n" +
                    "\t\t\t\t\t,ae_sle_cant_equip --55\n" +
                    "\t\t\t\t\t,ae_sle_observ --56\n" +
                    "\t\t\t\t\t,ae_sle_fecha_hora_inicio --57\n" +
                    "\t\t\t\t\t,ae_sle_fecha_hora_fin --58\n" +
                    "\t\t\t\t\t,ae_sle_cant_minutos --59\n" +
                    "\t\t\t\t\t,ae_sle_labor_realizada_cdgo --60\n" +
                    "\t\t\t\t\t-- Labor Realizada\n" +
                    "\t\t\t\t\t\t\t,ae_sle_lr_cdgo --61\n" +
                    "\t\t\t\t\t\t\t,ae_sle_lr_desc --62\n" +
                    "\t\t\t\t\t\t\t,CASE ae_sle_lr_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_ao_estad --63\n" +
                    "\t\t\t\t\t,ae_sle_sle_motonave_cdgo --64\n" +
                    "\t\t\t\t\t--Motonave\n" +
                    "\t\t\t\t\t\t\t,ae_sle_mn_cdgo --65\n" +
                    "\t\t\t\t\t\t\t,ae_sle_mn_desc --66\n" +
                    "\t\t\t\t\t\t\t,CASE ae_sle_mn_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_mn_estad --67\n" +
                    "\t\t\t\t\t,ae_sle_cntro_cost_auxiliar_cdgo --68\n" +
                    "\t\t\t\t\t\t--Centro Costo Auxiliar\n" +
                    "\t\t\t\t\t\t,ae_sle_cca_cdgo --69\n" +
                    "\t\t\t\t\t\t,ae_sle_cca_cntro_cost_subcentro_cdgo --70\n" +
                    "\t\t\t\t\t\t\t-- SubCentro de Costo\n" +
                    "\t\t\t\t\t\t\t,ae_sle_ccs_cdgo --71\n" +
                    "\t\t\t\t\t\t\t,ae_sle_ccs_desc --72\n" +
                    "\t\t\t\t\t\t\t,CASE ae_sle_ccs_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_ccs_estad --73\n" +
                    "\t\t\t\t\t\t,ae_sle_cca_desc --74\n" +
                    "\t\t\t\t\t\t,CASE ae_sle_cca_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cca_estad --75\n" +
                    "\t\t\t\t\t,ae_sle_compania_cdgo --76\n" +
                    "\t\t\t\t\t\t--Compañia\n" +
                    "\t\t\t\t\t\t,ae_sle_cp_cdgo --77\n" +
                    "\t\t\t\t\t\t,ae_sle_cp_desc --78\n" +
                    "\t\t\t\t\t\t,CASE ae_sle_cp_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_sle_cp_estad --79\n" +
                    "\t\t\t\t,ae_fecha_registro --80\n" +
                    "\t\t\t\t,ae_fecha_hora_inicio --81\n" +
                    "\t\t\t\t,ae_fecha_hora_fin --82\n" +
                    "\t\t\t\t,ae_cant_minutos --83\n" +
                    "\t\t\t\t,ae_equipo_cdgo --84\n" +
                    "\t\t\t\t\t--Equipo\n" +
                    "\t\t\t\t\t,ae_eq_cdgo --85\n" +
                    "\t\t\t\t\t,ae_eq_tipo_equipo_cdgo --86\n" +
                    "\t\t\t\t\t\t--Tipo Equipo\n" +
                    "\t\t\t\t\t\t,ae_eq_te_cdgo --87\n" +
                    "\t\t\t\t\t\t,ae_eq_te_desc --88\n" +
                    "\t\t\t\t\t\t,CASE ae_eq_te_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_te_estad --89\n" +
                    "\t\t\t\t\t,ae_eq_codigo_barra --90\n" +
                    "\t\t\t\t\t,ae_eq_referencia --91\n" +
                    "\t\t\t\t\t,ae_eq_producto --92\n" +
                    "\t\t\t\t\t,ae_eq_capacidad --93\n" +
                    "\t\t\t\t\t,ae_eq_marca --94\n" +
                    "\t\t\t\t\t,ae_eq_modelo --95\n" +
                    "\t\t\t\t\t,ae_eq_serial --96\n" +
                    "\t\t\t\t\t,ae_eq_desc --97\n" +
                    "\t\t\t\t\t,ae_eq_clasificador1_cdgo --98\n" +
                    "\t\t\t\t\t\t-- Clasificador 1\n" +
                    "\t\t\t\t\t\t,ae_eq_ce1_cdgo --99\n" +
                    "\t\t\t\t\t\t,ae_eq_ce1_desc --100\n" +
                    "\t\t\t\t\t\t,CASE ae_eq_ce1_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce1_estad --101\n" +
                    "\t\t\t\t\t,ae_eq_clasificador2_cdgo --102\n" +
                    "\t\t\t\t\t\t-- Clasificador 2\n" +
                    "\t\t\t\t\t\t,ae_eq_ce2_cdgo --103\n" +
                    "\t\t\t\t\t\t,ae_eq_ce2_desc --104\n" +
                    "\t\t\t\t\t\t,CASE ae_eq_ce2_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ce2_estad --105\n" +
                    "\t\t\t\t\t,ae_eq_proveedor_equipo_cdgo --106\n" +
                    "\t\t\t\t\t\t--Proveedor Equipo\n" +
                    "\t\t\t\t\t\t,ae_eq_pe_cdgo --107\n" +
                    "\t\t\t\t\t\t,ae_eq_pe_nit --108\n" +
                    "\t\t\t\t\t\t,ae_eq_pe_desc --109\n" +
                    "\t\t\t\t\t\t,CASE ae_eq_pe_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_pe_estad --110\n" +
                    "\t\t\t\t\t,ae_eq_equipo_pertenencia_cdgo --111\n" +
                    "\t\t\t\t\t\t-- Equipo Pertenencia\n" +
                    "\t\t\t\t\t\t,ae_eq_ep_cdgo --112\n" +
                    "\t\t\t\t\t\t,ae_eq_ep_desc --113\n" +
                    "\t\t\t\t\t\t,CASE ae_eq_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_ep_estad --114\n" +
                    "\t\t\t\t\t,ae_eq_eq_observ --115\n" +
                    "\t\t\t\t\t,CASE ae_eq_eq_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_eq_eq_estad --116\n" +
                    "\t\t\t\t\t,ae_eq_actvo_fijo_id --117\n" +
                    "\t\t\t\t\t,ae_eq_actvo_fijo_referencia --118\n" +
                    "\t\t\t\t\t,ae_eq_actvo_fijo_desc --119\n" +
                    "\t\t\t\t,ae_equipo_pertenencia_cdgo --120\n" +
                    "\t\t\t\t-- Equipo Pertenencia\n" +
                    "\t\t\t\t\t\t,ae_ep_cdgo --121\n" +
                    "\t\t\t\t\t\t,ae_ep_desc --122\n" +
                    "\t\t\t\t\t\t,CASE ae_ep_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_ep_estad --123\n" +
                    "\t\t\t\t,ae_cant_hora_operativa --124\n" +
                    "\t\t\t\t,ae_cant_hora_parada --125\n" +
                    "\t\t\t\t,ae_cant_hora_total --126\n" +
                    "\t\t\t\t,CASE ae_estad WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS ae_estad --127\n" +
                    "\t\t\t,[me_fecha]   --128\n" +
                    "\t\t\t,[me_proveedor_equipo_cdgo]   --129\n" +
                    "\t\t\t\t--Proveedor Equipo\n" +
                    "\t\t\t\t,[pe_cdgo] AS me_pe_cdgo   --130\n" +
                    "\t\t\t\t,[pe_nit] AS me_pe_nit   --131\n" +
                    "\t\t\t\t,[pe_desc] AS me_pe_desc   --132\n" +
                    "\t\t\t\t,CASE [pe_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS me_pe_estad   --133\n" +
                    "\t\t\t,[me_num_orden]   --134\n" +
                    "\t\t\t,[me_cntro_cost_auxiliar_cdgo]   --135\n" +
                    "\t\t\t\t-- Centro Costo Auxiliar\n" +
                    "\t\t\t\t,me_cntro_cost_auxiliar.[cca_cdgo] AS me_cca_cdgo   --136\n" +
                    "\t\t\t\t,me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo] AS me_cca_cntro_cost_subcentro_cdgo   --137\n" +
                    "\t\t\t\t\t--Centro Costo Subcentro\n" +
                    "\t\t\t\t\t,me_cntro_cost_subcentro.[ccs_cdgo]   --138\n" +
                    "\t\t\t\t\t,me_cntro_cost_subcentro.[ccs_desc]   --139\n" +
                    "\t\t\t\t\t,CASE me_cntro_cost_subcentro.[ccs_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ccs_estad]   --140\n" +
                    "\t\t\t\t,me_cntro_cost_auxiliar.[cca_desc] AS me_cca_desc   --141\n" +
                    "\t\t\t\t,CASE me_cntro_cost_auxiliar.[cca_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cca_estad]   --142\n" +
                    "\t\t\t,[me_labor_realizada_cdgo]   --143\n" +
                    "\t\t\t\t--Labor Realizada\n" +
                    "\t\t\t\t,[lr_cdgo] --144\n" +
                    "\t\t\t\t,[lr_desc] --145\n" +
                    "\t\t\t\t,[lr_cntro_cost_subcentro_cdgo] --146\n" +
                    "\t\t\t\t,CASE [lr_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [lr_estad]   --147\n" +
                    "\t\t\t\t,[lr_operativa] --148\n" +
                    "\t\t\t\t,[lr_parada] --149\n" +
                    "\t\t\t,[me_cliente_cdgo]   --150\n" +
                    "\t\t\t\t--Cliente \n" +
                    "\t\t\t\t,me_cliente.[cl_cdgo]   --151\n" +
                    "\t\t\t\t,me_cliente.[cl_desc]   --152\n" +
                    "\t\t\t\t,CASE me_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad]   --153\n" +
                    "\t\t\t,[me_articulo_cdgo]   --154\n" +
                    "\t\t\t\t--articulo\n" +
                    "\t\t\t\t,[ar_cdgo]   --155\n" +
                    "\t\t\t\t,[ar_desc]   --156\n" +
                    "\t\t\t\t,CASE [ar_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ar_estad]   --157\n" +
                    "\t\t\t,[me_fecha_hora_inicio]   --158\n" +
                    "\t\t\t,[me_fecha_hora_fin]   --159\n" +
                    "\t\t\t,[me_total_minutos]   --160\n" +
                    "\t\t\t,[me_valor_hora]   --161\n" +
                    "\t\t\t,[me_costo_total]   --162\n" +
                    "\t\t\t,[me_recobro_cdgo]   --163\n" +
                    "\t\t\t\t--Recobro\n" +
                    "\t\t\t\t,[rc_cdgo]   --164\n" +
                    "\t\t\t\t,[rc_desc]   --165\n" +
                    "\t\t\t\t,CASE [rc_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [rc_estad]   --166\n" +
                    "\t\t\t,[me_cliente_recobro_cdgo]   --167\n" +
                    "\t\t\t\t--Cliente Recobro\n" +
                    "\t\t\t\t,[clr_cdgo]   --168\n" +
                    "\t\t\t\t,[clr_cliente_cdgo]   --169\n" +
                    "\t\t\t\t\t--Cliente\n" +
                    "\t\t\t\t\t,me_clr_cliente.[cl_cdgo]   --170   \n" +
                    "\t\t\t\t\t,me_clr_cliente.[cl_desc]   --171\n" +
                    "\t\t\t\t\t,CASE me_clr_cliente.[cl_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [cl_estad]   --172\n" +
                    "\t\t\t\t,[clr_usuario_cdgo]   --173\n" +
                    "\t\t\t\t\t--Usuario Quien Registra Recobro\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_cdgo]   --174\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_clave]   --175\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_nombres]   --176\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_apellidos]   --177\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_perfil_cdgo]   --178\n" +
                    "\t\t\t\t\t\t--Perfil Usuario Registra recobro\n" +
                    "\t\t\t\t\t\t,me_clr_us_perfil.[prf_cdgo]   --179\n" +
                    "\t\t\t\t\t\t,me_clr_us_perfil.[prf_desc]   --180\n" +
                    "\t\t\t\t\t\t,CASE me_clr_us_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad]   --181\n" +
                    "\t\t\t\t\t,me_clr_usuario.[us_correo]   --182\n" +
                    "\t\t\t\t\t,CASE me_clr_usuario.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]   --183\n" +
                    "\t\t\t\t,[clr_valor_recobro]   --184\n" +
                    "\t\t\t\t,[clr_fecha_registro]   --185\n" +
                    "\t\t\t,[me_costo_total_recobro_cliente]   --186\n" +
                    "\t\t\t,[me_usuario_registro_cdgo]   --187\n" +
                    "\t\t\t\t--Usuario Quien Registra Equipo\n" +
                    "\t\t\t\t,me_us_registro.[us_cdgo]   --188\n" +
                    "\t\t\t\t,me_us_registro.[us_clave]   --189\n" +
                    "\t\t\t\t,me_us_registro.[us_nombres]   --190\n" +
                    "\t\t\t\t,me_us_registro.[us_apellidos]   --191\n" +
                    "\t\t\t\t,me_us_registro.[us_perfil_cdgo]   --192\n" +
                    "\t\t\t\t\t--Perfil de Usuario Quien Registra Equipo\n" +
                    "\t\t\t\t\t,me_us_regist_perfil.[prf_cdgo]   --193\n" +
                    "\t\t\t\t\t,me_us_regist_perfil.[prf_desc]   --194\n" +
                    "\t\t\t\t\t,CASE me_us_regist_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad]   --195\n" +
                    "\t\t\t\t,me_us_registro.[us_correo]   --196\n" +
                    "\t\t\t\t,CASE me_us_registro.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]   --197\n" +
                    "\t\t\t,[me_usuario_autorizacion_cdgo]   --198\n" +
                    "\t\t\t\t--Usuario quien autoriza\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_cdgo]   --199\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_clave]   --200\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_nombres]   --201\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_apellidos]   --202\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_perfil_cdgo]   --203\n" +
                    "\t\t\t\t\t--Perfil quien autoriza\n" +
                    "\t\t\t\t\t,me_us_autoriza_perfil.[prf_cdgo]   --204\n" +
                    "\t\t\t\t\t,me_us_autoriza_perfil.[prf_desc]   --205\n" +
                    "\t\t\t\t\t,CASE me_us_autoriza_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad]   --206\n" +
                    "\t\t\t\t,me_us_autorizacion.[us_correo]   --207\n" +
                    "\t\t\t\t,CASE me_us_autorizacion.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]   --208\n" +
                    "\t\t\t,[me_autorizacion_recobro_cdgo]   --209\n" +
                    "\t\t\t\t,[are_cdgo]   --210\n" +
                    "\t\t\t\t,[are_desc]   --211\n" +
                    "\t\t\t\t,CASE [are_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [are_estad]   --212\n" +
                    "\t\t\t,[me_observ_autorizacion]   --213\n" +
                    "\t\t\t,[me_inactividad]   --214\n" +
                    "\t\t\t,[me_causa_inactividad_cdgo]   --215\n" +
                    "\t\t\t\t,[ci_cdgo]   --216\n" +
                    "\t\t\t\t,[ci_desc]   --217\n" +
                    "\t\t\t\t,CASE [ci_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [ci_estad]   --218\n" +
                    "\t\t\t,[me_usuario_inactividad_cdgo]   --219\n" +
                    "\t\t\t\t--Usuario de Inactividad\n" +
                    "\t\t\t\t,me_us_inactividad.[us_cdgo]   --220\n" +
                    "\t\t\t\t,me_us_inactividad.[us_clave]   --221\n" +
                    "\t\t\t\t,me_us_inactividad.[us_nombres]   --222\n" +
                    "\t\t\t\t,me_us_inactividad.[us_apellidos]   --223\n" +
                    "\t\t\t\t,me_us_inactividad.[us_perfil_cdgo]   --224\n" +
                    "\t\t\t\t\t,me_us_inactvdad_perfil.[prf_cdgo]   --225\n" +
                    "\t\t\t\t\t,me_us_inactvdad_perfil.[prf_desc]   --226\n" +
                    "\t\t\t\t\t,CASE me_us_inactvdad_perfil.[prf_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [prf_estad]   --227\n" +
                    "\t\t\t\t,me_us_inactividad.[us_correo]   --228\n" +
                    "\t\t\t\t,CASE me_us_inactividad.[us_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [us_estad]\t   --229\n" +
                    "\t\t\t,[me_motivo_parada_estado]   --230\n" +
                    "\t\t\t,[me_motivo_parada_cdgo]   --231\n" +
                    "\t\t\t\t--Motivo de Parada\n" +
                    "\t\t\t\t,[mpa_cdgo]   --232\n" +
                    "\t\t\t\t,[mpa_desc]   --233\n" +
                    "\t\t\t\t,[mpa_estad]   --234\n" +
                    "\t\t\t,[me_observ]   --235\n" +
                    "\t\t\t,CASE [me_estado] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [me_estado]\t   --236\n" +
                    "\t\t\t,CASE [me_desde_mvto_carbon] WHEN 1 THEN 'SI' WHEN 0 THEN 'NO' ELSE NULL END AS [me_desde_mvto_carbon]   --237\n" +
                    "   ,[co_cdgo] --238\n" +
                    "   ,[co_desc] --239\n" +
                    "  ,CASE [co_estad] WHEN 1 THEN 'ACTIVO' WHEN 0 THEN 'INACTIVO' ELSE NULL END AS [co_estad]	   --240\n" +
                    "   ,me_motonave.[mn_cdgo] --241\n" +
                    "   ,me_motonave.[mn_desc] --242\n" +
                    "   ,me_motonave.[mn_estad] --243\n" +
                    "\t FROM ["+DB+"].[dbo].[mvto_equipo]\n" +
                    "\tINNER JOIN (SELECT [ae_cdgo] AS ae_cdgo\n" +
                    "\t\t\t\t\t  ,[ae_cntro_oper_cdgo] AS ae_cntro_oper_cdgo\n" +
                    "\t\t\t\t\t\t\t--Centro Operacion\n" +
                    "\t\t\t\t\t\t\t,ae_cntro_oper.[co_cdgo] AS ae_co_cdgo\n" +
                    "\t\t\t\t\t\t\t,ae_cntro_oper.[co_desc] AS ae_co_desc\n" +
                    "\t\t\t\t\t\t\t,ae_cntro_oper.[co_estad] AS ae_co_estado\n" +
                    "\t\t\t\t\t  ,[ae_solicitud_listado_equipo_cdgo] AS ae_solicitud_listado_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t-- Solicitud Listado Equipo\n" +
                    "\t\t\t\t\t\t\t,[sle_cdgo] AS ae_sle_cdgo\n" +
                    "\t\t\t\t\t\t\t,[sle_solicitud_equipo_cdgo] AS ae_sle_solicitud_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t  --Solicitud Equipo\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_cdgo] AS ae_sle_se_cdgo\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_cntro_oper_cdgo] AS ae_sle_se_cntro_oper_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t--CentroOperación SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_cntro_oper.[co_cdgo] AS ae_sle_se_co_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_cntro_oper.[co_desc] AS ae_sle_se_co_desc\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_cntro_oper.[co_estad] AS ae_sle_se_co_estad\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_fecha] AS ae_sle_se_fecha\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_usuario_realiza_cdgo] AS ae_sle_se_usuario_realiza_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Usuario SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_cdgo] AS ae_sle_se_us_registra_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_clave] AS ae_sle_se_us_registra_clave\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_nombres] AS ae_sle_se_us_registra_nombres\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_apellidos] AS ae_sle_se_us_registra_apellidos\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_perfil_cdgo] AS ae_sle_se_us_registra_perfil_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t--Perfil Usuario Quien Registra Manual\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra.[prf_cdgo] AS ae_sle_se_prf_us_registra_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra.[prf_desc] AS ae_sle_se_prf_us_registra_desc\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra.[prf_estad] AS ae_sle_se_prf_us_registra_estad\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_correo] AS ae_sle_se_us_registra_correo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_realiza.[us_estad] AS ae_sle_se_us_registra_estad\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_fecha_registro] AS ae_sle_se_fecha_registro\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_estado_solicitud_equipo_cdgo] AS ae_sle_se_estado_solicitud_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Estado de la solicitud\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[ese_cdgo] AS ae_sle_se_ese_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[ese_desc] AS ae_sle_se_ese_desc\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[ese_estad] AS ae_sle_se_ese_estad\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_fecha_confirmacion] AS ae_sle_se_fecha_confirmacion\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_usuario_confirma_cdgo] AS ae_se_usuario_confirma_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Usuario SolicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_cdgo] AS ae_sle_se_us_confirma_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_clave] AS ae_sle_se_us_confirma_clave\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_nombres] AS ae_sle_se_us_confirma_nombres\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_apellidos] AS ae_sle_se_us_confirma_apellidos\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_perfil_cdgo] AS ae_sle_se_us_confirma_perfil_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t--Perfil Usuario Quien Registra Manual\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra_confirma.[prf_cdgo] AS ae_sle_se_prf_us_confirma_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra_confirma.[prf_desc] AS ae_sle_se_prf_us_confirma_desc\n" +
                    "\t\t\t\t\t\t\t\t\t\t\t,ae_prf_registra_confirma.[prf_estad] AS ae_sle_se_prf_us_confirma_estad\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_correo] AS ae_sle_se_us_confirma_correo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,se_usuario_confirma.[us_estad] AS ae_sle_se_us_confirma_estado\n" +
                    "\t\t\t\t\t\t\t\t  ,[se_confirmacion_solicitud_equipo_cdgo] AS ae_sle_se_confirmacion_solicitud_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t--Confirmacion solicitudEquipo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[cse_cdgo] AS ae_sle_se_cse_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[cse_desc] AS ae_sle_se_ces_desc\n" +
                    "\t\t\t\t\t\t\t\t\t\t,[cse_estad] AS ae_sle_se_ces_estado\n" +
                    "\t\t\t\t\t\t\t,[sle_tipo_equipo_cdgo] AS ae_sle_tipo_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t--Tipo de Equipo\n" +
                    "\t\t\t\t\t\t\t\t,sle_tipoEquipo.[te_cdgo] AS ae_sle_te_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,sle_tipoEquipo.[te_desc] AS ae_sle_te_desc\n" +
                    "\t\t\t\t\t\t\t\t,sle_tipoEquipo.[te_estad] AS ae_sle_te_estad\n" +
                    "\t\t\t\t\t\t\t,[sle_marca_equipo] AS ae_sle_marca_equipo\n" +
                    "\t\t\t\t\t\t\t,[sle_modelo_equipo] AS ae_sle_modelo_equipo\n" +
                    "\t\t\t\t\t\t\t,[sle_cant_equip] AS ae_sle_cant_equip\n" +
                    "\t\t\t\t\t\t\t,[sle_observ] AS ae_sle_observ\n" +
                    "\t\t\t\t\t\t\t,[sle_fecha_hora_inicio] AS ae_sle_fecha_hora_inicio\n" +
                    "\t\t\t\t\t\t\t,[sle_fecha_hora_fin] AS ae_sle_fecha_hora_fin\n" +
                    "\t\t\t\t\t\t\t,[sle_cant_minutos] AS   ae_sle_cant_minutos\n" +
                    "\t\t\t\t\t\t\t,[sle_labor_realizada_cdgo] AS ae_sle_labor_realizada_cdgo\n" +
                    "\t\t\t\t\t\t\t-- Labor Realizada\n" +
                    "\t\t\t\t\t\t\t\t  ,[lr_cdgo] AS ae_sle_lr_cdgo\n" +
                    "\t\t\t\t\t\t\t\t  ,[lr_desc] AS ae_sle_lr_desc\n" +
                    "\t\t\t\t\t\t\t\t  ,[lr_estad] AS ae_sle_lr_estad\n" +
                    "\t\t\t\t\t\t\t,[sle_motonave_cdgo] AS ae_sle_sle_motonave_cdgo\n" +
                    "\t\t\t\t\t\t\t--Motonave\n" +
                    "\t\t\t\t\t\t\t\t  ,sle_motonave.[mn_cdgo] AS ae_sle_mn_cdgo\n" +
                    "\t\t\t\t\t\t\t\t  ,sle_motonave.[mn_desc] AS ae_sle_mn_desc\n" +
                    "\t\t\t\t\t\t\t\t  ,sle_motonave.[mn_estad] AS ae_sle_mn_estad\n" +
                    "\t\t\t\t\t\t\t,[sle_cntro_cost_auxiliar_cdgo] AS ae_sle_cntro_cost_auxiliar_cdgo\n" +
                    "\t\t\t\t\t\t\t\t--Centro Costo Auxiliar\n" +
                    "\t\t\t\t\t\t\t\t,[cca_cdgo] AS ae_sle_cca_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,[cca_cntro_cost_subcentro_cdgo] AS ae_sle_cca_cntro_cost_subcentro_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t-- SubCentro de Costo\n" +
                    "\t\t\t\t\t\t\t\t\t,[ccs_cdgo] AS ae_sle_ccs_cdgo\n" +
                    "\t\t\t\t\t\t\t\t\t,[ccs_desc] AS ae_sle_ccs_desc\n" +
                    "\t\t\t\t\t\t\t\t\t,[ccs_estad] AS ae_sle_ccs_estad\n" +
                    "\t\t\t\t\t\t\t\t,[cca_desc] AS ae_sle_cca_desc\n" +
                    "\t\t\t\t\t\t\t\t,[cca_estad] AS ae_sle_cca_estad\n" +
                    "\t\t\t\t\t\t\t,[sle_compania_cdgo] AS ae_sle_compania_cdgo\n" +
                    "\t\t\t\t\t\t\t\t--Compañia\n" +
                    "\t\t\t\t\t\t\t\t,[cp_cdgo] AS ae_sle_cp_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,[cp_desc] AS ae_sle_cp_desc\n" +
                    "\t\t\t\t\t\t\t\t,[cp_estad] AS ae_sle_cp_estad\n" +
                    "\t\t\t\t\t  ,[ae_fecha_registro] AS ae_fecha_registro\n" +
                    "\t\t\t\t\t  ,[ae_fecha_hora_inicio] AS ae_fecha_hora_inicio\n" +
                    "\t\t\t\t\t  ,[ae_fecha_hora_fin] AS ae_fecha_hora_fin\n" +
                    "\t\t\t\t\t  ,[ae_cant_minutos] AS ae_cant_minutos\n" +
                    "\t\t\t\t\t  ,[ae_equipo_cdgo] AS ae_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t--Equipo\n" +
                    "\t\t\t\t\t\t\t,[eq_cdgo] AS ae_eq_cdgo\n" +
                    "\t\t\t\t\t\t\t,[eq_tipo_equipo_cdgo] AS ae_eq_tipo_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t--Tipo Equipo\n" +
                    "\t\t\t\t\t\t\t\t,eq_tipo_equipo.[te_cdgo] AS ae_eq_te_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,eq_tipo_equipo.[te_desc] AS ae_eq_te_desc\n" +
                    "\t\t\t\t\t\t\t\t,eq_tipo_equipo.[te_estad] AS ae_eq_te_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_codigo_barra] AS ae_eq_codigo_barra\n" +
                    "\t\t\t\t\t\t\t,[eq_referencia] AS ae_eq_referencia\n" +
                    "\t\t\t\t\t\t\t,[eq_producto] AS ae_eq_producto\n" +
                    "\t\t\t\t\t\t\t,[eq_capacidad] AS ae_eq_capacidad\n" +
                    "\t\t\t\t\t\t\t,[eq_marca] AS ae_eq_marca\n" +
                    "\t\t\t\t\t\t\t,[eq_modelo] AS ae_eq_modelo\n" +
                    "\t\t\t\t\t\t\t,[eq_serial] AS ae_eq_serial\n" +
                    "\t\t\t\t\t\t\t,[eq_desc] AS ae_eq_desc\n" +
                    "\t\t\t\t\t\t\t,[eq_clasificador1_cdgo] AS ae_eq_clasificador1_cdgo\n" +
                    "\t\t\t\t\t\t\t\t-- Clasificador 1\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador1.[ce_cdgo] AS ae_eq_ce1_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador1.[ce_desc] AS ae_eq_ce1_desc\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador1.[ce_estad] AS ae_eq_ce1_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_clasificador2_cdgo] AS ae_eq_clasificador2_cdgo\n" +
                    "\t\t\t\t\t\t\t\t-- Clasificador 2\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador2.[ce_cdgo] AS ae_eq_ce2_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador2.[ce_desc] AS ae_eq_ce2_desc\n" +
                    "\t\t\t\t\t\t\t\t,eq_clasificador2.[ce_estad] AS ae_eq_ce2_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_proveedor_equipo_cdgo] AS ae_eq_proveedor_equipo_cdgo\n" +
                    "\t\t\t\t\t\t\t\t--Proveedor Equipo\n" +
                    "\t\t\t\t\t\t\t\t,[pe_cdgo] AS ae_eq_pe_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,[pe_nit] AS ae_eq_pe_nit\n" +
                    "\t\t\t\t\t\t\t\t,[pe_desc] AS ae_eq_pe_desc\n" +
                    "\t\t\t\t\t\t\t\t,[pe_estad] AS ae_eq_pe_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_equipo_pertenencia_cdgo] AS ae_eq_equipo_pertenencia_cdgo\n" +
                    "\t\t\t\t\t\t\t\t-- Equipo Pertenencia\n" +
                    "\t\t\t\t\t\t\t\t,eq_pertenencia.[ep_cdgo] AS ae_eq_ep_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,eq_pertenencia.[ep_desc] AS ae_eq_ep_desc\n" +
                    "\t\t\t\t\t\t\t\t,eq_pertenencia.[ep_estad] AS ae_eq_ep_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_observ] AS ae_eq_eq_observ\n" +
                    "\t\t\t\t\t\t\t,[eq_estad] AS ae_eq_eq_estad\n" +
                    "\t\t\t\t\t\t\t,[eq_actvo_fijo_id] AS ae_eq_actvo_fijo_id\n" +
                    "\t\t\t\t\t\t\t,[eq_actvo_fijo_referencia] AS ae_eq_actvo_fijo_referencia\n" +
                    "\t\t\t\t\t\t\t,[eq_actvo_fijo_desc] AS ae_eq_actvo_fijo_desc\n" +
                    "\t\t\t\t\t  ,[ae_equipo_pertenencia_cdgo] AS ae_equipo_pertenencia_cdgo\n" +
                    "\t\t\t\t\t\t-- Equipo Pertenencia\n" +
                    "\t\t\t\t\t\t\t\t,ae_pertenencia.[ep_cdgo] AS ae_ep_cdgo\n" +
                    "\t\t\t\t\t\t\t\t,ae_pertenencia.[ep_desc] AS ae_ep_desc\n" +
                    "\t\t\t\t\t\t\t\t,ae_pertenencia.[ep_estad]\t AS ae_ep_estad\n" +
                    "\t\t\t\t\t  ,[ae_cant_minutos_operativo] AS ae_cant_hora_operativa\n" +
                    "\t\t\t\t\t  ,[ae_cant_minutos_parada] AS ae_cant_hora_parada\n" +
                    "\t\t\t\t\t  ,[ae_cant_minutos_total] AS ae_cant_hora_total\n" +
                    "\t\t\t\t\t  ,[ae_estad] AS ae_estad\n" +
                    "\t\t\t\t\t  FROM ["+DB+"].[dbo].[asignacion_equipo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[solicitud_listado_equipo] ON [ae_solicitud_listado_equipo_cdgo]=[sle_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[cntro_oper] ae_cntro_oper ON [ae_cntro_oper_cdgo]=ae_cntro_oper.[co_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[solicitud_equipo] ON [sle_solicitud_equipo_cdgo]=[se_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[cntro_oper] se_cntro_oper ON [se_cntro_oper_cdgo]=se_cntro_oper.[co_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[usuario] se_usuario_realiza ON [se_usuario_realiza_cdgo]=se_usuario_realiza.[us_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra ON se_usuario_realiza.[us_perfil_cdgo]=ae_prf_registra.[prf_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[estado_solicitud_equipo] ON [se_estado_solicitud_equipo_cdgo]=[ese_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN  ["+DB+"].[dbo].[usuario] se_usuario_confirma ON [se_usuario_realiza_cdgo]=se_usuario_confirma.[us_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB+"].[dbo].[perfil] ae_prf_registra_confirma ON se_usuario_confirma.[us_perfil_cdgo]=ae_prf_registra_confirma.[prf_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN  ["+DB+"].[dbo].[confirmacion_solicitud_equipo] ON [se_confirmacion_solicitud_equipo_cdgo]=[cse_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[tipo_equipo] sle_tipoEquipo ON [sle_tipo_equipo_cdgo]=sle_tipoEquipo.[te_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [sle_labor_realizada_cdgo]=[lr_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT  JOIN["+DB+"].[dbo].[motonave] sle_motonave ON [sle_motonave_cdgo]=sle_motonave.[mn_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] ON [sle_cntro_cost_auxiliar_cdgo]=[cca_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [cca_cntro_cost_subcentro_cdgo]=[ccs_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[compania] ON [sle_compania_cdgo]=[cp_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[equipo] ON [ae_equipo_cdgo]=[eq_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[tipo_equipo] eq_tipo_equipo ON [eq_tipo_equipo_cdgo]=eq_tipo_equipo.[te_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador1 ON [eq_clasificador1_cdgo]=eq_clasificador1.[ce_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB+"].[dbo].[clasificador_equipo] eq_clasificador2 ON [eq_clasificador2_cdgo]=eq_clasificador2.[ce_cdgo]\n" +
                    "\t\t\t\t\t\t\tINNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [eq_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] eq_pertenencia ON [eq_equipo_pertenencia_cdgo]=eq_pertenencia.[ep_cdgo]\n" +
                    "\t\t\t\t\t\t\tLEFT JOIN ["+DB+"].[dbo].[equipo_pertenencia] ae_pertenencia ON [ae_equipo_pertenencia_cdgo]=ae_pertenencia.[ep_cdgo]\t\n" +
                    "\t) asignacion_equipo ON [me_asignacion_equipo_cdgo]=[ae_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[proveedor_equipo] ON [me_proveedor_equipo_cdgo]=[pe_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_oper] ON [me_cntro_oper_cdgo]=[co_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_auxiliar] me_cntro_cost_auxiliar ON [me_cntro_cost_auxiliar_cdgo]=me_cntro_cost_auxiliar.[cca_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro]  me_cntro_cost_subcentro ON me_cntro_cost_auxiliar.[cca_cntro_cost_subcentro_cdgo]=me_cntro_cost_subcentro.[ccs_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[labor_realizada] ON [me_labor_realizada_cdgo]=[lr_cdgo] \n" +
                    "\tLEFT JOIN ["+DB+"].[dbo].[cliente] me_cliente ON [me_cliente_cdgo]=me_cliente.[cl_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[articulo] ON [me_articulo_cdgo]=[ar_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[motonave] me_motonave ON [me_motonave_cdgo]=me_motonave.[mn_cdgo]\n" +
                    "\tINNER JOIN ["+DB+"].[dbo].[recobro] ON [me_recobro_cdgo]=[rc_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[cliente_recobro] ON [me_cliente_recobro_cdgo]=[clr_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[cliente] me_clr_cliente ON [clr_cliente_cdgo]=me_clr_cliente.[cl_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[usuario] me_clr_usuario ON [clr_usuario_cdgo]=me_clr_usuario.[us_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[perfil] me_clr_us_perfil ON me_clr_usuario.[us_perfil_cdgo]=me_clr_us_perfil.[prf_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_registro ON [me_usuario_registro_cdgo]=me_us_registro.[us_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_regist_perfil ON me_us_registro.[us_perfil_cdgo]=me_us_regist_perfil.[prf_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_autorizacion ON [me_usuario_autorizacion_cdgo]=me_us_autorizacion.[us_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_autoriza_perfil ON me_us_autorizacion.[us_perfil_cdgo]=me_us_autoriza_perfil.[prf_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[autorizacion_recobro] ON [me_autorizacion_recobro_cdgo]=[are_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[causa_inactividad] ON [me_causa_inactividad_cdgo]=[ci_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[usuario] me_us_inactividad ON [me_usuario_inactividad_cdgo]=me_us_inactividad.[us_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[perfil] me_us_inactvdad_perfil ON me_us_inactividad.[us_perfil_cdgo]=me_us_inactvdad_perfil.[prf_cdgo]\n" +
                    "\tLEFT JOIN  ["+DB+"].[dbo].[motivo_parada] ON [me_motivo_parada_cdgo]=[mpa_cdgo]\n" +
                    "\n" +
                    "\tWHERE  [me_inactividad]=0 AND [me_desde_mvto_carbon]=0 AND ae_equipo_cdgo =? AND [me_fecha_hora_fin] IS NULL ORDER BY [me_cdgo] DESC");
            query.setString(1, equipoI.getCodigo());
            //query.setString(2, DatetimeFin);
            ResultSet resultSet; resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator=false;
                }
                //MvtoCarbon_ListadoEquipos mvto_listEquipo = new MvtoCarbon_ListadoEquipos();
                //mvto_listEquipo.setCodigo(resultSet.getString(1));
                //MvtoCarbon mvtoCarbon = new MvtoCarbon();
                //mvtoCarbon.setCodigo(resultSet.getString(3));
                // mvtoCarbon.setCentroOperacion(new CentroOperacion(Integer.parseInt(resultSet.getString(5)),resultSet.getString(6),resultSet.getString(7)));
                //mvtoCarbon.setCentroCostoAuxiliar(new CentroCostoAuxiliar(Integer.parseInt(resultSet.getString(9)),new CentroCostoSubCentro(Integer.parseInt(resultSet.getString(11)),resultSet.getString(12),resultSet.getString(13)),resultSet.getString(14),resultSet.getString(15)));
                //mvtoCarbon.setArticulo(new Articulo(resultSet.getString(17),resultSet.getString(18),resultSet.getString(19)));
                //mvtoCarbon.setCliente(new Cliente(resultSet.getString(21),resultSet.getString(22),resultSet.getString(23)));
                //mvtoCarbon.setTransportadora(new Transportadora(resultSet.getString(25),resultSet.getString(26),resultSet.getString(27),resultSet.getString(28),resultSet.getString(29)));
                //mvtoCarbon.setFechaRegistro(resultSet.getString(30));
                // mvtoCarbon.setNumero_orden(resultSet.getString(31));
                //mvtoCarbon.setDeposito(resultSet.getString(32));
                //mvtoCarbon.setConsecutivo(resultSet.getString(33));
                //mvtoCarbon.setPlaca(resultSet.getString(34));
                //mvtoCarbon.setPesoVacio(resultSet.getString(35));
                //mvtoCarbon.setPesoLleno(resultSet.getString(36));
                //mvtoCarbon.setPesoNeto(resultSet.getString(37));
                //mvtoCarbon.setFechaEntradaVehiculo(resultSet.getString(38));
                //mvtoCarbon.setFecha_SalidaVehiculo(resultSet.getString(39));
                //mvtoCarbon.setFechaInicioDescargue(resultSet.getString(40));
                // mvtoCarbon.setFechaFinDescargue(resultSet.getString(41));
                //Usuario us = new Usuario();
                //us.setCodigo(resultSet.getString(43));
                //us.setClave(resultSet.getString(44));
                //us.setNombres(resultSet.getString(45));
                //us.setApellidos(resultSet.getString(46));
                //us.setPerfilUsuario(new Perfil(resultSet.getString(48),resultSet.getString(49),resultSet.getString(50)));
                //us.setCorreo(resultSet.getString(51));
                //us.setEstado(resultSet.getString(52));
                //mvtoCarbon.setUsuarioRegistroMovil(us);
                // mvtoCarbon.setObservacion(resultSet.getString(53));
                //EstadoMvtoCarbon estadMvtoCarbon = new EstadoMvtoCarbon();
                //estadMvtoCarbon.setCodigo(resultSet.getString(55));
                //estadMvtoCarbon.setDescripcion(resultSet.getString(56));
                //estadMvtoCarbon.setEstado(resultSet.getString(57));
                //mvtoCarbon.setEstadoMvtoCarbon(estadMvtoCarbon);
                //mvtoCarbon.setConexionPesoCcarga(resultSet.getString(58));
                //mvtoCarbon.setRegistroManual(resultSet.getString(59));
                //Usuario usRegManual = new Usuario();
                //usRegManual.setCodigo(resultSet.getString(61));
                //usRegManual.setClave(resultSet.getString(62));
                //usRegManual.setNombres(resultSet.getString(63));
                //usRegManual.setApellidos(resultSet.getString(64));
                //usRegManual.setPerfilUsuario(new Perfil(resultSet.getString(66),resultSet.getString(67),resultSet.getString(68)));
                //usRegManual.setCorreo(resultSet.getString(69));
                //usRegManual.setEstado(resultSet.getString(70));
                //mvtoCarbon.setUsuarioRegistraManual(usRegManual);
                //mvtoCarbon.setCantidadHorasDescargue(resultSet.getString(303));
                //mvto_listEquipo.setMvtoCarbon(mvtoCarbon);
                AsignacionEquipo asignacionEquipo = new AsignacionEquipo();
                asignacionEquipo.setCodigo(resultSet.getString(4));
                CentroOperacion co= new CentroOperacion();
                co.setCodigo(Integer.parseInt(resultSet.getString(5)));
                co.setDescripcion(resultSet.getString(6));
                co.setEstado(resultSet.getString(7));
                asignacionEquipo.setCentroOperacion(co);
                SolicitudListadoEquipo solicitudListadoEquipo = new SolicitudListadoEquipo();
                solicitudListadoEquipo.setCodigo(resultSet.getString(9));
                SolicitudEquipo solicitudEquipo= new SolicitudEquipo();
                solicitudEquipo.setCodigo(resultSet.getString(10));
                CentroOperacion co_se= new CentroOperacion();
                co_se.setCodigo(Integer.parseInt(resultSet.getString(13)));
                co_se.setDescripcion(resultSet.getString(14));
                co_se.setEstado(resultSet.getString(15));
                solicitudEquipo.setCentroOperacion(co_se);
                solicitudEquipo.setFechaSolicitud(resultSet.getString(16));
                Usuario us_se = new Usuario();
                us_se.setCodigo(resultSet.getString(18));
                //us_se.setClave(resultSet.getString(19));
                us_se.setNombres(resultSet.getString(20));
                us_se.setApellidos(resultSet.getString(21));
                us_se.setPerfilUsuario(new Perfil(resultSet.getString(23),resultSet.getString(24),resultSet.getString(25)));
                us_se.setCorreo(resultSet.getString(26));
                us_se.setEstado(resultSet.getString(27));
                solicitudEquipo.setUsuarioRealizaSolicitud(us_se);
                solicitudEquipo.setFechaRegistro(resultSet.getString(28));
                EstadoSolicitudEquipos estadoSolicitudEquipos = new EstadoSolicitudEquipos();
                estadoSolicitudEquipos.setCodigo(resultSet.getString(30));
                estadoSolicitudEquipos.setDescripcion(resultSet.getString(31));
                estadoSolicitudEquipos.setEstado(resultSet.getString(32));
                solicitudEquipo.setEstadoSolicitudEquipo(estadoSolicitudEquipos);
                solicitudEquipo.setFechaConfirmacion(resultSet.getString(33));
                Usuario us_se_confirm = new Usuario();
                us_se_confirm.setCodigo(resultSet.getString(35));
                //us_se_confirm.setClave(resultSet.getString(105));
                us_se_confirm.setNombres(resultSet.getString(37));
                us_se_confirm.setApellidos(resultSet.getString(38));
                us_se_confirm.setPerfilUsuario(new Perfil(resultSet.getString(40),resultSet.getString(41),resultSet.getString(42)));
                us_se_confirm.setCorreo(resultSet.getString(43));
                us_se_confirm.setEstado(resultSet.getString(44));
                solicitudEquipo.setUsuarioConfirmacionSolicitud(us_se_confirm);
                ConfirmacionSolicitudEquipos confirmacionSolicitudEquipos = new ConfirmacionSolicitudEquipos();
                confirmacionSolicitudEquipos.setCodigo(resultSet.getString(46));
                confirmacionSolicitudEquipos.setDescripcion(resultSet.getString(47));
                confirmacionSolicitudEquipos.setEstado(resultSet.getString(48));
                solicitudEquipo.setConfirmacionSolicitudEquipo(confirmacionSolicitudEquipos);
                solicitudListadoEquipo.setSolicitudEquipo(solicitudEquipo);
                TipoEquipo tipoEquipo = new TipoEquipo();
                tipoEquipo.setCodigo(resultSet.getString(50));
                tipoEquipo.setDescripcion(resultSet.getString(51));
                tipoEquipo.setEstado(resultSet.getString(52));
                solicitudListadoEquipo.setTipoEquipo(tipoEquipo);
                solicitudListadoEquipo.setMarcaEquipo(resultSet.getString(53));
                solicitudListadoEquipo.setModeloEquipo(resultSet.getString(54));
                solicitudListadoEquipo.setCantidad(Integer.parseInt(resultSet.getString(55)));
                solicitudListadoEquipo.setObservacacion(resultSet.getString(56));
                solicitudListadoEquipo.setFechaHoraInicio(resultSet.getString(57));
                solicitudListadoEquipo.setFechaHoraFin(resultSet.getString(58));
                solicitudListadoEquipo.setCantidadMinutos(resultSet.getInt(59));
                LaborRealizada laborRealizada = new LaborRealizada();
                laborRealizada.setCodigo(resultSet.getString(61));
                laborRealizada.setDescripcion(resultSet.getString(62));
                laborRealizada.setEstado(resultSet.getString(63));
                solicitudListadoEquipo.setLaborRealizada(laborRealizada);
                Motonave motonave = new Motonave();
                motonave.setCodigo(resultSet.getString(65));
                motonave.setDescripcion(resultSet.getString(66));
                motonave.setEstado(resultSet.getString(67));
                solicitudListadoEquipo.setMotonave(motonave);
                CentroCostoSubCentro centroCostoSubCentro = new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(71));
                centroCostoSubCentro.setDescripcion(resultSet.getString(72));
                centroCostoSubCentro.setEstado(resultSet.getString(73));
                CentroCostoAuxiliar centroCostoAuxiliar = new CentroCostoAuxiliar();
                centroCostoAuxiliar.setCodigo(resultSet.getString(69));
                centroCostoAuxiliar.setDescripcion(resultSet.getString(74));
                centroCostoAuxiliar.setEstado(resultSet.getString(75));
                centroCostoAuxiliar.setCentroCostoSubCentro(centroCostoSubCentro);
                solicitudListadoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar);
                Compañia compania = new Compañia();
                compania.setCodigo(resultSet.getString(77));
                compania.setDescripcion(resultSet.getString(78));
                compania.setEstado(resultSet.getString(79));
                solicitudListadoEquipo.setCompañia(compania);
                asignacionEquipo.setSolicitudListadoEquipo(solicitudListadoEquipo);
                asignacionEquipo.setFechaRegistro(resultSet.getString(80));
                asignacionEquipo.setFechaHoraInicio(resultSet.getString(81));
                asignacionEquipo.setFechaHoraFin(resultSet.getString(82));
                asignacionEquipo.setCantidadMinutosProgramados(resultSet.getString(83));
                Equipo equipo = new Equipo();
                equipo.setCodigo(resultSet.getString(85));
                equipo.setTipoEquipo(new TipoEquipo(resultSet.getString(87),resultSet.getString(88),resultSet.getString(89)));
                equipo.setCodigo_barra(resultSet.getString(90));
                equipo.setReferencia(resultSet.getString(91));
                equipo.setProducto(resultSet.getString(92));
                equipo.setCapacidad(resultSet.getString(93));
                equipo.setMarca(resultSet.getString(94));
                equipo.setModelo(resultSet.getString(95));
                equipo.setSerial(resultSet.getString(96));
                equipo.setDescripcion(resultSet.getString(97));
                equipo.setClasificador1(new ClasificadorEquipo(resultSet.getString(99),resultSet.getString(100),resultSet.getString(101)));
                equipo.setClasificador2(new ClasificadorEquipo(resultSet.getString(103),resultSet.getString(104),resultSet.getString(105)));
                equipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(107),resultSet.getString(108),resultSet.getString(109),resultSet.getString(110)));
                equipo.setPertenenciaEquipo(new Pertenencia(resultSet.getString(112),resultSet.getString(113),resultSet.getString(114)));
                equipo.setObservacion(resultSet.getString(115));
                equipo.setEstado(resultSet.getString(116));
                equipo.setActivoFijo_codigo(resultSet.getString(117));
                equipo.setActivoFijo_referencia(resultSet.getString(118));
                equipo.setActivoFijo_descripcion(resultSet.getString(119));
                asignacionEquipo.setEquipo(equipo);
                asignacionEquipo.setPertenencia(new Pertenencia(resultSet.getString(121),resultSet.getString(122),resultSet.getString(123)));
                asignacionEquipo.setCantidadMinutosOperativo(resultSet.getString(124));
                asignacionEquipo.setCantidadMinutosParada(resultSet.getString(125));
                asignacionEquipo.setCantidadMinutosTotal(resultSet.getString(126));
                asignacionEquipo.setEstado(resultSet.getString(127));
                //mvto_listEquipo.setAsignacionEquipo(asignacionEquipo);
                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                mvtoEquipo.setCodigo(resultSet.getString(1));
                mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                mvtoEquipo.setFechaRegistro(resultSet.getString(128));
                mvtoEquipo.setProveedorEquipo(new ProveedorEquipo(resultSet.getString(130),resultSet.getString(131),resultSet.getString(132),resultSet.getString(133)));
                mvtoEquipo.setNumeroOrden(resultSet.getString(134));
                CentroOperacion me_co= new CentroOperacion();
                me_co.setCodigo(Integer.parseInt(resultSet.getString(238)));
                me_co.setDescripcion(resultSet.getString(239));
                me_co.setEstado(resultSet.getString(240));
                mvtoEquipo.setCentroOperacion(me_co);
                CentroCostoSubCentro centroCostoSubCentro_mvtoEquipo = new CentroCostoSubCentro();
                centroCostoSubCentro_mvtoEquipo.setCodigo(resultSet.getInt(138));
                centroCostoSubCentro_mvtoEquipo.setDescripcion(resultSet.getString(139));
                centroCostoSubCentro_mvtoEquipo.setEstado(resultSet.getString(140));
                CentroCostoAuxiliar centroCostoAuxiliar_mvtoEquipo = new CentroCostoAuxiliar();
                centroCostoAuxiliar_mvtoEquipo.setCodigo(resultSet.getString(136));
                centroCostoAuxiliar_mvtoEquipo.setDescripcion(resultSet.getString(141));
                centroCostoAuxiliar_mvtoEquipo.setEstado(resultSet.getString(142));
                centroCostoAuxiliar_mvtoEquipo.setCentroCostoSubCentro(centroCostoSubCentro_mvtoEquipo);
                mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_mvtoEquipo);
                LaborRealizada laborRealizadaT = new LaborRealizada();
                laborRealizadaT.setCodigo(resultSet.getString(144));
                laborRealizadaT.setDescripcion(resultSet.getString(145));
                laborRealizadaT.setEstado(resultSet.getString(147));
                laborRealizadaT.setEs_operativa(resultSet.getString(148));
                laborRealizadaT.setEs_parada(resultSet.getString(149));
                mvtoEquipo.setLaborRealizada(laborRealizadaT);
                mvtoEquipo.setCliente(new Cliente(resultSet.getString(151),resultSet.getString(152),resultSet.getString(153)));
                mvtoEquipo.setArticulo(new Articulo(resultSet.getString(155),resultSet.getString(156),resultSet.getString(157)));
                Motonave me_motonave = new Motonave();
                me_motonave.setCodigo(resultSet.getString(241));
                me_motonave.setDescripcion(resultSet.getString(242));
                me_motonave.setEstado(resultSet.getString(243));
                mvtoEquipo.setMotonave(me_motonave);
                mvtoEquipo.setFechaHoraInicio(resultSet.getString(158));
                mvtoEquipo.setFechaHoraFin(resultSet.getString(159));
                mvtoEquipo.setTotalMinutos(resultSet.getString(160));
                mvtoEquipo.setValorHora(resultSet.getString(161));
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(186));
                Recobro recobro = new Recobro();
                recobro.setCodigo(resultSet.getString(164));
                recobro.setDescripcion(resultSet.getString(165));
                recobro.setEstado(resultSet.getString(166));
                mvtoEquipo.setRecobro(recobro);
                ClienteRecobro ClienteRecobro = new ClienteRecobro();
                ClienteRecobro.setCodigo(resultSet.getString(168));
                Cliente cliente_recobro = new Cliente();
                cliente_recobro.setCodigo(resultSet.getString(170));
                cliente_recobro.setDescripcion(resultSet.getString(171));
                cliente_recobro.setEstado(resultSet.getString(172));
                ClienteRecobro.setCliente(cliente_recobro);
                Usuario usuario_recobre = new Usuario();
                usuario_recobre.setCodigo(resultSet.getString(174));
                //usuario_recobre.setClave(resultSet.getString(244));
                usuario_recobre.setNombres(resultSet.getString(176));
                usuario_recobre.setApellidos(resultSet.getString(177));
                usuario_recobre.setPerfilUsuario(new Perfil(resultSet.getString(179),resultSet.getString(180),resultSet.getString(181)));
                usuario_recobre.setCorreo(resultSet.getString(182));
                usuario_recobre.setEstado(resultSet.getString(183));
                ClienteRecobro.setUsuario(usuario_recobre);
                ClienteRecobro.setValorRecobro(resultSet.getString(184));
                ClienteRecobro.setFechaRegistro(resultSet.getString(185));
                mvtoEquipo.setClienteRecobro(ClienteRecobro);
                mvtoEquipo.setCostoTotalRecobroCliente(resultSet.getString(186));
                Usuario usuario_me_registra = new Usuario();
                usuario_me_registra.setCodigo(resultSet.getString(188));
                //usuario_me_registra.setClave(resultSet.getString(258));
                usuario_me_registra.setNombres(resultSet.getString(190));
                usuario_me_registra.setApellidos(resultSet.getString(191));
                usuario_me_registra.setPerfilUsuario(new Perfil(resultSet.getString(193),resultSet.getString(194),resultSet.getString(195)));
                usuario_me_registra.setCorreo(resultSet.getString(196));
                usuario_me_registra.setEstado(resultSet.getString(197));
                mvtoEquipo.setUsuarioQuieRegistra(usuario_me_registra);
                Usuario usuario_me_autoriza = new Usuario();
                usuario_me_autoriza.setCodigo(resultSet.getString(199));
                //usuario_me_autoriza.setClave(resultSet.getString(269));
                usuario_me_autoriza.setNombres(resultSet.getString(201));
                usuario_me_autoriza.setApellidos(resultSet.getString(202));
                usuario_me_autoriza.setPerfilUsuario(new Perfil(resultSet.getString(204),resultSet.getString(205),resultSet.getString(206)));
                usuario_me_autoriza.setCorreo(resultSet.getString(207));
                usuario_me_autoriza.setEstado(resultSet.getString(208));
                mvtoEquipo.setUsuarioAutorizaRecobro(usuario_me_autoriza);
                AutorizacionRecobro autorizacionRecobro = new AutorizacionRecobro();
                autorizacionRecobro.setCodigo(resultSet.getString(210));
                autorizacionRecobro.setDescripcion(resultSet.getString(211));
                autorizacionRecobro.setEstado(resultSet.getString(212));
                mvtoEquipo.setAutorizacionRecobro(autorizacionRecobro);
                mvtoEquipo.setObservacionAutorizacion(resultSet.getString(213));
                mvtoEquipo.setInactividad(resultSet.getString(214));
                CausaInactividad causaInactividad = new CausaInactividad();
                causaInactividad.setCodigo(resultSet.getString(216));
                causaInactividad.setDescripcion(resultSet.getString(217));
                causaInactividad.setEstado(resultSet.getString(218));
                mvtoEquipo.setCausaInactividad(causaInactividad);
                Usuario usuario_me_us_inactividad = new Usuario();
                usuario_me_us_inactividad.setCodigo(resultSet.getString(220));
                //usuario_me_us_inactividad.setClave(resultSet.getString(221));
                usuario_me_us_inactividad.setNombres(resultSet.getString(222));
                usuario_me_us_inactividad.setApellidos(resultSet.getString(223));
                usuario_me_us_inactividad.setPerfilUsuario(new Perfil(resultSet.getString(225),resultSet.getString(226),resultSet.getString(227)));
                usuario_me_us_inactividad.setCorreo(resultSet.getString(228));
                usuario_me_us_inactividad.setEstado(resultSet.getString(229));
                mvtoEquipo.setMotivoParadaEstado(resultSet.getString(230));
                mvtoEquipo.setUsuarioInactividad(usuario_me_us_inactividad);
                MotivoParada motivoParada= new MotivoParada();
                motivoParada.setCodigo(resultSet.getString(232));
                motivoParada.setDescripcion(resultSet.getString(233));
                motivoParada.setEstado(resultSet.getString(234));
                mvtoEquipo.setMotivoParada(motivoParada);
                mvtoEquipo.setObservacionMvtoEquipo(resultSet.getString(235));
                mvtoEquipo.setEstado(resultSet.getString(236));
                mvtoEquipo.setDesdeCarbon(resultSet.getString(237));
                //mvtoEquipo.setTotalMinutos(resultSet.getString(307));
                listadoObjetos.add(mvtoEquipo);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los Movimientos de Carbon");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }*/

}
