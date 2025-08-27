import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.awt.print.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class PaySlip extends JFrame {
    private JTextField employeeIdSearchField;
    private JTextField employeeIdField;
    private JTextField firstNameField;
    private JTextField surnameField;
    private JTextField dobField;
    private JTextField departmentField;
    private JTextField designationField;
    private JTextField statusField;
    private JTextField dateHiredField;
    private JTextField jobTitleField;
    private JTextField basicSalaryField;
    
    public PaySlip() {
        initializeUI();
    }
    
    public PaySlip(String empId) {
        initializeUI();
        employeeIdSearchField.setText(empId);
        searchEmployee();
    }
    
    private void initializeUI() {
        setTitle("Pay Slip");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Search section
        JLabel employeeIdSearchLabel = new JLabel("Employee ID:");
        employeeIdSearchLabel.setBounds(20, 20, 100, 25);
        add(employeeIdSearchLabel);
        
        employeeIdSearchField = new JTextField();
        employeeIdSearchField.setBounds(120, 20, 350, 25);
        employeeIdSearchField.addActionListener(e -> searchEmployee());
        add(employeeIdSearchField);
        
        // Employee details section - left column
        JLabel employeeIdLabel = new JLabel("Employee Id:");
        employeeIdLabel.setBounds(20, 60, 100, 25);
        add(employeeIdLabel);
        employeeIdField = new JTextField();
        employeeIdField.setBounds(120, 60, 150, 25);
        employeeIdField.setEditable(false);
        add(employeeIdField);
        
        JLabel firstNameLabel = new JLabel("First name:");
        firstNameLabel.setBounds(20, 90, 100, 25);
        add(firstNameLabel);
        firstNameField = new JTextField();
        firstNameField.setBounds(120, 90, 150, 25);
        firstNameField.setEditable(false);
        add(firstNameField);
        
        JLabel surnameLabel = new JLabel("Surname:");
        surnameLabel.setBounds(20, 120, 100, 25);
        add(surnameLabel);
        surnameField = new JTextField();
        surnameField.setBounds(120, 120, 150, 25);
        surnameField.setEditable(false);
        add(surnameField);
        
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setBounds(20, 150, 100, 25);
        add(dobLabel);
        dobField = new JTextField();
        dobField.setBounds(120, 150, 150, 25);
        dobField.setEditable(false);
        add(dobField);
        
        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setBounds(20, 180, 100, 25);
        add(departmentLabel);
        departmentField = new JTextField();
        departmentField.setBounds(120, 180, 150, 25);
        departmentField.setEditable(false);
        add(departmentField);
        
        // Employee details section - right column
        JLabel designationLabel = new JLabel("Designation:");
        designationLabel.setBounds(300, 60, 100, 25);
        add(designationLabel);
        designationField = new JTextField();
        designationField.setBounds(400, 60, 150, 25);
        designationField.setEditable(false);
        add(designationField);
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(300, 90, 100, 25);
        add(statusLabel);
        statusField = new JTextField();
        statusField.setBounds(400, 90, 150, 25);
        statusField.setEditable(false);
        add(statusField);
        
        JLabel dateHiredLabel = new JLabel("Date Hired:");
        dateHiredLabel.setBounds(300, 120, 100, 25);
        add(dateHiredLabel);
        dateHiredField = new JTextField();
        dateHiredField.setBounds(400, 120, 150, 25);
        dateHiredField.setEditable(false);
        add(dateHiredField);
        
        JLabel jobTitleLabel = new JLabel("Job Title:");
        jobTitleLabel.setBounds(300, 150, 100, 25);
        add(jobTitleLabel);
        jobTitleField = new JTextField();
        jobTitleField.setBounds(400, 150, 150, 25);
        jobTitleField.setEditable(false);
        add(jobTitleField);
        
        JLabel basicSalaryLabel = new JLabel("Basic Salary:");
        basicSalaryLabel.setBounds(300, 180, 100, 25);
        add(basicSalaryLabel);
        basicSalaryField = new JTextField();
        basicSalaryField.setBounds(400, 180, 150, 25);
        basicSalaryField.setEditable(false);
        add(basicSalaryField);
        
        // Generate Slip button
        JButton generateSlipButton = new JButton("Generate Slip");
        generateSlipButton.setBounds(400, 250, 150, 40);
        generateSlipButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\payslip_icon.png")
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        generateSlipButton.addActionListener(e -> generatePaySlip());
        add(generateSlipButton);
        
        // Status bar
        JLabel loggedInLabel = new JLabel("Logged in As : 1");
        loggedInLabel.setBounds(10, 330, 150, 25);
        add(loggedInLabel);
        
        setVisible(true);
    }
    
    private void searchEmployee() {
        String empId = employeeIdSearchField.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID");
            return;
        }
        
        System.out.println("Searching for employee ID: " + empId);
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Employee found: " + rs.getString("first_name") + " " + rs.getString("surname"));
                
                employeeIdField.setText(rs.getString("emp_id"));
                firstNameField.setText(rs.getString("first_name"));
                surnameField.setText(rs.getString("surname"));
                dobField.setText(rs.getString("dob"));
                departmentField.setText(rs.getString("department"));
                designationField.setText(rs.getString("designation"));
                statusField.setText(rs.getString("status"));
                dateHiredField.setText(rs.getString("date_hired"));
                jobTitleField.setText(rs.getString("job_title"));
                basicSalaryField.setText(rs.getString("basic_salary"));
                
                // After loading employee data, check for their deductions
                System.out.println("Now checking for deductions...");
                
                // Verify the deductions table structure
                checkDeductionsTable(conn, empId);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found");
                System.out.println("No employee found with ID: " + empId);
                clearFields();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error searching for employee: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void checkDeductionsTable(Connection conn, String empId) {
        try {
            // Check if the deductions table exists and has the expected columns
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "deductions", null);
            
            if (!tables.next()) {
                System.out.println("WARNING: deductions table does not exist!");
                return;
            }
            
            System.out.println("Deductions table exists. Checking columns...");
            ResultSet columns = metaData.getColumns(null, null, "deductions", null);
            
            System.out.println("Columns in deductions table:");
            while (columns.next()) {
                System.out.println("- " + columns.getString("COLUMN_NAME") + 
                                  " (" + columns.getString("TYPE_NAME") + ")");
            }
            
            // Check if there are any deductions for this employee
            String checkSql = "SELECT COUNT(*) FROM deductions WHERE emp_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, empId);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                int count = checkRs.getInt(1);
                System.out.println("Found " + count + " deduction records for employee ID: " + empId);
                
                if (count > 0) {
                    // Print a sample record
                    String sampleSql = "SELECT * FROM deductions WHERE emp_id = ? LIMIT 1";
                    PreparedStatement sampleStmt = conn.prepareStatement(sampleSql);
                    sampleStmt.setString(1, empId);
                    ResultSet sampleRs = sampleStmt.executeQuery();
                    
                    if (sampleRs.next()) {
                        System.out.println("Sample deduction record:");
                        ResultSetMetaData rsmd = sampleRs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = rsmd.getColumnName(i);
                            String value = sampleRs.getString(i);
                            System.out.println("  " + columnName + ": " + value);
                        }
                    }
                }
            }
            
        } catch (SQLException ex) {
            System.out.println("Error checking deductions table: " + ex.getMessage());
        }
    }
    
    private void generatePaySlip() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        // Create a printable pay slip panel
        JPanel paySlipPanel = new JPanel();
        paySlipPanel.setLayout(new BoxLayout(paySlipPanel, BoxLayout.Y_AXIS));
        paySlipPanel.setBackground(Color.WHITE);
        
        // Add company header
        JLabel companyLabel = new JLabel("BVRIT");
        companyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        companyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paySlipPanel.add(companyLabel);
        
        JLabel paySlipLabel = new JLabel("Employee Pay Slip");
        paySlipLabel.setFont(new Font("Arial", Font.BOLD, 14));
        paySlipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paySlipPanel.add(paySlipLabel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(new JLabel("Generated: " + dateFormat.format(new Date())));
        datePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        paySlipPanel.add(datePanel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add employee details
        JPanel detailsPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        
        addDetailRow(detailsPanel, "Employee ID:", employeeIdField.getText());
        addDetailRow(detailsPanel, "Name:", firstNameField.getText() + " " + surnameField.getText());
        addDetailRow(detailsPanel, "Date of Birth:", dobField.getText());
        addDetailRow(detailsPanel, "Department:", departmentField.getText());
        addDetailRow(detailsPanel, "Designation:", designationField.getText());
        addDetailRow(detailsPanel, "Status:", statusField.getText());
        addDetailRow(detailsPanel, "Date Hired:", dateHiredField.getText());
        addDetailRow(detailsPanel, "Job Title:", jobTitleField.getText());
        
        paySlipPanel.add(detailsPanel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Fetch all allowances and deductions with details
        List<PayrollItem> allowances = fetchAllowanceDetails(employeeIdField.getText());
        List<PayrollItem> deductions = fetchDeductionDetails(employeeIdField.getText());
        
        // Calculate totals
        double basicSalary = Double.parseDouble(basicSalaryField.getText());
        double totalAllowances = getTotalAllowance(employeeIdField.getText());
        
        double totalDeductions = 0;
        for (PayrollItem item : deductions) {
            totalDeductions += item.amount;
        }
        
        // Calculate net salary
        double netSalary = basicSalary + totalAllowances - totalDeductions;
        
        System.out.println("Pay slip calculation:");
        System.out.println("Basic Salary: " + basicSalary);
        System.out.println("Total Allowances: " + totalAllowances);
        System.out.println("Total Deductions: " + totalDeductions);
        System.out.println("Net Salary: " + netSalary);
        
        // Add allowances details
        JPanel allowancesPanel = new JPanel(new BorderLayout());
        allowancesPanel.setBackground(Color.WHITE);
        allowancesPanel.setBorder(BorderFactory.createTitledBorder("Allowances"));
        
        String[] allowanceColumns = {"Description", "Amount"};
        Object[][] allowanceData = new Object[allowances.size() + 1][2];
        
        for (int i = 0; i < allowances.size(); i++) {
            PayrollItem item = allowances.get(i);
            allowanceData[i][0] = item.description;
            allowanceData[i][1] = String.format("$%.2f", item.amount);
        }
        allowanceData[allowances.size()][0] = "Total Allowances";
        allowanceData[allowances.size()][1] = String.format("$%.2f", totalAllowances);
        
        JTable allowanceTable = new JTable(allowanceData, allowanceColumns);
        allowanceTable.setEnabled(false);
        allowanceTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        allowanceTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        
        allowancesPanel.add(new JScrollPane(allowanceTable), BorderLayout.CENTER);
        allowancesPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 
                                      (allowances.size() + 2) * allowanceTable.getRowHeight() + 50));
        paySlipPanel.add(allowancesPanel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add deductions details
        JPanel deductionsPanel = new JPanel(new BorderLayout());
        deductionsPanel.setBackground(Color.WHITE);
        deductionsPanel.setBorder(BorderFactory.createTitledBorder("Deductions"));
        
        String[] deductionColumns = {"Description", "Amount"};
        Object[][] deductionData = new Object[deductions.size() + 1][2];
        
        for (int i = 0; i < deductions.size(); i++) {
            PayrollItem item = deductions.get(i);
            deductionData[i][0] = item.description;
            deductionData[i][1] = String.format("$%.2f", item.amount);
        }
        deductionData[deductions.size()][0] = "Total Deductions";
        deductionData[deductions.size()][1] = String.format("$%.2f", totalDeductions);
        
        JTable deductionTable = new JTable(deductionData, deductionColumns);
        deductionTable.setEnabled(false);
        deductionTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        deductionTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        
        deductionsPanel.add(new JScrollPane(deductionTable), BorderLayout.CENTER);
        deductionsPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 
                                     (deductions.size() + 2) * deductionTable.getRowHeight() + 50));
        paySlipPanel.add(deductionsPanel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Add salary summary
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Salary Summary"));
        
        addDetailRow(summaryPanel, "Basic Salary:", String.format("$%.2f", basicSalary));
        addDetailRow(summaryPanel, "Total Allowances:", String.format("$%.2f", totalAllowances));
        addDetailRow(summaryPanel, "Total Deductions:", String.format("$%.2f", totalDeductions));
        addDetailRow(summaryPanel, "Net Salary:", String.format("$%.2f", netSalary));
        
        paySlipPanel.add(summaryPanel);
        
        // Add net salary with bold font
        JPanel netSalaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        netSalaryPanel.setBackground(Color.WHITE);
        JLabel netSalaryLabel = new JLabel("Final Take-Home: " + String.format("$%.2f", netSalary));
        netSalaryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        netSalaryPanel.add(netSalaryLabel);
        paySlipPanel.add(netSalaryPanel);
        
        paySlipPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Add signature lines
        JPanel signaturePanel = new JPanel(new GridLayout(1, 2, 50, 0));
        signaturePanel.setBackground(Color.WHITE);
        
        JPanel employeeSignPanel = new JPanel(new BorderLayout());
        employeeSignPanel.setBackground(Color.WHITE);
        employeeSignPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.NORTH);
        JLabel employeeSignLabel = new JLabel("Employee Signature", JLabel.CENTER);
        employeeSignPanel.add(employeeSignLabel, BorderLayout.CENTER);
        
        
        
        signaturePanel.add(employeeSignPanel);
        
        
        paySlipPanel.add(signaturePanel);
        
        // Create a new frame to display the pay slip
        JFrame paySlipFrame = new JFrame("Pay Slip");
        paySlipFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        paySlipFrame.setSize(600, 700);
        paySlipFrame.setLocationRelativeTo(this);
        
        JScrollPane scrollPane = new JScrollPane(paySlipPanel);
        paySlipFrame.add(scrollPane);
        
        // Add print button
        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> printPaySlip(paySlipPanel));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(printButton);
        paySlipFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        paySlipFrame.setVisible(true);
    }
    
    // Helper class to store payroll items
    private class PayrollItem {
        String description;
        double amount;
        double percentage;
        String reason;
        
        PayrollItem(String description, double amount) {
            this.description = description;
            this.amount = amount;
            this.percentage = 0;
            this.reason = "";
        }
        
        PayrollItem(String description, double amount, double percentage, String reason) {
            this.description = description;
            this.amount = amount;
            this.percentage = percentage;
            this.reason = reason != null ? reason : "";
        }
    }
    
    private List<PayrollItem> fetchAllowanceDetails(String empId) {
        List<PayrollItem> allowances = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT overtime, medical, bonus, other, total_amount FROM allowances WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            boolean hasData = false;
            if (rs.next()) {
                hasData = true;
                // Add individual allowances if they are positive
                if (rs.getDouble("overtime") > 0) {
                    allowances.add(new PayrollItem("Overtime", rs.getDouble("overtime")));
                }
                if (rs.getDouble("medical") > 0) {
                    allowances.add(new PayrollItem("Medical", rs.getDouble("medical")));
                }
                if (rs.getDouble("bonus") > 0) {
                    allowances.add(new PayrollItem("Bonus", rs.getDouble("bonus")));
                }
                if (rs.getDouble("other") > 0) {
                    allowances.add(new PayrollItem("Other", rs.getDouble("other")));
                }
            }
            
            // If no data found in database, add default allowances
            if (!hasData) {
                allowances.add(new PayrollItem("Overtime", 0.00));
                allowances.add(new PayrollItem("Medical", 0.00));
                allowances.add(new PayrollItem("Bonus", 0.00));
                allowances.add(new PayrollItem("Other", 0.00));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching allowances: " + ex.getMessage());
        }
        
        return allowances;
    }
    
    private double getTotalAllowance(String empId) {
        double total = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT total_amount FROM allowances WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                total = rs.getDouble("total_amount");
                System.out.println("Retrieved total allowance from database: " + total);
            } else {
                System.out.println("No allowance record found for employee: " + empId);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error getting total allowance: " + ex.getMessage());
        }
        return total;
    }
    
    private List<PayrollItem> fetchDeductionDetails(String empId) {
        List<PayrollItem> deductions = new ArrayList<>();
        
        String sql = "SELECT deduction_type, amount, reason, percentage FROM deductions WHERE emp_id = ?";
        System.out.println("SQL query: " + sql + " with emp_id = " + empId);
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, empId);
            System.out.println("Executing query for employee ID: " + empId);
            
            // Get the basic salary for percentage calculations
            double basicSalary = 0.0;
            try {
                basicSalary = Double.parseDouble(basicSalaryField.getText());
                System.out.println("Basic salary for percentage calculations: " + basicSalary);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing basic salary: " + e.getMessage());
            }
            
            ResultSet rs = stmt.executeQuery();
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String description = rs.getString("deduction_type");
                double amount = rs.getDouble("amount");
                String reason = rs.getString("reason");
                double percentage = rs.getDouble("percentage");
                
                // If amount is zero but percentage is set, calculate the amount
                if (amount == 0 && percentage > 0 && basicSalary > 0) {
                    amount = (percentage / 100) * basicSalary;
                    System.out.println("Calculated amount from percentage: " + amount + 
                                     " (" + percentage + "% of " + basicSalary + ")");
                }
                
                System.out.println("Found deduction: " + description + ", amount: " + amount + 
                                 ", percentage: " + percentage);
                
                // Add percentage to description if available
                if (percentage > 0) {
                    description = description + " (" + percentage + "%)";
                }
                // Add reason to description if available
                if (reason != null && !reason.trim().isEmpty()) {
                    description = description + " - " + reason;
                }
                deductions.add(new PayrollItem(description, amount, percentage, reason));
            }
            
            // If no data found in database, add a placeholder
            if (!hasData) {
                System.out.println("No deductions found for employee ID: " + empId);
                deductions.add(new PayrollItem("No deductions found", 0.00, 0, ""));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching deductions: " + ex.getMessage());
            deductions.add(new PayrollItem("Error fetching deductions", 0.00, 0, ""));
        }
        
        return deductions;
    }
    
    private double getSalaryAfterDeduction(String empId) {
        double salaryAfterDeduction = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT salary_after_deduction FROM deductions WHERE emp_id = ? ORDER BY created_at DESC LIMIT 1")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                salaryAfterDeduction = rs.getDouble("salary_after_deduction");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return salaryAfterDeduction;
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(labelComponent);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueComponent);
    }
    
    private void printPaySlip(JPanel panel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print Pay Slip");
        
        job.setPrintable(new Printable() {
            public int print(Graphics g, PageFormat pf, int pageIndex) {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                
                // Scale to fit the page
                double scaleX = pf.getImageableWidth() / panel.getWidth();
                double scaleY = pf.getImageableHeight() / panel.getHeight();
                double scale = Math.min(scaleX, scaleY);
                
                g2d.scale(scale, scale);
                
                panel.print(g2d);
                
                return Printable.PAGE_EXISTS;
            }
        });
        
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Pay slip printed successfully!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage());
            }
        }
    }
    
    private void clearFields() {
        employeeIdField.setText("");
        firstNameField.setText("");
        surnameField.setText("");
        dobField.setText("");
        departmentField.setText("");
        designationField.setText("");
        statusField.setText("");
        dateHiredField.setText("");
        jobTitleField.setText("");
        basicSalaryField.setText("");
    }
    
    private Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to database...");
            
            // Attempt to connect to the database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
            System.out.println("Database connection successful");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            throw e;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaySlip());
    }
}