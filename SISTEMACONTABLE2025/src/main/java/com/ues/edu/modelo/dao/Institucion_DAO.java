/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.Institucion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Marlo
 */

public class Institucion_DAO {

    private final Conexion conexion;
    private ArrayList<Institucion> listaInstituciones;
    private Institucion institucion = null;

    // SQL
    private static final String SQL_MOSTRAR = "SELECT id, nombre FROM institucion";
    private static final String SQL_INSERTAR = "INSERT INTO institucion (nombre) VALUES (?)";
    private static final String SQL_MODIFICAR = "UPDATE institucion SET nombre = ? WHERE id = ?";


    public Institucion_DAO() {
        this.conexion = new Conexion();
    }

    public ArrayList<Institucion> mostrar() throws SQLException {
        this.listaInstituciones = new ArrayList<>();

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                institucion = new Institucion();
                institucion.setId(rs.getInt("id"));
                institucion.setNombre(rs.getString("nombre"));
                listaInstituciones.add(institucion);
            }
        }
        return this.listaInstituciones;
    }


    public String insertar(Institucion inst) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, inst.getNombre());

            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_institucion";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }

    public String modificar(Institucion inst) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

            ps.setString(1, inst.getNombre());
            ps.setInt(2, inst.getId());

            int resultado_actualizar = ps.executeUpdate();
            resultado = (resultado_actualizar > 0) ? "exito" : "error_modificar_institucion";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }

}
