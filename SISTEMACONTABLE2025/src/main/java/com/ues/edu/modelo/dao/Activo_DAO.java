package com.ues.edu.modelo.dao;

import com.ues.edu.conexion.Conexion;
import com.ues.edu.modelo.Activo;
import com.ues.edu.modelo.TipoCategoria;
import com.ues.edu.modelo.TipoUsado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Activo_DAO {

    Conexion cn = new Conexion();

    // ============= OBTENER UN ACTIVO POR ID (para calcular la depreciación) =============
    public Activo obtenerActivo(int id) {

        Activo a = null;

        String sql =
            "SELECT a.*, " +
            "       EXTRACT(YEAR FROM age(current_date, a.fechacompra)) AS anios_uso, " +
            "       tc.idtipo      AS tc_idtipo, " +
            "       tc.nombre      AS tc_nombre, " +
            "       tc.porcentaje  AS tc_porcentaje, " +
            "       tu.idtipousado AS tu_idtipousado, " +
            "       tu.anyos       AS tu_anyos, " +
            "       tu.porcentaje  AS tu_porcentaje " +
            "FROM activo a " +
            "LEFT JOIN tipocategoria tc ON a.idtipo = tc.idtipo " +
            "LEFT JOIN tipousado    tu ON a.idtipousado = tu.idtipousado " +
            "WHERE a.id = ?";

        try (Connection con = cn.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    a = new Activo();
                    a.setId(rs.getInt("id"));
                    a.setNombre(rs.getString("nombre"));
                    a.setCodigo(rs.getString("codigo"));                 // nuevo: código
                    a.setEstadoDelActivo(rs.getString("estadodelactivo"));// nuevo: estado del activo

                    a.setPrecioAdquisicion(rs.getDouble("precioadquisicion"));
                    a.setFechaCompra(rs.getDate("fechacompra"));
                    a.setEstadoDeCompra(rs.getString("estadodecompra"));
                    a.setAniosUso(rs.getInt("anios_uso"));

                    // -------- TipoCategoria --------
                    if (rs.getObject("tc_idtipo") != null) {
                        TipoCategoria tc = new TipoCategoria();
                        // usa el setter que tengas en tu clase TipoCategoria
                        tc.setIdTipo(rs.getInt("tc_idtipo"));
                        tc.setNombre(rs.getString("tc_nombre"));
                        tc.setPorcentaje(rs.getDouble("tc_porcentaje")); // 5,20,25,50
                        a.setTipoCategoria(tc);
                    }

                    // -------- TipoUsado --------
                    if (rs.getObject("tu_idtipousado") != null) {
                        TipoUsado tu = new TipoUsado();
                        tu.setIdTipoUsado(rs.getInt("tu_idtipousado"));
                        tu.setAnyos(rs.getInt("tu_anyos"));
                        tu.setPorcentaje(rs.getInt("tu_porcentaje")); // 80,60,40,20
                        a.setTipoUsado(tu);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }

    // ============= LISTAR TODOS LOS ACTIVOS PARA EL COMBO =============
    public List<Activo> listarActivos() {
        List<Activo> lista = new ArrayList<>();

        String sql = "SELECT id, nombre, codigo FROM activo ORDER BY nombre";

        try (Connection con = cn.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Activo a = new Activo();
                a.setId(rs.getInt("id"));
                a.setNombre(rs.getString("nombre"));
                a.setCodigo(rs.getString("codigo"));
                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}