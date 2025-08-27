import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Search extends JFrame {
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchByComboBox;
    
    // Fix for Name search which needs two parameters
    // Remove this method as it's no longer needed
    private PreparedStatement prepareNameSearch(Connection conn, String query, String searchTerm) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, "%" + searchTerm + "%");
        stmt.setString(2, "%" + searchTerm + "%");
        return stmt;
    }
    
    // Method to open Update form for selected employee
    private void openUpdateForEmployee(String empId) {
        new Update(empId);
        dispose(); // Optional: close the search window
    }
    
    public Search() {
        setTitle("Employee Search");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // North panel for search controls
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        // Search by dropdown
        searchPanel.add(new JLabel("Search By:"));
        searchByComboBox = new JComboBox<>(new String[]{"Employee ID", "Department", "Designation", "Email"});
        searchPanel.add(searchByComboBox);
        
        // Search field
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton);
        
        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            clearTable();
        });
        searchPanel.add(clearButton);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Center panel for results table
        String[] columns = {"Employee ID", "First Name", "Last Name", "Department", "Designation", "Email", "Contact"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Add mouse listener to the result table for double-click functionality
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    int row = resultTable.getSelectedRow();
                    if (row >= 0) {
                        String empId = tableModel.getValueAt(row, 0).toString(); // Assuming employee ID is in first column
                        openUpdateForEmployee(empId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // South panel for action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> viewSelectedEmployee());
        actionPanel.add(viewButton);
        
        
        JButton printButton = new JButton("Print");
        printButton.addActionListener(e -> printResults());
        actionPanel.add(printButton);
        
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> exportResults());
        actionPanel.add(exportButton);
        
        add(actionPanel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term");
            return;
        }
        
        clearTable();
        
        String searchBy = searchByComboBox.getSelectedItem().toString();
        String query = buildSearchQuery(searchBy);
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set search parameter based on search type
            if (searchBy.equals("Employee ID")) {
                stmt.setString(1, searchTerm);
            } else {
                stmt.setString(1, "%" + searchTerm + "%"); // For LIKE queries
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("emp_id"),
                    rs.getString("first_name"),
                    rs.getString("surname"),
                    rs.getString("department"),
                    rs.getString("designation"),
                    rs.getString("email"),
                    rs.getString("contact")
                };
                tableModel.addRow(row);
            }
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No results found");
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private String buildSearchQuery(String searchBy) {
        String query = "SELECT emp_id, first_name, surname, department, designation, email, contact FROM employees WHERE ";
        
        switch (searchBy) {
            case "Employee ID":
                query += "emp_id = ?";
                break;
            case "Department":
                query += "department LIKE ?";
                break;
            case "Designation":
                query += "designation LIKE ?";
                break;
            case "Email":
                query += "email LIKE ?";
                break;
            default:
                query += "emp_id = ?";
        }
        
        return query;
    }
    
    private void clearTable() {
        tableModel.setRowCount(0);
    }
    
    private void viewSelectedEmployee() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to view");
            return;
        }
        
        String empId = tableModel.getValueAt(selectedRow, 0).toString();
        viewSelectedEmployee(empId);
    }
    
    private void viewSelectedEmployee(String empId) {
        JDialog dialog = new JDialog(this, "Employee Details", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(null);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Employee Details");
        titleLabel.setBounds(20, 20, 200, 25);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);
        
        // Left column fields
        JTextField idField = new JTextField();
        idField.setBounds(120, 60, 200, 25);
        idField.setEditable(false);
        panel.add(new JLabel("Employee ID:")).setBounds(20, 60, 100, 25);
        panel.add(idField);
        
        JTextField firstNameField = new JTextField();
        firstNameField.setBounds(120, 90, 200, 25);
        firstNameField.setEditable(false);
        panel.add(new JLabel("First name:")).setBounds(20, 90, 100, 25);
        panel.add(firstNameField);
        
        JTextField surnameField = new JTextField();
        surnameField.setBounds(120, 120, 200, 25);
        surnameField.setEditable(false);
        panel.add(new JLabel("Surname:")).setBounds(20, 120, 100, 25);
        panel.add(surnameField);
        
        JSpinner dobSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd");
        dobSpinner.setEditor(dateEditor);
        dobSpinner.setBounds(120, 150, 170, 25);
        dobSpinner.setEnabled(false);
        panel.add(new JLabel("Date of Birth:")).setBounds(20, 150, 100, 25);
        panel.add(dobSpinner);
        
        JButton calendarBtn = new JButton("...");
        calendarBtn.setBounds(295, 150, 25, 25);
        calendarBtn.setEnabled(false);
        panel.add(calendarBtn);
        
        JRadioButton maleRadio = new JRadioButton("Male");
        maleRadio.setBounds(120, 180, 80, 25);
        maleRadio.setEnabled(false);
        JRadioButton femaleRadio = new JRadioButton("Female");
        femaleRadio.setBounds(200, 180, 80, 25);
        femaleRadio.setEnabled(false);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        panel.add(new JLabel("Gender:")).setBounds(20, 180, 100, 25);
        panel.add(maleRadio);
        panel.add(femaleRadio);
        
        JTextField emailField = new JTextField();
        emailField.setBounds(120, 210, 200, 25);
        emailField.setEditable(false);
        panel.add(new JLabel("Email:")).setBounds(20, 210, 100, 25);
        panel.add(emailField);
        
        JTextField contactField = new JTextField();
        contactField.setBounds(120, 240, 200, 25);
        contactField.setEditable(false);
        panel.add(new JLabel("Contact:")).setBounds(20, 240, 100, 25);
        panel.add(contactField);
        
        JTextField addressLine1Field = new JTextField();
        addressLine1Field.setBounds(120, 270, 200, 25);
        addressLine1Field.setEditable(false);
        panel.add(new JLabel("Address Line 1:")).setBounds(20, 270, 100, 25);
        panel.add(addressLine1Field);
        
        JTextField addressLine2Field = new JTextField();
        addressLine2Field.setBounds(120, 300, 200, 25);
        addressLine2Field.setEditable(false);
        panel.add(new JLabel("Address Line 2:")).setBounds(20, 300, 100, 25);
        panel.add(addressLine2Field);
        
        JTextField aptHouseField = new JTextField();
        aptHouseField.setBounds(120, 330, 200, 25);
        aptHouseField.setEditable(false);
        panel.add(new JLabel("Apt/House No:")).setBounds(20, 330, 100, 25);
        panel.add(aptHouseField);
        
        JTextField postCodeField = new JTextField();
        postCodeField.setBounds(120, 360, 200, 25);
        postCodeField.setEditable(false);
        panel.add(new JLabel("Post Code:")).setBounds(20, 360, 100, 25);
        panel.add(postCodeField);
        
        // Right column fields
        JTextField deptField = new JTextField();
        deptField.setBounds(450, 60, 200, 25);
        deptField.setEditable(false);
        panel.add(new JLabel("Department:")).setBounds(350, 60, 100, 25);
        panel.add(deptField);
        
        JTextField designationField = new JTextField();
        designationField.setBounds(450, 90, 200, 25);
        designationField.setEditable(false);
        panel.add(new JLabel("Designation:")).setBounds(350, 90, 100, 25);
        panel.add(designationField);
        
        JTextField statusField = new JTextField();
        statusField.setBounds(450, 120, 200, 25);
        statusField.setEditable(false);
        panel.add(new JLabel("Status:")).setBounds(350, 120, 100, 25);
        panel.add(statusField);
        
        JTextField dateHiredField = new JTextField();
        dateHiredField.setBounds(450, 150, 200, 25);
        dateHiredField.setEditable(false);
        panel.add(new JLabel("Date Hired:")).setBounds(350, 150, 100, 25);
        panel.add(dateHiredField);
        
        JTextField salaryField = new JTextField();
        salaryField.setBounds(450, 180, 200, 25);
        salaryField.setEditable(false);
        panel.add(new JLabel("Basic Salary:")).setBounds(350, 180, 100, 25);
        panel.add(salaryField);
        
        JTextField jobTitleField = new JTextField();
        jobTitleField.setBounds(450, 210, 200, 25);
        jobTitleField.setEditable(false);
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
        
        JLabel imageLabel = new JLabel("No Image", JLabel.CENTER);
        photoPanel.add(imageLabel, BorderLayout.CENTER);
        
        final String[] selectedImagePath = {null};
        
        JButton browseBtn = new JButton("Browse...");
        browseBtn.setBounds(500, 395, 100, 25);
        browseBtn.setEnabled(false);
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
        
        // Edit/Save button
        JButton editBtn = new JButton("Edit");
        editBtn.setBounds(350, 430, 120, 30);
        panel.add(editBtn);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.setBounds(480, 430, 120, 30);
        panel.add(closeBtn);
        
        // Load employee data
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employees WHERE emp_id = ?")) {
            
            stmt.setString(1, empId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                idField.setText(rs.getString("emp_id"));
                firstNameField.setText(rs.getString("first_name"));
                surnameField.setText(rs.getString("surname"));
                dobSpinner.setValue(rs.getDate("dob"));
                String gender = rs.getString("gender");
                if (gender != null) {
                    if (gender.equalsIgnoreCase("Male")) {
                        maleRadio.setSelected(true);
                    } else if (gender.equalsIgnoreCase("Female")) {
                        femaleRadio.setSelected(true);
                    }
                }
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contact"));
                addressLine1Field.setText(rs.getString("address_line1"));
                addressLine2Field.setText(rs.getString("address_line2"));
                aptHouseField.setText(rs.getString("apt_house_no"));
                postCodeField.setText(rs.getString("post_code"));
                deptField.setText(rs.getString("department"));
                designationField.setText(rs.getString("designation"));
                statusField.setText(rs.getString("status"));
                dateHiredField.setText(rs.getString("date_hired"));
                salaryField.setText(rs.getString("basic_salary"));
                jobTitleField.setText(rs.getString("job_title"));
                
                // Load photo if exists
                Blob photoBlob = rs.getBlob("photo");
                if (photoBlob != null) {
                    byte[] photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                    ImageIcon imageIcon = new ImageIcon(photoBytes);
                    Image image = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                    imageLabel.setText("");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Error loading employee data: " + ex.getMessage());
        }
        
        editBtn.addActionListener(e -> {
            if (editBtn.getText().equals("Edit")) {
                // Enable editing
                firstNameField.setEditable(true);
                surnameField.setEditable(true);
                dobSpinner.setEnabled(true);
                calendarBtn.setEnabled(true);
                maleRadio.setEnabled(true);
                femaleRadio.setEnabled(true);
                emailField.setEditable(true);
                contactField.setEditable(true);
                addressLine1Field.setEditable(true);
                addressLine2Field.setEditable(true);
                aptHouseField.setEditable(true);
                postCodeField.setEditable(true);
                deptField.setEditable(true);
                designationField.setEditable(true);
                statusField.setEditable(true);
                dateHiredField.setEditable(true);
                salaryField.setEditable(true);
                jobTitleField.setEditable(true);
                browseBtn.setEnabled(true);
                editBtn.setText("Save");
            } else {
                // Save changes
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE employees SET first_name=?, surname=?, dob=?, gender=?, " +
                         "email=?, contact=?, address_line1=?, address_line2=?, apt_house_no=?, " +
                         "post_code=?, department=?, designation=?, status=?, date_hired=?, " +
                         "basic_salary=?, job_title=? WHERE emp_id=?")) {
                    
                    stmt.setString(1, firstNameField.getText());
                    stmt.setString(2, surnameField.getText());
                    stmt.setDate(3, new java.sql.Date(((java.util.Date) dobSpinner.getValue()).getTime()));
                    stmt.setString(4, maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : null);
                    stmt.setString(5, emailField.getText());
                    stmt.setString(6, contactField.getText());
                    stmt.setString(7, addressLine1Field.getText());
                    stmt.setString(8, addressLine2Field.getText());
                    stmt.setString(9, aptHouseField.getText());
                    stmt.setString(10, postCodeField.getText());
                    stmt.setString(11, deptField.getText());
                    stmt.setString(12, designationField.getText());
                    stmt.setString(13, statusField.getText());
                    stmt.setString(14, dateHiredField.getText());
                    stmt.setString(15, salaryField.getText());
                    stmt.setString(16, jobTitleField.getText());
                    stmt.setString(17, empId);
                    
                    stmt.executeUpdate();
                    
                    // Update photo if changed
                    if (selectedImagePath[0] != null) {
                        try (PreparedStatement photoStmt = conn.prepareStatement(
                             "UPDATE employees SET photo = ? WHERE emp_id = ?")) {
                            File imageFile = new File(selectedImagePath[0]);
                            FileInputStream fis = new FileInputStream(imageFile);
                            photoStmt.setBinaryStream(1, fis, (int) imageFile.length());
                            photoStmt.setString(2, empId);
                            photoStmt.executeUpdate();
                            fis.close();
                        }
                    }
                    
                    JOptionPane.showMessageDialog(panel, "Employee details updated successfully!");
                    
                    // Disable editing
                    firstNameField.setEditable(false);
                    surnameField.setEditable(false);
                    dobSpinner.setEnabled(false);
                    calendarBtn.setEnabled(false);
                    maleRadio.setEnabled(false);
                    femaleRadio.setEnabled(false);
                    emailField.setEditable(false);
                    contactField.setEditable(false);
                    addressLine1Field.setEditable(false);
                    addressLine2Field.setEditable(false);
                    aptHouseField.setEditable(false);
                    postCodeField.setEditable(false);
                    deptField.setEditable(false);
                    designationField.setEditable(false);
                    statusField.setEditable(false);
                    dateHiredField.setEditable(false);
                    salaryField.setEditable(false);
                    jobTitleField.setEditable(false);
                    browseBtn.setEnabled(false);
                    editBtn.setText("Edit");
                    
                    // Refresh search results
                    performSearch();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "Error updating employee: " + ex.getMessage());
                }
            }
        });
        
        closeBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void printResults() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No results to print");
            return;
        }
        
        try {
            resultTable.print();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error printing: " + ex.getMessage());
        }
    }
    
    private void exportResults() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No results to export");
            return;
        }
        
        JOptionPane.showMessageDialog(this, "Export functionality will be implemented here");
        // Here you would implement export to CSV, Excel, etc.
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll_db", "root", "#kiran24NN");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Search());
    }
    
    private void updateSelectedEmployeeSalary() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update salary");
            return;
        }
        
        String empId = tableModel.getValueAt(selectedRow, 0).toString();
        new UpdateSalary(empId);
    }
}
