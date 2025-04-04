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

            JPanel cardPanel = new JPanel();
            cardPanel.setPreferredSize(new Dimension(150, 200));
            cardPanel.setLayout(new BorderLayout());

            // Load the Rarity Effect Image
            BufferedImage rarityEffect = loadRarityEffect(card.getRarity(), 150, 200);

            // Create an image buffer for the card with the rarity effect
            BufferedImage cardImage = new BufferedImage(150, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cardImage.createGraphics();

            // Draw the rarity effect first
            g2d.drawImage(rarityEffect, 0, 0, null);

            // Render the card on top
            CardRenderer.renderCard(g2d, card, 150, 200);
            g2d.dispose();

            JLabel cardLabel = new JLabel(new ImageIcon(cardImage));
            cardPanel.add(cardLabel, BorderLayout.CENTER);

            panel.add(cardPanel, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

            cardIndex++;

            timer = new Timer(1000, e -> {
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
            JPanel cardPanel = new JPanel();
            cardPanel.setPreferredSize(new Dimension(100, 150));
            cardPanel.setLayout(new BorderLayout());

            // Create an image buffer and render the card onto it
            BufferedImage cardImage = new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cardImage.createGraphics();
            CardRenderer.renderCard(g2d, card, 100, 150);
            g2d.dispose();

            JLabel cardLabel = new JLabel(new ImageIcon(cardImage));
            cardPanel.add(cardLabel, BorderLayout.CENTER);


            // Hover Effect for Zooming In
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    BufferedImage zoomedImage = new BufferedImage(120, 170, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = zoomedImage.createGraphics();
                    CardRenderer.renderCard(g2d, card, 120, 170);
                    g2d.dispose();
                    cardLabel.setIcon(new ImageIcon(zoomedImage));
                    cardPanel.revalidate();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    BufferedImage normalImage = new BufferedImage(100, 150, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = normalImage.createGraphics();
                    CardRenderer.renderCard(g2d, card, 100, 150);
                    g2d.dispose();
                    cardLabel.setIcon(new ImageIcon(normalImage));
                    cardPanel.revalidate();
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
            BufferedImage effect = ImageIO.read(new File("card game/cards/RarityEffect.png")); // Update with actual path
            System.out.println("Loaded Rarity Effect Size: " + effect.getWidth() + "x" + effect.getHeight());

            BufferedImage resizedEffect = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedEffect.createGraphics();
            g2d.drawImage(effect.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            g2d.dispose();
            return resizedEffect;
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

    }
}