import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;

public class EmployeeDashboard extends JFrame {
    private String employeeId;
    private JLabel nameLabel, departmentLabel, designationLabel, basicSalaryLabel, finalSalaryLabel;
    private JLabel dobLabel, genderLabel, emailLabel, contactLabel, addressLine1Label, addressLine2Label;
    private JLabel photoLabel;
    private JTable allowanceTable, deductionTable;
    private DefaultTableModel allowanceModel, deductionModel;
    
    public EmployeeDashboard(String employeeId) {
        this.employeeId = employeeId;
        setTitle("Employee Dashboard");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeComponents();
        loadEmployeeData();
        loadAllowances();
        loadDeductions();
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Employee Details Panel
        JPanel detailsPanel = createDetailsPanel();
        mainPanel.add(detailsPanel, BorderLayout.NORTH);
        
        // Create tabbed pane for allowances and deductions
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Allowance Tab
        JPanel allowancePanel = createAllowancePanel();
        tabbedPane.addTab("Allowances", allowancePanel);
        
        // Deduction Tab
        JPanel deductionPanel = createDeductionPanel();
        tabbedPane.addTab("Deductions", deductionPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        
        // Left panel for photo
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        panel.add(photoPanel, BorderLayout.WEST);
        
        // Right panel for details
        JPanel detailsPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        
        // Create labels
        nameLabel = new JLabel("Name: ");
        departmentLabel = new JLabel("Department: ");
        designationLabel = new JLabel("Designation: ");
        basicSalaryLabel = new JLabel("Basic Salary: ");
        finalSalaryLabel = new JLabel("Final Salary: ");
        dobLabel = new JLabel("Date of Birth: ");
        genderLabel = new JLabel("Gender: ");
        emailLabel = new JLabel("Email: ");
        contactLabel = new JLabel("Contact: ");
        addressLine1Label = new JLabel("Address Line 1: ");
        addressLine2Label = new JLabel("Address Line 2: ");
        
        // Add labels to panel
        detailsPanel.add(new JLabel("Employee ID: " + employeeId));
        detailsPanel.add(nameLabel);
        detailsPanel.add(departmentLabel);
        detailsPanel.add(designationLabel);
        detailsPanel.add(basicSalaryLabel);
        detailsPanel.add(finalSalaryLabel);
        detailsPanel.add(dobLabel);
        detailsPanel.add(genderLabel);
        detailsPanel.add(emailLabel);
        detailsPanel.add(contactLabel);
        detailsPanel.add(addressLine1Label);
        detailsPanel.add(addressLine2Label);
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAllowancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Type", "Amount", "Date"};
        allowanceModel = new DefaultTableModel(columns, 0);
        allowanceTable = new JTable(allowanceModel);
        
        panel.add(new JScrollPane(allowanceTable), BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createDeductionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Type", "Amount", "Reason", "Date"};
        deductionModel = new DefaultTableModel(columns, 0);
        deductionTable = new JTable(deductionModel);
        
        panel.add(new JScrollPane(deductionTable), BorderLayout.CENTER);
        return panel;
    }
    
    private void loadEmployeeData() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("first_name") + " " + rs.getString("surname"));
                departmentLabel.setText("Department: " + rs.getString("department"));
                designationLabel.setText("Designation: " + rs.getString("designation"));
                double basicSalary = rs.getDouble("basic_salary");
                basicSalaryLabel.setText("Basic Salary: $" + String.format("%.2f", basicSalary));
                dobLabel.setText("Date of Birth: " + rs.getString("dob"));
                genderLabel.setText("Gender: " + rs.getString("gender"));
                emailLabel.setText("Email: " + rs.getString("email"));
                contactLabel.setText("Contact: " + rs.getString("contact"));
                addressLine1Label.setText("Address Line 1: " + rs.getString("address_line1"));
                addressLine2Label.setText("Address Line 2: " + rs.getString("address_line2"));
                
                // Calculate and display final salary in bold
                double finalSalary = calculateFinalSalary(basicSalary);
                finalSalaryLabel.setText("<html><b>Final Salary: $" + String.format("%.2f", finalSalary) + "</b></html>");
                
                // Load and display photo
                String photoPath = rs.getString("photo");
                if (photoPath != null && !photoPath.isEmpty()) {
                    try {
                        ImageIcon originalIcon = new ImageIcon(photoPath);
                        Image scaledImage = originalIcon.getImage().getScaledInstance(
                            200, 200, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(scaledImage));
                    } catch (Exception ex) {
                        photoLabel.setText("No Photo");
                        ex.printStackTrace();
                    }
                } else {
                    photoLabel.setText("No Photo");
                }
            }
        } catch (SQLException ex) {
            showError("Error loading employee data", ex);
        }
    }
    
    private double calculateFinalSalary(double basicSalary) {
        double totalAllowance = 0;
        double totalDeduction = 0;
        
        try (Connection conn = getConnection()) {
            // Get total allowance from allowances table
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT total_amount FROM allowances WHERE emp_id = ?")) {
                stmt.setString(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalAllowance = rs.getDouble("total_amount");
                    System.out.println("Retrieved total allowance from database: " + totalAllowance);
                }
            }
            
            // Calculate total deductions
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT amount, percentage FROM deductions WHERE emp_id = ?")) {
                stmt.setString(1, employeeId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    double amount = rs.getDouble("amount");
                    double percentage = rs.getDouble("percentage");
                    
                    // If amount is zero but percentage is set, calculate the amount
                    if (amount == 0 && percentage > 0) {
                        amount = (percentage / 100) * basicSalary;
                    }
                    
                    if (amount > 0) {  // Only add positive amounts
                        totalDeduction += amount;
                        System.out.println("Adding deduction: " + amount + " (percentage: " + percentage + "%)");
                    }
                }
            }
        } catch (SQLException ex) {
            showError("Error calculating final salary", ex);
        }
        
        double finalSalary = basicSalary + totalAllowance - totalDeduction;
        System.out.println("Final salary calculation:");
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("Total Allowance: " + totalAllowance);
        System.out.println("Total Deduction: " + totalDeduction);
        System.out.println("Final Salary: " + finalSalary);
        
        return finalSalary;
    }
    
    private void loadAllowances() {
        allowanceModel.setRowCount(0);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT overtime, medical, bonus, other, created_at " +
                 "FROM allowances WHERE emp_id = ?")) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                if (rs.getDouble("overtime") > 0) {
                    allowanceModel.addRow(new Object[]{"Overtime", rs.getDouble("overtime"), rs.getString("created_at")});
                }
                if (rs.getDouble("medical") > 0) {
                    allowanceModel.addRow(new Object[]{"Medical", rs.getDouble("medical"), rs.getString("created_at")});
                }
                if (rs.getDouble("bonus") > 0) {
                    allowanceModel.addRow(new Object[]{"Bonus", rs.getDouble("bonus"), rs.getString("created_at")});
                }
                if (rs.getDouble("other") > 0) {
                    allowanceModel.addRow(new Object[]{"Other", rs.getDouble("other"), rs.getString("created_at")});
                }
            }
        } catch (SQLException ex) {
            showError("Error loading allowances", ex);
        }
    }
    
    private void loadDeductions() {
        deductionModel.setRowCount(0);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT deduction_type, amount, reason, created_at " +
                 "FROM deductions WHERE emp_id = ?")) {
            
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                double amount = rs.getDouble("amount");
                if (amount > 0) {  // Only add positive amounts
                    deductionModel.addRow(new Object[]{
                        rs.getString("deduction_type"),
                        amount,
                        rs.getString("reason"),
                        rs.getString("created_at")
                    });
                }
            }
        } catch (SQLException ex) {
            showError("Error loading deductions", ex);
        }
    }
    
    private void showError(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage());
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
} 