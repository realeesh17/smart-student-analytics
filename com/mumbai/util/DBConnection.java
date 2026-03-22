package com.mumbai.util;

import java.sql.*;

public class DBConnection {
    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    // ⚠️ Change PASSWORD to your MySQL password
    private static final String URL      = "jdbc:mysql://localhost:3306/mumbai_uni_predictor";
    private static final String USER     = "root";
    private static final String PASSWORD = "";  // <-- add your password here

    static {
        try { Class.forName(DRIVER); }
        catch (ClassNotFoundException e) { System.err.println("Driver not found: " + e.getMessage()); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection c) {
        try { if (c != null) c.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
