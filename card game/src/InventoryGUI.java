import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class InventoryGUI {
    private JPanel rootPanel;
    private JPanel inventoryPanel;
    private JPanel deckPanel;
    private JPanel panel;
    private Inventory inventory;
    private static final int DECK_SIZE = 5;
    private static final int UPGRADE_THRESHOLD = 10;
    private JWindow previewWindow;
    private Main main;


    // UI helper fields
    private Map<Integer, JButton> cardButtonsMap = new HashMap<>();
    private Set<Integer> deadCardsIds = new HashSet<>();

    public InventoryGUI(Inventory inventory, Main main) {
        panel = new JPanel(new BorderLayout());

        // Top panel with back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        // Load back button image
        ImageIcon backIcon = new ImageIcon("card game/cards/Battle.png"); // Make sure the path is correct
        JButton backButton = new JButton(backIcon);
        backButton.setPreferredSize(new Dimension(50, 50)); // Adjust size to fit your image
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setToolTipText("Back to Main Menu");

        // Add listener to go back
        backButton.addActionListener(e -> main.openMainMenu());

        topPanel.add(backButton);
        panel.add(topPanel, BorderLayout.NORTH);

        this.main = main;
        this.inventory = inventory;
        initUI();
        updateDeckDisplay();
        updateInventoryDisplay();
    }

    private void initUI() {
        rootPanel = new JPanel(new BorderLayout());

        // ─── TOP PANEL (Back Button + Deck Panel) ─────────────────────────────
        JPanel verticalBox = new JPanel();
        verticalBox.setLayout(new BoxLayout(verticalBox, BoxLayout.Y_AXIS));

        // Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);

        ImageIcon backIcon = new ImageIcon("card game/cards/Battle.png"); // use a dedicated back icon later
        JButton backButton = new JButton(backIcon);
        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setToolTipText("Back to Main Menu");
        backButton.addActionListener(e -> main.openMainMenu());

        topPanel.add(backButton);
        verticalBox.add(topPanel);

        // Deck display (stays under the back button)
        deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        verticalBox.add(deckPanel);

        rootPanel.add(verticalBox, BorderLayout.NORTH);

        // ─── INVENTORY PANEL ─────────────────────────────
        inventoryPanel = new JPanel(new GridBagLayout());
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(inventoryPanel);
        rootPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void updateDeckDisplay() {
        deckPanel.removeAll();
        cardButtonsMap.clear();
        deadCardsIds.clear();

        List<ICard> deck = inventory.getDeck().getDeck();
        int filled = 0;
        for (ICard card : deck) {
            int cardId = card.getId();
            JButton cardButton = new JButton();
            cardButton.setPreferredSize(new Dimension(80, 80));
            cardButton.setOpaque(true);
            cardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Set color using renderer
            cardButton.setBackground(CardRenderer.getCardColor(card));

            if (card.getHealth() <= 0) {
                deadCardsIds.add(cardId);
                cardButton.setEnabled(false);
                cardButton.setBackground(Color.DARK_GRAY);
            } else {
                cardButton.addActionListener(e -> removeFromDeck(card));
                cardButtonsMap.put(cardId, cardButton);
            }

            deckPanel.add(cardButton);
            filled++;
        }

        // Fill remaining slots with empty black squares
        for (int i = filled; i < DECK_SIZE; i++) {
            JPanel emptySlot = new JPanel();
            emptySlot.setPreferredSize(new Dimension(80, 80));
            emptySlot.setBackground(Color.DARK_GRAY);
            emptySlot.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            deckPanel.add(emptySlot);
        }

        deckPanel.revalidate();
        deckPanel.repaint();
    }

    private void updateInventoryDisplay() {
        hidePreview();
        inventoryPanel.removeAll();
        Map<String, Integer> groupedCounts = new HashMap<>();
        Map<String, ICard> cardReference = new HashMap<>();

        // 🔥 Populate the groupedCounts and cardReference maps
        for (ICard card : inventory.getCards()) {
            String name = card.getName();
            groupedCounts.put(name, groupedCounts.getOrDefault(name, 0) + 1);
            cardReference.putIfAbsent(name, card);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margin between cards
        gbc.anchor = GridBagConstraints.CENTER;

        int x = 0;
        int y = 0;

        for (Map.Entry<String, Integer> entry : groupedCounts.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            ICard card = cardReference.get(cardName);

            JButton cardPanel = new CardPanel(card, count);

            gbc.gridx = x;
            gbc.gridy = y;
            inventoryPanel.add(cardPanel, gbc);

            x++;
            if (x >= 5) { // 5 cards per row
                x = 0;
                y++;
            }
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
        /*String rarity = (card instanceof BasicCard) ? ((BasicCard) card).getRarity() : "Common";
        ICard upgradedCard = new BasicCard(card.getName() + " +1", card.getAttack() + 1, card.getHealth() + 1, rarity, card);
        inventory.addCards(Arrays.asList(upgradedCard));
        updateInventoryDisplay();
         */
    }

    private void showPreview(ICard card, Point screenPos) {
        if (previewWindow != null) previewWindow.dispose();

        previewWindow = new JWindow();
        previewWindow.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                CardRenderer.renderCard(g2d, card, 200, 240); // Size can be adjusted
                g2d.dispose();
            }
        };
        previewPanel.setPreferredSize(new Dimension(200, 240));
        previewPanel.setOpaque(false);

        previewWindow.getContentPane().add(previewPanel);
        previewWindow.pack();
        previewWindow.setLocation(screenPos.x + 100, screenPos.y - 50);
        previewWindow.setVisible(true);
    }

    private void hidePreview() {
        if (previewWindow != null) {
            previewWindow.setVisible(false);
            previewWindow.dispose();
            previewWindow = null;
        }
    }

    public JPanel getPanel() {
        return rootPanel;
    }

    private class CardPanel extends JButton {
        private ICard card;
        private int count;

        public CardPanel(ICard card, int count) {
            this.card = card;
            this.count = count;
            setPreferredSize(new Dimension(80, 80)); // Cube shape
            setBackground(CardRenderer.getCardColor(card)); // Use renderer-defined color
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            setOpaque(true);
            setFocusPainted(false);
            setContentAreaFilled(true);
            setToolTipText(null); // Use custom preview instead

            addActionListener(e -> {
                hidePreview(); // <- hide the preview first
                addToDeck(card);
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    showPreview(card, getLocationOnScreen());
                }

                public void mouseExited(MouseEvent e) {
                    hidePreview();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            // Draw count (x3, etc.)
            String countText = "x" + count;
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(countText);
            g2d.drawString(countText, getWidth() - textWidth - 4, getHeight() - 4);

            g2d.dispose();
        }
    }
}