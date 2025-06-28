package Final_Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Main1 extends JFrame {
	private Connection conn;

	// Main Constructor to set up the frame
	public Main1(Connection conn) {
		this.conn = conn;
		setTitle("Main Application");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());

		JButton loginButton = new JButton("Login");
		JButton createAccountButton = new JButton("Create Account");
		JButton exitButton = new JButton("Exit");

		// Action listeners for buttons
		loginButton.addActionListener(e -> {
			Login loginScreen = new Login(conn);
			loginScreen.setVisible(true);
			dispose();
		});

		createAccountButton.addActionListener(e -> {
			CreateAccount createAccountScreen = new CreateAccount(conn);
			createAccountScreen.setVisible(true);
			dispose();
		});

		exitButton.addActionListener(e -> System.exit(0));

		// Adding buttons to the main screen
		add(loginButton);
		add(createAccountButton);
		add(exitButton);
	}

	public static void main(String[] args) {
		// Database connection
		try {
			String dbURL = "jdbc:mysql://localhost:3306/new_personal";
			String username = "root";
			String password = "root_39@";
			Connection conn = DriverManager.getConnection(dbURL, username, password);

			// Show Main Application Screen
			Main1 mainAppScreen = new Main1(conn);
			mainAppScreen.setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}

// Login screen to enter Collector ID
class Login extends JFrame {
	private Connection conn;

	public Login(Connection conn) {
		this.conn = conn;
		setTitle("Login");
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(3, 2));

		JLabel idLabel = new JLabel("Enter Collector ID:");
		JTextField idField = new JTextField(15);
		JButton loginButton = new JButton("Login");

		loginButton.addActionListener(e -> {
			try {
				int collectorId = Integer.parseInt(idField.getText());
				String query = "SELECT * FROM Collection_plant WHERE Collector_ID = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(query)) {
					pstmt.setInt(1, collectorId);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
						// Login successful, open the main menu
						MainMenu mainMenuScreen = new MainMenu(conn, collectorId);
						mainMenuScreen.setVisible(true);
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Invalid Collector ID.", "Login Failed",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		add(idLabel);
		add(idField);
		add(loginButton);
	}
}

// Create Account screen
class CreateAccount extends JFrame {
	private Connection conn;

	public CreateAccount(Connection conn) {
		this.conn = conn;
		setTitle("Create Account");
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(6, 2));

		JLabel nameLabel = new JLabel("Collector Name:");
		JTextField nameField = new JTextField(15);
		JLabel deptLabel = new JLabel("Collector Department:");
		JTextField deptField = new JTextField(15);
		JLabel areaLabel = new JLabel("Collection Plant Area:");
		JTextField areaField = new JTextField(15);
		JLabel cityLabel = new JLabel("Collection Plant City:");
		JTextField cityField = new JTextField(15);
		JButton createButton = new JButton("Create Account");

		add(nameLabel);
		add(nameField);
		add(deptLabel);
		add(deptField);
		add(areaLabel);
		add(areaField);
		add(cityLabel);
		add(cityField);
		add(createButton);

		createButton.addActionListener(e -> {
			String name = nameField.getText();
			String dept = deptField.getText();
			String area = areaField.getText();
			String city = cityField.getText();

			if (name.isEmpty() || dept.isEmpty() || area.isEmpty() || city.isEmpty()) {
				JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String query = "INSERT INTO Collection_plant (Collector_Name, Collector_Dept, Collection_Plant_Area, Collection_Plant_City) VALUES (?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, name);
				pstmt.setString(2, dept);
				pstmt.setString(3, area);
				pstmt.setString(4, city);

				int affectedRows = pstmt.executeUpdate();
				if (affectedRows > 0) {
					ResultSet rs = pstmt.getGeneratedKeys();
					if (rs.next()) {
						int collectorId = rs.getInt(1);
						JOptionPane.showMessageDialog(this,
								"Account created successfully! Collector ID: " + collectorId);
						dispose();
					}
				} else {
					JOptionPane.showMessageDialog(this, "Account creation failed.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Database error.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}

// Main Menu screen after login
class MainMenu extends JFrame {
	private Connection conn;
	private int collectorId;

	public MainMenu(Connection conn, int collectorId) {
		this.conn = conn;
		this.collectorId = collectorId;
		setTitle("Collector Menu");
		setSize(500, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout());

		JButton showProfileButton = new JButton("Show Profile");
		JButton updateProfileButton = new JButton("Update Profile");
		JButton showTransactionButton = new JButton("Show Transactions");
		JButton deleteProfileButton = new JButton("Delete Profile");
		JButton exitButton = new JButton("Exit");

		showProfileButton.addActionListener(e -> displayProfile());
		updateProfileButton.addActionListener(e -> updateProfile());
		showTransactionButton.addActionListener(e -> showTransactions());
		deleteProfileButton.addActionListener(e -> deleteProfile());
		exitButton.addActionListener(e -> System.exit(0));

		add(showProfileButton);
		add(updateProfileButton);
		add(showTransactionButton);
		add(deleteProfileButton);
		add(exitButton);
	}

	private void displayProfile() {
		String query = "SELECT * FROM Collection_plant WHERE Collector_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, collectorId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String message = "Collector ID: " + rs.getInt("Collector_ID") + "\n" + "Name: "
						+ rs.getString("Collector_Name") + "\n" + "Department: " + rs.getString("Collector_Dept") + "\n"
						+ "Area: " + rs.getString("Collection_Plant_Area") + "\n" + "City: "
						+ rs.getString("Collection_Plant_City");
				JOptionPane.showMessageDialog(this, message);
			} else {
				JOptionPane.showMessageDialog(this, "Collector not found.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateProfile() {
		String[] options = { "Collector Name", "Collector Department", "Collection Plant Area",
				"Collection Plant City" };
		String choice = (String) JOptionPane.showInputDialog(this, "Select the field to update:", "Update Profile",
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (choice != null) {
			String newValue = JOptionPane.showInputDialog(this, "Enter new value for " + choice + ":");

			if (newValue != null && !newValue.trim().isEmpty()) {
				String columnToUpdate = "";
				switch (choice) {
				case "Collector Name":
					columnToUpdate = "Collector_Name";
					break;
				case "Collector Department":
					columnToUpdate = "Collector_Dept";
					break;
				case "Collection Plant Area":
					columnToUpdate = "Collection_Plant_Area";
					break;
				case "Collection Plant City":
					columnToUpdate = "Collection_Plant_City";
					break;
				}

				String query = "UPDATE Collection_plant SET " + columnToUpdate + " = ? WHERE Collector_ID = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(query)) {
					pstmt.setString(1, newValue);
					pstmt.setInt(2, collectorId);
					int rowsAffected = pstmt.executeUpdate();

					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Profile updated successfully.");
					} else {
						JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void deleteProfile() {
		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your profile?",
				"Confirm Deletion", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			String query = "DELETE FROM Collection_plant WHERE Collector_ID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setInt(1, collectorId);
				int rowsDeleted = pstmt.executeUpdate();
				if (rowsDeleted > 0) {
					JOptionPane.showMessageDialog(this, "Profile deleted successfully.");
					dispose();
					System.exit(0);
				} else {
					JOptionPane.showMessageDialog(this, "Deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// Show transactions for the collector
	private void showTransactions() {
		String query = "SELECT * FROM Transaction WHERE Collector_ID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, collectorId);
			ResultSet rs = pstmt.executeQuery();

			StringBuilder transactions = new StringBuilder("Transactions:\n");
			while (rs.next()) {
				transactions.append("Customer ID: ").append(rs.getInt("Customer_ID")).append(", ")
						.append("Collector ID: ").append(rs.getInt("Collector_ID")).append(", ").append("Date: ")
						.append(rs.getDate("Transaction_date")).append(", ").append("Status: ")
						.append(rs.getString("Status")).append(", ").append("Payment: ").append(rs.getDouble("Payment"))
						.append("\n");
			}

			JOptionPane.showMessageDialog(this, transactions.toString());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}