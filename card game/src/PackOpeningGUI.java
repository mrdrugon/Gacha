import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PackOpeningGUI extends JFrame {
    private JPanel panel;
    private JLabel displayLabel;
    private Timer timer;
    private List<ICard> cards;
    private int cardIndex = 0;
    private boolean packOpened = false;
    private boolean showingFinalScreen = false;
    private ImageIcon packIcon;

    public PackOpeningGUI(List<ICard> cards) {
        this.cards = cards;
        setTitle("Pack Opening");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new BorderLayout());
        displayLabel = new JLabel();
        displayLabel.setHorizontalAlignment(JLabel.CENTER);
        packIcon = new ImageIcon("card game/cards/pack.png");

        resizePackImage();

        panel.add(displayLabel, BorderLayout.CENTER);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (!packOpened) {
                    openPackAnimation();
                } else if (!showingFinalScreen) {
                    showNextCard();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePackImage();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void resizePackImage() {
        int width = getWidth();
        int height = getHeight();
        Image scaledImage = packIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        displayLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void openPackAnimation() {
        packOpened = true;
        panel.removeAll();
        JLabel openingLabel = new JLabel("Opening...", JLabel.CENTER);
        openingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(openingLabel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

        Timer delay = new Timer(1000, e -> {
            ((Timer) e.getSource()).stop();
            showNextCard();
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void showNextCard() {
        if (cardIndex < cards.size()) {
            ICard card = cards.get(cardIndex);
            panel.removeAll();

            CardPanel cardPanel = new CardPanel(card, 150, 200);

            // Use GridBagLayout to center the card both vertically and horizontally
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;

            centerWrapper.add(cardPanel, gbc);
            panel.add(centerWrapper, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

            cardIndex++;

            timer = new Timer(1200, e -> {
                timer.stop();
                showNextCard();
            });

            timer.setRepeats(false);
            timer.start();
        } else {
            showFinalScreen();
        }
    }
    private void showFinalScreen() {
        showingFinalScreen = true;
        panel.removeAll();

        JPanel finalPanel = new JPanel(new GridLayout(0, 3, 10, 10));

        for (ICard card : cards) {
            CardPanel cardPanel = new CardPanel(card, 100, 150);

            // Optional: Add hover effect to enlarge
            cardPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    cardPanel.setPreferredSize(new Dimension(120 + 40, 170 + 40));
                    cardPanel.width = 120;
                    cardPanel.height = 170;
                    cardPanel.revalidate();
                    cardPanel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    cardPanel.setPreferredSize(new Dimension(100 + 40, 150 + 40));
                    cardPanel.width = 100;
                    cardPanel.height = 150;
                    cardPanel.revalidate();
                    cardPanel.repaint();
                }
            });

            finalPanel.add(cardPanel);
        }

        panel.add(finalPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private BufferedImage loadRarityEffect(String rarity, int width, int height) {
        try {
            BufferedImage glow = ImageIO.read(new File("card game/cards/RarityEffect.png"));
            BufferedImage combined = new BufferedImage(width + 20, height + 20, BufferedImage.TYPE_INT_ARGB);
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
            return new BufferedImage(width + 20, height + 20, BufferedImage.TYPE_INT_ARGB);
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

            Timer animationTimer = new Timer(30, e -> {
                pulsePhase += pulseSpeed;
                pulseScale = 1.0f + 0.1f * (float) Math.sin(pulsePhase); // Pulses between 1.0 and 1.1
                repaint();
            });
            animationTimer.start();
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
    }
}