package com.example.frank.macrogainz;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class getInputStream extends AsyncTask {
    Context context;
    String usuario;
    String res;


    @Override
    protected String doInBackground(Object[] objects) {

        String direccion = "https://134.209.235.115/framos001/WEB/php/getImg.php";
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context, direccion);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //parámetros que le pasamos al php
            String parametros = "param1="+usuario;

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);//necesario por el método POST u PUT
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //incluimos los parámetros en la conexion
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();

                //System.out.println("********************\n"+result+"\n************************");

                res = result;
                res=urlConnection.getInputStream().toString();

                inputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public  getInputStream(Context cont,String usr){
        context=cont;
        usuario=usr;
    }



}
