package com.clochelabs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerDB {
    private static String user = "";
    private static String password = "";
    private static String hostname ="";

    /**
     * Initiate a connection with the DB
     * @return null if there is an SQL error else return the Connection object
     */
    public static Connection getCon() {
        try{
            return DriverManager.getConnection(hostname);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
