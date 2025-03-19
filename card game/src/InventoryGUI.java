import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryGUI {
    private JFrame frame;
    private JPanel inventoryPanel;
    private JPanel deckPanel;
    private Map<Card, Integer> inventory;
    private Map<Card, Integer> deckCount;
    private List<Card> deck;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;

    public InventoryGUI(Map<Card, Integer> inventory, List<Card> deck) {
        this.inventory = inventory;
        this.deck = deck;
        this.deckCount = new HashMap<>();
        for (Card card : deck) {
            deckCount.put(card, deckCount.getOrDefault(card, 0) + 1);
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
                cardButton = new JButton(card.getName() + " (ATK: " + card.getAttack() + ", HP: " + card.getHealth() + ")\n");
                cardButton.addActionListener(e -> removeFromDeck(card));
            } else {
                cardButton = new JButton("Empty");
            }

            deckPanel.add(cardButton);
        }
        deckPanel.revalidate();
        deckPanel.repaint();
    }

    private void updateInventoryDisplay() {
        inventoryPanel.removeAll();
        for (Map.Entry<Card, Integer> entry : inventory.entrySet()) {
            Card card = entry.getKey();
            int count = entry.getValue();
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new BorderLayout());

            JButton cardButton = new JButton(card.getName() + " (x" + count + ")\n");
            cardButton.addActionListener(e -> addToDeck(card));
            cardPanel.add(cardButton, BorderLayout.CENTER);

            if (count >= UPGRADE_THRESHOLD){
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

        if (deck.size() >= DECK_SIZE || inventory.getOrDefault(card, 0) <= 0) {
            return;
        }

        deck.add(card);
        deckCount.put(card, deckCount.getOrDefault(card, 0) + 1);
        inventory.put(card, inventory.get(card) - 1);
        if (inventory.get(card) == 0) {
            inventory.remove(card);
        }
        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void removeFromDeck(Card card) {
        if (deck.contains(card)) {
            deck.remove(card);
            deckCount.put(card, deckCount.get(card) - 1);
            if (deckCount.get(card) == 0) {
                deckCount.remove(card);
            }
            inventory.put(card, inventory.getOrDefault(card, 0) + 1);
            updateDeckDisplay();
            updateInventoryDisplay();
        }
    }

    private void upgradeCard(Card card){
        if (inventory.getOrDefault(card, 0) < UPGRADE_THRESHOLD){
            return;
        }
        inventory.put(card, inventory.get(card) - UPGRADE_THRESHOLD);
        if (inventory.get(card) == 0){
            inventory.remove(card);
        }
        Card upgradedCard = new Card(card.getName() + " +1", card.getAttack() + 1, card.getHealth() + 1, card.getRarity(), card.hasDoubleAttack(), card.hasRevive());
        inventory.put(upgradedCard, inventory.getOrDefault(upgradedCard, 0)+1);
        updateInventoryDisplay();
    }
}
