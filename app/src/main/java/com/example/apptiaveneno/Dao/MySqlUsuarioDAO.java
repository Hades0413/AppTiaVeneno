package com.example.apptiaveneno.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.apptiaveneno.Data.SqlOpenHelper;
import com.example.apptiaveneno.Entity.Usuario;

public class MySqlUsuarioDAO {

    SqlOpenHelper admin;

    public MySqlUsuarioDAO(Context context){
        admin = new SqlOpenHelper(context);
    }

    public int adicionarUsuario(Usuario bean) {
        int salida = -1;
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues registros = new ContentValues();
        registros.put("nombreCompleto", bean.getNombreCompleto());
        registros.put("correo", bean.getCorreo());
        registros.put("clave", bean.getClave());

        salida = (int) bd.insert("tb_usuario", null, registros);
        bd.close();

        return salida;
    }

    public Cursor consultarUsuarioPassword(String correo, String clave) throws SQLException {
        SQLiteDatabase db = admin.getReadableDatabase();

        String[] columns = {"idUsuario", "nombreCompleto", "correo", "clave"};
        String selection = "correo = ? AND clave = ?";
        String[] selectionArgs = {correo, clave};

        Cursor mcursor = db.query("tb_usuario", columns, selection, selectionArgs, null, null, null);
        return mcursor;
    }
}
