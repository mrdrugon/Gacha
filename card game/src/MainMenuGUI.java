import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private boolean growing = true;

    private boolean pauseBetweenPhases = false;
    private int pauseTimer = 0;
    private final int pauseDuration = 30; // frames to pause

    // Timing control
    private int frameCounter = 0;
    private final int sizeIncreaseInterval = 2; // Increase size every frame
    private final int rowsIncreaseInterval = 6; // Increase rows every frame

    private Color bgColor = Color.WHITE;
    private Color dotColor = Color.BLACK;

    private boolean transitioning = false;
    private float transitionProgress = 0f; // Goes from 0.0 to 1.0
    private final float transitionSpeed = 0.02f; // Lower = slower

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(null);
        setOpaque(false);

        // Player Name Label (Top Left)
        playerNameLabel = new JLabel(" Player: " + playerName);
        playerNameLabel.setBounds(50, 20, 250, 40);
        playerNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setBackground(new Color(0, 0, 0, 150)); // translucent black
        playerNameLabel.setOpaque(true);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(playerNameLabel);

        // Currency Label (Top Right)
        currencyLabel = new JLabel(" Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setBounds(1600, 20, 250, 40);
        currencyLabel.setForeground(Color.WHITE);
        currencyLabel.setBackground(new Color(0, 0, 0, 150)); // translucent black
        currencyLabel.setOpaque(true);
        currencyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(currencyLabel);

        // Load Images
        ImageIcon battleIcon = resizeImage("card game/cards/Battle.png", 1000, 800);
        ImageIcon inventoryIcon = resizeImage("card game/cards/Inventory.png", 600, 200);
        ImageIcon storeIcon = resizeImage("card game/cards/Store.png", 600, 200);
        ImageIcon exitIcon = resizeImage("card game/cards/Battle.png", 600, 200);

        // Battle Button (Large)
        battleButton = new JButton("BATTLE", battleIcon);
        battleButton.setBounds(100, 200, 1000, 800);
        styleButton(battleButton, 60);
        add(battleButton);

        // Inventory Button
        inventoryButton = new JButton("INVENTORY", inventoryIcon);
        inventoryButton.setBounds(1200, 200, 600, 200);
        styleButton(inventoryButton, 20);
        add(inventoryButton);

        // Store Button
        storeButton = new JButton("", storeIcon);
        storeButton.setBounds(1200, 500, 600, 200);
        styleButton(storeButton, 20);
        add(storeButton);

        // Exit Button
        exitButton = new JButton("EXIT", exitIcon);
        exitButton.setBounds(1200, 800, 600, 200);
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

        addHoverEffect(battleButton);
        addHoverEffect(inventoryButton);
        addHoverEffect(storeButton);
        addHoverEffect(exitButton);

        // Pass the log area to the main frame for logging messages
        mainFrame.setLogArea(logArea);

        // Start animation
        animationTimer = new Timer(16, e -> {
            int totalRows = getHeight() / dotSpacing + 2;
            frameCounter++;

            if (pauseBetweenPhases) {
                pauseTimer++;
                if (pauseTimer >= pauseDuration) {
                    pauseBetweenPhases = false;
                    pauseTimer = 0;
                }
                return; // skip update during pause
            }

            if (growing) {
                if (frameCounter % sizeIncreaseInterval == 0 && dotSize < maxDotSize) {
                    dotSize++;
                }
                if (frameCounter % rowsIncreaseInterval == 0 && rowsVisible < totalRows) {
                    rowsVisible++;
                }
                if (dotSize >= maxDotSize && rowsVisible >= totalRows) {
                    growing = false;
                    transitioning = true;
                    transitionProgress = 0f;
                }
            } else if (transitioning) {
                transitionProgress += transitionSpeed;
                if (transitionProgress >= 1f) {
                    transitioning = false;
                    growing = true;
                    frameCounter = 0;
                    dotSize = 2;
                    rowsVisible = 1;

                    bgColor = dotColor;
                    dotColor = getRandomDotColor(bgColor);
                }
            }

            repaint();
        });
        animationTimer.start();
    }

    // Function to Resize Images
    public static ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Function to Style Buttons
    public static void styleButton(JButton button, int fontSize) {
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setFont(new Font("AniMe Matrix - MB_EN", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new HoverEffect(button));
    }

    private Color getRandomColor() {
        float hue = (float) Math.random(); // random hue
        float saturation = 0.6f + (float)Math.random() * 0.4f; // stay colorful
        float brightness = 0.6f + (float)Math.random() * 0.4f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    private Color getRandomDotColor(Color background) {
        Color newColor;
        int attempts = 0;
        do {
            newColor = getRandomColor();
            attempts++;
        } while (!isContrasting(background, newColor) && attempts < 100); // Avoid infinite loop
        return newColor;
    }

    private boolean isContrasting(Color c1, Color c2) {
        // Use color difference formula based on brightness difference
        int b1 = (int) (0.299 * c1.getRed() + 0.587 * c1.getGreen() + 0.114 * c1.getBlue());
        int b2 = (int) (0.299 * c2.getRed() + 0.587 * c2.getGreen() + 0.114 * c2.getBlue());
        return Math.abs(b1 - b2) > 80; // Adjust this value if needed
    }

    private Color blendColors(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() * (1 - t) + c2.getRed() * t);
        int g = (int) (c1.getGreen() * (1 - t) + c2.getGreen() * t);
        int b = (int) (c1.getBlue() * (1 - t) + c2.getBlue() * t);
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Color currentBg = bgColor;
        if (transitioning) {
            currentBg = blendColors(bgColor, dotColor, transitionProgress);
        }
        g.setColor(currentBg);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(dotColor);

        int totalCols = getWidth() / dotSpacing + 2;

        for (int row = 0; row < rowsVisible; row++) {
            for (int col = 0; col < totalCols; col++) {
                int x = col * dotSpacing + dotSpacing / 2 - dotSize / 2;
                int y = row * dotSpacing + dotSpacing / 2 - dotSize / 2;
                g.fillOval(x, y, dotSize, dotSize);
            }
        }
    }

    private class HoverEffect extends MouseAdapter {
        private final JButton button;
        private final Rectangle originalBounds;
        private final Image originalImage;
        private final int growAmount = 10;

        public HoverEffect(JButton button) {
            this.button = button;
            this.originalBounds = new Rectangle(button.getBounds());

            // Extract the original image from the icon
            Icon icon = button.getIcon();
            if (icon instanceof ImageIcon) {
                originalImage = ((ImageIcon) icon).getImage();
            } else {
                originalImage = null;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            int newX = originalBounds.x - growAmount;
            int newY = originalBounds.y - growAmount;
            int newW = originalBounds.width + growAmount * 2;
            int newH = originalBounds.height + growAmount * 2;
            button.setBounds(newX, newY, newW, newH);

            if (originalImage != null) {
                Image scaled = originalImage.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaled));
            }

            button.repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            button.setBounds(originalBounds);

            if (originalImage != null) {
                Image scaled = originalImage.getScaledInstance(originalBounds.width, originalBounds.height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaled));
            }

            button.repaint();
        }
    }
}