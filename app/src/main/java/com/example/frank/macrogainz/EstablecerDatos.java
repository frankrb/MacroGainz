package com.example.frank.macrogainz;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EstablecerDatos extends AppCompatActivity {
    private String genero ="Seleccionar género";
    private int peso= 0;
    private int altura= 0;
    private String fecha="Seleccione su fecha de nacimiento";
    SimpleDateFormat simpleDateFormat;
    boolean set_altura = false;
    boolean set_peso = false;
    private Button aceptar;
    String nombreUsuario;
    //miBD GestorDB = new miBD(this);
    TextView texto;
    Runnable run;
    String[] datos;
    String[] values;
    ListView listView;
    AdaptadorListView eladap;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        datos=new String[]{getString(R.string.genero),getString(R.string.peso),getString(R.string.altura),getString(R.string.fecha_nacimiento)};
        values= new String[]{genero, String.valueOf(peso) + " kg", String.valueOf(altura) + " cm", String.valueOf(fecha)};

        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_establecer_datos);
        texto=(TextView) findViewById(R.id.textoTitulo);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             nombreUsuario= extras.getString("usuario");
             texto.append(" " + nombreUsuario);
        }

        aceptar=(Button) findViewById(R.id.aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(genero!="Seleccionar género" && fecha != "Seleccionar fecha" && peso != 0 && altura !=0){
                    //GestorDB.openDB();

                        Intent i = new Intent(EstablecerDatos.this,EstablecerEjercicio.class);
                        i.putExtra("usuario",nombreUsuario);
                        i.putExtra("peso",peso);
                        i.putExtra("altura",altura);
                        i.putExtra("fecha",fecha);
                        i.putExtra("genero",genero);
                        startActivity(i);
                        finish();

                }else{
                    Toast error = Toast.makeText(getApplicationContext(), getText(R.string.error_data), Toast.LENGTH_SHORT);
                    error.show();
                }


            }
        });

        listView= findViewById(R.id.listview);

        eladap = new AdaptadorListView(getApplicationContext(),datos,values);

        listView.setAdapter(eladap);

        run = new Runnable() {
            public void run() {
                //reload content
                values[0]=genero;
                values[1]=String.valueOf(peso)+" kg";
                values[2]=String.valueOf(altura)+" cm";
                values[3]=fecha;
                eladap.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
                listView.setAdapter(eladap);

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        // se llama al dialog del género
                        selectGenDialog();
                        values[position] = genero;
                        eladap.notifyDataSetChanged();
                        listView.setAdapter(eladap);
                        break;
                    case 1:
                        // se llama al dialog del peso
                        numberPickerDialogPeso();
                        if(set_peso==true) {
                            values[position] = peso + " kg";
                            eladap.notifyDataSetChanged();
                            listView.setAdapter(eladap);
                        }
                        break;
                    case 2:
                        // se llama al dialog de la altura
                        numberPickerDialog();
                        if(set_altura==true) {
                            values[position] = altura + " cm";
                            eladap.notifyDataSetChanged();
                            listView.setAdapter(eladap);
                            numberPickerDialog();
                        }
                        break;
                    case 3:
                        //se llama al dialog de fecha de nacimiento
                        datePickerDialog();
                        values[position] = fecha;
                        eladap.notifyDataSetChanged();
                        listView.setAdapter(eladap);
                        break;
                    default:
                        System.out.println("No se ha seleccionado ninguna opción válida");
                }
            }
        });
    }
    private void numberPickerDialog(){
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMaxValue(300);
        myNumberPicker.setMinValue(100);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                altura = newVal;
                runOnUiThread(run);
            }
        };

        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(myNumberPicker);
        builder.setTitle(getString(R.string.select_altura));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    set_altura=true;
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                set_altura=false;
            }
        });
        builder.show();
    }

    private void numberPickerDialogPeso(){
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMaxValue(400);
        myNumberPicker.setMinValue(40);
        NumberPicker.OnValueChangeListener myValChangedListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                peso = newVal;
                runOnUiThread(run);
            }
        };

        myNumberPicker.setOnValueChangedListener(myValChangedListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(myNumberPicker);
        builder.setTitle(getString(R.string.select_peso));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                set_peso=true;
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                set_peso=false;
            }
        });
        builder.show();
    }

    private void datePickerDialog(){
        Calendar calendario=Calendar.getInstance();
        int anyo=calendario.get(Calendar.YEAR);
        int mes=calendario.get(Calendar.MONTH);
        int dia=calendario.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpck= new DatePickerDialog(EstablecerDatos.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                simpleDateFormat = new SimpleDateFormat(year+"-"+ month +"-"+dayOfMonth);
                fecha = year+"-" + (month+1) +"-"+dayOfMonth;
                runOnUiThread(run);
            }
        }, anyo, mes, dia);
        dpck.show();
    }

    private void selectGenDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EstablecerDatos.this);
        builder.setTitle(getString(R.string.select_genero));
        CharSequence[] opciones = {"Masculino", "Femenino"};
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    genero="masculino";
                    runOnUiThread(run);
                }else{
                    genero="femenino";
                    runOnUiThread(run);
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("gen", values[0]);
        savedInstanceState.putString("pes", values[1]);
        savedInstanceState.putString("alt", values[2]);
        savedInstanceState.putString("fech", values[3]);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        values[0]=savedInstanceState.getString("gen");
        values[1]=savedInstanceState.getString("pes");
        values[2]=savedInstanceState.getString("alt");
        values[3]=savedInstanceState.getString("fech");
    }
}
