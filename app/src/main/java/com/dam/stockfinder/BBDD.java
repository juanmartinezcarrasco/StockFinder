package com.dam.stockfinder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BBDD extends SQLiteOpenHelper {


    private static final int VERSION_BASEDATOS = 1;
    private static final String NOMBRE_BASEDATOS = "mibasedatos.db";
    private static final String TABLA_USUARIOS ="CREATE TABLE IF NOT EXISTS usuarios " +
            " (ciudad TEXT PRIMARY KEY, nombre TEXT, password TEXT)";

    public BBDD(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_USUARIOS);

        // Insertar datos después de crear la tabla
        ContentValues values = new ContentValues();
        values.put("ciudad", "Sevilla");
        values.put("nombre", "user");
        values.put("password", "userpass");
        db.insert("usuarios", null, values);

        ContentValues values2 = new ContentValues();
        values2.put("ciudad", "Madrid");
        values2.put("nombre", "admin");
        values2.put("password", "adminpass");
        db.insert("usuarios", null, values2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLA_USUARIOS);
        onCreate(db);
    }

    public ArrayList<Usuarios> obtenerUsuarios() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Usuarios> lista_usuarios = new ArrayList<Usuarios>();
        String[] valores_recuperar = {"ciudad", "nombre", "password"};
        Cursor c = db.query("usuarios", valores_recuperar, null, null, null, null, null, null);
        c.moveToFirst();
        do {
            Usuarios usuarios = new Usuarios(c.getString(0), c.getString(1), c.getString(2));
            lista_usuarios.add(usuarios);
        } while (c.moveToNext());
        db.close();
        c.close();
        return lista_usuarios;
    }
}

