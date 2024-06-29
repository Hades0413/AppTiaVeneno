package com.example.apptiaveneno.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlOpenHelper extends SQLiteOpenHelper {

    public SqlOpenHelper(Context context) {
        super(context, "tiaveneno.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CATEGORIA (" +
                "IdCategoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Descripcion VARCHAR(100)," +
                "FechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP)");

        db.execSQL("CREATE TABLE PRODUCTO (" +
                "IdProducto INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Codigo VARCHAR(100)," +
                "IdCategoria INTEGER," +
                "Descripcion VARCHAR(100)," +
                "PrecioCompra DECIMAL(10,2)," +
                "PrecioVenta DECIMAL(10,2)," +
                "Stock INTEGER," +
                "FechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "RutaImagen VARCHAR(255)," +
                "FOREIGN KEY (IdCategoria) REFERENCES CATEGORIA(IdCategoria))");

        db.execSQL("CREATE TABLE USUARIO (" +
                "IdUsuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NombreCompleto VARCHAR(100)," +
                "Correo VARCHAR(100)," +
                "Clave VARCHAR(100)," +
                "FechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
