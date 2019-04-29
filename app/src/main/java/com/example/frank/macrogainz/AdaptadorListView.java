package com.example.frank.macrogainz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorListView extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private String[] datos;
    private String[] values;

    public AdaptadorListView(Context pcontext, String[] pdatos, String[] pvalues)
    {
        contexto = pcontext;
        datos = pdatos;
        values=pvalues;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datos.length;
    }

    @Override
    public Object getItem(int i) {
        return values[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.elemento_list_view,null);
        TextView nombre= (TextView) view.findViewById(R.id.titulo);
        TextView dato=(TextView) view.findViewById(R.id.contenido);
        nombre.setText(datos[i]);
        dato.setText(values[i]);
        return view;
    }
}
