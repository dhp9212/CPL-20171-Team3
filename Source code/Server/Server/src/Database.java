import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {

	
	 public static final String REQUEST_CURRENT_TEMP_HUM = "1";
	 public static final String REQUEST_ACCRUE_TEMP = "2";
	 public static final String REQUEST_STATE = "3";
	 public static final String REQUEST_SIGNUP = "4";
	 public static final String REQUEST_LOGIN = "5";
	 public static final String COLSE = "6";
	 
	 public static final String APP_LIGHT_ON = "10";
	 public static final String APP_LIGHT_OF = "11";
	 public static final String APP_LIGHT_AU = "12";
	
	 public static final String APP_HITTE_ON = "20";
	 public static final String APP_HITTE_OF = "21";
	 public static final String APP_HITTE_AU = "22";
	
	 public static final String APP_HUMID_ON = "30";
	 public static final String APP_HUMID_OF = "31";
	 public static final String APP_HUMID_AU = "32";
	
	 public static final String APP_MOTOR_LE = "40";
	 public static final String APP_MOTOR_RI = "41";
	 public static final String APP_MOTOR_OF = "42";

	private String url = "jdbc:mysql://dhdb.cvqwpznjcq93.us-west-2.rds.amazonaws.com:3306/";
	private String userName = "DH";
	private String password = "dh123";
	private String dbName = "DHDB";
	String driver = "com.mysql.jdbc.Driver";
	
	Connection con;
	Statement state;
	String query = "";
	String query_update = "";
	CallableStatement procedure;
	
	public Database(){
		try{
			con = DriverManager.getConnection(url + dbName, userName, password);
			state = con.createStatement();
			procedure = con.prepareCall("CALL mysql.store_time_zone");
			procedure.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

	public String select(String flag, String payload){
		
		ResultSet result = null;
		String ret = "";
		String[] splt = payload.split("/");
		query = "";
		
		if(flag.equals(REQUEST_LOGIN)){
			query = "";
			query += "SELECT ID, PWD FROM ACCOUNT WHERE ";
			query += "ID = '" + splt[0] + "' AND ";
			query += "PWD = '" + splt[1] + "';";

			try {
				
				System.out.println(query);
				result = state.executeQuery(query);
				
				if(!result.isBeforeFirst()){
					ret = "F_login";
				}
				else{
					ret = "S";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				ret = "F";
				e.printStackTrace();
			}
		}
		else if(flag.equals(REQUEST_CURRENT_TEMP_HUM)){
			query = "";
			query += "(SELECT SENSOR_TYPE, SENSOR_VALUE FROM SENSOR_DATA WHERE SENSOR_TYPE = 'TEMP_ONE' ORDER BY RCV_DATE LIMIT 1)";
			query += " UNION ";
			query += "(SELECT SENSOR_TYPE, SENSOR_VALUE FROM SENSOR_DATA WHERE SENSOR_TYPE = 'TEMP_TWO' ORDER BY RCV_DATE DESC LIMIT 1)";
			query += " UNION ";
			query += "(SELECT SENSOR_TYPE, SENSOR_VALUE FROM SENSOR_DATA WHERE SENSOR_TYPE = 'HUMI_ONE' ORDER BY RCV_DATE DESC LIMIT 1)";
			query += " UNION ";
			query += "(SELECT 'AVRAGE', ROUND(AVG(SENSOR_VALUE), 1) AS SENSOR_VALUE FROM SENSOR_DATA WHERE SENSOR_TYPE = 'HUMI_ONE' GROUP BY SENSOR_TYPE);";
			
			try {
				System.out.println(query);
				result = state.executeQuery(query);
				
				ret = "";
				
				while(result.next()){
					ret += result.getFloat("SENSOR_VALUE");
					if(!result.isLast()){
						ret += "/";
					}
				}
			}catch(SQLException e){
				ret = "F";
				e.printStackTrace();
			}
		}
		else if(flag.equals(REQUEST_ACCRUE_TEMP)){
			query = "";
			
			
			for(int i = 0; i < 8; i++){
			
				query += "(SELECT EXTRACT(HOUR FROM RCV_DATE) AS RCV_DATE, AVG(SENSOR_VALUE) AS SENSOR_VALUE ";
				query += "FROM SENSOR_DATA ";
				query += "WHERE SENSOR_TYPE = 'TEMP_ONE' AND ";
				query += "EXTRACT(YEAR FROM RCV_DATE) = EXTRACT(YEAR FROM NOW()) AND ";
				query += "EXTRACT(MONTH FROM RCV_DATE) = EXTRACT(MONTH FROM NOW()) AND ";
				query += "EXTRACT(DAY FROM RCV_DATE) = EXTRACT(DAY FROM NOW()) AND ";
				query += "EXTRACT(HOUR FROM RCV_DATE) = EXTRACT(HOUR FROM NOW()) - " + (3*i) + " ";
				query += "GROUP BY EXTRACT(HOUR FROM RCV_DATE))";
				
				if(i + 1 != 8)
					query += " UNION ";
			}
			
			query += " UNION ";
			
			for(int i = 0; i < 8; i++){
				
				query += "(SELECT EXTRACT(HOUR FROM RCV_DATE) AS RCV_DATE, AVG(SENSOR_VALUE) AS SENSOR_VALUE ";
				query += "FROM SENSOR_DATA ";
				query += "WHERE SENSOR_TYPE = 'TEMP_TWO' AND ";
				query += "EXTRACT(YEAR FROM RCV_DATE) = EXTRACT(YEAR FROM NOW()) AND ";
				query += "EXTRACT(MONTH FROM RCV_DATE) = EXTRACT(MONTH FROM NOW()) AND ";
				query += "EXTRACT(DAY FROM RCV_DATE) = EXTRACT(DAY FROM NOW()) AND ";
				query += "EXTRACT(HOUR FROM RCV_DATE) = EXTRACT(HOUR FROM NOW()) - " + (3*i) + " ";
				query += "GROUP BY EXTRACT(HOUR FROM RCV_DATE))";
				
				if(i + 1 != 8)
					query += " UNION ";
			}
			
			
			try {
				System.out.println(query);
				result = state.executeQuery(query);
				
				ret = "";
				
				while(result.next()){
					ret += result.getFloat("SENSOR_VALUE");
					if(!result.isLast()){
						ret += "/";
					}
				}
			}catch(SQLException e){
				ret = "F";
				e.printStackTrace();
			}
		}
		else if(flag.equals(REQUEST_STATE)){
			query = "";
			query += "SELECT LIGHT, LIGHT_AUTO, HITTE, HITTE_AUTO, HUMID, HUMID_AUTO, MOTOR, MOTOR_ON ";
			query += "FROM CURRENT_STATE";
			
			try {
				System.out.println(query);
				result = state.executeQuery(query);
				
				result.next();
				
				ret = "";
				ret += result.getString("LIGHT") + "/";
				ret += result.getString("LIGHT_AUTO") + "/";
				ret += result.getString("HITTE") + "/";
				ret += result.getString("HITTE_AUTO") + "/";
				ret += result.getString("HUMID") + "/";
				ret += result.getString("HUMID_AUTO") + "/";
				ret += result.getString("MOTOR") + "/";
				ret += result.getString("MOTOR_ON");
				
			}catch(SQLException e){
				ret = "F";
				e.printStackTrace();
			}
		}

		return ret;
	}
	
	public String rawInsert(String payload){
		String ret = "";
		String[] splt = payload.split("/");
		
		for(int i = 0; i < splt.length; i++){

			query = "INSERT INTO SENSOR_DATA VALUES(";
			query += "'" + splt[i].substring(4, 12) + "',";
			query += Float.parseFloat(splt[i].substring(13, 17));
			query += ", now());";
			
			try{
				state.executeUpdate(query);
				ret = "S";
			}catch(Exception e){
				e.printStackTrace();
				ret = "F";
				break;
			}
		}
		return ret;
	}
	

	public String insert(String flag, String payload){
		
		String ret = "";
		String[] splt = payload.split("/");
		
		if(flag.equals(REQUEST_STATE)){
			ret = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";		
		}
		else if(flag.equals(REQUEST_SIGNUP)){
			ret = "";
			query = "INSERT INTO ACCOUNT VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()"; // 현재 서버가 북미에 있기 때문에 북미 시간으로 들어간다. 프로시저 콜으로 될듯
			query += ");";			 	 
		}
		
		try{
			System.out.println(query);
			state.executeUpdate(query);
			ret = "S";
		}catch(Exception e){
			e.printStackTrace();
			ret = "F";
		}
		
		return ret;
	}
	
	public String insertControl(String flag, String payload){
		String ret = "";
		String[] splt = payload.split("/");
		
		/**********************************/
		if(flag.equals(APP_LIGHT_ON)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET LIGHT = TRUE";
			
		}
		else if(flag.equals(APP_LIGHT_OF)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET LIGHT = FALSE";
			
		}
		else if(flag.equals(APP_LIGHT_AU)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET LIGHT_AUTO = !LIGHT_AUTO";
		}
		/**********************************/
		else if(flag.equals(APP_HITTE_ON)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HITTE = TRUE";
		}
		else if(flag.equals(APP_HITTE_OF)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HITTE = FALSE";
		}
		else if(flag.equals(APP_HITTE_AU)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HITTE_AUTO = !HITTE_AUTO";
		}
		/**********************************/
		else if(flag.equals(APP_HUMID_ON)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HUMID = TRUE";
		}
		else if(flag.equals(APP_HUMID_OF)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HUMID = FALSE";
		}
		else if(flag.equals(APP_HUMID_AU)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET HUMID_AUTO = !HUMID_AUTO";
		}
		/**********************************/
		else if(flag.equals(APP_MOTOR_LE)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET MOTOR = TRUE";
		}
		else if(flag.equals(APP_MOTOR_RI)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET MOTOR = FALSE";
		}
		else if(flag.equals(APP_MOTOR_OF)){
			query = "";
			query = "INSERT INTO CONTROL_LOG VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()";
			query += ");";
			
			query_update = "";
			query_update += "UPDATE CURRENT_STATE SET MOTOR_ON = !MOTOR_ON";
		}
		/**********************************/
		
		

		
		try{
			System.out.println(query);
			state.executeUpdate(query);
			state.executeUpdate(query_update);
			ret = "S";
		}catch(Exception e){
			e.printStackTrace();
			ret = "F";
		}
		
		return ret;
	}
	
	public static Connection getConnection(String url, String id, String pwd){

		Connection con = null;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			con = DriverManager.getConnection(url, id, pwd);
			System.out.println("Connection success");
		}catch(ClassNotFoundException e){
			System.err.println("Not found Driver");
		}catch(SQLException e){
			System.out.println("Connection failed");
			e.printStackTrace();
		}
		
		return con;
	}
	

}
