package database;

public class MySQLDBinfo {
	private static final String HOSTNAME ="localhost";
	private static final String PORT_NUM ="8889";
	private static final String DB_NAME ="moviezone";
	private static final String USERNAME ="root";
	private static final String PASSWORD ="root";
	public static final String URL ="jdbc:mysql://"
			+ HOSTNAME + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD 
			+ "&autoReconnect=true&serverTimezone=UTC";

}
