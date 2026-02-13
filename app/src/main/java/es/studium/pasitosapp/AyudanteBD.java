package es.studium.pasitosapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AyudanteBD extends SQLiteOpenHelper
{
    private static final String NOMBRE_BASE_DE_DATOS = "pasitos", NOMBRE_TABLA = "ubicaciones";
    private static final int VERSION_BASE_DE_DATOS = 1;

    public AyudanteBD(Context context)
    {
        super(context, NOMBRE_BASE_DE_DATOS, null, VERSION_BASE_DE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(String.format
                ("CREATE TABLE IF NOT EXISTS %s(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "lat REAL, lon REAL, bateria INTEGER)", NOMBRE_TABLA));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
//        db.execSQL(String.format("DROP TABLE IF EXISTS %s", NOMBRE_TABLA));
//        onCreate(db);
    }

}


