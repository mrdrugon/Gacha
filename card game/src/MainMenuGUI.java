import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JButton exitButton;
    private JTextArea logArea;

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // TOP PANEL: Player Name (left) and Currency (right)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        playerNameLabel = new JLabel(playerName);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        playerNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        currencyLabel = new JLabel("Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(playerNameLabel, BorderLayout.WEST);
        topPanel.add(currencyLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL: Battle button with example.png image
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);

        // Load image from classpath using getResource()
        ImageIcon battleIcon = null;
        URL imgURL = getClass().getResource("/example.png");
        if (imgURL != null) {
            battleIcon = new ImageIcon(imgURL);
            // Scale the image to desired dimensions (e.g., 400x400)
            Image scaledImage = battleIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            battleIcon = new ImageIcon(scaledImage);
        } else {
            System.err.println("Could not find file: example.png");
            battleIcon = new ImageIcon();
        }

        battleButton = new JButton(battleIcon);
        battleButton.setBorderPainted(false);
        battleButton.setFocusPainted(false);
        battleButton.setContentAreaFilled(false);
        battleButton.addActionListener(e -> mainFrame.startBattle());
        centerPanel.add(battleButton);
        add(centerPanel, BorderLayout.CENTER);

        // RIGHT PANEL: Inventory, Store, and Exit buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        inventoryButton = createStyledButton("INVENTORY", new Dimension(180, 80), 20);
        storeButton = createStyledButton("STORE", new Dimension(180, 80), 20);
        exitButton = createStyledButton("EXIT", new Dimension(180, 80), 20);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(inventoryButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(storeButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(exitButton);
        rightPanel.add(Box.createVerticalGlue());
        add(rightPanel, BorderLayout.EAST);

        // LOG AREA at the bottom
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(800, 100));
        add(logScroll, BorderLayout.SOUTH);

        // Button listeners for additional functionality
        inventoryButton.addActionListener(e -> mainFrame.openInventory());
        storeButton.addActionListener(e -> new ShopGUI(mainFrame));
        exitButton.addActionListener(e -> System.exit(0));

        // Pass the log area to the main frame for logging messages
        mainFrame.setLogArea(logArea);
    }

    private JButton createStyledButton(String text, Dimension size, int fontSize) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        return button;
    }
}
