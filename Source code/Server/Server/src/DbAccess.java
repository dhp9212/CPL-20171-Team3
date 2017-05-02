import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;


public class DbAccess {

	
	 public static final String REQUEST_CURRENT_TEMP = "1";
	 public static final String REQUEST_ACCRUE_TEMP = "2";
	 public static final String REQUEST_HUM = "3";
	 public static final String REQUEST_CONTROL = "4";
	 public static final String REQUEST_SIGNUP = "5";
	 public static final String COLSE = "6";
	
	private String url = "jdbc:mysql://dhdb.cvqwpznjcq93.us-west-2.rds.amazonaws.com:3306/";
	private String userName = "DH";
	private String password = "dh123";
	private String dbName = "DHDB";
	String driver = "com.mysql.jdbc.Driver";
	
	Connection con;
	Statement state;
	String query = "";
	
	public DbAccess(){
		try{
			con = DriverManager.getConnection(url + dbName, userName, password);
			state = con.createStatement();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void test(){
		try{

			
			
			query = "SELECT * FROM ACCOUNT";
			
			ResultSet result = state.executeQuery(query);
			
			while(result.next()){
				
				String id = result.getString("ID");
				String lastLogin = result.getString("LASTLOGIN");
				System.out.println(id + " " + lastLogin);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void select(){
		
	}
	
	public void insert(String code){
		if(code.equals(REQUEST_CONTROL)){
			query = "INSERT INTO CONTROL VALUES('ON')";
			try{
				state.executeUpdate(query);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
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
