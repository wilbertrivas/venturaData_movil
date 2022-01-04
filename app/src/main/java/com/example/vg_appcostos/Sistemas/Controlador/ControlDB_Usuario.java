package com.example.vg_appcostos.Sistemas.Controlador;

import com.example.vg_appcostos.ConexionesDB.Costos_VG;
import com.example.vg_appcostos.Sistemas.EncriptarPassword;
import com.example.vg_appcostos.Sistemas.Modelo.Perfil;
import com.example.vg_appcostos.Sistemas.Modelo.Permisos;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
public class ControlDB_Usuario {
    private Connection conexion = null;
    private String tipoConexion;
    public ControlDB_Usuario(String tipoConexion) {
        this.tipoConexion= tipoConexion;
    }
    public ArrayList<Perfil> cargarPerfil() {
        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        ArrayList<Perfil> listadoPerfilUsuario = new ArrayList();
        conexion = control.ConectarBaseDatos();
        try {
            PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [prf_cdgo],[prf_desc] ,[prf_estad] FROM ["+DB+"].[dbo].[perfil] WHERE [prf_estad]=1; ");
            ResultSet resultSetBuscar = queryBuscar.executeQuery();
            while (resultSetBuscar.next()) {
                Perfil perfilUsuario = new Perfil();
                perfilUsuario.setCodigo(resultSetBuscar.getString(1));
                perfilUsuario.setDescripcion(resultSetBuscar.getString(2));
                perfilUsuario.setEstado(resultSetBuscar.getString(3));
                listadoPerfilUsuario.add(perfilUsuario);
            }
        } catch (Exception e) {

        }
        control.cerrarConexionBaseDatos();
        return listadoPerfilUsuario;
    }
    public ArrayList<Usuario> buscarUsuario_Permiso_AutorizarRecobros() throws SQLException{
        ArrayList<Usuario> listadoObjetos=null;

        Costos_VG control = new Costos_VG(tipoConexion);
        String DB=control.getBaseDeDatos();
        conexion = control.ConectarBaseDatos();
        if(conexion != null) {
            try {
                PreparedStatement queryBuscar = conexion.prepareStatement("SELECT  [us_cdgo]--1\n" +
                        "		,[us_nombres]--2\n" +
                        "		,[us_apellidos]--3\n" +
                        "		,[us_perfil_cdgo]--4\n" +
                        "		  ,[prf_cdgo]--5\n" +
                        "		  ,[prf_desc]--6\n" +
                        "		  ,CASE WHEN ([prf_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [prf_estad]--7\n" +
                        "      ,[us_correo]--8\n" +
                        "	  ,CASE WHEN ([us_estad]=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS [us_estad]--9\n" +
                        "      --,[prfp_perfil_cdgo]\n" +
                        "	  --,[prfp_permiso_cdgo]\n" +
                        "	  --,[pm_cdgo]\n" +
                        "      --,[pm_desc]\n" +
                        "  FROM ["+DB+"].[dbo].[usuario]\n" +
                        "	  INNER JOIN ["+DB+"].[dbo].[perfil] ON [us_perfil_cdgo]=[prf_cdgo]\n" +
                        "	  INNER JOIN ["+DB+"].[dbo].[perfil_permiso] ON [prfp_perfil_cdgo]=[us_perfil_cdgo]\n" +
                        "	  INNER JOIN ["+DB+"].[dbo].[permiso] ON [prfp_permiso_cdgo]=[pm_cdgo]\n" +
                        "  WHERE [pm_desc] LIKE 'MODULO_EQUIPO_AUTORIZAR_RECOBRO';");
                ResultSet resultSetBuscar = queryBuscar.executeQuery();
                boolean validador = true;
                while (resultSetBuscar.next()) {
                    if (validador) {
                        listadoObjetos = new ArrayList();
                        validador = false;
                    }
                    Usuario Objeto = new Usuario();
                    Objeto.setCodigo(resultSetBuscar.getString(1));
                    Objeto.setNombres(resultSetBuscar.getString(2));
                    Objeto.setApellidos(resultSetBuscar.getString(3));
                    Objeto.setPerfilUsuario(new Perfil(resultSetBuscar.getString(5), resultSetBuscar.getString(6), resultSetBuscar.getString(7)));
                    Objeto.setCorreo(resultSetBuscar.getString(8));
                    Objeto.setEstado(resultSetBuscar.getString(9));
                    listadoObjetos.add(Objeto);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        control.cerrarConexionBaseDatos();
        return listadoObjetos;
    }

    public Usuario validarUsuario(Usuario u) {
        //PreparedStatement pst=conexionBD_ControlCargaERP().prepareStatement(
        Usuario user = null;
        EncriptarPassword cripto = new EncriptarPassword();
        try {
            Costos_VG control = new Costos_VG(tipoConexion);
            String DB=control.getBaseDeDatos();
            conexion = control.ConectarBaseDatos();
            if(conexion != null) {
                //conexion = conexionBD();
                System.out.println("" + "SELECT [us_cdgo],[us_clave],[us_nombres],[us_apellidos],[us_perfil_cdgo] ,[us_correo]" +
                        "      ,CASE WHEN (us_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS us_estad,[prf_cdgo] ,[prf_desc]" +
                        "       ,CASE WHEN (prf_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS prf_estad ,[pm_cdgo],[pm_desc]" +
                        "  FROM [" + DB + "].[dbo].[usuario]" +
                        "	INNER JOIN [" + DB + "].[dbo].[perfil] ON us_perfil_cdgo= prf_cdgo" +
                        "	INNER JOIN [" + DB + "].[dbo].perfil_permiso ON prf_cdgo=prfp_perfil_cdgo" +
                        "	INNER JOIN [" + DB + "].[dbo].permiso ON pm_cdgo=prfp_permiso_cdgo" +
                        " WHERE [us_cdgo]=" + u.getCodigo() + " AND [us_clave]= " + cripto.md5(u.getClave()) + ";");
                PreparedStatement queryBuscar = conexion.prepareStatement("SELECT [us_cdgo],[us_clave],[us_nombres],[us_apellidos],[us_perfil_cdgo] ,[us_correo]" +
                        "      ,CASE WHEN (us_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS us_estad,[prf_cdgo] ,[prf_desc]" +
                        "       ,CASE WHEN (prf_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS prf_estad ,[pm_cdgo],[pm_desc]" +
                        "  FROM [" + DB + "].[dbo].[usuario]" +
                        "	INNER JOIN [" + DB + "].[dbo].[perfil] ON us_perfil_cdgo= prf_cdgo" +
                        "	INNER JOIN [" + DB + "].[dbo].perfil_permiso ON prf_cdgo=prfp_perfil_cdgo" +
                        "	INNER JOIN [" + DB + "].[dbo].permiso ON pm_cdgo=prfp_permiso_cdgo" +
                        " WHERE [pm_desc] LIKE 'APP%' AND [us_cdgo]=? AND [us_clave]= ?;");

            /*PreparedStatement queryBuscar = conexion.prepareStatement(""+
                    "SELECT [us_cdgo],[us_clave],[us_nombres],[us_apellidos],[us_perfil_cdgo] ,[us_correo]"+
                            " ,CASE WHEN (us_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS us_estad,[prf_cdgo] ,[prf_desc]"+
                    ",CASE WHEN (prf_estad=1) THEN 'ACTIVO' ELSE 'INACTIVO' END AS prf_estad ,[pm_cdgo],[pm_desc]"+
                            "FROM ["+DB+"].[dbo].[usuario]"+
                    "INNER JOIN ["+DB+"].[dbo].[perfil] ON us_perfil_cdgo= prf_cdgo"+
                    "INNER JOIN ["+DB+"].[dbo].perfil_permiso ON prf_cdgo=prfp_perfil_cdgo"+
                    "INNER JOIN ["+DB+"].[dbo].permiso ON pm_cdgo=prfp_permiso_cdgo"+
                    "WHERE [us_cdgo]=1111786243 --AND [us_clave]= '383028bb2e7587d00046b07bd729a7de';");*/

                queryBuscar.setString(1, u.getCodigo());
                queryBuscar.setString(2, cripto.md5(u.getClave()));
                ResultSet resultSetBuscar = queryBuscar.executeQuery();
                Perfil perfil = new Perfil();
                ArrayList<Permisos> listadoPermisos = new ArrayList<>();
                int contador = 1;
                while (resultSetBuscar.next()) {
                    if (cripto.md5(u.getClave().toString()).equals(resultSetBuscar.getString(2))) {
                        if (contador == 1) {
                            //cargamos el usuario
                            user = new Usuario();
                            user.setCodigo(resultSetBuscar.getString(1));
                            user.setClave(resultSetBuscar.getString(2));
                            user.setNombres(resultSetBuscar.getString(3));
                            user.setApellidos(resultSetBuscar.getString(4));
                            user.setCorreo(resultSetBuscar.getString(6));
                            user.setEstado(resultSetBuscar.getString(7));

                            //Cargamos el perfil
                            perfil.setCodigo(resultSetBuscar.getString(8));
                            perfil.setDescripcion(resultSetBuscar.getString(9));
                            perfil.setEstado(resultSetBuscar.getString(10));
                        }
                        //Cargamos los diferentes permisos
                        Permisos permisos = new Permisos();
                        permisos.setCodigo(resultSetBuscar.getString(11));
                        permisos.setDescripcion(resultSetBuscar.getString(12));
                        listadoPermisos.add(permisos);
                    }
                }
                if (user != null) {
                    perfil.setPermisos(listadoPermisos);
                    user.setPerfilUsuario(perfil);
                }
                conexion.close();
                control.cerrarConexionBaseDatos();
            }else{
                System.out.println("===============================================================================No hay conexión con el servidor");
            }
        } catch (SQLException e) {
            System.out.println("==============================================================>Error al conectar usuario");
            e.printStackTrace();
        }

        return user;
    }
}
