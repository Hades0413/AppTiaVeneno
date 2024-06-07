package com.example.apptiaveneno.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.apptiaveneno.Entity.Categoria;
import com.example.apptiaveneno.R;

import java.util.ArrayList;

public class AdaptadorCategoria extends BaseAdapter {
    private Context contexto;
    private ArrayList<Categoria> lista;

    public AdaptadorCategoria(Context contexto, ArrayList<Categoria> lista) {
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
        // Retorna la posición como el ID del ítem
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declarar las vistas
        TextView tvCodigo, tvDescripcion;
        View row;

        // Inflar el layout de la fila si aún no se ha hecho
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(contexto);
            row = inflater.inflate(R.layout.activity_data_categoria, parent, false);
        } else {
            row = convertView;
        }

        // Obtener las referencias de las vistas
        tvCodigo = row.findViewById(R.id.tvCodigo);
        tvDescripcion = row.findViewById(R.id.tvDescripcion);

        // Obtener el objeto Categoria de la posición actual
        Categoria categoria = lista.get(position);

        // Establecer los valores en las vistas
        tvCodigo.setText(String.valueOf(categoria.getIdCategoria()));
        tvDescripcion.setText(categoria.getDescripcion());

        return row;
    }
}
