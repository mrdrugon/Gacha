import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InventoryGUI {
    private JFrame frame;
    private JPanel inventoryPanel;
    private JPanel deckPanel;

    private Map<Integer, Integer> inventory;  // Store counts by card ID
    private Map<Integer, Card> cardLookup;    // Map ID -> Card object
    private Map<Integer, Integer> deckCount;  // Store counts by card ID
    private List<Card> deck;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;

    public InventoryGUI(List<Card> inventoryList, List<Card> deck) {
        this.inventory = new HashMap<>();
        this.cardLookup = new HashMap<>();
        this.deck = deck;
        this.deckCount = new HashMap<>();

        // Populate inventory and lookup table
        for (Card card : inventoryList) {
            int cardId = card.getId();
            inventory.put(cardId, inventory.getOrDefault(cardId, 0) + 1);
            cardLookup.put(cardId, card);
        }

        for (Card card : deck) {
            int cardId = card.getId();
            deckCount.put(cardId, deckCount.getOrDefault(cardId, 0) + 1);
        }

        initUI();
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

        updateDeckDisplay();
        updateInventoryDisplay();

        frame.setVisible(true);
    }

    private void updateDeckDisplay() {
        deckPanel.removeAll();
        for (int i = 0; i < DECK_SIZE; i++) {
            JButton cardButton;
            if (i < deck.size()) {
                Card card = deck.get(i);
                cardButton = new JButton(card.getName() + " (ATK: " + card.getAttack() + ", HP: " + card.getHealth() + ")");
                cardButton.setPreferredSize(new Dimension(100, 50));
                cardButton.addActionListener(e -> removeFromDeck(card.getId()));
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
        for (Map.Entry<Integer, Integer> entry : inventory.entrySet()) {
            int cardId = entry.getKey();
            int count = entry.getValue();
            Card card = cardLookup.get(cardId);

            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BorderLayout());

            JButton cardButton = new JButton(card.getName() + " (x" + count + ")");
            cardButton.setPreferredSize(new Dimension(100, 50));
            cardButton.addActionListener(e -> addToDeck(cardId));
            cardPanel.add(cardButton, BorderLayout.CENTER);

            if (count >= UPGRADE_THRESHOLD) {
                JButton upgradeButton = new JButton("Upgrade");
                upgradeButton.addActionListener(e -> upgradeCard(cardId));
                cardPanel.add(upgradeButton, BorderLayout.SOUTH);
            }

            inventoryPanel.add(cardPanel);
        }
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void addToDeck(int cardId) {
        if (deck.size() >= DECK_SIZE || inventory.getOrDefault(cardId, 0) <= 0) {
            return;
        }

        Card card = cardLookup.get(cardId);
        deck.add(card);
        deckCount.put(cardId, deckCount.getOrDefault(cardId, 0) + 1);
        inventory.put(cardId, inventory.get(cardId) - 1);

        if (inventory.get(cardId) == 0) {
            inventory.remove(cardId);
        }

        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void removeFromDeck(int cardId) {
        for (int i =0; i < deck.size(); i++) {
            if (deck.get(i).getId() == cardId) {
                deck.remove(i);
                deckCount.put(cardId, deckCount.get(cardId) - 1);
                if (deckCount.get(cardId) == 0) {
                    deckCount.remove(cardId);
                }
                inventory.put(cardId, inventory.getOrDefault(cardId, 0) + 1);
                break;
            }
        }
            updateDeckDisplay();
            updateInventoryDisplay();
    }

    private void upgradeCard(int cardId) {
        if (inventory.getOrDefault(cardId, 0) < UPGRADE_THRESHOLD) {
            return;
        }

        Card oldCard = cardLookup.get(cardId);
        inventory.put(cardId, inventory.get(cardId) - UPGRADE_THRESHOLD);
        if (inventory.get(cardId) == 0) {
            inventory.remove(cardId);
        }

        int upgradeCardId = new Random().nextInt(1000000);
        while (cardLookup.containsKey(upgradeCardId)){
            upgradeCardId = new Random().nextInt(1000000);
        }

        Card upgradedCard = new Card(oldCard.getName() + " +1", oldCard.getAttack() + 1, oldCard.getHealth() + 1, oldCard.getRarity(), oldCard.hasDoubleAttack(), oldCard.hasRevive());

        inventory.put(upgradeCardId, inventory.getOrDefault(upgradeCardId, 0) + 1);
        cardLookup.put(upgradeCardId, upgradedCard);

        updateInventoryDisplay();
    }
}
