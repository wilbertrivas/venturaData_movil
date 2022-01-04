package com.example.vg_appcostos.ConexionesDB;

import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Ccarga_OPP {
    private Connection conexion= null;
    private String servidor="";
    private String puertoConexion="";
    private String baseDeDatos="";
    private String usuario="";
    private String contrasena="";

    public Ccarga_OPP(String tipoConexion) {
        if(tipoConexion.equals("publico")){
            publico();
        }else{
            if(tipoConexion.equals("privado")){
                privado();
            }
        }
    }
    public void publico(){
        this.servidor = "190.131.213.43";
        this.puertoConexion = "3341";
        this.baseDeDatos = "venus_opp";
        this.usuario = "sa";
        this.contrasena = "root";


        /** Código de Prueba **/
        //this.servidor = "172.30.200.200";
        // this.puertoConexion = "3341";
        //this.baseDeDatos = "venus_opp_prueba_12032021";
    }
    public void privado(){
        this.servidor = "ccarga.goib.com";
        this.puertoConexion = "3341";
        this.baseDeDatos = "venus_opp";
        this.usuario = "sa";
        this.contrasena = "root";

        /** Código de Prueba **/
        //this.servidor = "172.30.200.200";
        //this.puertoConexion = "3341";
        //this.baseDeDatos = "venus_opp_prueba_06042021";

    }
    public Connection ConectarBaseDatos(){
        try{
            StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://"+servidor+":"+puertoConexion+";databaseName="+baseDeDatos+";user="+usuario+";password="+contrasena+";");
        } catch(Exception e){
            //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return conexion;
    }
    public void cerrarConexionBaseDatos(){
        try{
            conexion.close();
        }
        catch(SQLException e){
            e.printStackTrace();
            //JOptionPane.showMessageDialog(null, "Error al cerrar la Conexion a la Base de Datos");
        }
    }
    public String getBaseDeDatos() {
        return baseDeDatos;
    }
}
