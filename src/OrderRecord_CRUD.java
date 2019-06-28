import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
// import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
// import java.sql.Timestamp;
// import java.sql.Types;
import java.util.Properties;
// import API.DBAdminAPI;

/**
 * Create OrderRecord table and demonstrate basic single table test.
 * 
 * @author philip gust, Andy
 */
public class OrderRecord_CRUD {
	
	public static void main(String[] args) {
	    // the default framework is embedded
	    String protocol = "jdbc:derby:";
	    String dbName = "OrderManagerDB";
		String connStr = protocol + dbName+ ";create=true";

	    // tables tested by this program
		String dbTables[] = {
			"OrderRecord"
//			"PublishedBy", "PublishedIn", "WrittenBy",		// relations
//    	    	 	"Publisher", "Journal", "Article", "Author",		// entities
    	    };

		// name of data file
		String fileName = "/data/order_record_data.txt";
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
			PreparedStatement insertRow_OrderRecord = conn.prepareStatement(
					"insert into OrderRecord values(?, ?, ?, ?, DEFAULT)");
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
                  
            API.PrintUtil.printInventoryRecord(conn);
			String line;
			while ((line = br.readLine()) != null) {
				// split input line into fields at tab delimiter
				String[] data = line.split("\t");
				if (data.length != 3) {
					System.out.printf("Invalid input data. Expected fields number: 3."
							+ "Actual fields number: %d.\n", data.length);
					continue;
				}
				int orders_id = Integer.valueOf(data[0]);
				String product_sku = data[1];
				int unit_amount = Integer.valueOf(data[2]); // the number of unit for this SKU
				// Timestamp order_date = Timestamp.valueOf(data[2]);
	
				// add OrderRecord if does not exist
				try {
					// Both setString and setInt for orders_id works, weird right?
					// insertRow_Orders.setString(1, orders_id);
					insertRow_OrderRecord.setInt(1, orders_id);
					insertRow_OrderRecord.setString(2, product_sku);
					
					// Since the unitPrice is not null defined in the ProducRecord Table,
					// Have to set the Price to the guard value here, and then change the Price
					// either by: 1. Trigger "InsertUnitPriceInOrderRecord"
					// 2. Stored Function "getUnitPriceFromInventoryRecord"
					// I implemented both, the commented-out code below is using stored function
					// To retrieve the UnitPrice and then using that price for the prepared statement
					double unitPrice = -1; // safeguard value, will be changed
									
//		        	// prepared statement for calling isEmail procedure with 1 param
//		        	PreparedStatement invoke_getUnitPriceFromInventoryRecord =
//		        	conn.prepareStatement("values ( getUnitPriceFromInventoryRecord(?) )");
//		        	
//		        	invoke_getUnitPriceFromInventoryRecord.setString(1, product_sku);
//		        	rs = invoke_getUnitPriceFromInventoryRecord.executeQuery();
//		        	if (rs.next()) {
//		        		unitPrice = rs.getBigDecimal(1).doubleValue();
//		        		System.out.printf("Product_SKU: %s unitPrice: %f\n", product_sku, unitPrice);
//		        	}
//		            // rs.close();
//		        	
//		        	invoke_getUnitPriceFromInventoryRecord.close();
			
					// Here the UnitPrice is still -1 and trigger will change the UnitPrice
					insertRow_OrderRecord.setDouble(3, unitPrice);
					insertRow_OrderRecord.setInt(4, unit_amount); // amount of this SKU product
					
					// the 5th ? is the default false status: not enough stock
					try {
						insertRow_OrderRecord.execute();
					}catch(SQLException ex) {
						// System.err.println(ex.getMessage());
						System.err.printf("Already inserted OrderRecord with Orders_ID %d and SKU %s\n",orders_id,product_sku );
					}				
				} catch (SQLException ex) {
					ex.printStackTrace();
					System.err.printf("Insert failed because trigger haven't set price >= 0\n");
				}




//
//				// add Article if does not exist
//				String articleTitle = data[4];
//				String articleDOI = data[5];
//				if (!Biblio.isDoi(articleDOI)) {
//					System.err.printf("Unable to insert Article \"%s\" invalid DOI %s\n", articleTitle, articleDOI);
//					continue;
//				}
//				try {
//					insertRow_Article.setString(1, articleTitle);
//					insertRow_Article.setString(2, articleDOI);
//					insertRow_Article.execute();
//				} catch (SQLException ex) {
//					// already exists
//					System.err.printf("Already inserted Article \"%s\" DOI %s\n", articleTitle, articleDOI);
//				}
//

//
//				// add Author if does not exist
//				String authorFamilyName = data[6];
//				String authorGivenName = data[7];
//				long authorORCID = 0;
//				try {
//					authorORCID = Biblio.parseOrcid(data[8]);
//					insertRow_Author.setString(1, authorFamilyName);
//					insertRow_Author.setString(2, authorGivenName);
//					insertRow_Author.setLong(3, authorORCID);
//					insertRow_Author.execute();
//				} catch (SQLException ex) {
//					// already exists
//					// System.err.printf("Already inserted Author %s, %s ORCID %016d\n", 
//					//		authorFamilyName, authorGivenName, authorORCID);
//				} catch (NumberFormatException ex) {
//					 System.err.printf("Unable to insert Author %s, %s invalid ORCID %s\n", 
//							 authorFamilyName, authorGivenName, data[8]);
//					continue;
//				}

			}
			// print number of rows in tables
			for (String tbl : dbTables) {
				rs = stmt.executeQuery("select count(*) from " + tbl);
				if (rs.next()) {
					int count = rs.getInt(1);
					System.out.printf("Table %s : count: %d\n", tbl, count);
				}
			}
			rs.close();
			API.PrintUtil.printOrderRecord(conn);
			API.PrintUtil.printInventoryRecord(conn);
			API.PrintUtil.printOrders(conn);
			stmt.execute("Delete From OrderRecord where Orders_Id = 6");
			API.PrintUtil.printInventoryRecord(conn);
//			String query = "insert into OrderRecord "
//					+ "values(3,'PC-123456-0C', 2310.5,3, DEFAULT)";
//			DBAdminAPI.transactionRollback(conn, stmt, query, dbTables[0]);
			


//			// delete article
//			System.out.println("\nDeleting article 10.1145/2838730 from CACM with 3 authors");
//			stmt.execute("delete from Article where doi = '10.1145/2838730'");
//			PubUtil.printArticles(conn);
//			PubUtil.printAuthors(conn);
//
//			// delete publisher ACM
//			System.out.println("\nDeleting publisher ACM");
//			stmt.executeUpdate("delete from Publisher where name = 'ACM'");
//			PubUtil.printPublishers(conn);
//			PubUtil.printJournals(conn);
//			PubUtil.printArticles(conn);
//			PubUtil.printAuthors(conn);
//			
//			// delete journal Spectrum (0018-9235)
//			System.out.println("\nDeleting journal Spectrum from IEEE");
//			stmt.executeUpdate("delete from Journal where issn = " + Biblio.parseIssn("0018-9235"));
//			PubUtil.printJournals(conn);
//			PubUtil.printArticles(conn);
//			PubUtil.printAuthors(conn);
//					
//			// delete journal Computer
//			System.out.println("\nDeleting journal Computer from IEEE");
//			stmt.executeUpdate("delete from Journal where title = 'Computer'");
//			PubUtil.printPublishers(conn);
//			PubUtil.printJournals(conn);
//			PubUtil.printArticles(conn);
//			PubUtil.printAuthors(conn);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}