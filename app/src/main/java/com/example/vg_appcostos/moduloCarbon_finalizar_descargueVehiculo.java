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
import com.example.vg_appcostos.ModuloCarbon.Modelo.MotivoNoLavado;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon_ListadoEquipos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.LaborRealizada;
import com.example.vg_appcostos.ModuloEquipo.Model.MotivoParada;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class moduloCarbon_finalizar_descargueVehiculo extends AppCompatActivity implements Serializable {
    Usuario user = null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    Spinner spinnerPlaca,spinnerListadoEquipos,spinnerlavadoVehiculo,spinnerMotivoLavadolistadoVehículo;
    MvtoCarbon Objeto;
    Button bntFinalizarDescargue,Btn_Atras;
    ArrayList<MvtoCarbon> listadoMvtoCarbon;
    ArrayList<MotivoNoLavado> listadoMotivoNoLavadoVehiculo;
    ArrayList <String> listadoEquiposLavado;
    TextView usuarioCodigo, usuarioNombre,
            placaSeleccionada,
            articulo, cliente, transportadora, orden, deposito,
            pesoLleno, fechaTara, fechaInicioDescargue, centroOperacion, subcentroCosto,
            auxCentroCosto,usuarioQuieRegistra,estadoMvtoCarbon,text_BodegaDestino,text_laborRealizada,textViewLavadoDeVehiculo,textViewMensajeLavadoVehiculo,TextView_observacion;
    EditText placa,actividad_observacion;
    ImageView recargarPlaca;
    String PlacaSeleccionada;
    String FechaTaraSeleccionada;
    AlertDialog.Builder builder;
    String typeConnection="";
    ArrayList<MvtoCarbon_ListadoEquipos> listado_mvtoCarbon_ListadoEquipos=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_carbon__finalizar_descargue_vehiculo);
        listadoMvtoCarbon = new ArrayList<>();
        listadoMotivoNoLavadoVehiculo=null;
        listadoEquiposLavado=null;
        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        PlacaSeleccionada = "";
        FechaTaraSeleccionada = "";
        usuarioCodigo = (TextView) findViewById(R.id.usuarioCodigo);
        usuarioNombre = (TextView) findViewById(R.id.usuarioNombre);
        usuarioCodigo.setText("" + user.getCodigo());
        usuarioNombre.setText("" + user.getNombres() + " " + user.getApellidos());
        spinnerPlaca = (Spinner) findViewById(R.id.spinnerPlaca);
        spinnerListadoEquipos = (Spinner) findViewById(R.id.spinnerListadoEquipos);
        spinnerlavadoVehiculo = (Spinner) findViewById(R.id.spinnerlavadoVehiculo);
        spinnerMotivoLavadolistadoVehículo = (Spinner) findViewById(R.id.spinnerMotivoLavadolistadoVehículo);
        recargarPlaca = (ImageView) findViewById(R.id.recargarPlaca);
        articulo = (TextView) findViewById(R.id.articulo);
        cliente = (TextView) findViewById(R.id.cliente);
        transportadora = (TextView) findViewById(R.id.transportadora);
        orden = (TextView) findViewById(R.id.orden);
        deposito = (TextView) findViewById(R.id.deposito);
        pesoLleno = (TextView) findViewById(R.id.pesoLleno);
        fechaTara = (TextView) findViewById(R.id.fechaTara);
        fechaInicioDescargue = (TextView) findViewById(R.id.fechaInicioDescargue);
        centroOperacion = (TextView) findViewById(R.id.centroOperacion);
        subcentroCosto = (TextView) findViewById(R.id.subcentroCosto);
        auxCentroCosto = (TextView) findViewById(R.id.auxCentroCosto);
        usuarioQuieRegistra = (TextView) findViewById(R.id.usuarioQuieRegistra);
        estadoMvtoCarbon = (TextView) findViewById(R.id.estadoMvtoCarbon);
        text_BodegaDestino = (TextView) findViewById(R.id.text_BodegaDestino);
        text_laborRealizada = (TextView) findViewById(R.id.text_laborRealizada);
        placaSeleccionada = (TextView) findViewById(R.id.placaSeleccionada);
        textViewLavadoDeVehiculo = (TextView) findViewById(R.id.textViewLavadoDeVehiculo);
        textViewMensajeLavadoVehiculo = (TextView) findViewById(R.id.textViewMensajeLavadoVehiculo);
        TextView_observacion = (TextView) findViewById(R.id.TextView_observacion);
        placa = (EditText) findViewById(R.id.placa);
        actividad_observacion = (EditText) findViewById(R.id.actividad_observacion);
        bntFinalizarDescargue = (Button) findViewById(R.id.bntFinalizarDescargue);
        Btn_Atras = (Button) findViewById(R.id.Btn_Atras);
        cargueInfo("Placa");
        builder = new AlertDialog.Builder(this);

        //Cargamos el selector de lavadoVehículo
        //Cargamos las labores Realizadas de acuerdo a la asignación
        try {
            ArrayList<String> arrayListado = new ArrayList<>();
            arrayListado.add("SI");
            arrayListado.add("NO");
            ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
            spinnerlavadoVehiculo.setAdapter(adaptadorListado);
        }catch (Exception e){
            e.printStackTrace();
        }


        textViewLavadoDeVehiculo.setVisibility(View.INVISIBLE);
        spinnerlavadoVehiculo.setVisibility(View.INVISIBLE);
        textViewMensajeLavadoVehiculo.setVisibility(View.INVISIBLE);
        spinnerMotivoLavadolistadoVehículo.setVisibility(View.INVISIBLE);
        TextView_observacion.setVisibility(View.INVISIBLE);
        actividad_observacion.setVisibility(View.INVISIBLE);

        spinnerlavadoVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerlavadoVehiculo.getSelectedItemPosition()==0){//Seleccionó SI
                    textViewLavadoDeVehiculo.setVisibility(View.VISIBLE);
                    spinnerlavadoVehiculo.setVisibility(View.VISIBLE);
                    textViewMensajeLavadoVehiculo.setVisibility(View.VISIBLE);
                    textViewMensajeLavadoVehiculo.setText("Seleccione quien realizará el lavado del vehículo:");
                    spinnerMotivoLavadolistadoVehículo.setVisibility(View.VISIBLE);
                    cargueInfo("CargarEquipos_LavadoVehiculo");
                    TextView_observacion.setVisibility(View.VISIBLE);
                    actividad_observacion.setVisibility(View.VISIBLE);
                }else{//Seleccionó NO
                    textViewLavadoDeVehiculo.setVisibility(View.VISIBLE);
                    spinnerlavadoVehiculo.setVisibility(View.VISIBLE);
                    textViewMensajeLavadoVehiculo.setVisibility(View.VISIBLE);
                    textViewMensajeLavadoVehiculo.setText("Seleccione motivo del no lavado del vehículo:");
                    spinnerMotivoLavadolistadoVehículo.setVisibility(View.VISIBLE);
                    cargueInfo("CargarMotivosNoLavadoVehiculo");
                    TextView_observacion.setVisibility(View.VISIBLE);
                    actividad_observacion.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPlaca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargueInfo("PlacaEspecifica");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        placa.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                System.out.println("Key:" + cs.toString());
                Pattern pat = Pattern.compile("^[a-zA-Z]{3}\\d{3}");
                Matcher mat = pat.matcher(cs.toString());
                if (mat.matches()) {
                    System.out.println("Placa correcta");
                    cargueInfo("PlacaBuscar");
                    System.out.println("" + spinnerPlaca.getCount());
                    //if (spinnerPlaca.getCount() > 2) {//Selecionamos la segunda Placa, la primera un un string vacio
                        for (MvtoCarbon mvtonCar : listadoMvtoCarbon) {
                            if (mvtonCar.getPlaca().equalsIgnoreCase(placa.getText().toString())) {
                                System.out.println(mvtonCar.getArticulo().getDescripcion());
                                placaSeleccionada.setText("" + mvtonCar.getPlaca());
                                articulo.setText("" + mvtonCar.getArticulo().getDescripcion());
                                cliente.setText("" + mvtonCar.getCliente().getDescripcion());
                                transportadora.setText("" + mvtonCar.getTransportadora().getDescripcion());
                                //orden.setText("ORDEN: " + Objeto.getOrden());
                                deposito.setText("" + mvtonCar.getDeposito());
                                pesoLleno.setText("" + mvtonCar.getPesoVacio());
                                fechaTara.setText("" + mvtonCar.getFechaEntradaVehiculo());
                                fechaInicioDescargue.setText("" + mvtonCar.getFechaInicioDescargue());
                                centroOperacion.setText("" + mvtonCar.getCentroOperacion().getDescripcion());
                                subcentroCosto.setText("" + mvtonCar.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                                auxCentroCosto.setText("" + mvtonCar.getCentroCostoAuxiliar().getDescripcion());
                                usuarioQuieRegistra.setText("" + mvtonCar.getUsuarioRegistroMovil().getNombres() +" "+ mvtonCar.getUsuarioRegistroMovil().getApellidos());
                                estadoMvtoCarbon.setText("" + mvtonCar.getEstadoMvtoCarbon().getDescripcion());
                                text_BodegaDestino.setText("" + mvtonCar.getCentroCostoAuxiliarDestino().getDescripcion());
                                text_laborRealizada.setText("" + mvtonCar.getLaborRealizada().getDescripcion());
                                Objeto = mvtonCar;
                                spinnerPlaca.setSelection(1);
                                cargueInfo("list_equipos");
                            }
                        }
                   // }
                } else {
                    System.out.println("Placa Incorrecta");
                    System.out.println("" + spinnerPlaca.getCount());
                }
                if (cs.toString().equals("")) {
                    cargueInfo("Placa");
                }
            }
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence cs, int i, int j, int k) {
            }
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
        textViewLavadoDeVehiculo.setVisibility(View.INVISIBLE);
        spinnerlavadoVehiculo.setVisibility(View.INVISIBLE);
        textViewMensajeLavadoVehiculo.setVisibility(View.INVISIBLE);
        spinnerMotivoLavadolistadoVehículo.setVisibility(View.INVISIBLE);
        TextView_observacion.setVisibility(View.INVISIBLE);
        actividad_observacion.setVisibility(View.INVISIBLE);

        bntFinalizarDescargue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bntFinalizarDescargue.setEnabled(false);
                if (Objeto != null) {
                    try {
                        listado_mvtoCarbon_ListadoEquipos=null;
                        listado_mvtoCarbon_ListadoEquipos=new ControlDB_Carbon(typeConnection).buscarPlaca_EnMovimientoCarbon_Activos(Objeto);
                        if(listado_mvtoCarbon_ListadoEquipos != null){//Hay movimientos de equipos pendientes por finalizar el ciclo
                            String listadoEquipoTransito="";
                            int contador=0;
                            for(MvtoCarbon_ListadoEquipos mvtoCarbon_ListadoEquipos: listado_mvtoCarbon_ListadoEquipos){
                                contador++;
                                listadoEquipoTransito += "______________________\nITEM: "+contador+" "+
                                        "CÓDIGO: "+mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getCodigo()+" \n"+
                                        " DESCRIPCIÓN: "+mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getDescripcion() +
                                        " "+mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getMarca()+
                                        " "+mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getModelo()+"\n";

                            }
                            builder.setTitle("Advertencia!!!");
                            builder.setMessage("Para la Placa "+Objeto.getPlaca()+ " se encuentran en tránsito "+ contador+ " equipos, los cuales se describen a continuación:\n"+
                                    listadoEquipoTransito+"\n"+ "¿Está seguro que desea cerrar el ciclo de los equipos en tránsito junto con el ciclo del vehículo de placa "+Objeto.getPlaca()+"?");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cerrarCiclosEquipo(listado_mvtoCarbon_ListadoEquipos,user);
                                    int result = 0;
                                    try {
                                        if(Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()==1) {//Se selecciono SubCentro de Costo Recibo
                                            if (spinnerlavadoVehiculo.getSelectedItemPosition() == 0) {//El usuario seleccionó que si hay lavado de vehículo
                                                if (listadoEquiposLavado != null) {
                                                    String[] data = listadoEquiposLavado.get(spinnerMotivoLavadolistadoVehículo.getSelectedItemPosition()).split(" ");
                                                    Equipo equipo = new Equipo();
                                                    equipo.setCodigo(data[0]);
                                                    Objeto.setEquipoLavadoVehiculo(equipo);
                                                    Objeto.setLavadorVehiculoObservacion(actividad_observacion.getText().toString());

                                                    MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                                    motivoNoLavado.setCodigo("NULL");
                                                    Objeto.setMotivoNoLavado(motivoNoLavado);
                                                    Objeto.setLavadoVehiculo("1");

                                                }

                                            } else {
                                                if (spinnerlavadoVehiculo.getSelectedItemPosition() == 1) {//El usuario seleccionó que no hay lavado de vehhículo
                                                    Objeto.setLavadoVehiculo("0");
                                                    Equipo equipo = new Equipo();
                                                    equipo.setCodigo("NULL");
                                                    Objeto.setEquipoLavadoVehiculo(equipo);
                                                    Objeto.setLavadorVehiculoObservacion(actividad_observacion.getText().toString());
                                                    if (listadoMotivoNoLavadoVehiculo != null) {
                                                        Objeto.setMotivoNoLavado(listadoMotivoNoLavadoVehiculo.get(spinnerMotivoLavadolistadoVehículo.getSelectedItemPosition()));
                                                    } else {
                                                        MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                                        motivoNoLavado.setCodigo("NULL");
                                                        Objeto.setMotivoNoLavado(motivoNoLavado);

                                                    }

                                                }

                                            }
                                        }else{
                                            Objeto.setLavadoVehiculo("NULL");
                                            Equipo equipo = new Equipo();
                                            equipo.setCodigo("NULL");
                                            Objeto.setEquipoLavadoVehiculo(equipo);
                                            MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                            motivoNoLavado.setCodigo("NULL");
                                            Objeto.setMotivoNoLavado(motivoNoLavado);
                                            Objeto.setLavadorVehiculoObservacion("NULL");

                                        }
                                        Objeto.setUsuarioQuienCierra(user);
                                        result = new ControlDB_Carbon(typeConnection).MvtoCarbon_finalizarDescargue(Objeto, user);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    } catch (SocketException e) {
                                        e.printStackTrace();
                                    }
                                    if (result == 1) {
                                        //mensaje("Se registro la finalización del descargue del vehículo de manera exitosa");
                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Registro Exitoso!!");
                                        builder.setMessage("Se finalizó el descargue del vehículo de manera exitosa");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent interfaz = new Intent(moduloCarbon_finalizar_descargueVehiculo.this, Menu.class);
                                                interfaz.putExtra("usuarioB",  user);
                                                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                                                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                                                startActivity(interfaz);
                                            }}
                                        );
                                        builder.show();
                                    } else {
                                        //mensaje("Error al tratar de finalizar el  descargue del vehiculo");
                                        builder = new AlertDialog.Builder(builder.getContext());
                                        builder.setTitle("Error!!");
                                        builder.setMessage("No se pudo finalizar el  descargue del vehículo..");
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                                            }
                                        });
                                        builder.show();
                                    }
                                }}
                            );
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    });
                            builder.show();
                        }else{
                            //Validamos si se seleccionó el Subcentro Recibo
                            if(Objeto.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()==1) {//Se selecciono SubCentro de Costo Recibo
                                if (spinnerlavadoVehiculo.getSelectedItemPosition() == 0) {//El usuario seleccionó que si hay lavado de vehículo
                                    if (listadoEquiposLavado != null) {
                                        String[] data = listadoEquiposLavado.get(spinnerMotivoLavadolistadoVehículo.getSelectedItemPosition()).split(" ");
                                        Equipo equipo = new Equipo();
                                        equipo.setCodigo(data[0]);
                                        Objeto.setEquipoLavadoVehiculo(equipo);
                                        Objeto.setLavadorVehiculoObservacion(actividad_observacion.getText().toString());

                                        MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                        motivoNoLavado.setCodigo("NULL");
                                        Objeto.setMotivoNoLavado(motivoNoLavado);
                                        Objeto.setLavadoVehiculo("1");
                                    }

                                } else {
                                    if (spinnerlavadoVehiculo.getSelectedItemPosition() == 1) {//El usuario seleccionó que no hay lavado de vehhículo
                                        Objeto.setLavadoVehiculo("0");
                                        Equipo equipo = new Equipo();
                                        equipo.setCodigo("NULL");
                                        Objeto.setEquipoLavadoVehiculo(equipo);
                                        Objeto.setLavadorVehiculoObservacion(actividad_observacion.getText().toString());
                                        if (listadoMotivoNoLavadoVehiculo != null) {
                                            Objeto.setMotivoNoLavado(listadoMotivoNoLavadoVehiculo.get(spinnerMotivoLavadolistadoVehículo.getSelectedItemPosition()));
                                        } else {
                                            MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                            motivoNoLavado.setCodigo("NULL");
                                            Objeto.setMotivoNoLavado(motivoNoLavado);

                                        }

                                    }

                                }
                            }else{
                                Objeto.setLavadoVehiculo("NULL");
                                Equipo equipo = new Equipo();
                                equipo.setCodigo("NULL");
                                Objeto.setEquipoLavadoVehiculo(equipo);
                                MotivoNoLavado motivoNoLavado = new MotivoNoLavado();
                                motivoNoLavado.setCodigo("NULL");
                                Objeto.setMotivoNoLavado(motivoNoLavado);
                                Objeto.setLavadorVehiculoObservacion("NULL");

                            }
                            Objeto.setUsuarioQuienCierra(user);
                            int result = new ControlDB_Carbon(typeConnection).MvtoCarbon_finalizarDescargue(Objeto, user);
                            if (result == 1) {
                                //mensaje("Se registro la finalización del descargue del vehículo de manera exitosa");
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Registro Exitoso!!");
                                builder.setMessage("Se finalizó el descargue del vehículo de manera exitosa");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent interfaz = new Intent(moduloCarbon_finalizar_descargueVehiculo.this, Menu.class);
                                        interfaz.putExtra("usuarioB",  user);
                                        interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                                        interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                                        startActivity(interfaz);
                                    }}
                                );
                                builder.show();
                            } else {
                                //mensaje("Error al tratar de finalizar el  descargue del vehiculo");
                                builder = new AlertDialog.Builder(builder.getContext());
                                builder.setTitle("Error!!");
                                builder.setMessage("No se pudo finalizar el  descargue del vehiculo..");
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
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    //mensaje("Error!!. Debe seleccionar una placa de vehiculo para Iniciar el Cargue");
                    builder.setTitle("Error!!");
                    builder.setMessage("Debe seleccionar una placa de vehiculo para Iniciar el Cargue..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
                bntFinalizarDescargue.setEnabled(true);
            }
        });
        Btn_Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interfaz = new Intent(moduloCarbon_finalizar_descargueVehiculo.this, Menu.class);
                interfaz.putExtra("usuarioB",  user);
                interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
                interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
                startActivity(interfaz);
            }
        });
    }
    public void cerrarCiclosEquipo(ArrayList<MvtoCarbon_ListadoEquipos> listado_mvtoCarbon_ListadoEquipos, Usuario us){
        if(listado_mvtoCarbon_ListadoEquipos != null) {
            for (MvtoCarbon_ListadoEquipos mvtoCarbon_ListadoEquipos : listado_mvtoCarbon_ListadoEquipos) {
                mvtoCarbon_ListadoEquipos.getMvtoEquipo().setUsuarioQuienCierra(us);
                MotivoParada motivoParada = new MotivoParada();
                motivoParada.setCodigo("1");
                int result = 0;
                try {
                    result = new ControlDB_Carbon(typeConnection).mvtoCarbon_cerrarCiclo_mtvoEquipo(mvtoCarbon_ListadoEquipos, us, motivoParada);
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
    public void cargueInfo(String opcion) {
        switch (opcion) {
            case "Placa": {
                ArrayList<String> arrayListado = new ArrayList<>();
                System.out.println("Placa reconocida: " + placa.getText().toString());
                ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                listadoMvtoCarbon = controlDB_Carbon.buscarMtvo_CarbonEnTransito(zonaTrabajoSeleccionada);
                arrayListado.add("");
                for (MvtoCarbon listadoObjeto : listadoMvtoCarbon) {
                    arrayListado.add(listadoObjeto.getPlaca());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                spinnerPlaca.setAdapter(adaptadorListado);
                break;
            }
            case "PlacaBuscar": {
                ArrayList<String> arrayListado = new ArrayList<>();
                System.out.println("Placa reconocida: " + placa.getText().toString());
                ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                listadoMvtoCarbon = controlDB_Carbon.buscarMtvo_CarbonEnTransito(placa.getText().toString(),zonaTrabajoSeleccionada);
                arrayListado.add("");
                for (MvtoCarbon listadoObjeto : listadoMvtoCarbon) {
                    arrayListado.add(listadoObjeto.getPlaca());
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListado);
                spinnerPlaca.setAdapter(adaptadorListado);
                break;
            }
            case "PlacaEspecifica": {
                for (MvtoCarbon mvtonCar : listadoMvtoCarbon) {
                    if (!spinnerPlaca.getSelectedItem().toString().isEmpty()) {
                        if (mvtonCar.getPlaca().equals(spinnerPlaca.getSelectedItem().toString())) {
                           // System.out.println(mvtonCar.getArticulo().getDescripcion());
                            placaSeleccionada.setText("" + mvtonCar.getPlaca());
                            articulo.setText("" + mvtonCar.getArticulo().getDescripcion());
                            cliente.setText("C" + mvtonCar.getCliente().getDescripcion());
                            transportadora.setText("" + mvtonCar.getTransportadora().getDescripcion());
                            //orden.setText("ORDEN: " + Objeto.getOrden());
                            deposito.setText("" + mvtonCar.getDeposito());
                            pesoLleno.setText("" + mvtonCar.getPesoVacio());
                            fechaTara.setText("" + mvtonCar.getFechaEntradaVehiculo());
                            fechaInicioDescargue.setText("" + mvtonCar.getFechaInicioDescargue());
                            centroOperacion.setText("" + mvtonCar.getCentroOperacion().getDescripcion());
                            subcentroCosto.setText("" + mvtonCar.getCentroCostoAuxiliar().getCentroCostoSubCentro().getDescripcion());
                            auxCentroCosto.setText("" + mvtonCar.getCentroCostoAuxiliar().getDescripcion());
                            usuarioQuieRegistra.setText("" + mvtonCar.getUsuarioRegistroMovil().getNombres() +" "+ mvtonCar.getUsuarioRegistroMovil().getApellidos());
                            estadoMvtoCarbon.setText("" + mvtonCar.getEstadoMvtoCarbon().getDescripcion());
                            text_BodegaDestino.setText("" + mvtonCar.getCentroCostoAuxiliarDestino().getDescripcion());
                            text_laborRealizada.setText("" + mvtonCar.getLaborRealizada().getDescripcion());
                            Objeto = mvtonCar;
                            cargueInfo("list_equipos");
                            if(mvtonCar.getCentroCostoAuxiliar().getCentroCostoSubCentro().getCodigo()==1){//Se selecciono SubCentro de Costo Recibo
                                textViewLavadoDeVehiculo.setVisibility(View.VISIBLE);
                                spinnerlavadoVehiculo.setVisibility(View.VISIBLE);
                                textViewMensajeLavadoVehiculo.setVisibility(View.VISIBLE);
                                spinnerMotivoLavadolistadoVehículo.setVisibility(View.VISIBLE);
                                TextView_observacion.setVisibility(View.VISIBLE);
                                actividad_observacion.setVisibility(View.VISIBLE);

                                if(spinnerlavadoVehiculo.getSelectedItemPosition()==0){//Seleccionó SI
                                    textViewLavadoDeVehiculo.setVisibility(View.VISIBLE);
                                    spinnerlavadoVehiculo.setVisibility(View.VISIBLE);
                                    textViewMensajeLavadoVehiculo.setVisibility(View.VISIBLE);
                                    textViewMensajeLavadoVehiculo.setText("Seleccione quien realizará el lavado del vehículo:");
                                    spinnerMotivoLavadolistadoVehículo.setVisibility(View.VISIBLE);
                                    cargueInfo("CargarEquipos_LavadoVehiculo");
                                    TextView_observacion.setVisibility(View.VISIBLE);
                                    actividad_observacion.setVisibility(View.VISIBLE);
                                }else{//Seleccionó NO
                                    textViewLavadoDeVehiculo.setVisibility(View.VISIBLE);
                                    spinnerlavadoVehiculo.setVisibility(View.VISIBLE);
                                    textViewMensajeLavadoVehiculo.setVisibility(View.VISIBLE);
                                    textViewMensajeLavadoVehiculo.setText("Seleccione motivo del no lavado del vehículo:");
                                    spinnerMotivoLavadolistadoVehículo.setVisibility(View.VISIBLE);
                                    cargueInfo("CargarMotivosNoLavadoVehiculo");
                                    TextView_observacion.setVisibility(View.VISIBLE);
                                    actividad_observacion.setVisibility(View.VISIBLE);
                                }
                            }else{
                                textViewLavadoDeVehiculo.setVisibility(View.INVISIBLE);
                                spinnerlavadoVehiculo.setVisibility(View.INVISIBLE);
                                textViewMensajeLavadoVehiculo.setVisibility(View.INVISIBLE);
                                spinnerMotivoLavadolistadoVehículo.setVisibility(View.INVISIBLE);
                                TextView_observacion.setVisibility(View.INVISIBLE);
                                actividad_observacion.setVisibility(View.INVISIBLE);
                            }




                        }
                    }
                }
                break;
            }
            case "list_equipos":{
                ArrayList<String> arrayListado =new ArrayList<>();
                try {
                    ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                    arrayListado = controlDB_Carbon.MvtoCarbon_ListadoEquipo(Objeto.getCodigo());
                }catch (SQLException e){
                    //mensaje("No se pudo consultar los Equipos");
                    builder.setTitle("Error!!");
                    builder.setMessage("No se pudo consultar los Equipos, valide datos..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
                ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this,android.R.layout.simple_list_item_1, arrayListado);
                spinnerListadoEquipos.setAdapter(adaptadorListado);
                break;
            }
            case "CargarEquipos_LavadoVehiculo":{
                if(Objeto != null) {
                    //ArrayList<String> arrayListado = new ArrayList<>();
                    try {
                        //ControlDB_Carbon controlDB_Carbon = new ControlDB_Carbon(typeConnection);
                        listadoEquiposLavado = new ControlDB_Carbon(typeConnection).MvtoCarbon_ListadoEquipo(Objeto.getCodigo());
                    } catch (SQLException e) {
                        //mensaje("No se pudo consultar los Equipos");
                        builder.setTitle("Error!!");
                        builder.setMessage("No se pudo consultar los Equipos, valide datos..");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Hacer cosas aqui al hacer clic en el boton de aceptar
                            }
                        });
                        builder.show();
                    }if(listadoEquiposLavado != null) {
                        ArrayAdapter<String> adaptadorListado = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listadoEquiposLavado);
                        spinnerMotivoLavadolistadoVehículo.setAdapter(adaptadorListado);
                    }
                }
                break;
            }
            case "CargarMotivosNoLavadoVehiculo":{
                try {
                    listadoMotivoNoLavadoVehiculo = new ControlDB_Carbon(typeConnection).listarMotivoNoLavado_Activos();
                } catch (SQLException e) {//Ocurrió un error al consultar
                    e.printStackTrace();
                    builder.setTitle("Error!!");
                    builder.setMessage("No se pudo consultar los Equipos, valide datos..");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
                if(listadoMotivoNoLavadoVehiculo != null){//La consulta trae resultados
                    ArrayList<String> arrayListadoMotivos = new ArrayList<>();
                    for (MotivoNoLavado listadoObjeto : listadoMotivoNoLavadoVehiculo) {
                        arrayListadoMotivos.add(listadoObjeto.getDescripcion());
                    }
                    ArrayAdapter<String> adaptadorListadoT = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListadoMotivos);
                    spinnerMotivoLavadolistadoVehículo.setAdapter(adaptadorListadoT);
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
                    Intent interfaz = new Intent(moduloCarbon_finalizar_descargueVehiculo.this, Menu.class);
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

