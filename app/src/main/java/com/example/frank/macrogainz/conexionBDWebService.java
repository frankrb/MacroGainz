package com.example.frank.macrogainz;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;

public class conexionBDWebService extends AsyncTask<Void, Void, JSONObject> {
    String direccion = "https://134.209.235.115/framos001/WEB/php/usuarios.php";
    String usuario="";
    String nombre="";
    String apellidos="";
    String email="";
    String contrasena="";
    int peso;
    int altura;
    String fechaNac="";
    String sexo="";
    String ejercicio="";
    Context context;
    String operacion="";
    String token="";
    String foto="";
    String titulo="";

    public conexionBDWebService(Context cont,String tok) {
        context=cont;
        operacion="saveToken";
        token=tok;
    }

    public conexionBDWebService(Context applicationContext, String saveImg, String nombreUsuario, String fotoen64, String tit) {
        context=applicationContext;
        operacion=saveImg;
        usuario=nombreUsuario;
        foto = fotoen64;
        titulo=tit;
    }

    /**Metodo que devuelve un json con los resultados de las distintas peticiones
     * **/
    @Override
    protected JSONObject doInBackground(Void... voids) {

        JSONObject json= null;

        HttpsURLConnection urlConnection = null;
        switch(operacion) {
            case "login":
                direccion = "https://134.209.235.115/framos001/WEB/php/login.php";
                break;
            case "updateUsuarioDetalles":
                direccion = "https://134.209.235.115/framos001/WEB/php/updateUsuarioDetalles.php";
                break;
            case "updateUsuarioEjercicio":
                direccion = "https://134.209.235.115/framos001/WEB/php/updateUsuarioEjercicio.php";
                break;
            case "updatePesoUsuario":
                direccion = "https://134.209.235.115/framos001/WEB/php/updatePesoUsuario.php";
                break;
            case "existenDatos":
                direccion = "https://134.209.235.115/framos001/WEB/php/existenDatos.php";
                break;
            case "insertarPesoInicial":
                direccion = "https://134.209.235.115/framos001/WEB/php/insertarPesoInicial.php";
                break;
            case "getDatos":
                direccion = "https://134.209.235.115/framos001/WEB/php/getDatos.php";
                break;
            case "getPesos":
                direccion = "https://134.209.235.115/framos001/WEB/php/getPesos.php";
                break;
            case "insertarPeso":
                direccion = "https://134.209.235.115/framos001/WEB/php/insertarPeso.php";
                break;
            case "insertarUsuario":
                direccion = "https://134.209.235.115/framos001/WEB/php/insertarUsuario.php";
                break;
            case "insertarToken":
                direccion = "https://134.209.235.115/framos001/WEB/php/insertarToken.php";
                break;
            case "mensajesFCMweb":
                direccion = "https://134.209.235.115/framos001/WEB/php/mensajesFCMweb.php";
                break;
            case "saveImg":
                direccion = "https://134.209.235.115/framos001/WEB/php/saveImg.php";
                break;
            default:
                break;
        }


        try {
            //establece una conexion HTTPS
            urlConnection = GeneradorConexionesSeguras.getInstance().crearConexionSegura(context, direccion);
            if(!operacion.equals("saveImg")) {
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
            }
            //inicializamos los parametros de la petición
            String parametros ="";

            if(!operacion.equals("saveImg")) {
                //parámetros que le pasamos al php
                 parametros = "param1=" + usuario + "&param2=" + contrasena + "&param3=" + ejercicio + "&param4=" + peso + "&param5=" + altura + "&param6=" + fechaNac + "&param7=" + sexo + "&nombre=" + nombre + "&apellidos=" + apellidos + "&email=" + email + "&token=" + token + "&foto=" + foto + "&titulo=" + titulo;
            }else{
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("param1", usuario).appendQueryParameter("foto",foto).appendQueryParameter("titulo",titulo);
                 parametros = builder.build().getEncodedQuery();
            }

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);//necesario por el método POST u PUT
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //incluimos los parámetros en la conexion
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200){
                //guardamos los resultados en el json
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader (new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();

                System.out.println("********************"+operacion+"\n"+result+"\n************************");

                JSONParser parser = new JSONParser();
                json = (JSONObject) parser.parse(result);

                inputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return json;
        }


        public conexionBDWebService(Context cont, String oper, String usr, String nom, String apell, String em,String contra){
        context=cont;
        operacion=oper;
        usuario=usr;
        nombre=nom;
        apellidos=apell;
        email=em;
        contrasena=contra;
        }
        public conexionBDWebService(Context cont,String oper,String usr, String param){
            context=cont;
            operacion=oper;
            usuario=usr;
            contrasena=param;
            ejercicio=param;
            token = param;
        }
    public conexionBDWebService(Context cont,String oper,String usr, int p){
        context=cont;
        operacion=oper;
        usuario=usr;
        peso=p;
    }
        public conexionBDWebService(Context cont, String oper, String usr){
            context=cont;
            operacion=oper;
            usuario=usr;
        }
        public conexionBDWebService(Context cont,String oper,String usr, int pes, int alt, String nacimiento, String sex){
            context=cont;
            operacion=oper;
            usuario=usr;
            peso=pes;
            altura=alt;
            fechaNac=nacimiento;
            sexo=sex;
        }

}
