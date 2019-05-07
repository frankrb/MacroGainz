package com.example.frank.macrogainz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.simple.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class PaginaPrincipal extends AppCompatActivity {

    NotificationManager elManager;
    NotificationCompat.Builder elBuilder;

    TextView welcome;

    GraphView graph;

    String nombreUsuario;
    String actividadFisica;
    int edad;
    int kcal;
    int peso;
    int mejorPeso;
    int altura;

    int nuevoPeso=0;
    boolean pesoOK=false;

    String sexo;
    String plan;

    TextView pesoIdeal;
    TextView kcalDiarias;
    TextView pesoActual;
    Button perfil;
    Button addPeso;

    //miBD GestorBD;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //GestorBD.closeDB();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);

        graph = (GraphView) findViewById(R.id.graph);
        welcome = findViewById(R.id.welcome);
        pesoIdeal = findViewById(R.id.pesoIdeal);
        pesoActual=findViewById(R.id.pesoActual);
        kcalDiarias = findViewById(R.id.kcalorias);
        perfil = findViewById(R.id.perfil);
        addPeso=findViewById(R.id.addPeso);

        //modificamos el grafico
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        //GestorBD = new miBD(PaginaPrincipal.this);
        //GestorBD.openDB();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             nombreUsuario= extras.getString("usuario");
             System.out.println(nombreUsuario);
             welcome.setText(getText(R.string.welcome) +" "+ nombreUsuario);

             //comprobamos si el usuario ya tiene
            // peso edad altura
            //GestorBD.exitenDatos(nombreUsuario)
            try {
                if(!controladorBDWebService.getInstance().existenDatos(this, "existenDatos",nombreUsuario)){
                    //añadimos los datos
                    Intent i = new Intent(PaginaPrincipal.this, EstablecerDatos.class);
                    i.putExtra("usuario", nombreUsuario);
                    startActivity(i);
                    finish();
                }else{
                 //cargamos datos en la pagina
                    JSONObject json = controladorBDWebService.getInstance().getDatos(this, "getDatos",nombreUsuario);
                    try {
                        peso = Integer.parseInt((String) json.get("peso"));
                        sexo = (String) json.get("sexo");
                        altura = Integer.parseInt((String)json.get("altura"));
                        actividadFisica = (String) json.get("ejercicio");
                        edad = this.calcularEdad((String) json.get("fecha_nacimiento"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                   if(peso!=0 && sexo !=null && altura!=0){
                   mejorPeso = getMejorPeso(altura,sexo);
                   }else{
                       System.out.println("ERROR DE VALORES DE LOS DATOS");
                   }
                   //aplicamos un plan: bajar de peso - subir de peso - mantenimiento
                   if((mejorPeso - peso) <= -2){

                       plan="bajar";

                   }else if((mejorPeso - peso) >= 2){

                       plan ="subir";

                   }else{

                       plan = "mantenimiento";
                   }

                   int kcalDia=calcularKcalDiarias(peso,altura,edad,sexo,actividadFisica);

                   if(plan.equals("mantenimiento")){
                       kcal =kcalDia;
                   }else if(plan.equals("subir")){

                       kcal=kcalDia+100;

                   }else if(plan.equals("bajar")){

                       kcal=kcalDia-100;
                   }

                   pesoActual.setText("Este es tu peso actual: " + peso + " kg");
                   pesoIdeal.setText("Este es tu peso ideal: " + mejorPeso +" kg");
                   kcalDiarias.setText(kcal+" kcal");

                   mostrarDatos(controladorBDWebService.getInstance().getPesos(getApplicationContext(),"getPesos",nombreUsuario));
                   System.out.println("Se ha terminado de iniciar");

                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //configurar notificaciones
        elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        elBuilder = new NotificationCompat.Builder(this, "IdCanal");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "Canal de Notificaciones", NotificationManager.IMPORTANCE_DEFAULT);
            elCanal.setDescription("Notificacion Peso");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);
            elManager.createNotificationChannel(elCanal);
        }

        elBuilder.setSmallIcon(R.drawable.logo)
                .setContentTitle("Este es su peso actual "+peso)
                .setContentText("Este es su peso ideal "+mejorPeso)
                .setSubText("Se ha añadido un nuevo peso")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);


        addPeso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerDialogPeso();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PaginaPrincipal.this, PerfilUsuario.class);
                i.putExtra("usuario", nombreUsuario);
                startActivity(i);
                finish();
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calcularEdad(String fecha) throws ParseException {
        int annos;

        String startDate = fecha;

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

        java.util.Date date = sdf1.parse(startDate);
        java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());

        Calendar calendar =Calendar.getInstance();
        calendar.setTime(sqlStartDate);
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int dates=calendar.get(Calendar.DATE);



        LocalDate l1 = LocalDate.of(year, month, dates);

        LocalDate now1 = LocalDate.now();

        Period diff1 = Period.between(l1, now1);

        annos = diff1.getYears();

        System.out.println("Se ha terminado de calcular la edad " + annos+peso+altura);
        return annos;

    }
    /**Calcula las kcal diarias del usuario
     * **/
    private int calcularKcalDiarias(int peso, int altura, int edad, String sexo,String actividad) {

        int kcal;

        if(sexo.equals("mascuino")){
            kcal = (int) ((10*peso) + (6.25*altura) - (5*edad) + 5);
        }else{
            kcal = (int) ((10*peso) + (6.25*altura) - (5*edad) -161);
        }

        switch (actividad){
            case "sedentario":
                kcal= (int) (kcal*1.2);
                break;
            case "activo":
                kcal = (int) (kcal*1.375);
                break;
            case "muyActivo":
                kcal = (int) (kcal*1.55);
                break;
            case "extremadamenteActivo":
                kcal = (int) (kcal*1.725);
                break;
                default:
        }
        System.out.println("Se ha terminado de calcular las kcal: "+ kcal);
        return kcal;
    }

    private int getMejorPeso(int altura, String sexo) {
        int mejorPeso=0;
        if(sexo.equals("masculino")){
            mejorPeso=altura-100;
        }else{
            mejorPeso=altura-104;
        }
        System.out.println("Se ha terminado de calcular el mejor peso" + mejorPeso);
       return  mejorPeso;
    }


    /**Muestra la gráfica con los pesos del usuario en el tiempo
     * **/
    private void mostrarDatos(int[] datos){
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {});
        for(int i=0;i<datos.length;i++){
            DataPoint data = new DataPoint(i,datos[i]);
            series.appendData(data,true,datos.length);
        }

        graph.addSeries(series);
    }


    private void numberPickerDialogPeso(){
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMaxValue(400);
        myNumberPicker.setMinValue(50);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                nuevoPeso = newVal;
            }
        };

        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(myNumberPicker);
        builder.setTitle(getString(R.string.select_peso));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pesoOK=true;
                peso=nuevoPeso;
                //subir el peso al servidor
                controladorBDWebService.getInstance().insertarPeso(getApplicationContext(),"insertarPeso",nombreUsuario, nuevoPeso);

                try {
                    //muestra los datos
                    mostrarDatos(controladorBDWebService.getInstance().getPesos(getApplicationContext(),"getPesos",nombreUsuario));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //enviar notificación de peso

                elManager.notify(1, elBuilder.build());


                recreate();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pesoOK=false;
            }
        });
        builder.show();
    }

}
