package com.example.frank.macrogainz;

public class Datos {

    private int posicion;


    private static Datos data;

    public static Datos getInstance() {
        if (data==null) {

            data = new Datos(0);
        }
        return data;
    }

    private Datos(int pos) {
        posicion=pos;
    }

    public void setPosicion(int p){

        posicion=p;
    }

    public int getPosicion(){

        return posicion;
    }


}
