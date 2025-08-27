import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class Deduction extends JFrame {
    private JTextField employeeIdSearchField, employeeIdField, firstNameField, surnameField, 
                      dobField, departmentField, designationField, statusField, dateHiredField, 
                      jobTitleField, basicSalaryField, percentageField, amountField, reasonField;
    private JRadioButton percentageRadio, amountRadio;
    private JLabel totalDeductionLabel, salaryAfterDeductionLabel;
    private double totalDeduction = 0.0, salaryAfterDeduction = 0.0;
    
    public Deduction() {
        setTitle("Deduction Management");
        setSize(650, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeSearchSection();
        initializeEmployeeDetailsSection();
        initializeDeductionSection();
        initializeButtons();
        
        setVisible(true);
    }
    
    private void initializeSearchSection() {
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setBounds(20, 20, 100, 25);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(searchLabel);
        
        JLabel employeeIdSearchLabel = new JLabel("Employee ID:");
        employeeIdSearchLabel.setBounds(20, 50, 100, 25);
        add(employeeIdSearchLabel);
        
        employeeIdSearchField = new JTextField();
        employeeIdSearchField.setBounds(120, 50, 500, 25);
        employeeIdSearchField.addActionListener(e -> searchEmployee());
        add(employeeIdSearchField);
    }
    
    private void initializeEmployeeDetailsSection() {
        // Left side fields
        addField("Employee Id:", employeeIdField = new JTextField(), 20, 90, 120, 90, false);
        addField("First name:", firstNameField = new JTextField(), 20, 120, 120, 120, false);
        addField("Surname:", surnameField = new JTextField(), 20, 150, 120, 150, false);
        addField("Date of Birth:", dobField = new JTextField(), 20, 180, 120, 180, false);
        addField("Department:", departmentField = new JTextField(), 20, 210, 120, 210, false);
        
        // Right side fields
        addField("Designation:", designationField = new JTextField(), 300, 90, 400, 90, false);
        addField("Status:", statusField = new JTextField(), 300, 120, 400, 120, false);
        addField("Date Hired:", dateHiredField = new JTextField(), 300, 150, 400, 150, false);
        addField("Job Title:", jobTitleField = new JTextField(), 300, 180, 400, 180, false);
        addField("Basic Salary:", basicSalaryField = new JTextField(), 300, 210, 400, 210, false);
    }
    
    private void initializeDeductionSection() {
        JLabel updateSalaryLabel = new JLabel("Update Salary by:");
        updateSalaryLabel.setBounds(20, 250, 120, 25);
        add(updateSalaryLabel);
        
        percentageRadio = new JRadioButton("Percentage (%)");
        percentageRadio.setBounds(150, 250, 120, 25);
        add(percentageRadio);
        
        amountRadio = new JRadioButton("Amount");
        amountRadio.setBounds(280, 250, 100, 25);
        add(amountRadio);
        
        ButtonGroup deductionTypeGroup = new ButtonGroup();
        deductionTypeGroup.add(percentageRadio);
        deductionTypeGroup.add(amountRadio);
        percentageRadio.setSelected(true);
        
        addField("Percentage:", percentageField = new JTextField("0"), 20, 280, 120, 280, true);
        addField("Amount:", amountField = new JTextField("0"), 230, 280, 330, 280, true);
        addField("Reason:", reasonField = new JTextField(), 20, 310, 120, 310, true);
        
        percentageRadio.addActionListener(e -> {
            percentageField.setEnabled(true);
            amountField.setEnabled(false);
        });
        
        amountRadio.addActionListener(e -> {
            percentageField.setEnabled(false);
            amountField.setEnabled(true);
        });
        
        totalDeductionLabel = new JLabel("Total Deduction: 0.00");
        totalDeductionLabel.setBounds(450, 280, 180, 25);
        totalDeductionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(totalDeductionLabel);
        
        salaryAfterDeductionLabel = new JLabel("Salary after deduction: 0.00");
        salaryAfterDeductionLabel.setBounds(450, 310, 200, 25);
        salaryAfterDeductionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(salaryAfterDeductionLabel);
    }
    
    private void initializeButtons() {
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(120, 350, 100, 30);
        saveButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\save_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        saveButton.addActionListener(e -> saveDeduction());
        add(saveButton);
        
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBounds(230, 350, 100, 30);
        calculateButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\calculate_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        calculateButton.addActionListener(e -> calculateDeduction());
        add(calculateButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(340, 350, 100, 30);
        clearButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\clear_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        clearButton.addActionListener(e -> clearFields());
        add(clearButton);
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
                 "SELECT emp_id, first_name, surname, dob, department, designation, status, " +
                 "date_hired, job_title, basic_salary FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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
                
                // Enable deduction fields for editing
                percentageField.setEnabled(percentageRadio.isSelected());
                amountField.setEnabled(amountRadio.isSelected());
                reasonField.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found");
                clearFields();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void calculateDeduction() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        try {
            double basicSalary = Double.parseDouble(basicSalaryField.getText());
            double finalSalary = basicSalary;
            
            // Get total allowances
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT total_amount FROM allowances WHERE emp_id = ?")) {
                
                stmt.setString(1, employeeIdField.getText());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    finalSalary += rs.getDouble("total_amount");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            if (percentageRadio.isSelected()) {
                double percentage = Double.parseDouble(percentageField.getText());
                totalDeduction = (percentage / 100) * finalSalary;
            } else {
                totalDeduction = Double.parseDouble(amountField.getText());
            }
            
            salaryAfterDeduction = finalSalary - totalDeduction;
            
            DecimalFormat df = new DecimalFormat("0.00");
            totalDeductionLabel.setText("Total Deduction: " + df.format(totalDeduction));
            salaryAfterDeductionLabel.setText("Salary after deduction: " + df.format(salaryAfterDeduction));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields");
        }
    }
    
    private void saveDeduction() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        if (totalDeduction == 0) {
            JOptionPane.showMessageDialog(this, "Please calculate deduction first");
            return;
        }
        
        if (reasonField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason for the deduction");
            return;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO deductions (emp_id, deduction_type, percentage, amount, reason, total_deduction, salary_after_deduction) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, employeeIdField.getText());
            stmt.setString(2, percentageRadio.isSelected() ? "Percentage" : "Amount");
            stmt.setDouble(3, percentageRadio.isSelected() ? Double.parseDouble(percentageField.getText()) : 0);
            stmt.setDouble(4, amountRadio.isSelected() ? Double.parseDouble(amountField.getText()) : 0);
            stmt.setString(5, reasonField.getText());
            stmt.setDouble(6, totalDeduction);
            stmt.setDouble(7, salaryAfterDeduction);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Deduction saved successfully");
                
                // Remove the code that updates the employee's basic salary
                // We're now just recording the deduction without changing the base salary
                
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save deduction");
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
        departmentField.setText("");
        designationField.setText("");
        statusField.setText("");
        dateHiredField.setText("");
        jobTitleField.setText("");
        basicSalaryField.setText("");
        
        percentageRadio.setSelected(true);
        amountRadio.setSelected(false);
        percentageField.setText("0");
        amountField.setText("0");
        reasonField.setText("");
        
        totalDeduction = 0.0;
        salaryAfterDeduction = 0.0;
        totalDeductionLabel.setText("Total Deduction: 0.00");
        salaryAfterDeductionLabel.setText("Salary after deduction: 0.00");
        
        // Disable deduction fields until an employee is searched
        percentageField.setEnabled(true);
        amountField.setEnabled(false);
        reasonField.setEnabled(false);
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Deduction());
    }
}