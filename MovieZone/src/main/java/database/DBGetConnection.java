package database;

public class DBGetConnection {
	
	public static DBConnection getConnection() {
		return new MySQLConnection();
	}
	
}
