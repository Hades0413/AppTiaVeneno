package com.example.apptiaveneno.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apptiaveneno.Data.SqlOpenHelper;
import com.example.apptiaveneno.Entity.Categoria;

import java.util.ArrayList;

public class MySqlCategoriaDAO {

    SqlOpenHelper admin;

    public MySqlCategoriaDAO(Context context) {
        admin = new SqlOpenHelper(context);
    }

    public ArrayList<Categoria> listaCategoria() {
        ArrayList<Categoria> lista = new ArrayList<>();
        SQLiteDatabase base = admin.getReadableDatabase();
        Cursor filas = base.rawQuery("select IdCategoria, Descripcion from CATEGORIA", null);
        while (filas.moveToNext()) {
            Categoria cat = new Categoria();
            cat.setIdCategoria(filas.getInt(0));
            cat.setDescripcion(filas.getString(1));
            lista.add(cat);
        }
        filas.close();
        base.close();
        return lista;
    }

    public ArrayList<Categoria> listaCategoria(String descripcion) {
        ArrayList<Categoria> lista = new ArrayList<>();
        SQLiteDatabase base = admin.getReadableDatabase();
        Cursor filas = base.rawQuery("select IdCategoria, Descripcion from CATEGORIA where Descripcion like ?", new String[]{descripcion + "%"});
        while (filas.moveToNext()) {
            Categoria cat = new Categoria();
            cat.setIdCategoria(filas.getInt(0));
            cat.setDescripcion(filas.getString(1));
            lista.add(cat);
        }
        filas.close();
        base.close();
        return lista;
    }

    public Categoria listaCategoriaxCodigo(int idCategoria) {
        Categoria cat = null;
        SQLiteDatabase base = admin.getReadableDatabase();
        Cursor filas = base.rawQuery("select IdCategoria, Descripcion from CATEGORIA where IdCategoria = ?", new String[]{String.valueOf(idCategoria)});
        if (filas.moveToFirst()) {
            cat = new Categoria();
            cat.setIdCategoria(filas.getInt(0));
            cat.setDescripcion(filas.getString(1));
        }
        filas.close();
        base.close();
        return cat;
    }

    public int adicionar(Categoria cat) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("Descripcion", cat.getDescripcion());
        salida = (int) base.insert("CATEGORIA", null, valores);
        base.close();
        return salida;
    }

    public int actualizar(Categoria cat) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("Descripcion", cat.getDescripcion());
        salida = base.update("CATEGORIA", valores, "IdCategoria = ?", new String[]{String.valueOf(cat.getIdCategoria())});
        base.close();
        return salida;
    }

    public int eliminar(int idCategoria) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        salida = base.delete("CATEGORIA", "IdCategoria = ?", new String[]{String.valueOf(idCategoria)});
        base.close();
        return salida;
    }
}
