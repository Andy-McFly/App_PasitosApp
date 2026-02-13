package es.studium.pasitosapp.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import es.studium.pasitosapp.AyudanteBD;

public class UbicacionController
{
    private AyudanteBD ayudanteBD;
    private String NOMBRE_TABLA = "ubicaciones";

    public UbicacionController(Context contexto)
    {
        ayudanteBD = new AyudanteBD(contexto);
    }
    public void insertar(double lat, double lon, int bateria)
    {
        SQLiteDatabase bd = ayudanteBD.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("lat", lat);
        valores.put("lon", lon);
        valores.put("bateria", bateria);
        bd.insert("ubicaciones", null, valores);
    }

    public Cursor obtenerUbicaciones()
    {
        SQLiteDatabase bd = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"id", "lat", "lon", "bateria"};
        Cursor cursor = bd.query(NOMBRE_TABLA, columnasAConsultar,
                null, null, null, null, "id" );
        return cursor;
    }

    public void borrarDatos()
    {
        SQLiteDatabase bd = ayudanteBD.getWritableDatabase();
        bd.delete(NOMBRE_TABLA, null, null);
    }
}
