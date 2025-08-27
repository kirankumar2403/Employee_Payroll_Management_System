import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Reports extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel employeeModel, allowanceModel, deductionModel;
    private JTable employeeTable, allowanceTable, deductionTable;
    private JComboBox<String> departmentFilter, statusFilter;
    
    public Reports() {
        setTitle("Payroll Reports");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeComponents();
        loadAllReports();
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Employee Report Tab
        JPanel employeePanel = createEmployeePanel();
        tabbedPane.addTab("Employee Report", employeePanel);
        
        // Allowance Report Tab
        JPanel allowancePanel = createAllowancePanel();
        tabbedPane.addTab("Allowance Report", allowancePanel);
        
        // Deduction Report Tab
        JPanel deductionPanel = createDeductionPanel();
        tabbedPane.addTab("Deduction Report", deductionPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
            
    }
    
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        departmentFilter = new JComboBox<>(new String[]{"All", "IT", "HR", "Finance", "Marketing", "Operations"});
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Inactive", "On Leave"});
        
        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(departmentFilter);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadEmployeeData());
        filterPanel.add(refreshButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Employee ID", "Name", "Department", "Designation", "Status", "Date Hired", "Basic Salary"};
        employeeModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(employeeModel);
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAllowancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Employee ID", "Name", "Department", "Overtime", "Medical", "Bonus", "Other", "Total"};
        allowanceModel = new DefaultTableModel(columns, 0);
        allowanceTable = new JTable(allowanceModel);
        panel.add(new JScrollPane(allowanceTable), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDeductionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Employee ID", "Name", "Department", "Type", "Amount", "Reason", "Date"};
        deductionModel = new DefaultTableModel(columns, 0);
        deductionTable = new JTable(deductionModel);
        panel.add(new JScrollPane(deductionTable), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadAllReports() {
        loadEmployeeData();
        loadAllowanceData();
        loadDeductionData();
    }
    
    private void loadEmployeeData() {
        employeeModel.setRowCount(0);
        String deptFilter = departmentFilter.getSelectedItem().toString();
        String statusFilter = this.statusFilter.getSelectedItem().toString();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT emp_id, CONCAT(first_name, ' ', surname) as name, department, " +
                 "designation, status, date_hired, basic_salary FROM employees " +
                 "WHERE (? = 'All' OR department = ?) " +
                 "AND (? = 'All' OR status = ?) " +
                 "ORDER BY emp_id")) {
            
            stmt.setString(1, deptFilter);
            stmt.setString(2, deptFilter);
            stmt.setString(3, statusFilter);
            stmt.setString(4, statusFilter);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                employeeModel.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("designation"),
                    rs.getString("status"),
                    rs.getString("date_hired"),
                    rs.getString("basic_salary")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading employee data", ex);
        }
    }
    
    private void loadAllowanceData() {
        allowanceModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT a.emp_id, CONCAT(e.first_name, ' ', e.surname) as name, e.department, " +
                 "a.overtime, a.medical, a.bonus, a.other, a.total_amount " +
                 "FROM allowances a JOIN employees e ON a.emp_id = e.emp_id " +
                 "ORDER BY a.emp_id")) {
            
            while (rs.next()) {
                allowanceModel.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getDouble("overtime"),
                    rs.getDouble("medical"),
                    rs.getDouble("bonus"),
                    rs.getDouble("other"),
                    rs.getDouble("total_amount")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading allowance data", ex);
        }
    }
    
    private void loadDeductionData() {
        deductionModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT d.emp_id, CONCAT(e.first_name, ' ', e.surname) as name, e.department, " +
                 "d.deduction_type, d.total_deduction, d.reason, d.created_at " +
                 "FROM deductions d JOIN employees e ON d.emp_id = e.emp_id " +
                 "ORDER BY d.emp_id")) {
            
            while (rs.next()) {
                deductionModel.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getString("deduction_type"),
                    rs.getDouble("total_deduction"),
                    rs.getString("reason"),
                    rs.getString("created_at")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading deduction data", ex);
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
        SwingUtilities.invokeLater(() -> new Reports());
    }
} 