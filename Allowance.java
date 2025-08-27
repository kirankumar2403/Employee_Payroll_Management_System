import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

public class Allowance extends JFrame {
    private JTextField employeeIdSearchField, employeeIdField, firstNameField, surnameField, 
                      dobField, basicSalaryField, departmentField, overtimeField, totalOvertimeField,
                      medicalField, ratePerHourField, bonusField, otherField;
    private JTable allowanceTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;
    private double totalAmount = 0.0;
    
    public Allowance() {
        setTitle("Allowance Management");
        setSize(650, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeSearchSection();
        initializeEmployeeDetailsSection();
        initializeAllowanceSection();
        initializeTable();
        initializeButtons();
        
        loadAllowanceData();
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
        addField("Employee Id:", employeeIdField = new JTextField(), 20, 90, 120, 90, false);
        addField("First name:", firstNameField = new JTextField(), 20, 120, 120, 120, false);
        addField("Surname:", surnameField = new JTextField(), 20, 150, 120, 150, false);
        addField("Date of Birth:", dobField = new JTextField(), 20, 180, 120, 180, false);
        addField("Basic Salary:", basicSalaryField = new JTextField(), 20, 210, 120, 210, false);
        addField("Department:", departmentField = new JTextField(), 20, 240, 120, 240, false);
    }
    
    private void initializeAllowanceSection() {
        JLabel amountsLabel = new JLabel("Please enter the amounts");
        amountsLabel.setBounds(400, 90, 200, 25);
        add(amountsLabel);
        
        addField("Overtime:", overtimeField = new JTextField("0"), 300, 120, 400, 120, true);
        addField("Total Overtime:", totalOvertimeField = new JTextField("0"), 490, 120, 580, 120, false);
        addField("Medical:", medicalField = new JTextField("0"), 300, 150, 400, 150, true);
        addField("Rate Per Hour:", ratePerHourField = new JTextField("0"), 490, 150, 580, 150, true);
        addField("Bonus:", bonusField = new JTextField("0"), 300, 180, 400, 180, true);
        addField("Other:", otherField = new JTextField("0"), 300, 210, 400, 210, true);
        
        totalAmountLabel = new JLabel("Total Amount : 0.00");
        totalAmountLabel.setBounds(450, 410, 180, 25);
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(totalAmountLabel);
    }
    
    private void initializeTable() {
        String[] columns = {"id", "ovt", "med", "bonus", "other", "emp", "salary", "rate", "total", "first", "surn", "dept"};
        tableModel = new DefaultTableModel(columns, 0);
        allowanceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(allowanceTable);
        scrollPane.setBounds(20, 280, 610, 120);
        add(scrollPane);
    }
    
    private void initializeButtons() {
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(450, 440, 80, 25);
        saveButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\save_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        saveButton.addActionListener(e -> saveAllowance());
        add(saveButton);
        
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBounds(450, 470, 80, 25);
        calculateButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\calculate_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        calculateButton.addActionListener(e -> calculateAllowance());
        add(calculateButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(540, 440, 80, 25);
        clearButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\clear_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        clearButton.addActionListener(e -> clearFields());
        add(clearButton);
    }
    
    private void addField(String labelText, JTextField field, int labelX, int labelY, int fieldX, int fieldY, boolean editable) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, labelY, 100, 25);
        add(label);
        field.setBounds(fieldX, fieldY, 80, 25);
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
                 "SELECT emp_id, first_name, surname, dob, basic_salary, department FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                employeeIdField.setText(rs.getString("emp_id"));
                firstNameField.setText(rs.getString("first_name"));
                surnameField.setText(rs.getString("surname"));
                dobField.setText(rs.getString("dob"));
                basicSalaryField.setText(rs.getString("basic_salary"));
                departmentField.setText(rs.getString("department"));
                
                // Enable allowance fields for editing
                overtimeField.setEditable(true);
                medicalField.setEditable(true);
                bonusField.setEditable(true);
                otherField.setEditable(true);
                ratePerHourField.setEditable(true);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found");
                clearFields();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void calculateAllowance() {
        try {
            double overtime = Double.parseDouble(overtimeField.getText());
            double ratePerHour = Double.parseDouble(ratePerHourField.getText());
            double medical = Double.parseDouble(medicalField.getText());
            double bonus = Double.parseDouble(bonusField.getText());
            double other = Double.parseDouble(otherField.getText());
            
            double totalOvertime = overtime * ratePerHour;
            totalOvertimeField.setText(String.valueOf(totalOvertime));
            
            totalAmount = totalOvertime + medical + bonus + other;
            
            DecimalFormat df = new DecimalFormat("0.00");
            totalAmountLabel.setText("Total Amount : " + df.format(totalAmount));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields");
        }
    }
    
    private void saveAllowance() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        if (totalAmount == 0) {
            JOptionPane.showMessageDialog(this, "Please calculate allowance first");
            return;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO allowances (emp_id, overtime, medical, bonus, other, rate_per_hour, total_overtime, total_amount) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, employeeIdField.getText());
            stmt.setDouble(2, Double.parseDouble(overtimeField.getText()));
            stmt.setDouble(3, Double.parseDouble(medicalField.getText()));
            stmt.setDouble(4, Double.parseDouble(bonusField.getText()));
            stmt.setDouble(5, Double.parseDouble(otherField.getText()));
            stmt.setDouble(6, Double.parseDouble(ratePerHourField.getText()));
            stmt.setDouble(7, Double.parseDouble(totalOvertimeField.getText()));
            stmt.setDouble(8, totalAmount);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Allowance saved successfully");
                loadAllowanceData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save allowance");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void loadAllowanceData() {
        tableModel.setRowCount(0);
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.id, a.overtime, a.medical, a.bonus, a.other, a.emp_id, " +
                 "e.basic_salary, a.rate_per_hour, a.total_amount, e.first_name, e.surname, e.department " +
                 "FROM allowances a JOIN employees e ON a.emp_id = e.emp_id ORDER BY a.id DESC")) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getDouble("overtime"),
                    rs.getDouble("medical"),
                    rs.getDouble("bonus"),
                    rs.getDouble("other"),
                    rs.getString("emp_id"),
                    rs.getString("basic_salary"),
                    rs.getDouble("rate_per_hour"),
                    rs.getDouble("total_amount"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("department")
                };
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading allowance data: " + ex.getMessage());
        }
    }
    
    private void clearFields() {
        employeeIdField.setText("");
        firstNameField.setText("");
        surnameField.setText("");
        dobField.setText("");
        basicSalaryField.setText("");
        departmentField.setText("");
        
        overtimeField.setText("0");
        totalOvertimeField.setText("0");
        medicalField.setText("0");
        ratePerHourField.setText("0");
        bonusField.setText("0");
        otherField.setText("0");
        
        totalAmount = 0.0;
        totalAmountLabel.setText("Total Amount : 0.00");
        
        // Disable allowance fields until an employee is searched
        overtimeField.setEditable(false);
        medicalField.setEditable(false);
        bonusField.setEditable(false);
        otherField.setEditable(false);
        ratePerHourField.setEditable(false);
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Allowance());
    }
}