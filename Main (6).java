package Final_Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Main {
	private static Connection conn;
	private static final String USER = "root";
	private static final String PASS = "root_39@";
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/new_personal";

	public static void main(String[] args) {
		// Establish database connection before launching the GUI
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error connecting to database.");
			return; // Exit if connection fails
		}

		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Main Menu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);

		// Main panel setup with padding
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Title label with colored background panel
		JLabel titleLabel = new JLabel("Welcome to the Main Menu", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
		titleLabel.setForeground(Color.WHITE);
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(72, 61, 139)); // Dark Slate Blue
		titlePanel.add(titleLabel);

		// Button panel
		JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Top padding

		// Create buttons with new color scheme
		JButton createAccountButton = createStyledButton("Create New Account", new Color(255, 165, 0)); // Orange
		JButton loginButton = createStyledButton("Login", new Color(34, 139, 34)); // Forest Green

		// Button actions
		createAccountButton.addActionListener(e -> showCreateAccountMenu(frame));
		loginButton.addActionListener(e -> showLoginOptions(frame));

		// Add buttons to panel
		buttonPanel.add(createAccountButton);
		buttonPanel.add(loginButton);

		// Add panels to frame
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
	}

	// Method to create a styled button with rounded edges and hover effect
	private static JButton createStyledButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(new Font("Arial", Font.PLAIN, 14));
		button.setForeground(Color.WHITE);
		button.setBackground(color);
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createLineBorder(color.darker(), 2, true));
		button.setOpaque(true);

		// Hover effect
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(color.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(color);
			}
		});
		return button;
	}

	// Method to show Create Account menu
	private static void showCreateAccountMenu(JFrame frame) {
		JTextField nameField = new JTextField();
		JTextField societyField = new JTextField();
		JTextField cityField = new JTextField();
		JTextField pincodeField = new JTextField();
		Object[] message = { "Name:", nameField, "Society:", societyField, "City:", cityField, "Pincode:",
				pincodeField };

		int option = JOptionPane.showConfirmDialog(frame, message, "Create New Account", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			try {
				String insertQuery = "INSERT INTO Customer (Customer_name, c_society, c_city, c_pincode) VALUES (?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, nameField.getText());
				pstmt.setString(2, societyField.getText());
				pstmt.setString(3, cityField.getText());
				pstmt.setLong(4, Long.parseLong(pincodeField.getText()));

				int rowsInserted = pstmt.executeUpdate();
				if (rowsInserted > 0) {
					ResultSet rs = pstmt.getGeneratedKeys();
					if (rs.next()) {
						int newCustomerId = rs.getInt(1);
						JOptionPane.showMessageDialog(frame,
								"Account created successfully! Your Customer ID is: " + newCustomerId);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Error creating account.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Error creating account.");
			}
		}
	}

	// Method to show Login options
	private static void showLoginOptions(JFrame frame) {
		String customerIdInput = JOptionPane.showInputDialog(frame, "Enter your Customer ID to login:");
		try {
			int customerId = Integer.parseInt(customerIdInput);
			// If login is successful, show options for the logged-in customer
			showLoggedInOptions(frame, customerId);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Invalid Customer ID!");
		}
	}

	// Method to show options for logged-in user
	private static void showLoggedInOptions(JFrame frame, int customerId) {
		String[] options = { "1. Show Profile", "2. Update Profile", "3. Delete Account", "4. Show Transactions",
				"5. Exit" };
		int choice = JOptionPane.showOptionDialog(frame, "Select an option", "Customer Dashboard",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

		switch (choice) {
		case 0:
			showProfile(frame, customerId);
			break;
		case 1:
			updateProfile(frame, customerId);
			break;
		case 2:
			deleteAccount(frame, customerId);
			break;
		case 3:
			showTransactions(frame, customerId); // Show transactions option
			break;
		case 4:
			closeConnection();
			System.exit(0);
			break;
		}
	}

	// Method to show Profile
	private static void showProfile(JFrame frame, int customerId) {
		try {
			String sql = "SELECT * FROM Customer WHERE customer_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, customerId);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				String customerDetails = "Customer ID: " + rs.getInt("customer_id") + "\n" + "Name: "
						+ rs.getString("Customer_name") + "\n" + "Society: " + rs.getString("c_society") + "\n"
						+ "City: " + rs.getString("c_city") + "\n" + "Pincode: " + rs.getLong("c_pincode");
				JOptionPane.showMessageDialog(null, customerDetails);
			} else {
				JOptionPane.showMessageDialog(null, "Customer not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching customer details.");
		}
	}

	// Method to update Profile
	private static void updateProfile(JFrame frame, int customerId) {
		String[] updateOptions = { "1. Update Name", "2. Update Society", "3. Update City", "4. Update Pincode" };
		int updateChoice = JOptionPane.showOptionDialog(frame, "Select an option to update", "Update Profile",
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, updateOptions, updateOptions[0]);

		try {
			String updateQuery = null;
			PreparedStatement pstmt = null;

			switch (updateChoice) {
			case 0: // Update Name
				String newName = JOptionPane.showInputDialog("Enter new name:");
				updateQuery = "UPDATE Customer SET Customer_name = ? WHERE customer_id = ?";
				pstmt = conn.prepareStatement(updateQuery);
				pstmt.setString(1, newName);
				break;
			case 1: // Update Society
				String newSociety = JOptionPane.showInputDialog("Enter new society name:");
				updateQuery = "UPDATE Customer SET c_society = ? WHERE customer_id = ?";
				pstmt = conn.prepareStatement(updateQuery);
				pstmt.setString(1, newSociety);
				break;
			case 2: // Update City
				String newCity = JOptionPane.showInputDialog("Enter new city:");
				updateQuery = "UPDATE Customer SET c_city = ? WHERE customer_id = ?";
				pstmt = conn.prepareStatement(updateQuery);
				pstmt.setString(1, newCity);
				break;
			case 3: // Update Pincode
				String newPincode = JOptionPane.showInputDialog("Enter new pincode:");
				updateQuery = "UPDATE Customer SET c_pincode = ? WHERE customer_id = ?";
				pstmt = conn.prepareStatement(updateQuery);
				pstmt.setString(1, newPincode);
				break;
			default:
				return;
			}

			pstmt.setInt(2, customerId);
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(null, "Profile updated successfully!");

		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error updating profile.");
		}
	}

	// Method to delete Account
	private static void deleteAccount(JFrame frame, int customerId) {
		int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete your account?",
				"Delete Account", JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			try {
				String deleteQuery = "DELETE FROM Customer WHERE customer_id = ?";
				PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
				pstmt.setInt(1, customerId);
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(null, "Account deleted successfully!");
				closeConnection();
				System.exit(0);
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error deleting account.");
			}
		}
	}

	// Method to show Transactions
	// Method to show Transactions
	private static void showTransactions(JFrame frame, int customerId) {
		try {
			String sql = "SELECT * FROM Transaction WHERE Customer_ID = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, customerId);
			ResultSet rs = statement.executeQuery();

			StringBuilder transactions = new StringBuilder("Transactions:\n");

			while (rs.next()) {
				// Removed the Transaction_ID from the display
				transactions.append("Date: ").append(rs.getDate("Transaction_date")).append("\n").append("Status: ")
						.append(rs.getString("Status")).append("\n").append("Payment: ").append(rs.getDouble("Payment"))
						.append("\n\n");
			}

			if (transactions.toString().equals("Transactions:\n")) {
				transactions.append("No transactions found.");
			}

			JOptionPane.showMessageDialog(null, transactions.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error fetching transactions.");
		}
	}

	// Close database connection
	private static void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}