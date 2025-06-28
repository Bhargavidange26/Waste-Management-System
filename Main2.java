package Final_Project;

import java.sql.*;
import java.util.Scanner;

class Customer {
	Scanner sc = new Scanner(System.in);

	// Method to insert a new customer
	void insertCustomer(Connection conn) {
		System.out.println("Enter Name:");
		String name = sc.nextLine();
		System.out.println("Enter Society Name:");
		String societyName = sc.nextLine();
		System.out.println("Enter City Name: ");
		String city = sc.nextLine();
		System.out.print("Enter Pincode: ");
		long pincode = sc.nextLong();
		sc.nextLine(); // Consume the newline character

		String query = "INSERT INTO Customer (Customer_name, c_society, c_city, c_pincode) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, name);
			pstmt.setString(2, societyName);
			pstmt.setString(3, city);
			pstmt.setLong(4, pincode);

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						int customerId = generatedKeys.getInt(1);
						System.out.println("Customer inserted successfully, Your Customer_ID is: " + customerId);
					} else {
						System.out.println("No Customer_ID generated.");
					}
				}
			} else {
				System.out.println("Error inserting customer: No rows affected.");
			}
		} catch (SQLException e) {
			System.out.println("Error inserting customer: " + e.getMessage());
		}
	}

	// Method to display customer details
	void displayCustomer(Connection conn, int customerId) {
		try {
			String query = "SELECT customer_id, customer_name, c_society, c_city, c_pincode FROM Customer WHERE customer_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, customerId);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					System.out.println("Customer ID: " + rs.getInt("customer_id"));
					System.out.println("Customer Name: " + rs.getString("customer_name"));
					System.out.println("Society Name: " + rs.getString("c_society"));
					System.out.println("City: " + rs.getString("c_city"));
					System.out.println("Pincode: " + rs.getLong("c_pincode"));
				} else {
					System.out.println("Customer not found.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error fetching customer details: " + e.getMessage());
		}
	}

	// Method to update customer details
	void updateCustomer(Connection conn, int customerId) {
		try {
			String checkQuery = "SELECT COUNT(*) FROM Customer WHERE customer_id = ?";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
				checkStmt.setInt(1, customerId);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next() && rs.getInt(1) == 0) {
					System.out.println("Customer not found.");
					return;
				}
			}

			boolean updateMore = true;
			while (updateMore) {
				System.out.println("Which data do you want to update?");
				System.out.println("1. Name");
				System.out.println("2. Society Name");
				System.out.println("3. City");
				System.out.println("4. Pincode");
				System.out.println("5. Exit update");
				int updateChoice = sc.nextInt();
				sc.nextLine();

				String updateQuery = "";
				switch (updateChoice) {
				case 1:
					System.out.print("Enter new Name: ");
					String newName = sc.nextLine();
					updateQuery = "UPDATE Customer SET customer_name = ? WHERE customer_id = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
						pstmt.setString(1, newName);
						pstmt.setInt(2, customerId);
						pstmt.executeUpdate();
						System.out.println("Customer name updated successfully.");
					}
					break;
				case 2:
					System.out.print("Enter new Society Name: ");
					String newSociety = sc.nextLine();
					updateQuery = "UPDATE Customer SET c_society = ? WHERE customer_id = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
						pstmt.setString(1, newSociety);
						pstmt.setInt(2, customerId);
						pstmt.executeUpdate();
						System.out.println("Society Name updated successfully.");
					}
					break;
				case 3:
					System.out.print("Enter new City: ");
					String newCity = sc.nextLine();
					updateQuery = "UPDATE Customer SET c_city = ? WHERE customer_id = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
						pstmt.setString(1, newCity);
						pstmt.setInt(2, customerId);
						pstmt.executeUpdate();
						System.out.println("City updated successfully.");
					}
					break;
				case 4:
					System.out.print("Enter new Pincode: ");
					long newPincode = sc.nextLong();
					updateQuery = "UPDATE Customer SET c_pincode = ? WHERE customer_id = ?";
					try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
						pstmt.setLong(1, newPincode);
						pstmt.setInt(2, customerId);
						pstmt.executeUpdate();
						System.out.println("Pincode updated successfully.");
					}
					break;
				case 5:
					updateMore = false;
					System.out.println("Finished updating customer data.");
					break;
				default:
					System.out.println("Invalid choice.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error updating customer details: " + e.getMessage());
		}
	}

	// Method to delete customer
	void deleteCustomer(Connection conn, int customerId) {
		try {
			String deleteQuery = "DELETE FROM Customer WHERE customer_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
				pstmt.setInt(1, customerId);
				int rowsAffected = pstmt.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Customer deleted successfully.");
				} else {
					System.out.println("Customer not found.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error deleting customer: " + e.getMessage());
		}
	}

	// Method to show customer transactions
	void showTransaction(Connection conn, int customerId) {
		try {
			String query = "SELECT * FROM Transaction WHERE Customer_ID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, customerId);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					System.out.println("Transaction Details:");
					// System.out.println("Transaction_ID: " + rs.getInt("Transaction_ID"));
					System.out.println("Customer_ID: " + rs.getInt("Customer_ID"));
					System.out.println("Collector_ID: " + rs.getInt("Collector_ID"));
					System.out.println("Transaction_date: " + rs.getDate("Transaction_date"));
					System.out.println("Status: " + rs.getString("Status"));
					System.out.println("Payment: " + rs.getBigDecimal("Payment"));
				} else {
					System.out.println("No transaction found.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving transaction details: " + e.getMessage());
		}
	}

	// Method to login customer
	boolean loginCustomer(Connection conn, int customerId) {
		try {
			String query = "SELECT COUNT(*) FROM Customer WHERE customer_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, customerId);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					return true; // Customer exists
				} else {
					System.out.println("Customer ID not found.");
					return false;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error logging in: " + e.getMessage());
			return false;
		}
	}

	// Method to show customer menu and handle choices
	void customerMenu(Connection conn) {
		int customerChoice;
		do {
			System.out.println("1. Login \n2. Create new account \n3. Exit");
			customerChoice = sc.nextInt();
			sc.nextLine();

			switch (customerChoice) {
			case 1:
				System.out.println("Enter Customer ID:");
				int customerId = sc.nextInt();
				sc.nextLine();

				// Attempt to log in
				if (loginCustomer(conn, customerId)) {
					int accountChoice;
					do {
						System.out.println(
								"1. Show Profile \n2. Update Details \n3. Delete account \n4. Show Transaction \n5. Exit");
						accountChoice = sc.nextInt();
						sc.nextLine();

						switch (accountChoice) {
						case 1:
							displayCustomer(conn, customerId);
							break;
						case 2:
							updateCustomer(conn, customerId);
							break;
						case 3:
							deleteCustomer(conn, customerId);
							break;
						case 4:
							showTransaction(conn, customerId);
							break;
						case 5:
							System.out.println("Exiting...");
							break;
						default:
							System.out.println("Invalid choice.");
						}
					} while (accountChoice != 5);
				}
				break;

			case 2:
				insertCustomer(conn); // Create new customer account
				break;

			case 3:
				System.out.println("Exiting...");
				break;

			default:
				System.out.println("Invalid choice.");
			}
		} while (customerChoice != 3);
	}
}

class Collector {
	Scanner sc = new Scanner(System.in);

	void insertcollectionPlant(Connection conn) {
		System.out.println("Enter Collector Name:");
		String name = sc.nextLine();
		System.out.println("Enter Collector Department Name:");
		String DeptName = sc.nextLine();
		System.out.println("Enter Collection Plant Area Name: ");
		String plant_area = sc.nextLine();
		System.out.print("Enter Collection Plant City: ");
		String plant_city = sc.nextLine();

		// Do not include customer_id in the insert query
		String query = "INSERT INTO Collection_Plant (Collector_Name, Collector_Dept, Collection_Plant_Area, Collection_Plant_City) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, name);
			pstmt.setString(2, DeptName);
			pstmt.setString(3, plant_area);
			pstmt.setString(4, plant_city);

			// Execute the insert query
			pstmt.executeUpdate();

			// Retrieve generated keys
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int collectorId = generatedKeys.getInt(1); // Retrieve the generated Collector_ID
					System.out.println(
							"Collection plant data inserted successfully, Your Collector_ID is: " + collectorId);
				} else {
					System.out.println("No Collector_ID generated.");
				}
			}

		} catch (SQLException e) {
			System.out.println("Error inserting collection plant: " + e.getMessage());
		}
	}

	void displayCollectionPlant(Connection conn, int collector_id) {
		try {
			// Query to check if the collector exists
			String checkQuery = "SELECT count(*) FROM Collection_plant WHERE Collector_ID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
				pstmt.setInt(1, collector_id);
				ResultSet rs = pstmt.executeQuery();

				// If no collector is found with the given ID
				if (rs.next() && rs.getInt(1) == 0) {
					System.out.println("Collector not found");
					return;
				}
			}

			// If collector exists, fetch and display the details
			String query = "SELECT * FROM Collection_plant WHERE Collector_ID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, collector_id);
				ResultSet rs = pstmt.executeQuery();

				// Printing the header row for better readability
				System.out.println("-------------------------------------------------------------------------------");
				System.out.printf("%-10s %-20s %-30s %-20s %-10s\n", "Collector ID", "Collector Name",
						"Collector Department Name", "Collection Plant Area", "Collection Plant City");
				System.out.println("-------------------------------------------------------------------------------");

				// Fetching and printing the collector's data
				if (rs.next()) {
					System.out.printf("%-10d %-20s %-30s %-20s %-10s\n", rs.getInt("Collector_ID"),
							rs.getString("Collector_Name"), rs.getString("Collector_Dept"),
							rs.getString("Collection_Plant_Area"), rs.getString("Collection_Plant_City"));
				}

				System.out.println("-------------------------------------------------------------------------------");

			}
		} catch (SQLException e) {
			System.out.println("Error fetching collection plant details: " + e.getMessage());
		}
	}

	void updateCollectionPlant(Connection conn, int collector_id) {
		String checkQuery = "SELECT COUNT(*) FROM Collection_plant WHERE collector_id = ?";
		try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
			checkStmt.setInt(1, collector_id);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) == 0) {
				System.out.println("Can't find your Account");
				return; // Exit if employee does not exist
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Ask user which field to update
		boolean updateMore = true;

		while (updateMore) {
			System.out.println("Which data do you want to update?");
			System.out.println("1. Name");
			System.out.println("2. Department Name");
			System.out.println("3. Collection Plant Area");
			System.out.println("4. Collection Plant City");
			System.out.println("5. Exit update");
			int updateChoice = sc.nextInt();
			sc.nextLine(); // Consume newline

			String updateQuery = "";
			switch (updateChoice) {
			case 1: // Update Name
				System.out.print("Enter new Name: ");
				String newName = sc.nextLine();
				updateQuery = "UPDATE Collection_plant SET Collector_name = ? WHERE collector_id= ?";
				try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
					pstmt.setString(1, newName);
					pstmt.setInt(2, collector_id);
					pstmt.executeUpdate();
					System.out.println("Collector name updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case 2: // Update Society Name
				System.out.print("Enter new Deapartment Name: ");
				String new_DeptName = sc.nextLine();
				updateQuery = "UPDATE Collection_plant SET Collector_Dept= ? WHERE collector_id = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
					pstmt.setString(1, new_DeptName);
					pstmt.setInt(2, collector_id);
					pstmt.executeUpdate();
					System.out.println("Collector Department Name updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case 3: // Update City
				System.out.print("Enter new Area name: ");
				String new_PlantArea = sc.nextLine();
				updateQuery = "UPDATE Collection_plant SET Collection_Plant_Area = ? WHERE collector_id= ?";
				try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
					pstmt.setString(1, new_PlantArea);
					pstmt.setInt(2, collector_id);
					pstmt.executeUpdate();
					System.out.println("Collection plant area name updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case 4: // Update Pincode
				System.out.print("Enter new City: ");
				String new_PlantCity = sc.nextLine();
				updateQuery = "UPDATE Collection_plant SET Collection_Plant_Area = ? WHERE collector_id= ?";
				try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
					pstmt.setString(1, new_PlantCity);
					pstmt.setInt(2, collector_id);
					pstmt.executeUpdate();
					System.out.println("Customer pincode updated successfully.");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;

			case 5: // Finish updating
				updateMore = false;
				System.out.println("Finished updating employee data.");
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	void deleteCollectionPlant(Connection conn, int collector_id) {
		String deleteQuery = "DELETE FROM Collection_plant WHERE collector_id = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
			pstmt.setInt(1, collector_id);
			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Your account is  deleted successfully.");
			} else {
				System.out.println("No Account found with the given number.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	boolean loginCollector(Connection conn, int collector_id) {
		try {
			String query = "SELECT COUNT(*) FROM Collection_plant WHERE collector_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, collector_id);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					return true; // Customer exists
				} else {
					System.out.println("Customer ID not found.");
					return false;
				}
			}
		} catch (SQLException e) {
			System.out.println("Error logging in: " + e.getMessage());
			return false;
		}
	}

	void showTransaction(Connection conn, int collector_id) {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter Customer_ID:");
		int cust_id = sc.nextInt();
		sc.nextLine(); // Consume newline to clear the buffer

		// SQL query to get the transaction details between a specific collector and
		// customer
		String query = "SELECT * FROM Transaction WHERE Collector_ID = ? AND Customer_ID = ?";

		try {
			// Prepare the statement
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, collector_id); // Set the collector_id parameter
			pstmt.setInt(2, cust_id); // Set the customer_id parameter

			// Execute the query
			ResultSet rs = pstmt.executeQuery();

			// If there are results, display the transaction details
			if (rs.next()) {
				System.out.println("Transaction Details:");
				// System.out.println("Transaction_ID: " + rs.getInt("Transaction_ID"));
				System.out.println("Customer_ID: " + rs.getInt("Customer_ID"));
				System.out.println("Collector_ID: " + rs.getInt("Collector_ID"));
				System.out.println("Transaction_date: " + rs.getDate("Transaction_date"));
				System.out.println("Status: " + rs.getString("Status"));
				System.out.println("Payment: " + rs.getBigDecimal("Payment"));
			} else {
				System.out.println("No transaction found for the given Collector_ID and Customer_ID.");
			}

			// Close the result set and prepared statement
			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			// Handle any SQL exceptions
			System.out.println("Error retrieving transaction details: " + e.getMessage());
		}

	}

	void collectormenu(Connection conn) {
		Scanner sc = new Scanner(System.in);

		int custChoice;
		do {
			System.out.println("1.Login \n2.Create new account \n3.Exit");
			custChoice = sc.nextInt();
			sc.nextLine();
			switch (custChoice) {
			case 1:
				int accChoice;
				System.out.println("Enter Collector_ID :");
				int collector_id = sc.nextInt();
				sc.nextLine();
				String checkQuery = "SELECT COUNT(*) FROM Collection_plant WHERE collector_id = ?";
				try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
					checkStmt.setInt(1, collector_id);
					ResultSet rs = checkStmt.executeQuery();
					if (rs.next() && rs.getInt(1) == 0) {
						System.out.println("Can't find your Account");
						return; // Exit if employee does not exist
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (loginCollector(conn, collector_id)) {
					do {

						System.out.println(
								"1.Show Profile of collection Plant \n2.Update Details of Collection Plant \n3.Delete Collection Plant \n4.Show Transaction \n5.Exit");
						accChoice = sc.nextInt();
						sc.nextLine();
						switch (accChoice) {
						case 1:
							displayCollectionPlant(conn, collector_id);
							break;
						case 2:
							updateCollectionPlant(conn, collector_id);
							break;
						case 3:
							deleteCollectionPlant(conn, collector_id);
							break;
						case 4:
							showTransaction(conn, collector_id);
							break;
						case 5:
							System.out.println("Exiting");
							;
							break;
						default:
							System.out.println("Inavalid choice");
							break;
						}
					} while (accChoice != 5);
				}
				break;
			case 2:
				insertcollectionPlant(conn);
				break;
			case 4:
				System.out.println("Exiting as collector");
				break;
			default:
				System.out.println("Invalid choice");
				break;
			}
		} while (custChoice != 3);
	}
}

public class Main2 {
	private static Connection conn;
	private static final String USER = "root";
	private static final String PASS = "root_39@";
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/personal";
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		Customer customer = new Customer();
		Collector collector = new Collector();
		try {
			// Load and register JDBC driver
			Class.forName(JDBC_DRIVER);
			// Establish database connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			int mainChoice;
			do {
				System.out.println("1.I am Customer \n2.I am Collector \n3.Exit ");
				mainChoice = sc.nextInt();
				sc.nextLine();
				switch (mainChoice) {
				case 1:
					customer.customerMenu(conn);
					break;
				case 2:
					collector.collectormenu(conn);
					break;
				case 3:
					System.out.println("Exiting..");
					break;
				default:
					System.out.println("Invalid choice is entered");
				}
			} while (mainChoice != 3);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			// Close connection to prevent resource leaks
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}