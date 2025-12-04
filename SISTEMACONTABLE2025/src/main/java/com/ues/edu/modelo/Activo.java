/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo;
import java.util.Date;

/**
 *
 * @author Marlo
 */
public class Activo {
    private int id;
    private String nombre;

    private Unidad unidad;
    private TipoCategoria tipoCategoria;
    private TipoUsado tipoUsado;

    private String codigo;
    private int correlativo;
    private String caracteristicas;
    private Date fechaCompra;
    private int vidaUtil;
    private Boolean estadoActivo;
    private String descripcionEstado;    // donado, vendido, votado
    private double precioAdquisicion;
    private Double precioUsado;        // puede ser null
    private String estadoDeCompra;     // nuevo/usado
    
     private int aniosUso;//depreciacionaños

    public Activo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(Unidad unidad) {
        this.unidad = unidad;
    }

    public TipoCategoria getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(TipoCategoria tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public TipoUsado getTipoUsado() {
        return tipoUsado;
    }

    public void setTipoUsado(TipoUsado tipoUsado) {
        this.tipoUsado = tipoUsado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(int correlativo) {
        this.correlativo = correlativo;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public int getVidaUtil() {
        return vidaUtil;
    }

    public void setVidaUtil(int vidaUtil) {
        this.vidaUtil = vidaUtil;
    }

    public Boolean getEstadoActivo() {
        return estadoActivo;
    }

    public void setEstadoActivo(Boolean estadoActivo) {
        this.estadoActivo = estadoActivo;
    }

    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    public void setDescripcionEstado(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }

   

    public double getPrecioAdquisicion() {
        return precioAdquisicion;
    }

    public void setPrecioAdquisicion(double precioAdquisicion) {
        this.precioAdquisicion = precioAdquisicion;
    }

    public Double getPrecioUsado() {
        return precioUsado;
    }

    public void setPrecioUsado(Double precioUsado) {
        this.precioUsado = precioUsado;
    }

    public String getEstadoDeCompra() {
        return estadoDeCompra;
    }

    public void setEstadoDeCompra(String estadoDeCompra) {
        this.estadoDeCompra = estadoDeCompra;
    }
    
    // *** depreciacion ***
    public int getAniosUso() { return aniosUso; }
    public void setAniosUso(int aniosUso) { this.aniosUso = aniosUso; }
}

