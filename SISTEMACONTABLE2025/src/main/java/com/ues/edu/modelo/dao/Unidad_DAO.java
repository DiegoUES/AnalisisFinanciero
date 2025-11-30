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
    private ArrayList<Institucion> listaInstituciones;
    private Unidad unidad = null;

    // SQL
    private static final String SQL_MOSTRAR = "SELECT u.idunidad, u.nombre, i.nombre AS institucion_nombre \n"
            + "FROM unidad u \n"
            + "INNER JOIN institucion i ON i.id = u.idinstitucion";

    private static final String SQL_INSERTAR = "INSERT INTO unidad (nombre, idinstitucion) VALUES (?, ?)";

    private static final String SQL_MODIFICAR = "UPDATE unidad SET idinstitucion = ? WHERE idunidad = ?";

    private static final String SQL_BY_ID = "SELECT u.idunidad, u.idinstitucion, i.nombre AS institucion_nombre \n"
            + "FROM unidad u \n"
            + "INNER JOIN institucion i ON i.id = u.idinstitucion \n"
            + "WHERE u.idunidad = ?";

    private static final String SQL_CARGARCOMBO = "SELECT ins.id,ins.nombre FROM institucion ins";

    public Unidad_DAO() {
        this.conexion = new Conexion();
    }

    public ArrayList<Unidad> mostrar() throws SQLException {
        this.listaUnidades = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                unidad = new Unidad();

                unidad.setId(rs.getInt("idunidad"));
                unidad.setNombre(rs.getString("nombre"));

                Institucion inst = new Institucion();
                inst.setNombre(rs.getString("institucion_nombre"));

                unidad.setInstitucion(inst);

                listaUnidades.add(unidad);
            }
        }
        return this.listaUnidades;
    }

    public ArrayList<Institucion> CargarComboInstitucion() throws SQLException, ClassNotFoundException {
        this.listaInstituciones = new ArrayList<>();

        try (Connection connection = conexion.getConexion(); PreparedStatement preparedStatement = connection.prepareStatement(SQL_CARGARCOMBO); ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Institucion institucion = new Institucion();
                institucion.setId(rs.getInt("id"));
                institucion.setNombre(rs.getString("nombre"));

                this.listaInstituciones.add(institucion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.listaInstituciones;
    }

    public String insertar(Unidad unidad) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {

            ps.setString(1, unidad.getNombre());
            ps.setObject(2, unidad.getInstitucion().getId());

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

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

            ps.setObject(1, unidad.getInstitucion().getId());
            ps.setInt(2, unidad.getId());

            int resultado_actualizar = ps.executeUpdate();
            resultado = (resultado_actualizar > 0) ? "exito" : "error_modificar_unidad";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }

    public Unidad cargarDatos(int idUnidad) throws SQLException {
        Unidad unidad = null;

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_BY_ID)) {

            ps.setInt(1, idUnidad);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    unidad = new Unidad();
                    unidad.setId(rs.getInt("idunidad"));

                    Institucion inst = new Institucion();
                    inst.setId(rs.getInt("idinstitucion"));
                    inst.setNombre(rs.getString("institucion_nombre"));

                    unidad.setInstitucion(inst);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        this.conexion.cerrarConexiones();
        return unidad;
    }

}
