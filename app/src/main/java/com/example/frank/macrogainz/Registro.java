package com.example.frank.macrogainz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    EditText nombre;
    EditText apellidos;
    EditText usuario;
    EditText email;
    EditText contrasena;
    EditText contrasena2;
    Button registrarse;
    //miBD GestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //GestorBD = new miBD(Registro.this);

        //accion de registro
        registrarse=findViewById(R.id.registrarse);
        usuario=findViewById(R.id.user2);
        nombre=findViewById(R.id.nombre);
        apellidos=findViewById(R.id.apellidos);
        email=findViewById(R.id.email);
        contrasena=findViewById(R.id.contra1);
        contrasena2=findViewById(R.id.contra2);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GestorBD.openDB();
                boolean correcto=false;
                if(datosOK(usuario.getText().toString().trim(),nombre.getText().toString().trim(),apellidos.getText().toString().trim(),contrasena.getText().toString().trim(),contrasena2.getText().toString().trim(),email.getText().toString().trim())) {
                    try {
                        correcto = controladorBDWebService.getInstance().insertarUsuario(getApplicationContext(),"insertarUsuario",usuario.getText().toString().trim(), nombre.getText().toString().trim(), apellidos.getText().toString().trim(), email.getText().toString().trim(), contrasena.getText().toString().trim());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (correcto) {
                        //finish();
                    }
                }
            }
        });
    }

    public boolean datosOK(String user, String nombre, String apellidos, String contra, String contra1, String email){
        boolean ok = false;
        Pattern usuario = Pattern.compile("[a-zA-Z0-9]{1,30}");
        Pattern nombre_apellido = Pattern.compile("[a-zA-Z]{1,30}");
        Pattern contrasena = Pattern.compile("[A-Za-z0-9_*@#%&!¡=.;,]{1,30}");
        Pattern email_pattern = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        Matcher match1 = usuario.matcher(user);
        Matcher match2 = nombre_apellido.matcher(nombre);
        Matcher match3 = nombre_apellido.matcher(apellidos);
        Matcher match4 = contrasena.matcher(contra);
        Matcher match5 = email_pattern.matcher(email);

        if(user.length()>=1 && nombre.length()>=1 && apellidos.length()>=1 && contra.length()>=1){
            ok=true;
            if(match1.matches()){
                ok = true;
                if(match2.matches()){
                ok=true;
                    if(match3.matches()){
                    ok=true;
                        if(match4.matches()){
                        ok=true;
                        }else{
                            Toast error = Toast.makeText(getApplicationContext(), "Cambie su Contraseña", Toast.LENGTH_SHORT);
                            error.show();
                            ok=false;
                        }
                    }else{
                        Toast error = Toast.makeText(getApplicationContext(), "Cambie su Apellido", Toast.LENGTH_SHORT);
                        error.show();
                        ok=false;
                    }
                }else{
                    Toast error = Toast.makeText(getApplicationContext(), "Cambie su Nombre", Toast.LENGTH_SHORT);
                    error.show();
                    ok=false;
                }
            }else{
                Toast error = Toast.makeText(getApplicationContext(), "Cambie el nombre de usuario", Toast.LENGTH_SHORT);
                error.show();
                ok=false;
            }
        }else{
            Toast error = Toast.makeText(getApplicationContext(), "No puede dejar campos vacíos", Toast.LENGTH_SHORT);
            error.show();
            ok=false;
        }
        if(ok){

            if(match5.find()){
            ok=true;
            }else{
                Toast error = Toast.makeText(getApplicationContext(), "Introduzca un email válido", Toast.LENGTH_SHORT);
                error.show();
                ok=false;
            }
        }

        if(ok){
            if(contra.equals(contra1)){
                ok=true;
            }else{
                Toast error = Toast.makeText(getApplicationContext(), "Las contraseñas deben coincidir", Toast.LENGTH_SHORT);
                error.show();
                ok=false;
            }
        }

        return ok;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("nom", String.valueOf(nombre.getText()));
        savedInstanceState.putString("apell", String.valueOf(apellidos.getText()));
        savedInstanceState.putString("us", String.valueOf(usuario.getText()));
        savedInstanceState.putString("em", String.valueOf(email.getText()));
        savedInstanceState.putString("cont1", String.valueOf(contrasena.getText()));
        savedInstanceState.putString("cont2", String.valueOf(contrasena2.getText()));

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        nombre.setText(savedInstanceState.getString("nom"));
        apellidos.setText(savedInstanceState.getString("apell"));
        usuario.setText(savedInstanceState.getString("us"));
        email.setText(savedInstanceState.getString("em"));
        contrasena.setText(savedInstanceState.getString("cont1"));
        contrasena2.setText(savedInstanceState.getString("cont2"));

    }
}
