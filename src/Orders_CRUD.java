
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
// import java.text.SimpleDateFormat;
// import java.util.Date;
import java.util.Properties;

import API.DBAdminAPI;
import API.PrintUtil;

/*
 * This programs deal with populating Customer table with information retrieved from 
 * online form source such as google sheet.
 * 
 * function: Orders_CRUD class does Insert/Update/Delete tests on Orders table.
 * 
 * @Reference assignment-3 Testassignment3.java && assingment
 * @Author  Andy Wang,Theodore Li
 * */
public class Orders_CRUD {
	
	
	public static void main(String[] args) {
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "OrderManagerDB";
		String connStr = protocol + dbName+ ";create=true";

	    // tables tested by this program
		String dbTables[] = { "Orders"
    	    };
		// name of data file
		String fileName = "/data/orders_data.txt";
		String filePath = new File("").getAbsolutePath();
		String readerInput = (filePath + fileName);
		
		Properties props = new Properties(); // connection properties
        // providing a user name and password is optional in the embedded
        // and derbyclient frameworks
        props.put("user", "user1");
        props.put("password", "user1");

        // result set for queries
        ResultSet rs = null;
		try (
			// open data file
			BufferedReader br = new BufferedReader(new FileReader(new File(readerInput)));
			
			// connect to database
			Connection  conn = DriverManager.getConnection(connStr, props);
			Statement stmt = conn.createStatement();
			
			// insert prepared statements
			PreparedStatement insertRow_Orders = conn.prepareStatement(
					"insert into Orders values(DEFAULT, ?, ?, DEFAULT, DEFAULT)");	

		) {
			// connect to the database using URL
            System.out.println("Connected to database " + dbName);
            
            // clear data from tables
            for (String tbl : dbTables) {
	            try {
	            		stmt.executeUpdate("delete from " + tbl);
	            		System.out.println("Truncated table " + tbl);
	            } catch (SQLException ex) {
	            		System.out.println("Did not truncate table " + tbl);
	            }
            }
            int row_cnt = 1;
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				if (data.length != 2) {
					System.out.printf("Invalid input data. Expected fields number at most: 4."
							+ "Actual fields number: %d.\n", data.length);
					continue;
				}
				//String orders_id = data[0];
				String customer_id = data[0];
				int amounts = Integer.valueOf(data[1]);
				// Timestamp order_date = Timestamp.valueOf(data[2]);
				// String ship_date = data[4]; 
				
				// add Publisher if does not exist
				try {
//					System.out.println(row_cnt);
					// insertRow_Orders.setString(1, orders_id);
					insertRow_Orders.setString(1, customer_id);
					insertRow_Orders.setInt(2, amounts);
					// insertRow_Orders.setTimestamp(3, order_date);
					// insertRow_Orders.setString(5, ship_date);
					insertRow_Orders.execute();
				} catch (SQLException ex) {
					// already exists
//					System.err.println(row_cnt);
				}
				
				
			}	
				
			
			
			// print number of rows in tables
			for (String tbl : dbTables) {
				rs = stmt.executeQuery("select count(*) from " + tbl);
				if (rs.next()) {
					int count = rs.getInt(1);
					System.out.printf("Table %s : count: %d\n", tbl, count);
				}
				;
			}
			
//			String orders_id = data[0];
//			String customer_id = data[1];
//			int amounts = Integer.valueOf(data[2]);
//			String order_date = data[3];
//			String ship_date = data[4];
			
			// Transaction rollback on insertion
			String query = "insert into Orders "
					+ "values(DEFAULT,'cchap@foxmail.com', 1,DEFAULT,DEFAULT) ";
			DBAdminAPI.transactionRollback(conn, stmt, query, dbTables[0]);
			



		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}