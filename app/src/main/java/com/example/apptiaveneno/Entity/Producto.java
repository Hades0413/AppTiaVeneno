package com.example.apptiaveneno.Entity;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Producto implements Serializable {
    private int idProducto;
    private String codigo;
    private int idCategoria;
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private int stock;
    private Date fechaRegistro;
    private String rutaImagen;


    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    @NonNull
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", codigo='" + codigo + '\'' +
                ", idCategoria=" + idCategoria +
                ", descripcion='" + descripcion + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", fechaRegistro=" + fechaRegistro +
                ", rutaImagen='" + rutaImagen + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return idProducto == producto.idProducto;
    }

    @Override
    public int hashCode() {
        return idProducto;
    }

}
