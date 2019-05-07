package com.example.frank.macrogainz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class ProfilePicture extends AsyncTask {
    Context context;
    String usuario;
    String res;

    /**Este método nos devuelve un Bitmap con la imagen del usuario en cuestión
     * **/
    @Override
    protected Bitmap doInBackground(Object[] objects) {

        String direccion = "https://134.209.235.115/framos001/WEB/php/getImg.php";
        HttpsURLConnection urlConnection = null;
        Bitmap resultado = null;
        try {
            //nos conectamos de forma segura mediante HTTPS
            urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context, direccion);
            //urlConnection.setConnectTimeout(5000);
            //urlConnection.setReadTimeout(5000);

            //parámetros que le pasamos al php
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("param1", usuario);

            String parametrosURL = builder.build().getEncodedQuery();

            String parametros = "param1="+usuario;

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);//necesario por el método POST u PUT
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //incluimos los parámetros en la conexion
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosURL);
            out.close();


            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200){
                //obtenemos la imagen de la BD del servidor
                resultado = BitmapFactory.decodeStream(urlConnection.getInputStream());
            }else{
                System.out.println("No se ha cargado la imagen");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public ProfilePicture(Context cont, String usr){
        context=cont;
        usuario=usr;
    }



}
