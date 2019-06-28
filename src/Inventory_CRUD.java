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
import java.util.Properties;
import API.DBAdminAPI;

/*
 * This programs deal with populating Customer table with information retrieved from 
 * online form source such as google sheet.
 * 
 * function: Inventory_CRUD class does Insert/Update/Delete tests on InventoryRecord table.
 * 
 * @Reference assignment-3 Testassignment3.java
 * @Author Theodore Li,Andy Wang
 * */
public class Inventory_CRUD {

	// the default framework is embedded
	static String protocol = "jdbc:derby:";
	static String dbName = "OrderManagerDB";

	/**
	 * parse single line of buffer input into array by one or more than one
	 * separated spaces * Parse single line of buffer input into array by one or
	 * more than one separated spaces e.g. PC-123456-1N 12 2130.12," * @param Input
	 * line of data from inventory_data txt
	 * 
	 * @param line
	 * @return Array of String
	 */
	private static String[] parseInventoryData(String line) {
		String delims = "[\\t]+";
		String[] res = line.split(delims);
		return res;
	};
	
	private static void printErrInput(String sku_str, String unit_str, String price_str) {
		System.err.printf("Error occured while inserting Product_SKU %s, UnitAmount %s and UnitPrice %s \n", sku_str,
				unit_str, price_str);
	}
	
	/**
	 * Driver function for CRUD.
	 * 
	 * Use case: administrator login in to view the available listing of all the
	 * products.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Iventory_CRUD's working Directory: %s\n", System.getProperty("user.dir"));
		Properties props = new Properties();

		// providing a user name and password is optional in the derby embedded client framework.
		props.put("user", "user1");
		props.put("password", "user1");

		// statement is channel for sending commands thru connection
		Statement stmt = null;

		// result set for queries
		ResultSet rs = null;

		// tables tested by this program
		String dbTables[] = { "InventoryRecord" };

		// name of data file
		String fileName = "/data/inventory_data.txt";
		// concatenate relative file path
		String filePath = new File("").getAbsolutePath();
		String readerInput = (filePath + fileName);// .replaceAll("\\s+","");
		// connect to the database using URL
		String connStr = protocol + dbName + ";create=true";
		try (
				// open data file
				BufferedReader buffer = new BufferedReader(new FileReader(new File(readerInput)));
				// connect to database
				Connection conn = DriverManager.getConnection(connStr, props);

		) {
			stmt = conn.createStatement();
			System.out.println("Connected to and created database " + dbName);

			// select operation prepared statements
			PreparedStatement select_product = conn.prepareStatement(
					"select 1 from INVENTORYRECORD where Product_SKU = ? and AvailableUnits = ? and UnitPrice = ?");

			// insert operation prepared statements
			PreparedStatement insertRow_Product = conn.prepareStatement("insert into INVENTORYRECORD values(?, ?, ?)");

			// show data before insertion
			for (String tbl : dbTables) {
				rs = stmt.executeQuery("select count(*) from " + tbl);
				if (rs.next()) {
					int count = rs.getInt(1);
					System.out.printf("Before insertion, Table %s has %d rows \n", tbl, count);
				}
			}

			// clear data from tables
			for (String tbl : dbTables) {
				try {
					stmt.executeUpdate("Delete from " + tbl);
					System.out.println("Truncated table " + tbl);
				} catch (SQLException ex) {
					System.out.println("Did not truncate table " + tbl);
				}
			}
			// int row_cnt =  1;
			String line;
			while ((line = buffer.readLine()) != null) {

				// split input line into fields at tab delimiter
				String[] datalist = parseInventoryData(line);
				if (datalist.length != 3) {
					System.out.printf("Invalid input data. Expected fields number: 3."
							+ "Actual fields number: %d.\n", datalist.length);

					continue;
				}
				String sku_str = datalist[0];
				String unit_str = datalist[1];
				String price_str = datalist[2];

				if (!Validation.isProperCount(unit_str)) {
					printErrInput(sku_str, unit_str, price_str);
					continue;
				}
					
				int unit = Integer.parseInt(unit_str);
				Double price = Validation.strToDecimal(price_str);

				select_product.setString(1, sku_str);
				select_product.setInt(2, unit);
				// System.out.println("  price: "+ price + " count: "+row_cnt);
				// row_cnt++;
				select_product.setDouble(3, price);

				// use exectute() to fire up sql inside statement
				select_product.execute();
				rs = select_product.getResultSet();
				// check if product ResultSet object exists in db
				if (!rs.next()) {
					
					insertRow_Product.setString(1, sku_str);
					insertRow_Product.setInt(2, unit);
					insertRow_Product.setDouble(3, price);
					
					try	{
						insertRow_Product.execute();
					} catch(SQLException e) {
						System.err.printf("Insert %s to InventoryRecord failed.\n",sku_str);
					}								
					if (insertRow_Product.getUpdateCount() != 1) {
						printErrInput(sku_str, unit_str, price_str);
					}
				}
				rs.close();

			}

			// print number of rows in tables
			for (String tbl : dbTables) {
				rs = stmt.executeQuery("select count(*) from " + tbl);
				if (rs.next()) {
					int count = rs.getInt(1);
					System.out.printf("After insertion, Table %s has %d rows \n", tbl, count);
				}
			}
			rs.close();
			
			API.PrintUtil.printInventoryRecord(conn);
			String query = "delete from InventoryRecord where Product_SKU = 'PC-123456-1N'";
			DBAdminAPI.transactionRollback(conn, stmt, query, dbTables[0]);
			
 
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				// close statement
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}