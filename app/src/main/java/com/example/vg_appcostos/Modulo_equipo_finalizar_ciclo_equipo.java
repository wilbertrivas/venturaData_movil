package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon_ListadoEquipos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Recobro;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import com.google.zxing.Result;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Modulo_equipo_finalizar_ciclo_equipo extends AppCompatActivity implements Serializable, ZXingScannerView.ResultHandler{
    ArrayList<MvtoEquipo> Listado_MvtoEquipo=null;
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    String codigoEquipoS="";
    Spinner spinnerMovimientosEquipos,spinnerRecobroCliente,spinnerRazónFinalización,spinnerTipoEquipo,spinnerMarcaEquipo,spinnerEquipo;

    ArrayList<MotivoParada> listadoMotivoParada;
    ArrayList<MvtoEquipo> listadoEquipos;
    ArrayList<TipoEquipo> listadoTipoEquipo;
    ArrayList<String> listadoMarcaEquipo;
    ArrayList<Recobro> listadoRecobro;
    TextView usuarioCodigo,usuarioNombre;
    TextView equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoSerial,equipoDescripcion,equipoProveedorEquipo;
    TextView actividad_UsuarioInicia,actividad_fechaInicio,actividad_centroOperacion,actividad_subcentrocosto,actividad_centroCostoAuxiliar,actividad_cliente,textViewRazonFinalizacion,
    actividad_producto,actividad_motonave,actividad_laborRealizada,text_BodegaDestinoS,text_BodegaDestino;
    EditText actividad_numOrden,actividad_observacion;

    Button bntIniciar,bntAtras;
    private ZXingScannerView escanerView;
    AlertDialog.Builder builder;

    String typeConnection="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_equipo_finalizar_ciclo_equipo);

        spinnerMovimientosEquipos = (Spinner) findViewById(R.id.spinnerMovimientosEquipos);
        spinnerRecobroCliente = (Spinner) findViewById(R.id.spinnerRecobroCliente);
        spinnerRazónFinalización = (Spinner) findViewById(R.id.spinnerRazónFinalización);
        listadoMotivoParada = new ArrayList<>();
        listadoRecobro= new ArrayList<>();
        listadoEquipos= new ArrayList<>();
        listadoTipoEquipo= new ArrayList<>();
        listadoMarcaEquipo= new ArrayList<>();

        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        usuarioCodigo = (TextView) findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView) findViewById(R.id.usuarioNombre);
        usuarioCodigo.setText("" + user.getCodigo());
        usuarioNombre.setText("" + user.getNombres() + " " + user.getApellidos());

        bntIniciar = (Button) findViewById(R.id.bntIniciar);
        bntAtras = (Button) findViewById(R.id.bntAtras);
        Listado_MvtoEquipo = new ArrayList<>();
        equipoCodigo = (TextView) findViewById(R.id.equipoCodigo);
        equipoTipoEquipo = (TextView) findViewById(R.id.equipoTipoEquipo);
        equipoMarca = (TextView) findViewById(R.id.equipoMarca);
        equipoModelo = (TextView) findViewById(R.id.equipoModelo);
        equipoSerial = (TextView) findViewById(R.id.equipoSerial);
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
        text_BodegaDestinoS = (TextView) findViewById(R.id.text_BodegaDestinoS);
        text_BodegaDestino = (TextView) findViewById(R.id.text_BodegaDestino);
        textViewRazonFinalizacion = (TextView) findViewById(R.id.textViewRazonFinalizacion);
        cargueInfo("list_recobro");
        cargueInfo("cargar_razonesFinalizacón");
        spinnerTipoEquipo= (Spinner)findViewById(R.id.spinnerTipoEquipo);
        spinnerMarcaEquipo= (Spinner)findViewById(R.id.spinnerMarcaEquipo);
        spinnerEquipo= (Spinner)findViewById(R.id.spinnerEquipo);
        builder = new AlertDialog.Builder(this);
        spinnerMovimientosEquipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MvtoEquipo Objeto = Listado_MvtoEquipo.get(spinnerMovimientosEquipos.getSelectedItemPosition());
                if (Objeto != null) {

                    equipoCodigo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getCodigo());
                    equipoTipoEquipo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
                    equipoMarca.setText("" + Objeto.getAsignacionEquipo().getEquipo().getMarca());
                    equipoModelo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getModelo());
                    equipoSerial.setText("" + Objeto.getAsignacionEquipo().getEquipo().getSerial());
                    equipoDescripcion.setText("" + Objeto.getAsignacionEquipo().getEquipo().getDescripcion());
                    equipoProveedorEquipo.setText("" + Objeto.getProveedorEquipo().getDescripcion());

                    actividad_UsuarioInicia.setText("" + Objeto.getUsuarioQuieRegistra().getNombres() +" "+Objeto.getUsuarioQuieRegistra().getApellidos());
                    actividad_fechaInicio.setText("" + Objeto.getFechaHoraInicio());
                    actividad_centroOperacion.setText("" + Objeto.getCentroOperacion().getDescripcion());
                    actividad_subcentrocosto.setText("" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                    actividad_centroCostoAuxiliar.setText("" + Objeto.getCentroCostoAuxiliar().getDescripcion());
                    actividad_cliente.setText("" + Objeto.getCliente().getDescripcion());
                    actividad_producto.setText("" + Objeto.getArticulo().getDescripcion());
                    actividad_motonave.setText("" + Objeto.getMotonave().getDescripcion());
                    actividad_laborRealizada.setText("" + Objeto.getLaborRealizada().getDescripcion());
                    text_BodegaDestino.setText("" + Objeto.getCentroCostoAuxiliarDestino().getDescripcion());

                    if(Objeto.getLaborRealizada().getDescripcion().equals("STAND BY")){
                        cargueInfo("cargar_razonesFinalizacón_StandBy");
                        textViewRazonFinalizacion.setText("RAZÓN DEL STAND BY");
                    }else{
                        cargueInfo("cargar_razonesFinalizacón");
                        textViewRazonFinalizacion.setText("RAZÓN DE FINALIZACIÓN");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //ArrayList<MvtoCarbon_ListadoEquipos> Listado_mvtoCarbon_ListadoEquipos=null;
        Listado_MvtoEquipo = (ArrayList<MvtoEquipo>) getIntent().getExtras().getSerializable("Listado_MvtoEquipo");
        if (Listado_MvtoEquipo != null) {
            ArrayList<String> arrayListado = new ArrayList<>();
            for (MvtoEquipo listadoObjeto : Listado_MvtoEquipo) {
                arrayListado.add(
                    "______________________________________\n" +
                            "MOVIMIENTO EQUIPO # " + listadoObjeto.getCodigo() + "\n" +
                            "DATOS DEL EQUIPO\n" +
                            "  CÓDIGO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo() + "\n" +
                            "  EQUIPO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion() + "" +
                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getMarca() + "" +
                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getModelo() + "" +
                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion() + "\n" +

                            "  DATOS DE ACTIVIDAD\n" +
                            "  USUARIO ACTIVIDAD: " + listadoObjeto.getUsuarioQuieRegistra().getNombres()+" "+listadoObjeto.getUsuarioQuieRegistra().getApellidos() + "\n" +
                            "  FECHA INICIO: " + listadoObjeto.getFechaHoraInicio() + "\n" +
                            "  CENTRO OPERACIÓN: " + listadoObjeto.getCentroOperacion().getDescripcion() +" \n" +
                            "  SUBCENTRO COSTO: " + listadoObjeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() +" \n" +
                            "  CENTRO COSTO AUXILIAR: " + listadoObjeto.getCentroCostoAuxiliar().getDescripcion() +" \n" +
                            "  CLIENTE: " + listadoObjeto.getCliente().getDescripcion() +" \n" +
                            "  PRODUCTO: " + listadoObjeto.getArticulo().getDescripcion() +" \n" +
                            "  LABOR REALIZADA: " + listadoObjeto.getLaborRealizada().getDescripcion()+" \n"
                );
            }

            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
            spinnerMovimientosEquipos.setAdapter(adaptadorListado);
        } else {
            System.out.println("Asignacion nula");
        }
        bntIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Listado_MvtoEquipo != null) {
                    MvtoEquipo Objeto = Listado_MvtoEquipo.get(spinnerMovimientosEquipos.getSelectedItemPosition());
                    if (Objeto != null) {
                        if (listadoMotivoParada != null) {
                            MotivoParada motivoParada = listadoMotivoParada.get(spinnerRazónFinalización.getSelectedItemPosition());
                            Objeto.setNumeroOrden(actividad_numOrden.getText().toString());
                            Objeto.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                            Objeto.setObservacionMvtoEquipo(actividad_observacion.getText().toString());
                            if(motivoParada.getDescripcion().equals("STAND BY") && actividad_observacion.getText().toString().equals("")){
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Advertencia!!");
                                builder.setMessage("La obsevación debe ser obligatoria");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                            }else {
                                int result = 0;
                                try {
                                    Objeto.setUsuarioQuienCierra(user);
                                    result = new ControlDB_Equipo(typeConnection).cerrarCiclo_mtvoEquipo(Objeto, user, motivoParada);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (SocketException e) {
                                    e.printStackTrace();
                                }
                                if (result == 1) {
                                    //mensaje("Se registro el inicio de descargue de manera exitosa");
                                    builder.setTitle("Registro Exitoso!!");
                                    builder.setMessage("Se finalizó la actividad con el equipo escaneado de forma exitosa");
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            equipoCodigo.setText("");
                                            equipoTipoEquipo.setText("");
                                            equipoMarca.setText("");
                                            equipoModelo.setText("");
                                            equipoSerial.setText("");
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
                                            //cargueInfo("cargarMovimientos");
                                            Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Menu.class);
                                            interfaz.putExtra("usuarioB", user);
                                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                                            startActivity(interfaz);
                                        }
                                    });
                                    builder.show();
                                } else {
                                    //mensaje("Error al tratar de cerrar el ciclo del equipo");
                                    builder.setTitle("Error!!");
                                    builder.setMessage("No se pudo cerrar el ciclo del equipo, valide datos..");
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();
                                }
                            }
                        } else {
                            //mensaje("Se debe cargar un Motivo de Finalización");
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
                    //mensaje("Debe Escanear un Equipo");
                    builder.setTitle("Error!!");
                    builder.setMessage("Debe Escanear un Equipo, valide datos..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        bntAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Menu.class);
                interfaz.putExtra("usuarioB", user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
            }
        });
    }

    public void cargue_info(ArrayList<String> arrayListado) {
        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
        spinnerMovimientosEquipos.setAdapter(adaptadorListado);
        MvtoEquipo Objeto = Listado_MvtoEquipo.get(spinnerMovimientosEquipos.getSelectedItemPosition());
        if (Objeto != null) {

            equipoCodigo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getCodigo());
            equipoTipoEquipo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
            equipoMarca.setText("" + Objeto.getAsignacionEquipo().getEquipo().getMarca());
            equipoModelo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getModelo());
            equipoSerial.setText("" + Objeto.getAsignacionEquipo().getEquipo().getSerial());
            equipoDescripcion.setText("" + Objeto.getAsignacionEquipo().getEquipo().getDescripcion());
            equipoProveedorEquipo.setText("" + Objeto.getAsignacionEquipo().getEquipo().getProveedorEquipo().getDescripcion());


            actividad_UsuarioInicia.setText("" + Objeto.getUsuarioQuieRegistra().getNombres() +" "+Objeto.getUsuarioQuieRegistra().getApellidos());
            actividad_fechaInicio.setText("" + Objeto.getFechaHoraInicio());
            actividad_centroOperacion.setText("" + Objeto.getCentroOperacion().getDescripcion());
            actividad_subcentrocosto.setText("" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
            actividad_centroCostoAuxiliar.setText("" + Objeto.getCentroCostoAuxiliar().getDescripcion());
            actividad_cliente.setText("" + Objeto.getCliente().getDescripcion());
            actividad_producto.setText("" + Objeto.getArticulo().getDescripcion());
            actividad_motonave.setText("" + Objeto.getMotonave().getDescripcion());
            actividad_laborRealizada.setText("" + Objeto.getLaborRealizada().getDescripcion());
            if(Objeto.getLaborRealizada().getDescripcion().equals("STAND BY")){
                cargueInfo("cargar_razonesFinalizacón_StandBy");
                textViewRazonFinalizacion.setText("RAZÓN DEL STAND BY");
            }else{
                cargueInfo("cargar_razonesFinalizacón");
                textViewRazonFinalizacion.setText("RAZÓN DE FINALIZACIÓN");
            }


        }
    };

    public void cargueInfo(String opcion) {
        switch (opcion) {
            case "cargar_razonesFinalizacón": {
                ArrayList<String> arrayListado = new ArrayList<>();
                try {
                    ControlDB_MotivoParada ControlDB_MotivoParada = new ControlDB_MotivoParada(typeConnection);
                    listadoMotivoParada = ControlDB_MotivoParada.buscarActivos();
                    if(listadoMotivoParada !=null) {
                        for (MotivoParada listadoObjeto : listadoMotivoParada) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    }

                } catch (SQLException e) {
                    //mensaje("No se pudo consultar los Motivos de Parada");
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
            case "cargar_razonesFinalizacón_StandBy": {
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
                    textViewRazonFinalizacion.setText("RAZÓN DEL STAND BY");
                } catch (SQLException e) {
                    //mensaje("No se pudo consultar los Motivos de Parada");
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
            case "cargarMovimientos": {
                Equipo equipo = new Equipo();
                equipo.setCodigo(codigoEquipoS);
                ArrayList<MvtoEquipo> listado_mvtoEquipo = null;
                try {
                    listado_mvtoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivos(equipo,zonaTrabajoSeleccionada);
                    System.out.println("====================================================>" + codigoEquipoS);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Listado_MvtoEquipo = listado_mvtoEquipo;
                ArrayList<String> arrayListado = new ArrayList<>();
                for (MvtoEquipo listadoObjeto : Listado_MvtoEquipo) {
                    arrayListado.add(

                            "______________________________________\n" +
                                    "MOVIMIENTO EQUIPO # " + listadoObjeto.getCodigo() + "\n" +
                                    "DATOS DEL EQUIPO\n" +
                                    "  CÓDIGO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo() + "\n" +
                                    "  EQUIPO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion() + "" +
                                    " " + listadoObjeto.getAsignacionEquipo().getEquipo().getMarca() + "" +
                                    " " + listadoObjeto.getAsignacionEquipo().getEquipo().getModelo() + "" +
                                    " " + listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion() + "\n" +

                                 "  DATOS DE ACTIVIDAD\n" +
                            "  USUARIO ACTIVIDAD: " + listadoObjeto.getUsuarioQuieRegistra().getNombres()+" "+listadoObjeto.getUsuarioQuieRegistra().getApellidos() + "\n" +
                            "  FECHA INICIO: " + listadoObjeto.getFechaHoraInicio() + "\n" +
                            "  CENTRO OPERACIÓN: " + listadoObjeto.getCentroOperacion().getDescripcion() +" \n" +
                            "  SUBCENTRO COSTO: " + listadoObjeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() +" \n" +
                            "  CENTRO COSTO AUXILIAR: " + listadoObjeto.getCentroCostoAuxiliar().getDescripcion() +" \n" +
                            "  CLIENTE: " + listadoObjeto.getCliente().getDescripcion() +" \n" +
                            "  PRODUCTO: " + listadoObjeto.getArticulo().getDescripcion() +" \n" +
                            "  MOTONAVE: " + listadoObjeto.getMotonave().getDescripcion() +" \n" +
                            "  LABOR REALIZADA: " + listadoObjeto.getLaborRealizada().getDescripcion()+" \n"
                    );
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                spinnerMovimientosEquipos.setAdapter(adaptadorListado);
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
            case "list_tipoEquipo":{
                try {
                    ArrayList<String> arrayListadoT =new ArrayList<>();
                    listadoTipoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivosTipoEquipo(zonaTrabajoSeleccionada);
                    if(listadoTipoEquipo != null) {
                        for (TipoEquipo Objeto : listadoTipoEquipo) {
                            arrayListadoT.add(Objeto.getDescripcion());
                        }
                        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListadoT);
                        spinnerTipoEquipo.setAdapter(adaptadorListado);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "list_marcaEquipo":{
                try {
                    ArrayList<String> arrayListadoT =new ArrayList<>();
                    if(listadoTipoEquipo != null) {
                        listadoMarcaEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivosMarcaEquipo(listadoTipoEquipo.get(spinnerTipoEquipo.getSelectedItemPosition()).getCodigo(),zonaTrabajoSeleccionada);
                        if (listadoMarcaEquipo != null) {
                            for (String Objeto : listadoMarcaEquipo) {
                                arrayListadoT.add(Objeto);
                            }
                            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListadoT);
                            spinnerMarcaEquipo.setAdapter(adaptadorListado);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "list_Equipo":{
                try {
                    if(listadoTipoEquipo != null && listadoMarcaEquipo != null ) {
                        ArrayList<String> arrayListadoT = new ArrayList<>();
                        Equipo equipo = new Equipo();
                        equipo.setMarca(listadoMarcaEquipo.get(spinnerMarcaEquipo.getSelectedItemPosition()));
                        equipo.setTipoEquipo(listadoTipoEquipo.get(spinnerTipoEquipo.getSelectedItemPosition()));
                        listadoEquipos = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivosEquipos(equipo,zonaTrabajoSeleccionada);
                        if (listadoEquipos != null) {
                            for (MvtoEquipo Objeto : listadoEquipos) {
                                arrayListadoT.add(Objeto.getAsignacionEquipo().getEquipo().getDescripcion() + " " + Objeto.getAsignacionEquipo().getEquipo().getModelo());
                            }
                            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListadoT);
                            spinnerEquipo.setAdapter(adaptadorListado);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void mensaje(String mensaje) {
        int toastDuration = 10000; // in MilliSeconds
        final Toast mToast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        CountDownTimer countDownTimer = new CountDownTimer(toastDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                mToast.show();
            }

            public void onFinish() {
                mToast.cancel();
            }
        };
        mToast.show();
        countDownTimer.start();
    }

    public void esperarYCerrar(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos

            }
        }, milisegundos);
    }

    public void EscannerQR(View view) {
        escanerView = new ZXingScannerView(this);
        setContentView(escanerView);
        escanerView.setResultHandler(this);
        escanerView.startCamera();
    }
    public void seleccionarEquipoManual(View view){
        cargueInfo("list_tipoEquipo");
        cargueInfo("list_marcaEquipo");
        cargueInfo("list_Equipo");
        listenerSpinner();
    }
    public void listenerSpinner() {
        spinnerTipoEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("list_marcaEquipo");
                cargueInfo("list_Equipo");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerMarcaEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("list_Equipo");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<MvtoEquipo> listado_MvtoEquipo = null;
                try {
                    listado_MvtoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivos(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()).getAsignacionEquipo().getEquipo(),zonaTrabajoSeleccionada);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (listado_MvtoEquipo != null) {
                    Listado_MvtoEquipo = listado_MvtoEquipo;
                    if (Listado_MvtoEquipo != null) {
                        ArrayList<String> arrayListado = new ArrayList<>();
                        for (MvtoEquipo listadoObjeto : Listado_MvtoEquipo) {
                            arrayListado.add(
                                    "______________________________________\n" +
                                            "MOVIMIENTO EQUIPO # " + listadoObjeto.getCodigo() + "\n" +
                                            "DATOS DEL EQUIPO\n" +
                                            "  CÓDIGO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo() + "\n" +
                                            "  EQUIPO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getMarca() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getModelo() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion() + "\n" +

                                            "  DATOS DE ACTIVIDAD\n" +
                                            "  USUARIO ACTIVIDAD: " + listadoObjeto.getUsuarioQuieRegistra().getNombres() + " " + listadoObjeto.getUsuarioQuieRegistra().getApellidos() + "\n" +
                                            "  FECHA INICIO: " + listadoObjeto.getFechaHoraInicio() + "\n" +
                                            "  CENTRO OPERACIÓN: " + listadoObjeto.getCentroOperacion().getDescripcion() + " \n" +
                                            "  SUBCENTRO COSTO: " + listadoObjeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + " \n" +
                                            "  CENTRO COSTO AUXILIAR: " + listadoObjeto.getCentroCostoAuxiliar().getDescripcion() + " \n" +
                                            "  CLIENTE: " + listadoObjeto.getCliente().getDescripcion() + " \n" +
                                            "  PRODUCTO: " + listadoObjeto.getArticulo().getDescripcion() + " \n" +
                                            "  LABOR REALIZADA: " + listadoObjeto.getLaborRealizada().getDescripcion() + " \n"
                            );
                        }
                        //ArrayAdapter<String> adaptadorListador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                        CargarMovimientos(arrayListado);


                        //ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                        //spinnerMovimientosEquipos.setAdapter(adaptadorListado);

                        /*
                        Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Modulo_equipo_finalizar_ciclo_equipo.class);
                        interfaz.putExtra("Listado_MvtoEquipo", listado_MvtoEquipo);
                        interfaz.putExtra("usuarioB", user);
                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                        startActivity(interfaz);*/
                    }

                } else {
                    //mensaje("El equipo escaneado no tiene actividades pendientes");
                    builder.setTitle("Alerta!!");
                    builder.setMessage("El equipo escaneado no tiene actividades pendientes..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Modulo_equipo_finalizar_ciclo_equipo.class);
                            interfaz.putExtra("usuarioB", user);
                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                            startActivity(interfaz);
                            onPause();
                        }
                    });
                    builder.show();
                    //escanerView.resumeCameraPreview(this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }
    public void CargarMovimientos(ArrayList<String> arrayListado){
        ArrayAdapter<String> adaptadorListador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
        spinnerMovimientosEquipos.setAdapter(adaptadorListador);
    }
    @Override
    public void handleResult(Result rawResult) {
        String codigoEquipo = rawResult.getText();
        try {
            Equipo equipo = new Equipo();
            equipo.setCodigo(codigoEquipo);
            ArrayList<MvtoEquipo> listado_MvtoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoActivos(equipo,zonaTrabajoSeleccionada);
            if (listado_MvtoEquipo != null) {

                Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Modulo_equipo_finalizar_ciclo_equipo.class);
                interfaz.putExtra("Listado_MvtoEquipo", listado_MvtoEquipo);
                interfaz.putExtra("usuarioB", user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
                onPause();
            } else {
                //mensaje("El equipo escaneado no tiene actividades pendientes");
                builder.setTitle("Alerta!!");
                builder.setMessage("El equipo escaneado no tiene actividades pendientes..");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Modulo_equipo_finalizar_ciclo_equipo.class);
                    interfaz.putExtra("usuarioB",  user);
                    interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    startActivity(interfaz);
                    onPause();
                    }
                });
                builder.show();
                //escanerView.resumeCameraPreview(this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        escanerView = new ZXingScannerView(this);
        escanerView.stopCamera();
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
                    Intent interfaz = new Intent(Modulo_equipo_finalizar_ciclo_equipo.this, Menu.class);
                    interfaz.putExtra("usuarioB", user);
                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
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