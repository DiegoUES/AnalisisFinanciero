package com.ues.edu.modelo;

public class UnidadComboDTO {
    private int idUnidad;
    private String nombreUnidad;
    private int idInstitucion;
    private String nombreInstitucion;

    
    public UnidadComboDTO() {}
    
    public UnidadComboDTO(int idUnidad, String nombreUnidad, int idInstitucion, String nombreInstitucion) {
        this.idUnidad = idUnidad;
        this.nombreUnidad = nombreUnidad;
        this.idInstitucion = idInstitucion;
        this.nombreInstitucion = nombreInstitucion;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public String getNombreUnidad() {
        return nombreUnidad;
    }

    public void setNombreUnidad(String nombreUnidad) {
        this.nombreUnidad = nombreUnidad;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

  
   
}