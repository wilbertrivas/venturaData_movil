package com.example.vg_appcostos.ConexionesDB;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vg_appcostos.MainActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Costos_VG extends AppCompatActivity {
    private Connection conexion= null;
    private String servidor="";
    private String puertoConexion="";
    private String baseDeDatos="";
    private String usuario="";
    private String contrasena="";
    private String tipoConexion="";

    public Costos_VG(String tipoConexion) {
        this.puertoConexion = "3341";
        this.baseDeDatos = "costos_vg";
        //this.baseDeDatos = "costos_vg_test";
        this.usuario = "sa";
        this.contrasena = "root";
        this.tipoConexion=tipoConexion;

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
       //this.servidor = "tigre.goib.com";
    }
    public void privado(){
        this.servidor = "ccarga.goib.com";
        //this.servidor = "tigre.goib.com";
        //this.servidor = "pantera.goib.com";
    }
    public Connection ConectarBaseDatos(){
        try{
            if(!tipoConexion.equals("")) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                //conexion= DriverManager.getConnection("jdbc:jtds:sqlserver://172.25.80.81:1433;databaseName=costos_vg;user=sa;password=Ventura.20;");
                conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://" + servidor + ":" + puertoConexion + ";databaseName=" + baseDeDatos + ";user=" + usuario + ";password=" + contrasena + ";");
          /*if(conexion!=null){
                //validamos la configuración Regional en el servidor

              try{
                    PreparedStatement query= conexion.prepareStatement("DECLARE @validarConfiguracionRegional SMALLDATETIME='2020-02-05 08:45:00';\n" +
                            "                    SELECT @validarConfiguracionRegional;");
                    ResultSet resultSet= query.executeQuery();
                    while(resultSet.next()){
                        System.out.println("Fue===>"+resultSet.getString(1));
                        if(!resultSet.getString(1).equals("2020-02-05 08:45:00.0.")){
                            System.out.println("Valide configuración regional en el servidor \nDECLARE @validarConfiguracionRegional SMALLDATETIME='2020-02-05 08:45:00';\n" +
                                    "                    \nSELECT @validarConfiguracionRegional;\n" +
                                    "                    \n--	Respuesta OK: '2020-02-05 08:45:00'\n" +
                                    "                    \n--  Respuesta NO: '2020-05-02 08:45:00'");
                            System.out.println("=====================================================================> la conexion termino siendo null");
                            conexion=null;
                        }else{
                            System.out.println("=====================================================================> la conexion no termino siendo null");
                        }
                    }
                }catch (SQLException sqlException) {
                    //JOptionPane.showMessageDialog(null, "Error al tratar de consultar los articulos");
                  System.out.println("=====================================================================>error al tratar de validar fechas en e server");
                    sqlException.printStackTrace();
                }
              System.out.println("=====================================================================> conexion == es diferente de null");
                return conexion;
            }*/
            }else{
                conexion= null;
            }
        } catch(Exception e){
            conexion= null;
            e.printStackTrace();
            System.out.println("=====================================================================> conexion == null");
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
