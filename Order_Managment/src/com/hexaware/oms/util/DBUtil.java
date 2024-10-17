package com.hexaware.oms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	    public static Connection getDBConnection() {

	        Connection conn = null;

	        try {
	            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());


	            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/oms", "root", "Siddhu@123");
	        }
	        catch (SQLException e) {
	        	System.err.println("Error connecting to database: " + e.getMessage());
	  	  	}
	        return conn;
	    }

}