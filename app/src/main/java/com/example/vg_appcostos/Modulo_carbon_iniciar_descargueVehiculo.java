package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.EstadoMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Recobro;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.google.zxing.Result;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Modulo_carbon_iniciar_descargueVehiculo extends AppCompatActivity  implements Serializable, ZXingScannerView.ResultHandler{
    boolean validadorPresionaBotonIniciar=true;
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    AsignacionEquipo asignacion=null;
    Spinner spinnerSubCentroCosto,spinnerAuxiliarCentroCosto,spinnerAuxiliarCentroCostoDestino, spinnerCentroOperacion, spinnerlaborRealizar,spinnerRecobroCliente;
    ArrayList<Recobro> listadoRecobro;
    Spinner spinnerTipoEquipo,spinnerMarcaEquipo,spinnerEquipo;
    TextView usuarioCodigo,usuarioNombre,articulo,cliente,transportadora,orden,deposito, peso,fechaTara,placaS,textView_BodegaDestino,textView_BodegaOrigen;
    MvtoCarbon Objeto;
    MvtoEquipo mvtoEquipo;
    Button Btn_Iniciar,Btn_Atras;
    Recobro recobroCliente;
    ArrayList<MvtoCarbon> listadoMvtoCarbon;
    ArrayList<LaborRealizada> listadoLaborRealizada;
    EditText placa;
    ImageView recargarPlaca;

    //ArrayList de selecciones
    ArrayList<CentroCostoSubCentro> listadoCentroCostosSubCentro;
    ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar;
    ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliarDestino;
    ArrayList<CentroOperacion> listadoCentroOperacion;
    ArrayList<TipoEquipo> listadoTipoEquipo;
    ArrayList<String> listadoMarcaEquipo;
    ArrayList<AsignacionEquipo> listadoEquipos;
    AlertDialog.Builder builder;
    String typeConnection="";
    TextView equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoSerial,equipoDescripcion,equipoProveedorEquipo
            ,asignacion_CentroOperacion,asignacion_SubcentroCosto,asignacion_auxCentroCosto,asignacion_FechaInicio,asignacion_FechaFin,asignacion_CantidadHorasProgram;
    private ZXingScannerView escanerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_carbon_iniciar_descargue_vehiculo);
        //Inicializamos los ArrayList

        listadoCentroCostosSubCentro= new ArrayList<>();
        listadoCentroCostoAuxiliar= new ArrayList<>();
        listadoCentroCostoAuxiliarDestino= new ArrayList<>();
        listadoCentroOperacion= new ArrayList<>();
        listadoLaborRealizada= new ArrayList<>();
        listadoRecobro= new ArrayList<>();
        listadoTipoEquipo= new ArrayList<>();
        listadoMarcaEquipo= new ArrayList<>();
        listadoEquipos= new ArrayList<>();
        recobroCliente= null;
        mvtoEquipo = null;
        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");

        usuarioCodigo = (TextView)findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView)findViewById(R.id.usuarioNombre);
        articulo= (TextView)findViewById(R.id.articulo);
        cliente= (TextView)findViewById(R.id.cliente);
        transportadora= (TextView)findViewById(R.id.transportadora);
        orden= (TextView)findViewById(R.id.orden);
        deposito= (TextView)findViewById(R.id.deposito);
        peso= (TextView)findViewById(R.id.peso);
        fechaTara= (TextView)findViewById(R.id.fechaTara);
        placaS= (TextView)findViewById(R.id.placaS);
        textView_BodegaDestino= (TextView)findViewById(R.id.textView_BodegaDestino);
        textView_BodegaOrigen= (TextView)findViewById(R.id.textView_BodegaOrigen);

        Btn_Iniciar= (Button)findViewById(R.id.Btn_Iniciar);
        Btn_Atras= (Button)findViewById(R.id.Btn_Atras);

        placa = (EditText)findViewById(R.id.placa);
        recargarPlaca= (ImageView)findViewById(R.id.recargarPlaca);

        usuarioCodigo.setText(user.getCodigo());
        usuarioNombre.setText(user.getNombres()+" "+user.getApellidos());

        spinnerSubCentroCosto= (Spinner)findViewById(R.id.spinnerSubCentroCosto);
        spinnerAuxiliarCentroCosto= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCosto);
        spinnerAuxiliarCentroCostoDestino= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCostoDestino);
        spinnerCentroOperacion= (Spinner)findViewById(R.id.spinnerCentroOperacion);
        spinnerlaborRealizar= (Spinner)findViewById(R.id.spinnerlaborRealizar);
        spinnerRecobroCliente= (Spinner)findViewById(R.id.spinnerRecobroCliente);

        spinnerTipoEquipo= (Spinner)findViewById(R.id.spinnerTipoEquipo);
        spinnerMarcaEquipo= (Spinner)findViewById(R.id.spinnerMarcaEquipo);
        spinnerEquipo= (Spinner)findViewById(R.id.spinnerEquipo);


        //Equipo Y Asignacion
        equipoCodigo= (TextView)findViewById(R.id.equipoCodigo);
        equipoTipoEquipo= (TextView)findViewById(R.id.equipoTipoEquipo);
        equipoMarca= (TextView)findViewById(R.id.equipoMarca);
        equipoModelo= (TextView)findViewById(R.id.equipoModelo);
        equipoSerial= (TextView)findViewById(R.id.equipoSerial);
        equipoDescripcion= (TextView)findViewById(R.id.equipoDescripcion);
        equipoProveedorEquipo= (TextView)findViewById(R.id.equipoProveedorEquipo);
        asignacion_CentroOperacion= (TextView)findViewById(R.id.asignacion_CentroOperacion);
        asignacion_SubcentroCosto= (TextView)findViewById(R.id.asignacion_SubcentroCosto);
        asignacion_auxCentroCosto= (TextView)findViewById(R.id.asignacion_auxCentroCosto);
        asignacion_FechaInicio= (TextView)findViewById(R.id.asignacion_FechaInicio);
        asignacion_FechaFin= (TextView)findViewById(R.id.asignacion_FechaFin);
        asignacion_CantidadHorasProgram= (TextView)findViewById(R.id.asignacion_CantidadHorasProgram);

        textView_BodegaDestino.setVisibility(View.INVISIBLE);
        spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);
        //cargueInfo("Placa");
        builder = new AlertDialog.Builder(this);

        cargarAsignacion();

        spinnerCentroOperacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("SubCentro_Costo");
                cargueInfo("AuxiliarCentro_Costo");
                cargueInfo("carga_listadoLaboresRealizada");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSubCentroCosto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listadoCentroCostosSubCentro !=null) {
                    cargueInfo("AuxiliarCentro_Costo");
                    cargueInfo("carga_listadoLaboresRealizada");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerlaborRealizar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listadoCentroCostosSubCentro !=null) {
                    if (listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")) {
                        textView_BodegaOrigen.setText("C.C. AUXILIAR ORIGEN");
                        textView_BodegaDestino.setVisibility(View.VISIBLE);
                        spinnerAuxiliarCentroCostoDestino.setVisibility(View.VISIBLE);
                    } else {
                        textView_BodegaDestino.setVisibility(View.INVISIBLE);
                        spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cargueInfo("C.O");
        //cargueInfo("SubCentro_Costo");
        //cargueInfo("AuxiliarCentro_Costo");
        //cargueInfo("carga_listadoLaboresRealizada");
        cargueInfo("list_recobro");

        mvtoEquipo = (MvtoEquipo) getIntent().getExtras().getSerializable("mvtoEquipo");
        if(mvtoEquipo !=null){
            if(listadoCentroOperacion !=null){
                int contador =0;
                for(CentroOperacion Objeto2: listadoCentroOperacion){
                    if(Objeto2.getCodigo() == mvtoEquipo.getCentroOperacion().getCodigo()){
                        spinnerCentroOperacion.setSelection(contador);
                    }
                    contador++;
                }
            }
            if(listadoCentroCostosSubCentro !=null){
                int contador =0;
                for(CentroCostoSubCentro Objeto2: listadoCentroCostosSubCentro){
                    if(Objeto2.getCodigo() == mvtoEquipo.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()){
                        spinnerSubCentroCosto.setSelection(contador);
                    }
                    contador++;
                }
            }
            if(listadoCentroCostoAuxiliar !=null){
                int contador =0;
                for(CentroCostoAuxiliar Objeto2: listadoCentroCostoAuxiliar){
                    if(Objeto2.getCodigo() == mvtoEquipo.getCentroCostoAuxiliar().getCodigo()){
                        spinnerAuxiliarCentroCosto.setSelection(contador);
                    }
                    contador++;
                }
            }
            /*if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                    textView_BodegaOrigen.setText("C.C. AUXILIAR ORIGEN");
                    textView_BodegaDestino.setVisibility(View.VISIBLE);
                    spinnerAuxiliarCentroCostoDestino.setVisibility(View.VISIBLE);
                }else{
                    textView_BodegaDestino.setVisibility(View.INVISIBLE);
                    spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);
                }*/
            /*if(listadoCentroCostoAuxiliarDestino !=null){
                if(mvtoEquipo.getCentroCostoAuxiliarDestino() != null){
                    int contador =0;
                    for(CentroCostoAuxiliar Objeto2: listadoCentroCostoAuxiliarDestino){
                        if(Objeto2.getCodigo() == mvtoEquipo.getCentroCostoAuxiliarDestino().getCodigo()){
                            spinnerAuxiliarCentroCostoDestino.setSelection(contador);
                        }
                        contador++;
                    }
                    textView_BodegaOrigen.setText("C.C. AUXILIAR ORIGEN");
                    textView_BodegaDestino.setVisibility(View.VISIBLE);
                    spinnerAuxiliarCentroCostoDestino.setVisibility(View.VISIBLE);
                    mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                }
            }*/

            mvtoEquipo.setCentroOperacion(listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()));
            //mvtoEquipo.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));
            //mvtoEquipo.getCentroCostoAuxiliar().setCentroCostoSubCentro(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
            mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
            //if()
        }
        recobroCliente =(Recobro) getIntent().getExtras().getSerializable("recobroCliente");
        if(recobroCliente !=null){
            if(recobroCliente.getDescripcion().equals("SI")){
                spinnerRecobroCliente.setSelection(1);
            }else{
                spinnerRecobroCliente.setSelection(0);
            }
        }
        Objeto = (MvtoCarbon) getIntent().getExtras().getSerializable("Objeto_MvtoCarbon");
        if(Objeto !=null){//Sacamos la información del Objeto Carbón.
            placaS.setText("" + Objeto.getPlaca());
            articulo.setText("" + Objeto.getArticulo().getDescripcion());
            cliente.setText("" + Objeto.getCliente().getDescripcion());
            transportadora.setText("" + Objeto.getTransportadora().getDescripcion());
            deposito.setText("" + Objeto.getDeposito());
            peso.setText("" + Objeto.getPesoVacio());
            fechaTara.setText("" + Objeto.getFechaEntradaVehiculo());
        }else{
            System.out.println("Objeto Movimiento carbon nulo");
        }


        Btn_Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validadorPresionaBotonIniciar) {
                    validadorPresionaBotonIniciar=false;
                    if (asignacion != null) {
                        if (new ControlDB_Carbon(typeConnection).validarStandByPendiente(asignacion.getEquipo().getCodigo())) {//El equipo tiene Standy BY pendientes, por tal motivo no deja registrar nuevas actividades
                            builder = new AlertDialog.Builder(builder.getContext());
                            builder.setTitle("Advertencia!!");
                            builder.setMessage("El siguiente equipo:\n"
                                    + "Código: " + asignacion.getEquipo().getCodigo() + "\n"
                                    + "Descripción:" + asignacion.getEquipo().getDescripcion() + " " + asignacion.getEquipo().getModelo() + "\n Se encuentra con un Stand By pendiente, debe cerrarlo para poder hacer nuevos registros.\nDesea cerrar el Standy By?");
                            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Modulo_Equipo_cierre_StandBy.class);
                                    interfaz.putExtra("usuarioB", user);
                                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                    interfaz.putExtra("codigoEquipoT", "" + asignacion.getEquipo().getCodigo());
                                    interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                    startActivity(interfaz);
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    validadorPresionaBotonIniciar=true;
                                }
                            });
                            builder.show();
                        } else {                      //El equipo no tiene StandBy pendientes, por tal motivo se puede seguir con el registrar
                            if (placaS.getText().equals("")) {                      //No se ha seleccionado una placa en transito
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Alerta!!");
                                builder.setMessage("Debe Seleccionar una Placa..");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.show();
                            } else {                  //El usuario ya cago la placa que se encuentra en transito
                                //ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                                if (listadoCentroOperacion == null || listadoCentroCostoAuxiliar == null || listadoLaborRealizada == null) {  //Valida si se cargo un CentroOperacion, AuxiliarCentroCosto,LaborRealizada
                                    builder = new AlertDialog.Builder(builder.getContext());
                                    builder.setTitle("Alerta!!");
                                    builder.setMessage("Verifique que tenga cargado un Centro de Operación, Sitio Origen (CentroCosto Auxiliar) y una actividad a Realizar");
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            validadorPresionaBotonIniciar=true;
                                        }
                                    });
                                    builder.show();
                                } else {                  //El usuario ya cargo un Centro_Operación, CentroCosto_Auxiliar y una labor_Realizada
                                    Objeto.setCentroOperacion(listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()));
                                    Objeto.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));

                                    if (Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo() == 1) {//Se selecciono  Subcentro de Costo Recibo por tal motivo el lavado de vehículo es SI
                                        //Objeto.setLavadoVehiculo("1");
                                        Objeto.setLavadoVehiculo("NULL" +
                                                "");
                                        Objeto.setObservacion("NULL");
                                    } else {
                                        Objeto.setLavadoVehiculo("NULL");
                                        Objeto.setObservacion("NULL");
                                    }
                                    //Articulo ya se encuentra registrado
                                    //Cliente ya se encuentra registrado
                                    //Transportadora ya se encuentra registrada
                                    Objeto.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                    Objeto.setFechaRegistro("(SELECT SYSDATETIME())");
                                    //Numero de orden ya se encuentra registrado
                                    //Deposito ya se encuentra registrado
                                    Objeto.setConsecutivo("''");
                                    Objeto.setPlaca(placaS.getText().toString());
                                    //Peso Vacio Ya se encuientra registrada
                                    Objeto.setPesoLleno("0");
                                    Objeto.setPesoNeto("0");
                                    //Fecha Entrada ya se encuentra registrada
                                    //Objeto.setFechaEntradaVehiculo("'" + Objeto.getFechaEntradaVehiculo() + "'");
                                    //Cargamos fecha de salida NULL, porque se debe de cargar una vez el vehiculo pase en ccarga a tiquete
                                    Objeto.setFechaInicioDescargue("(SELECT SYSDATETIME())");
                                    //Objeto.setFechaFinDescargue("NULL");
                                    Objeto.setUsuarioRegistroMovil(user);
                                    Objeto.setObservacion("''");
                                    Objeto.setEstadoMvtoCarbon(new EstadoMvtoCarbon("1", "ACTIVO", "1"));
                                    Objeto.setConexionPesoCcarga("0");

                                    if (mvtoEquipo == null) {
                                        mvtoEquipo = new MvtoEquipo();
                                    }
                                    mvtoEquipo.setCentroOperacion(listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()));
                                    mvtoEquipo.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));
                                    mvtoEquipo.getCentroCostoAuxiliar().setCentroCostoSubCentro(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));

                                    mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                    if (listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")) {
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                        Objeto.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                    } else {
                                        CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                        ccAuxiliar.setCodigo("NULL");
                                        Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                    }
                                    mvtoEquipo.setAsignacionEquipo(asignacion);
                                    mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                                    mvtoEquipo.setNumeroOrden("");
                                    mvtoEquipo.setCliente(Objeto.getCliente());
                                    mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                    mvtoEquipo.setUsuarioQuieRegistra(user);
                                    mvtoEquipo.setEstado("1");
                                    mvtoEquipo.setDesdeCarbon("1");
                                    if (listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()).getCodigo() == asignacion.getCentroOperacion().getCodigo() &&
                                            listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getCodigo() == asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCodigo() &&
                                            listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getCodigo() == asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()) {
                                        if (new ControlDB_Carbon(typeConnection).validarExistenciaMvtoCarbon(Objeto)) {//La placa ya fue registrada con la misma fecha de tara
                                            if (new ControlDB_Carbon(typeConnection).validarVehiculoEnTransitoMvtoCarbon(Objeto)) {
                                                builder = new AlertDialog.Builder(builder.getContext());
                                                builder.setTitle("Advertencia!!");
                                                builder.setMessage("La placa " + Objeto.getPlaca() + " actualmente se encuentra en transito, debe completar el ciclo para registrarla nuevamente");
                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        validadorPresionaBotonIniciar=true;
                                                    }
                                                });
                                                builder.show();
                                            } else {
                                                //Validamos si la programación se venció
                                                AsignacionEquipo ValidarTiempoAsignacionEquipo = null;
                                                try {
                                                    ValidarTiempoAsignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(mvtoEquipo.getAsignacionEquipo().getEquipo());
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                if (ValidarTiempoAsignacionEquipo == null) {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Advertencia!!");
                                                    builder.setMessage("El equipo Seleccionado se le venció la programación, deben hacer una nueva programación");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                            validadorPresionaBotonIniciar=true;
                                                        }
                                                    });
                                                    builder.show();
                                                } else {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Advertencia !!");
                                                    builder.setMessage("La placa Seleccionada ya fue registrada, debe ingresar una placa diferente..");
                                                    builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                            validadorPresionaBotonIniciar=true;
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                                    /*  Código para permitir que se registren la misma placa con la misma tara

                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Advertencia !!");
                                                    builder.setMessage("La placa Seleccionada ya fue registrada desea registrarla nuevamente..");
                                                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            final int[] retorno = {0};
                                                            try {
                                                                retorno[0] = new ControlDB_Carbon(typeConnection).registrarMvtoCarbonCompleto(Objeto, asignacion, user, mvtoEquipo);
                                                            } catch (FileNotFoundException e) {
                                                                e.printStackTrace();
                                                            } catch (UnknownHostException e) {
                                                                e.printStackTrace();
                                                            } catch (SocketException e) {
                                                                e.printStackTrace();
                                                            }

                                                            if (retorno[0] == 1) {
                                                                builder = new AlertDialog.Builder(builder.getContext());
                                                                builder.setTitle("Registro Exitoso!!");
                                                                builder.setMessage("Se registro la Placa " + Objeto.getPlaca() + " de forma exitosa.");
                                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        //spinnerPlaca.setSelection(0);
                                                                        placa.setText("");
                                                                        articulo.setText("");
                                                                        cliente.setText("");
                                                                        transportadora.setText("");
                                                                        orden.setText("");
                                                                        deposito.setText("");
                                                                        peso.setText("");
                                                                        fechaTara.setText("");
                                                                        Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
                                                                        interfaz.putExtra("usuarioB", user);
                                                                        interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                        interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                                        startActivity(interfaz);
                                                                    }
                                                                });
                                                                builder.show();
                                                            } else {
                                                                if (retorno[0] == 2) {
                                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                                    builder.setTitle("Error!!");
                                                                    builder.setMessage("No hay conexión con el servidor valide conexión de red..");
                                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                            validadorPresionaBotonIniciar=true;
                                                                        }
                                                                    });
                                                                    builder.show();
                                                                } else {
                                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                                    builder.setTitle("Error!!");
                                                                    builder.setMessage("No se pudo registrar el inicio de descargue del vehiculo..");
                                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            validadorPresionaBotonIniciar=true;
                                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                        }
                                                                    });
                                                                    builder.show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                            validadorPresionaBotonIniciar=true;
                                                        }
                                                    });
                                                    builder.show();
                                                    */
                                            }
                                        } else {//La placa no ha sido registrada, es la primera vez que se registra con esa fecha de tara
                                            //Validamos si la programación se venció
                                            AsignacionEquipo ValidarTiempoAsignacionEquipo = null;
                                            try {
                                                ValidarTiempoAsignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(mvtoEquipo.getAsignacionEquipo().getEquipo());
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            if (ValidarTiempoAsignacionEquipo == null) {
                                                builder = new AlertDialog.Builder(builder.getContext());
                                                builder.setTitle("Advertencia!!");
                                                builder.setMessage("El equipo Seleccionado se le venció la programación, deben hacer una nueva programación");
                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        validadorPresionaBotonIniciar=true;
                                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                    }
                                                });
                                                builder.show();
                                            } else {
                                                final int[] retorno = {0};
                                                try {
                                                    retorno[0] = new ControlDB_Carbon(typeConnection).registrarMvtoCarbonCompleto(Objeto, asignacion, user, mvtoEquipo);
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                } catch (SocketException e) {
                                                    e.printStackTrace();
                                                }

                                                if (retorno[0] == 1) {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Registro Exitoso!!");
                                                    builder.setMessage("Se registro la Placa " + Objeto.getPlaca() + " de forma exitosa.");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //spinnerPlaca.setSelection(0);
                                                            placa.setText("");
                                                            articulo.setText("");
                                                            cliente.setText("");
                                                            transportadora.setText("");
                                                            orden.setText("");
                                                            deposito.setText("");
                                                            peso.setText("");
                                                            fechaTara.setText("");
                                                            Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
                                                            interfaz.putExtra("usuarioB", user);
                                                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                            interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                            startActivity(interfaz);
                                                        }
                                                    });
                                                    builder.show();
                                                } else {
                                                    if (retorno[0] == 2) {
                                                        builder = new AlertDialog.Builder(builder.getContext());
                                                        builder.setTitle("Error!!");
                                                        builder.setMessage("No hay conexión con el servidor valide conexión de red..");
                                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                validadorPresionaBotonIniciar=true;
                                                            }
                                                        });
                                                        builder.show();
                                                    } else {
                                                        builder = new AlertDialog.Builder(builder.getContext());
                                                        builder.setTitle("Error!!");
                                                        builder.setMessage("No se pudo registrar el inicio de descargue del vehiculo..");
                                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                validadorPresionaBotonIniciar=true;
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Advertencia!!");
                                        builder.setMessage("El equipo está programado para trabajar en: " +
                                                "\nCentro Operación: " + asignacion.getCentroOperacion().getDescripcion() +
                                                "\nSubcentro Costo:" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() +
                                                "\nCentroCosto Auxiliar:" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion() +
                                                "\n\n Y usted lo está programando para trabajar por:\n" +
                                                "\nCentro Operación: " + listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()).getDescripcion() +
                                                "\nSubcentro Costo:" + listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getDescripcion() +
                                                "\nCentroCosto Auxiliar:" + listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getDescripcion()
                                                + "\nEsta seguró que desea registrar la actividad");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (new ControlDB_Carbon(typeConnection).validarExistenciaMvtoCarbon(Objeto)) {//La placa ya fue registrada con la misma fecha de tara
                                                    if (new ControlDB_Carbon(typeConnection).validarVehiculoEnTransitoMvtoCarbon(Objeto)) {
                                                        builder = new AlertDialog.Builder(builder.getContext());
                                                        builder.setTitle("Advertencia!!");
                                                        builder.setMessage("La placa " + Objeto.getPlaca() + " actualmente se encuentra en transito, debe completar el ciclo para registrarla nuevamente");
                                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                validadorPresionaBotonIniciar=true;
                                                            }
                                                        });
                                                        builder.show();
                                                    } else {
                                                        //
                                                        //Validamos si la programación se venció
                                                        AsignacionEquipo ValidarTiempoAsignacionEquipo = null;
                                                        try {
                                                            ValidarTiempoAsignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(mvtoEquipo.getAsignacionEquipo().getEquipo());
                                                        } catch (SQLException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (ValidarTiempoAsignacionEquipo == null) {
                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Advertencia!!");
                                                            builder.setMessage("El equipo Seleccionado se le venció la programación, deben hacer una nueva programación");
                                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                    validadorPresionaBotonIniciar=true;
                                                                }
                                                            });
                                                            builder.show();
                                                        } else {
                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Advertencia !!");
                                                            builder.setMessage("La placa Seleccionada ya fue registrada, debe ingresar una placa diferente..");
                                                            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                    validadorPresionaBotonIniciar=true;
                                                                }
                                                            });
                                                            builder.show();
                                                        }
                                                        /*  Código para permitir que se registren la misma placa con la misma tara


                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Advertencia !!");
                                                            builder.setMessage("La placa Seleccionada ya fue registrada desea registrarla nuevamente..");
                                                            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    final int[] retorno = {0};
                                                                    try {
                                                                        retorno[0] = new ControlDB_Carbon(typeConnection).registrarMvtoCarbonCompleto(Objeto, asignacion, user, mvtoEquipo);
                                                                    } catch (FileNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (UnknownHostException e) {
                                                                        e.printStackTrace();
                                                                    } catch (SocketException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    if (retorno[0] == 1) {
                                                                        builder = new AlertDialog.Builder(builder.getContext());
                                                                        builder.setTitle("Registro Exitoso!!");
                                                                        builder.setMessage("Se registro la Placa " + Objeto.getPlaca() + " de forma exitosa.");
                                                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                //spinnerPlaca.setSelection(0);
                                                                                placa.setText("");
                                                                                articulo.setText("");
                                                                                cliente.setText("");
                                                                                transportadora.setText("");
                                                                                orden.setText("");
                                                                                deposito.setText("");
                                                                                peso.setText("");
                                                                                fechaTara.setText("");
                                                                                Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
                                                                                interfaz.putExtra("usuarioB", user);
                                                                                interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                                interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                                                startActivity(interfaz);
                                                                            }
                                                                        });
                                                                        builder.show();
                                                                    } else {
                                                                        if (retorno[0] == 2) {
                                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                                            builder.setTitle("Error!!");
                                                                            builder.setMessage("No hay conexión con el servidor valide conexión de red..");
                                                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                                    validadorPresionaBotonIniciar=true;
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                        } else {
                                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                                            builder.setTitle("Error!!");
                                                                            builder.setMessage("No se pudo registrar el inicio de descargue del vehiculo..");
                                                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                                    validadorPresionaBotonIniciar=true;
                                                                                }
                                                                            });
                                                                            builder.show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                    validadorPresionaBotonIniciar=true;
                                                                }
                                                            });
                                                            builder.show();
                                                        }*/
                                                    }
                                                } else {//La placa no ha sido registrada, es la primera vez que se registra con esa fecha de tara
                                                    //Validamos si la programación se venció
                                                    AsignacionEquipo ValidarTiempoAsignacionEquipo = null;
                                                    try {
                                                        ValidarTiempoAsignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(mvtoEquipo.getAsignacionEquipo().getEquipo());
                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (ValidarTiempoAsignacionEquipo == null) {
                                                        builder = new AlertDialog.Builder(builder.getContext());
                                                        builder.setTitle("Advertencia!!");
                                                        builder.setMessage("El equipo Seleccionado se le venció la programación, deben hacer una nueva programación");
                                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                validadorPresionaBotonIniciar=true;
                                                            }
                                                        });
                                                        builder.show();
                                                    } else {
                                                        final int[] retorno = {0};
                                                        try {
                                                            retorno[0] = new ControlDB_Carbon(typeConnection).registrarMvtoCarbonCompleto(Objeto, asignacion, user, mvtoEquipo);
                                                        } catch (FileNotFoundException e) {
                                                            e.printStackTrace();
                                                        } catch (UnknownHostException e) {
                                                            e.printStackTrace();
                                                        } catch (SocketException e) {
                                                            e.printStackTrace();
                                                        }

                                                        if (retorno[0] == 1) {
                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Registro Exitoso!!");
                                                            builder.setMessage("Se registro la Placa " + Objeto.getPlaca() + " de forma exitosa.");
                                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    //spinnerPlaca.setSelection(0);
                                                                    placa.setText("");
                                                                    articulo.setText("");
                                                                    cliente.setText("");
                                                                    transportadora.setText("");
                                                                    orden.setText("");
                                                                    deposito.setText("");
                                                                    peso.setText("");
                                                                    fechaTara.setText("");
                                                                    Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
                                                                    interfaz.putExtra("usuarioB", user);
                                                                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                    interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                                    startActivity(interfaz);
                                                                }
                                                            });
                                                            builder.show();
                                                        } else {
                                                            if (retorno[0] == 2) {
                                                                builder = new AlertDialog.Builder(builder.getContext());
                                                                builder.setTitle("Error!!");
                                                                builder.setMessage("No hay conexión con el servidor valide conexión de red..");
                                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                        validadorPresionaBotonIniciar=true;
                                                                    }
                                                                });
                                                                builder.show();
                                                            } else {
                                                                builder = new AlertDialog.Builder(builder.getContext());
                                                                builder.setTitle("Error!!");
                                                                builder.setMessage("No se pudo registrar el inicio de descargue del vehiculo..");
                                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                                        validadorPresionaBotonIniciar=true;
                                                                    }
                                                                });
                                                                builder.show();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                validadorPresionaBotonIniciar=true;
                                            }
                                        });
                                        builder.show();
                                    }
                                }
                            }
                        }
                    } else {
                        builder = new AlertDialog.Builder(builder.getContext());
                        builder.setTitle("Advertencia!!");
                        builder.setMessage("Debe carga un Equipo responsable de la actividad");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                validadorPresionaBotonIniciar=true;
                            }
                        });
                        builder.show();
                    }
                }
            }
        });
        Btn_Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
            interfaz.putExtra("usuarioB",  user);
            interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
            startActivity(interfaz);
            }
        });

        placa.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                System.out.println("Key:"+ cs.toString());
                Pattern pat = Pattern.compile("^[a-zA-Z]{3}\\d{3}");
                Matcher mat = pat.matcher(cs.toString());
                if (mat.matches()) {
                    //System.out.println("Placa correcta");
                    cargueInfo("Placa");
                } else {
                    System.out.println("Placa Incorrecta");
                }
                /*if(cs.toString().equals("")){
                    cargueInfo("Placa");
                }*/
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int k) { }
        });
        recargarPlaca.setClickable(true);
        recargarPlaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objeto=null;
                placaS.setText("");
                placa.setText("");
                articulo.setText("");
                cliente.setText("");
                transportadora.setText("");
                orden.setText("");
                deposito.setText("");
                peso.setText("");
                fechaTara.setText("");
            }
        });

    }
    public void listenerSpinner(){
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
                try {
                    asignacion = listadoEquipos.get(spinnerEquipo.getSelectedItemPosition());
                    equipoCodigo.setText("" + asignacion.getEquipo().getCodigo());
                    equipoTipoEquipo.setText("" + asignacion.getEquipo().getTipoEquipo().getDescripcion());
                    equipoMarca.setText("" + asignacion.getEquipo().getMarca());
                    equipoModelo.setText("" + asignacion.getEquipo().getModelo());
                    equipoSerial.setText("" + asignacion.getEquipo().getSerial());
                    equipoDescripcion.setText("" + asignacion.getEquipo().getDescripcion());
                    equipoProveedorEquipo.setText("" + asignacion.getEquipo().getProveedorEquipo().getDescripcion());
                    asignacion_CentroOperacion.setText("" + asignacion.getCentroOperacion().getDescripcion());
                    asignacion_SubcentroCosto.setText("" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                    asignacion_auxCentroCosto.setText("" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion());
                    asignacion_FechaInicio.setText("" + asignacion.getFechaHoraInicio());
                    asignacion_FechaFin.setText("" + asignacion.getFechaHoraFin());
                    try {
                        asignacion_CantidadHorasProgram.setText("" + (Integer.parseInt(asignacion.getCantidadMinutosProgramados()) / 60));
                    } catch (Exception e) {
                        asignacion_CantidadHorasProgram.setText("");
                    }
                }catch(Exception e){
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void cargarAsignacion(){
        asignacion=(AsignacionEquipo) getIntent().getExtras().getSerializable("AsignacionEquipo");
        if(asignacion !=null) {
            try {
                ArrayList<String> arrayListadoT =new ArrayList<>();
                arrayListadoT.add(asignacion.getEquipo().getDescripcion()+" "+asignacion.getEquipo().getModelo());
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                spinnerEquipo.setAdapter(adaptadorListado);
            } catch (Exception e) {
                e.printStackTrace();
            }
            equipoCodigo.setText("" + asignacion.getEquipo().getCodigo());
            equipoTipoEquipo.setText("" + asignacion.getEquipo().getTipoEquipo().getDescripcion());
            equipoMarca.setText("" + asignacion.getEquipo().getMarca());
            equipoModelo.setText("" + asignacion.getEquipo().getModelo());
           // equipoSerial.setText("" + asignacion.getEquipo().getSerial());
            equipoDescripcion.setText("" + asignacion.getEquipo().getDescripcion());
            equipoProveedorEquipo.setText("" + asignacion.getEquipo().getProveedorEquipo().getDescripcion());
            asignacion_CentroOperacion.setText("" + asignacion.getCentroOperacion().getDescripcion());
            asignacion_SubcentroCosto.setText("" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
            asignacion_auxCentroCosto.setText("" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion());
            asignacion_FechaInicio.setText("" + asignacion.getFechaHoraInicio());
            asignacion_FechaFin.setText("" + asignacion.getFechaHoraFin());
            try {
                asignacion_CantidadHorasProgram.setText("" + (Integer.parseInt(asignacion.getCantidadMinutosProgramados()) / 60));
            } catch (Exception e) {
                asignacion_CantidadHorasProgram.setText("");
            }
            //Cargamos las labores Realizadas de acuerdo a la asignación
            ArrayList<String> arrayListado =new ArrayList<>();
            try {
                listadoLaborRealizada= new ControlDB_LaborRealizada(typeConnection).buscarPorSubcentroCosto(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro());
                for (LaborRealizada listadoObjeto : listadoLaborRealizada) {
                    arrayListado.add(listadoObjeto.getDescripcion());
                }
            }catch (SQLException e){
                //mensaje("No se pudo cargar las labores Realizadas");
                builder.setTitle("Error!!");
                builder.setMessage("No se pudo cargar las labores Realizadas..");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                    }
                });
                builder.show();
            }
            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
            spinnerlaborRealizar.setAdapter(adaptadorListado);

        }
    };
    public void cargueInfo(String opcion){
        switch (opcion){
            case "Placa":{
                ArrayList<String> arrayListado =new ArrayList<>();
                ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                System.out.println("Placa reconocida: "+placa.getText().toString());
                listadoMvtoCarbon = controlDB_Carbon.cargarPlacaTransito(placa.getText().toString());
                    if(listadoMvtoCarbon !=null) {
                        for (MvtoCarbon mvtonCar : listadoMvtoCarbon) {
                            System.out.print("===============================================================================================================================================>xxxxx");
                            System.out.println(mvtonCar.getArticulo().getDescripcion());
                            placaS.setText("" + mvtonCar.getPlaca());
                            articulo.setText("" + mvtonCar.getArticulo().getDescripcion());
                            cliente.setText("" + mvtonCar.getCliente().getDescripcion());
                            transportadora.setText("" + mvtonCar.getTransportadora().getDescripcion());
                            deposito.setText("" + mvtonCar.getDeposito());
                            peso.setText("" + mvtonCar.getPesoVacio());
                            fechaTara.setText("" + mvtonCar.getFechaEntradaVehiculo());
                            Objeto = mvtonCar;
                        }
                    }
                System.out.print("===============================================================================================================================================>"+listadoMvtoCarbon.size());
                break;
            }
            case "C.O":{
                ArrayList<String> arrayListado =new ArrayList<>();
                try {
                    listadoCentroOperacion = new ControlDB_Carbon(typeConnection).buscarCentroOperacion("");
                    if(listadoCentroOperacion != null) {
                        for (CentroOperacion listadoObjeto : listadoCentroOperacion) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    }
                }catch (SQLException e){
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinnerCentroOperacion.setAdapter(adaptadorListado);
                break;
            }
            case "SubCentro_Costo":{
                if(listadoCentroOperacion != null){
                    ArrayList<String> arrayListado =new ArrayList<>();
                    try {
                        listadoCentroCostosSubCentro =  new ControlDB_Carbon(typeConnection).listarCentroCostoSubCentro(listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()));
                        if (listadoCentroCostosSubCentro != null) {
                            for (CentroCostoSubCentro listadoObjeto : listadoCentroCostosSubCentro) {
                                arrayListado.add(listadoObjeto.getDescripcion());
                            }
                            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                            spinnerSubCentroCosto.setAdapter(adaptadorListado);
                        }else{
                            listadoCentroCostoAuxiliar=null;
                            listadoCentroCostoAuxiliarDestino=null;
                            listadoLaborRealizada=null;
                            ArrayList<String> arrayListadoC =new ArrayList<>();
                            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoC);
                            spinnerSubCentroCosto.setAdapter(adaptadorListado);
                            spinnerAuxiliarCentroCosto.setAdapter(adaptadorListado);
                            spinnerAuxiliarCentroCostoDestino.setAdapter(adaptadorListado);
                            spinnerlaborRealizar.setAdapter(adaptadorListado);
                        }
                    }catch (SQLException e){
                        System.out.println("No se pudo consultar los SubCentro de Costos");
                    }
                }else{
                    listadoCentroCostosSubCentro=null;
                    listadoCentroCostoAuxiliar=null;
                    listadoCentroCostoAuxiliarDestino=null;
                    listadoLaborRealizada=null;
                    ArrayList<String> arrayListado =new ArrayList<>();
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                    spinnerSubCentroCosto.setAdapter(adaptadorListado);
                    spinnerAuxiliarCentroCosto.setAdapter(adaptadorListado);
                    spinnerAuxiliarCentroCostoDestino.setAdapter(adaptadorListado);
                    spinnerlaborRealizar.setAdapter(adaptadorListado);
                }
                break;
            }
            case "AuxiliarCentro_Costo":{
                if(listadoCentroCostosSubCentro != null) {
                    ArrayList<String> arrayListado = new ArrayList<>();
                    try {
                        listadoCentroCostoAuxiliar = new ControlDB_Carbon(typeConnection).buscarCentroCostoAuxiliar("" + listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getCodigo());
                        for (CentroCostoAuxiliar listadoObjeto : listadoCentroCostoAuxiliar) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    } catch (SQLException e) {
                        System.out.println("No se pudo los Auxiliares de Centro de Costos");
                    }
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                    spinnerAuxiliarCentroCosto.setAdapter(adaptadorListado);
                    spinnerAuxiliarCentroCostoDestino.setAdapter(adaptadorListado);
                    listadoCentroCostoAuxiliarDestino = listadoCentroCostoAuxiliar;
                }else{
                    ArrayList<String> arrayListado =new ArrayList<>();
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                    //spinnerSubCentroCosto.setAdapter(adaptadorListado);
                    spinnerAuxiliarCentroCostoDestino.setAdapter(adaptadorListado);
                    spinnerAuxiliarCentroCosto.setAdapter(adaptadorListado);
                    spinnerlaborRealizar.setAdapter(adaptadorListado);
                }
                break;
            }
            case "carga_listadoLaboresRealizada":{
                if(listadoCentroCostosSubCentro != null) {
                    ArrayList<String> arrayListado = new ArrayList<>();
                    try {
                        listadoLaborRealizada = new ControlDB_LaborRealizada(typeConnection).buscarPorSubcentroCosto(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
                        for (LaborRealizada listadoObjeto : listadoLaborRealizada) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    } catch (SQLException e) {
                        builder.setTitle("Error!!");
                        builder.setMessage("No se pudo cargar las labores Realizadas, valide datos..");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                    spinnerlaborRealizar.setAdapter(adaptadorListado);
                }else{
                    ArrayList<String> arrayListado =new ArrayList<>();
                    ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                    spinnerlaborRealizar.setAdapter(adaptadorListado);
                }
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
                    listadoTipoEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarTipoEquiposProgramados();
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
                        listadoMarcaEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarMarcaEquiposProgramados(listadoTipoEquipo.get(spinnerTipoEquipo.getSelectedItemPosition()).getDescripcion());
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
                        listadoEquipos = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionEquipos_Por_TipoEquipo_MarcaEquipo(listadoTipoEquipo.get(spinnerTipoEquipo.getSelectedItemPosition()).getDescripcion(), listadoMarcaEquipo.get(spinnerMarcaEquipo.getSelectedItemPosition()));
                        if (listadoEquipos != null) {
                            for (AsignacionEquipo Objeto : listadoEquipos) {
                                arrayListadoT.add(Objeto.getEquipo().getDescripcion() + " " + Objeto.getEquipo().getModelo());
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
    @Override
    public void handleResult(Result rawResult) {
        String codigoEquipo=rawResult.getText();
        //Validamos Si el Equipo Existe en el sistema
        ControlDB_Equipo controlDB_equipo = new ControlDB_Equipo(typeConnection);
        try {
            Equipo equipo = new Equipo();
            equipo = new ControlDB_Equipo(typeConnection).consultarEquipo(codigoEquipo);
            //equipo.setCodigo(codigoEquipo);
            if(equipo != null) {
                final AsignacionEquipo asignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(equipo);
                if (asignacionEquipo != null) {
                    mvtoEquipo= new MvtoEquipo();
                    mvtoEquipo.setCentroOperacion(listadoCentroOperacion.get(spinnerCentroOperacion.getSelectedItemPosition()));
                    if(listadoCentroCostoAuxiliar != null && listadoCentroCostosSubCentro != null) {
                        CentroCostoAuxiliar centroCostoAuxiliar_Temp = new CentroCostoAuxiliar();
                        centroCostoAuxiliar_Temp = listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition());
                        centroCostoAuxiliar_Temp.setCentroCostoSubCentro(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
                        mvtoEquipo.setCentroCostoAuxiliar(centroCostoAuxiliar_Temp);
                    }
                    if(listadoLaborRealizada != null) {
                        mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                        if (listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")) {
                            mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                        } else {
                            CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                            ccAuxiliar.setCodigo("NULL");
                            mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                            //Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                        }
                    }
                    /*System.out.println("==================================================================================================================>...Se fue con el cargue del equipo");
                    System.out.println("==================================================================================================================>...mvtoEquipo:"+mvtoEquipo);
                    System.out.println("==================================================================================================================>...Objeto_MvtoCarbon:"+Objeto.getCodigo());
                    System.out.println("==================================================================================================================>...TipoConexion:"+getIntent().getExtras().getString("TipoConexion"));
                    System.out.println("==================================================================================================================>...recobroCliente:"+listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                    */Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Modulo_carbon_iniciar_descargueVehiculo.class);
                    interfaz.putExtra("AsignacionEquipo", asignacionEquipo);
                    interfaz.putExtra("usuarioB", user);
                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("mvtoEquipo",mvtoEquipo);
                    interfaz.putExtra("Objeto_MvtoCarbon",Objeto);
                    interfaz.putExtra("recobroCliente", listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    //interfaz.putExtra("equipoCompartido", spinnerEquipoCompartido.getSelectedItem().toString());
                    startActivity(interfaz);
                    onPause();
                } else {
                    //mensaje("No Hay asignación programada para el equipo escaneado");
                    builder.setTitle("Alerta!!");
                    builder.setMessage("No Hay asignación programada para el siguiente equipo: "
                            +"\nCódigo: " + equipo.getCodigo()
                            +"\nMarca: "+equipo.getMarca()
                            +"\nModelo: "+equipo.getModelo()
                            +"\nTipo: "+equipo.getTipoEquipo().getDescripcion()
                            +"\nDescripción : "+equipo.getDescripcion()
                            +"");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Modulo_carbon_iniciar_descargueVehiculo.class);
                            interfaz.putExtra("AsignacionEquipo", asignacionEquipo);
                            interfaz.putExtra("usuarioB", user);
                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                            interfaz.putExtra("mvtoEquipo",mvtoEquipo);
                            interfaz.putExtra("Objeto_MvtoCarbon",Objeto);
                            interfaz.putExtra("recobroCliente", listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                            startActivity(interfaz);
                            onPause();
                        }
                    });
                    builder.show();
                    //escanerView.resumeCameraPreview(this);
                }
            }else{
                //mensaje("No Hay asignación programada para el equipo escaneado");
                builder.setTitle("Alerta!!");
                builder.setMessage("No se encontro el Código No." + codigoEquipo+" como un equipo en el sistema");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Modulo_carbon_iniciar_descargueVehiculo.class);
                        interfaz.putExtra("usuarioB", user);
                        interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                        interfaz.putExtra("mvtoEquipo",mvtoEquipo);
                        interfaz.putExtra("Objeto_MvtoCarbon",Objeto);
                        interfaz.putExtra("recobroCliente", listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
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
                    Intent interfaz = new Intent(Modulo_carbon_iniciar_descargueVehiculo.this, Menu.class);
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
