import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class EmployeeManager extends JFrame {
    
    public EmployeeManager() {
        setTitle("Employee Manager");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Register New Employee", createRegisterPanel());
        tabbedPane.addTab("View Employees", createViewPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(null);
        
        // Title
        JLabel titleLabel = new JLabel("Employee Details");
        titleLabel.setBounds(20, 20, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);
        
        // Left column fields
        JTextField idField = new JTextField();
        idField.setBounds(120, 60, 200, 25);
        idField.setEditable(false);
        idField.setText(generateEmployeeId());
        panel.add(new JLabel("Employee ID:")).setBounds(20, 60, 100, 25);
        panel.add(idField);
        
        JTextField firstNameField = new JTextField();
        firstNameField.setBounds(120, 90, 200, 25);
        panel.add(new JLabel("First name:")).setBounds(20, 90, 100, 25);
        panel.add(firstNameField);
        
        JTextField surnameField = new JTextField();
        surnameField.setBounds(120, 120, 200, 25);
        panel.add(new JLabel("Surname:")).setBounds(20, 120, 100, 25);
        panel.add(surnameField);
        
        JSpinner dobSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd");
        dobSpinner.setEditor(dateEditor);
        dobSpinner.setBounds(120, 150, 170, 25);
        panel.add(new JLabel("Date of Birth:")).setBounds(20, 150, 100, 25);
        panel.add(dobSpinner);
        
        JButton calendarBtn = new JButton("...");
        calendarBtn.setBounds(295, 150, 25, 25);
        panel.add(calendarBtn);
        
        JRadioButton maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(120, 180, 80, 25);
        JRadioButton femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(200, 180, 80, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        panel.add(new JLabel("Gender:")).setBounds(20, 180, 100, 25);
        panel.add(maleRadio);
        panel.add(femaleRadio);
        
        JTextField emailField = new JTextField();
        emailField.setBounds(120, 210, 200, 25);
        panel.add(new JLabel("Email:")).setBounds(20, 210, 100, 25);
        panel.add(emailField);
        
        JTextField contactField = new JTextField();
        contactField.setBounds(120, 240, 200, 25);
        panel.add(new JLabel("Contact:")).setBounds(20, 240, 100, 25);
        panel.add(contactField);
        
        JTextField addressLine1Field = new JTextField();
        addressLine1Field.setBounds(120, 270, 200, 25);
        panel.add(new JLabel("Address Line 1:")).setBounds(20, 270, 100, 25);
        panel.add(addressLine1Field);
        
        JTextField addressLine2Field = new JTextField();
        addressLine2Field.setBounds(120, 300, 200, 25);
        panel.add(new JLabel("Address Line 2:")).setBounds(20, 300, 100, 25);
        panel.add(addressLine2Field);
        
        JTextField aptHouseField = new JTextField();
        aptHouseField.setBounds(120, 330, 200, 25);
        panel.add(new JLabel("Apt/House No:")).setBounds(20, 330, 100, 25);
        panel.add(aptHouseField);
        
        JTextField postCodeField = new JTextField();
        postCodeField.setBounds(120, 360, 200, 25);
        panel.add(new JLabel("Post Code:")).setBounds(20, 360, 100, 25);
        panel.add(postCodeField);
        
        JPasswordField passwordRegField = new JPasswordField();
        passwordRegField.setBounds(120, 390, 200, 25);
        panel.add(new JLabel("Password:")).setBounds(20, 390, 100, 25);
        panel.add(passwordRegField);
        
        // Right column fields
        JTextField deptField = new JTextField();
        deptField.setBounds(450, 60, 200, 25);
        panel.add(new JLabel("Department:")).setBounds(350, 60, 100, 25);
        panel.add(deptField);
        
        JTextField designationField = new JTextField();
        designationField.setBounds(450, 90, 200, 25);
        panel.add(new JLabel("Designation:")).setBounds(350, 90, 100, 25);
        panel.add(designationField);
        
        JTextField statusField = new JTextField();
        statusField.setBounds(450, 120, 200, 25);
        panel.add(new JLabel("Status:")).setBounds(350, 120, 100, 25);
        panel.add(statusField);
        
        JTextField dateHiredField = new JTextField();
        dateHiredField.setBounds(450, 150, 200, 25);
        panel.add(new JLabel("Date Hired:")).setBounds(350, 150, 100, 25);
        panel.add(dateHiredField);
        
        JTextField salaryField = new JTextField();
        salaryField.setBounds(450, 180, 200, 25);
        panel.add(new JLabel("Basic Salary:")).setBounds(350, 180, 100, 25);
        panel.add(salaryField);
        
        JTextField jobTitleField = new JTextField();
        jobTitleField.setBounds(450, 210, 200, 25);
        panel.add(new JLabel("Job Title:")).setBounds(350, 210, 100, 25);
        panel.add(jobTitleField);
        
        // Photo panel
        JPanel photoPanel = new JPanel();
        photoPanel.setBounds(450, 240, 200, 150);
        photoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        photoPanel.setBackground(new Color(100, 149, 237));
        photoPanel.setLayout(new BorderLayout());
        panel.add(new JLabel("Photo:")).setBounds(350, 240, 100, 25);
        panel.add(photoPanel);
        
        JLabel imageLabel = new JLabel("No Image Selected", JLabel.CENTER);
        photoPanel.add(imageLabel, BorderLayout.CENTER);
        
        final String[] selectedImagePath = {null};
        
        JButton browseBtn = new JButton("Browse...");
        browseBtn.setBounds(500, 395, 100, 25);
        panel.add(browseBtn);
        
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                try {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedImagePath[0] = selectedFile.getAbsolutePath();
                    ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
                    Image image = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                    imageLabel.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error loading image: " + ex.getMessage());
                }
            }
        });
        
        JButton addRecordBtn = new JButton("Add Record");
        addRecordBtn.setBounds(350, 430, 120, 30);
        panel.add(addRecordBtn);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(480, 430, 120, 30);
        panel.add(clearBtn);
        
        addRecordBtn.addActionListener(e -> {
            if (firstNameField.getText().isEmpty() || surnameField.getText().isEmpty() || 
                passwordRegField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(panel, "Please fill in all required fields including password!");
                return;
            }
            
            String gender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "";
            java.util.Date dobDate = (java.util.Date) dobSpinner.getValue();
            java.sql.Date sqlDob = new java.sql.Date(dobDate.getTime());
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN")) {
                try (PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO employees (emp_id, first_name, surname, dob, gender, department, " +
                     "designation, status, date_hired, basic_salary, job_title, email, contact, " +
                     "address_line1, address_line2, apt_house_no, post_code, username, password) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    
                    String empId = idField.getText();
                    stmt.setString(1, empId);
                    stmt.setString(2, firstNameField.getText());
                    stmt.setString(3, surnameField.getText());
                    stmt.setDate(4, sqlDob);
                    stmt.setString(5, gender);
                    stmt.setString(6, deptField.getText());
                    stmt.setString(7, designationField.getText());
                    stmt.setString(8, statusField.getText());
                    stmt.setString(9, dateHiredField.getText());
                    stmt.setString(10, salaryField.getText());
                    stmt.setString(11, jobTitleField.getText());
                    stmt.setString(12, emailField.getText());
                    stmt.setString(13, contactField.getText());
                    stmt.setString(14, addressLine1Field.getText());
                    stmt.setString(15, addressLine2Field.getText());
                    stmt.setString(16, aptHouseField.getText());
                    stmt.setString(17, postCodeField.getText());
                    stmt.setString(18, empId);
                    stmt.setString(19, new String(passwordRegField.getPassword()));
                    
                    stmt.executeUpdate();
                }
                
                if (selectedImagePath[0] != null) {
                    try (PreparedStatement photoStmt = conn.prepareStatement(
                         "UPDATE employees SET photo = ? WHERE emp_id = ?")) {
                        File imageFile = new File(selectedImagePath[0]);
                        FileInputStream fis = new FileInputStream(imageFile);
                        photoStmt.setBinaryStream(1, fis, (int) imageFile.length());
                        photoStmt.setString(2, idField.getText());
                        photoStmt.executeUpdate();
                        fis.close();
                    }
                }
                
                JOptionPane.showMessageDialog(panel, "Employee registered successfully!\nEmployee ID: " + idField.getText());
                clearBtn.doClick();
                
            } catch (SQLIntegrityConstraintViolationException dup) {
                JOptionPane.showMessageDialog(panel, "Employee ID already exists.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });
        
        clearBtn.addActionListener(e -> {
            idField.setText(generateEmployeeId());
            firstNameField.setText("");
            surnameField.setText("");
            dobSpinner.setValue(new java.util.Date());
            genderGroup.clearSelection();
            deptField.setText("");
            designationField.setText("");
            statusField.setText("");
            dateHiredField.setText("");
            salaryField.setText("");
            jobTitleField.setText("");
            emailField.setText("");
            contactField.setText("");
            addressLine1Field.setText("");
            addressLine2Field.setText("");
            aptHouseField.setText("");
            postCodeField.setText("");
            passwordRegField.setText("");
            imageLabel.setIcon(null);
            imageLabel.setText("No Image Selected");
            selectedImagePath[0] = null;
        });
        
        return panel;
    }
    
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table model
        String[] columns = {"Employee ID", "First Name", "Last Name", "Department", "Designation", "Email", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadEmployeeData(model);
        
        return panel;
    }
    
    private void loadEmployeeData(DefaultTableModel model) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT emp_id, first_name, surname, department, designation, email, contact FROM employees")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("emp_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("department"),
                    rs.getString("designation"),
                    rs.getString("email"),
                    rs.getString("contact")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + ex.getMessage());
        }
    }
    
    private String generateEmployeeId() {
        // Generate a random 6-digit number
        int randomNum = (int) (Math.random() * 900000) + 100000; // This gives a number between 100000 and 999999
        return "EMP" + randomNum;
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeManager());
    }
}