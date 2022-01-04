package com.example.vg_appcostos.ModuloCarbon.Modelo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vg_appcostos.R;

import java.util.List;

public class ListAdapter_ListElement_ReporteMvtoEquipo extends RecyclerView.Adapter<ListAdapter_ListElement_ReporteMvtoEquipo.ViewHolder>{
    private List<ListElement_ReporteMvtoEquipo> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapter_ListElement_ReporteMvtoEquipo(List<ListElement_ReporteMvtoEquipo> itemsList, Context context){
        this.mInflater=LayoutInflater.from(context);
        this.context = context;
        this.mData=itemsList;
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    @Override
    public ListAdapter_ListElement_ReporteMvtoEquipo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_element_report_mvtoequipo, null);
        return new ListAdapter_ListElement_ReporteMvtoEquipo.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ListAdapter_ListElement_ReporteMvtoEquipo.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElement_ReporteMvtoEquipo> items){
        mData=items;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView equipo,tipoEquipo,numeroOrden,subcentroCosto,auxCentroCostoOrigen,auxCentroCostoDestino,laborRealizada,cliente,articulo,motonave,fechaInicio,fechaFin,parada,motivoParada,estad,contador;
        LinearLayout lineaEstado;
        ViewHolder(View itemView){
            super(itemView);
            contador= itemView.findViewById(R.id.contador);
            iconImage= itemView.findViewById(R.id.iconImagenView);
            equipo= itemView.findViewById(R.id.equipo);
            tipoEquipo= itemView.findViewById(R.id.tipoEquipo);
            numeroOrden= itemView.findViewById(R.id.numeroOrden);
            subcentroCosto= itemView.findViewById(R.id.subcentroCosto);
            auxCentroCostoOrigen= itemView.findViewById(R.id.auxCentroCostoOrigen);
            auxCentroCostoDestino= itemView.findViewById(R.id.auxCentroCostoDestino);
            laborRealizada= itemView.findViewById(R.id.laborRealizada);
            cliente= itemView.findViewById(R.id.cliente);
            articulo= itemView.findViewById(R.id.articulo);
            motonave= itemView.findViewById(R.id.motonave);
            fechaInicio= itemView.findViewById(R.id.fechaInicio);
            fechaFin= itemView.findViewById(R.id.fechaFin);
            parada= itemView.findViewById(R.id.parada);
            motivoParada= itemView.findViewById(R.id.motivoParada);
            estad= itemView.findViewById(R.id.estad);

            lineaEstado= itemView.findViewById(R.id.lineaEstado);


        }
        void bindData(final ListElement_ReporteMvtoEquipo item){
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            if(item.getEquipo().getTipoEquipo().getCodigo().equals("4")){//101-CARGADORES
                iconImage.setImageResource(R.drawable.report_mvtoequipo_cargadores);
            }else{
                if(item.getEquipo().getTipoEquipo().getCodigo().equals("6")){//103-EXCAVADORAS
                    iconImage.setImageResource(R.drawable.report_mvtoequipo_excavadoras);
                }else{
                    if(item.getEquipo().getTipoEquipo().getCodigo().equals("0")){//PALERO
                        iconImage.setImageResource(R.drawable.report_mvtoequipo_paleros);
                    }else{
                        if(item.getEquipo().getTipoEquipo().getCodigo().equals("7")){//103-105-MINI CARGADORES
                            iconImage.setImageResource(R.drawable.report_mvtoequipo_minicargador2);
                        }
                        else{
                            iconImage.setImageResource(R.drawable.report_mvtoequipo_no_imagen);
                        }
                    }
                }
            }
            contador.setText(item.getContador());
            equipo.setText(item.getEquipo().getDescripcion()+" "+item.getEquipo().getModelo());
            tipoEquipo.setText((item.getEquipo().getTipoEquipo().getDescripcion()));
            numeroOrden.setText(item.getNumeroOrden());
            auxCentroCostoOrigen.setText(item.getCentroCostoAuxiliarOrigen().getDescripcion());
            subcentroCosto.setText(item.getCentroCostoAuxiliarOrigen().getCentroCostoSubCentro().getDescripcion());
            auxCentroCostoDestino.setText(item.getCentroCostoAuxiliarDestino().getDescripcion());
            laborRealizada.setText(item.getLaborRealizada().getDescripcion());
            cliente.setText(item.getCliente().getDescripcion());
            articulo.setText(item.getArticulo().getDescripcion());
            motonave.setText(item.getMotonave().getDescripcion());
            fechaInicio.setText(item.getFechaInicio());
            fechaFin.setText(item.getFechaFin());
            parada.setText(item.getParada());
            motivoParada.setText(item.getMotivoParada().getDescripcion());
            estad.setText(item.getEstado());
            if(item.getEstado().equals("PENDIENTE CIERRE")) {
                lineaEstado.setBackgroundColor(Color.parseColor("#F94848"));//rojo
            }else{
                lineaEstado.setBackgroundColor(Color.parseColor("#6DBD5F"));//Verde
            }
        }
    }


}