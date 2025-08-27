import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Update extends JFrame {
    private JTextField employeeIdSearchField, employeeIdField, firstNameField, surnameField, 
                      dobField, departmentField, designationField, statusField, dateHiredField,
                      jobTitleField, basicSalaryField, emailField, contactField, addressLine1Field,
                      addressLine2Field, aptHouseNoField, postCodeField;
    private JRadioButton maleRadio, femaleRadio;
    private JLabel photoLabel;
    private String photoPath;
    
    public Update() {
        initializeUI();
    }
    
    public Update(String empId) {
        initializeUI();
        employeeIdSearchField.setText(empId);
        searchEmployee();
    }
    
    private void initializeUI() {
        setTitle("Update Employee");
        setSize(800, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeSearchPanel();
        initializePersonalDetailsPanel();
        setVisible(true);
    }
    
    private void initializeSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(10, 10, 770, 60);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.setLayout(null);
        
        JLabel employeeIdSearchLabel = new JLabel("Employee ID:");
        employeeIdSearchLabel.setBounds(20, 25, 100, 25);
        searchPanel.add(employeeIdSearchLabel);
        
        employeeIdSearchField = new JTextField();
        employeeIdSearchField.setBounds(120, 25, 500, 25);
        employeeIdSearchField.addActionListener(e -> searchEmployee());
        searchPanel.add(employeeIdSearchField);
        
        add(searchPanel);
    }
    
    private void initializePersonalDetailsPanel() {
        JPanel personalDetailsPanel = new JPanel();
        personalDetailsPanel.setBounds(10, 80, 770, 470);
        personalDetailsPanel.setBorder(BorderFactory.createTitledBorder("Personal Details"));
        personalDetailsPanel.setLayout(null);
        
        // Left column
        addField(personalDetailsPanel, "Employee Id:", employeeIdField = new JTextField(), 20, 30, 120, 30, false);
        addField(personalDetailsPanel, "First name:", firstNameField = new JTextField(), 20, 60, 120, 60, true);
        addField(personalDetailsPanel, "Surname:", surnameField = new JTextField(), 20, 90, 120, 90, true);
        addField(personalDetailsPanel, "Date of Birth:", dobField = new JTextField(), 20, 120, 120, 120, true);
        
        // Gender selection
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(20, 150, 100, 25);
        personalDetailsPanel.add(genderLabel);
        maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(120, 150, 60, 25);
        femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(180, 150, 80, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        personalDetailsPanel.add(maleRadio);
        personalDetailsPanel.add(femaleRadio);
        
        // Continue with other fields
        addField(personalDetailsPanel, "Email:", emailField = new JTextField(), 20, 180, 120, 180, true);
        addField(personalDetailsPanel, "Contact:", contactField = new JTextField(), 20, 210, 120, 210, true);
        addField(personalDetailsPanel, "Address Line 1:", addressLine1Field = new JTextField(), 20, 240, 120, 240, true);
        addField(personalDetailsPanel, "Address Line 2:", addressLine2Field = new JTextField(), 20, 270, 120, 270, true);
        addField(personalDetailsPanel, "Apt/House No:", aptHouseNoField = new JTextField(), 20, 300, 120, 300, true);
        addField(personalDetailsPanel, "Post Code:", postCodeField = new JTextField(), 20, 330, 120, 330, true);
        
        // Middle column
        addField(personalDetailsPanel, "Department:", departmentField = new JTextField(), 280, 30, 380, 30, true);
        addField(personalDetailsPanel, "Designation:", designationField = new JTextField(), 280, 60, 380, 60, true);
        addField(personalDetailsPanel, "Status:", statusField = new JTextField(), 280, 90, 380, 90, true);
        addField(personalDetailsPanel, "Date Hired:", dateHiredField = new JTextField(), 280, 120, 380, 120, true);
        addField(personalDetailsPanel, "Job Title:", jobTitleField = new JTextField(), 280, 150, 380, 150, true);
        addField(personalDetailsPanel, "Basic Salary:", basicSalaryField = new JTextField(), 280, 180, 380, 180, true);
        
        // Photo section
        photoLabel = new JLabel();
        photoLabel.setBounds(550, 30, 200, 200);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        personalDetailsPanel.add(photoLabel);
        
        // Buttons
        JButton updateButton = new JButton("Update Record");
        updateButton.setBounds(380, 240, 150, 30);
        updateButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\save_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        updateButton.addActionListener(e -> updateEmployee());
        personalDetailsPanel.add(updateButton);
        
        JButton deleteButton = new JButton("Delete Record");
        deleteButton.setBounds(550, 240, 150, 30);
        deleteButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\delete_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        deleteButton.addActionListener(e -> deleteEmployee());
        personalDetailsPanel.add(deleteButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(465, 290, 150, 30);
        clearButton.setIcon(new ImageIcon(new ImageIcon("c:\\Users\\kiran\\OneDrive\\Desktop\\Pictures\\proj\\clear_icon.png")
                .getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        clearButton.addActionListener(e -> clearFields());
        personalDetailsPanel.add(clearButton);
        
        add(personalDetailsPanel);
        
        // Status bar
        JLabel loggedInLabel = new JLabel("Logged in As : 1");
        loggedInLabel.setBounds(10, 560, 150, 25);
        add(loggedInLabel);
    }
    
    private void addField(JPanel panel, String labelText, JTextField field, int labelX, int labelY, int fieldX, int fieldY, boolean editable) {
        JLabel label = new JLabel(labelText);
        label.setBounds(labelX, labelY, 100, 25);
        panel.add(label);
        field.setBounds(fieldX, fieldY, 150, 25);
        field.setEditable(editable);
        panel.add(field);
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
                
                String gender = rs.getString("gender");
                if (gender != null) {
                    if (gender.equalsIgnoreCase("Male")) {
                        maleRadio.setSelected(true);
                    } else if (gender.equalsIgnoreCase("Female")) {
                        femaleRadio.setSelected(true);
                    }
                }
                
                departmentField.setText(rs.getString("department"));
                designationField.setText(rs.getString("designation"));
                statusField.setText(rs.getString("status"));
                dateHiredField.setText(rs.getString("date_hired"));
                jobTitleField.setText(rs.getString("job_title"));
                basicSalaryField.setText(rs.getString("basic_salary"));
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contact"));
                
                // Address fields
                addressLine1Field.setText(rs.getString("address_line1"));
                addressLine2Field.setText(rs.getString("address_line2"));
                aptHouseNoField.setText(rs.getString("apt_house_no"));
                postCodeField.setText(rs.getString("post_code"));
                
                // Load photo if available
                byte[] photoData = rs.getBytes("photo");
                if (photoData != null) {
                    ImageIcon imageIcon = new ImageIcon(photoData);
                    Image image = imageIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(image));
                } else {
                    photoLabel.setIcon(null);
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found");
                clearFields();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void updateEmployee() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE employees SET first_name = ?, surname = ?, dob = ?, gender = ?, " +
                 "department = ?, designation = ?, status = ?, date_hired = ?, job_title = ?, " +
                 "basic_salary = ?, email = ?, contact = ?, address_line1 = ?, address_line2 = ?, " +
                 "apt_house_no = ?, post_code = ? WHERE emp_id = ?")) {
            
            stmt.setString(1, firstNameField.getText());
            stmt.setString(2, surnameField.getText());
            stmt.setString(3, dobField.getText());
            stmt.setString(4, maleRadio.isSelected() ? "Male" : "Female");
            stmt.setString(5, departmentField.getText());
            stmt.setString(6, designationField.getText());
            stmt.setString(7, statusField.getText());
            stmt.setString(8, dateHiredField.getText());
            stmt.setString(9, jobTitleField.getText());
            stmt.setString(10, basicSalaryField.getText());
            stmt.setString(11, emailField.getText());
            stmt.setString(12, contactField.getText());
            stmt.setString(13, addressLine1Field.getText());
            stmt.setString(14, addressLine2Field.getText());
            stmt.setString(15, aptHouseNoField.getText());
            stmt.setString(16, postCodeField.getText());
            stmt.setString(17, employeeIdField.getText());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Employee information updated successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update employee information");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void deleteEmployee() {
        if (employeeIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search for an employee first");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this employee?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM employees WHERE emp_id = ?")) {
                
                stmt.setString(1, employeeIdField.getText());
                
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete employee");
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }
    
    private void clearFields() {
        employeeIdField.setText("");
        firstNameField.setText("");
        surnameField.setText("");
        dobField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        departmentField.setText("");
        designationField.setText("");
        statusField.setText("");
        dateHiredField.setText("");
        jobTitleField.setText("");
        basicSalaryField.setText("");
        emailField.setText("");
        contactField.setText("");
        addressLine1Field.setText("");
        addressLine2Field.setText("");
        aptHouseNoField.setText("");
        postCodeField.setText("");
        photoLabel.setIcon(null);
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Update());
    }
}