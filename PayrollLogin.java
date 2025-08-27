import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PayrollLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    public PayrollLogin() {
        setTitle("Payroll Login");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Title
        JLabel title = new JLabel("Please enter your username and password");
        title.setBounds(150, 40, 400, 30);
        add(title);

        // Username
        add(new JLabel("Username:")).setBounds(150, 90, 100, 25);
        usernameField = new JTextField();
        usernameField.setBounds(250, 90, 200, 25);
        add(usernameField);

        // Password
        add(new JLabel("Password:")).setBounds(150, 130, 100, 25);
        passwordField = new JPasswordField();
        passwordField.setBounds(250, 130, 200, 25);
        add(passwordField);

        // Role
        add(new JLabel("Select Position:")).setBounds(150, 170, 100, 25);
        roleBox = new JComboBox<>(new String[]{"Admin", "HR", "Employee"});
        roleBox.setBounds(250, 170, 200, 25);
        add(roleBox);

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(250, 220, 90, 30);
        add(loginBtn);
        
        loginBtn.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        
        if (role.equals("Employee")) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM employees WHERE emp_id = ? AND password = ?")) {
                
                stmt.setString(1, username);
                stmt.setString(2, password);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();
                    new EmployeeDashboard(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid employee credentials!");
                }
            } catch (SQLException ex) {
                showError("Error during login", ex);
            }
        } else if (role.equals("HR") && username.equals("hr") && password.equals("hr123")) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new HRDashboard();
        } else if (role.equals("Admin") && username.equals("admin") && password.equals("admin123")) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            new AdminDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid " + role + " credentials!");
        }
    }
    
    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage());
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PayrollLogin());
    }
}
