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
    private Timer animationTimer;

    // Animation state variables
    private int dotSize = 2;
    private final int maxDotSize = 100;
    private final int dotSpacing = 50;
    private int rowsVisible = 1;
    private boolean growing = true;

    private boolean pauseBetweenPhases = false;
    private int pauseTimer = 0;
    private final int pauseDuration = 30; // frames to pause

    // Timing control for the animation
    private int frameCounter = 0;
    private final int sizeIncreaseInterval = 2;
    private final int rowsIncreaseInterval = 6;

    private Color bgColor = Color.WHITE;
    private Color dotColor = Color.BLACK;

    private boolean transitioning = false;
    private float transitionProgress = 0f; // Goes from 0.0 to 1.0
    private final float transitionSpeed = 0.02f; // Lower = slower

    public MainMenuGUI(String playerName, Main mainFrame) {
        // Use absolute positioning so we can calculate component bounds manually.
        setLayout(null);
        setOpaque(false);

        // Get the current screen dimensions.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        // Base resolution for ratios.
        double baseWidth = 1920.0;
        double baseHeight = 1080.0;

        // Calculate battle button bounds based on ratios relative to 1920x1080.
        int battleX = (int) (100 * screenWidth / baseWidth);
        int battleY = (int) (200 * screenHeight / baseHeight);
        int battleWidth = (int) (1000 * screenWidth / baseWidth);
        int battleHeight = (int) (800 * screenHeight / baseHeight);

        // Calculate inventory button bounds.
        int inventoryX = (int) (1200 * screenWidth / baseWidth);
        int inventoryY = (int) (200 * screenHeight / baseHeight);
        int inventoryWidth = (int) (600 * screenWidth / baseWidth);
        int inventoryHeight = (int) (200 * screenHeight / baseHeight);

        // Calculate store button bounds.
        int storeX = (int) (1200 * screenWidth / baseWidth);
        int storeY = (int) (500 * screenHeight / baseHeight);
        int storeWidth = (int) (600 * screenWidth / baseWidth);
        int storeHeight = (int) (200 * screenHeight / baseHeight);

        // Calculate exit button bounds.
        int exitX = (int) (1200 * screenWidth / baseWidth);
        int exitY = (int) (800 * screenHeight / baseHeight);
        int exitWidth = (int) (600 * screenWidth / baseWidth);
        int exitHeight = (int) (200 * screenHeight / baseHeight);

        // Define labels (their positions are also scaled—adjust as needed)
        int labelWidth = (int)(300 * screenWidth / baseWidth);
        int labelHeight = (int)(40 * screenHeight / baseHeight);

        // Player Name label (top left)
        playerNameLabel = new JLabel(" Player: " + playerName);
        playerNameLabel.setBounds((int)(50 * screenWidth / baseWidth), (int)(20 * screenHeight / baseHeight), labelWidth, labelHeight);
        playerNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setBackground(new Color(0, 0, 0, 150));
        playerNameLabel.setOpaque(true);
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, (int)(20 * screenWidth / baseWidth)));
        add(playerNameLabel);

        // Currency label (top right)
        currencyLabel = new JLabel(" Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setBounds((int)(1600 * screenWidth / baseWidth), (int)(20 * screenHeight / baseHeight), labelWidth, labelHeight);
        currencyLabel.setForeground(Color.WHITE);
        currencyLabel.setBackground(new Color(0, 0, 0, 150));
        currencyLabel.setOpaque(true);
        currencyLabel.setFont(new Font("Arial", Font.BOLD, (int)(20 * screenWidth / baseWidth)));
        add(currencyLabel);

        // Load images for the buttons (ensure that the image paths are correct)
        ImageIcon battleIcon = resizeImage("card game/cards/Battle.png", battleWidth, battleHeight);
        ImageIcon inventoryIcon = resizeImage("card game/cards/Inventory.png", inventoryWidth, inventoryHeight);
        ImageIcon storeIcon = resizeImage("card game/cards/Store.png", storeWidth, storeHeight);
        // For the exit button, using the Battle image as a placeholder
        ImageIcon exitIcon = resizeImage("card game/cards/Battle.png", exitWidth, exitHeight);

        // Create and position the Battle button.
        battleButton = new JButton("BATTLE", battleIcon);
        battleButton.setBounds(battleX, battleY, battleWidth, battleHeight);
        styleButton(battleButton, (int)(60 * screenWidth / baseWidth)); // font scaled to resolution
        add(battleButton);

        // Create and position the Inventory button.
        inventoryButton = new JButton("INVENTORY", inventoryIcon);
        inventoryButton.setBounds(inventoryX, inventoryY, inventoryWidth, inventoryHeight);
        styleButton(inventoryButton, (int)(20 * screenWidth / baseWidth));
        add(inventoryButton);

        // Create and position the Store button.
        storeButton = new JButton("", storeIcon);
        storeButton.setBounds(storeX, storeY, storeWidth, storeHeight);
        styleButton(storeButton, (int)(20 * screenWidth / baseWidth));
        add(storeButton);

        // Create and position the Exit button.
        exitButton = new JButton("EXIT", exitIcon);
        exitButton.setBounds(exitX, exitY, exitWidth, exitHeight);
        styleButton(exitButton, (int)(20 * screenWidth / baseWidth));
        add(exitButton);

        // Set up action listeners for the buttons.
        battleButton.addActionListener(e -> mainFrame.startBattle());
        inventoryButton.addActionListener(e -> mainFrame.openInventory());
        storeButton.addActionListener(e -> mainFrame.openShop());
        exitButton.addActionListener(e -> System.exit(0));

        // Add hover effects to the buttons.
        addHoverEffect(battleButton);
        addHoverEffect(inventoryButton);
        addHoverEffect(storeButton);
        addHoverEffect(exitButton);

        // Animation timer for the animated (dot) background.
        animationTimer = new Timer(16, e -> {
            int totalRows = getHeight() / dotSpacing + 2;
            frameCounter++;

            if (pauseBetweenPhases) {
                pauseTimer++;
                if (pauseTimer >= pauseDuration) {
                    pauseBetweenPhases = false;
                    pauseTimer = 0;
                }
                return; // Skip update during pause
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

    // Utility to resize an image from the given file path.
    public static ImageIcon resizeImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Utility method to style buttons.
    public static void styleButton(JButton button, int fontSize) {
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("AniMe Matrix - MB_EN", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }

    // Add a hover enlargement effect to the given button.
    private void addHoverEffect(JButton button) {
        button.addMouseListener(new HoverEffect(button));
    }

    // Returns a random color.
    private Color getRandomColor() {
        float hue = (float) Math.random();
        float saturation = 0.6f + (float) Math.random() * 0.4f;
        float brightness = 0.6f + (float) Math.random() * 0.4f;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    // Finds a random dot color that contrasts with the background.
    private Color getRandomDotColor(Color background) {
        Color newColor;
        int attempts = 0;
        do {
            newColor = getRandomColor();
            attempts++;
        } while (!isContrasting(background, newColor) && attempts < 100);
        return newColor;
    }

    // Checks if two colors have sufficient contrast.
    private boolean isContrasting(Color c1, Color c2) {
        int b1 = (int) (0.299 * c1.getRed() + 0.587 * c1.getGreen() + 0.114 * c1.getBlue());
        int b2 = (int) (0.299 * c2.getRed() + 0.587 * c2.getGreen() + 0.114 * c2.getBlue());
        return Math.abs(b1 - b2) > 80;
    }

    // Blend two colors using a given ratio t (0.0 to 1.0).
    private Color blendColors(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() * (1 - t) + c2.getRed() * t);
        int g = (int) (c1.getGreen() * (1 - t) + c2.getGreen() * t);
        int b = (int) (c1.getBlue() * (1 - t) + c2.getBlue() * t);
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Use blended background color during transitions.
        Color currentBg = transitioning ? blendColors(bgColor, dotColor, transitionProgress) : bgColor;
        g.setColor(currentBg);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw animated dots on top of the background.
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

    // Inner class for the hover enlargement effect.
    private class HoverEffect extends MouseAdapter {
        private final JButton button;
        private final Rectangle originalBounds;
        private final Image originalImage;
        private final int growAmount = 10;

        public HoverEffect(JButton button) {
            this.button = button;
            this.originalBounds = new Rectangle(button.getBounds());
            Icon icon = button.getIcon();
            originalImage = (icon instanceof ImageIcon) ? ((ImageIcon) icon).getImage() : null;
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
