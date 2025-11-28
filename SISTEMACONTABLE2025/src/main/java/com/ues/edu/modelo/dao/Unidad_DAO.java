/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.Institucion;
import com.ues.edu.modelo.Unidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Marlo
 */
public class Unidad_DAO {

    private final Conexion conexion;
    private ArrayList<Unidad> listaUnidades;
    private Unidad unidad = null;

    // SQL
    private static final String SQL_MOSTRAR =
            "SELECT u.id, u.nombre, i.id AS idinstitucion, i.nombre AS institucion_nombre " +
            "FROM unidad u INNER JOIN institucion i ON i.id = u.idInstitucion";

    private static final String SQL_INSERTAR =
            "INSERT INTO unidad (nombre, idInstitucion) VALUES (?, ?)";

    private static final String SQL_MODIFICAR =
            "UPDATE unidad SET nombre = ?, idInstitucion = ? WHERE id = ?";

    public Unidad_DAO() {
        this.conexion = new Conexion();
    }

    public ArrayList<Unidad> mostrar() throws SQLException {
        this.listaUnidades = new ArrayList<>();

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                unidad = new Unidad();

                unidad.setId(rs.getInt("id"));
                unidad.setNombre(rs.getString("nombre"));

                Institucion inst = new Institucion();
                inst.setId(rs.getInt("idinstitucion"));
                inst.setNombre(rs.getString("institucion_nombre"));

                unidad.setInstitucion(inst);

                listaUnidades.add(unidad);
            }
        }
        return this.listaUnidades;
    }

    public String insertar(Unidad unidad) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, unidad.getNombre());
            ps.setInt(2, unidad.getInstitucion().getId());

            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_unidad";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }

    public String modificar(Unidad unidad) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

            ps.setString(1, unidad.getNombre());
            ps.setInt(2, unidad.getInstitucion().getId());
            ps.setInt(3, unidad.getId());

            int resultado_actualizar = ps.executeUpdate();
            resultado = (resultado_actualizar > 0) ? "exito" : "error_modificar_unidad";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }
}
