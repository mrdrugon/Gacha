import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class InventoryGUI {
    private JFrame frame;
    private JPanel inventoryPanel;
    private JPanel deckPanel;

    // Now the GUI uses the global Inventory instance.
    private Inventory inventory;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;

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
        // Group cards by name and count them from the global inventory.
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

            JPanel cardPanel = new JPanel(new BorderLayout());
            JButton cardButton = new JButton(card.getName() + " (x" + count + ")");
            cardButton.setPreferredSize(new Dimension(120, 50));
            cardButton.addActionListener(e -> addToDeck(card));
            cardPanel.add(cardButton, BorderLayout.CENTER);

            if (count >= UPGRADE_THRESHOLD) {
                JButton upgradeButton = new JButton("Upgrade");
                upgradeButton.addActionListener(e -> upgradeCard(card));
                cardPanel.add(upgradeButton, BorderLayout.SOUTH);
            }
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
        // Check if there are enough copies available.
        int count = 0;
        for (Card c : inventory.getCards()) {
            if (c.getName().equals(card.getName())) {
                count++;
            }
        }
        if (count < UPGRADE_THRESHOLD) {
            return;
        }
        // Remove UPGRADE_THRESHOLD copies from the inventory.
        for (int i = 0; i < UPGRADE_THRESHOLD; i++) {
            inventory.removeOneCard(card);
        }
        // Create the upgraded card.
        Card upgradedCard = new Card(card.getName() + " +1", card.getAttack() + 1, card.getHealth() + 1, card.getRarity(), card.hasDoubleAttack(), card.hasRevive());
        inventory.addCards(Arrays.asList(upgradedCard));
        updateInventoryDisplay();
    }
}
