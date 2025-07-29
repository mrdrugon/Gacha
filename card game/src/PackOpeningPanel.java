import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PackOpeningPanel extends JPanel {
    private List<ICard> cards;
    private int currentCardIndex = 0;
    private Timer revealTimer;
    private int cardIndex = 0;
    private boolean packOpened = false;
    private boolean isAnimating = false;
    private Timer cardTimer;
    private Timer pulseTimer;
    private List<CardPanel> displayedPanels = new ArrayList<>();
    private JLabel packLabel;
    private Image packImage;
    private Runnable onFinish; // Called after final click

    public PackOpeningPanel(List<ICard> cards, Runnable onFinish) {
        this.cards = cards;
        this.onFinish = onFinish;
        setOpaque(false);
        setLayout(null);

        packImage = new ImageIcon("card game/cards/pack.png").getImage();
        packLabel = new JLabel(new ImageIcon(packImage));
        packLabel.setHorizontalAlignment(JLabel.CENTER);
        packLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!packOpened && !isAnimating) {
                    startPackOpening();
                } else if (isAnimating) {
                    skipToEnd();
                }
            }
        });

        setLayout(new BorderLayout()); // Important for proper placement
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(packLabel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void startPackOpening() {
        packOpened = true;
        isAnimating = true;

        // Flash white
        packLabel.setIcon(new ImageIcon(createWhiteImage(packImage.getWidth(null), packImage.getHeight(null))));
        Timer flashTimer = new Timer(300, e -> {
            ((Timer) e.getSource()).stop();
            removeAll();
            showNextCard();
        });
        flashTimer.setRepeats(false);
        flashTimer.start();
        repaint();
    }

    private void skipToEnd() {
        if (cardTimer != null) cardTimer.stop();
        showFinalScreen();
    }

    private Image createWhiteImage(int width, int height) {
        BufferedImage whiteImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = whiteImg.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return whiteImg;
    }

    private void showNextCard() {
        if (cardIndex >= cards.size()) {
            showFinalScreen();
            return;
        }

        removeAll();
        ICard card = cards.get(cardIndex);
        CardPanel panel = new CardPanel(card, 250, 300);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cardTimer != null) cardTimer.stop();
                cardIndex++;
                showNextCard();
            }
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(panel);
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        cardIndex++;
        cardTimer = new Timer(1200, e -> {
            cardTimer.stop();
            showNextCard();
        });
        cardTimer.setRepeats(false);
        cardTimer.start();
    }

    private void showFinalScreen() {
        removeAll();
        isAnimating = false;

        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
        grid.setOpaque(false);

        for (ICard card : cards) {
            CardPanel panel = new CardPanel(card, 200, 250);
            displayedPanels.add(panel);
            grid.add(panel);
        }

        pulseTimer = new Timer(30, e -> displayedPanels.forEach(CardPanel::updatePulse));
        pulseTimer.start();

        JLabel tip = new JLabel("Click anywhere to continue", JLabel.CENTER);
        tip.setFont(new Font("Arial", Font.ITALIC, 14));
        tip.setForeground(Color.LIGHT_GRAY);

        add(grid, BorderLayout.CENTER);
        add(tip, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cleanupAndFinish();
            }
        });
        revalidate();
        repaint();
    }

    private void cleanupAndFinish() {
        if (pulseTimer != null) pulseTimer.stop();
        if (cardTimer != null) cardTimer.stop();
        if (onFinish != null) onFinish.run();
    }

    private void startReveal() {
        revealTimer = new Timer(500, e -> {
            currentCardIndex++;
            repaint();

            if (currentCardIndex >= cards.size()) {
                revealTimer.stop();
            }
        });
        revealTimer.start();
        repaint();
    }

    public void openCards(List<ICard> cards, JLayeredPane layeredPane) {
        PackOpeningPanel overlay = new PackOpeningPanel(cards, () -> {});
        overlay.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        overlay.requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    private BufferedImage loadRarityEffect(String rarity, int width, int height) {
        try {
            BufferedImage glow = ImageIO.read(new File("card game/cards/RarityEffect.png"));
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = combined.createGraphics();

            // Tint based on rarity
            Color tint = switch (rarity.toLowerCase()) {
                case "common" -> new Color(150, 150, 150, 200);
                case "uncommon" -> new Color(25, 113, 0, 200);
                case "rare" -> new Color(0, 0, 255, 200);
                case "epic" -> new Color(128, 0, 128, 200);
                case "legendary" -> new Color(255, 215, 0, 200);
                default -> new Color(255, 255, 255, 200);
            };

            // Draw tinted background
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
            g2d.setColor(tint);
            g2d.fillRect(0, 0, combined.getWidth(), combined.getHeight());

            // Overlay the glow image (optional)
            g2d.drawImage(glow, 0, 0, combined.getWidth(), combined.getHeight(), null);
            g2d.dispose();

            return combined;
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }

    class CardPanel extends JPanel {
        private BufferedImage rarityEffect;
        private ICard card;
        private int width, height;
        private float pulseScale = 1.0f;
        private float pulseSpeed = 0.05f;
        private float pulsePhase = 0f;

        public CardPanel(ICard card, int width, int height) {
            this.card = card;
            this.width = width;
            this.height = height;
            this.rarityEffect = loadRarityEffect(card.getRarity(), width, height);
            setPreferredSize(new Dimension(width + 40, height + 40)); // extra space for pulsing
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            // Anti-aliasing and rendering quality
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate center for pulsing
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            int effectW = (int) ((width + 20) * pulseScale);
            int effectH = (int) ((height + 20) * pulseScale);
            int x = centerX - effectW / 2;
            int y = centerY - effectH / 2;

            if (rarityEffect != null) {
                g2d.drawImage(rarityEffect, x, y, effectW, effectH, this);
            }

            // Draw the card itself centered over the effect
            int cardX = centerX - width / 2;
            int cardY = centerY - height / 2;
            g2d.translate(cardX, cardY);
            CardRenderer.renderCard(g2d, card, width, height);

            g2d.dispose();
        }

        public void updatePulse() {
            pulsePhase += pulseSpeed;
            pulseScale = 1.0f + 0.05f * (float) Math.sin(pulsePhase);
            repaint();
        }
    }
}
