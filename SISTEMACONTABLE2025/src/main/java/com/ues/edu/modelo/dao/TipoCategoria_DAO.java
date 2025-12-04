/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.TipoCategoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author herna
 */
public class TipoCategoria_DAO {

    private final Conexion conexion;
    private ArrayList<TipoCategoria> listaTipoCategorias;
    private TipoCategoria tipoCategoria = null;

    private static final String SQL_MOSTRAR = "SELECT idTipo, nombre, porcentaje FROM tipocategoria ORDER BY idTipo";
    private static final String SQL_INSERTAR = "INSERT INTO tipocategoria (idTipo, nombre, porcentaje) VALUES (?, ?, ?)";
    private static final String SQL_MODIFICAR = "UPDATE tipocategoria SET nombre = ?, porcentaje = ? WHERE idTipo = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM tipocategoria WHERE idTipo = ?";
    private static final String SQL_BUSCAR_POR_ID = "SELECT idTipo, nombre, porcentaje FROM tipocategoria WHERE idTipo = ?";
    private static final String SQL_VERIFICAR_ID = "SELECT COUNT(*) FROM tipocategoria WHERE idTipo = ?";
    private static final String SQL_MAX_ID = "SELECT MAX(idTipo) FROM tipocategoria";

    public TipoCategoria_DAO() {
        this.conexion = new Conexion();
    }

    public ArrayList<TipoCategoria> mostrar() throws SQLException {
        this.listaTipoCategorias = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tipoCategoria = new TipoCategoria();
                tipoCategoria.setIdTipo(rs.getInt("idTipo"));
                tipoCategoria.setNombre(rs.getString("nombre"));
                tipoCategoria.setPorcentaje(rs.getDouble("porcentaje"));
                listaTipoCategorias.add(tipoCategoria);
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar tipos de categoría: " + e.getMessage());
            throw e;
        }
        return this.listaTipoCategorias;
    }

    public String insertar(TipoCategoria tipo) throws SQLException {
        String resultado;

       
        if (tipo.getPorcentaje() < 0 || tipo.getPorcentaje() > 100) {
            return "error_porcentaje_invalido";
        }

        if (tipo.getIdTipo() < 1 || tipo.getIdTipo() > 9999) {
            return "error_idtipo_invalido";
        }

        if (existeIdTipo(tipo.getIdTipo())) {
            return "error_idtipo_duplicado";
        }

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {

            ps.setInt(1, tipo.getIdTipo());
            ps.setString(2, tipo.getNombre());
            ps.setDouble(3, tipo.getPorcentaje());

            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_tipo";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            System.err.println("Error al insertar tipo de categoría: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    public String modificar(TipoCategoria tipo) throws SQLException {
        String resultado;

        // Validar porcentaje
        if (tipo.getPorcentaje() < 0 || tipo.getPorcentaje() > 100) {
            return "error_porcentaje_invalido";
        }

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

            ps.setString(1, tipo.getNombre());
            ps.setDouble(2, tipo.getPorcentaje());
            ps.setInt(3, tipo.getIdTipo());

            int resultado_actualizar = ps.executeUpdate();
            resultado = (resultado_actualizar > 0) ? "exito" : "error_modificar_tipo";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            System.err.println("Error al modificar tipo de categoría: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    public String eliminar(int id) throws SQLException {
        String resultado;

        if (estaRelacionadoConActivo(id)) {
            return "error_relacionado_con_activo";
        }

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);

            int resultado_eliminar = ps.executeUpdate();
            resultado = (resultado_eliminar > 0) ? "exito" : "error_eliminar_tipo";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            System.err.println("Error al eliminar tipo de categoría: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    public TipoCategoria buscarPorId(int id) throws SQLException {
        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipoCategoria = new TipoCategoria();
                tipoCategoria.setIdTipo(rs.getInt("idTipo"));
                tipoCategoria.setNombre(rs.getString("nombre"));
                tipoCategoria.setPorcentaje(rs.getDouble("porcentaje"));
                return tipoCategoria;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar tipo de categoría por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Verificar si ya existe un idTipo
    public boolean existeIdTipo(int idTipo) throws SQLException {
        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_VERIFICAR_ID)) {

            ps.setInt(1, idTipo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Obtener el siguiente idTipo disponible
    public int obtenerSiguienteId() throws SQLException {
        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MAX_ID)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return (maxId > 0) ? maxId + 1 : 1;
            }
        }
        return 1;
    }

    // Verificar si ya existe un nombre
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tipocategoria WHERE nombre = ?";

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Verificar si ya existe un nombre excluyendo un idTipo
    public boolean existeNombreExcluyendo(String nombre, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tipocategoria WHERE nombre = ? AND idTipo != ?";

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean estaRelacionadoConActivo(int idTipo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM activo WHERE idTipo = ?";

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar relación con activo: " + e.getMessage());
            throw e;
        }
        return false;
    }
}
