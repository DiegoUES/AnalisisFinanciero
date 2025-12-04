/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.Activo;
import com.ues.edu.modelo.TipoCategoria;
import com.ues.edu.modelo.TipoUsado;
import com.ues.edu.modelo.Unidad;
import com.ues.edu.modelo.UnidadComboDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Mayel
 */
public class ActivoDao {

    private final Conexion conexion;
    private ArrayList<Activo> listaActivosNuevos;
    private ArrayList<Activo> listaActivosUsados;
    private ArrayList<Activo> listaActivosDeBaja;
    private Activo activo = null;
    private Unidad unidad = null;
    private TipoCategoria tipo = null;
    private TipoUsado tipoUsado = null;
    private ArrayList<UnidadComboDTO> listaUnidadesCombo;
    private ArrayList<TipoCategoria> listaTipoCategoria;
    private ArrayList<TipoUsado> listaTipoUsado;

    private static final String SQL_INSERTAR_NUEVO = "INSERT INTO activo (nombre, idunidad, idtipo, codigo, correlativo, caracteristicas, fechacompra, vidautil, estadodelactivo, precioadquisicion, estadodecompra) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERTAR_USADO = "INSERT INTO activo (nombre, idunidad, idtipo, idtipousado, codigo, correlativo, caracteristicas, fechacompra, vidautil, estadodelactivo, precioadquisicion, preciousado, estadodecompra) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_OBTENER_UNIDADES_CON_INSTITUCION
            = "SELECT i.id AS idInsti, i.nombre AS nombreInsti, u.id AS idUni, u.nombre AS nombreUnidad "
            + "FROM unidad u "
            + "INNER JOIN institucion i ON u.idinstitucion = i.id "
            + "ORDER BY i.nombre, u.nombre";
    private static final String SQL_OBTENER_TIPOCATEGORIA = "SELECT\n"
            + "	tc.idtipo, \n"
            + "	tc.nombre\n"
            + "FROM\n"
            + "	tipocategoria tc";
    private static final String SQL_OBTENER_TIPOUSADO = "SELECT\n"
            + "	tu.idtipousado, \n"
            + "	tu.porcentaje, \n"
            + "	tu.anyos\n"
            + "FROM\n"
            + "	tipousado tu";
    private static final String SQL_MOSTRAR_NUEVOS = "SELECT \n"
                + "    a.id,\n"
            + "    a.nombre AS nombreActivo,\n"
            + "    u.nombre AS nombreUnidad,\n"
            + "    tc.nombre AS nombreTipo,\n"
            + "    a.codigo,\n"
            + "    a.caracteristicas,\n"
            + "    a.fechacompra,\n"
            + "    a.vidautil,\n"
            + "    a.estadodelactivo,\n"
            + "    a.precioadquisicion,\n"
            + "    a.estadodecompra\n"
            + "FROM activo a\n"
            + "JOIN tipocategoria tc ON tc.idtipo = a.idtipo\n"
            + "JOIN unidad u ON u.id = a.idunidad\n"
            + "WHERE a.estadodelactivo = true\n"
            + "  AND a.estadodecompra = 'Nuevo';";
    private static final String SQL_MOSTRAR_USADOS = "SELECT \n"
                + "    a.id,\n"
            + "    a.nombre AS nombreActivo,\n"
            + "    u.nombre AS nombreUnidad,\n"
            + "    tc.nombre AS nombreTipo,\n"
            + "    a.codigo,\n"
            + "    a.caracteristicas,\n"
            + "    a.fechacompra,\n"
            + "    a.vidautil,\n"
            + "    a.estadodelactivo,\n"
            + "    a.precioadquisicion,\n"
            + "    a.preciousado,\n"
            + "    a.estadodecompra\n"
            + "FROM activo a\n"
            + "JOIN tipocategoria tc ON tc.idtipo = a.idtipo\n"
            + "JOIN unidad u ON u.id = a.idunidad\n"
            + "WHERE a.estadodelactivo = true\n"
            + "  AND a.estadodecompra = 'Usado';";
    private static final String SQL_MOSTRAR_INACTIVOS = "SELECT \n"
            + "a.nombre AS nombreActivo,\n"
            + " u.nombre AS nombreUnidad,\n"
            + "a.codigo,\n"
            + "a.fechacompra,\n"
            + "a.estadodelactivo,\n"
            + "a.estadodecompra,\n"
            + "a.descripcionestado\n"
            + "FROM activo a\n"
            + "JOIN unidad u ON u.id = a.idunidad\n"
            + "WHERE a.estadodelactivo = false";

    private static final String SQL_MODIFICAR = "UPDATE activo\n"
            + "SET descripcionestado = ?, estadodelactivo = ?\n"
            + "WHERE id = ?;";

    public ActivoDao() {
        this.conexion = new Conexion();
    }

    //METODO PARA INSERTAR NUEVO
    public String insertarNuevo(Activo activo) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR_NUEVO)) {
            ps.setString(1, activo.getNombre());
            ps.setInt(2, activo.getUnidad().getId());
            ps.setInt(3, activo.getTipoCategoria().getIdTipo());
            ps.setString(4, activo.getCodigo());
            ps.setInt(5, activo.getCorrelativo());
            ps.setString(6, activo.getCaracteristicas());
            ps.setDate(7, new java.sql.Date(activo.getFechaCompra().getTime()));
            ps.setInt(8, activo.getVidaUtil());
            ps.setBoolean(9, activo.getEstadoActivo());
            ps.setDouble(10, activo.getPrecioAdquisicion());
            ps.setString(11, activo.getEstadoDeCompra());
            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_activo_nuevo";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }
    //METODO PARA INSERTAR USADO

    public String insertarUsado(Activo activo) throws SQLException {
        String resultado;

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR_USADO)) {
            ps.setString(1, activo.getNombre());
            ps.setInt(2, activo.getUnidad().getId());
            ps.setInt(3, activo.getTipoCategoria().getIdTipo());
            ps.setInt(4, activo.getTipoUsado().getIdTipoUsado());
            ps.setString(5, activo.getCodigo());
            ps.setInt(6, activo.getCorrelativo());
            ps.setString(7, activo.getCaracteristicas());
            ps.setDate(8, new java.sql.Date(activo.getFechaCompra().getTime()));
            ps.setInt(9, activo.getVidaUtil());
            ps.setBoolean(10, activo.getEstadoActivo());
            ps.setDouble(11, activo.getPrecioAdquisicion());
            ps.setDouble(12, activo.getPrecioUsado());
            ps.setString(13, activo.getEstadoDeCompra());

            int resultado_insertar = ps.executeUpdate();
            resultado = (resultado_insertar > 0) ? "exito" : "error_insertar_activo_usado";

        } catch (SQLException e) {
            resultado = "error_excepcion";
            e.printStackTrace();
        }

        return resultado;
    }

    //METODO PARA CARGAR EL COMBO DE UNIDAD
    public ArrayList<UnidadComboDTO> obtenerUnidadesConInstitucion() throws SQLException {
        this.listaUnidadesCombo = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_OBTENER_UNIDADES_CON_INSTITUCION); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UnidadComboDTO unidadCombo = new UnidadComboDTO();
                unidadCombo.setIdInstitucion(rs.getInt("idInsti"));
                unidadCombo.setNombreInstitucion(rs.getString("nombreInsti"));
                unidadCombo.setIdUnidad(rs.getInt("idUni"));
                unidadCombo.setNombreUnidad(rs.getString("nombreUnidad"));
                listaUnidadesCombo.add(unidadCombo);
            }
        }
        return this.listaUnidadesCombo;
    }

    //METODO PARA CARGAR EL COMBO TIPO CATEGORIA
    public ArrayList<TipoCategoria> obtenerTipoCategoria() throws SQLException {
        this.listaTipoCategoria = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_OBTENER_TIPOCATEGORIA); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoCategoria tc = new TipoCategoria();
                tc.setIdTipo(rs.getInt("idtipo"));
                tc.setNombre(rs.getString("nombre"));
                listaTipoCategoria.add(tc);
            }
        }
        return this.listaTipoCategoria;
    }

    //METODO PARA CARGAR EL COMBO TIPO USADO
    public ArrayList<TipoUsado> obtenerTipoUsado() throws SQLException {
        this.listaTipoUsado = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_OBTENER_TIPOUSADO); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoUsado tu = new TipoUsado();
                tu.setIdTipoUsado(rs.getInt("idtipousado"));
                tu.setAnyos(rs.getInt("anyos"));
                tu.setPorcentaje(rs.getInt("porcentaje"));
                listaTipoUsado.add(tu);
            }
        }
        return this.listaTipoUsado;
    }

    //METODO PARA MOSTRAR ACTIVOS NUEVOS
    public ArrayList<Activo> mostrarNuevos() throws SQLException {
        this.listaActivosNuevos = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR_NUEVOS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Activo obj = new Activo();
                Unidad uni = new Unidad();
                  obj.setId(rs.getInt("id")); 
                TipoCategoria tipo = new TipoCategoria();
                obj.setNombre(rs.getString("nombreActivo"));
                uni.setNombre(rs.getString("nombreUnidad"));
                obj.setUnidad(uni);
                tipo.setNombre(rs.getString("nombreTipo"));
                obj.setTipoCategoria(tipo);
                obj.setCodigo(rs.getString("codigo"));
                obj.setCaracteristicas(rs.getString("caracteristicas"));
                obj.setFechaCompra(rs.getDate("fechacompra"));
                obj.setVidaUtil(rs.getInt("vidautil"));
                obj.setEstadoActivo(rs.getBoolean("estadodelactivo"));
                obj.setPrecioAdquisicion(rs.getDouble("precioadquisicion"));
                obj.setEstadoDeCompra(rs.getString("estadodecompra"));
                listaActivosNuevos.add(obj);
            }
        }
        return this.listaActivosNuevos;
    }

    //METODO PARA MOSTRAR ACTIVOS USADOS
    public ArrayList<Activo> mostrarUsados() throws SQLException {
        this.listaActivosUsados = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR_USADOS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Activo obj = new Activo();
                Unidad uni = new Unidad();
                  obj.setId(rs.getInt("id")); 
                TipoCategoria tipo = new TipoCategoria();
                obj.setNombre(rs.getString("nombreActivo"));
                uni.setNombre(rs.getString("nombreUnidad"));
                obj.setUnidad(uni);
                tipo.setNombre(rs.getString("nombreTipo"));
                obj.setTipoCategoria(tipo);
                obj.setCodigo(rs.getString("codigo"));
                obj.setCaracteristicas(rs.getString("caracteristicas"));
                obj.setFechaCompra(rs.getDate("fechacompra"));
                obj.setVidaUtil(rs.getInt("vidautil"));
                obj.setEstadoActivo(rs.getBoolean("estadodelactivo"));
                obj.setPrecioAdquisicion(rs.getDouble("precioadquisicion"));
                obj.setPrecioUsado(rs.getDouble("preciousado"));
                obj.setEstadoDeCompra(rs.getString("estadodecompra"));
                listaActivosUsados.add(obj);
            }
        }
        return this.listaActivosUsados;
    }

    //METODO PARA OBTENER EL ULTIMO CORRELATIVO
    public int obtenerUltimoCorrelativo() throws SQLException {
        int ultimoCorrelativo = 0;
        String SQL_ULTIMO_CORRELATIVO = "SELECT COALESCE(MAX(correlativo), 0) AS ultimo_correlativo FROM activo";

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_ULTIMO_CORRELATIVO); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                ultimoCorrelativo = rs.getInt("ultimo_correlativo");
            }
        }
        return ultimoCorrelativo;
    }

    //METODO PARA OBTENER ACTIVOS DADOS DE BAJA
    public ArrayList<Activo> mostrarDeBaja() throws SQLException {
        this.listaActivosDeBaja = new ArrayList<>();

        try (Connection cn = conexion.getConexion(); PreparedStatement ps = cn.prepareStatement(SQL_MOSTRAR_INACTIVOS); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Activo obj = new Activo();
                Unidad uni = new Unidad();
                obj.setNombre(rs.getString("nombreActivo"));
                uni.setNombre(rs.getString("nombreUnidad"));
                obj.setUnidad(uni);
                obj.setCodigo(rs.getString("codigo"));
                obj.setFechaCompra(rs.getDate("fechacompra"));
                obj.setEstadoActivo(rs.getBoolean("estadodelactivo"));
                obj.setEstadoDeCompra(rs.getString("estadodecompra"));
                obj.setDescripcionEstado(rs.getString("descripcionestado"));
                listaActivosDeBaja.add(obj);
            }
        }
        return this.listaActivosDeBaja;
    }

    //METODO PARA EDITAR UN ACTIVO (DAR DE BAJA)
    public boolean actualizarEstadoActivo(int idActivo, String nuevaDescripcion, boolean nuevoEstado) throws SQLException {
    boolean actualizado = false;
   

    try (Connection cn = conexion.getConexion();
         PreparedStatement ps = cn.prepareStatement(SQL_MODIFICAR)) {

        ps.setString(1, nuevaDescripcion);
        ps.setBoolean(2, nuevoEstado);
        ps.setInt(3, idActivo);

        int filasAfectadas = ps.executeUpdate();
        actualizado = (filasAfectadas > 0);
    }
    return actualizado;
}
    
    //METODO PARA OBTENER ACTIVO POR ID
public Activo obtenerActivoPorId(int idActivo) throws SQLException {
    Activo activo = null;
    String SQL_OBTENER_POR_ID = "SELECT id, nombre, codigo, estadodelactivo FROM activo WHERE id = ?";

    try (Connection cn = conexion.getConexion();
         PreparedStatement ps = cn.prepareStatement(SQL_OBTENER_POR_ID)) {

        ps.setInt(1, idActivo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            activo = new Activo();
            activo.setId(rs.getInt("id"));
            activo.setNombre(rs.getString("nombre"));
            activo.setCodigo(rs.getString("codigo"));
            activo.setEstadoActivo(rs.getBoolean("estadodelactivo"));
        }
    }
    return activo;
}
}
