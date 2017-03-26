package info.mourya.goiot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Utility {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/goiot",
				"root", "password");
	}
}