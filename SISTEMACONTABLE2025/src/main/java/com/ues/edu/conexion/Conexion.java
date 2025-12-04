/*
     * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
     * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private Connection conexion = null;
    private static final ResultSet rs = null;
    private static Statement sentencia = null;
    private static final PreparedStatement ps = null;

    //Cambie el puerto de 5432 a 5434 porque uso postgre 16
            private static final String jdbcURL = "jdbc:postgresql://localhost:5434/SISTEMAFINANCIERO2025?useSSL=false";
            private static final String jdbcUsername = "postgres";
            private static final String jdbcPassword = "root"; //CAMBIAR

    public Connection getConexion() {
        Connection con = null;
        try {
            // Class.forName("com.mysql.jdbc.Driver");para mysql
            Class.forName("org.postgresql.Driver");
            // Obtener la conexion
            con = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("conectando a la DB");
            System.out.println("CONEXION EXITOSA");
        } catch (SQLException ex) {
        } catch (Exception e) {
        }
        return con;

    }

    public void cerrarConexiones() {
        if (sentencia != null) {
            try {
                sentencia.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar el Statement" + e);
            }
        }
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexion a la bd" + e);
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexion a la bd" + e);
            }
        }

    }

    public PreparedStatement prepareStatement(String select__from_Empleados_WHERE_DUI__) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
