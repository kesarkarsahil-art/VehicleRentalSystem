package com.vehiclerental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {
            String url = "jdbc:mysql://localhost:3306/vehicle_rental";
            String user = "root";
            String password = "password";  

            con = DriverManager.getConnection(url, user, password);

            System.out.println(" ");

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        return con;
    }
}