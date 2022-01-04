package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vg_appcostos.ModuloCarbon.Modelo.ListAdapter_ListElement_ReporteMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ListElement_ReporteMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    Usuario user=null;
    AlertDialog.Builder builder;

    TextView Tcodigo, Tnombre,zonaTrabajo;
    Button  btn_CarbonIniciarDescargueVehiculo,btn_CarbonCerrarDescargueVehiculo,btn_CarbonIniciarCicloEquipo,
            btn_CarbonCerrarCicloEquipo,btn_EquipoIniciarCicloEquipo,btn_EquipoCerrarCicloEquipo,btn_reporteModuloCarbon,btn_reporteModuloEquipo;
            //Btn_EquipoRegistrar,Btn_EquipoConsultar,Btn_EquipoActualizar;
            ImageView bntSalir;
            ZonaTrabajo  zonaTrabajoSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Tcodigo = (TextView) findViewById(R.id.codigo);
        Tnombre = (TextView) findViewById(R.id.nombre);
        zonaTrabajo = (TextView) findViewById(R.id.zonaTrabajo);
        btn_CarbonIniciarDescargueVehiculo = (Button) findViewById(R.id.btn_CarbonIniciarDescargueVehiculo);
        btn_CarbonIniciarCicloEquipo = (Button) findViewById(R.id.btn_CarbonIniciarCicloEquipo);
        btn_CarbonCerrarCicloEquipo = (Button) findViewById(R.id.btn_CarbonCerrarCicloEquipo);
        btn_CarbonCerrarDescargueVehiculo = (Button) findViewById(R.id.btn_CarbonCerrarDescargueVehiculo);
        btn_reporteModuloCarbon = (Button) findViewById(R.id.btn_reporteModuloCarbon);
        btn_reporteModuloEquipo = (Button) findViewById(R.id.btn_reporteModuloEquipo);

        btn_EquipoIniciarCicloEquipo = (Button) findViewById(R.id.btn_EquipoIniciarCicloEquipo);
        btn_EquipoCerrarCicloEquipo = (Button) findViewById(R.id.btn_EquipoCerrarCicloEquipo);
        bntSalir= (ImageView)findViewById(R.id.bntSalir);
        builder = new AlertDialog.Builder(this);
        // final Usuario user = new ControlDB_Usuario().validarUsuario(new Usuario(codigo, contrasena));
        user= (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        //Cargamos la zonaTrabajo en la interfaz
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        if(zonaTrabajoSeleccionada != null) {
            zonaTrabajo.setText(zonaTrabajoSeleccionada.getDescripcion());
        }
        if(user != null) {
            Tcodigo.setText(user.getCodigo());
            Tnombre.setText(user.getNombres() + " " + user.getApellidos());
        }
        bntSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent interfaz = new Intent(Menu.this, MainActivity.class);
            startActivity(interfaz);
            }
        });
        btn_CarbonIniciarDescargueVehiculo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILCARBON_INICAR_DESCARGUE_VEHICULO")) {
                            //System.out.println(user.getPerfilUsuario().getPermisos().get(i).getDescripcion());
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, Modulo_carbon_iniciar_descargueVehiculo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_CarbonCerrarDescargueVehiculo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILCARBON_CERRAR_DESCARGUE_VEHICULO")) {
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, moduloCarbon_finalizar_descargueVehiculo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_reporteModuloCarbon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    Intent interfaz = new Intent(Menu.this, Reporte_Registro_MvtoCarbon.class);
                    //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                    //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                    interfaz.putExtra("usuarioB",  user);
                    interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    startActivity(interfaz);

                }
            }
        });
        btn_reporteModuloEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    Intent interfaz = new Intent(Menu.this, Reporte_Registro_MvtoEquipo.class);
                    //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                    //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                    interfaz.putExtra("usuarioB",  user);
                    interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    startActivity(interfaz);
                }
            }
        });


        btn_CarbonIniciarCicloEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILCARBON_INICIAR_CICLO_EQUIPO")) {
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, ModuloCarbon_iniciar_ciclo_equipo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_CarbonCerrarCicloEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILCARBON_CERRAR_CICLO_EQUIPO")) {
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, Modulo_carbon_finalizar_ciclo_equipo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_EquipoIniciarCicloEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILEQUIPO_INICIAR_CICLO_EQUIPO")) {
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, modulo_equipo_iniciar_ciclo_equipo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btn_EquipoCerrarCicloEquipo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(user != null) {
                    boolean validator = false;
                    for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
                        if (user.getPerfilUsuario().getPermisos().get(i).getDescripcion().equals("APPMOVILEQUIPO_CERRAR_CICLO_EQUIPO")) {
                            validator = true;
                        }
                    }
                    if (validator) {
                        /*Intent interfaz = new Intent(Menu.this, ModuloCarbon_Registrar.class);
                        interfaz.putExtra("codigo",getIntent().getExtras().getString("codigo"));
                        interfaz.putExtra("contrasena",getIntent().getExtras().getString("contrasena"));
                        startActivity(interfaz);*/
                        Intent interfaz = new Intent(Menu.this, Modulo_equipo_finalizar_ciclo_equipo.class);
                        //interfaz.putExtra("codigo", getIntent().getExtras().getString("codigo"));
                        //interfaz.putExtra("contrasena", getIntent().getExtras().getString("contrasena"));
                        interfaz.putExtra("usuarioB",  user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                        startActivity(interfaz);

                    } else {
                        Toast.makeText(getApplicationContext(), "EL USUARIO " + Tnombre.getText() + " No tiene permiso para ingresar a esta opcion", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //Ocultamos El menu para hacer validaciones de Permisos
        btn_CarbonIniciarDescargueVehiculo.setVisibility(View.INVISIBLE);
        btn_CarbonCerrarDescargueVehiculo.setVisibility(View.INVISIBLE);
        btn_CarbonIniciarCicloEquipo.setVisibility(View.INVISIBLE);
        btn_CarbonCerrarCicloEquipo.setVisibility(View.INVISIBLE);

        btn_EquipoIniciarCicloEquipo.setVisibility(View.INVISIBLE);
        btn_EquipoCerrarCicloEquipo.setVisibility(View.INVISIBLE);


        for (int i = 0; i < user.getPerfilUsuario().getPermisos().size(); i++) {
            switch (user.getPerfilUsuario().getPermisos().get(i).getDescripcion()) {
                case "APPMOVILCARBON_INICAR_DESCARGUE_VEHICULO": {
                    btn_CarbonIniciarDescargueVehiculo.setVisibility(View.VISIBLE);
                    break;
                }
                case "APPMOVILCARBON_INICIAR_CICLO_EQUIPO": {
                    btn_CarbonIniciarCicloEquipo.setVisibility(View.VISIBLE);
                    break;
                }
                case "APPMOVILCARBON_CERRAR_CICLO_EQUIPO": {
                    btn_CarbonCerrarCicloEquipo.setVisibility(View.VISIBLE);
                    break;
                }
                case "APPMOVILCARBON_CERRAR_DESCARGUE_VEHICULO": {
                    btn_CarbonCerrarDescargueVehiculo.setVisibility(View.VISIBLE);
                    break;
                }
                case "APPMOVILEQUIPO_INICIAR_CICLO_EQUIPO": {
                    btn_EquipoIniciarCicloEquipo.setVisibility(View.VISIBLE);
                    break;
                }
                case "APPMOVILEQUIPO_CERRAR_CICLO_EQUIPO": {
                    btn_EquipoCerrarCicloEquipo.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    break;
                }

            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent interfaz = new Intent(Menu.this, Menu.class);
            interfaz.putExtra("usuarioB",  user);
            interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
            startActivity(interfaz);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
