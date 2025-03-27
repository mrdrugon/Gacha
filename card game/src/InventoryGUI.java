import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Arrays;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;

public class InventoryGUI {
    private JFrame frame;
    private JPanel inventoryPanel;
    private JPanel deckPanel;

    // Now the GUI uses the global Inventory instance.
    private Inventory inventory;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;
    private BufferedImage metallicTexture;

    public InventoryGUI(Inventory inventory) {
        this.inventory = inventory;
        loadTexture();
        initUI();
        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void loadTexture(){
        try{
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
        } catch (IOException e){
            metallicTexture = null;
        }
    }

    private void initUI() {
        frame = new JFrame("Inventory");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        deckPanel = new JPanel();
        deckPanel.setLayout(new GridLayout(1, DECK_SIZE));
        frame.add(deckPanel, BorderLayout.NORTH);

        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new GridLayout(0, 5));
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void updateDeckDisplay() {
        deckPanel.removeAll();
        List<Card> deck = inventory.getDeck().getDeck();
        for (int i = 0; i < DECK_SIZE; i++) {
            JButton cardButton;
            if (i < deck.size()) {
                Card card = deck.get(i);
                cardButton = new JButton(card.getName() + " (ATK: " + card.getAttack() + ", HP: " + card.getHealth() + ")");
                cardButton.setPreferredSize(new Dimension(100, 50));
                cardButton.addActionListener(e -> removeFromDeck(card));
            } else {
                cardButton = new JButton("Empty");
                cardButton.setPreferredSize(new Dimension(100, 50));
            }
            deckPanel.add(cardButton);
        }
        deckPanel.revalidate();
        deckPanel.repaint();
    }

    private void updateInventoryDisplay() {
        inventoryPanel.removeAll();
        Map<String, Integer> groupedCounts = new HashMap<>();
        Map<String, Card> cardReference = new HashMap<>();
        for (Card card : inventory.getCards()) {
            String name = card.getName();
            groupedCounts.put(name, groupedCounts.getOrDefault(name, 0) + 1);
            cardReference.put(name, card);
        }
        for (Map.Entry<String, Integer> entry : groupedCounts.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            Card card = cardReference.get(cardName);

            JButton cardPanel = new CardPanel(card, count);
            inventoryPanel.add(cardPanel);
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void addToDeck(Card card) {
        if (inventory.getDeck().isFull()) {
            return;
        }
        if (inventory.addCardToDeck(card)) {
            updateDeckDisplay();
            updateInventoryDisplay();
        }
    }

    private void removeFromDeck(Card card) {
        if (inventory.removeCardFromDeck(card)) {
            updateDeckDisplay();
            updateInventoryDisplay();
        }
    }

    private void upgradeCard(Card card) {
        int count = 0;
        for (Card c : inventory.getCards()) {
            if (c.getName().equals(card.getName())) {
                count++;
            }
        }
        if (count < UPGRADE_THRESHOLD) {
            return;
        }
        for (int i = 0; i < UPGRADE_THRESHOLD; i++) {
            inventory.removeOneCard(card);
        }
        Card upgradedCard = new Card(card.getName() + " +1", card.getAttack() + 1, card.getHealth() + 1, card.getRarity(), card.hasDoubleAttack(), card.hasRevive());
        inventory.addCards(Arrays.asList(upgradedCard));
        updateInventoryDisplay();
    }

    private class CardPanel extends JButton {
        private Card card;
        private int count;

        public CardPanel(Card card, int count) {
            this.card = card;
            this.count = count;
            setPreferredSize(new Dimension(120, 50));
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            addActionListener(e -> addToDeck(card));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color baseColor = getCardColor(card);
            RadialGradientPaint gradient = new RadialGradientPaint(new Point2D.Double(width / 2.0, height / 2.0),
                    Math.max(width, height) / 2.0f,
                    new float[]{0f, 1f},
                    new Color[]{baseColor.brighter(), baseColor.darker()}
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, width, height);

            if (metallicTexture != null) {
                if (card.getName().equals("Gold")) {
                    BufferedImage tintedTexture = applyTint(metallicTexture, new Color(255, 215, 0, 80));
                    g2d.drawImage(tintedTexture, 0, 0, width, height, null);
                } else if (card.getName().equals("Silver")) {
                    BufferedImage tintedTexture = applyTint(metallicTexture, new Color(100, 100, 100, 50));
                    g2d.drawImage(tintedTexture, 0, 0, width, height, null);
                }
            }

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String stats = "HP: " + card.getHealth() + "        ATK: " + card.getAttack();
            g2d.drawString(stats, 10, height - 20);

            String countText = "X("+count+")";
            g2d.drawString(countText, 10, height - 5);

            super.paintComponent(g);
        }

        private BufferedImage applyTint(BufferedImage image, Color tint){
            BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
            Graphics2D g2d = tinted.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.setColor(tint);
            g2d.setComposite(AlphaComposite.SrcOver);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
            g2d.dispose();
            return tinted;
        }
    }

    private Color getCardColor(Card card) {
        switch (card.getName()) {
            case "Red": return Color.RED;
            case "Blue": return Color.BLUE;
            case "Green": return Color.GREEN;
            case "Yellow": return Color.YELLOW;
            case "Silver": return new Color(192, 192, 192);
            case "Gold": return new Color(255, 215, 0);
            case "Rainbow": return Color.MAGENTA;
            default: return new Color(100, 100, 100);
        }
    }
}
