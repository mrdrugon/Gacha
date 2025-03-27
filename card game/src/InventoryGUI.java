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

public class InventoryGUI {
    private JFrame frame;
    private JPanel inventoryPanel;
    private JPanel deckPanel;
    private Inventory inventory;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;
    private BufferedImage metallicTexture;

    // UI helper fields
    private Map<Integer, JButton> cardButtonsMap = new HashMap<>();
    private Set<Integer> deadCardsIds = new HashSet<>();

    public InventoryGUI(Inventory inventory) {
        this.inventory = inventory;
        loadTexture();
        initUI();
        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void loadTexture() {
        try {
            metallicTexture = ImageIO.read(new File("card game/cards/metallicTexture.png"));
        } catch (IOException e) {
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
        cardButtonsMap.clear();
        deadCardsIds.clear();

        // Get the deck from the inventory.
        List<ICard> deck = inventory.getDeck().getDeck();
        for (ICard card : deck) {
            int cardId = card.getId();
            JButton cardButton = new JButton("<html>"
                    + card.getName()
                    + "<br>ATK: " + card.getAttack()
                    + "<br>HP: " + card.getHealth() + "</html>");
            if (card.getHealth() <= 0) {
                deadCardsIds.add(cardId);
            }
            if (deadCardsIds.contains(cardId)) {
                cardButton.setEnabled(false);
                cardButton.setBackground(Color.GRAY);
            } else {
                // In this UI, clicking a deck card will remove it from the deck.
                cardButton.addActionListener(e -> removeFromDeck(card));
                cardButtonsMap.put(cardId, cardButton);
            }
            deckPanel.add(cardButton);
        }
        deckPanel.revalidate();
        deckPanel.repaint();
    }

    private void updateInventoryDisplay() {
        inventoryPanel.removeAll();
        Map<String, Integer> groupedCounts = new HashMap<>();
        Map<String, ICard> cardReference = new HashMap<>();

        for (ICard card : inventory.getCards()) {
            String name = card.getName();
            groupedCounts.put(name, groupedCounts.getOrDefault(name, 0) + 1);
            cardReference.put(name, card);
        }
        for (Map.Entry<String, Integer> entry : groupedCounts.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            ICard card = cardReference.get(cardName);
            JButton cardPanel = new CardPanel(card, count);
            inventoryPanel.add(cardPanel);
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void addToDeck(ICard card) {
        if (inventory.getDeck().isFull()) {
            return;
        }
        if (inventory.addCardToDeck(card)) {
            updateDeckDisplay();
            updateInventoryDisplay();
        }
    }

    private void removeFromDeck(ICard card) {
        if (inventory.removeCardFromDeck(card)) {
            updateDeckDisplay();
            updateInventoryDisplay();
        }
    }

    private void upgradeCard(ICard card) {
        int count = 0;
        for (ICard c : inventory.getCards()) {
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
        // Create an upgraded card.
        // For rarity, we assume ICard has a getRarity() method; if not, we can cast to BasicCard.
        String rarity = (card instanceof BasicCard) ? ((BasicCard) card).getRarity() : "Common";
        ICard upgradedCard = new BasicCard(card.getName() + " +1", card.getAttack() + 1, card.getHealth() + 1, rarity);
        inventory.addCards(Arrays.asList(upgradedCard));
        updateInventoryDisplay();
    }

    private class CardPanel extends JButton {
        private ICard card;
        private int count;

        public CardPanel(ICard card, int count) {
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
            RadialGradientPaint gradient = new RadialGradientPaint(
                    new Point2D.Double(width / 2.0, height / 2.0),
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

            String countText = "X(" + count + ")";
            g2d.drawString(countText, 10, height - 5);

            super.paintComponent(g);
        }

        private BufferedImage applyTint(BufferedImage image, Color tint) {
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

    private Color getCardColor(ICard card) {
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
