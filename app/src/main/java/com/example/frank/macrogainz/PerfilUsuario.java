package com.example.frank.macrogainz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static android.support.constraint.Constraints.TAG;

public class PerfilUsuario extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CODIGO_DE_PERMISO = 1;
    static final int CODIGO_DE_PERMISO_STORAGE = 1;

    String foto;

    String nombreUsuario;

    String actividadBD;
    String msgActividad;
    int alt;
    int pes;
    String sex;

    //miBD GestorBD = new miBD(this);

    ImageView imgPerfil;

    TextView usuario;

    TextView nombreApellidos;
    TextView fecha;
    TextView edad;
    TextView peso;
    TextView altura;
    TextView genero;
    TextView actividad;
    TextView plan;

    Button volver;
    Button notificaciones;

    int mejorPeso;

    String planA;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        volver = findViewById(R.id.volver);
        notificaciones=findViewById(R.id.notFcm);
        imgPerfil=findViewById(R.id.imagenPerfil);

        usuario=findViewById(R.id.userName);
        nombreApellidos=findViewById(R.id.nombreApellidos);
        fecha=findViewById(R.id.fecha);
        edad=findViewById(R.id.edad);
        peso=findViewById(R.id.peso);
        altura=findViewById(R.id.altura);
        genero=findViewById(R.id.genero);
        actividad = findViewById(R.id.actividad);
        plan=findViewById(R.id.infoPlan);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             nombreUsuario = extras.getString("usuario");
        }


        usuario.setText(nombreUsuario);
        //vemos si el usuario tiene foto de perfil
        try {
            Bitmap foto64 = (Bitmap) new getInputStream(this, nombreUsuario).execute().get();
            if(foto64!=null){
                //si tiene la cargamos
                imgPerfil.setImageBitmap(foto64);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = controladorBDWebService.getInstance().getDatos(this, "getDatos",nombreUsuario);
            nombreApellidos.setText("Nombre: "+ (String) json.get("nombre")+" "+(String) json.get("apellidos"));
            fecha.setText("Fecha de nacimiento: "+(String) json.get("fecha_nacimiento"));
            edad.setText("Edad: "+this.calcularEdad((String) json.get("fecha_nacimiento")));
            peso.setText("Peso actual: "+ (String)json.get("peso")+ " kg");
            pes=Integer.parseInt ((String)json.get("peso"));
            altura.setText("Altura: " + (String)json.get("altura")+" cm");
            alt = Integer.parseInt ((String)json.get("altura"));
            genero.setText("Genero: "+(String)json.get("sexo"));
            sex = (String)json.get("sexo");
            actividadBD=(String)json.get("ejercicio");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch(actividadBD) {
            case "sedentario":
                msgActividad="Sedentari@";
                break;
            case "activo":
                msgActividad="Activ@";
                break;
            case "muyActivo":
                msgActividad="Muy Activ@";
                break;
            case "extremadamenteActivo":
                msgActividad="Extremadamente Activ@";
                break;

            default:
        }

        actividad.setText("Actividad física: "+msgActividad);

        mejorPeso=getMejorPeso(alt,sex);

        //aplicamos un plan: bajar de peso - subir de peso - mantenimiento
        if((mejorPeso - pes) <= -2){

            planA=getString(R.string.sobrepeso);

        }else if((mejorPeso - pes) >= 2){

            planA=getString(R.string.bajopeso);

        }else{

            planA=getString(R.string.pesoIdeal);
        }

        plan.setText("\nPlan a seguir: \n"+planA);


        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PerfilUsuario.this, PaginaPrincipal.class);
                i.putExtra("usuario", nombreUsuario);
                startActivity(i);
                finish();
            }
        });


        notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Subscripción a entrenamientos");
                // [START subscribe_topics]
                FirebaseApp.initializeApp(PerfilUsuario.this);
                FirebaseMessaging.getInstance().subscribeToTopic("Entrenamiento")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d(TAG, msg);
                                Toast.makeText(PerfilUsuario.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]

                // Get token
                // [START retrieve_current_token]
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                //guardamos el token en el servidor
                                controladorBDWebService.getInstance().insertarToken(getApplicationContext(),"insertarToken",nombreUsuario,token);

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d(TAG, msg);
                                //Toast.makeText(PerfilUsuario.this, msg, Toast.LENGTH_SHORT).show();
                                //llamamos a nuestro php para que envíe una notficación a nuestro teléfono
                                controladorBDWebService.getInstance().mensajesFCMweb(getApplicationContext(),"mensajesFCMweb",nombreUsuario);
                            }
                        });
                // [END retrieve_current_token]
            }
        });

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CODIGO_DE_PERMISO);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {
            // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Toast.makeText(PerfilUsuario.this, "ES NECESARIO EL PERMISO PARA SACAR FOTO DE PERFIL DE USUARIO", Toast.LENGTH_SHORT).show();

            }
            else{
            //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
            //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Toast.makeText(PerfilUsuario.this, "ES NECESARIO EL PERMISO PARA SACAR FOTO DE PERFIL DE USUARIO", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }

        }
        else {
        //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //reducimos su tamaño
            int anchoDestino = imgPerfil.getWidth();
            int altoDestino = imgPerfil.getHeight();

            int anchoImagen = imageBitmap.getWidth();
            int altoImagen = imageBitmap.getHeight();

            float ratioImagen = (float) anchoImagen / (float) altoImagen;
            float ratioDestino = (float) anchoDestino / (float) altoDestino;
            int anchoFinal = anchoDestino;
            int altoFinal = altoDestino;
            if (ratioDestino > ratioImagen) {
                anchoFinal = (int) ((float)altoDestino * ratioImagen);
            } else {
                altoFinal = (int) ((float)anchoDestino / ratioImagen);
            }

            Bitmap newBitmap = Bitmap.createScaledBitmap(imageBitmap,anchoFinal,altoFinal,true);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] fototransformada = stream.toByteArray();

            String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

            String titulo="foto_usuario";

            try {
                controladorBDWebService.getInstance().saveImg(getApplicationContext(), "saveImg",nombreUsuario,fotoen64,titulo);
                Bitmap foto64 = (Bitmap) new getInputStream(this, nombreUsuario).execute().get();
                if(foto64==null){
                }else{
                    imgPerfil.setImageBitmap(foto64);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private int getMejorPeso(int altura, String sexo) {
        int mejorPeso=0;
        if(sexo.equals("masculino")){
            mejorPeso=altura-100;
        }else{
            mejorPeso=altura-104;
        }
        return  mejorPeso;
    }

    private int calcularEdad(){
        int edad = 0;

        return edad;
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_DE_PERMISO:{
        // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }

                }
                else {
        // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA
                    Toast.makeText(PerfilUsuario.this, "ES NECESARIO EL PERMISO PARA SACAR FOTO DE PERFIL DE USUARIO", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }

    }

}
