import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JButton exitButton;
    private JTextArea logArea;

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(null);

        // Player Name Label (Top Left)
        playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setBounds(50, 20, 200, 40);
        add(playerNameLabel);

        // Currency Label
        currencyLabel = new JLabel("Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setBounds(1400, 20, 200, 40);
        add(currencyLabel);

        // Load Images
        ImageIcon battleIcon = resizeImage("card game/cards/Battle.png", 400, 400);
        ImageIcon inventoryIcon = resizeImage("card game/cards/Battle.png", 200, 80);
        ImageIcon storeIcon = resizeImage("card game/cards/Battle.png", 200, 80);
        ImageIcon exitIcon = resizeImage("card game/cards/Battle.png", 200, 80);

        // Battle Button (Large)
        battleButton = new JButton("BATTLE", battleIcon);
        battleButton.setBounds(550, 200, 400, 400);
        styleButton(battleButton, 60);
        add(battleButton);

        // Inventory Button
        inventoryButton = new JButton("INVENTORY", inventoryIcon);
        inventoryButton.setBounds(1100, 250, 200, 80);
        styleButton(inventoryButton, 20);
        add(inventoryButton);

        // Store Button
        storeButton = new JButton("STORE", storeIcon);
        storeButton.setBounds(1100, 350, 200, 80);
        styleButton(storeButton, 20);
        add(storeButton);

        // Exit Button
        exitButton = new JButton("EXIT", exitIcon);
        exitButton.setBounds(1100, 450, 200, 80);
        styleButton(exitButton, 20);
        add(exitButton);

        // LOG AREA at the bottom
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(800, 100));
        add(logScroll, BorderLayout.SOUTH);

        // Button listeners for additional functionality
        battleButton.addActionListener(e -> mainFrame.startBattle());
        inventoryButton.addActionListener(e -> mainFrame.openInventory());
        storeButton.addActionListener(e -> new ShopGUI(mainFrame));
        exitButton.addActionListener(e -> System.exit(0));

        // Pass the log area to the main frame for logging messages
        mainFrame.setLogArea(logArea);
    }

    // Function to Resize Images
    private ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Function to Style Buttons
    private void styleButton(JButton button, int fontSize) {
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setFont(new Font("AniMe Matrix - MB_EN", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }
}

