package com.example.apptiaveneno.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apptiaveneno.Data.SqlOpenHelper;
import com.example.apptiaveneno.Entity.Producto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MySqlProductoDAO {
    SqlOpenHelper admin;

    public MySqlProductoDAO(Context context) {
        admin = new SqlOpenHelper(context);
    }

    public ArrayList<Producto> listaProducto() {
        ArrayList<Producto> lista = new ArrayList<>();
        SQLiteDatabase base = admin.getReadableDatabase();
        Cursor filas = base.rawQuery("select IdProducto, Codigo, IdCategoria, Descripcion, PrecioCompra, PrecioVenta, Stock, FechaRegistro, RutaImagen from PRODUCTO", null);
        while (filas.moveToNext()) {
            Producto prod = new Producto();
            prod.setIdProducto(filas.getInt(0));
            prod.setCodigo(filas.getString(1));
            prod.setIdCategoria(filas.getInt(2));
            prod.setDescripcion(filas.getString(3));
            prod.setPrecioCompra(filas.getDouble(4));
            prod.setPrecioVenta(filas.getDouble(5));
            prod.setStock(filas.getInt(6));
            prod.setFechaRegistro(parseFecha(filas.getString(7)));
            prod.setRutaImagen(filas.getString(8));
            lista.add(prod);
        }
        filas.close();
        base.close();
        return lista;
    }

    public ArrayList<Producto> listaProducto(String descripcion) {
        ArrayList<Producto> lista = new ArrayList<>();
        SQLiteDatabase base = admin.getReadableDatabase();
        Cursor filas = base.rawQuery("select IdProducto, Codigo, IdCategoria, Descripcion, PrecioCompra, PrecioVenta, Stock, FechaRegistro, RutaImagen from PRODUCTO where Descripcion like ?", new String[]{descripcion + "%"});
        while (filas.moveToNext()) {
            Producto prod = new Producto();
            prod.setIdProducto(filas.getInt(0));
            prod.setCodigo(filas.getString(1));
            prod.setIdCategoria(filas.getInt(2));
            prod.setDescripcion(filas.getString(3));
            prod.setPrecioCompra(filas.getDouble(4));
            prod.setPrecioVenta(filas.getDouble(5));
            prod.setStock(filas.getInt(6));
            prod.setFechaRegistro(parseFecha(filas.getString(7)));
            prod.setRutaImagen(filas.getString(8));
            lista.add(prod);
        }
        filas.close();
        base.close();
        return lista;
    }

    public int adicionarProducto(Producto prod) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("Codigo", prod.getCodigo());
        valores.put("IdCategoria", prod.getIdCategoria());
        valores.put("Descripcion", prod.getDescripcion());
        valores.put("PrecioCompra", String.valueOf(prod.getPrecioCompra()));
        valores.put("PrecioVenta", String.valueOf(prod.getPrecioVenta()));
        valores.put("Stock", prod.getStock());
        valores.put("FechaRegistro", formatFecha(prod.getFechaRegistro()));
        valores.put("RutaImagen", prod.getRutaImagen());
        salida = (int) base.insert("PRODUCTO", null, valores);
        base.close();
        return salida;
    }

    public int actualizarProducto(Producto prod) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("Codigo", prod.getCodigo());
        valores.put("IdCategoria", prod.getIdCategoria());
        valores.put("Descripcion", prod.getDescripcion());
        valores.put("PrecioCompra", String.valueOf(prod.getPrecioCompra()));
        valores.put("PrecioVenta", String.valueOf(prod.getPrecioVenta()));
        valores.put("Stock", prod.getStock());
        valores.put("FechaRegistro", formatFecha(prod.getFechaRegistro()));
        valores.put("RutaImagen", prod.getRutaImagen());
        salida = base.update("PRODUCTO", valores, "IdProducto = ?", new String[]{String.valueOf(prod.getIdProducto())});
        base.close();
        return salida;
    }

    public int eliminarProducto(int idProducto) {
        int salida = -1;
        SQLiteDatabase base = admin.getWritableDatabase();
        salida = base.delete("PRODUCTO", "IdProducto = ?", new String[]{String.valueOf(idProducto)});
        base.close();
        return salida;
    }

    // Método para parsear la fecha de String a Date
    private Date parseFecha(String fechaString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(fechaString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para formatear la fecha de Date a String
    private String formatFecha(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(fecha);
    }
}
