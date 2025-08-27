import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel overviewPanel, employeesPanel, settingsPanel;
    
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeComponents();
        loadStatistics();
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Overview Tab
        overviewPanel = createOverviewPanel();
        tabbedPane.addTab("Overview", new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\overview_icon.png"), overviewPanel);
        
        // Employees Tab
        employeesPanel = createEmployeesPanel();
        tabbedPane.addTab("Employees", new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\employee_icon.png"), employeesPanel);
        
        // Settings Tab
        settingsPanel = createSettingsPanel();
        tabbedPane.addTab("Settings", new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\settings_icon.png"), settingsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new PayrollLogin();
        });
        add(logoutBtn, BorderLayout.SOUTH);
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Total Employees Card
        JPanel totalEmployeesCard = createStatCard("Total Employees", "0", Color.BLUE);
        panel.add(totalEmployeesCard);
        
        // Total Departments Card
        JPanel totalDepartmentsCard = createStatCard("Total Departments", "0", Color.GREEN);
        panel.add(totalDepartmentsCard);
        
        // Active Employees Card
        JPanel activeEmployeesCard = createStatCard("Active Employees", "0", Color.ORANGE);
        panel.add(activeEmployeesCard);
        
        // Total Salary Card
        JPanel totalSalaryCard = createStatCard("Total Salary", "$0", Color.RED);
        panel.add(totalSalaryCard);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        
        
        
        // Employee table
        String[] columns = {"Employee ID", "First Name", "Surname", "Department", "Basic Salary", "Status"};
        JTable employeeTable = new JTable(new DefaultTableModel(columns, 0));
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        
        // Load employee data
        loadEmployeeData(employeeTable);
        
        return panel;
    }
    
    private void loadEmployeeData(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT emp_id, first_name, surname, department, basic_salary, status FROM employees")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("department"),
                    String.format("$%.2f", rs.getDouble("basic_salary")),
                    rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            showError("Error loading employee data", ex);
        }
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Database Settings Panel
        JPanel dbPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        dbPanel.setBorder(BorderFactory.createTitledBorder("Database Settings"));
        
        // Backup Database Button
        JButton backupBtn = new JButton("Backup Database");
        backupBtn.addActionListener(e -> backupDatabase());
        dbPanel.add(backupBtn);
        
        // Restore Database Button
        JButton restoreBtn = new JButton("Restore Database");
        restoreBtn.addActionListener(e -> restoreDatabase());
        dbPanel.add(restoreBtn);
        
        // Optimize Database Button
        JButton optimizeBtn = new JButton("Optimize Database");
        optimizeBtn.addActionListener(e -> optimizeDatabase());
        dbPanel.add(optimizeBtn);
        
        panel.add(dbPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup Location");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String backupPath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                // Create backup command
                String[] command = {
                    "mysqldump",
                    "-u", "root",
                    "-p#kiran24NN",
                    "payroll_db",
                    "-r", backupPath + "/payroll_backup_" + System.currentTimeMillis() + ".sql"
                };
                
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "Database backup completed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Backup failed. Please check the database connection.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showError("Error during backup", ex);
            }
        }
    }
    
    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Backup File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQL Files", "sql"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String backupFile = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                // Create restore command
                String[] command = {
                    "mysql",
                    "-u", "root",
                    "-p#kiran24NN",
                    "payroll_db",
                    "-e", "source " + backupFile
                };
                
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    JOptionPane.showMessageDialog(this, "Database restore completed successfully!");
                    // Refresh the dashboard data
                    loadStatistics();
                } else {
                    JOptionPane.showMessageDialog(this, "Restore failed. Please check the backup file.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                showError("Error during restore", ex);
            }
        }
    }
    
    private void optimizeDatabase() {
        try (Connection conn = getConnection()) {
            // Optimize all tables
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("OPTIMIZE TABLE employees");
                stmt.execute("OPTIMIZE TABLE allowances");
                stmt.execute("OPTIMIZE TABLE deductions");
                
                JOptionPane.showMessageDialog(this, "Database optimization completed successfully!");
            }
        } catch (SQLException ex) {
            showError("Error during optimization", ex);
        }
    }
    
    private void loadStatistics() {
        try (Connection conn = getConnection()) {
            // Load total employees
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM employees")) {
                if (rs.next()) {
                    updateStatCard("Total Employees", String.valueOf(rs.getInt("total")));
                }
            }
            
            // Load department statistics
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(DISTINCT department) as total FROM employees")) {
                if (rs.next()) {
                    updateStatCard("Total Departments", String.valueOf(rs.getInt("total")));
                }
            }
            
            // Load active employees
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as active FROM employees WHERE status = 'Active'")) {
                if (rs.next()) {
                    updateStatCard("Active Employees", String.valueOf(rs.getInt("active")));
                }
            }
            
            // Load total salary
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT SUM(basic_salary) as total FROM employees")) {
                if (rs.next()) {
                    double totalSalary = rs.getDouble("total");
                    updateStatCard("Total Salary", String.format("$%.2f", totalSalary));
                }
            }
            
        } catch (SQLException ex) {
            showError("Error loading statistics", ex);
        }
    }
    
    private void updateStatCard(String title, String value) {
        Component[] components = overviewPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                Component[] cardComponents = card.getComponents();
                for (Component cardComp : cardComponents) {
                    if (cardComp instanceof JLabel) {
                        JLabel label = (JLabel) cardComp;
                        if (label.getText().equals(title)) {
                            // Find the value label (it's the second label in the card)
                            for (Component valueComp : cardComponents) {
                                if (valueComp instanceof JLabel && valueComp != label) {
                                    ((JLabel) valueComp).setText(value);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
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