import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginSignupGUI extends JFrame {
    private JTabbedPane tabbedPane;

    // Login panel components
    private JPanel loginPanel;
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JButton loginButton;

    // Signup panel components
    private JPanel signupPanel;
    private JTextField signupUsernameField;
    private JPasswordField signupPasswordField;
    private JPasswordField signupConfirmPasswordField;
    private JButton signupButton;

    public LoginSignupGUI() {
        setTitle("Login / Signup");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Build login panel
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(15);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(15);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        loginPanel.add(loginButton, gbc);

        // Build signup panel
        signupPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        signupUsernameField = new JTextField(15);
        signupPanel.add(signupUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        signupPasswordField = new JPasswordField(15);
        signupPanel.add(signupPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        signupConfirmPasswordField = new JPasswordField(15);
        signupPanel.add(signupConfirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        signupButton = new JButton("Sign Up");
        signupPanel.add(signupButton, gbc);

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Sign Up", signupPanel);

        add(tabbedPane);

        // Button listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in both fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Player player = PlayerManager.getPlayer(username);
        if(player == null){
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(player.verifyPassword(password)){
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Launch the main game screen and close the login window.
            new Main();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignup() {
        String username = signupUsernameField.getText().trim();
        String password = new String(signupPasswordField.getPassword());
        String confirmPassword = new String(signupConfirmPasswordField.getPassword());

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(PlayerManager.playerExists(username)){
            JOptionPane.showMessageDialog(this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Player newPlayer = new Player(username, password);
        PlayerManager.addPlayer(newPlayer);
        JOptionPane.showMessageDialog(this, "Signup successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        // Optionally switch to the login tab.
        tabbedPane.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginSignupGUI().setVisible(true);
        });
    }
}
