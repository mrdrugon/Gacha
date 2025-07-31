// File: InventoryGUI.java
// (default package; place alongside other GUI classes)

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

        ImageIcon backIcon = new ImageIcon(
                getClass().getClassLoader().getResource("card_game/cards/Battle.png")
        );
        JButton backButton = new JButton(backIcon);
        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setToolTipText("Back to Main Menu");
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

        // Top: back button + deck panel
        JPanel verticalBox = new JPanel();
        verticalBox.setLayout(new BoxLayout(verticalBox, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        ImageIcon backIcon = new ImageIcon(
                getClass().getClassLoader().getResource("card_game/cards/Battle.png")
        );
        JButton backButton = new JButton(backIcon);
        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setToolTipText("Back to Main Menu");
        backButton.addActionListener(e -> main.openMainMenu());
        topPanel.add(backButton);
        verticalBox.add(topPanel);

        deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        verticalBox.add(deckPanel);

        rootPanel.add(verticalBox, BorderLayout.NORTH);

        // Inventory grid
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

            // Use card's own theme color
            cardButton.setBackground(card.getColor());

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

        // Fill remaining slots
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

        for (ICard card : inventory.getCards()) {
            String name = card.getName();
            groupedCounts.put(name, groupedCounts.getOrDefault(name, 0) + 1);
            cardReference.putIfAbsent(name, card);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        int x = 0, y = 0;
        for (Map.Entry<String, Integer> entry : groupedCounts.entrySet()) {
            String cardName = entry.getKey();
            int count = entry.getValue();
            ICard card = cardReference.get(cardName);

            JButton cardPanel = new CardPanel(card, count);
            gbc.gridx = x; gbc.gridy = y;
            inventoryPanel.add(cardPanel, gbc);

            x++;
            if (x >= 5) {
                x = 0;
                y++;
            }
        }

        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void addToDeck(ICard card) {
        if (inventory.getDeck().isFull()) return;
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

    private void showPreview(ICard card, Point screenPos) {
        if (previewWindow != null) previewWindow.dispose();

        previewWindow = new JWindow();
        previewWindow.setBackground(new Color(0, 0, 0, 0));

        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                CardRenderer.renderCard(g2d, card, 200, 240);
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
        private final ICard card;
        private final int count;

        public CardPanel(ICard card, int count) {
            this.card = card;
            this.count = count;
            setPreferredSize(new Dimension(80, 80));
            setOpaque(true);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Use card.getColor() instead of CardRenderer.getCardColor
            setBackground(card.getColor());
            setFocusPainted(false);
            setContentAreaFilled(true);

            addActionListener(e -> {
                hidePreview();
                addToDeck(card);
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    showPreview(card, getLocationOnScreen());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    hidePreview();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            String countText = "x" + count;
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(countText);
            g2d.drawString(countText, getWidth() - textWidth - 4, getHeight() - 4);
            g2d.dispose();
        }
    }
}
