package com.example.vg_appcostos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.vg_appcostos.ModuloCarbon.Controlador.ControlDB_Carbon;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ListAdapter_ListElement_ReporteMvtoEquipo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ListElement_ReporteMvtoEquipo;
import com.example.vg_appcostos.ModuloCarbon.Modelo.ZonaTrabajo;
import com.example.vg_appcostos.ModuloEquipo.Controller.ControlDB_Equipo;
import com.example.vg_appcostos.ModuloEquipo.Model.MvtoEquipo;
import com.example.vg_appcostos.Sistemas.Modelo.Usuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Reporte_Registro_MvtoEquipo extends AppCompatActivity {
    Usuario user = null;
    ZonaTrabajo zonaTrabajoSeleccionada = null;
    String typeConnection = "";
    List<ListElement_ReporteMvtoEquipo> elements;
    ImageView fechaActual, fechaRango, atras;
    private String fechaHoraInicio_consulta;
    private String fechaHoraFinalización_consulta;
    TextView labelFechaHoraSeleccionado;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte__registro__mvto_equipo);
        //initReporteCarbon();
        user = (Usuario) getIntent().getExtras().getSerializable("usuarioB");
        typeConnection = getIntent().getExtras().getString("TipoConexion");
        zonaTrabajoSeleccionada = (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
        fechaActual = (ImageView) findViewById(R.id.fechaActual);
        fechaRango = (ImageView) findViewById(R.id.fechaRango);
        atras = (ImageView) findViewById(R.id.atras);
        labelFechaHoraSeleccionado = (TextView) findViewById(R.id.labelFechaHoraSeleccionado);
        fechaHoraInicio_consulta = "";
        fechaHoraFinalización_consulta = "";
        zonaTrabajoSeleccionada = (ZonaTrabajo) getIntent().getExtras().getSerializable("zonaTrabajoSeleccionada");
    }
    // @Override
    public void onClick(View v) {
        fechaHoraInicio_consulta = "";
        fechaHoraFinalización_consulta = "";
        labelFechaHoraSeleccionado.setText("");
        if (v == fechaActual) {
            initReporteCarbon();
        }
        if (v == fechaRango) {
            final Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH);
            int ano = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialogFinal = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    fechaHoraFinalización_consulta = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " 23:59:59.999";
                    labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta + " Al " + fechaHoraFinalización_consulta);
                    initReporteCarbon();
                }
            }, ano, mes, dia);
            datePickerDialogFinal.show();
            DatePickerDialog datePickerDialogInicial = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    fechaHoraInicio_consulta = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " 00:00:00.0";
                    labelFechaHoraSeleccionado.setText(fechaHoraInicio_consulta + " Al " + fechaHoraFinalización_consulta);
                }
            }, ano, mes, dia);
            datePickerDialogInicial.show();
        }
        if (v == atras) {
            Intent interfaz = new Intent(Reporte_Registro_MvtoEquipo.this, Menu.class);
            interfaz.putExtra("usuarioB", user);
            interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
            interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
            startActivity(interfaz);
        }
    }
    public void initReporteCarbon() {
        elements = new ArrayList<>();
        ArrayList<MvtoEquipo> listadoMvtoEquipo = null;
        if (fechaHoraInicio_consulta.equals("") && fechaHoraFinalización_consulta.equals("")) {//Selecciono el icono de mostrar los registro del día de Hoy
            listadoMvtoEquipo = new ControlDB_Equipo(typeConnection).reporteMvtoEquipo_EquiposTrabajado(user, "", "");
        } else {
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
                        listadoMvtoEquipo = new ControlDB_Equipo(typeConnection).reporteMvtoEquipo_EquiposTrabajado(user, fechaHoraInicio_consulta, fechaHoraFinalización_consulta);
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
        if (listadoMvtoEquipo != null) {
            int incremento = listadoMvtoEquipo.size();
            for (MvtoEquipo mvtoEquipo : listadoMvtoEquipo) {
                ListElement_ReporteMvtoEquipo listElement_ReporteMvtoEquipo = new ListElement_ReporteMvtoEquipo();
                listElement_ReporteMvtoEquipo.setContador("EQUIPO # " + incremento);
                listElement_ReporteMvtoEquipo.setEquipo(mvtoEquipo.getAsignacionEquipo().getEquipo());
                listElement_ReporteMvtoEquipo.setNumeroOrden(mvtoEquipo.getNumeroOrden());
                listElement_ReporteMvtoEquipo.setCentroCostoAuxiliarOrigen(mvtoEquipo.getCentroCostoAuxiliar());
                listElement_ReporteMvtoEquipo.setCentroCostoSubCentro(mvtoEquipo.getCentroCostoAuxiliar().getCentroCostoSubCentro());
                listElement_ReporteMvtoEquipo.setCentroCostoAuxiliarDestino(mvtoEquipo.getCentroCostoAuxiliarDestino());
                listElement_ReporteMvtoEquipo.setLaborRealizada(mvtoEquipo.getLaborRealizada());
                listElement_ReporteMvtoEquipo.setCliente(mvtoEquipo.getCliente());
                listElement_ReporteMvtoEquipo.setArticulo(mvtoEquipo.getArticulo());
                listElement_ReporteMvtoEquipo.setMotonave(mvtoEquipo.getMotonave());
                listElement_ReporteMvtoEquipo.setFechaInicio(mvtoEquipo.getFechaHoraInicio());
                listElement_ReporteMvtoEquipo.setFechaFin(mvtoEquipo.getFechaHoraFin());
                listElement_ReporteMvtoEquipo.setParada(mvtoEquipo.getMotivoParadaEstado());
                listElement_ReporteMvtoEquipo.setMotivoParada(mvtoEquipo.getMotivoParada());
                if(mvtoEquipo.getFechaHoraFin() == null) {
                    listElement_ReporteMvtoEquipo.setEstado("PENDIENTE CIERRE");
                }else{
                    listElement_ReporteMvtoEquipo.setEstado("FINALIZADO");
                }
                elements.add(listElement_ReporteMvtoEquipo);
                incremento--;
            }
            ListAdapter_ListElement_ReporteMvtoEquipo listAdapter = new ListAdapter_ListElement_ReporteMvtoEquipo(elements, this);
            RecyclerView recyclerView = findViewById(R.id.listRecyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(listAdapter);
        }
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
                    Intent interfaz = new Intent(Reporte_Registro_MvtoEquipo.this, Menu.class);
                    interfaz.putExtra("usuarioB", user);
                    interfaz.putExtra("TipoConexion", getIntent().getExtras().getString("TipoConexion"));
                    interfaz.putExtra("zonaTrabajoSeleccionada", zonaTrabajoSeleccionada);
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