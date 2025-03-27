import javax.swing.*;
import java.awt.*;
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

    // UI helper fields
    private Map<Integer, JButton> cardButtonsMap = new HashMap<>();
    private Set<Integer> deadCardsIds = new HashSet<>();

    public InventoryGUI(Inventory inventory) {
        this.inventory = inventory;
        initUI();
        updateDeckDisplay();
        updateInventoryDisplay();
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
            CardRenderer.renderCard(g2d, card, getWidth(), getHeight());
            super.paintComponent(g);
        }


    }
}