package com.example.frank.macrogainz;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ElAdaptadorRecycler extends RecyclerView.Adapter <ElViewHolder>{

    private String[] losnombres;
    private boolean[] seleccionados;
    private ArrayList<Integer> imagenes;
    private int position;

    public ElAdaptadorRecycler (String[] nombres, ArrayList<Integer> imageness){

        losnombres=nombres;
        seleccionados=new boolean[nombres.length];
        imagenes = imageness;
        position = 0;
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View ellayoutdelafila= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mi_card_view,null);
        ElViewHolder evh = new ElViewHolder(ellayoutdelafila);
        evh.seleccion=seleccionados;
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder elViewHolder, final int i) {
        elViewHolder.eltexto.setText(losnombres[i]);
        elViewHolder.imagen.setImageResource(imagenes.get(i));
        elViewHolder.milayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = i;
                notifyDataSetChanged();

                Datos.getInstance().setPosicion(position);
            }
        });
        if(position==i){
            elViewHolder.milayout.setBackgroundColor(Color.parseColor("#fff200"));
        }
        else
        {
            elViewHolder.milayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }


    }

    @Override
    public int getItemCount() {
        return losnombres.length;
    }

    public int getPosition(){return position;}
}
