package com.example.frank.macrogainz;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ElViewHolder extends RecyclerView.ViewHolder {

    public ConstraintLayout milayout;
    public TextView eltexto;
    public ImageView imagen;
    public CardView elCardView;
    public boolean[] seleccion;

    public ElViewHolder (final View v){
        super(v);

        milayout=v.findViewById(R.id.constCardView);
        eltexto=v.findViewById(R.id.ejercicio);
        imagen=v.findViewById(R.id.imageEjercicio);
        elCardView=v.findViewById(R.id.cardView);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (seleccion[getAdapterPosition()]==true){
                    /**QUE HACER AL MARCAR O DESMARCAR UN ELEMENTO**/

                    seleccion[getAdapterPosition()]=false;
                    elCardView.setBackgroundColor(Color.WHITE);
                }
                else{
                    seleccion[getAdapterPosition()]=true;
                    elCardView.setBackgroundColor(Color.parseColor("#fff200"));
                }
            }
        });
    }
}
