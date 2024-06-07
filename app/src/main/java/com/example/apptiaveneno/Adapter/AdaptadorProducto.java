package com.example.apptiaveneno.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apptiaveneno.R;
import com.example.apptiaveneno.Entity.Producto;
import java.util.ArrayList;

public class AdaptadorProducto extends BaseAdapter {
    private Context contexto;
    private ArrayList<Producto> lista;

    public AdaptadorProducto(Context contexto, ArrayList<Producto> lista) {
        this.contexto = contexto;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView tvCodigo,tvDescripcion;

        View row;

        LayoutInflater inflater= (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        row=inflater.inflate(R.layout.activity_data_producto,parent,false);

        tvCodigo=(TextView)row.findViewById(R.id.tvCodigo);
        tvDescripcion=(TextView)row.findViewById(R.id.tvDescripcion);

        tvCodigo.setText(""+lista.get(position).getCodigo());
        tvDescripcion.setText(lista.get(position).getDescripcion());
        return row;
    }
}
