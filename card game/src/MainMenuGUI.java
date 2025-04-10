import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JButton exitButton;
    private JTextArea logArea;
    private Timer animationTimer;

    // Animation state
    private int dotSize = 2;
    private final int maxDotSize = 100;
    private final int dotSpacing = 50;
    private int rowsVisible = 1;
    private boolean isBlackDots = true;
    private boolean growing = true;

    // Timing control
    private int frameCounter = 0;
    private final int sizeIncreaseInterval = 2; // Increase size every frame
    private final int rowsIncreaseInterval = 6; // Increase rows every frame




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
        animationTimer = new Timer(16, e -> {
            int totalRows = getHeight() / dotSpacing + 2;

            frameCounter++;

            if (growing){
                if (frameCounter % sizeIncreaseInterval == 0 && dotSize < maxDotSize){
                    dotSize++;
                }
                if (frameCounter % rowsIncreaseInterval == 0 && rowsVisible < totalRows){
                    rowsVisible++;
                }
                if (dotSize >= maxDotSize && rowsVisible >= totalRows){
                  growing = false;
                }
            }else {
                dotSize = 2;
                rowsVisible = 1;
                isBlackDots = !isBlackDots;
                growing = true;
                frameCounter = 0;
            }
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

        g.setColor(isBlackDots ? Color.WHITE : Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(isBlackDots ? Color.BLACK : Color.WHITE);

        int totalCols = getWidth() / dotSpacing + 2;

        for (int row = 0; row < rowsVisible; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = col * dotSpacing + dotSpacing / 2 - dotSize / 2;
                int y = row * dotSpacing + dotSpacing / 2 - dotSize / 2;
                g.fillOval(x, y, dotSize, dotSize);
            }
        }
    }
}