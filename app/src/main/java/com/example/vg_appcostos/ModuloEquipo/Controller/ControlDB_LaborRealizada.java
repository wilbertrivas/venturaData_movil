package com.example.vg_appcostos.ModuloEquipo.Controller;

import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Config;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ControlDB_LaborRealizada {
    private Connection conexion = null;
    private String tipoConexion;

    public ControlDB_LaborRealizada(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }
    public int registrar(LaborRealizada Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException {
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{

            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            conexion= control.ConectarBaseDatos();
            if(validarExistenciaActualizar(Objeto)){
                result=3;
            }else{
                conexion= control.ConectarBaseDatos();
                PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[labor_realizada] ([lr_cdgo],[lr_desc],[lr_cntro_cost_subcentro_cdgo],[lr_estad],[lr_operativa],[lr_parada],[lr_bodegaDestino]) VALUES ((SELECT (CASE WHEN (MAX([lr_cdgo]) IS NULL)\n" +
                        " THEN 1\n" +
                        " ELSE (MAX([lr_cdgo])+1) END)AS [lr_cdgo]\n" +
                        " FROM ["+DB+"].[dbo].[labor_realizada]),?,?,?,?,?,?);");
                Query.setString(1, Objeto.getDescripcion());
                Query.setInt(2,    Objeto.getCentroCostoSubCentro().getCodigo());
                Query.setString(3, Objeto.getEstado());
                Query.setString(4, Objeto.getEs_operativa());
                Query.setString(5, Objeto.getEs_parada());
                Query.setString(6, Objeto.getBodegaDestino());
                Query.execute();
                result=1;
                if(result==1){
                    result=0;
                    //Sacamos el ultimo valor
                    PreparedStatement queryMaximo= conexion.prepareStatement("SELECT MAX(lr_cdgo) FROM ["+DB+"].[dbo].[labor_realizada];");
                    ResultSet resultSetMaximo= queryMaximo.executeQuery();
                    while(resultSetMaximo.next()){
                        if(resultSetMaximo.getString(1) != null){
                            Objeto.setCodigo(resultSetMaximo.getString(1));
                        }
                    }
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
                            "           ,'LABOR REALIZADA'" +
                            "           ,CONCAT (?,?,' Nombre: ',?,'Subcentro Costos ',?,' Estado: ',?,' Operativa: ',?,'Parada:',?, ' Bodega_Destino:',?));");
                    Query_Auditoria.setString(1, us.getCodigo());
                    Query_Auditoria.setString(2, namePc);
                    Query_Auditoria.setString(3, ipPc);
                    Query_Auditoria.setString(4, macPC);
                    Query_Auditoria.setString(5, Objeto.getCodigo());
                    Query_Auditoria.setString(6, "Se registró una nueva actividad de operación en el sistema, con Código: ");
                    Query_Auditoria.setString(7, Objeto.getCodigo());
                    Query_Auditoria.setString(8, Objeto.getDescripcion());
                    Query_Auditoria.setString(9, Objeto.getCentroCostoSubCentro().getDescripcion());
                    Query_Auditoria.setString(10, estado);
                    Query_Auditoria.setString(11, Objeto.getEs_operativa());
                    Query_Auditoria.setString(12, Objeto.getEs_parada());
                    Query_Auditoria.setString(13, Objeto.getBodegaDestino());
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
    public ArrayList<LaborRealizada> buscar(String valorConsulta) throws SQLException{
        ArrayList<LaborRealizada> listadoObjetos = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            ResultSet resultSet;
            if(valorConsulta.equals("")){
                PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                        "      ,[lr_desc]\n" +
                        "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                        "		  ,[ccs_cdgo]\n" +
                        "		  ,[ccs_desc]\n" +
                        "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                        "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                        "	  ,[lr_operativa]\n" +
                        "	  ,[lr_parada]" +
                        ",[lr_bodegaDestino] \n" +
                        "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo];");
                resultSet= query.executeQuery();
            }else{
                PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                        "      ,[lr_desc]\n" +
                        "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                        "		  ,[ccs_cdgo]\n" +
                        "		  ,[ccs_desc]\n" +
                        "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                        "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                        "	  ,[lr_operativa]\n" +
                        "	  ,[lr_parada],[lr_bodegaDestino] \n" +
                        "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                        "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_desc] like ?;");
                query.setString(1, "%"+valorConsulta+"%");
                resultSet= query.executeQuery();
            }
            while(resultSet.next()){
                LaborRealizada Objeto = new LaborRealizada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(7));
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                centroCostoSubCentro.setEstado(resultSet.getString(6));
                Objeto.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setEs_operativa(resultSet.getString(8));
                Objeto.setEs_parada(resultSet.getString(9));
                Objeto.setBodegaDestino(resultSet.getString(10));
                Objeto.setCentroCostoSubCentro(centroCostoSubCentro);
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public ArrayList<LaborRealizada> buscarPorSubcentroCosto(CentroCostoSubCentro IcentroCostoSubCentro) throws SQLException{
        ArrayList<LaborRealizada> listadoObjetos = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            ResultSet resultSet;

            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino]\n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_cntro_cost_subcentro_cdgo] = ? AND [lr_estad]=1;");
            query.setInt(1, IcentroCostoSubCentro.getCodigo());
            resultSet= query.executeQuery();

            while(resultSet.next()){
                LaborRealizada Objeto = new LaborRealizada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(7));
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                centroCostoSubCentro.setEstado(resultSet.getString(6));
                Objeto.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setEs_operativa(resultSet.getString(8));
                Objeto.setEs_parada(resultSet.getString(9));
                Objeto.setBodegaDestino(resultSet.getString(10));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public LaborRealizada buscarEspecifico(String codigo) throws SQLException{
        LaborRealizada Objeto =null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino] \n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_cdgo] = ?;");
            query.setString(1, codigo);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                Objeto = new LaborRealizada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(7));
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                centroCostoSubCentro.setEstado(resultSet.getString(6));
                Objeto.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setEs_operativa(resultSet.getString(8));
                Objeto.setEs_parada(resultSet.getString(9));
                Objeto.setBodegaDestino(resultSet.getString(10));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<LaborRealizada> buscarActivos() throws SQLException{
        ArrayList<LaborRealizada> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino] \n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_estad]=1;");
            ResultSet resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator= false;
                }
                LaborRealizada Objeto = new LaborRealizada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(7));
                CentroCostoSubCentro centroCostoSubCentro= new CentroCostoSubCentro();
                centroCostoSubCentro.setCodigo(resultSet.getInt(4));
                centroCostoSubCentro.setDescripcion(resultSet.getString(5));
                centroCostoSubCentro.setEstado(resultSet.getString(6));
                Objeto.setCentroCostoSubCentro(centroCostoSubCentro);
                Objeto.setEs_operativa(resultSet.getString(8));
                Objeto.setEs_parada(resultSet.getString(9));
                Objeto.setBodegaDestino(resultSet.getString(10));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public String buscar_nombre(String nombre) throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino] \n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_desc] like ?;");
            query.setString(1, nombre);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                return resultSet.getString(1);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return "";
    }
    public String buscar_Id(String id) throws SQLException{
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino]\n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_cdgo] =?;");
            query.setString(1, id);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                return resultSet.getString(2);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar las labores realizadas");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return "";
    }
    public boolean validarPorDescripcion(LaborRealizada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino]\n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_desc] like ? AND lr_cntro_cost_subcentro_cdgo=?;");
            query.setString(1, Objeto.getDescripcion());
            query.setInt(2, Objeto.getCentroCostoSubCentro().getCodigo());
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al Tratar de buscar");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public boolean validarPorCodigo(LaborRealizada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino]\n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_cdgo] like ?;");
            query.setString(1, Objeto.getCodigo());
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al Tratar de buscar");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public boolean validarExistenciaActualizar(LaborRealizada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT  [lr_cdgo]\n" +
                    "      ,[lr_desc]\n" +
                    "      ,[lr_cntro_cost_subcentro_cdgo]\n" +
                    "		  ,[ccs_cdgo]\n" +
                    "		  ,[ccs_desc]\n" +
                    "		  ,CASE WHEN ([ccs_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [ccs_estad] \n" +
                    "	  ,CASE WHEN ([lr_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [lr_estad] \n" +
                    "	  ,[lr_operativa]\n" +
                    "	  ,[lr_parada],[lr_bodegaDestino]\n" +
                    "  FROM ["+DB+"].[dbo].[labor_realizada]\n" +
                    "  INNER JOIN ["+DB+"].[dbo].[cntro_cost_subcentro] ON [lr_cntro_cost_subcentro_cdgo]=[ccs_cdgo] WHERE [lr_desc] like ? AND lr_cntro_cost_subcentro_cdgo=?;");
            query.setString(1, Objeto.getDescripcion());
            query.setInt(2, Objeto.getCentroCostoSubCentro().getCodigo());
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                retorno =true;
            }
        }catch (SQLException sqlException){
            System.out.println("Error al Tratar de buscar");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return retorno;
    }
    public int actualizar(LaborRealizada Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            LaborRealizada LaborRealizadaAnterior=buscarEspecifico(""+Objeto.getCodigo());
            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            conexion= control.ConectarBaseDatos();
            PreparedStatement query= conexion.prepareStatement("UPDATE ["+DB+"].[dbo].[labor_realizada] set [lr_desc]=?, [lr_estad]=?, [lr_operativa]=?,[lr_parada]=? ,[lr_bodegaDestino]=? WHERE [lr_cdgo]=?");
            query.setString(1, Objeto.getDescripcion());
            query.setString(2, Objeto.getEstado());
            query.setString(3, Objeto.getEs_operativa());
            query.setString(4, Objeto.getEs_parada());
            query.setString(5, Objeto.getBodegaDestino());
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
                        "           ,'LABOR REALIZADA'" +
                        "           ,CONCAT('Se registró una nueva actualización en el sistema sobre ',?,' Código: ',?,' Nombre: ',?,' Estado: ',?,"
                        + "' actualizando la siguiente informacion a Código: ',?,' Nombre: ',?,' Estado: ',?));");
                Query_Auditoria.setString(1, us.getCodigo());
                Query_Auditoria.setString(2, namePc);
                Query_Auditoria.setString(3, ipPc);
                Query_Auditoria.setString(4, macPC);
                Query_Auditoria.setString(5, LaborRealizadaAnterior.getCodigo());
                Query_Auditoria.setString(6, " la Actividad de Operación, ");
                Query_Auditoria.setString(7, LaborRealizadaAnterior.getCodigo());
                Query_Auditoria.setString(8, LaborRealizadaAnterior.getDescripcion());
                Query_Auditoria.setString(9, LaborRealizadaAnterior.getEstado()+ " Operativa: "+LaborRealizadaAnterior.getEs_operativa()+" Parada: "+LaborRealizadaAnterior.getEs_parada()+ " Bodega_Destino: "+LaborRealizadaAnterior.getBodegaDestino());
                Query_Auditoria.setString(10, Objeto.getCodigo());
                Query_Auditoria.setString(11, Objeto.getDescripcion());
                Query_Auditoria.setString(12, estado+ " Operativa: "+Objeto.getEs_operativa()+" Parada: "+Objeto.getEs_parada()+ " Bodega_Destino: "+Objeto.getBodegaDestino());
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
}
