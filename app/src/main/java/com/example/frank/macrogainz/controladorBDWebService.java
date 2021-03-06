package com.example.frank.macrogainz;

import android.content.Context;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class controladorBDWebService {
    private static controladorBDWebService instancia;

    private controladorBDWebService(){
    }

    public static controladorBDWebService getInstance(){
        if (instancia==null){
            instancia = new controladorBDWebService();
        }
        return instancia;
    }
    //devuelve true si los datos del usuario son correctos
    public boolean login(Context cont, String oper, String usr, String contra) throws IOException, ParseException, ExecutionException, InterruptedException {
        boolean sePuede=false;
        JSONObject json = new conexionBDWebService(cont, oper, usr, contra).execute().get();

        String user = (String) json.get("usuario");
        if(user!=null){
            sePuede=true;
        }
        return sePuede;
    }
    //devuelve true si existen datos adicionales del usuario
    public boolean existenDatos(Context cont, String oper, String usr) throws ExecutionException, InterruptedException {
        boolean existen=true;
        JSONObject json = new conexionBDWebService(cont, oper, usr).execute().get();

        String user = (String) json.get("usuario");

        if(user!=null){
        existen=false;
        }
        return existen;
    }
    //devuelve un json con los datos del usuario
    public JSONObject getDatos(Context cont, String oper, String usr) throws ExecutionException, InterruptedException {
        boolean existen=true;
        JSONObject json = new conexionBDWebService(cont, oper, usr).execute().get();

        return json;
    }
    //devuelve true si se ha realizado correctamente la petición
    public boolean updateUsuarioEjercicio(Context cont,String oper, String usr,String ejer) throws ExecutionException, InterruptedException {
        boolean correcto=false;
        JSONObject json = new conexionBDWebService(cont, oper, usr,ejer).execute().get();

        String res = (String) json.get("respuesta");

        if(res.equals("correcto")){
            correcto=true;
        }
        return correcto;
    }

    //devuelve true si se ha realizado correctamente la petición
    public boolean updateUsuarioDetalles(Context applicationContext, String oper, String usr, int peso, int alt, String fecha, String genero) throws ExecutionException, InterruptedException {
        boolean correcto=false;
        JSONObject json = new conexionBDWebService(applicationContext, oper, usr,peso, alt, fecha, genero).execute().get();

        String res = (String) json.get("respuesta");

        if(res.equals("correcto")){
            correcto=true;
        }
        return correcto;

    }
    //devuelve true si se ha realizado correctamente la petición
    public boolean insertarPesoInicial(Context applicationContext, String oper, String usr, int peso) throws ExecutionException, InterruptedException {
        boolean correcto=false;
        JSONObject json = new conexionBDWebService(applicationContext, oper, usr,peso).execute().get();

        String res = (String) json.get("respuesta");

        if(res.equals("correcto")){
            correcto=true;
        }
        return correcto;

    }
    //inserta un nuevo peso y actualiza el peso del usuario
    public void insertarPeso(Context applicationContext, String insertarPeso, String nombreUsuario, int peso) {
        new conexionBDWebService(applicationContext,insertarPeso,nombreUsuario, peso).execute();

    }

    //devuelve un array con todos los pesos del usuario
    public int[] getPesos(Context applicationContext, String getPesos, String nombreUsuario) throws ExecutionException, InterruptedException {
        JSONObject json = new conexionBDWebService(applicationContext,getPesos,nombreUsuario).execute().get();
        JSONArray arr = (JSONArray) json.get("Pesos");
        int total = arr.size();
        int[] pesos = new int[total];

        Iterator i = arr.iterator();
        int cont =0;
        while (i.hasNext()) {
            JSONObject varPeso = (JSONObject) i.next();
            int peso = Integer.parseInt((String)varPeso.get("peso"));
            pesos[cont]=peso;
            cont++;
        }

        return pesos;
    }

    //inserta un nuevo usuario con los datos proporcionados
    public boolean insertarUsuario(Context applicationContext, String insertarUsuario, String usuario, String nombre, String apellidos, String email, String contra) throws ExecutionException, InterruptedException {
        boolean correcto=false;
        JSONObject json = new conexionBDWebService(applicationContext, insertarUsuario,usuario,nombre,apellidos,email,contra).execute().get();

        String res = (String) json.get("respuesta");

        if(res.equals("correcto")){
            correcto=true;
        }
        return correcto;
    }

    //inserta un nuevo token del usuario
    public void insertarToken(Context context,String oper,String user,String token) {
        new conexionBDWebService(context,oper,user,token).execute();
    }
    //genera una notificación FCM desde php del servidor
    public void mensajesFCMweb(Context applicationContext, String mensajesFCMweb, String nombreUsuario) {
        new conexionBDWebService(applicationContext,mensajesFCMweb,nombreUsuario).execute();
    }
    //guarda la imagen encoded en string64
    public void saveImg(Context applicationContext, String saveImg, String nombreUsuario, String fotoen64, String titulo) throws ExecutionException, InterruptedException {
        Toast.makeText(applicationContext, "*******saveIMGCONNNNN: \n" + fotoen64 + "\n**********", Toast.LENGTH_SHORT).show();
        JSONObject json = new conexionBDWebService(applicationContext, saveImg,nombreUsuario,fotoen64,titulo).execute().get();
        String res = (String) json.get("respuesta");
        Toast.makeText(applicationContext, "*******RESPUESTAS: \n" + res + "\n**********", Toast.LENGTH_SHORT).show();
    }
}
