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
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Recobro;
import com.example.vg_appcostos.ModuloEquipo.Model.TipoEquipo;
import com.example.vg_appcostos.Sistemas.Controlador.ControlDB_Usuario;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
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

public class ModuloCarbon_iniciar_ciclo_equipo extends AppCompatActivity implements Serializable, ZXingScannerView.ResultHandler{
    boolean validadorPresionaBotonIniciar=true;
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    AsignacionEquipo asignacion=null;
    ArrayList<AsignacionEquipo> listadoEquipos;
    Spinner spinnerPlaca,spinnerRecobroCliente,/*spinnerEquipoCompartido,*/spinnerTipoEquipo,spinnerMarcaEquipo,spinnerEquipo;//,
            /*spinnerSubCentroCosto,spinnerAuxiliarCentroCosto,*//*spinnerAuxiliarCentroCostoDestino/*,spinnerlaborRealizar*/;
    //ArrayList<CentroCostoSubCentro> listadoCentroCostosSubCentro;
    //ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar;
    //ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliarDestino;
    //ArrayList<LaborRealizada> listadoLaborRealizada;
    ArrayList<MvtoCarbon> listadoMvtoCarbon;
    ArrayList<Recobro> listadoRecobro;
    ArrayList<TipoEquipo> listadoTipoEquipo;
    ArrayList<String> listadoMarcaEquipo;
    TextView usuarioCodigo,usuarioNombre,
            articulo,cliente,transportadora,orden,deposito,pesoLleno,fechaTara,fechaInicioDescargue,centroOperacion,subcentroCosto,
            auxCentroCosto,placaSeleccionada,textSubCentro,textCentroCostoAuxiliar,
            equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoSerial,equipoDescripcion,equipoProveedorEquipo
            ,asignacion_CentroOperacion,asignacion_SubcentroCosto,asignacion_auxCentroCosto,
            asignacion_FechaInicio,asignacion_FechaFin,asignacion_CantidadHorasProgram,usuario,
            /*textView_BodegaDestino,*/textView_BodegaOrigen,text_BodegaDestinoS,text_BodegaDestino,text_laborRealizadaS,text_laborRealizada;
    String PlacaSeleccionada,FechaTaraSeleccionada,equipoCompartido;
    MvtoCarbon Objeto;
    MvtoEquipo mvtoEquipo;
    Button bntIniciar,bntAtras;
    EditText placa;
    ImageView recargarPlaca;
    private ZXingScannerView escanerView;
    String recobroCliente;
    AlertDialog.Builder builder;
    String typeConnection=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_carbon_iniciar_ciclo_equipo);
        user= (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");

        if(user != null && typeConnection!=null){
            listadoMvtoCarbon= new ArrayList<>();
            listadoRecobro= new ArrayList<>();
            //Inicializamos los ArrayList
            //listadoCentroCostosSubCentro= new ArrayList<>();
            //listadoCentroCostoAuxiliar= new ArrayList<>();
            //listadoCentroCostoAuxiliarDestino= new ArrayList<>();
            //listadoLaborRealizada= new ArrayList<>();
            listadoTipoEquipo= new ArrayList<>();
            listadoMarcaEquipo= new ArrayList<>();
            listadoEquipos= new ArrayList<>();
            PlacaSeleccionada="";
            FechaTaraSeleccionada="";
            equipoCompartido=null;
            recobroCliente= null;
            usuarioCodigo = (TextView)findViewById(R.id.usuarioCodigo);
            usuarioNombre = (TextView)findViewById(R.id.usuarioNombre);
            textSubCentro = (TextView)findViewById(R.id.textSubCentro);
            textCentroCostoAuxiliar = (TextView)findViewById(R.id.textView_BodegaOrigen);
            usuarioCodigo.setText(""+user.getCodigo());
            usuarioNombre.setText(""+user.getNombres()+" "+user.getApellidos());
            spinnerPlaca= (Spinner)findViewById(R.id.spinnerPlaca);
            spinnerRecobroCliente= (Spinner)findViewById(R.id.spinnerRecobroCliente);
            //spinnerEquipoCompartido= (Spinner)findViewById(R.id.spinnerEquipoCompartido);
            recargarPlaca= (ImageView)findViewById(R.id.recargarPlaca);
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
            //spinnerSubCentroCosto= (Spinner)findViewById(R.id.spinnerSubCentroCosto);
            //spinnerAuxiliarCentroCosto= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCosto);
            //spinnerAuxiliarCentroCostoDestino= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCostoDestino);
            //spinnerlaborRealizar= (Spinner)findViewById(R.id.spinnerlaborRealizar);
            placaSeleccionada= (TextView)findViewById(R.id.placaSeleccionada);
            placa = (EditText)findViewById(R.id.placa);
            //textView_BodegaDestino= (TextView)findViewById(R.id.textView_BodegaDestino);
            textView_BodegaOrigen= (TextView)findViewById(R.id.textView_BodegaOrigen);
            text_BodegaDestinoS= (TextView)findViewById(R.id.text_BodegaDestinoS);
            text_BodegaDestino= (TextView)findViewById(R.id.text_BodegaDestino);
            text_laborRealizadaS= (TextView)findViewById(R.id.text_laborRealizadaS);
            text_laborRealizada= (TextView)findViewById(R.id.text_laborRealizada );
            mvtoEquipo = null;
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
            usuario= (TextView)findViewById(R.id.usuario);
            bntIniciar= (Button)findViewById(R.id.bntIniciar);
            bntAtras= (Button)findViewById(R.id.bntAtras);
            cargueInfo("Placa");
            cargueInfo("list_recobro");
            cargueInfo("list_compartido");

            spinnerTipoEquipo= (Spinner)findViewById(R.id.spinnerTipoEquipo);
            spinnerMarcaEquipo= (Spinner)findViewById(R.id.spinnerMarcaEquipo);
            spinnerEquipo= (Spinner)findViewById(R.id.spinnerEquipo);

            //spinnerSubCentroCosto.setEnabled(false);
            //spinnerAuxiliarCentroCosto.setEnabled(false);
            //spinnerlaborRealizar.setEnabled(false);

            //Ocultamos la bodegaDetino
            //textView_BodegaDestino.setVisibility(View.INVISIBLE);
            //spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);


            builder = new AlertDialog.Builder(this);
            spinnerPlaca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cargueInfo("PlacaEspecifica");
                    cargueInfo("SubCentro_Costo");
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            /*spinnerSubCentroCosto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            */

            /*spinnerEquipoCompartido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==0){//NO
                        spinnerSubCentroCosto.setEnabled(false);
                        spinnerAuxiliarCentroCosto.setEnabled(false);

                        //Ocultamos los Auxliares y SubcentroCostos
                        textSubCentro.setVisibility(View.INVISIBLE);
                        textCentroCostoAuxiliar.setVisibility(View.INVISIBLE);
                        spinnerSubCentroCosto.setVisibility(View.INVISIBLE);
                        spinnerAuxiliarCentroCosto.setVisibility(View.INVISIBLE);
                        cargueInfo("carga_listadoLaboresRealizada_por_MvtoCarbon");
                    }else{//SI
                        spinnerSubCentroCosto.setEnabled(true);
                        spinnerAuxiliarCentroCosto.setEnabled(true);

                        //Mostramos los Auxliares y SubcentroCostos
                        textSubCentro.setVisibility(View.VISIBLE);
                        textCentroCostoAuxiliar.setVisibility(View.VISIBLE);
                        spinnerSubCentroCosto.setVisibility(View.VISIBLE);
                        spinnerAuxiliarCentroCosto.setVisibility(View.VISIBLE);
                        cargueInfo("carga_listadoLaboresRealizada");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });*/
            /*spinnerlaborRealizar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(listadoLaborRealizada != null) {
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
            });*/
            placa.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int s, int b, int c) {
                    System.out.println("Key:"+ cs.toString());
                    Pattern pat = Pattern.compile("^[a-zA-Z]{3}\\d{3}");
                    Matcher mat = pat.matcher(cs.toString());
                    if (mat.matches()) {
                        System.out.println("Placa correcta");
                        cargueInfo("Placa");

                        Objeto=new ControlDB_Carbon(typeConnection).buscarMtvo_CarbonParticular(placa.getText().toString());
                        placaSeleccionada.setText(""+Objeto.getPlaca());
                        articulo.setText("" + Objeto.getArticulo().getDescripcion());
                        cliente.setText("" + Objeto.getCliente().getDescripcion());
                        transportadora.setText("" + Objeto.getTransportadora().getDescripcion());
                        //orden.setText("ORDEN: " + Objeto.getOrden());
                        deposito.setText("" + Objeto.getDeposito());
                        pesoLleno.setText("" + Objeto.getPesoVacio());
                        fechaTara.setText("" + Objeto.getFechaEntradaVehiculo());
                        fechaInicioDescargue.setText("" + Objeto.getFechaInicioDescargue());
                        centroOperacion.setText("" + Objeto.getCentroOperacion().getDescripcion());
                        subcentroCosto.setText("" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                        auxCentroCosto.setText("" + Objeto.getCentroCostoAuxiliar().getDescripcion());
                        text_BodegaDestino.setText("" + Objeto.getCentroCostoAuxiliarDestino().getDescripcion());
                        text_laborRealizada.setText("" + Objeto.getLaborRealizada().getDescripcion());
                        usuario.setText(Objeto.getUsuarioRegistroMovil().getNombres()+" "+Objeto.getUsuarioRegistroMovil().getApellidos());
                    } else {
                        System.out.println("Placa Incorrecta");
                        System.out.println(""+spinnerPlaca.getCount());
                    }
                    if(cs.toString().equals("")){
                        cargueInfo("Placa");
                    }
                }
                public void afterTextChanged(Editable editable) { }
                public void beforeTextChanged(CharSequence cs, int i, int j, int k) { }
            });
            recargarPlaca.setClickable(true);
            recargarPlaca.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Dimos Clic");
                    placa.setText("");
                    cargueInfo("Placa");

                }
            });




            mvtoEquipo = (MvtoEquipo) getIntent().getExtras().getSerializable("mvtoEquipo");
            if(mvtoEquipo !=null){
                asignacion= mvtoEquipo.getAsignacionEquipo();                //(AsignacionEquipo) getIntent().getExtras().getSerializable("AsignacionEquipo");
                if(asignacion!=null) {
                    try {
                        ArrayList<String> arrayListadoT =new ArrayList<>();
                        arrayListadoT.add(asignacion.getEquipo().getDescripcion()+" "+asignacion.getEquipo().getModelo());
                        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                        spinnerEquipo.setAdapter(adaptadorListado);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(listadoMvtoCarbon !=null){
                        int contador =0;
                        for(MvtoCarbon Objeto: listadoMvtoCarbon){
                            if(Objeto.getPlaca().equals(mvtoEquipo.getMvtoCarbon().getPlaca())){
                                spinnerPlaca.setSelection(contador);
                            }
                            contador++;
                        }
                    }
                    System.out.println("LA PLACA FUE "+PlacaSeleccionada);
                    equipoCodigo.setText(""+asignacion.getEquipo().getCodigo());
                    equipoTipoEquipo.setText(""+asignacion.getEquipo().getTipoEquipo().getDescripcion());
                    equipoMarca.setText(""+asignacion.getEquipo().getMarca());
                    equipoModelo.setText(""+asignacion.getEquipo().getModelo());
                    equipoSerial.setText(""+asignacion.getEquipo().getSerial());
                    equipoDescripcion.setText(""+asignacion.getEquipo().getDescripcion());
                    equipoProveedorEquipo.setText(""+asignacion.getEquipo().getProveedorEquipo().getDescripcion());
                    asignacion_CentroOperacion.setText(""+asignacion.getCentroOperacion().getDescripcion());
                    asignacion_SubcentroCosto.setText(""+asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                    asignacion_auxCentroCosto.setText(""+asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion());
                    asignacion_FechaInicio.setText(""+asignacion.getFechaHoraInicio());
                    asignacion_FechaFin.setText(""+asignacion.getFechaHoraFin());
                    try{
                        asignacion_CantidadHorasProgram.setText(""+(Integer.parseInt(asignacion.getCantidadMinutosProgramados())/60));
                    }catch(Exception e){
                        asignacion_CantidadHorasProgram.setText("");
                    }
                    //equipoCompartido =getIntent().getExtras().getString("equipoCompartido");
                    /*if(equipoCompartido !=null){
                        /*
                        if(equipoCompartido.equalsIgnoreCase("SI")){
                            spinnerEquipoCompartido.setSelection(1);

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
                            cargueInfo("carga_listadoLaboresRealizada");
                            if(mvtoEquipo !=null){
                                if(listadoLaborRealizada !=null){
                                    int contador =0;
                                    for(LaborRealizada Objeto2: listadoLaborRealizada){
                                        if(Objeto2.getCodigo() == mvtoEquipo.getLaborRealizada().getCodigo()){
                                            spinnerlaborRealizar.setSelection(contador);
                                        }
                                        contador++;
                                    }
                                }
                            }
                            if(listadoCentroCostoAuxiliarDestino !=null){
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
                            }
                        }else{// Selección fue NO
                            spinnerEquipoCompartido.setSelection(0);
                            cargueInfo("carga_listadoLaboresRealizada_por_MvtoCarbon");
                            if(mvtoEquipo !=null){
                                if(listadoLaborRealizada !=null){
                                    int contador =0;
                                    for(LaborRealizada Objeto2: listadoLaborRealizada){
                                        if(Objeto2.getCodigo() == mvtoEquipo.getLaborRealizada().getCodigo()){
                                            spinnerlaborRealizar.setSelection(contador);
                                        }
                                        contador++;
                                    }
                                }
                            }
                        }*/
                    //}
                }
                recobroCliente =getIntent().getExtras().getString("recobroCliente");  // =(Recobro) getIntent().getExtras().getSerializable("recobroCliente");
                if(recobroCliente !=null){
                    if(recobroCliente.equals("SI")){
                        spinnerRecobroCliente.setSelection(1);
                    }else{
                        spinnerRecobroCliente.setSelection(0);
                    }
                }
            }
            bntIniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validadorPresionaBotonIniciar) {
                        validadorPresionaBotonIniciar = false;
                        if (mvtoEquipo == null) {
                            mvtoEquipo = new MvtoEquipo();
                        }
                        if (asignacion != null) {
                            if (Objeto != null) {
                                if (new ControlDB_Carbon(typeConnection).validarStandByPendiente(asignacion.getEquipo().getCodigo())) {//El equipo tiene Standy BY pendientes, por tal motivo no deja registrar nuevas actividades
                                    builder = new AlertDialog.Builder(builder.getContext());
                                    builder.setTitle("Advertencia!!");
                                    builder.setMessage("El siguiente equipo:\n"
                                            + "Código: " + asignacion.getEquipo().getCodigo() + "\n"
                                            + "Descripción:" + asignacion.getEquipo().getDescripcion() + " " + asignacion.getEquipo().getModelo() + "\n Se encuentra con un Stand By pendiente, debe cerrarlo para poder hacer nuevos registros.\nDesea cerrar el Standy By?");
                                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Modulo_Equipo_cierre_StandBy.class);
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
                                } else {/*El equipo no se encuentra en StandBy por ende
                                     Validamos que el Centro Operación, CentroCostoAuxiliar, SubcentroCosto de la programación del equipo sea igual a los  programados en el vehículo*/
                                    if (Objeto.getCentroOperacion().getCodigo() == asignacion.getCentroOperacion().getCodigo() &&
                                            Objeto.getCentroCostoAuxiliar().getCodigo() == asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCodigo() &&
                                            Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo() == asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()
                                    ) {
                                        MvtoEquipo mvtoEquipo = new MvtoEquipo();
                                        mvtoEquipo.setAsignacionEquipo(asignacion);
                                        mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                                        mvtoEquipo.setNumeroOrden("");
                                        mvtoEquipo.setCentroOperacion(asignacion.getCentroOperacion());
                                        mvtoEquipo.setCentroCostoAuxiliar(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar());

                                        //mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                        mvtoEquipo.setLaborRealizada(Objeto.getLaborRealizada());
                                        mvtoEquipo.setCliente(Objeto.getCliente());
                                        mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                        mvtoEquipo.setUsuarioQuieRegistra(user);
                                        mvtoEquipo.setEstado("1");
                                        mvtoEquipo.setDesdeCarbon("1");
                                        if (Objeto.getCentroCostoAuxiliarDestino().getCodigo() != null) {
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(Objeto.getCentroCostoAuxiliarDestino());
                                        } else {
                                            CentroCostoAuxiliar cc_auxiliar = new CentroCostoAuxiliar();
                                            cc_auxiliar.setCodigo("NUL");
                                            cc_auxiliar.setCodigo("NULL");
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(cc_auxiliar);
                                        }
                                    /*if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                    }else{
                                        CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                        ccAuxiliar.setCodigo("NULL");
                                        Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                    }*/
                                        //Tenemos 3 Objetos     (MvtoCarbon Objeto)   (MvtoEquipo mvtoEquipo) (AsignacionEquipo asignacion)
                                        try {
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
                                                int result = new ControlDB_Carbon(typeConnection).registrarEnListadoMvtoCarbon(Objeto, asignacion, user, mvtoEquipo);
                                                if (result == 1) {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Registro Exitoso!!");
                                                    builder.setMessage("Se inicio de descargue de manera exitosa");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    asignacion = null;
                                                                    equipoCodigo.setText("");
                                                                    equipoTipoEquipo.setText("");
                                                                    equipoMarca.setText("");
                                                                    equipoModelo.setText("");
                                                                    equipoSerial.setText("");
                                                                    equipoDescripcion.setText("");
                                                                    equipoProveedorEquipo.setText("");
                                                                    asignacion_CentroOperacion.setText("");
                                                                    asignacion_SubcentroCosto.setText("");
                                                                    asignacion_auxCentroCosto.setText("");
                                                                    asignacion_FechaInicio.setText("");
                                                                    asignacion_FechaFin.setText("");
                                                                    asignacion_CantidadHorasProgram.setText("");

                                                                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
                                                                    interfaz.putExtra("usuarioB", user);
                                                                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                    interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                                    startActivity(interfaz);
                                                                }
                                                            }
                                                    );
                                                    builder.show();
                                                } else {
                                                    //mensaje("Error al registrar el descargue del vehiculo con este equipo");
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Error!!");
                                                    builder.setMessage("No se pudo registrar el descargue del vehículo con este equipo..");
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
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    } else {/*El Centro_Operación, CentroCosto_Auxiliar, Subcentro_Costo de la programación del equipo no es igual a los  programados en el vehículo por tal motivo
                                            Preguntaremos si el usuario desea registrar igualmente la información suministrada*/

                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Advertencia!!");


                                        builder.setMessage("El Centro de Operación, Subcentro de costo y el centro de costo Auxiliar de la programación de equipo no coincide con los programados del vehículos\n\n" +
                                                "Se encontró la siguiente inconsistencia,\n" +
                                                "Vehículo:" + "\n" +
                                                "Placa:" + Objeto.getPlaca() + "\n" +
                                                "Centro Operación:" + asignacion.getCentroOperacion().getDescripcion() + "\n" +
                                                "Subcentro Costo:" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + "\n" +
                                                "Auxiliar Centro Costo:" + Objeto.getCentroCostoAuxiliar().getDescripcion() + "\n\n\n" +
                                                "Equipo:" + "\n" +
                                                "Código:" + asignacion.getEquipo().getCodigo() + "\n" +
                                                "Descripción:" + asignacion.getEquipo().getDescripcion() + " " + asignacion.getEquipo().getModelo() + "\n" +
                                                "Centro Operación:" + asignacion.getCentroOperacion().getDescripcion() + "\n" +
                                                "Subcentro Costo::" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + "\n" +
                                                "Auxiliar Centro Costo:" + asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion() + "\n\n\nDesea registrar igualmente la información?.");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Procedemos a registrar la información teniendo en cuenta que el usuario autorizó
                                                MvtoEquipo mvtoEquipo = new MvtoEquipo();
                                                mvtoEquipo.setAsignacionEquipo(asignacion);
                                                mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                                                mvtoEquipo.setNumeroOrden("");
                                                mvtoEquipo.setCentroOperacion(asignacion.getCentroOperacion());
                                                mvtoEquipo.setCentroCostoAuxiliar(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar());

                                                //mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                                mvtoEquipo.setLaborRealizada(Objeto.getLaborRealizada());
                                                mvtoEquipo.setCliente(Objeto.getCliente());
                                                mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                                mvtoEquipo.setUsuarioQuieRegistra(user);
                                                mvtoEquipo.setEstado("1");
                                                mvtoEquipo.setDesdeCarbon("1");
                                                if (Objeto.getCentroCostoAuxiliarDestino().getCodigo() != null) {
                                                    mvtoEquipo.setCentroCostoAuxiliarDestino(Objeto.getCentroCostoAuxiliarDestino());
                                                } else {
                                                    CentroCostoAuxiliar cc_auxiliar = new CentroCostoAuxiliar();
                                                    cc_auxiliar.setCodigo("NUL");
                                                    cc_auxiliar.setCodigo("NULL");
                                                    mvtoEquipo.setCentroCostoAuxiliarDestino(cc_auxiliar);
                                                }
                                    /*if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                    }else{
                                        CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                        ccAuxiliar.setCodigo("NULL");
                                        Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                        mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                    }*/
                                                //Tenemos 3 Objetos     (MvtoCarbon Objeto)   (MvtoEquipo mvtoEquipo) (AsignacionEquipo asignacion)
                                                try {
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
                                                        int result = new ControlDB_Carbon(typeConnection).registrarEnListadoMvtoCarbon(Objeto, asignacion, user, mvtoEquipo);
                                                        if (result == 1) {
                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Registro Exitoso!!");
                                                            builder.setMessage("Se inicio de descargue de manera exitosa");
                                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            asignacion = null;
                                                                            equipoCodigo.setText("");
                                                                            equipoTipoEquipo.setText("");
                                                                            equipoMarca.setText("");
                                                                            equipoModelo.setText("");
                                                                            equipoSerial.setText("");
                                                                            equipoDescripcion.setText("");
                                                                            equipoProveedorEquipo.setText("");
                                                                            asignacion_CentroOperacion.setText("");
                                                                            asignacion_SubcentroCosto.setText("");
                                                                            asignacion_auxCentroCosto.setText("");
                                                                            asignacion_FechaInicio.setText("");
                                                                            asignacion_FechaFin.setText("");
                                                                            asignacion_CantidadHorasProgram.setText("");

                                                                            Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
                                                                            interfaz.putExtra("usuarioB", user);
                                                                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                            interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                                            startActivity(interfaz);
                                                                        }
                                                                    }
                                                            );
                                                            builder.show();
                                                        } else {
                                                            //mensaje("Error al registrar el descargue del vehiculo con este equipo");
                                                            builder = new AlertDialog.Builder(builder.getContext());
                                                            builder.setTitle("Error!!");
                                                            builder.setMessage("No se pudo registrar el descargue del vehiculo con este equipo..");
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
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                } catch (SocketException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                validadorPresionaBotonIniciar=true;
                                            }
                                        });
                                        builder.show();
                                    }
                                    //#################
/*
                                if (spinnerEquipoCompartido.getSelectedItem().toString().equals("SI")) {//El vehiculo fue marcado como compartido
                                    if (Objeto.getCentroOperacion().getCodigo() == asignacion.getCentroOperacion().getCodigo() &&
                                            listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getCodigo() ==
                                                    Objeto.getCentroCostoAuxiliar().getCodigo() &&
                                            listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getCodigo() ==
                                                    Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()) {
                                        mvtoEquipo.setAsignacionEquipo(asignacion);
                                        mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                                        mvtoEquipo.setNumeroOrden("");
                                        mvtoEquipo.setCentroOperacion(asignacion.getCentroOperacion());
                                        mvtoEquipo.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));
                                        mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                        mvtoEquipo.setCliente(Objeto.getCliente());
                                        mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                        mvtoEquipo.setUsuarioQuieRegistra(user);
                                        mvtoEquipo.setEstado("1");
                                        mvtoEquipo.setDesdeCarbon("1");
                                        if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                        }else{
                                            CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                            ccAuxiliar.setCodigo("NULL");
                                            Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                        }
                                        //Tenemos 3 Objetos     (MvtoCarbon Objeto)   (MvtoEquipo mvtoEquipo) (AsignacionEquipo asignacion)
                                        System.out.println("" + typeConnection);
                                        try {
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
                                                builder.setMessage("Al equipo Seleccionado se le venció la programación, deben hacer una nueva programación");
                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                    }
                                                });
                                                builder.show();
                                            }else {
                                                int result = new ControlDB_Carbon(typeConnection).registrarEnListadoMvtoCarbon(Objeto, asignacion, user, mvtoEquipo);
                                                if (result == 1) {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Registro Exitoso!!");
                                                    builder.setMessage("Se inicio de descargue de manera exitosa");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    asignacion = null;
                                                                    equipoCodigo.setText("");
                                                                    equipoTipoEquipo.setText("");
                                                                    equipoMarca.setText("");
                                                                    equipoModelo.setText("");
                                                                    equipoSerial.setText("");
                                                                    equipoDescripcion.setText("");
                                                                    equipoProveedorEquipo.setText("");
                                                                    asignacion_CentroOperacion.setText("");
                                                                    asignacion_SubcentroCosto.setText("");
                                                                    asignacion_auxCentroCosto.setText("");
                                                                    asignacion_FechaInicio.setText("");
                                                                    asignacion_FechaFin.setText("");
                                                                    asignacion_CantidadHorasProgram.setText("");
                                                                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
                                                                    interfaz.putExtra("usuarioB", user);
                                                                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                    startActivity(interfaz);
                                                                }
                                                            }
                                                    );
                                                    builder.show();
                                                } else {
                                                    //mensaje("Error al registrar el descargue del vehiculo con este equipo");
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Error!!");
                                                    builder.setMessage("No se pudo registrar el descargue del vehiculo con este equipo,valide datos..");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        //mensaje("Error!!. Los Centro de Operación, Auxiliares y Subcentro de Costos deben ser igual a lo programados en el descargue del vehículo");
                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Error!!");
                                        builder.setMessage("Error!!\n" +
                                                "Los Centro Operación, Auxiliares y Subcentro de costo deben ser igual tanto en el descargue del vehículo, como en la programación del equipo\n\n" +
                                                "Se encontró la siguiente inconsistencia,\n" +
                                                "Vehículo de Placa:" + Objeto.getPlaca() +
                                                "Centro Operación:" + asignacion.getCentroOperacion().getCodigo() + "\n" +
                                                "Subcentro Costo:" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion() + "\n" +
                                                "Auxiliar Centro Costo:" + Objeto.getCentroCostoAuxiliar().getDescripcion() + "\n\n\n" +
                                                "Equipo:" + asignacion.getEquipo().getCodigo() + "\n" +
                                                "Centro Operación:" + asignacion.getEquipo().getCodigo() + "\n" +
                                                "Subcentro Costo::" + listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getDescripcion() + "\n" +
                                                "Auxiliar Centro Costo:" + listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getDescripcion() + "\n"
                                        );

                                        builder.setMessage("Los Centro de Operación, Auxiliares y Subcentro de Costos deben ser igual a lo programados en el descargue del vehículo..");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                            }
                                        });
                                        builder.show();
                                    }
                                } else {//El equipo no fue marcado como compartido
                                    if (Objeto.getCentroOperacion().getCodigo() == asignacion.getCentroOperacion().getCodigo() &&
                                            Objeto.getCentroCostoAuxiliar().getCodigo() == asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCodigo() &&
                                            Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo() ==
                                                    asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()
                                    ) {
                                        MvtoEquipo mvtoEquipo = new MvtoEquipo();
                                        mvtoEquipo.setAsignacionEquipo(asignacion);
                                        mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                                        mvtoEquipo.setNumeroOrden("");
                                        mvtoEquipo.setCentroOperacion(asignacion.getCentroOperacion());
                                        mvtoEquipo.setCentroCostoAuxiliar(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar());
                                        mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                                        mvtoEquipo.setCliente(Objeto.getCliente());
                                        mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                                        mvtoEquipo.setUsuarioQuieRegistra(user);
                                        mvtoEquipo.setEstado("1");
                                        mvtoEquipo.setDesdeCarbon("1");
                                        if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                        }else{
                                            CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                            ccAuxiliar.setCodigo("NULL");
                                            Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                            mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                        }
                                        //Tenemos 3 Objetos     (MvtoCarbon Objeto)   (MvtoEquipo mvtoEquipo) (AsignacionEquipo asignacion)
                                        try {
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
                                                    }
                                                });
                                                builder.show();
                                            }else {
                                                int result = new ControlDB_Carbon(typeConnection).registrarEnListadoMvtoCarbon(Objeto, asignacion, user, mvtoEquipo);
                                                if (result == 1) {
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Registro Exitoso!!");
                                                    builder.setMessage("Se inicio de descargue de manera exitosa");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    asignacion = null;
                                                                    equipoCodigo.setText("");
                                                                    equipoTipoEquipo.setText("");
                                                                    equipoMarca.setText("");
                                                                    equipoModelo.setText("");
                                                                    equipoSerial.setText("");
                                                                    equipoDescripcion.setText("");
                                                                    equipoProveedorEquipo.setText("");
                                                                    asignacion_CentroOperacion.setText("");
                                                                    asignacion_SubcentroCosto.setText("");
                                                                    asignacion_auxCentroCosto.setText("");
                                                                    asignacion_FechaInicio.setText("");
                                                                    asignacion_FechaFin.setText("");
                                                                    asignacion_CantidadHorasProgram.setText("");

                                                                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
                                                                    interfaz.putExtra("usuarioB", user);
                                                                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                                    startActivity(interfaz);
                                                                }
                                                            }
                                                    );
                                                    builder.show();
                                                } else {
                                                    //mensaje("Error al registrar el descargue del vehiculo con este equipo");
                                                    builder = new AlertDialog.Builder(builder.getContext());
                                                    builder.setTitle("Error!!");
                                                    builder.setMessage("No se pudo registrar el descargue del vehiculo con este equipo..");
                                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            }
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        //mensaje("Error!!. Los Centro de Operación, Auxiliares y Subcentro de Costos deben ser iguales tanto en el descargue del vehículo como en la operación");
                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Error!!");
                                        builder.setMessage("Los Centro de Operación, Auxiliares y Subcentro de Costos deben ser iguales tanto en el descargue del vehículo como en la operación..");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                            }
                                        });
                                        builder.show();
                                    }
                                }*/
                                }
                            } else {
                                //mensaje("Error!!. Debe seleccionar una placa de vehículo para Iniciar el Cargue");
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Error!!");
                                builder.setMessage("Se debe seleccionar una placa de vehículo para Iniciar el Cargue..");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Hacer cosas aqui al hacer clic en el boton de aceptar
                                        validadorPresionaBotonIniciar=true;
                                    }
                                });
                                builder.show();
                            }
                        } else {
                            //mensaje("Error!!. Se debe cargar una programación de equipo a través del escaneo de código de barra");
                            builder = new AlertDialog.Builder(builder.getContext());
                            builder.setTitle("Error!!");
                            builder.setMessage("Se debe cargar una programación de equipo a través del escaneo de código de barra..");
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
            bntAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
                    interfaz.putExtra("usuarioB",  user);
                    interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    startActivity(interfaz);
                }
            });
        }else{
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Advertencia!!");
            builder.setMessage("Usuario no autorizado para cargar interfaz");
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Cerramos el aplicativo, redireccionamos a la página de login
                }
            });
            builder.show();
        }

    }
    public void cargarDatosInterfaz(String opcion){

    }
    public void cargueInfo(String opcion){
        switch (opcion){
            case "Placa":{
                ArrayList<String> arrayListado =new ArrayList<>();
                System.out.println("Placa reconocida: "+placa.getText().toString());
                ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                listadoMvtoCarbon = controlDB_Carbon.buscarMtvoCarbon_Optimzado(placa.getText().toString());
                for (MvtoCarbon listadoObjeto : listadoMvtoCarbon) {
                    arrayListado.add(listadoObjeto.getPlaca());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinnerPlaca.setAdapter(adaptadorListado);
                break;
            }
            case "PlacaEspecifica":{
                if(!spinnerPlaca.getSelectedItem().toString().isEmpty()) {
                    Objeto=new ControlDB_Carbon(typeConnection).buscarMtvo_CarbonParticular(listadoMvtoCarbon.get(spinnerPlaca.getSelectedItemPosition()).getPlaca(),
                                                                                                listadoMvtoCarbon.get(spinnerPlaca.getSelectedItemPosition()).getPesoVacio(),
                                                                                                listadoMvtoCarbon.get(spinnerPlaca.getSelectedItemPosition()).getFechaEntradaVehiculo(),
                                                                                                listadoMvtoCarbon.get(spinnerPlaca.getSelectedItemPosition()).getFechaInicioDescargue());
                    placaSeleccionada.setText(""+Objeto.getPlaca());
                    articulo.setText("" + Objeto.getArticulo().getDescripcion());
                    cliente.setText("" + Objeto.getCliente().getDescripcion());
                    transportadora.setText("" + Objeto.getTransportadora().getDescripcion());
                    //orden.setText("ORDEN: " + Objeto.getOrden());
                    deposito.setText("" + Objeto.getDeposito());
                    pesoLleno.setText("" + Objeto.getPesoVacio());
                    fechaTara.setText("" + Objeto.getFechaEntradaVehiculo());
                    fechaInicioDescargue.setText("" + Objeto.getFechaInicioDescargue());
                    centroOperacion.setText("" + Objeto.getCentroOperacion().getDescripcion());
                    subcentroCosto.setText("" + Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                    auxCentroCosto.setText("" + Objeto.getCentroCostoAuxiliar().getDescripcion());
                    text_BodegaDestino.setText("" + Objeto.getCentroCostoAuxiliarDestino().getDescripcion());
                    text_laborRealizada.setText("" + Objeto.getLaborRealizada().getDescripcion());
                    usuario.setText(Objeto.getUsuarioRegistroMovil().getNombres()+" "+Objeto.getUsuarioRegistroMovil().getApellidos());

                    cargueInfo("carga_listadoLaboresRealizada_por_MvtoCarbon");
                }

                break;
            }
            /*case "SubCentro_Costo":{
                if(Objeto != null){
                    ArrayList<String> arrayListado =new ArrayList<>();
                    try {
                        listadoCentroCostosSubCentro =  new ControlDB_Carbon(typeConnection).listarCentroCostoSubCentro(Objeto.getCentroOperacion());
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
            }*/
            /*case "AuxiliarCentro_Costo":{
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
            }*/
            /*case "carga_listadoLaboresRealizada":{
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
            }*/
            /*case "carga_listadoLaboresRealizada_por_MvtoCarbon":{
                if(Objeto !=null) {
                    ArrayList<String> arrayListado =new ArrayList<>();
                    try {
                        listadoLaborRealizada = new ControlDB_LaborRealizada(typeConnection).buscarPorSubcentroCosto(Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro());
                        for (LaborRealizada listadoObjeto : listadoLaborRealizada) {
                            arrayListado.add(listadoObjeto.getDescripcion());
                        }
                    } catch (SQLException e) {
                        //mensaje("No se pudo cargar las labores Realizadas");
                        builder = new AlertDialog.Builder(builder.getContext());
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
            }*/
            /* */
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
            /*case "list_compartido":{
                ArrayList<String> arrayListadoT =new ArrayList<>();
                arrayListadoT.add("NO");
                arrayListadoT.add("SI");
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                spinnerEquipoCompartido.setAdapter(adaptadorListado);
                break;
            }*/
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
    @Override
    public void handleResult(Result rawResult) {
        String codigoEquipo=rawResult.getText();
        //System.out.println("____________________________________________________________________________________>"+spinnerEquipoCompartido.getSelectedItem().toString());
        //Validamos Si el Equipo Existe en el sistema
        ControlDB_Equipo controlDB_equipo = new ControlDB_Equipo(typeConnection);
        try {
            Equipo equipo = new Equipo();
            equipo = new ControlDB_Equipo(typeConnection).buscarEspecifico(codigoEquipo);
            //equipo.setCodigo(codigoEquipo);
            if(equipo != null) {
                AsignacionEquipo asignacionEquipo = new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(equipo);
                if (asignacionEquipo != null) {
                    mvtoEquipo= new MvtoEquipo();
                    if(Objeto != null) {
                        mvtoEquipo.setCentroCostoAuxiliar(Objeto.getCentroCostoAuxiliar());
                        mvtoEquipo.getCentroCostoAuxiliar().setCentroCostoSubCentro(Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro());
                        mvtoEquipo.setLaborRealizada(Objeto.getLaborRealizada());
                        mvtoEquipo.setMvtoCarbon(Objeto);
                    }
                    /*if(listadoLaborRealizada != null) {
                        mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                    }*/
                    mvtoEquipo.setAsignacionEquipo(asignacionEquipo);


                    /*
                    if(spinnerEquipoCompartido.getSelectedItem().toString().equalsIgnoreCase("SI")){
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
                            }
                        }
                        mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                        mvtoEquipo.setMvtoCarbon(Objeto);
                    }else{
                        mvtoEquipo.setCentroCostoAuxiliar(Objeto.getCentroCostoAuxiliar());
                        mvtoEquipo.getCentroCostoAuxiliar().setCentroCostoSubCentro(Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro());
                        if(listadoLaborRealizada != null) {
                            mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));
                        }
                        mvtoEquipo.setAsignacionEquipo(asignacionEquipo);
                        mvtoEquipo.setMvtoCarbon(Objeto);
                    }*/




                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, ModuloCarbon_iniciar_ciclo_equipo.class);
                    interfaz.putExtra("usuarioB", user);
                    interfaz.putExtra("mvtoEquipo",mvtoEquipo);
                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("recobroCliente", listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()).getDescripcion());
                    interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                    //interfaz.putExtra("equipoCompartido", spinnerEquipoCompartido.getSelectedItem().toString());
                    startActivity(interfaz);
                    onPause();
                } else {
                    builder = new AlertDialog.Builder(builder.getContext());
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
                            Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, ModuloCarbon_iniciar_ciclo_equipo.class);
                            interfaz.putExtra("usuarioB", user);
                            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                            startActivity(interfaz);
                            onPause();
                        }
                    });
                    builder.show();
                }
            }else{
                //mensaje("No Hay asignación programada para el equipo escaneado");
                builder = new AlertDialog.Builder(builder.getContext());
                builder.setTitle("Alerta!!");
                builder.setMessage("No se encontro el Código No." + codigoEquipo+" como un equipo en el sistema");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, ModuloCarbon_iniciar_ciclo_equipo.class);
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
                    Intent interfaz = new Intent(ModuloCarbon_iniciar_ciclo_equipo.this, Menu.class);
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
