import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {

	
	 public static final String REQUEST_CURRENT_TEMP_HUM = "1";
	 public static final String REQUEST_ACCRUE_TEMP = "2";
	 public static final String REQUEST_CONTROL = "3";
	 public static final String REQUEST_SIGNUP = "4";
	 public static final String COLSE = "5";

	private String url = "jdbc:mysql://dhdb.cvqwpznjcq93.us-west-2.rds.amazonaws.com:3306/";
	private String userName = "DH";
	private String password = "dh123";
	private String dbName = "DHDB";
	String driver = "com.mysql.jdbc.Driver";
	
	Connection con;
	Statement state;
	String query = "";
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
	

	public ResultSet select(String flag, String payload){
		
		ResultSet result = null;
		String[] splt = payload.split("/");
		
		if(flag.equals(REQUEST_CURRENT_TEMP_HUM)){
			
		}
		else if(flag.equals(REQUEST_ACCRUE_TEMP)){
			
		}
		
		return result;
	}
	
	public String rawInsert(String payload){
		String ret = "";
		String[] splt = payload.split("/");
		
		for(int i = 0; i < splt.length; i++){
			
			query = "INSERT INTO SENSOR_DATA VALUES(";
			query += "'" + splt[i].substring(4, 11) + "',";
			query += Float.parseFloat(splt[i].substring(13, 16));
			query += ", now());";
			
			System.out.println(query);
			
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
		
		if(flag.equals(REQUEST_CONTROL)){
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
			query = "INSERT INTO ACCOUNT VALUES(";
			for(int i = 0; i < splt.length; i++){
				query += "'" + splt[i] + "'";
				if(i+1 != splt.length)
					query += ", ";
			}
			query += ", now()"; // 현재 서버가 북미에 있기 때문에 북미 시간으로 들어간다. 프로시저 콜으로 될듯
			query += ");";			 	 
		}
		
		
		System.out.println(query);
		
		try{
			state.executeUpdate(query);
			ret = "S";
		}catch(Exception e){
			e.printStackTrace();
			ret = "F";
		}
		
		return ret;
	}
	
	public void modify(){
		
	}
	
	public void delete(){
		
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
