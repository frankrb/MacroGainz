package com.example.frank.macrogainz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MacroGainz extends AppCompatActivity {
    private ImageView logo;
    private TextView usuario;
    private TextView contra;
    private Button registro;
    private Button login;
    //private miBD GestorBD;
    private String direccion="https://134.209.235.115/framos001/WEB/php/usuarios.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_gainz);

        //GestorBD = new miBD(MacroGainz.this);

        logo = findViewById(R.id.imageView);
        logo.setImageResource(R.drawable.logo);
        this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        //usuario y contra
        usuario=findViewById(R.id.user);
        contra=findViewById(R.id.contra);
        //registro
        Button registro = findViewById(R.id.registroUsuario);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MacroGainz.this,Registro.class);
                startActivity(i);
            }
        });

        //login
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean correcto=false;
                controladorBDWebService controlador = controladorBDWebService.getInstance();
                try {
                    //intenta logearse con el servidor
                    correcto = controlador.login(getApplicationContext(),"login",usuario.getText().toString().trim(),contra.getText().toString().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent i =new Intent(MacroGainz.this, PaginaPrincipal.class);
                i.putExtra("usuario",usuario.getText().toString().trim());

                if(correcto){
                    //GestorBD.closeDB();
                    startActivity(i);
                    finish();
                }else{
                    Toast error = Toast.makeText(getApplicationContext(), getText(R.string.error_data), Toast.LENGTH_SHORT);
                    error.show();
                }
            }
        });

    }
}
