package com.example.frank.macrogainz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EstablecerEjercicio extends AppCompatActivity {

    private int position;

    private String nombreEjercicio;
    private String nombreUsuario;
    public Button aceptar;

    private String genero;
    private int peso;
    private int altura;
    private String fecha;


    //&miBD GestorBD = new miBD(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establecer_ejercicio);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombreUsuario= extras.getString("usuario");
            genero=extras.getString("genero");
            peso=extras.getInt("peso");
            altura=extras.getInt("altura");
            fecha=extras.getString("fecha");
        }

        //GestorBD.openDB();

        String[] nombres = {"Nada de ejercicio","Ejercicio ligero \nDos días por semana", "Ejercicio moderado \nCuatro días por semana", "Deporte regular \nSeis días a la semana"};

        ArrayList<Integer> imagenes = new ArrayList<>();

        imagenes.add(R.drawable.sofa);
        imagenes.add(R.drawable.andar);
        imagenes.add(R.drawable.correr);
        imagenes.add(R.drawable.gym);

        aceptar=findViewById(R.id.aceptarEjercicio);

        RecyclerView lalista= findViewById(R.id.elreciclerview);

        ElAdaptadorRecycler eladaptador = new ElAdaptadorRecycler(nombres,imagenes);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual= new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        lalista.setLayoutManager(elLayoutRejillaIgual);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position=Datos.getInstance().getPosicion();

                switch(position) {
                    case 0:
                        nombreEjercicio="sedentario";
                        break;
                    case 1:
                        nombreEjercicio="activo";
                        break;
                    case 2:
                        nombreEjercicio="muyActivo";
                        break;
                    case 3:
                        nombreEjercicio="extremadamenteActivo";
                        break;

                    default:
                }

                try {
                    //1 == GestorBD.updateUsuarioEjercicio(nombreUsuario, nombreEjercicio)
                    //introduce en la BD del servidor la cantidad de ejercicio que hace el usuario
                    if(controladorBDWebService.getInstance().updateUsuarioEjercicio(getApplicationContext(), "updateUsuarioEjercicio",nombreUsuario,nombreEjercicio)){
                        Intent i= new Intent(EstablecerEjercicio.this,PaginaPrincipal.class);
                        i.putExtra("usuario",nombreUsuario);
                        startActivity(i);
                        finish();
                    }else{
                        Toast errorDB = Toast.makeText(getApplicationContext(), getText(R.string.error_BD), Toast.LENGTH_SHORT);
                        errorDB.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                boolean correcto=false;
                try {
                    //row = GestorBD.updateUsuarioDetalles(nombreUsuario,peso,altura,fecha,genero);
                    //introduce los datos del usuario e inserta el peso inicial del mismo
                    correcto = controladorBDWebService.getInstance().updateUsuarioDetalles(getApplicationContext(), "updateUsuarioDetalles",nombreUsuario,peso,altura,fecha,genero);
                    controladorBDWebService.getInstance().insertarPesoInicial(getApplicationContext(), "insertarPesoInicial",nombreUsuario,peso);

                    //GestorBD.insertarPesoInicial(peso,nombreUsuario);
                    //GestorBD.closeDB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(correcto) {
                    Toast msg = Toast.makeText(getApplicationContext(), getText(R.string.msg_ok) + "\n" + genero + "\n" + peso + "\n" + altura + "\n" + fecha, Toast.LENGTH_SHORT);
                    msg.show();
                    Intent i = new Intent(EstablecerEjercicio.this,PaginaPrincipal.class);
                    i.putExtra("usuario",nombreUsuario);
                    startActivity(i);
                    finish();
                }else {
                    Toast errorDB = Toast.makeText(getApplicationContext(), getText(R.string.error_BD), Toast.LENGTH_SHORT);
                    errorDB.show();
                }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //GestorBD.closeDB();
    }
}
