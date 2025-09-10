import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
public class GymandDietMana {
    private static final String URL = "jdbc:mysql://localhost:3306/gym";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "Ce50_Saltlake"; // Replace with your MySQL password
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
            panel.setBackground(new Color(255, 223, 186)); // Light peach background
            JButton btnAdmin = createStyledButton("Admin", new Color(255, 165, 0)); // Orange
            JButton btnTrainer = createStyledButton("Trainer", new Color(0, 128, 0)); // Green
            JButton btnClient = createStyledButton("Client", new Color(255, 255, 0)); // Yellow
            JButton btnPackages = createStyledButton("Packages", new Color(255, 105, 180)); // Pink
            panel.add(btnAdmin);
            panel.add(btnTrainer);
            panel.add(btnClient);
            panel.add(btnPackages);            btnPackages.addActionListener(e -> displayPackages());
            btnTrainer.addActionListener(e -> showTrainerOptions());
            btnClient.addActionListener(e -> showClientOptions());
            frame.add(panel);
            frame.setVisible(true);
        });
    }
    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        return button;
    }
    private static void displayPackages() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM packages")) {
            StringBuilder sb = new StringBuilder("<html><body>");
            sb.append("<h2 style='color:blue;'>Package Details</h2>");
            sb.append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse;'>");
            sb.append("<tr><th>Duration</th><th>Assistance</th><th>Money</th></tr>");
            while (rs.next()) {
                String duration = rs.getString("duration");
                String assistance = rs.getString("assistance");
                double money = rs.getDouble("money");
                sb.append(String.format("<tr><td>%s</td><td>%s</td><td>$%.2f</td></tr>", duration, assistance, money));
            }
            sb.append("</table></body></html>");
            JLabel label = new JLabel(sb.toString());
            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(null, scrollPane, "Package Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void showTrainerOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton btnNewTrainer = createStyledButton("New Trainer", new Color(100, 149, 237)); // Cornflower Blue
        JButton btnExistingTrainer = createStyledButton("Existing Trainer", new Color(255, 99, 71)); // Tomato Red
        optionsPanel.add(btnNewTrainer);
        optionsPanel.add(btnExistingTrainer);
        JFrame optionsFrame = new JFrame("Trainer Options");
        optionsFrame.setSize(300, 200);
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.add(optionsPanel);
        btnNewTrainer.addActionListener(e -> {
            optionsFrame.dispose();
            showNewTrainerForm();
        });
        btnExistingTrainer.addActionListener(e -> {
            optionsFrame.dispose();
            showTrainerSelection();
        });
        optionsFrame.setVisible(true);
    }
    private static void showTrainerSelection() {
        List<String> trainerNames = new ArrayList<>();
        List<String> trainerIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM trainers")) {
            while (rs.next()) {
                trainerIds.add(rs.getString("id"));
                trainerNames.add(rs.getString("name"));
            }
            String[] namesArray = trainerNames.toArray(new String[0]);
            String selectedTrainer = (String) JOptionPane.showInputDialog(null, "Select a trainer:",
                    "Trainer Selection", JOptionPane.QUESTION_MESSAGE, null, namesArray, namesArray[0]);
            if (selectedTrainer != null) {
                int index = trainerNames.indexOf(selectedTrainer);
                String trainerId = trainerIds.get(index);
                displayTrainerDetails(trainerId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void displayTrainerDetails(String trainerId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM trainers WHERE id = ?")) {
            pstmt.setString(1, trainerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    String type = rs.getString("type");
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><body>");
                    sb.append("<h2 style='color:blue;'>Trainer Details</h2>");
                    sb.append("<p><strong>Name:</strong> ").append(name).append("</p>");
                    sb.append("<p><strong>Phone:</strong> ").append(phone).append("</p>");
                    sb.append("<p><strong>Email:</strong> ").append(email).append("</p>");
                    sb.append("<p><strong>Type:</strong> ").append(type).append("</p>");
                    sb.append("</body></html>");
                    JLabel label = new JLabel(sb.toString());
                    JScrollPane scrollPane = new JScrollPane(label);
                    scrollPane.setPreferredSize(new Dimension(400, 300));
                    JButton btnGetStudents = new JButton("Get Student Details");
                    btnGetStudents.addActionListener(e -> displayStudentDetails(trainerId));
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(scrollPane, BorderLayout.CENTER);
                    panel.add(btnGetStudents, BorderLayout.SOUTH);
                    JOptionPane.showMessageDialog(null, panel, "Trainer Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No trainer found with ID: " + trainerId, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void displayStudentDetails(String trainerId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM clients WHERE trainer_id = ?")) {
            pstmt.setString(1, trainerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                StringBuilder sb = new StringBuilder("<html><body>");
                sb.append("<h2 style='color:blue;'>Student Details</h2>");
                sb.append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse;'>");
                sb.append("<tr><th>ID</th><th>Name</th><th>Phone</th><th>Email</th><th>Height</th><th>Weight</th><th>Target</th></tr>");
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    double height = rs.getDouble("height");
                    double weight = rs.getDouble("weight");
                    String target = rs.getString("target");
                    sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%.2f</td><td>%.2f</td><td>%s</td></tr>",
                            id, name, phone, email, height, weight, target));
                }
                sb.append("</table></body></html>");
                JLabel label = new JLabel(sb.toString());
                JScrollPane scrollPane = new JScrollPane(label);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(null, scrollPane, "Student Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void showClientOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton btnNewClient = createStyledButton("New Client", new Color(100, 149, 237)); // Cornflower Blue
        JButton btnExistingClient = createStyledButton("Existing Client", new Color(255, 99, 71)); // Tomato Red
        optionsPanel.add(btnNewClient);
        optionsPanel.add(btnExistingClient);
        JFrame optionsFrame = new JFrame("Client Options");
        optionsFrame.setSize(300, 200);
        optionsFrame.setLocationRelativeTo(null);
        optionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        optionsFrame.add(optionsPanel);
        btnNewClient.addActionListener(e -> {
            optionsFrame.dispose();
            showNewClientForm();
        });
        btnExistingClient.addActionListener(e -> {
            optionsFrame.dispose();
            showClientSelection();
        });
        optionsFrame.setVisible(true);
    }
    private static void showClientSelection() {
        List<String> clientNames = new ArrayList<>();
        List<String> clientIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM clients")) {

            while (rs.next()) {
                clientIds.add(rs.getString("id"));
                clientNames.add(rs.getString("name"));
            }

            String[] namesArray = clientNames.toArray(new String[0]);
            String selectedClient = (String) JOptionPane.showInputDialog(null, "Select a client:",
                    "Client Selection", JOptionPane.QUESTION_MESSAGE, null, namesArray, namesArray[0]);

            if (selectedClient != null) {
                int index = clientNames.indexOf(selectedClient);
                String clientId = clientIds.get(index);
                displayClientDetails(clientId);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void displayClientDetails(String clientId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM clients WHERE id = ?")) {

            pstmt.setString(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    double height = rs.getDouble("height");
                    double weight = rs.getDouble("weight");
                    String target = rs.getString("target");
                    String trainerId = rs.getString("trainer_id");

                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><body>");
                    sb.append("<h2 style='color:blue;'>Client Details</h2>");
                    sb.append("<p><strong>Name:</strong> ").append(name).append("</p>");
                    sb.append("<p><strong>Phone:</strong> ").append(phone).append("</p>");
                    sb.append("<p><strong>Email:</strong> ").append(email).append("</p>");
                    sb.append("<p><strong>Height:</strong> ").append(height).append("</p>");
                    sb.append("<p><strong>Weight:</strong> ").append(weight).append("</p>");
                    sb.append("<p><strong>Target:</strong> ").append(target).append("</p>");
                    sb.append("<p><strong>Trainer ID:</strong> ").append(trainerId).append("</p>");
                    sb.append("</body></html>");

                    JLabel label = new JLabel(sb.toString());
                    JScrollPane scrollPane = new JScrollPane(label);
                    scrollPane.setPreferredSize(new Dimension(400, 300));

                    // Option for Update or Delete
                    int choice = JOptionPane.showOptionDialog(null, scrollPane, "Client Details",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                            new Object[]{"Update", "Delete", "Cancel"}, "Update");

                    if (choice == 0) {
                        showUpdateClientForm(clientId, name, phone, email, height, weight, target, trainerId);
                    } else if (choice == 1) {
                        deleteClient(clientId);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "No client found with ID: " + clientId, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void showUpdateClientForm(String clientId, String name, String phone, String email, double height, double weight, String target, String trainerId) {
        JFrame formFrame = new JFrame("Update Client Form");
        formFrame.setSize(500, 500);
        formFrame.setLocationRelativeTo(null);
        formFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formFrame.add(new JLabel("Name:"), gbc);

        JTextField txtName = new JTextField(name, 20);
        gbc.gridx = 1;
        formFrame.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formFrame.add(new JLabel("Phone:"), gbc);

        JTextField txtPhone = new JTextField(phone, 20);
        gbc.gridx = 1;
        formFrame.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formFrame.add(new JLabel("Email:"), gbc);

        JTextField txtEmail = new JTextField(email, 20);
        gbc.gridx = 1;
        formFrame.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formFrame.add(new JLabel("Height (cm):"), gbc);

        JTextField txtHeight = new JTextField(String.valueOf(height), 20);
        gbc.gridx = 1;
        formFrame.add(txtHeight, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formFrame.add(new JLabel("Weight (kg):"), gbc);

        JTextField txtWeight = new JTextField(String.valueOf(weight), 20);
        gbc.gridx = 1;
        formFrame.add(txtWeight, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formFrame.add(new JLabel("Target:"), gbc);

        JTextField txtTarget = new JTextField(target, 20);
        gbc.gridx = 1;
        formFrame.add(txtTarget, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formFrame.add(new JLabel("Trainer ID:"), gbc);

        JTextField txtTrainerId = new JTextField(trainerId, 20);
        gbc.gridx = 1;
        formFrame.add(txtTrainerId, gbc);

        JButton btnSubmit = new JButton("Update");
        gbc.gridx = 1;
        gbc.gridy = 7;
        formFrame.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> {
            String updatedName = txtName.getText();
            String updatedPhone = txtPhone.getText();
            String updatedEmail = txtEmail.getText();
            double updatedHeight = Double.parseDouble(txtHeight.getText());
            double updatedWeight = Double.parseDouble(txtWeight.getText());
            String updatedTarget = txtTarget.getText();
            String updatedTrainerId = txtTrainerId.getText();

            updateClientDetails(clientId, updatedName, updatedPhone, updatedEmail, updatedHeight, updatedWeight, updatedTarget, updatedTrainerId);
            formFrame.dispose();
        });

        formFrame.setVisible(true);
    }

    private static void updateClientDetails(String clientId, String name, String phone, String email, double height, double weight, String target, String trainerId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE clients SET name = ?, phone = ?, email = ?, height = ?, weight = ?, target = ?, trainer_id = ? WHERE id = ?")) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setDouble(4, height);
            pstmt.setDouble(5, weight);
            pstmt.setString(6, target);
            pstmt.setString(7, trainerId);
            pstmt.setString(8, clientId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Client details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteClient(String clientId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM clients WHERE id = ?")) {

            pstmt.setString(1, clientId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Client deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void showNewTrainerForm() {
        JFrame formFrame = new JFrame("New Trainer Form");
        formFrame.setSize(500, 400);
        formFrame.setLocationRelativeTo(null);
        formFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formFrame.add(new JLabel("Name:"), gbc);
        JTextField txtName = new JTextField(20);
        gbc.gridx = 1;
        formFrame.add(txtName, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formFrame.add(new JLabel("Phone:"), gbc);
        JTextField txtPhone = new JTextField(20);
        gbc.gridx = 1;
        formFrame.add(txtPhone, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formFrame.add(new JLabel("Email:"), gbc);
        JTextField txtEmail = new JTextField(20);
        gbc.gridx = 1;
        formFrame.add(txtEmail, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formFrame.add(new JLabel("Type:"), gbc);
        JRadioButton rbtnGeneral = new JRadioButton("General Trainer");
        JRadioButton rbtnPersonal = new JRadioButton("Personal Trainer");
        ButtonGroup group = new ButtonGroup();
        group.add(rbtnGeneral);
        group.add(rbtnPersonal);
        JPanel typePanel = new JPanel(new FlowLayout());
        typePanel.add(rbtnGeneral);
        typePanel.add(rbtnPersonal);
        gbc.gridx = 1;
        formFrame.add(typePanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formFrame.add(new JLabel("Gender:"), gbc);
        JRadioButton rbtnMale = new JRadioButton("Male");
        JRadioButton rbtnFemale = new JRadioButton("Female");
        JRadioButton rbtnOther = new JRadioButton("Other");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbtnMale);
        genderGroup.add(rbtnFemale);
        genderGroup.add(rbtnOther);
        JPanel genderPanel = new JPanel(new FlowLayout());
        genderPanel.add(rbtnMale);
        genderPanel.add(rbtnFemale);
        genderPanel.add(rbtnOther);
        gbc.gridx = 1;
        formFrame.add(genderPanel, gbc);
        JButton btnSubmit = new JButton("Submit");
        gbc.gridx = 1;
        gbc.gridy = 5;
        formFrame.add(btnSubmit, gbc);
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText();
            String phone = txtPhone.getText();
            String email = txtEmail.getText();
            String type = rbtnGeneral.isSelected() ? "General" : "Personal";
            String gender = rbtnMale.isSelected() ? "Male" : rbtnFemale.isSelected() ? "Female" : "Other";
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(formFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            addNewTrainer(name, phone, email, type, gender);
            formFrame.dispose();
        });
        formFrame.setVisible(true);
    }
    private static void addNewTrainer(String name, String phone, String email, String type, String gender) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM trainers");
            rs.next();
            int newId = rs.getInt(1) + 1;
            String trainerId = String.format("%03d", newId);
            String insertSQL = "INSERT INTO trainers (id, name, phone, email, type, gender) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, trainerId);
                pstmt.setString(2, name);
                pstmt.setString(3, phone);
                pstmt.setString(4, email);
                pstmt.setString(5, type);
                pstmt.setString(6, gender);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Trainer added successfully with ID: " + trainerId, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
        private static void showNewClientForm() {
            JFrame formFrame = new JFrame("New Client Registration");
            formFrame.setSize(600, 700);
            formFrame.setLocationRelativeTo(null);
            formFrame.getContentPane().setBackground(new Color(240, 248, 255));

            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(new Color(240, 248, 255));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;

            // Title
            JLabel titleLabel = new JLabel("New Client Registration");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(new Color(0, 102, 204));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(0, 0, 20, 0);
            mainPanel.add(titleLabel, gbc);

            // Reset gridwidth and insets
            gbc.gridwidth = 1;
            gbc.insets = new Insets(10, 10, 10, 10);

            // Input fields
            JTextField txtName = new JTextField(20);
            JTextField txtPhone = new JTextField(20);
            JTextField txtEmail = new JTextField(20);
            JTextField txtHeight = new JTextField(20);
            JTextField txtWeight = new JTextField(20);
            JTextField txtTarget = new JTextField(20);

            addFormField(mainPanel, gbc, "Name:", txtName);
            addFormField(mainPanel, gbc, "Phone:", txtPhone);
            addFormField(mainPanel, gbc, "Email:", txtEmail);
            addFormField(mainPanel, gbc, "Height (cm):", txtHeight);
            addFormField(mainPanel, gbc, "Weight (kg):", txtWeight);
            addFormField(mainPanel, gbc, "Target:", txtTarget);

            // Trainer Selection
            JComboBox<String> trainerComboBox = new JComboBox<>();
            addFormField(mainPanel, gbc, "Select Trainer:", trainerComboBox);
            populateTrainerComboBox(trainerComboBox);

            // Package Selection
            JComboBox<String> packageComboBox = new JComboBox<>();
            addFormField(mainPanel, gbc, "Select Package:", packageComboBox);

            // Gender Selection
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            genderPanel.setBackground(new Color(240, 248, 255));
            ButtonGroup genderGroup = new ButtonGroup();
            JRadioButton rbtnMale = createStyledRadioButton("Male");
            JRadioButton rbtnFemale = createStyledRadioButton("Female");
            JRadioButton rbtnOther = createStyledRadioButton("Other");
            genderGroup.add(rbtnMale);
            genderGroup.add(rbtnFemale);
            genderGroup.add(rbtnOther);
            genderPanel.add(rbtnMale);
            genderPanel.add(rbtnFemale);
            genderPanel.add(rbtnOther);
            addFormField(mainPanel, gbc, "Gender:", genderPanel);

            // Submit Button
            JButton btnSubmit = new JButton("Submit");
            styleButton(btnSubmit);
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            mainPanel.add(btnSubmit, gbc);

            // Add mainPanel to a JScrollPane
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            formFrame.add(scrollPane);

            // Action Listener for Trainer Selection
            trainerComboBox.addActionListener(e -> {
                String selectedTrainer = (String) trainerComboBox.getSelectedItem();
                if (selectedTrainer != null) {
                    String trainerType = selectedTrainer.split("\\(")[1].split("\\)")[0];
                    populatePackageComboBox(packageComboBox, trainerType);
                }
            });

            // Action Listener for Submit Button
            btnSubmit.addActionListener(e -> {
                String name = txtName.getText();
                String phone = txtPhone.getText();
                String email = txtEmail.getText();
                double height = Double.parseDouble(txtHeight.getText());
                double weight = Double.parseDouble(txtWeight.getText());
                String target = txtTarget.getText();
                String selectedTrainer = (String) trainerComboBox.getSelectedItem();
                String trainerId = selectedTrainer.split(" -")[0];
                String gender = rbtnMale.isSelected() ? "Male" : (rbtnFemale.isSelected() ? "Female" : "Other");
                String selectedPackage = (String) packageComboBox.getSelectedItem();
                String packageId = selectedPackage.split(" -")[0];

                addNewClientWithPackage(name, phone, email, height, weight, target, trainerId, gender, packageId);
                formFrame.dispose();
            });

            formFrame.setVisible(true);
        }

        private static void populatePackageComboBox(JComboBox<String> packageComboBox, String trainerType) {
            packageComboBox.removeAllItems();
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("SELECT id, duration, assistance FROM packages WHERE id IN (?, ?, ?, ?)")) {

                if ("Personal".equals(trainerType)) {
                    pstmt.setString(1, "2");
                    pstmt.setString(2, "4");
                    pstmt.setString(3, "6");
                    pstmt.setString(4, "8");
                } else {
                    pstmt.setString(1, "1");
                    pstmt.setString(2, "3");
                    pstmt.setString(3, "5");
                    pstmt.setString(4, "7");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String packageId = rs.getString("id");
                        String duration = rs.getString("duration");
                        String assistance = rs.getString("assistance");
                        packageComboBox.addItem(packageId + " - " + duration + " (" + assistance + ")");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        private static void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(label, gbc);

        gbc.gridx = 1;
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0, 102, 204), 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            ((JTextField) field).setFont(new Font("Arial", Font.PLAIN, 14));
        }
        panel.add(field, gbc);
    }

    private static JRadioButton createStyledRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        radioButton.setBackground(new Color(240, 248, 255));
        return radioButton;
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private static void populateTrainerComboBox(JComboBox<String> trainerComboBox) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, type FROM trainers")) {

            while (rs.next()) {
                String trainerId = rs.getString("id");
                String trainerName = rs.getString("name");
                String trainerType = rs.getString("type");
                trainerComboBox.addItem(trainerId + " - " + trainerName + " (" + trainerType + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
        private static void addNewClientWithPackage(String name, String phone, String email, double height, double weight, String target, String trainerId, String gender, String packageId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO clients (id, name, phone, email, height, weight, target, trainer_id, gender, package_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM clients");
            rs.next();
            int newId = rs.getInt(1) + 1;
            String clientId = String.format("%03d", newId);
            pstmt.setString(1, clientId);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setDouble(5, height);
            pstmt.setDouble(6, weight);
            pstmt.setString(7, target);
            pstmt.setString(8, trainerId);
            pstmt.setString(9, gender);
            pstmt.setString(10, packageId);
            pstmt.executeUpdate();
            displayPackageDetails(packageId);
            JOptionPane.showMessageDialog(null, "Client added successfully with ID: " + clientId, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    private static void displayPackageDetails(String packageId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM packages WHERE id = ?")) {
            pstmt.setString(1, packageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String duration = rs.getString("duration");
                    String assistance = rs.getString("assistance");
                    double money = rs.getDouble("money");
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><body>");
                    sb.append("<h2 style='color:blue;'>Selected Package Details</h2>");
                    sb.append("<p><strong>Duration:</strong> ").append(duration).append("</p>");
                    sb.append("<p><strong>Assistance:</strong> ").append(assistance).append("</p>");
                    sb.append("<p><strong>Price:</strong> $").append(money).append("</p>");
                    sb.append("</body></html>");
                    JLabel label = new JLabel(sb.toString());
                    JOptionPane.showMessageDialog(null, label, "Package Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}







