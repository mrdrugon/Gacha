import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JButton exitButton;
    private JTextArea logArea;
    private int scrollY = 0;
    private Timer animationTimer;

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(null);
        setOpaque(false);

        // Player Name Label (Top Left)
        playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setBounds(50, 20, 200, 40);
        add(playerNameLabel);

        // Currency Label
        currencyLabel = new JLabel("Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setBounds(1400, 20, 200, 40);
        add(currencyLabel);

        // Load Images
        ImageIcon battleIcon = resizeImage("card game/cards/Battle.png", 700, 700);
        ImageIcon inventoryIcon = resizeImage("card game/cards/Inventory.png", 200, 200);
        ImageIcon storeIcon = resizeImage("card game/cards/Store.png", 200, 200);
        ImageIcon exitIcon = resizeImage("card game/cards/Battle.png", 200, 200);

        // Battle Button (Large)
        battleButton = new JButton("BATTLE", battleIcon);
        battleButton.setBounds(250, 200, 700, 700);
        styleButton(battleButton, 60);
        add(battleButton);

        // Inventory Button
        inventoryButton = new JButton("INVENTORY", inventoryIcon);
        inventoryButton.setBounds(1100, 200, 200, 200);
        styleButton(inventoryButton, 20);
        add(inventoryButton);

        // Store Button
        storeButton = new JButton("STORE", storeIcon);
        storeButton.setBounds(1100, 450, 200, 200);
        styleButton(storeButton, 20);
        add(storeButton);

        // Exit Button
        exitButton = new JButton("EXIT", exitIcon);
        exitButton.setBounds(1100, 700, 200, 200);
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

        // Start animation
        animationTimer = new Timer(30, e -> {
            scrollY -= 1;
            if (scrollY < -40) scrollY = 0; // Wrap cleanly every full dot spacing
            repaint();
        });
        animationTimer.start();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int dotSize = 10;
        int spacing = 40; // Distance between dots
        int offsetY = scrollY % spacing;

        // 1. Draw the dot grid
        for (int y = -spacing + offsetY; y < panelHeight; y += spacing) {
            for (int x = 0; x < panelWidth; x += spacing) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.fillOval(x, y, dotSize, dotSize);
            }
        }

        // 2. Create a transparent animated gradient overlay
        float time = (System.currentTimeMillis() % 10000) / 10000f;
        Color tintColor1 = Color.getHSBColor(time, 0.7f, 1f);
        Color tintColor2 = Color.getHSBColor((time + 0.1f) % 1f, 0.7f, 1f);
        GradientPaint gradient = new GradientPaint(0, 0, tintColor1, 0, panelHeight, tintColor2);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // transparent overlay
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        g2d.setComposite(AlphaComposite.SrcOver); // Reset

        // 3. Fade at top
        GradientPaint topFade = new GradientPaint(0, 0, new Color(0, 0, 0, 255), 0, 100, new Color(0, 0, 0, 0));
        g2d.setPaint(topFade);
        g2d.fillRect(0, 0, panelWidth, 100);

        // 4. Fade at bottom
        GradientPaint bottomFade = new GradientPaint(0, panelHeight - 100, new Color(0, 0, 0, 0), 0, panelHeight, new Color(0, 0, 0, 255));
        g2d.setPaint(bottomFade);
        g2d.fillRect(0, panelHeight - 100, panelWidth, 100);

        g2d.dispose();
    }
}

