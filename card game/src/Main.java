import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextArea logArea;
    private Inventory inventory;
    private int playerPoints;
    private MainMenuGUI mainMenu;
    private InventoryGUI inventoryGUI;

    public Main() {
        inventory = new Inventory();
        playerPoints = 0;

        setTitle("Gacha Card Game");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Authentication Panel
        JPanel authPanel = buildAuthPanel();
        cardPanel.add(authPanel, "auth");

        mainMenu = new MainMenuGUI("Player", this);
        inventoryGUI = new InventoryGUI(inventory, this);

        cardPanel.add(mainMenu, "MainMenu");
        cardPanel.add(inventoryGUI.getPanel(), "Inventory");

        cardLayout.show(cardPanel, "MainMenu");

        logArea = new JTextArea(0, 0);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        add(cardPanel);
        cardLayout.show(cardPanel, "auth"); // Show login/signup first
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
        loginButton.addActionListener(e -> {
            String username = loginUsername.getText().trim();
            String password = new String(loginPassword.getPassword());

            if (!username.isEmpty() && !password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Create MainMenuGUI with the required arguments
                mainMenu = new MainMenuGUI(username, this);
                cardPanel.add(mainMenu, "mainMenu");

                cardLayout.show(cardPanel, "mainMenu");// Switch to main menu
                openPack();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Sign Up action listener.
        signupButton.addActionListener(e -> {
            String username = signupUsername.getText().trim();
            String password = new String(signupPassword.getPassword());
            String confirm = new String(signupConfirm.getPassword());

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Signup successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                tabbedPane.setSelectedIndex(0); // Switch to login tab
            }
        });

        return panel;
    }

    public void openGamePanel() {
        JPanel gamePanel = buildGamePanel();
        cardPanel.add(gamePanel, "game");
        cardLayout.show(cardPanel, "game");
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Game Screen"));
        return panel;
    }

    public void openPack() {
        Pack pack = PackFactory.createPack(PackType.NORMAL);
        java.util.List<ICard> newCards = pack.openPack();
        inventory.addCards(newCards);

        // Declare the overlay as an array so we can access it inside the lambda
        final PackOpeningPanel[] overlay = new PackOpeningPanel[1];

        overlay[0] = new PackOpeningPanel(newCards, () -> {
            getLayeredPane().remove(overlay[0]);
            getLayeredPane().repaint();
        });

        overlay[0].setBounds(0, 0, getWidth(), getHeight());
        getLayeredPane().add(overlay[0], JLayeredPane.POPUP_LAYER);
        overlay[0].requestFocusInWindow();
    }

    public void openInventory() {
        cardPanel.remove(inventoryGUI.getPanel()); // Remove old panel
        inventoryGUI = new InventoryGUI(inventory, this); // Recreate GUI with latest inventory
        cardPanel.add(inventoryGUI.getPanel(), "Inventory"); // Add new one
        cardLayout.show(cardPanel, "Inventory");
    }

    public void openMainMenu() {
        cardLayout.show(cardPanel, "MainMenu");
    }

    public void startBattle() {
        if (inventory.getDeck().getDeck().size() < Deck.DECK_SIZE) {
            log("You need 5 cards in your deck to battle!");
            return;
        }
        // Create enemy deck using factory.
        Pack enemyPack = PackFactory.createPack(PackType.NORMAL);
        java.util.List<ICard> enemyDeck = enemyPack.openPack();
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

    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
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
