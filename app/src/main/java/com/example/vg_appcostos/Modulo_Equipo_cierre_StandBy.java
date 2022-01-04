package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Recobro;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Modulo_Equipo_cierre_StandBy extends AppCompatActivity implements Serializable{
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    Spinner spinnerRecobroCliente,spinnerRazónFinalización;

    ArrayList<MotivoParada> listadoMotivoParada;
    ArrayList<Recobro> listadoRecobro;
    TextView usuarioCodigo,usuarioNombre;
    TextView equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoDescripcion,equipoProveedorEquipo;
    TextView actividad_UsuarioInicia,actividad_fechaInicio,actividad_centroOperacion,actividad_subcentrocosto,actividad_centroCostoAuxiliar,actividad_cliente,
            actividad_producto,actividad_motonave,actividad_laborRealizada;
    EditText actividad_numOrden,actividad_observacion;

    Button bntIniciar,bntAtras;
    AlertDialog.Builder builder;
    String typeConnection="";
    String codigoEquipo="";
    MvtoEquipo Objeto=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo__equipo_cierre__stand_by);
        spinnerRecobroCliente = (Spinner) findViewById(R.id.spinnerRecobroCliente);
        spinnerRazónFinalización = (Spinner) findViewById(R.id.spinnerRazónFinalización);
        listadoMotivoParada = new ArrayList<>();
        listadoRecobro= new ArrayList<>();

        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        codigoEquipo=  getIntent().getExtras().getString("codigoEquipoT");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        usuarioCodigo = (TextView) findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView) findViewById(R.id.usuarioNombre);
        usuarioCodigo.setText("" + user.getCodigo());
        usuarioNombre.setText("" + user.getNombres() + " " + user.getApellidos());

        bntIniciar = (Button) findViewById(R.id.bntIniciar);
        bntAtras = (Button) findViewById(R.id.bntAtras);
        equipoCodigo = (TextView) findViewById(R.id.equipoCodigo);
        equipoTipoEquipo = (TextView) findViewById(R.id.equipoTipoEquipo);
        equipoMarca = (TextView) findViewById(R.id.equipoMarca);
        equipoModelo = (TextView) findViewById(R.id.equipoModelo);
        equipoDescripcion = (TextView) findViewById(R.id.equipoDescripcion);
        equipoProveedorEquipo = (TextView) findViewById(R.id.equipoProveedorEquipo);

        actividad_UsuarioInicia = (TextView) findViewById(R.id.actividad_UsuarioInicia);
        actividad_numOrden = (EditText) findViewById(R.id.actividad_numOrden);
        actividad_observacion = (EditText) findViewById(R.id.actividad_observacion);
        actividad_fechaInicio = (TextView) findViewById(R.id.actividad_fechaInicio);
        actividad_centroOperacion = (TextView) findViewById(R.id.actividad_centroOperacion);
        actividad_subcentrocosto = (TextView) findViewById(R.id.actividad_subcentrocosto);
        actividad_centroCostoAuxiliar = (TextView) findViewById(R.id.actividad_centroCostoAuxiliar);
        actividad_cliente = (TextView) findViewById(R.id.actividad_cliente);
        actividad_producto = (TextView) findViewById(R.id.actividad_producto);
        actividad_motonave = (TextView) findViewById(R.id.actividad_motonave);
        actividad_laborRealizada = (TextView) findViewById(R.id.actividad_laborRealizada);
        cargueInfo("list_recobro");
        cargueInfo("cargar_razonesFinalizacón");
        builder = new AlertDialog.Builder(this);
        if(!codigoEquipo.equals("")){
            try {
                Equipo equipo = new Equipo();
                equipo.setCodigo(codigoEquipo);
                ArrayList<MvtoEquipo> listado_MvtoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivos_StandBy(equipo);
                if (listado_MvtoEquipo != null) {
                    for (MvtoEquipo ObjetoT : listado_MvtoEquipo) {
                        equipoCodigo.setText("" + ObjetoT.getAsignacionEquipo().getEquipo().getCodigo());
                        equipoTipoEquipo.setText("" + ObjetoT.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
                        equipoMarca.setText("" + ObjetoT.getAsignacionEquipo().getEquipo().getMarca());
                        equipoModelo.setText("" + ObjetoT.getAsignacionEquipo().getEquipo().getModelo());
                        equipoDescripcion.setText("" + ObjetoT.getAsignacionEquipo().getEquipo().getDescripcion());
                        equipoProveedorEquipo.setText("" + ObjetoT.getProveedorEquipo().getDescripcion());
                        actividad_UsuarioInicia.setText("" + ObjetoT.getUsuarioQuieRegistra().getNombres() + " " + ObjetoT.getUsuarioQuieRegistra().getApellidos());
                        actividad_fechaInicio.setText("" + ObjetoT.getFechaHoraInicio());
                        actividad_centroOperacion.setText("" + ObjetoT.getCentroOperacion().getDescripcion());
                        actividad_subcentrocosto.setText("" + ObjetoT.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                        actividad_centroCostoAuxiliar.setText("" + ObjetoT.getCentroCostoAuxiliar().getDescripcion());
                        actividad_cliente.setText("" + ObjetoT.getCliente().getDescripcion());
                        actividad_producto.setText("" + ObjetoT.getArticulo().getDescripcion());
                        actividad_motonave.setText("" + ObjetoT.getMotonave().getDescripcion());
                        actividad_laborRealizada.setText("" + ObjetoT.getLaborRealizada().getDescripcion());
                        Objeto=ObjetoT;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        bntIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actividad_observacion.getText().equals("")){
                    builder = new AlertDialog.Builder(builder.getContext());
                    builder.setTitle("Advertencia!!");
                    builder.setMessage("La obsevación debe ser obligatoria");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
                else {
                    if (!actividad_observacion.getText().toString().equals("")) {
                        if (Objeto != null) {
                            if (listadoMotivoParada != null) {
                                MotivoParada motivoParada = listadoMotivoParada.get(spinnerRazónFinalización.getSelectedItemPosition());
                                Objeto.setNumeroOrden(actividad_numOrden.getText().toString());
                                Objeto.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                Objeto.setObservacionMvtoEquipo(actividad_observacion.getText().toString());
                                Objeto.setUsuarioQuienCierra(user);
                                int result = 0;
                                try {
                                    result = new ControlDB_Equipo(typeConnection).cerrarCiclo_mtvoEquipo(Objeto, user, motivoParada);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                }
                                if (result == 1) {
                                    builder = new AlertDialog.Builder(builder.getContext());
                                    builder.setTitle("Registro Exitoso!!");
                                    builder.setMessage("Se finalizó la actividad con el equipo escaneado de forma exitosa");
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            equipoCodigo.setText("");
                                            equipoTipoEquipo.setText("");
                                            equipoMarca.setText("");
                                            equipoModelo.setText("");
                                            equipoDescripcion.setText("");
                                            equipoProveedorEquipo.setText("");

                                            actividad_UsuarioInicia.setText("");
                                            actividad_fechaInicio.setText("");
                                            actividad_centroOperacion.setText("");
                                            actividad_subcentrocosto.setText("");
                                            actividad_centroCostoAuxiliar.setText("");
                                            actividad_cliente.setText("");
                                            actividad_producto.setText("");
                                            actividad_motonave.setText("");
                                            actividad_laborRealizada.setText("");
                                            actividad_numOrden.setText("");
                                            actividad_observacion.setText("");
                                            Intent interfaz = new Intent(Modulo_Equipo_cierre_StandBy.this, Menu.class);
                                            interfaz.putExtra("usuarioB", user);
                                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                                            startActivity(interfaz);
                                        }
                                    });
                                    builder.show();
                                } else {
                                    builder = new AlertDialog.Builder(builder.getContext());
                                    builder.setTitle("Error!!");
                                    builder.setMessage("No se pudo cerrar el ciclo del equipo, valide datos..");
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();
                                }
                            } else {
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Error!!");
                                builder.setMessage("Se debe cargar un Motivo de Finalización, valide datos..");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                            }
                        }
                    } else {
                        builder = new AlertDialog.Builder(builder.getContext());
                        builder.setTitle("Advertencia!!");
                        builder.setMessage("Debe colocar una observación de finalización del Stand By");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }
            }
        });

        bntAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interfaz = new Intent(Modulo_Equipo_cierre_StandBy.this, Menu.class);
                interfaz.putExtra("usuarioB", user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
            }
        });
    }
    public void cargueInfo(String opcion) {
        switch (opcion) {
            case "cargar_razonesFinalizacón": {
                ArrayList<String> arrayListado = new ArrayList<>();
                try {
                    ControlDB_MotivoParada ControlDB_MotivoParada = new ControlDB_MotivoParada(typeConnection);
                    listadoMotivoParada = ControlDB_MotivoParada.buscarActivos();
                    listadoMotivoParada.remove(0);
                    if(listadoMotivoParada !=null) {
                        for (MotivoParada listadoObjeto : listadoMotivoParada) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    }
                } catch (SQLException e) {
                    builder.setTitle("Error!!");
                    builder.setMessage("No se pudo consultar los Motivos de Parada, valide datos..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                spinnerRazónFinalización.setAdapter(adaptadorListado);
                break;
            }
            case "list_recobro":{
                try {
                    ArrayList<String> arrayListadoT =new ArrayList<>();
                    ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                    listadoRecobro = controlDB_Carbon.listarRecobro();
                    for (Recobro Objeto : listadoRecobro) {
                        arrayListadoT.add(Objeto.getDescripcion());
                    }
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                    spinnerRecobroCliente.setAdapter(adaptadorListado);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            builder = new AlertDialog.Builder(builder.getContext());
            builder.setTitle("Alerta!!");
            builder.setMessage("Está seguro que desea salir?");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent interfaz = new Intent(Modulo_Equipo_cierre_StandBy.this, Menu.class);
                    interfaz.putExtra("usuarioB",  user);
                    interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    startActivity(interfaz);

                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}