package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class MySQLClearTable {
	
	public static void main(String[] args) {
		try {
			Connection conn = null;

			try {
				Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
				conn = DriverManager.getConnection(MySQLDBinfo.URL);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			if(conn == null) {
				return;
			}
			
			//Clear tables 
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS genres";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS likes";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS movies";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS users";
			stmt.executeUpdate(sql);
			
			//Create new tables
			sql = "CREATE TABLE movies"
					+ "(movie_id INT(255) NOT NULL PRIMARY KEY,"
					+ " title VARCHAR(255),"
					+ " rating FLOAT,"
					+ " release_date VARCHAR(255),"
					+ " poster_url VARCHAR(255),"
					+ " overview VARCHAR(255))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE genres"
					+ "(movie_id INT(255) NOT NULL,"
					+ " genre VARCHAR(255) NOT NULL,"
					+ " PRIMARY KEY (movie_id, genre),"
					+ " FOREIGN KEY (movie_id) REFERENCES movies(movie_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users"
					+ "(user_id VARCHAR(255) NOT NULL,"
					+ " password VARCHAR(255) NOT NULL,"
					+ " PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE likes"
					+ "(user_id VARCHAR(255) NOT NULL,"
					+ " movie_id INT(255) NOT NULL,"
					+ " PRIMARY KEY (user_id, movie_id),"
					+ " FOREIGN KEY (movie_id) REFERENCES movies(movie_id),"
					+ " FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
			
		
		
		
	}

}
