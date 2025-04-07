import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
    private BufferedImage backgroundImage;
    private int scrollY = 0;
    private Timer animationTimer;

    public MainMenuGUI(String playerName, Main mainFrame) {
        try {
            backgroundImage = ImageIO.read(new File("card game/cards/Background.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        animationTimer = new Timer(1, e -> {
            scrollY -= 2;
            if (scrollY < 0) scrollY = backgroundImage.getHeight();
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

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            // Scroll background upward
            int imgHeight = backgroundImage.getHeight();
            int panelHeight = getHeight();

            // Loop vertically
            for (int y = -imgHeight + scrollY; y < panelHeight; y += imgHeight) {
                for (int x = 0; x < getWidth(); x += backgroundImage.getWidth()) {
                    g2d.drawImage(tintedImage(), x, y, this);
                }
            }
            g2d.dispose();
        }
    }

    // Apply dynamic gradient tint
    private BufferedImage tintedImage() {
        int w = backgroundImage.getWidth();
        int h = backgroundImage.getHeight();

        BufferedImage tinted = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tinted.createGraphics();

        // Draw original image
        g.drawImage(backgroundImage, 0, 0, null);

        // Overlay gradient with changing color
        float time = (System.currentTimeMillis() % 20000) / 20000f; // Slower hue shift
        Color startColor = Color.getHSBColor(time, 1.0f, 1.0f);
        Color endColor = Color.getHSBColor((time + 0.33f) % 1f, 1.0f, 1.0f);
        GradientPaint gp = new GradientPaint(0, scrollY, startColor, 0, scrollY + h, endColor, true);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);
        g.dispose();

        return tinted;
    }
}