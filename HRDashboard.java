import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HRDashboard extends JFrame {
    
    public HRDashboard() {
        setTitle("HR Dashboard");
        setSize(1000, 700);  // Increased window size for better layout
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        // Employee Menu with dropdown items
        JMenu employeeMenu = new JMenu("Employee");
        JMenuItem employeeRegistration = new JMenuItem("Employee Registration");
        employeeRegistration.addActionListener(e -> openEmployeeManager());
        employeeMenu.add(employeeRegistration);
        menuBar.add(employeeMenu);
        
        // Reports Menu with dropdown items
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem employeesReport = new JMenuItem("Employees RP");
        employeesReport.addActionListener(e -> new Reports());
        reportsMenu.add(employeesReport);
    
        
        menuBar.add(reportsMenu);
        
        JMenu aboutMenu = new JMenu("About");
        menuBar.add(aboutMenu);
        
        setJMenuBar(menuBar);
        
        // Background image - you can replace this with your own image
        try {
            JLabel backgroundLabel = new JLabel(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\background.jpg")
                    .getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH)));  // Adjusted to match new window size
            backgroundLabel.setBounds(0, 0, 1000, 700);  // Adjusted to match new window size
            add(backgroundLabel);
            
            // Add buttons on top of the background - adjusted positions and sizes
            JButton employeeManagerBtn = createDashboardButton("Employee Manager", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\employee_icon.png", 550, 350);
            employeeManagerBtn.addActionListener(e -> openEmployeeManager());
            backgroundLabel.add(employeeManagerBtn);
            
            JButton searchBtn = createDashboardButton("Search", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\search_icon.png", 800, 350);
            searchBtn.addActionListener(e -> openSearch());
            backgroundLabel.add(searchBtn);
            
            JButton allowanceBtn = createDashboardButton("Allowance", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\allowance_icon.png", 550, 420);
            allowanceBtn.addActionListener(e -> openAllowance());
            backgroundLabel.add(allowanceBtn);
            
            JButton updateSalaryBtn = createDashboardButton("Update Salary", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\salary_icon.png", 800, 420);
            updateSalaryBtn.addActionListener(e -> openUpdateSalary()); // Add this action listener
            backgroundLabel.add(updateSalaryBtn);
            
            JButton deductionBtn = createDashboardButton("Deduction", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\deduction_icon.png", 550, 490);
            deductionBtn.addActionListener(e -> openDeduction());
            backgroundLabel.add(deductionBtn);
            
            // In the try block
            JButton paymentBtn = createDashboardButton("Payment", "c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\payment_icon.png", 800, 490);
            paymentBtn.addActionListener(e -> openPaySlip());
            backgroundLabel.add(paymentBtn);
            
            // Add logged in status
            JLabel loggedInLabel = new JLabel("Logged in As : 1");
            loggedInLabel.setBounds(10, 610, 150, 20);  // Adjusted position
            loggedInLabel.setForeground(Color.WHITE);
            backgroundLabel.add(loggedInLabel);
            
        } catch (Exception e) {
            e.printStackTrace();
            // If image loading fails, use a plain background
            getContentPane().setBackground(new Color(0, 51, 102));
            
            // Add buttons directly to the content pane - adjusted positions and sizes
            JButton employeeManagerBtn = createDashboardButton("Employee Manager", null, 550, 350);
            employeeManagerBtn.addActionListener(e1 -> openEmployeeManager());
            add(employeeManagerBtn);
            
            JButton searchBtn = createDashboardButton("Search", null, 800, 350);
            searchBtn.addActionListener(e1 -> openSearch());
            add(searchBtn);
            
            JButton allowanceBtn = createDashboardButton("Allowance", null, 550, 420);
            allowanceBtn.addActionListener(e1 -> openAllowance());
            add(allowanceBtn);
            
            JButton updateSalaryBtn = createDashboardButton("Update Salary", null, 800, 420);
            updateSalaryBtn.addActionListener(e1 -> openUpdateSalary()); // Fix variable name and add action listener
            add(updateSalaryBtn);
            
            
            JButton deductionBtn = createDashboardButton("Deduction", null, 550, 490);
            deductionBtn.addActionListener(e1 -> openDeduction());
            add(deductionBtn);
            
            // In the catch block
            JButton paymentBtn = createDashboardButton("Payment", null, 800, 490);
            paymentBtn.addActionListener(e1 -> openPaySlip());
            add(paymentBtn);
            
            // Add logged in status
            JLabel loggedInLabel = new JLabel("Logged in As : 1");
            loggedInLabel.setBounds(10, 610, 150, 20);  // Adjusted position
            loggedInLabel.setForeground(Color.WHITE);
            add(loggedInLabel);
        }
        
        setVisible(true);
    }
    
    
    
    private void openEmployeeManager() {
        new EmployeeManager();
    }
    
    private void openSearch() {
        new Search();
    }
    
    private void openAllowance() {
        new Allowance();
    }
    
    private void openDeduction() {
        new Deduction();
    }
    
    private void openUpdateSalary() {
        new UpdateSalary();
    }
    
    private void openPaySlip() {
        new PaySlip();
    }
    
    private JButton createDashboardButton(String text, String iconPath, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(iconPath);
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
                button.setHorizontalTextPosition(SwingConstants.RIGHT);
                button.setIconTextGap(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HRDashboard());
    }
}