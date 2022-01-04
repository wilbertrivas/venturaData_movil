package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Toast;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Articulo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.BaseDatos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoAuxiliar;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroCostoSubCentro;
import com.example.vg_appcostos.ModuloCarbon.Modelo.CentroOperacion;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Cliente;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.AsignacionEquipo;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloCarbon.Modelo.Motonave;
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

public class modulo_equipo_iniciar_ciclo_equipo extends AppCompatActivity implements Serializable, ZXingScannerView.ResultHandler{
    boolean validadorPresionaBotonIniciar=true;
    Usuario user=null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    AsignacionEquipo asignacion=null;

    Spinner spinner_buscarCliente,spinner_buscarProducto,spinner_buscarMotonave,spinnerRecobroCliente,spinnerEquipoCompartido,spinnerSeleccionarCliente,spinnerSeleccionarProducto,spinnerSeleccionarMotonave,
            spinnerSubCentroCosto,spinnerAuxiliarCentroCosto,spinnerlaborRealizar,spinnerTipoEquipo,spinnerMarcaEquipo,spinnerEquipo,spinnerAuxiliarCentroCostoDestino,spinnerCentroOperacion/*,spinner_proveedorEquipo*/;

    ArrayList<TipoEquipo> listadoTipoEquipo;
    ArrayList<String> listadoMarcaEquipo;
    ArrayList<AsignacionEquipo> listadoEquipos;
    ArrayList<CentroCostoSubCentro> listadoCentroCostosSubCentro;
    ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliar;
    ArrayList<CentroCostoAuxiliar> listadoCentroCostoAuxiliarDestino;
    ArrayList<Cliente> listadoClientes;
    ArrayList<Articulo> listadoProductos;
    ArrayList<Motonave> listadoMotonaves;
    ArrayList<CentroOperacion> listadoCentroOperacion;
    ArrayList<LaborRealizada> listadoLaborRealizada;
    TextView usuarioCodigo,usuarioNombre,textView_BodegaDestino;
    Button bntIniciar,bntAtras;
    ArrayList<Recobro> listadoRecobro;
    ImageView recargarCliente,recargarProducto,recargarMotonave;
    TextView textSubCentro,textCentroCostoAuxiliar,texview_centroOperacion,textView_selectMotonave;
    private ZXingScannerView escanerView;

    TextView equipoCodigo,equipoTipoEquipo,equipoMarca,equipoModelo,equipoSerial,equipoDescripcion,equipoProveedorEquipo,
            asignacion_CentroOperacion,asignacion_SubcentroCosto,asignacion_auxCentroCosto,asignacion_laborRealizada,asignacion_FechaInicio,
            asignacion_FechaFin,asignacion_CantidadHorasProgram;

    Recobro recobroCliente;
    EditText EditText_buscarCliente,EditText_buscarProducto,EditText_buscarMotonave;
    AlertDialog.Builder builder;
    String typeConnection="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_equipo_iniciar_ciclo_equipo);
        listadoRecobro= new ArrayList<>();
        //Inicializamos los ArrayList
        listadoCentroCostosSubCentro= new ArrayList<>();
        listadoCentroCostoAuxiliar= new ArrayList<>();
        listadoCentroCostoAuxiliarDestino= new ArrayList<>();
        listadoClientes= new ArrayList<>();
        listadoProductos= new ArrayList<>();
        listadoMotonaves= new ArrayList<>();
        //listadoProveedorEquipo= new ArrayList<>();
        listadoLaborRealizada= new ArrayList<>();
        user= (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");

        listadoTipoEquipo= new ArrayList<>();
        listadoMarcaEquipo= new ArrayList<>();
        listadoEquipos= new ArrayList<>();
        listadoCentroOperacion= new ArrayList<>();

        //PlacaSeleccionada="";
        //FechaTaraSeleccionada="";
        recobroCliente= null;
        usuarioCodigo = (TextView)findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView)findViewById(R.id.usuarioNombre);
        textView_BodegaDestino = (TextView)findViewById(R.id.textView_BodegaDestino);
        //textView_Cliente = (TextView)findViewById(R.id.textView_Cliente);
        textSubCentro = (TextView)findViewById(R.id.textSubCentro);
        textCentroCostoAuxiliar = (TextView)findViewById(R.id.textCentroCostoAuxiliar);
        texview_centroOperacion = (TextView)findViewById(R.id.texview_centroOperacion);
        textView_selectMotonave = (TextView)findViewById(R.id.textView_selectMotonave);
        //textView_clienteSeleccionado = (TextView)findViewById(R.id.textView_clienteSeleccionado);
        //textView_ProductoSeleccionado = (TextView)findViewById(R.id.textView_ProductoSeleccionado);
        usuarioCodigo.setText(""+user.getCodigo());
        usuarioNombre.setText(""+user.getNombres()+" "+user.getApellidos());
        spinner_buscarCliente= (Spinner)findViewById(R.id.spinner_buscarCliente);
        spinner_buscarProducto= (Spinner)findViewById(R.id.spinner_buscarProducto);
        spinner_buscarMotonave= (Spinner)findViewById(R.id.spinner_buscarMotonave);
        spinnerRecobroCliente= (Spinner)findViewById(R.id.spinnerRecobroCliente);
        //spinnerEquipoCompartido= (Spinner)findViewById(R.id.spinnerEquipoCompartido);
        spinnerSeleccionarCliente= (Spinner)findViewById(R.id.spinnerSeleccionarCliente);
        spinnerSeleccionarProducto= (Spinner)findViewById(R.id.spinnerSeleccionarProducto);
        spinnerSeleccionarMotonave= (Spinner)findViewById(R.id.spinnerSeleccionarMotonave);
        recargarProducto= (ImageView)findViewById(R.id.recargarProducto);
        recargarCliente= (ImageView)findViewById(R.id.recargarCliente);
        recargarMotonave= (ImageView)findViewById(R.id.recargarMotonave);
        spinnerSubCentroCosto= (Spinner)findViewById(R.id.spinnerSubCentroCosto);
        spinnerAuxiliarCentroCosto= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCosto);
        spinnerAuxiliarCentroCostoDestino= (Spinner)findViewById(R.id.spinnerAuxiliarCentroCostoDestino);
        spinnerCentroOperacion= (Spinner)findViewById(R.id.spinnerCentroOperacion);
        spinnerlaborRealizar= (Spinner)findViewById(R.id.spinnerlaborRealizar);
        //spinner_proveedorEquipo= (Spinner)findViewById(R.id.spinner_proveedorEquipo);
        //placaSeleccionada= (TextView)findViewById(R.id.placaSeleccionada);
        EditText_buscarCliente = (EditText)findViewById(R.id.EditText_buscarCliente);
        EditText_buscarProducto = (EditText)findViewById(R.id.EditText_buscarProducto);
        EditText_buscarMotonave = (EditText)findViewById(R.id.EditText_buscarMotonave);

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
        asignacion_auxCentroCosto = (TextView)findViewById(R.id.asignacion_auxCentroCosto);
        asignacion_laborRealizada = (TextView)findViewById(R.id.asignacion_laborRealizada);
        asignacion_FechaInicio= (TextView)findViewById(R.id.asignacion_FechaInicio);
        asignacion_FechaFin= (TextView)findViewById(R.id.asignacion_FechaFin);
        asignacion_CantidadHorasProgram= (TextView)findViewById(R.id.asignacion_CantidadHorasProgram);
        bntIniciar= (Button)findViewById(R.id.bntIniciar);
        bntAtras= (Button)findViewById(R.id.bntAtras);
        //cargueInfo("Placa");
        cargueInfo("C.O");
        cargueInfo("list_recobro");
        cargueInfo("list_recobro");
        cargueInfo("list_compartido");
        cargueInfo("requiere_Cliente");
        cargueInfo("requiere_Producto");
        cargueInfo("requiere_Motonave");

        spinnerTipoEquipo= (Spinner)findViewById(R.id.spinnerTipoEquipo);
        spinnerMarcaEquipo= (Spinner)findViewById(R.id.spinnerMarcaEquipo);
        spinnerEquipo= (Spinner)findViewById(R.id.spinnerEquipo);
        //spinnerSubCentroCosto.setEnabled(false);
        //spinnerAuxiliarCentroCosto.setEnabled(false);

        //Ocultamos la bodegaDetino
        textView_BodegaDestino.setVisibility(View.INVISIBLE);
        spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);


        builder = new AlertDialog.Builder(this);

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

                    if(listadoCentroCostosSubCentro != null) {
                        validarCargueMotonave(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
                        /*if (listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getCodigo() == 3) {//Seleccionamos Subcentro Embarque por Tal motivo activamos la motonave
                            spinnerSeleccionarMotonave.setSelection(1);
                            textView_selectMotonave.setVisibility(View.VISIBLE);
                            spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                            EditText_buscarMotonave.setVisibility(View.VISIBLE);
                            recargarMotonave.setVisibility(View.VISIBLE);
                            spinner_buscarMotonave.setVisibility(View.VISIBLE);

                        } else {
                            spinnerSeleccionarMotonave.setSelection(0);
                            textView_selectMotonave.setVisibility(View.INVISIBLE);
                            spinnerSeleccionarMotonave.setVisibility(View.INVISIBLE);
                            EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                            recargarMotonave.setVisibility(View.INVISIBLE);
                            spinner_buscarMotonave.setVisibility(View.INVISIBLE);
                        }*/
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerAuxiliarCentroCosto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    /*if(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getDescripcion().equals("NO APLICA")){
                        spinnerSeleccionarMotonave.setSelection(1);
                        textView_selectMotonave.setVisibility(View.VISIBLE);
                        spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                        EditText_buscarMotonave.setVisibility(View.VISIBLE);
                        recargarMotonave.setVisibility(View.VISIBLE);
                        spinner_buscarMotonave.setVisibility(View.VISIBLE);
                        spinnerSeleccionarMotonave.setEnabled(false);

                    }else{
                        spinnerSeleccionarMotonave.setSelection(0);
                        //textView_selectMotonave.setVisibility(View.INVISIBLE);
                        //spinnerSeleccionarMotonave.setVisibility(View.INVISIBLE);
                        //EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                        //recargarMotonave.setVisibility(View.INVISIBLE);
                        //spinner_buscarMotonave.setVisibility(View.INVISIBLE);

                        textView_selectMotonave.setVisibility(View.VISIBLE);
                        spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                        EditText_buscarMotonave.setVisibility(View.VISIBLE);
                        recargarMotonave.setVisibility(View.VISIBLE);
                        spinner_buscarMotonave.setVisibility(View.VISIBLE);
                    }*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSeleccionarCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//NO
                    EditText_buscarCliente.setEnabled(false);
                    recargarCliente.setEnabled(false);
                    spinner_buscarCliente.setEnabled(false);
                    EditText_buscarCliente.setVisibility(View.INVISIBLE);
                    recargarCliente.setVisibility(View.INVISIBLE);
                    spinner_buscarCliente.setVisibility(View.INVISIBLE);
                }else{//SI
                    EditText_buscarCliente.setEnabled(true);
                    recargarCliente.setEnabled(true);
                    spinner_buscarCliente.setEnabled(true);
                    EditText_buscarCliente.setVisibility(View.VISIBLE);
                    recargarCliente.setVisibility(View.VISIBLE);
                    spinner_buscarCliente.setVisibility(View.VISIBLE);
                    cargueInfo("buscar_Cliente");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSeleccionarProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//NO
                    EditText_buscarProducto.setEnabled(false);
                    recargarProducto.setEnabled(false);
                    spinner_buscarProducto.setEnabled(false);
                    EditText_buscarProducto.setVisibility(View.INVISIBLE);
                    recargarProducto.setVisibility(View.INVISIBLE);
                    spinner_buscarProducto.setVisibility(View.INVISIBLE);

                }else{//SI
                    EditText_buscarProducto.setEnabled(true);
                    recargarProducto.setEnabled(true);
                    spinner_buscarProducto.setEnabled(true);
                    EditText_buscarProducto.setVisibility(View.VISIBLE);
                    recargarProducto.setVisibility(View.VISIBLE);
                    spinner_buscarProducto.setVisibility(View.VISIBLE);
                    cargueInfo("buscar_Producto");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSeleccionarMotonave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//NO
                    EditText_buscarMotonave.setEnabled(false);
                    recargarMotonave.setEnabled(false);
                    spinner_buscarMotonave.setEnabled(false);
                    EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                    recargarMotonave.setVisibility(View.INVISIBLE);
                    spinner_buscarMotonave.setVisibility(View.INVISIBLE);

                }else{//SI
                    EditText_buscarMotonave.setEnabled(true);
                    recargarMotonave.setEnabled(true);
                    spinner_buscarMotonave.setEnabled(true);
                    EditText_buscarMotonave.setVisibility(View.VISIBLE);
                    recargarMotonave.setVisibility(View.VISIBLE);
                    spinner_buscarMotonave.setVisibility(View.VISIBLE);
                    cargueInfo("buscar_Motonave");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //listadoEquipos

        spinnerlaborRealizar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")){
                    //textView_BodegaOrigen.setText("C.C. AUXILIAR ORIGEN");
                    textView_BodegaDestino.setVisibility(View.VISIBLE);
                    spinnerAuxiliarCentroCostoDestino.setVisibility(View.VISIBLE);
                }else{
                    textView_BodegaDestino.setVisibility(View.INVISIBLE);
                    spinnerAuxiliarCentroCostoDestino.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /*spinnerEquipoCompartido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){//NO
                    texview_centroOperacion.setVisibility(View.INVISIBLE);
                    spinnerCentroOperacion.setEnabled(false);
                    spinnerCentroOperacion.setVisibility(View.INVISIBLE);

                    spinnerSubCentroCosto.setEnabled(false);
                    spinnerAuxiliarCentroCosto.setEnabled(false);
                    //Ocultamos los Auxliares y SubcentroCostos
                    textSubCentro.setVisibility(View.INVISIBLE);
                    textCentroCostoAuxiliar.setVisibility(View.INVISIBLE);
                    spinnerSubCentroCosto.setVisibility(View.INVISIBLE);
                    spinnerAuxiliarCentroCosto.setVisibility(View.INVISIBLE);
                }else{//SI
                    texview_centroOperacion.setVisibility(View.VISIBLE);
                    spinnerCentroOperacion.setEnabled(true);
                    spinnerCentroOperacion.setVisibility(View.VISIBLE);
                    spinnerSubCentroCosto.setEnabled(true);
                    spinnerAuxiliarCentroCosto.setEnabled(true);

                    //Mostramos los Auxliares y SubcentroCostos
                    textSubCentro.setVisibility(View.VISIBLE);
                    textCentroCostoAuxiliar.setVisibility(View.VISIBLE);
                    spinnerSubCentroCosto.setVisibility(View.VISIBLE);
                    spinnerAuxiliarCentroCosto.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        /*
        spinner_buscarCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //textView_clienteSeleccionado.setText("Cliente Seleccionado:"+listadoClientes.get(spinner_buscarCliente.getSelectedItemPosition()).getDescripcion());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_buscarProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //textView_ProductoSeleccionado.setText("Producto Seleccionado:"+listadoProductos.get(spinner_buscarProducto.getSelectedItemPosition()).getDescripcion());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner_buscarMotonave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        EditText_buscarCliente.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
               // cs.toString();
                cargueInfo("buscar_Cliente");
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int k) { }
        });
        EditText_buscarProducto.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                //cs.toString();
                cargueInfo("buscar_Producto");
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int k) { }
        });
        EditText_buscarMotonave.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                //cs.toString();
                cargueInfo("buscar_Motonave");
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int k) { }
        });

        recargarCliente.setClickable(true);
        recargarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText_buscarCliente.setText("");
                cargueInfo("buscar_Cliente");
            }
        });

        recargarProducto.setClickable(true);
        recargarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText_buscarProducto.setText("");
                cargueInfo("buscar_Producto");
            }
        });
        recargarMotonave.setClickable(true);
        recargarMotonave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText_buscarMotonave.setText("");
                cargueInfo("buscar_Motonave");
            }
        });

        asignacion=(AsignacionEquipo) getIntent().getExtras().getSerializable("AsignacionEquipo");
        if(asignacion!=null) {
            //PlacaSeleccionada = getIntent().getExtras().getString("PlacaSeleccionada");
            //FechaTaraSeleccionada = getIntent().getExtras().getString("FechaTaraSeleccionada");
            recobroCliente =(Recobro) getIntent().getExtras().getSerializable("recobroCliente");
            if(recobroCliente !=null){
                if(recobroCliente.getDescripcion().equals("SI")){
                    spinnerRecobroCliente.setSelection(1);
                }else{
                    spinnerRecobroCliente.setSelection(0);
                }
            }/*
            String equipoCompartido =null;
            equipoCompartido=getIntent().getExtras().getString("equipoCompartido");
            if(equipoCompartido !=null){
                if(equipoCompartido.equals("SI")){
                    spinnerEquipoCompartido.setSelection(1);
                }else{
                    spinnerEquipoCompartido.setSelection(0);
                }
            }*/
            //System.out.println("LA PLACA FUE "+PlacaSeleccionada);
            //cargueInfo("PlacaEspecificaLectura");
            equipoCodigo.setText(""+asignacion.getEquipo().getCodigo());
            equipoTipoEquipo.setText(""+asignacion.getEquipo().getTipoEquipo().getDescripcion());
            equipoMarca.setText(""+asignacion.getEquipo().getMarca());
            equipoModelo.setText(""+asignacion.getEquipo().getModelo());
            equipoSerial.setText(""+asignacion.getEquipo().getSerial());
            equipoDescripcion.setText(""+asignacion.getEquipo().getDescripcion());
            equipoProveedorEquipo.setText(""+asignacion.getEquipo().getProveedorEquipo().getDescripcion());
            /*int contador=0;
            for(ProveedorEquipo proveedorEquip : listadoProveedorEquipo){
                if(asignacion.getEquipo().getProveedorEquipo().getCodigo().equals(proveedorEquip.getCodigo())){
                    spinner_proveedorEquipo.setSelection(contador);
                }
                contador++;
            }*/

            asignacion_CentroOperacion.setText(""+asignacion.getCentroOperacion().getDescripcion());
            asignacion_SubcentroCosto.setText(""+asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
            asignacion_auxCentroCosto.setText(""+asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion());
            asignacion_laborRealizada.setText(""+asignacion.getSolicitudListadoEquipo().getLaborRealizada().getDescripcion());




            //Validamos si se carga la motonave
            validarCargueMotonave(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro());

            //Cargamos los CentroOperación, SubCentroCostos y los CentroCostosAuxiliares según la asignación cargada
            cargaParametrosEquipoSeleccion(asignacion);


            asignacion_FechaInicio.setText(""+asignacion.getFechaHoraInicio());
            asignacion_FechaFin.setText(""+asignacion.getFechaHoraFin());
            try{
                asignacion_CantidadHorasProgram.setText(""+(Integer.parseInt(asignacion.getCantidadMinutosProgramados())/60));
            }catch(Exception e){
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
                builder.setMessage("No se pudo cargar las labores Realizadas, valide datos..");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
            spinnerlaborRealizar.setAdapter(adaptadorListado);

        }else{
            System.out.println("Asignacion nula");
        }
        bntIniciar.setOnClickListener(new View.OnClickListener() {
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
                                   Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, Modulo_Equipo_cierre_StandBy.class);
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
                       } else {
                           //El vehiculo fue marcado como compartido
                           //AlertDialog.Builder alertbox = new AlertDialog.Builder(ModuloCarbon_consultarPlacas_Internas.this);
                           //alertbox.setMessage("Está seguro que desea iniciar el descargue");
                           //alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                           //public void onClick(DialogInterface arg0, int arg1) {
                           MvtoEquipo mvtoEquipo = new MvtoEquipo();
                           mvtoEquipo.setAsignacionEquipo(asignacion);
                           //mvtoEquipo.setFechaRegistro(); sale del sistema
                           mvtoEquipo.setProveedorEquipo(asignacion.getEquipo().getProveedorEquipo());
                           mvtoEquipo.setNumeroOrden("");
                           mvtoEquipo.setCentroOperacion(asignacion.getCentroOperacion());
                           boolean validator = true;
                           /*if (spinnerEquipoCompartido.getSelectedItem().toString().equals("SI")) {
                               if (listadoCentroOperacion == null || listadoCentroCostoAuxiliar == null || listadoLaborRealizada == null) {  //Valida si se cargo un CentroOperacion, AuxiliarCentroCosto,LaborRealizada
                                   validator = false;
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
                               } else {*/
                                   //mvtoEquipo.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));
                              // }

                           //} else {
                              // mvtoEquipo.setCentroCostoAuxiliar(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar());
                           //}
                           if(listadoCentroOperacion == null || listadoCentroCostoAuxiliar == null || listadoLaborRealizada == null){
                               validator = false;
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
                           }else{
                               mvtoEquipo.setCentroCostoAuxiliar(listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()));
                           }
                           if(validator){//validamos si se seleccionó Embarque de ser así cargamos la motonave y si en Centro de costo Auxiliar seleccionó NO Aplica la motonave es obligatoria
                               if( listadoCentroCostoAuxiliar != null ) {
                                   if (listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getCentroCostoSubCentro().getCodigo() == 3) {//Seleccionó embarque
                                       if (listadoCentroCostoAuxiliar.get(spinnerAuxiliarCentroCosto.getSelectedItemPosition()).getCodigo().equals("26")) {//Seleccionó no Aplica en Embarque
                                           if (listadoMotonaves != null) {
                                               if (spinnerSeleccionarMotonave.getSelectedItem().toString().equals("SI")) {
                                                   mvtoEquipo.setMotonave(listadoMotonaves.get(spinner_buscarMotonave.getSelectedItemPosition()));
                                               } else {//
                                                   validator = false;
                                                   builder = new AlertDialog.Builder(builder.getContext());
                                                   builder.setTitle("Alerta!!");
                                                   builder.setMessage("Verifique que tenga cargada una motonave ya que es obligatoria");
                                                   builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           validadorPresionaBotonIniciar = true;
                                                       }
                                                   });
                                                   builder.show();
                                               }
                                           } else {//No hay Motonaves activas en el sistema
                                               validator = false;
                                               builder = new AlertDialog.Builder(builder.getContext());
                                               builder.setTitle("Alerta!!");
                                               builder.setMessage("No hay motonaves activas en el sistema");
                                               builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       validadorPresionaBotonIniciar = true;
                                                   }
                                               });
                                               builder.show();
                                           }
                                       } else {//Seleccionó Embarque pero otro centroCostoAuxiliar diferente a NO APLICA, por tal motivo la motonave no es obligatoria
                                           if (spinnerSeleccionarMotonave.getSelectedItem().toString().equals("NO")) {
                                               Motonave motonave = new Motonave();
                                               motonave.setCodigo(null);
                                               motonave.setDescripcion(null);
                                               motonave.setEstado(null);
                                               motonave.setBaseDatos(new BaseDatos(null));
                                               mvtoEquipo.setMotonave(motonave);
                                           } else {
                                               if (spinnerSeleccionarMotonave.getSelectedItem().toString().equals("SI")) {
                                                   mvtoEquipo.setMotonave(listadoMotonaves.get(spinner_buscarMotonave.getSelectedItemPosition()));
                                               }
                                           }
                                       }
                                   } else {//No seleccionó embarque
                                       if (spinnerSeleccionarMotonave.getSelectedItem().toString().equals("NO")) {
                                           Motonave motonave = new Motonave();
                                           motonave.setCodigo(null);
                                           motonave.setDescripcion(null);
                                           motonave.setEstado(null);
                                           motonave.setBaseDatos(new BaseDatos(null));
                                           mvtoEquipo.setMotonave(motonave);
                                       } else {
                                           if (spinnerSeleccionarMotonave.getSelectedItem().toString().equals("SI")) {
                                               mvtoEquipo.setMotonave(listadoMotonaves.get(spinner_buscarMotonave.getSelectedItemPosition()));
                                           }
                                       }
                                   }


                               }else{//No hay activado Centro de costo auxiliar
                                   validator = false;
                                   builder = new AlertDialog.Builder(builder.getContext());
                                   builder.setTitle("Alerta!!");
                                   builder.setMessage("Verifique que tenga cargado un sitio Origen (CentroCosto Auxiliar)");
                                   builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           validadorPresionaBotonIniciar=true;
                                       }
                                   });
                                   builder.show();
                               }
                           }
                           if (validator) {
                               //LaborRealizada laborRealizada = new LaborRealizada();
                               //laborRealizada.setCodigo("0");
                               //laborRealizada.setDescripcion("DESCARGUE");
                               //laborRealizada.setEstado("ACTIVO");
                               //mvtoEquipo.setLaborRealizada(laborRealizada);
                               if (listadoLaborRealizada != null) {
                                   mvtoEquipo.setLaborRealizada(listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()));

                                   if (listadoLaborRealizada.get(spinnerlaborRealizar.getSelectedItemPosition()).getBodegaDestino().equals("1")) {
                                       mvtoEquipo.setCentroCostoAuxiliarDestino(listadoCentroCostoAuxiliarDestino.get(spinnerAuxiliarCentroCostoDestino.getSelectedItemPosition()));
                                   } else {
                                       CentroCostoAuxiliar ccAuxiliar = new CentroCostoAuxiliar();
                                       ccAuxiliar.setCodigo("NULL");
                                       //Objeto.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                       mvtoEquipo.setCentroCostoAuxiliarDestino(ccAuxiliar);
                                   }
                               }
                               //mvtoEquipo.setProveedorEquipo(listadoProveedorEquipo.get(spinner_proveedorEquipo.getSelectedItemPosition()));

                               if (spinnerSeleccionarCliente.getSelectedItem().toString().equals("NO")) {
                                   Cliente cliente = new Cliente();
                                   cliente.setCodigo(null);
                                   cliente.setDescripcion(null);
                                   cliente.setEstado(null);
                                   mvtoEquipo.setCliente(cliente);
                               } else {
                                   if (spinnerSeleccionarCliente.getSelectedItem().toString().equals("SI")) {
                                       mvtoEquipo.setCliente(listadoClientes.get(spinner_buscarCliente.getSelectedItemPosition()));
                                   }
                               }

                               if (spinnerSeleccionarProducto.getSelectedItem().toString().equals("NO")) {
                                   Articulo articulo = new Articulo();
                                   articulo.setCodigo(null);
                                   articulo.setDescripcion(null);
                                   articulo.setEstado(null);
                                   mvtoEquipo.setArticulo(articulo);
                               } else {
                                   if (spinnerSeleccionarProducto.getSelectedItem().toString().equals("SI")) {
                                       mvtoEquipo.setArticulo(listadoProductos.get(spinner_buscarProducto.getSelectedItemPosition()));
                                   }
                               }




                               //mvtoEquipo.setProducto(); el producto es nulo, es de acuerdo al descargue de carbon
                               //mvtoEquipo.setFechaHoraInicio(); esta fecha de inicio la toma del sistema
                               //mvtoEquipo.setFechaHoraFin(); is NULL porque es un Inicio de Operacion
                               //mvtoEquipo.setTotalHora();is NULL porque es un Inicio de Operacion
                               //mvtoEquipo.setValorHora();is NULL porque es un Inicio de Operacion
                               mvtoEquipo.setRecobro(listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                               //mvtoEquipo.setClienteRecobro();is NULL porque es un Inicio de Operacion
                               //mvtoEquipo.setCostoTotalRecobroCliente();is NULL porque es un Inicio de Operacion
                               mvtoEquipo.setUsuarioQuieRegistra(user);
                               //mvtoEquipo.setUsuarioAutorizaRecobro();is NULL porque es un Inicio de Operacion
                               mvtoEquipo.setEstado("1");
                               mvtoEquipo.setDesdeCarbon("0");
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
                                   //Tenemos 3 Objetos     (MvtoCarbon Objeto)   (MvtoEquipo mvtoEquipo) (AsignacionEquipo asignacion)
                                   try {

                                       int result = new ControlDB_Equipo(typeConnection).registrarMvtoEquipo(user, mvtoEquipo);
                                       if (result == 1) {
                                           //bntIniciarDescargue.setEnabled(false);
                                           //mensaje("Se registro el inicio de la actividad de manera exitosa");
                                           builder.setTitle("Registro Exitoso!!");
                                           builder.setMessage("Se inicio la Actividad realizada por el equipo de forma exitosa");
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
                                                           asignacion_laborRealizada.setText("");
                                                           asignacion_FechaInicio.setText("");
                                                           asignacion_FechaFin.setText("");
                                                           asignacion_CantidadHorasProgram.setText("");

                                                           EditText_buscarCliente.setText("");
                                                           EditText_buscarProducto.setText("");
                                                           Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, Menu.class);
                                                           interfaz.putExtra("usuarioB", user);
                                                           interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                                                           interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
                                                           startActivity(interfaz);
                                                       }
                                                   }
                                           );
                                           builder.show();
                                       } else {
                                           //mensaje("Error al registrar el inicio de la actividad");
                                           builder.setTitle("Error!!");
                                           builder.setMessage("No se pudo registrar el inicio de la actividad con el equipo, valide datos..");
                                           builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   //Hacer cosas aqui al hacer clic en el boton de aceptar
                                                   validadorPresionaBotonIniciar=true;
                                               }
                                           });
                                           builder.show();
                                       }
                                   } catch (FileNotFoundException e) {
                                       e.printStackTrace();
                                   } catch (UnknownHostException e) {
                                       e.printStackTrace();
                                   } catch (SocketException e) {
                                       e.printStackTrace();
                                   }
                               }
                           }
                       }
                   } else {
                       //mensaje("Error!!. Se debe cargar una programación de equipo a través del escaneo de código de barra");
                       builder.setTitle("Error!!");
                       builder.setMessage("Se debe cargar una programación de equipo a través del escaneo de código de barra, valide datos..");
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
        bntAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, Menu.class);
                interfaz.putExtra("usuarioB",  user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
            }
        });
    }
    public void cargueInfo(String opcion)  {
        switch (opcion){
            case "buscar_Cliente":{
                ArrayList<String> arrayListado =new ArrayList<>();
                System.out.println("Placa reconocida: "+EditText_buscarCliente.getText().toString());
                ControlDB_Equipo controlDB_Equipo = new ControlDB_Equipo(typeConnection);
                try {
                    listadoClientes = controlDB_Equipo.buscarClientes(EditText_buscarCliente.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //arrayListado.add("");
                for (Cliente listadoObjeto : listadoClientes) {
                    arrayListado.add(listadoObjeto.getDescripcion());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinner_buscarCliente.setAdapter(adaptadorListado);
                break;
            }
            case "buscar_Producto":{
                ArrayList<String> arrayListado =new ArrayList<>();
                //System.out.println("Placa reconocida: "+EditText_buscarProducto.getText().toString());
                ControlDB_Equipo controlDB_Equipo = new ControlDB_Equipo(typeConnection);
                try {
                    listadoProductos = controlDB_Equipo.buscarArticulos(EditText_buscarProducto.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //arrayListado.add("");
                for (Articulo listadoObjeto : listadoProductos) {
                    arrayListado.add(listadoObjeto.getDescripcion());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinner_buscarProducto.setAdapter(adaptadorListado);
                break;
            }
            case "buscar_Motonave":{
                ArrayList<String> arrayListado =new ArrayList<>();
                //System.out.println("Placa reconocida: "+EditText_buscarProducto.getText().toString());
                ControlDB_Equipo controlDB_Equipo = new ControlDB_Equipo(typeConnection);
                try {
                    listadoMotonaves = controlDB_Equipo.buscarMotonavesControlCarga(EditText_buscarMotonave.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //arrayListado.add("");
                for (Motonave listadoObjeto : listadoMotonaves) {
                    arrayListado.add(listadoObjeto.getDescripcion());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinner_buscarMotonave.setAdapter(adaptadorListado);
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

                if(listadoCentroCostosSubCentro != null) {
                    validarCargueMotonave(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
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
                validarCargueMotonave(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()));
                /*if(listadoCentroCostosSubCentro != null) {
                    if(listadoCentroCostosSubCentro.get(spinnerSubCentroCosto.getSelectedItemPosition()).getCodigo()==3){//Seleccionamos Subcentro Embarque por Tal motivo activamos la motonave
                        spinnerSeleccionarMotonave.setSelection(1);
                        textView_selectMotonave.setVisibility(View.VISIBLE);
                        spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                        EditText_buscarMotonave.setVisibility(View.VISIBLE);
                        recargarMotonave.setVisibility(View.VISIBLE);
                        spinner_buscarMotonave.setVisibility(View.VISIBLE);

                    }else{
                        spinnerSeleccionarMotonave.setSelection(0);
                        textView_selectMotonave.setVisibility(View.INVISIBLE);
                        spinnerSeleccionarMotonave.setVisibility(View.INVISIBLE);
                        EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                        recargarMotonave.setVisibility(View.INVISIBLE);
                        spinner_buscarMotonave.setVisibility(View.INVISIBLE);
                    }
                }*/

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
            case "requiere_Cliente":{
                ArrayList<String> arrayListadoT =new ArrayList<>();
                arrayListadoT.add("NO");
                arrayListadoT.add("SI");
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                spinnerSeleccionarCliente.setAdapter(adaptadorListado);
                break;
            }
            case "requiere_Producto":{
                ArrayList<String> arrayListadoT =new ArrayList<>();
                arrayListadoT.add("NO");
                arrayListadoT.add("SI");
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                spinnerSeleccionarProducto.setAdapter(adaptadorListado);
                break;
            }
            case "requiere_Motonave":{
                ArrayList<String> arrayListadoT =new ArrayList<>();
                arrayListadoT.add("NO");
                arrayListadoT.add("SI");
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListadoT);
                spinnerSeleccionarMotonave.setAdapter(adaptadorListado);
                break;
            }
            /*case "carga_listadoProveedorEquipo":{
                ArrayList<String> arrayListado =new ArrayList<>();
                try {
                    ControlDB_Equipo controlDB_Equipo = new ControlDB_Equipo(typeConnection);
                    listadoProveedorEquipo = controlDB_Equipo.buscarProveedoresEquipos();
                    for (ProveedorEquipo listadoObjeto : listadoProveedorEquipo) {
                        arrayListado.add(listadoObjeto.getDescripcion());
                    }
                }catch (SQLException e){
                    //mensaje("No se pudo consultar los Proveedores de Equipos");
                    builder.setTitle("Error!!");
                    builder.setMessage("No se pudo consultar los Proveedores de Equipos, valide datos..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinner_proveedorEquipo.setAdapter(adaptadorListado);
                break;
            }*/
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

    @Override
    public void handleResult(Result rawResult) {
        String codigoEquipo=rawResult.getText();
        //Validamos Si el Equipo Existe en el sistema
        ControlDB_Equipo controlDB_equipo = new ControlDB_Equipo(typeConnection);
        try {
            Equipo equipo = new Equipo();
            equipo.setCodigo(codigoEquipo);
            AsignacionEquipo asignacionEquipo= new ControlDB_AsignacionEquipo(typeConnection).buscarAsignacionPorEquipo(equipo);
            if(asignacionEquipo !=null){
                Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, modulo_equipo_iniciar_ciclo_equipo.class);
                interfaz.putExtra("AsignacionEquipo",  asignacionEquipo);
                interfaz.putExtra("usuarioB",  user);
                interfaz.putExtra("recobroCliente",  listadoRecobro.get(spinnerRecobroCliente.getSelectedItemPosition()));
                //interfaz.putExtra("equipoCompartido",  spinnerEquipoCompartido.getSelectedItem().toString());
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
                onPause();
            }else{
                //mensaje("No Hay asignación programada para el equipo escaneado");
                    builder.setTitle("Alerta!!");
                    builder.setMessage("No Hay asignación programada para el equipo escaneado..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, modulo_equipo_iniciar_ciclo_equipo.class);
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
                    Intent interfaz = new Intent(modulo_equipo_iniciar_ciclo_equipo.this, Menu.class);
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

    //Validaciones de Información
    public void listenerSpinner(){
        spinnerTipoEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("list_marcaEquipo");
                cargueInfo("list_Equipo");
                if(listadoEquipos != null){
                    try {
                        cargaParametrosEquipoSeleccion(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()));
                        validarCargueMotonave(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()).getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerMarcaEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("list_Equipo");
                if(listadoEquipos != null){
                    try {
                        cargaParametrosEquipoSeleccion(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()));
                        validarCargueMotonave(listadoEquipos.get(spinnerEquipo.getSelectedItemPosition()).getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

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
                    if(asignacion.getSolicitudListadoEquipo().getLaborRealizada() !=null){
                        asignacion_laborRealizada.setText(""+asignacion.getSolicitudListadoEquipo().getLaborRealizada().getDescripcion());
                    }else{
                        asignacion_laborRealizada.setText("");
                    }

                    //Validamos si se carga la motonave
                    if(listadoEquipos != null){
                        try {
                            cargaParametrosEquipoSeleccion(asignacion);
                            validarCargueMotonave(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                        /*if(asignacion.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getDescripcion().equals("NO APLICA")){
                            spinnerSeleccionarMotonave.setSelection(1);
                            textView_selectMotonave.setVisibility(View.VISIBLE);
                            spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                            EditText_buscarMotonave.setVisibility(View.VISIBLE);
                            recargarMotonave.setVisibility(View.VISIBLE);
                            spinner_buscarMotonave.setVisibility(View.VISIBLE);
                        }else{
                            spinnerSeleccionarMotonave.setSelection(0);
                            textView_selectMotonave.setVisibility(View.INVISIBLE);
                            spinnerSeleccionarMotonave.setVisibility(View.INVISIBLE);
                            EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                            recargarMotonave.setVisibility(View.INVISIBLE);
                            spinner_buscarMotonave.setVisibility(View.INVISIBLE);
                        }*/

                    asignacion_FechaInicio.setText("" + asignacion.getFechaHoraInicio());
                    asignacion_FechaFin.setText("" + asignacion.getFechaHoraFin());
                    try {
                        asignacion_CantidadHorasProgram.setText("" + (Integer.parseInt(asignacion.getCantidadMinutosProgramados()) / 60));
                    } catch (Exception e) {
                        asignacion_CantidadHorasProgram.setText("");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void cargaParametrosEquipoSeleccion(AsignacionEquipo asigEquipo){
        if(asigEquipo != null){
            try {
                if (listadoCentroOperacion != null) {//Cargamos el centro de Operación de la asignación
                    int contadorO = 0;
                    for (CentroOperacion ctrOperacion : listadoCentroOperacion) {
                        if (asigEquipo.getCentroOperacion().getCodigo() == ctrOperacion.getCodigo()) {
                            spinnerCentroOperacion.setSelection(contadorO);
                        }
                        contadorO++;
                    }
                } else {//El listado de Centro de Operación es nulo por tal motivo no se hace nada
                    System.out.println("El listado de centro de operacion es nulo");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (listadoCentroCostosSubCentro != null) {
                    int contadorS = 0;
                    for (CentroCostoSubCentro ctroCostoSubcentro : listadoCentroCostosSubCentro) {
                        if (asigEquipo.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo() == ctroCostoSubcentro.getCodigo()) {
                            spinnerSubCentroCosto.setSelection(contadorS);
                        }
                        contadorS++;
                    }
                } else {
                    System.out.println("El listado de CentroCostoSubCentro es nulo");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (listadoCentroCostoAuxiliar != null) {
                    int contadorA = 0;
                    for (CentroCostoAuxiliar ctroCostoAuxiliar : listadoCentroCostoAuxiliar) {
                        if (asigEquipo.getSolicitudListadoEquipo().getCentroCostoAuxiliar().getCodigo() == ctroCostoAuxiliar.getCodigo()) {
                            spinnerAuxiliarCentroCosto.setSelection(contadorA);
                        }
                        contadorA++;
                    }
                } else {
                    System.out.println("El listado de CentroCostoAuxiliar es nulo");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try{//validamos si la asignación tiene una motonave cargada para mostrarla en la interfaz
                if(asigEquipo.getSolicitudListadoEquipo().getMotonave().getCodigo() !=null){
                    if(listadoMotonaves != null){
                        int contadorM=0;
                        for(Motonave mtnave : listadoMotonaves){
                            if(asigEquipo.getSolicitudListadoEquipo().getMotonave().getCodigo().equals(mtnave.getCodigo()) &&
                                    asigEquipo.getSolicitudListadoEquipo().getMotonave().getBaseDatos().getCodigo().equals(mtnave.getBaseDatos().getCodigo())){
                                spinner_buscarMotonave.setSelection(contadorM);
                            }
                            contadorM++;
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            try{//validamos si la actividad tiene programada una actividad en particular para cargarla en la interfaz
                if(asigEquipo.getSolicitudListadoEquipo().getLaborRealizada() !=null){
                    if(listadoLaborRealizada != null){
                        int contadorL=0;
                        for(LaborRealizada laborRealizada : listadoLaborRealizada){
                            if(asigEquipo.getSolicitudListadoEquipo().getLaborRealizada().getCodigo().equals(laborRealizada.getCodigo())){
                                spinnerlaborRealizar.setSelection(contadorL);
                            }
                            contadorL++;
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{//La asignación es nula
            System.out.println("La asignación es nula");
        }
    }
    public void validarCargueMotonave(CentroCostoSubCentro subCentro){
        /*Metodo que permite validar si hemos seleccionado el Subcentro Embarque y de ser seleccionado activa el selector de motonave*/
        if(subCentro != null){
            if (subCentro.getCodigo() == 3) {//Seleccionamos Embarque por tal motivo cargamos las motonaves
                spinnerSeleccionarMotonave.setEnabled(true);
                spinnerSeleccionarMotonave.setSelection(1);
                textView_selectMotonave.setVisibility(View.VISIBLE);
                spinnerSeleccionarMotonave.setVisibility(View.VISIBLE);
                EditText_buscarMotonave.setVisibility(View.VISIBLE);
                recargarMotonave.setVisibility(View.VISIBLE);
                spinner_buscarMotonave.setVisibility(View.VISIBLE);
                //spinnerSeleccionarMotonave.setEnabled(false);
            }else{
                spinnerSeleccionarMotonave.setSelection(0);
                textView_selectMotonave.setVisibility(View.INVISIBLE);
                spinnerSeleccionarMotonave.setVisibility(View.INVISIBLE);
                EditText_buscarMotonave.setVisibility(View.INVISIBLE);
                recargarMotonave.setVisibility(View.INVISIBLE);
                spinner_buscarMotonave.setVisibility(View.INVISIBLE);
            }
        }
    }



    //Borrar
        /*public void mensaje(String mensaje){
        int toastDuration = 10000; // in MilliSeconds
        final Toast mToast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        CountDownTimer countDownTimer= new CountDownTimer(toastDuration, 1000) {
            public void onTick(long millisUntilFinished) { mToast.show(); }
            public void onFinish(){ mToast.cancel(); } }; mToast.show(); countDownTimer.start();
    }*/
}
