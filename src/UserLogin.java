import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/*=====================
User Login Frame - Ryan
======================*/

//needs user and admin role box / options
//needs username / email
//needs password 
//needs forgot password then send to email

class UserLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton;
    private JComboBox<String> roleBox; // User or Admin

    public UserLoginFrame() {
        setTitle("Login - Vehicular Cloud Console");
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        UIStyling.stylePanel(panel);

        // Role selection drop down menu
        JLabel roleLabel = new JLabel("Select Role:");
        UIStyling.styleLabel(roleLabel);
        panel.add(roleLabel);

        String[] roles = {"User", "Admin"};
        roleBox = new JComboBox<>(roles);
        panel.add(roleBox);

        // Username / Email box section
        JLabel usernameLabel = new JLabel("Username / Email:");
        UIStyling.styleLabel(usernameLabel);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        UIStyling.styleTextField(usernameField);
        panel.add(usernameField);

        // Password box section
        JLabel passwordLabel = new JLabel("Password:");
        UIStyling.styleLabel(passwordLabel);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        UIStyling.styleTextField(passwordField);
        panel.add(passwordField);

        // Buttons for login and forgot password
        loginButton = new JButton("Login");
        forgotPasswordButton = new JButton("Forgot Password");

        UIStyling.styleButton(loginButton);
        UIStyling.styleButton(forgotPasswordButton);

        panel.add(loginButton);
        panel.add(forgotPasswordButton);

        // Window title bar text
        JLabel titleLabel = UIStyling.createTitleLabel("Login");
        UIStyling.setupFrame(this, panel, titleLabel, "Login - Vehicular Cloud Console");

        // Action listeners
        loginButton.addActionListener(e -> authenticateUser());
        forgotPasswordButton.addActionListener(e -> recoverPassword());

        setVisible(true);
    }

    // Authenticate user or admin validation
    private void authenticateUser() {
        String role = (String) roleBox.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean loginSuccess = false;

        if (role.equals("Admin")) {
            // Hardcoded admin credentials for demo testing
            loginSuccess = username.equalsIgnoreCase("admin") && password.equals("admin123");
        } else {
            // Regular User: in real system, validate against file/database
            loginSuccess = true; // accept any non-empty for demo
        }

        if (loginSuccess) {
            // Logs login
            try (FileWriter writer = new FileWriter("user_login_data.txt", true)) {
                writer.write("Timestamp: " + LocalDateTime.now() + "\n");
                writer.write("Role: " + role + "\n");
                writer.write("Username: " + username + "\n");
                writer.write("Password: " + password + "\n"); // In real system, would encrypt
                writer.write("---------------------------------\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving login data: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(this, role + " login successful!");
            dispose(); // Close login frame
            new RoleSelectionFrame(); // Open RoleSelectionFrame
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials for " + role,
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Clear fields
        usernameField.setText("");
        passwordField.setText("");
    }

    // Simple forgot password
    private void recoverPassword() {
        String email = JOptionPane.showInputDialog(this, "Enter your email to recover password:");
        if (email != null && !email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password recovery instructions sent to: " + email);
        } else {
            JOptionPane.showMessageDialog(this, "Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}