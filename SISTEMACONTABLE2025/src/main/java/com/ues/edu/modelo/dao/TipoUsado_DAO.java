/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.TipoUsado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author herna
 */
public class TipoUsado_DAO {

    private final Conexion conexion;
    private ArrayList<TipoUsado> listaTipoUsados;
    private TipoUsado tipoUsado = null;

    // SQL - Para tipousado (id auto increment, no se incluye en INSERT)
    private static final String SQL_MOSTRAR = "SELECT idtipousado, anyos, porcentaje FROM tipousado ORDER BY anyos";
    private static final String SQL_INSERTAR = "INSERT INTO tipousado (anyos, porcentaje) VALUES (?, ?)";
    private static final String SQL_MODIFICAR = "UPDATE tipousado SET anyos = ?, porcentaje = ? WHERE idtipousado = ?";
    private static final String SQL_ELIMINAR = "DELETE FROM tipousado WHERE idtipousado = ?";
    private static final String SQL_BUSCAR_POR_ID = "SELECT idtipousado, anyos, porcentaje FROM tipousado WHERE idtipousado = ?";
    private static final String SQL_VERIFICAR_ANYOS = "SELECT COUNT(*) FROM tipousado WHERE anyos = ?";
    private static final String SQL_VERIFICAR_ANYOS_EXCLUYENDO = "SELECT COUNT(*) FROM tipousado WHERE anyos = ? AND idtipousado != ?";

    public TipoUsado_DAO() {
        this.conexion = new Conexion();
    }

    public ArrayList<TipoUsado> mostrar() throws SQLException {
        this.listaTipoUsados = new ArrayList<>();

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tipoUsado = new TipoUsado();
                tipoUsado.setIdTipoUsado(rs.getInt("idtipousado"));
                tipoUsado.setAnyos(rs.getInt("anyos"));
                tipoUsado.setPorcentaje(rs.getInt("porcentaje"));
                listaTipoUsados.add(tipoUsado);
            }
        } catch (SQLException e) {
            System.err.println("Error al mostrar tipos de usado: " + e.getMessage());
            throw e;
        }
        return this.listaTipoUsados;
    }

    public String insertar(TipoUsado tipo) throws SQLException {
        String resultado;

        // Validar porcentaje (20-80)
        if (tipo.getPorcentaje() < 20 || tipo.getPorcentaje() > 80) {
            return "error_porcentaje_invalido";
        }

        // Validar años (positivo)
        if (tipo.getAnyos() <= 0) {
            return "error_anyos_invalido";
        }

        // Verificar si los años ya existen (debe ser único)
        if (existeAnyos(tipo.getAnyos())) {
            return "error_anyos_duplicado";
        }

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR)) {

            ps.setInt(1, tipo.getAnyos());
            ps.setInt(2, tipo.getPorcentaje());

            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_tipo_usado";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            System.err.println("Error al insertar tipo usado: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    public String modificar(TipoUsado tipo) throws SQLException {
        String resultado;

        // Validar porcentaje (20-80)
        if (tipo.getPorcentaje() < 20 || tipo.getPorcentaje() > 80) {
            return "error_porcentaje_invalido";
        }

        // Validar años (positivo)
        if (tipo.getAnyos() <= 0) {
            return "error_anyos_invalido";
        }

        // Verificar si los años ya existen (excluyendo el actual)
        if (existeAnyosExcluyendo(tipo.getAnyos(), tipo.getIdTipoUsado())) {
            return "error_anyos_duplicado";
        }

        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

            ps.setInt(1, tipo.getAnyos());
            ps.setInt(2, tipo.getPorcentaje());
            ps.setInt(3, tipo.getIdTipoUsado());

            int resultado_actualizar = ps.executeUpdate();
            resultado = (resultado_actualizar > 0) ? "exito" : "error_modificar_tipo_usado";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            System.err.println("Error al modificar tipo usado: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

   // Y modifica el método eliminar:
public String eliminar(int id) throws SQLException {
    String resultado;

    // Verificar relaciones primero
    if (estaRelacionado(id)) {
        return "error_relacionado";
    }

    try (Connection cn = conexion.getConexion();
         PreparedStatement ps = cn.prepareStatement(SQL_ELIMINAR)) {

        ps.setInt(1, id);
        int resultado_eliminar = ps.executeUpdate();
        resultado = (resultado_eliminar > 0) ? "exito" : "error_eliminar_tipo_usado";

    } catch (SQLException e) {
        resultado = "error_excepcion";
        System.err.println("Error al eliminar tipo usado: " + e.getMessage());
        e.printStackTrace();
    }

    return resultado;
}

    public TipoUsado buscarPorId(int id) throws SQLException {
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipoUsado = new TipoUsado();
                tipoUsado.setIdTipoUsado(rs.getInt("idtipousado"));
                tipoUsado.setAnyos(rs.getInt("anyos"));
                tipoUsado.setPorcentaje(rs.getInt("porcentaje"));
                return tipoUsado;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar tipo usado por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    // Verificar si ya existen los años (campo único)
    public boolean existeAnyos(int anyos) throws SQLException {
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_VERIFICAR_ANYOS)) {

            ps.setInt(1, anyos);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Verificar si ya existen los años excluyendo un id
    public boolean existeAnyosExcluyendo(int anyos, int idExcluir) throws SQLException {
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(SQL_VERIFICAR_ANYOS_EXCLUYENDO)) {

            ps.setInt(1, anyos);
            ps.setInt(2, idExcluir);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Verificar si existe un porcentaje (opcional, si necesitas)
    public boolean existePorcentaje(int porcentaje) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tipousado WHERE porcentaje = ?";
        
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, porcentaje);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Buscar por años (opcional)
    public TipoUsado buscarPorAnyos(int anyos) throws SQLException {
        String sql = "SELECT idtipousado, anyos, porcentaje FROM tipousado WHERE anyos = ?";
        
        try (Connection cn = conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, anyos);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tipoUsado = new TipoUsado();
                tipoUsado.setIdTipoUsado(rs.getInt("idtipousado"));
                tipoUsado.setAnyos(rs.getInt("anyos"));
                tipoUsado.setPorcentaje(rs.getInt("porcentaje"));
                return tipoUsado;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar tipo usado por años: " + e.getMessage());
            throw e;
        }
        return null;
    }
    // Verificar si el tipo usado está siendo usado en alguna tabla
public boolean estaRelacionado(int idTipoUsado) throws SQLException {
    // Si tipousado también es llave foránea en activo u otra tabla:
    String sql = "SELECT COUNT(*) FROM activo WHERE idTipoUsado = ?";
    // O la tabla donde sea llave foránea
    
    try (Connection cn = conexion.getConexion();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, idTipoUsado);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    }
    return false;
}

}