import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.lang.Thread;

public class OrderManagerCreateDatabase {
	
	public static void main(String[] args) {
		System.out.printf("In %s,Working Directory: %s\n", Thread.currentThread().getStackTrace()[1] ,System.getProperty("user.dir"));
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "omDB";
		String connStr = protocol + dbName+ ";create=true";

		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");
        Connection conn = null;
        
        Statement statmt = null;
	   // TODO tables created by this program
        String tables [] = {};
        
        //TODO stored functions for business logics
        String[] storeFunctions = {};
        
        try {
        	// connect to the database using URL
			conn = DriverManager.getConnection(connStr, props);
	        // statement is channel for sending commands thru connection 
	        statmt = conn.createStatement();
	        System.out.println("Connected to and created database " + dbName);
	        
	    	
			//1) drop the table if exist
			   for (int i = 0; i < tables.length; i++) {
				   String tbName = tables[i]; 
	       		   try{ 
	       			   statmt.execute("drop table " + tbName);
	       			   System.out.println("Dropped table " + tbName + "table number " +  i);
	       		   } catch (SQLException e) {
	       			   System.out.println("Did not drop table " + tbName); 
	       			   e.printStackTrace();
	       		   }
			   } 
			// drop the storedFunctions and recreate as below
		   	   for (String fun : storeFunctions) {
		   		   try {
		   			   statmt.executeUpdate("drop function " + fun );
		   			   System.out.println("Dropped function " + fun);
		   		   } catch (SQLException ex) {
		   			   System.out.print(fun +" function dropping failed !");
		   			   ex.printStackTrace();
		   		   }
		   	   }
	   	   
		   	//2) create the table: list of prepare statement store in the array
				String sql_stringlist[]  = new String[7];
				sql_stringlist[0] = "create table Customer(";
	        
		   	   
		   	   
		   	   
	        
        } catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//close connection
			if(conn != null) {
				try {
					conn.close();
				} catch(SQLException e) {
					e.printStackTrace();
				}
			}
        
	}
}
