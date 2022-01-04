package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon_ListadoEquipos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
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

public class  Modulo_carbon_finalizar_ciclo_equipo extends AppCompatActivity implements Serializable, ZXingScannerView.ResultHandler{

    ArrayList<MvtoCarbon_ListadoEquipos> Listado_mvtoCarbon_ListadoEquipos=null;
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    String codigoEquipoS="";
    Spinner spinnerMovimientosEquipos,spinnerRazónFinalización,spinnerTipoEquipo,spinnerMarcaEquipo,spinnerEquipo;
    ArrayList<MvtoEquipo> listadoEquipos;
    ArrayList<TipoEquipo> listadoTipoEquipo;
    ArrayList<String> listadoMarcaEquipo;
    ArrayList<MvtoCarbon_ListadoEquipos> Listado_MvtoEquipo=null;

    ArrayList<MotivoParada> listadoMotivoParada;
    TextView usuarioCodigo,usuarioNombre,articulo,cliente,transportadora,orden,deposito,
            pesoLleno,fechaTara,fechaInicioDescargue,centroOperacion,subcentroCosto,
            auxCentroCosto,placaSeleccionada,text_laborRealizada,text_BodegaDestino;
    Button bntIniciar,bntAtras;
    private ZXingScannerView escanerView;
    TextView equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoSerial,equipoDescripcion,equipoProveedorEquipo,actividad_laborRealizada,actividad_UsuarioRegistra;
    AlertDialog.Builder builder;
    String typeConnection="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_carbon_finalizar_ciclo_equipo);
        spinnerMovimientosEquipos= (Spinner)findViewById(R.id.spinnerMovimientosEquipos);
        spinnerRazónFinalización= (Spinner)findViewById(R.id.spinnerRazónFinalización);
        listadoMotivoParada= new ArrayList<>();
        user= (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        usuarioCodigo = (TextView)findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView)findViewById(R.id.usuarioNombre);
        usuarioCodigo.setText(user.getCodigo());
        usuarioNombre.setText(user.getNombres()+" "+user.getApellidos());

        bntIniciar= (Button)findViewById(R.id.bntIniciar);
        bntAtras= (Button)findViewById(R.id.bntAtras);
        placaSeleccionada= (TextView)findViewById(R.id.placaSeleccionada);
        text_laborRealizada= (TextView)findViewById(R.id.text_laborRealizada);
        text_BodegaDestino= (TextView)findViewById(R.id.text_BodegaDestino);
        articulo= (TextView)findViewById(R.id.articulo);
        cliente= (TextView)findViewById(R.id.cliente);
        transportadora= (TextView)findViewById(R.id.transportadora);
        orden= (TextView)findViewById(R.id.orden);
        deposito= (TextView)findViewById(R.id.deposito);
        pesoLleno= (TextView)findViewById(R.id.pesoLleno);
        fechaTara= (TextView)findViewById(R.id.fechaTara);
        fechaInicioDescargue= (TextView)findViewById(R.id.fechaInicioDescargue);
        centroOperacion= (TextView)findViewById(R.id.centroOperacion);
        subcentroCosto= (TextView)findViewById(R.id.subcentroCosto);
        auxCentroCosto= (TextView)findViewById(R.id.auxCentroCosto);



        spinnerTipoEquipo= (Spinner)findViewById(R.id.spinnerTipoEquipo);
        spinnerMarcaEquipo= (Spinner)findViewById(R.id.spinnerMarcaEquipo);
        spinnerEquipo= (Spinner)findViewById(R.id.spinnerEquipo);
        listadoEquipos= new ArrayList<>();
        listadoTipoEquipo= new ArrayList<>();
        listadoMarcaEquipo= new ArrayList<>();

        cargueInfo("cargar_razonesFinalizacón");
        //Equipo Y Asignacion
        equipoCodigo= (TextView)findViewById(R.id.equipoCodigo);
        equipoTipoEquipo= (TextView)findViewById(R.id.equipoTipoEquipo);
        equipoMarca= (TextView)findViewById(R.id.equipoMarca);
        equipoModelo= (TextView)findViewById(R.id.equipoModelo);
        equipoSerial= (TextView)findViewById(R.id.equipoSerial);
        equipoDescripcion= (TextView)findViewById(R.id.equipoDescripcion);
        equipoProveedorEquipo= (TextView)findViewById(R.id.equipoProveedorEquipo);
        actividad_laborRealizada= (TextView)findViewById(R.id.actividad_laborRealizada);
        actividad_UsuarioRegistra= (TextView)findViewById(R.id.actividad_UsuarioRegistra);
        builder = new AlertDialog.Builder(this);
        spinnerMovimientosEquipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MvtoCarbon_ListadoEquipos Objeto =Listado_mvtoCarbon_ListadoEquipos.get(spinnerMovimientosEquipos.getSelectedItemPosition());
                if(Objeto != null){
                    placaSeleccionada.setText(""+Objeto.getMvtoCarbon().getPlaca());
                    articulo.setText("" + Objeto.getMvtoCarbon().getArticulo().getDescripcion());
                    cliente.setText("" + Objeto.getMvtoCarbon().getCliente().getDescripcion());
                    transportadora.setText("" + Objeto.getMvtoCarbon().getTransportadora().getDescripcion());
                    //orden.setText("ORDEN: " + Objeto.getMvtoCarbon().geto());
                    deposito.setText("" + Objeto.getMvtoCarbon().getDeposito());
                    pesoLleno.setText("" + Objeto.getMvtoCarbon().getPesoVacio());
                    fechaTara.setText("" + Objeto.getMvtoCarbon().getFechaEntradaVehiculo());
                    fechaInicioDescargue.setText("" + Objeto.getMvtoCarbon().getFechaInicioDescargue());
                    centroOperacion.setText("" + Objeto.getMvtoCarbon().getCentroOperacion().getDescripcion());
                    subcentroCosto.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                    auxCentroCosto.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliar().getDescripcion());
                    text_BodegaDestino.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliarDestino().getDescripcion());
                    text_laborRealizada.setText("" + Objeto.getMvtoCarbon().getLaborRealizada().getDescripcion());

                    equipoCodigo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getCodigo());
                    equipoTipoEquipo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
                    equipoMarca.setText(""+Objeto.getAsignacionEquipo().getEquipo().getMarca());
                    equipoModelo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getModelo());
                    equipoSerial.setText(""+Objeto.getAsignacionEquipo().getEquipo().getSerial());
                    equipoDescripcion.setText(""+Objeto.getAsignacionEquipo().getEquipo().getDescripcion());
                    equipoProveedorEquipo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getProveedorEquipo().getDescripcion());
                    actividad_laborRealizada.setText(""+Objeto.getMvtoEquipo().getLaborRealizada().getDescripcion());
                    actividad_UsuarioRegistra.setText(""+Objeto.getMvtoEquipo().getUsuarioQuieRegistra().getNombres()+" "+Objeto.getMvtoEquipo().getUsuarioQuieRegistra().getApellidos());

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //ArrayList<MvtoCarbon_ListadoEquipos> Listado_mvtoCarbon_ListadoEquipos=null;
        Listado_mvtoCarbon_ListadoEquipos=(ArrayList<MvtoCarbon_ListadoEquipos>) getIntent().getExtras().getSerializable("Listado_mvtoCarbon_ListadoEquipos");
        if(Listado_mvtoCarbon_ListadoEquipos!=null) {
            ArrayList<String> arrayListado =new ArrayList<>();
            for (MvtoCarbon_ListadoEquipos listadoObjeto : Listado_mvtoCarbon_ListadoEquipos) {
                arrayListado.add(

                                "__________________________________\n" +
                                "MOVIMIENTO EQUIPO # "+listadoObjeto.getMvtoEquipo().getCodigo()+"\n"+
                                "DATOS DEL VEHÍCULO DESCARGADO \n"+
                                         "  PLACA: \n     ==>"+listadoObjeto.getMvtoCarbon().getPlaca()+"\n"+
                                         "  ARTICULO: \n     ==>"+listadoObjeto.getMvtoCarbon().getArticulo().getDescripcion()+"\n"+
                                         "  CLIENTE: \n     ==>"+listadoObjeto.getMvtoCarbon().getCliente().getDescripcion()+"\n"+
                                         //"  PESO VACIO: "+listadoObjeto.getMvtoCarbon().getPesoVacio()+"\n"+
                                         //"  PESO LLENO: "+listadoObjeto.getMvtoCarbon().getPesoLleno()+"\n"+
                                         //"  PESO NETO: "+listadoObjeto.getMvtoCarbon().getPesoNeto()+"\n"+
                                         "  FECHA_INICIO DESCARGUE: \n     ==>"+listadoObjeto.getMvtoCarbon().getFechaInicioDescargue()+"\n"+
                                         "  SUBCENTRO COSTO: \n     ==>"+listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion()+"\n"+
                                         "  AUXILIAR CENTRO COSTO: \n     ==>"+listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getDescripcion()+"\n"+
                                 "DATOS DEL EQUIPO\n"+
                                        "  CÓDIGO: \n     ==>"+listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo()+"\n"+
                                        "  EQUIPO: \n     ==>"+listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion()+""+
                                        " "+listadoObjeto.getAsignacionEquipo().getEquipo().getMarca()+""+
                                        " "+listadoObjeto.getAsignacionEquipo().getEquipo().getModelo()+""+
                                        " "+listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion()+"\n"+
                                        "  LABOR REALIZADA: \n     ==>"+listadoObjeto.getMvtoEquipo().getLaborRealizada().getDescripcion()+"\n"+
                                        "DATOS DEL USUARIO QUIEN INICIO ACTIVIDAD\n"+
                                        "  CÓDIGO: \n     ==>"+listadoObjeto.getMvtoEquipo().getUsuarioQuieRegistra().getCodigo()+"\n"+
                                        "  NOMBE: \n     ==>"+listadoObjeto.getMvtoEquipo().getUsuarioQuieRegistra().getNombres()+" "+listadoObjeto.getMvtoEquipo().getUsuarioQuieRegistra().getApellidos()
                        );
            }

            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
            spinnerMovimientosEquipos.setAdapter(adaptadorListado);
        }else{
            System.out.println("Asignacion nula");
        }
        bntIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Listado_mvtoCarbon_ListadoEquipos !=null) {
                    MvtoCarbon_ListadoEquipos Objeto = Listado_mvtoCarbon_ListadoEquipos.get(spinnerMovimientosEquipos.getSelectedItemPosition());
                    if (Objeto != null) {
                        if (listadoMotivoParada != null) {
                            MotivoParada motivoParada = listadoMotivoParada.get(spinnerRazónFinalización.getSelectedItemPosition());
                            int result = 0;
                            try {
                                Objeto.getMvtoEquipo().setUsuarioQuienCierra(user);
                                result = new ControlDB_Carbon(typeConnection).mvtoCarbon_cerrarCiclo_mtvoEquipo(Objeto, user, motivoParada);
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
                                builder.setMessage("Se finalizó la Actividad realizada por el equipo de forma exitosa");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        placaSeleccionada.setText("");
                                        articulo.setText("");
                                        cliente.setText("");
                                        transportadora.setText("");
                                        orden.setText("");
                                        deposito.setText("");
                                        pesoLleno.setText("");
                                        fechaTara.setText("");
                                        fechaInicioDescargue.setText("");
                                        centroOperacion.setText("");
                                        subcentroCosto.setText("");
                                        auxCentroCosto.setText("");
                                        text_BodegaDestino.setText("");
                                        text_laborRealizada.setText("");

                                        equipoCodigo.setText("");
                                        equipoTipoEquipo.setText("");
                                        equipoMarca.setText("");
                                        equipoModelo.setText("");
                                        equipoSerial.setText("");
                                        equipoDescripcion.setText("");
                                        equipoProveedorEquipo.setText("");
                                        actividad_laborRealizada.setText("");
                                        actividad_UsuarioRegistra.setText("");
                                        //codigoEquipoS = Objeto.getMvtoEquipo().getCodigo();
                                        Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Menu.class);
                                        interfaz.putExtra("usuarioB",  user);
                                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                                        startActivity(interfaz);

                                        //cargueInfo("cargarMovimientos");
                                    }}
                                    );
                                builder.show();
                            } else {
                                //&mensaje("Error al tratar de cerrar el ciclo del equipo");
                                builder.setTitle("Error!!");
                                builder.setMessage("No se pudo cerrar el ciclo del equipo..");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            //mensaje("Se debe cargar un Motivo de Finalización");
                            builder.setTitle("Error!!");
                            builder.setMessage("Se debe cargar un Motivo de Finalización..");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                }
                            });
                            builder.show();
                        }
                    }
                }else{
                    //mensaje("Debe Escanear un Equipo");
                    builder.setTitle("Alerta!!");
                    builder.setMessage("Debe Escanear un Equipo..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
            }
        });

        bntAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Menu.class);
                interfaz.putExtra("usuarioB",  user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
            }
        });

    }
    public void cargue_info (ArrayList<String> arrayListado){
        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
        spinnerMovimientosEquipos.setAdapter(adaptadorListado);
        MvtoCarbon_ListadoEquipos Objeto =Listado_mvtoCarbon_ListadoEquipos.get(spinnerMovimientosEquipos.getSelectedItemPosition());
        if(Objeto != null){
            placaSeleccionada.setText(""+Objeto.getMvtoCarbon().getPlaca());
            articulo.setText("" + Objeto.getMvtoCarbon().getArticulo().getDescripcion());
            cliente.setText("" + Objeto.getMvtoCarbon().getCliente().getDescripcion());
            transportadora.setText("" + Objeto.getMvtoCarbon().getTransportadora().getDescripcion());
            //orden.setText("ORDEN: " + Objeto.getMvtoCarbon().geto());
            deposito.setText("" + Objeto.getMvtoCarbon().getDeposito());
            pesoLleno.setText("" + Objeto.getMvtoCarbon().getPesoVacio());
            fechaTara.setText("" + Objeto.getMvtoCarbon().getFechaEntradaVehiculo());
            fechaInicioDescargue.setText("" + Objeto.getMvtoCarbon().getFechaInicioDescargue());
            centroOperacion.setText("" + Objeto.getMvtoCarbon().getCentroOperacion().getDescripcion());
            subcentroCosto.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
            auxCentroCosto.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliar().getDescripcion());
            text_BodegaDestino.setText("" + Objeto.getMvtoCarbon().getCentroCostoAuxiliarDestino().getDescripcion());
            text_laborRealizada.setText("" + Objeto.getMvtoCarbon().getLaborRealizada().getDescripcion());

            equipoCodigo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getCodigo());
            equipoTipoEquipo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion());
            equipoMarca.setText(""+Objeto.getAsignacionEquipo().getEquipo().getMarca());
            equipoModelo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getModelo());
            equipoSerial.setText(""+Objeto.getAsignacionEquipo().getEquipo().getSerial());
            equipoDescripcion.setText(""+Objeto.getAsignacionEquipo().getEquipo().getDescripcion());
            equipoProveedorEquipo.setText(""+Objeto.getAsignacionEquipo().getEquipo().getProveedorEquipo().getDescripcion());
            actividad_laborRealizada.setText(""+Objeto.getMvtoEquipo().getLaborRealizada().getDescripcion());
            actividad_UsuarioRegistra.setText(""+Objeto.getMvtoEquipo().getUsuarioQuieRegistra().getNombres()+" "+Objeto.getMvtoEquipo().getUsuarioQuieRegistra().getApellidos());

        }
    };
    public void cargueInfo(String opcion){
        switch (opcion){
            case "cargar_razonesFinalizacón":{
                ArrayList<String> arrayListado =new ArrayList<>();
                try {
                    ControlDB_MotivoParada ControlDB_MotivoParada = new ControlDB_MotivoParada(typeConnection);
                    listadoMotivoParada = ControlDB_MotivoParada.buscarActivos();
                    if(listadoMotivoParada != null) {
                        for (MotivoParada listadoObjeto : listadoMotivoParada) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    }
                }catch (SQLException e){
                    //mensaje("No se pudo consultar los Motivos de Parada");
                    builder.setTitle("Error!!");
                    builder.setMessage("No se pudo consultar los Motivos de Parada..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinnerRazónFinalización.setAdapter(adaptadorListado);
                break;
            }
            case "cargarMovimientos":{
                Equipo equipo = new Equipo();
                equipo.setCodigo(codigoEquipoS);
                ArrayList<MvtoCarbon_ListadoEquipos> listado_mvtoCarbon_ListadoEquipos= null;
                try {
                    listado_mvtoCarbon_ListadoEquipos = new ControlDB_Carbon(typeConnection).buscarEquipo_EnMovimientoCarbon_Activos(equipo,zonaTrabajoSeleccionada);
                    System.out.println("====================================================>"+codigoEquipoS);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Listado_mvtoCarbon_ListadoEquipos=listado_mvtoCarbon_ListadoEquipos;
                ArrayList<String> arrayListado = new ArrayList<>();
                if(Listado_mvtoCarbon_ListadoEquipos !=null) {
                    for (MvtoCarbon_ListadoEquipos listadoObjeto : Listado_mvtoCarbon_ListadoEquipos) {
                        arrayListado.add(

                                "__________________________________\n" +
                                        "MOVIMIENTO EQUIPO # " + listadoObjeto.getMvtoEquipo().getCodigo() + "\n" +
                                        "DATOS DEL VEHÍCULO DESCARGADO \n" +
                                        "  PLACA: " + listadoObjeto.getMvtoCarbon().getPlaca() + "\n" +
                                        "  ARTICULO: " + listadoObjeto.getMvtoCarbon().getArticulo().getDescripcion() + "\n" +
                                        "  CLIENTE: " + listadoObjeto.getMvtoCarbon().getCliente().getDescripcion() + "\n" +
                                        //"  PESO VACIO: "+listadoObjeto.getMvtoCarbon().getPesoVacio()+"\n"+
                                        //"  PESO LLENO: "+listadoObjeto.getMvtoCarbon().getPesoLleno()+"\n"+
                                        //"  PESO NETO: "+listadoObjeto.getMvtoCarbon().getPesoNeto()+"\n"+
                                        "  FECHA_INICIO DESCARGUE: " + listadoObjeto.getMvtoCarbon().getFechaInicioDescargue() + "\n" +
                                        "  SUBCENTRO COSTO: " + listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + "\n" +
                                        "  AUXILIAR CENTRO COSTO: " + listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getDescripcion() + "\n" +
                                        "DATOS DEL EQUIPO\n" +
                                        "  CÓDIGO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo() + "\n" +
                                        "  EQUIPO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion() + "" +
                                        " " + listadoObjeto.getAsignacionEquipo().getEquipo().getMarca() + "" +
                                        " " + listadoObjeto.getAsignacionEquipo().getEquipo().getModelo() + "" +
                                        " " + listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion() + "\n" +
                                        "  LABOR REALIZADA: " + listadoObjeto.getMvtoEquipo().getLaborRealizada().getDescripcion());
                    }
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinnerMovimientosEquipos.setAdapter(adaptadorListado);
                break;
            }
            case "list_tipoEquipo":{
                try {
                    ArrayList<String> arrayListadoT =new ArrayList<>();
                    listadoTipoEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoCarbonActivosTipoEquipo(zonaTrabajoSeleccionada);
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
                        listadoMarcaEquipo = new ControlDB_Equipo(typeConnection).buscar_MvtoEquipoCarbonMarcaEquipo(listadoTipoEquipo.get(spinnerTipoEquipo.getSelectedItemPosition()).getCodigo(),zonaTrabajoSeleccionada);
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
                        listadoEquipos = new ControlDB_Equipo(typeConnection).buscar_MvtoCarbonActivosEquipos(equipo,zonaTrabajoSeleccionada);
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
    public void mensaje(String mensaje){
        int toastDuration = 10000; // in MilliSeconds
        final Toast mToast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        CountDownTimer countDownTimer= new CountDownTimer(toastDuration, 1000) {
            public void onTick(long millisUntilFinished) { mToast.show(); }
            public void onFinish(){ mToast.cancel(); } }; mToast.show(); countDownTimer.start();
    }
    public void esperarYCerrar(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos

            }
        }, milisegundos);
    }
    public void EscannerQR(View view){
        escanerView= new ZXingScannerView(this);
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
                ArrayList<MvtoCarbon_ListadoEquipos> listado_MvtoEquipo = null;
                try {
                    listado_MvtoEquipo = new ControlDB_Carbon(typeConnection).buscarEquipo_EnMovimientoCarbon_Activos(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()).getAsignacionEquipo().getEquipo(),zonaTrabajoSeleccionada);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (listado_MvtoEquipo != null) {
                    Listado_MvtoEquipo = listado_MvtoEquipo;
                    Listado_mvtoCarbon_ListadoEquipos=listado_MvtoEquipo;
                    if (Listado_MvtoEquipo != null) {
                        ArrayList<String> arrayListado = new ArrayList<>();
                        for (MvtoCarbon_ListadoEquipos listadoObjeto : listado_MvtoEquipo) {
                            arrayListado.add(
                                    "__________________________________\n" +
                                            "MOVIMIENTO EQUIPO # " + listadoObjeto.getMvtoEquipo().getCodigo() + "\n" +
                                            "DATOS DEL VEHÍCULO DESCARGADO \n" +
                                            "  PLACA: " + listadoObjeto.getMvtoCarbon().getPlaca() + "\n" +
                                            "  ARTICULO: " + listadoObjeto.getMvtoCarbon().getArticulo().getDescripcion() + "\n" +
                                            "  CLIENTE: " + listadoObjeto.getMvtoCarbon().getCliente().getDescripcion() + "\n" +
                                            //"  PESO VACIO: "+listadoObjeto.getMvtoCarbon().getPesoVacio()+"\n"+
                                            //"  PESO LLENO: "+listadoObjeto.getMvtoCarbon().getPesoLleno()+"\n"+
                                            //"  PESO NETO: "+listadoObjeto.getMvtoCarbon().getPesoNeto()+"\n"+
                                            "  FECHA_INICIO DESCARGUE: " + listadoObjeto.getMvtoCarbon().getFechaInicioDescargue() + "\n" +
                                            "  SUBCENTRO COSTO: " + listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + "\n" +
                                            "  AUXILIAR CENTRO COSTO: " + listadoObjeto.getMvtoCarbon().getCentroCostoAuxiliar().getDescripcion() + "\n" +
                                            "DATOS DEL EQUIPO\n" +
                                            "  CÓDIGO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getCodigo() + "\n" +
                                            "  EQUIPO: " + listadoObjeto.getAsignacionEquipo().getEquipo().getTipoEquipo().getDescripcion() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getMarca() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getModelo() + "" +
                                            " " + listadoObjeto.getAsignacionEquipo().getEquipo().getDescripcion() + "\n" +
                                            "  LABOR REALIZADA: " + listadoObjeto.getMvtoEquipo().getLaborRealizada().getDescripcion());
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
                            Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Modulo_equipo_finalizar_ciclo_equipo.class);
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
        String codigoEquipo=rawResult.getText();
        try {
            Equipo equipo = new Equipo();
            equipo.setCodigo(codigoEquipo);
            ArrayList<MvtoCarbon_ListadoEquipos> listado_mvtoCarbon_ListadoEquipos= new ControlDB_Carbon(typeConnection).buscarEquipo_EnMovimientoCarbon_Activos(equipo,zonaTrabajoSeleccionada);
            if(listado_mvtoCarbon_ListadoEquipos !=null){
                Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Modulo_carbon_finalizar_ciclo_equipo.class);
                interfaz.putExtra("Listado_mvtoCarbon_ListadoEquipos",  listado_mvtoCarbon_ListadoEquipos);
                interfaz.putExtra("usuarioB",  user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
                onPause();
            }else{
                //mensaje("El equipo escaneado no tiene actividades pendientes");
                builder.setTitle("Alerta!!");
                builder.setMessage("El equipo escaneado no tiene actividades pendientes..");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Modulo_carbon_finalizar_ciclo_equipo.class);
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
        escanerView= new ZXingScannerView(this);
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
                    Intent interfaz = new Intent(Modulo_carbon_finalizar_ciclo_equipo.this, Menu.class);
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
