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

/**
 * Product_CRUD class does Insert/Update/Delete tests on Product table.
 * 
 * @Author Ted Li,Andy Wang
 */
public class Product_CRUD {

	// the default framework is embedded
	static String protocol = "jdbc:derby:";
	static String dbName = "OrderManagerDB";

	/**
	 * Parse single line of buffer input into array by one or more than one
	 * separated spaces
	 * 
	 * @param Input line of data from product_data txt
	 * @return Array of String
	 */
	private static String[] parseProductData(String line) {
		String delims = "[\\t]+";
		String[] res = line.split(delims);
		return res;
	};

	/**
	 * Driver function for CRUD.
	 * 
	 * Use case: customers login in to view the available listing of all the
	 * products.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.printf("Product_CRUD's working directory is: %s\n", System.getProperty("user.dir"));
		Properties props = new Properties();
		// providing a user name and password is optional in the embedded
		// and derbyclient frameworks
		props.put("user", "user1");
		props.put("password", "user1");
		// saved information session for this connection

		// statement is channel for sending commands thru connection
		Statement stmt = null;

		// result set for queries
		ResultSet rs = null;

		// tables tested by this program
//		String dbTables[] = { "WrittenBy", // relations
//				"Publisher", "Journal", "Article", "Author", // entities
		String dbTables[] = { "Product" };

		// name of data file
		String fileName = "/data/product_data.txt";
		// concatenate relative file path
		String filePath = new File("").getAbsolutePath();
		String readerInput = (filePath + fileName);// .replaceAll("\\s+","");

		// connect to the database using URL
		String connStr = protocol + dbName + ";create=true";
		try (
				// open data file
				BufferedReader buffer = new BufferedReader(new FileReader(new File(readerInput)));
				// connect to database
				Connection conn = DriverManager.getConnection(connStr, props);) {
			stmt = conn.createStatement();
			System.out.println("Connected to and created database " + dbName);

			// select operation prepared statements
			PreparedStatement select_product = conn
					.prepareStatement("select 1 from PRODUCT where Name = ? and Description = ? and Product_SKU = ?");

			// insert operation prepared statements
			PreparedStatement insertRow_Product = conn.prepareStatement("insert into PRODUCT values(?, ?, ?)");

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

			String line;
			while ((line = buffer.readLine()) != null) {

				// split input line into fields at tab delimiter
				String[] datalist = parseProductData(line);
//				System.out.println(datalist.length + " fields\n");
				if (datalist.length != 3) {
					System.out.printf("Invalid input data. Expected fields number: 3." + "Actual fields number: %d.\n",
							datalist.length);
					continue;
				}
				String sku_str = datalist[0];
				String name_str = datalist[1];
				String descrip_str = datalist[2];
				if (datalist[2].length() > 256) {
					descrip_str = datalist[2].substring(0, 255);
				}

				select_product.setString(1, sku_str);
				select_product.setString(3, name_str);
				select_product.setString(2, descrip_str);
				// Uncertain getResult will finally fire up select query

				// use exectute() flag
				select_product.execute();
				rs = select_product.getResultSet();
//				System.out.println("1 sku_str: "+ rs.getNString(1));
				// check if product ResultSet object exists in db
				if (!rs.next()) {
					insertRow_Product.setString(1, name_str);
					insertRow_Product.setString(2, descrip_str);
					insertRow_Product.setString(3, sku_str);
					try {
						insertRow_Product.execute();
					} catch (SQLException e) {
						System.err.printf("Adding SKU:%s is not successfull.\n", sku_str);
					}

//					System.out.println(cur + " has " + insertRow_Product.getUpdateCount() + "rows \n");
					if (insertRow_Product.getUpdateCount() != 1) {
						System.err.printf("Error occured while inserting SKU: %s, Name %s, Description %s\n",
						sku_str,name_str, descrip_str);
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
			
			API.PrintUtil.printProducts(conn);
			String query = "delete from Product where Product_SKU = 'PC-123456-1N'";
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