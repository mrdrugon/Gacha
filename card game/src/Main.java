import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Game UI components
    private JTextArea logArea;
    private Inventory inventory;
    private int playerPoints;

    public Main() {
        // Initialize game state
        inventory = new Inventory();
        playerPoints = 0;

        setTitle("Gacha Card Game");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use CardLayout to switch between auth and game panels.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create and add the authentication panel (with Login and Sign Up tabs)
        JPanel authPanel = buildAuthPanel();
        cardPanel.add(authPanel, "auth");

        // Create and add the main game panel
        JPanel gamePanel = buildGamePanel();
        cardPanel.add(gamePanel, "game");

        add(cardPanel);
        // Show the authentication panel first.
        cardLayout.show(cardPanel, "auth");
    }

    private JPanel buildAuthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // ----- Login Tab -----
        JPanel loginTab = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginTab.add(loginTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginTab.add(new JLabel("Username:"), gbc);
        JTextField loginUsername = new JTextField(15);
        gbc.gridx = 1;
        loginTab.add(loginUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginTab.add(new JLabel("Password:"), gbc);
        JPasswordField loginPassword = new JPasswordField(15);
        gbc.gridx = 1;
        loginTab.add(loginPassword, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginTab.add(loginButton, gbc);

        // ----- Sign Up Tab -----
        JPanel signupTab = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        JLabel signupTitle = new JLabel("Sign Up");
        signupTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.gridwidth = 2;
        signupTab.add(signupTitle, gbc2);
        gbc2.gridwidth = 1;

        gbc2.gridx = 0;
        gbc2.gridy = 1;
        signupTab.add(new JLabel("Username:"), gbc2);
        JTextField signupUsername = new JTextField(15);
        gbc2.gridx = 1;
        signupTab.add(signupUsername, gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 2;
        signupTab.add(new JLabel("Password:"), gbc2);
        JPasswordField signupPassword = new JPasswordField(15);
        gbc2.gridx = 1;
        signupTab.add(signupPassword, gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 3;
        signupTab.add(new JLabel("Confirm Password:"), gbc2);
        JPasswordField signupConfirm = new JPasswordField(15);
        gbc2.gridx = 1;
        signupTab.add(signupConfirm, gbc2);

        JButton signupButton = new JButton("Sign Up");
        gbc2.gridx = 0;
        gbc2.gridy = 4;
        gbc2.gridwidth = 2;
        signupTab.add(signupButton, gbc2);

        // Add tabs to the tabbed pane.
        tabbedPane.addTab("Login", loginTab);
        tabbedPane.addTab("Sign Up", signupTab);
        panel.add(tabbedPane, BorderLayout.CENTER);

        // ----- Action Listeners -----

        // Login action listener.
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = loginUsername.getText().trim();
                String password = new String(loginPassword.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(Main.this, "Please fill in both fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Player player = PlayerManager.getPlayer(username);
                if (player == null) {
                    JOptionPane.showMessageDialog(Main.this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (player.verifyPassword(password)) {
                    JOptionPane.showMessageDialog(Main.this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Switch to the game panel.
                    cardLayout.show(cardPanel, "game");
                } else {
                    JOptionPane.showMessageDialog(Main.this, "Incorrect password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Sign Up action listener.
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = signupUsername.getText().trim();
                String password = new String(signupPassword.getPassword());
                String confirm = new String(signupConfirm.getPassword());
                if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(Main.this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!password.equals(confirm)) {
                    JOptionPane.showMessageDialog(Main.this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (PlayerManager.playerExists(username)) {
                    JOptionPane.showMessageDialog(Main.this, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Player newPlayer = new Player(username, password);
                PlayerManager.addPlayer(newPlayer);
                JOptionPane.showMessageDialog(Main.this, "Signup successful! Please login now.", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Optionally switch to the login tab.
                tabbedPane.setSelectedIndex(0);
            }
        });

        return panel;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Log area for game messages.
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        panel.add(logScroll, BorderLayout.CENTER);

        // Button panel with game options.
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        JButton openPackButton = new JButton("Open Pack");
        JButton inventoryButton = new JButton("Inventory");
        JButton battleButton = new JButton("Battle");
        JButton shopButton = new JButton("Shop");
        buttonPanel.add(openPackButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(battleButton);
        buttonPanel.add(shopButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions.
        openPackButton.addActionListener(e -> openPack());
        inventoryButton.addActionListener(e -> openInventory());
        battleButton.addActionListener(e -> startBattle());
        shopButton.addActionListener(e -> new ShopGUI(Main.this));

        return panel;
    }

    private void openPack() {
        java.util.List<ICard> newCards = new NormalPack().openPack();
        inventory.addCards(newCards);
        new PackOpeningGUI(newCards);
    }

    private void openInventory() {
        new InventoryGUI(inventory);
    }

    private void startBattle() {
        if (inventory.getDeck().getDeck().size() < Deck.DECK_SIZE) {
            log("You need 5 cards in your deck to battle!");
            return;
        }
        java.util.List<ICard> enemyDeck = new NormalPack().openPack();
        new BattleGUI(inventory.getDeck().getDeck(), enemyDeck, this);
    }

    public void battleResult(String outcome) {
        if (outcome.equals("win")) {
            log("You won! You get a new pack.");
            openPack();
        } else if (outcome.equals("loss")) {
            log("You lost! You earn points.");
            playerPoints += 10;
        } else if (outcome.equals("tie")) {
            log("The battle ended in a tie! You get half rewards.");
            playerPoints += 5;
        }
    }

    public int getPlayerPoints() {
        return playerPoints;
    }

    public void addPlayerPoints(int amount) {
        playerPoints += amount;
        log("You now have " + playerPoints + " points.");
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void log(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainFrame = new Main();
            mainFrame.setVisible(true);
        });
    }
}
