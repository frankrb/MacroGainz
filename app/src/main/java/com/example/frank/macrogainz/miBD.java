package com.example.frank.macrogainz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;

public class miBD extends SQLiteOpenHelper {
    private static final String DBNAME = "mydb";
    private static final int VERSION = 1;
    private SQLiteDatabase db = null;
    SQLiteDatabase myDB;

    public miBD(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuarios ('usuario' VARCHAR PRIMARY KEY NOT NULL,'nombre' VARCHAR,'apellidos' VARCHAR,'email' VARCHAR, 'contra' VARCHAR,'peso' INT,'altura' INT,'fecha_nacimiento' DATE,'sexo' VARCHAR,'ejercicio' VARCHAR)");
        db.execSQL("CREATE TABLE Pesos ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,'peso' INT,'idusuario' VARCHAR, FOREIGN KEY('idusuario') REFERENCES Usuarios ('usuario'))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void openDB(){
        myDB=getWritableDatabase();
    }
    public void closeDB(){
        if(myDB!=null && myDB.isOpen()){
            myDB.close();
        }
    }
    public boolean existeUsuario(String usuario){
        Cursor c = db.rawQuery("SELECT * FROM Usuarios WHERE usuario = "+usuario+"", null);
        int i=0;
        while (c.moveToNext()){
           i++;
        }
        c.close();
        db.close();
        if(i==0){
            return false;
        }else{
            return true;
        }
    }

    public long insertarUsuario(String usuario, String nombre,String apellidos, String email, String contra){
        ContentValues nuevo = new ContentValues();
        nuevo.put("usuario", usuario);
        nuevo.put("nombre", nombre);
        nuevo.put("apellidos", apellidos);
        nuevo.put("email", email);
        nuevo.put("contra", contra);
        return myDB.insert("Usuarios", null, nuevo);
    }
    public long updateUsuario(String usuario, String nombre,String apellidos, String email, String contra){
        ContentValues nuevo = new ContentValues();
        nuevo.put("nombre", nombre);
        nuevo.put("apellidos", apellidos);
        nuevo.put("email", email);
        nuevo.put("contra", contra);

        String whereClause= "usuario = '" + usuario +"'";



        return myDB.update("Usuarios", nuevo, whereClause, null);
    }
    //añadir datos al usuario
    public int updateUsuarioDetalles(String usuario, int peso, Integer altura, String nacimiento, String sexo) throws ParseException {
        db=getWritableDatabase();
        String startDate=nacimiento;
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date date = sdf1.parse(startDate);
        java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());
        ContentValues nuevo = new ContentValues();
        nuevo.put("altura", altura);
        nuevo.put("fecha_nacimiento", nacimiento);
        nuevo.put("peso", peso);
        nuevo.put("sexo", sexo);

        String whereClause= "usuario = '" + usuario +"'";

        return myDB.update("Usuarios", nuevo, whereClause, null);
    }

    //añadir ejercicio al usuario
    public int updateUsuarioEjercicio(String usuario,String ejercicio) throws ParseException {
        db=getWritableDatabase();

        ContentValues nuevo = new ContentValues();

        nuevo.put("ejercicio", ejercicio);

        String whereClause= "usuario = '" + usuario +"'";

        return myDB.update("Usuarios", nuevo, whereClause, null);
    }



    public long deleteUsuario(String usuario){
        String whereClause= "usuario = " + usuario;
        return myDB.delete("Usuarios", whereClause, null);
    }

    public void updatePesoUsuario(String usuario,int peso){

        db=getWritableDatabase();

        ContentValues nuevo = new ContentValues();
        nuevo.put("peso", peso);
        String whereClause= "usuario = '" + usuario +"'";

        db.update("Usuarios", nuevo, whereClause, null);

    }

    public void insertarPesoInicial(int peso, String usuario) {
        db = getWritableDatabase();
        ContentValues nuevo = new ContentValues();
        nuevo.put("idusuario", usuario);
        nuevo.put("peso", peso);
        long i =db.insert("Pesos", null, nuevo);
    }


    public boolean iniciarSesion(String usuario,String contra){

        db = getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Usuarios WHERE usuario = '" + usuario + "' AND contra = '" + contra + "' ", null);
        int i=0;

        while (c.moveToNext()){
            i++;
        }

        c.close();
        db.close();

        if(i>=1){
            return true;
        }else{
            return false;
        }
    }

    //comprobar si existen suficientes datos de usuario
    public boolean exitenDatos(String usuario){

        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Usuarios WHERE usuario = '" + usuario + "' AND (altura IS NULL OR altura = '' )", null);
        int i=0;

        while (c.moveToNext()){
            i++;
        }

        c.close();
        db.close();

        if(i>=1){
            return true;
        }else{
            return false;
        }
    }



    //calcular edad
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getEdad(String usuario) throws ParseException {

        int edad=0;
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT fecha_nacimiento FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            //calculamos la edad
            String startDate = c.getString(0);

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

            edad = diff1.getYears();

            i++;
        }

        c.close();
        db.close();

        if(i>=1){
            return edad;
        }else{
            return 0;
        }
    }

    public long insertarPeso(int peso, String usuario){
        db = getWritableDatabase();
        ContentValues nuevo = new ContentValues();
        nuevo.put("idusuario", usuario);
        nuevo.put("peso", peso);
        long i =db.insert("Pesos", null, nuevo);

        if(i==-1){
            System.out.println("Error al añadir a PESOS");
        }else{
            updatePesoUsuario(usuario,peso);
        }

        return i;
    }


    public int getPeso(String usuario) {
        int peso=0;
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT peso FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
             peso = c.getInt(0);
            i++;
        }
        c.close();

        if(i>=1){
            return peso;
        }else{
            return 0;
        }
    }

    public String getSexo(String usuario) {
        String sexo="";
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT sexo FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            sexo = c.getString(0);
            i++;
        }
        c.close();

        if(i>=1){
            return sexo;
        }else{
            return null;
        }
    }

    public int getAltura(String usuario) {
        int altura=0;
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT altura FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            altura = c.getInt(0);
            i++;
        }
        c.close();

        if(i>=1){
            return altura;
        }else{
            return 0;
        }
    }

    public int[] getPesos(String usuario) {
        int peso;
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT peso FROM Pesos WHERE idusuario = '" + usuario + "'", null);

        int total = c.getCount();
        int[] datos = new int[total];

        int i=0;

        while (c.moveToNext()){
            //calculamos la edad
            peso = c.getInt(0);
            datos[i]=peso;
            i++;
        }
        c.close();

        return datos;
    }

    public String getActividad(String usuario) {
        String actividad="";
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT ejercicio FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            //calculamos la edad
            actividad= c.getString(0);
            i++;
        }
        c.close();

        if(i>=1){
            return actividad;
        }else{
            return null;
        }

    }

    public String getNombreApellidos(String usuario) {
        String nombreApellidos="";
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT nombre,apellidos FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            //calculamos la edad
            nombreApellidos= c.getString(0);
            nombreApellidos = nombreApellidos+" "+c.getString(1);
            i++;
        }
        c.close();

        if(i>=1){
            return nombreApellidos;
        }else{
            return null;
        }

    }

    public String getFecha(String usuario) throws ParseException {
        String fecha="";
        db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT fecha_nacimiento FROM Usuarios WHERE usuario = '" + usuario + "'", null);
        int i=0;

        while (c.moveToNext()){
            fecha = c.getString(0);
            i++;
        }
        c.close();

        if(i>=1){
            return fecha;
        }else{
            return null;
        }
    }
}
