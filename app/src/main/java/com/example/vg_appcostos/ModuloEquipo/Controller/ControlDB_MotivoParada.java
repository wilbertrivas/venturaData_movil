package com.example.vg_appcostos.ModuloEquipo.Controller;

import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
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

public class ControlDB_MotivoParada {
    private Connection conexion = null;
    private String tipoConexion;
    public ControlDB_MotivoParada(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }
    public int registrar(MotivoParada Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException {
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
            if(!validarPorDescripcion(Objeto)){
                conexion= control.ConectarBaseDatos();
                if(!validarPorCodigo(Objeto)){
                    conexion= control.ConectarBaseDatos();
                    PreparedStatement Query= conexion.prepareStatement("INSERT INTO ["+DB+"].[dbo].[motivo_parada] ([mpa_cdgo],[mpa_desc],[mpa_estad]) VALUES ((SELECT (CASE WHEN (MAX([mpa_cdgo]) IS NULL)\n" +
                            " THEN 1\n" +
                            " ELSE (MAX([mpa_cdgo])+1) END)AS [mpa_cdgo]\n" +
                            " FROM ["+DB+"].[dbo].[motivo_parada]),?,?);");
                    Query.setString(1, Objeto.getDescripcion());
                    Query.setString(2, Objeto.getEstado());
                    Query.execute();
                    result=1;
                    if(result==1){
                        result=0;
                        //Sacamos el ultimo valor
                        PreparedStatement queryMaximo= conexion.prepareStatement("SELECT MAX(mpa_cdgo) FROM ["+DB+"].[dbo].[motivo_parada];");
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
                                "           ,'MOTIVO_PARADA'" +
                                "           ,CONCAT (?,?,' Nombre: ',?,' Estado: ',?));");
                        Query_Auditoria.setString(1, us.getCodigo());
                        Query_Auditoria.setString(2, namePc);
                        Query_Auditoria.setString(3, ipPc);
                        Query_Auditoria.setString(4, macPC);
                        Query_Auditoria.setString(5, Objeto.getCodigo());
                        Query_Auditoria.setString(6, "Se registró un nuevo motivo de parada en el sistema, con Código: ");
                        Query_Auditoria.setString(7, Objeto.getCodigo());
                        Query_Auditoria.setString(8, Objeto.getDescripcion());
                        Query_Auditoria.setString(9, estado);
                        Query_Auditoria.execute();
                        result=1;
                    }
                }else{
                    System.out.println("Ya existe un motivo de parada registrado con ese código");
                }
            }else{
                System.out.println("Ya existe un motivo de parada registrado con ese nombre");
            }
        }
        catch (SQLException sqlException ){
            result=0;
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return result;
    }
    public ArrayList<MotivoParada> buscar(String valorConsulta) throws SQLException{
        ArrayList<MotivoParada> listadoObjetos = new ArrayList();
        Costos_VG control = new Costos_VG(tipoConexion);

        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            ResultSet resultSet;
            if(valorConsulta.equals("")){
                PreparedStatement query= conexion.prepareStatement("SELECT mpa_cdgo, mpa_desc, CASE WHEN (mpa_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS mpa_estad FROM ["+DB+"].[dbo].[motivo_parada];");
                resultSet= query.executeQuery();
            }else{
                PreparedStatement query= conexion.prepareStatement("SELECT mpa_cdgo, mpa_desc, CASE WHEN (mpa_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS mpa_estad FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_desc] like ?;");
                query.setString(1, "%"+valorConsulta+"%");
                resultSet= query.executeQuery();
            }
            while(resultSet.next()){
                MotivoParada Objeto = new MotivoParada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
                listadoObjetos.add(Objeto);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los motivos de parada");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }
    public MotivoParada buscarEspecifico(String codigo) throws SQLException{
        MotivoParada Objeto =null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT mpa_cdgo, mpa_desc, CASE WHEN (mpa_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS mpa_estad FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_cdgo] = ?;");
            query.setString(1, codigo);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                Objeto = new MotivoParada();
                Objeto.setCodigo(resultSet.getString(1));
                Objeto.setDescripcion(resultSet.getString(2));
                Objeto.setEstado(resultSet.getString(3));
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar al consultar los motivos de parada");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return Objeto;
    }
    public ArrayList<MotivoParada> buscarActivos() throws SQLException{
        ArrayList<MotivoParada> listadoObjetos = null;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        System.out.println("Tipo Conexion: "+tipoConexion +" BaseDatos: "+ control.getBaseDeDatos());
        conexion= control.ConectarBaseDatos();
        try{
            System.out.println(""+"SELECT mpa_cdgo, mpa_desc, CASE WHEN (mpa_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS mpa_estad FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_estad]=1;");
            PreparedStatement query= conexion.prepareStatement("SELECT mpa_cdgo, mpa_desc, CASE WHEN (mpa_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS mpa_estad FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_estad]=1;");
            ResultSet resultSet= query.executeQuery();
            boolean validator=true;
            while(resultSet.next()){
                if(validator){
                    listadoObjetos = new ArrayList();
                    validator= false;
                }
                MotivoParada Objetos = new MotivoParada();
                Objetos.setCodigo(resultSet.getString(1));
                Objetos.setDescripcion(resultSet.getString(2));
                Objetos.setEstado(resultSet.getString(3));
                listadoObjetos.add(Objetos);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar los motivos de parada");
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
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_desc] like ?;");
            query.setString(1, nombre);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                return resultSet.getString(1);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar los motivos de parada");
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
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_cdgo] =?;");
            query.setString(1, id);
            ResultSet resultSet= query.executeQuery();
            while(resultSet.next()){
                return resultSet.getString(2);
            }
        }catch (SQLException sqlException) {
            System.out.println("Error al tratar de consultar los motivos de parada");
            sqlException.printStackTrace();
        }
        control.cerrarConexionBaseDatos();
        return "";
    }
    public boolean validarPorDescripcion(MotivoParada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_desc] like ?;");
            query.setString(1, Objeto.getDescripcion());
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
    public boolean validarPorCodigo(MotivoParada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_cdgo] like ?;");
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
    public boolean validarExistenciaActualizar(MotivoParada Objeto){
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        boolean retorno=false;
        try{
            PreparedStatement query= conexion.prepareStatement("SELECT * FROM ["+DB+"].[dbo].[motivo_parada] WHERE [mpa_desc] like ? AND [mpa_cdgo]<> ?;");
            query.setString(1, Objeto.getDescripcion());
            query.setString(2, Objeto.getCodigo());
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
    public int actualizar(MotivoParada Objeto, Usuario us) throws FileNotFoundException, UnknownHostException, SocketException{
        int result=0;
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion= control.ConectarBaseDatos();
        try{
            MotivoParada MotivoParadaAnterior=buscarEspecifico(""+Objeto.getCodigo());
            String estado="";
            if(Objeto.getEstado().equalsIgnoreCase("1")){
                estado="ACTIVO";
            }else{
                estado="INACTIVO";
            }
            conexion= control.ConectarBaseDatos();
            PreparedStatement query= conexion.prepareStatement("UPDATE ["+DB+"].[dbo].[motivo_parada] set [mpa_desc]=?, [mpa_estad]=? WHERE [mpa_cdgo]=?");
            query.setString(1, Objeto.getDescripcion());
            query.setString(2, Objeto.getEstado());
            query.setString(3, Objeto.getCodigo());
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
                        "           ,'MOTIVO_PARADA'" +
                        "           ,CONCAT('Se registró una nueva actualización en el sistema sobre ',?,' Código: ',?,' Nombre: ',?,' Estado: ',?,"
                        + "' actualizando la siguiente informacion a Código: ',?,' Nombre: ',?,' Estado: ',?));");
                Query_Auditoria.setString(1, us.getCodigo());
                Query_Auditoria.setString(2, namePc);
                Query_Auditoria.setString(3, ipPc);
                Query_Auditoria.setString(4, macPC);
                Query_Auditoria.setString(5, MotivoParadaAnterior.getCodigo());
                Query_Auditoria.setString(6, " el motivo de Parada, ");
                Query_Auditoria.setString(7, MotivoParadaAnterior.getCodigo());
                Query_Auditoria.setString(8, MotivoParadaAnterior.getDescripcion());
                Query_Auditoria.setString(9, MotivoParadaAnterior.getEstado());
                Query_Auditoria.setString(10, Objeto.getCodigo());
                Query_Auditoria.setString(11, Objeto.getDescripcion());
                Query_Auditoria.setString(12, estado);
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
