import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateSalary extends JFrame {
    private JTextField employeeIdSearchField, employeeIdField, firstNameField, surnameField, 
                      dobField, basicSalaryField, departmentField, percentageField, amountField;
    private JRadioButton percentageRadio, amountRadio;
    
    public UpdateSalary() {
        initializeUI();
    }
    
    public UpdateSalary(String empId) {
        initializeUI();
        employeeIdSearchField.setText(empId);
        searchEmployee();
    }
    
    private void initializeUI() {
        setTitle("Update Salary");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeSearchSection();
        initializeEmployeeDetailsSection();
        initializeSalaryUpdateSection();
        setVisible(true);
    }
    
    private void initializeSearchSection() {
        JLabel employeeIdSearchLabel = new JLabel("Employee ID:");
        employeeIdSearchLabel.setBounds(20, 20, 100, 25);
        add(employeeIdSearchLabel);
        
        employeeIdSearchField = new JTextField();
        employeeIdSearchField.setBounds(120, 20, 350, 25);
        employeeIdSearchField.addActionListener(e -> searchEmployee());
        add(employeeIdSearchField);
    }
    
    private void initializeEmployeeDetailsSection() {
        // Left column
        addField("Employee Id:", employeeIdField = new JTextField(), 20, 60, 120, 60, false);
        addField("First name:", firstNameField = new JTextField(), 20, 90, 120, 90, false);
        addField("Surname:", surnameField = new JTextField(), 20, 120, 120, 120, false);
        
        // Right column
        addField("Date of Birth:", dobField = new JTextField(), 300, 60, 400, 60, false);
        addField("Basic Salary:", basicSalaryField = new JTextField(), 300, 90, 400, 90, false);
        addField("Department:", departmentField = new JTextField(), 300, 120, 400, 120, false);
    }
    
    private void initializeSalaryUpdateSection() {
        JLabel updateSalaryLabel = new JLabel("Update Salary by:");
        updateSalaryLabel.setBounds(20, 170, 120, 25);
        add(updateSalaryLabel);
        
        percentageRadio = new JRadioButton("Percentage (%)");
        percentageRadio.setBounds(140, 170, 120, 25);
        percentageRadio.addActionListener(e -> {
            percentageField.setEnabled(true);
            amountField.setEnabled(false);
            amountField.setText("");
        });
        add(percentageRadio);
        
        amountRadio = new JRadioButton("Amount");
        amountRadio.setBounds(270, 170, 80, 25);
        amountRadio.setSelected(true);
        amountRadio.addActionListener(e -> {
            percentageField.setEnabled(false);
            amountField.setEnabled(true);
            percentageField.setText("");
        });
        add(amountRadio);
        
        ButtonGroup updateTypeGroup = new ButtonGroup();
        updateTypeGroup.add(percentageRadio);
        updateTypeGroup.add(amountRadio);
        
        addField("Percentage:", percentageField = new JTextField(), 20, 200, 120, 200, false);
        addField("Amount:", amountField = new JTextField(), 250, 200, 350, 200, true);
        
        JButton updateButton = new JButton("Update");
        updateButton.setBounds(450, 250, 100, 30);
        updateButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\save_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        updateButton.addActionListener(e -> updateSalary());
        add(updateButton);
    }
    
    private void addField(String labelText, JTextField field, int labelX, int labelY, int fieldX, int fieldY, boolean editable) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, labelY, 100, 25);
        add(label);
        field.setBounds(fieldX, fieldY, 150, 25);
        field.setEditable(editable);
        add(field);
    }
    
    private void searchEmployee() {
        String empId = employeeIdSearchField.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID");
            return;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                employeeIdField.setText(rs.getString("emp_id"));
                firstNameField.setText(rs.getString("first_name"));
                surnameField.setText(rs.getString("surname"));
                dobField.setText(rs.getString("dob"));
                basicSalaryField.setText(rs.getString("basic_salary"));
                departmentField.setText(rs.getString("department"));
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found");
                clearFields();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void updateSalary() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        double currentSalary;
        try {
            currentSalary = Double.parseDouble(basicSalaryField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid current salary");
            return;
        }
        
        double newSalary = currentSalary;
        
        if (percentageRadio.isSelected()) {
            try {
                double percentage = Double.parseDouble(percentageField.getText());
                newSalary = currentSalary * (1 + percentage / 100);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid percentage");
                return;
            }
        } else if (amountRadio.isSelected()) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                newSalary = currentSalary + amount;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount");
                return;
            }
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE employees SET basic_salary = ? WHERE emp_id = ?")) {
            
            stmt.setDouble(1, newSalary);
            stmt.setString(2, employeeIdField.getText());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Salary updated successfully");
                basicSalaryField.setText(String.valueOf(newSalary));
                percentageField.setText("");
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update salary");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        employeeIdField.setText("");
        firstNameField.setText("");
        surnameField.setText("");
        dobField.setText("");
        basicSalaryField.setText("");
        departmentField.setText("");
        percentageField.setText("");
        amountField.setText("");
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdateSalary());
    }
}