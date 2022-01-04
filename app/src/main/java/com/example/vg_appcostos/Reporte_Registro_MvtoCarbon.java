package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ListAdapter_ListElement_ReporteMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ListElement_ReporteMvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.MvtoCarbon_ListadoEquipos;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Reporte_Registro_MvtoCarbon extends AppCompatActivity {
    Usuario user = null;
    ZonaTrabajo zonaTrabajoSeleccionada=null;
    String typeConnection="";
    List<ListElement_ReporteMvtoCarbon> elements;
    ImageView fechaActual,fechaRango,atras;
    private String fechaHoraInicio_consulta;
    private String fechaHoraFinalización_consulta;
    TextView labelFechaHoraSeleccionado;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte__registro__mvto_carbon);
        //initReporteCarbon();
        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection=  getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        fechaActual = (ImageView) findViewById(R.id.fechaActual);
        fechaRango = (ImageView) findViewById(R.id.fechaRango);
        atras = (ImageView) findViewById(R.id.atras);
        labelFechaHoraSeleccionado = (TextView) findViewById(R.id.labelFechaHoraSeleccionado);

        fechaHoraInicio_consulta="";
        fechaHoraFinalización_consulta="";
        zonaTrabajoSeleccionada= (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        //fechaInicio.setOnClickListener(this);
        /*fechaInicio.setOnClickListener(new View.OnClickListener() {

        });*/
    }
   // @Override
    public void onClick(View v) {
        fechaHoraInicio_consulta="";
        fechaHoraFinalización_consulta="";
        labelFechaHoraSeleccionado.setText("");
        if(v==fechaActual){
            initReporteCarbon();
        }

        if(v==fechaRango) {
            /*final Calendar d = Calendar.getInstance();
            int hora = d.get(Calendar.HOUR_OF_DAY);
            int minutos = d.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    fechaHoraInicio_consulta=fechaHoraInicio_consulta+" "+hourOfDay+":"+minute;
                    //labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta);
                    labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta+"---"+fechaHoraFinalización_consulta);
                    //textView8.setText(textView8.getText().toString()+" "+hourOfDay+":"+minute);
                }
            },hora,minutos,true);
            timePickerDialog.show();*/
            final Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH);
            int ano = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialogFinal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear , int dayOfMonth) {
                    fechaHoraFinalización_consulta=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+" 23:59:59.999";
                    labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta +" Al "+fechaHoraFinalización_consulta);
                    initReporteCarbon();
                }
            },ano, mes, dia);
            datePickerDialogFinal.show();
            DatePickerDialog datePickerDialogInicial = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear , int dayOfMonth) {
                    fechaHoraInicio_consulta=year+"-"+(monthOfYear+1)+"-"+dayOfMonth+" 00:00:00.0";
                    labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta +" Al "+fechaHoraFinalización_consulta);
                }
            },ano, mes, dia);
            datePickerDialogInicial.show();


        }
        if(v==atras){
            Intent interfaz = new Intent(Reporte_Registro_MvtoCarbon.this, Menu.class);
            interfaz.putExtra("usuarioB",  user);
            interfaz.putExtra("TipoConexion",  getIntent().getExtras().getString("TipoConexion"));
            interfaz.putExtra("zonaTrabajoSeleccionada",  zonaTrabajoSeleccionada);
            startActivity(interfaz);
        }
    }
    public void initReporteCarbon(){
        //if((!fechaHoraInicio_consulta.equals(""))&& (!fechaHoraFinalización_consulta.equals(""))) {
        elements = new ArrayList<>();
        ArrayList<MvtoCarbon> listadoMvtoCarbon=null;
        if(fechaHoraInicio_consulta.equals("") && fechaHoraFinalización_consulta.equals("")){//Selecciono el icono de mostrar los registro del día de Hoy
            listadoMvtoCarbon = new ControlDB_Carbon(typeConnection).reporteCarbon_VehiculosTrabajado(user, "", "");
        }else {
            if (!(fechaHoraInicio_consulta.equals("")) && !(fechaHoraFinalización_consulta.equals(""))) {//ambas fecha son validas para consultar en la base de datos
                try {
                    int valor = Integer.parseInt(new ControlDB_Carbon(typeConnection).compardorEntreDosFechas(fechaHoraInicio_consulta, fechaHoraFinalización_consulta));
                    if (valor < 0) {
                        builder = new AlertDialog.Builder(this);
                        builder = new AlertDialog.Builder(builder.getContext());
                        builder.setTitle("Error!!");
                        builder.setMessage("La fecha de Inicio no puede ser mayor a la fecha de finalizaicón, valide datos..");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();

                    } else {
                        listadoMvtoCarbon = new ControlDB_Carbon(typeConnection).reporteCarbon_VehiculosTrabajado(user, fechaHoraInicio_consulta, fechaHoraFinalización_consulta);
                        /*if(valor ==0){//Ambas fechas son iguales
                            fechaHoraInicio_consulta= fechaHoraInicio_consulta+"";
                            fechaHoraFinalización_consulta= fechaHoraFinalización_consulta+"";
                            JOptionPane.showMessageDialog(null, "Error!!.. La fecha de Inicio no puede ser Igual a la fecha de Finalización","Advertencia", JOptionPane.ERROR_MESSAGE );
                        }*/
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                builder = new AlertDialog.Builder(this);
                builder = new AlertDialog.Builder(builder.getContext());
                builder.setTitle("Advertencia!!");
                builder.setMessage("Debe seleccionar una fecha inicial y una fecha final, valide datos..");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        }
            if(listadoMvtoCarbon != null){
                int incremento=listadoMvtoCarbon.size();
                for(MvtoCarbon mvtoCarbon: listadoMvtoCarbon){
                    String listadoEquipos="";
                    int contador=0;
                    if(mvtoCarbon.getListadoMvtoCarbon_ListadoEquipos() != null) {//Hay movimientos de equipos pendientes por finalizar el ciclo
                        for (MvtoCarbon_ListadoEquipos mvtoCarbon_ListadoEquipos : mvtoCarbon.getListadoMvtoCarbon_ListadoEquipos()) {
                            contador++;
                            listadoEquipos += "#"+contador+" "+mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getCodigo() +mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getDescripcion() +
                                    " " + mvtoCarbon_ListadoEquipos.getAsignacionEquipo().getEquipo().getModelo() + "\n";
                        }
                    }
                    ListElement_ReporteMvtoCarbon listElement_reporteMvtoCarbon = new ListElement_ReporteMvtoCarbon();
                    listElement_reporteMvtoCarbon.setContador("VEHÍCULO # "+incremento);
                    listElement_reporteMvtoCarbon.setPlaca(mvtoCarbon.getPlaca());
                    listElement_reporteMvtoCarbon.setCliente(mvtoCarbon.getCliente());
                    listElement_reporteMvtoCarbon.setArticulo(mvtoCarbon.getArticulo());
                    listElement_reporteMvtoCarbon.setDeposito(mvtoCarbon.getDeposito());
                    listElement_reporteMvtoCarbon.setCentroCostoSubCentro(mvtoCarbon.getCentroCostoAuxiliar().getCentroCostoSubCentro());
                    listElement_reporteMvtoCarbon.setCentroCostoAuxiliarOriente(mvtoCarbon.getCentroCostoAuxiliar());
                    listElement_reporteMvtoCarbon.setCentroCostoAuxiliarDestino(mvtoCarbon.getCentroCostoAuxiliarDestino());
                    listElement_reporteMvtoCarbon.setLaborRealizada(mvtoCarbon.getLaborRealizada());
                    listElement_reporteMvtoCarbon.setRecaudoEmpresa(mvtoCarbon.getValorRecaudoEmpresa());
                    listElement_reporteMvtoCarbon.setLavadoVehiculo(mvtoCarbon.getLavadoVehiculo());
                    listElement_reporteMvtoCarbon.setEquipoLavadoVehiculo(mvtoCarbon.getEquipoLavadoVehiculo());
                    listElement_reporteMvtoCarbon.setFechaInicioDescargue(mvtoCarbon.getFechaInicioDescargue());
                    listElement_reporteMvtoCarbon.setFechaFinDescargue(mvtoCarbon.getFechaFinDescargue());
                    listElement_reporteMvtoCarbon.setEquipoEncargadosDescargue(listadoEquipos);
                    if(mvtoCarbon.getFechaFinDescargue() == null) {
                        listElement_reporteMvtoCarbon.setEstado("PENDIENTE CIERRE");
                    }else{
                        listElement_reporteMvtoCarbon.setEstado("FINALIZADO");
                    }
                    elements.add(listElement_reporteMvtoCarbon);
                    incremento--;
                }
                ListAdapter_ListElement_ReporteMvtoCarbon listAdapter = new ListAdapter_ListElement_ReporteMvtoCarbon(elements, this);
                RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(listAdapter);
            }
        //}
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            builder = new AlertDialog.Builder(this);
            builder = new AlertDialog.Builder(builder.getContext());
            builder.setTitle("Alerta!!");
            builder.setMessage("Está seguro que desea salir?");
            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent interfaz = new Intent(Reporte_Registro_MvtoCarbon.this, Menu.class);
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
