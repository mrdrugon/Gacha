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
        this.deck = deck; // Ensure deck state is passed correctly
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
        deckPanel.revalidate();  // Make sure the panel is updated
        deckPanel.repaint();
    }

    private void updateInventoryDisplay() {
        inventoryPanel.removeAll();

        Map<String, Integer> groupedInventory = new HashMap<>();
        Map<String, Card> cardReference = new HashMap<>();

        // Update the grouped inventory only based on the cards remaining in inventory
        for (Map.Entry<Integer, Integer> entry : inventory.entrySet()) {
            Card card = cardLookup.get(entry.getKey());
            String cardName = card.getName();
            groupedInventory.put(cardName, groupedInventory.getOrDefault(cardName, 0) + entry.getValue());
            cardReference.putIfAbsent(cardName, card);
        }

        for (Map.Entry<String, Integer> entry : groupedInventory.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            Card card = cardReference.get(cardName);

            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BorderLayout());

            JButton cardButton = new JButton(card.getName() + " (x" + count + ")");
            cardButton.setPreferredSize(new Dimension(120, 50));
            cardButton.addActionListener(e -> addToDeck(card.getId()));

            cardPanel.add(cardButton, BorderLayout.CENTER);

            if (count >= UPGRADE_THRESHOLD) {
                JButton upgradeButton = new JButton("Upgrade");
                upgradeButton.addActionListener(e -> upgradeCard(card.getId()));
                cardPanel.add(upgradeButton, BorderLayout.SOUTH);
            }

            inventoryPanel.add(cardPanel);
        }

        inventoryPanel.revalidate();  // Make sure the panel is updated
        inventoryPanel.repaint();
    }

    private void addToDeck(int cardId) {
        if (deck.size() >= DECK_SIZE || inventory.getOrDefault(cardId, 0) <= 0) {
            return; // Can't add more cards if deck is full or card is unavailable
        }

        Card card = cardLookup.get(cardId);
        deck.add(card);  // Add card to deck

        // Update deck count
        deckCount.put(cardId, deckCount.getOrDefault(cardId, 0) + 1);

        // Reduce card count in inventory
        inventory.put(cardId, inventory.get(cardId) - 1);
        if (inventory.get(cardId) == 0) {
            inventory.remove(cardId);  // Remove card if count reaches 0
        }

        // Update the UI
        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void removeFromDeck(int cardId) {
        // Look for the card in the deck and remove it
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getId() == cardId) {
                deck.remove(i);  // Remove card from deck

                // Update deck count
                deckCount.put(cardId, Math.max(deckCount.getOrDefault(cardId, 0) - 1, 0));
                if (deckCount.get(cardId) == 0) {
                    deckCount.remove(cardId);
                }

                // Add the card back to the inventory
                inventory.put(cardId, inventory.getOrDefault(cardId, 0) + 1);

                // Update the UI
                updateDeckDisplay();
                updateInventoryDisplay();
                break;
            }
        }
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

        Card upgradedCard = new Card(oldCard.getName() + " +1", oldCard.getAttack() + 1, oldCard.getHealth()  + 1, oldCard.getRarity(), oldCard.hasDoubleAttack(), oldCard.hasRevive());

        // Add upgraded card to inventory and cardLookup
        inventory.put(upgradeCardId, 1);
        cardLookup.put(upgradeCardId, upgradedCard);

        updateInventoryDisplay();
    }
}