package com.example.vg_appcostos.ModuloCarbon.Modelo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vg_appcostos.R;
import java.util.List;

public class ListAdapter_ListElement_ReporteMvtoCarbon  extends RecyclerView.Adapter<ListAdapter_ListElement_ReporteMvtoCarbon.ViewHolder>{
    private List<ListElement_ReporteMvtoCarbon> mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapter_ListElement_ReporteMvtoCarbon(List<ListElement_ReporteMvtoCarbon> itemsList, Context context){
        this.mInflater=LayoutInflater.from(context);
        this.context = context;
        this.mData=itemsList;
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    @Override
    public ListAdapter_ListElement_ReporteMvtoCarbon.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_element_report_mvtocarbon, null);
        return new ListAdapter_ListElement_ReporteMvtoCarbon.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ListAdapter_ListElement_ReporteMvtoCarbon.ViewHolder holder, final int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElement_ReporteMvtoCarbon> items){
        mData=items;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView placaSeleccionada,cliente,articulo,deposito,subcentroCosto,auxCentroCostoOrigen,auxCentroCostoDestino,laborRealizada,lavadoVehículo,recaudoEmpresa
                ,fechaInicioDescargue,fechaFinDescargue,contador;
        LinearLayout lineaEstadoVehiculo;
        EditText equiposEncargadosOperacion,EquipoQuienLavaVehiculo;
        TextView estad;

        ViewHolder(View itemView){
            super(itemView);
            lineaEstadoVehiculo= itemView.findViewById(R.id.lineaEstadoVehiculo);
            iconImage= itemView.findViewById(R.id.iconImagenView);
            placaSeleccionada= itemView.findViewById(R.id.placaSeleccionada);
            contador= itemView.findViewById(R.id.contador);
            cliente= itemView.findViewById(R.id.cliente);
            articulo= itemView.findViewById(R.id.articulo);
            deposito= itemView.findViewById(R.id.deposito);
            subcentroCosto= itemView.findViewById(R.id.subcentroCosto);
            auxCentroCostoOrigen= itemView.findViewById(R.id.auxCentroCostoOrigen);
            auxCentroCostoDestino= itemView.findViewById(R.id.auxCentroCostoDestino);
            laborRealizada= itemView.findViewById(R.id.laborRealizada);
            lavadoVehículo= itemView.findViewById(R.id.lavadoVehículo);
            recaudoEmpresa= itemView.findViewById(R.id.recaudoEmpresa);
            EquipoQuienLavaVehiculo= itemView.findViewById(R.id.EquipoQuienLavaVehiculo);
            fechaInicioDescargue= itemView.findViewById(R.id.fechaInicioDescargue);
            fechaFinDescargue= itemView.findViewById(R.id.fechaFinDescargue);
            equiposEncargadosOperacion= itemView.findViewById(R.id.equiposEncargadosOperacion);
            estad= itemView.findViewById(R.id.estad);

        }
        void bindData(final ListElement_ReporteMvtoCarbon item){
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);

            contador.setText(item.getContador());
            placaSeleccionada.setText(item.getPlaca());
            cliente.setText(item.getCliente().getDescripcion());
            articulo.setText(item.getArticulo().getDescripcion());
            deposito.setText(item.getDeposito());
            subcentroCosto.setText(item.getCentroCostoAuxiliarOrigen().getCentroCostoSubCentro().getDescripcion());
            auxCentroCostoOrigen.setText(item.getCentroCostoAuxiliarOrigen().getDescripcion());
            auxCentroCostoDestino.setText(item.getCentroCostoAuxiliarDestino().getDescripcion());
            laborRealizada.setText(item.getLaborRealizada().getDescripcion());
            lavadoVehículo.setText(item.getLavadoVehiculo());
            recaudoEmpresa.setText(item.getRecaudoEmpresa());
            if(item.getEquipoLavadoVehiculo().getCodigo() !=null){
                EquipoQuienLavaVehiculo.setText(item.getEquipoLavadoVehiculo().getDescripcion()+ item.getEquipoLavadoVehiculo().getModelo());
            }else{
                EquipoQuienLavaVehiculo.setText("");
            }

            fechaInicioDescargue.setText(item.getFechaInicioDescargue());
            fechaFinDescargue.setText(item.getFechaFinDescargue());
            equiposEncargadosOperacion.setText(item.getEquipoEncargadosDescargue());
            estad.setText(item.getEstado());
            if(item.getEstado().equals("PENDIENTE CIERRE")) {
                lineaEstadoVehiculo.setBackgroundColor(Color.parseColor("#F94848"));//rojo
            }else{
                lineaEstadoVehiculo.setBackgroundColor(Color.parseColor("#6DBD5F"));//Verde
            }
        }
    }


}