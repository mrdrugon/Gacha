import javax.swing.*;
import java.awt.*;

public class ShopGUI {
    private JFrame frame;
    private Main main;
    private JLabel pointsLabel;

    public ShopGUI(Main main) {
        this.main = main;
        frame = new JFrame("Shop");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Points label at the top.
        pointsLabel = new JLabel("Points: " + main.getPlayerPoints());
        pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(pointsLabel, BorderLayout.NORTH);

        // Panel for pack buying buttons.
        JPanel packPanel = new JPanel(new GridLayout(3, 1));
        addPackButton(packPanel, "Basic Pack (10 points)", 10, PackType.NORMAL);
        addPackButton(packPanel, "Rare Pack (30 points)", 30, PackType.RARE);
        addPackButton(packPanel, "Legendary Pack (50 points)", 50, PackType.LEGENDARY);
        frame.add(packPanel, BorderLayout.CENTER);

        // Panel for selling cards.
        JPanel sellPanel = new JPanel(new FlowLayout());
        JButton sellCardButton = new JButton("Sell Card");
        sellCardButton.addActionListener(e -> sellCard());
        sellPanel.add(sellCardButton);
        frame.add(sellPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void addPackButton(JPanel panel, String name, int cost, PackType type) {
        JButton button = new JButton(name);
        button.addActionListener(e -> buyPack(cost, type));
        panel.add(button);
    }

    private void buyPack(int cost, PackType type) {
        if (main.getPlayerPoints() >= cost) {
            main.addPlayerPoints(-cost);
            updatePointsLabel();
            Pack pack = PackFactory.createPack(type);
            java.util.List<ICard> newCards = pack.openPack();
            main.getInventory().addCards(newCards);
            main.log("You bought a " + type + " pack and got: " + newCards);
        } else {
            main.log("Not enough points!");
        }
    }

    private void updatePointsLabel() {
        pointsLabel.setText("Points: " + main.getPlayerPoints());
    }

    // New method to sell a card.
    private void sellCard() {
        // Retrieve inventory cards.
        java.util.List<ICard> cards = main.getInventory().getCards();
        if (cards.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No cards available to sell.", "Sell Card", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Create a list model to display card info.
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (ICard card : cards) {
            listModel.addElement(card.toString());
        }
        JList<String> cardList = new JList<>(listModel);
        cardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(cardList);
        scrollPane.setPreferredSize(new Dimension(300, 150));

        int result = JOptionPane.showConfirmDialog(frame, scrollPane, "Select a card to sell", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int selectedIndex = cardList.getSelectedIndex();
            if (selectedIndex >= 0) {
                ICard cardToSell = cards.get(selectedIndex);
                int sellValue = 0;
                // Determine card's sell value based on rarity.
                String rarity = "";
                // If the card is a BasicCard (or decorated BasicCard), we can retrieve its rarity.
                if (cardToSell instanceof BasicCard) {
                    rarity = ((BasicCard) cardToSell).getRarity();
                } else {
                    // As a fallback, use the card name to infer rarity.
                    String name = cardToSell.getName();
                    if (name.equalsIgnoreCase("Rare") || name.contains("Rare"))
                        rarity = "Rare";
                    else if (name.equalsIgnoreCase("Legendary") || name.contains("Legendary"))
                        rarity = "Legendary";
                    else
                        rarity = "Common";
                }
                if (rarity.equalsIgnoreCase("Common")) {
                    sellValue = 2;
                } else if (rarity.equalsIgnoreCase("Rare")) {
                    sellValue = 5;
                } else if (rarity.equalsIgnoreCase("Legendary")) {
                    sellValue = 10;
                }
                int confirmSell = JOptionPane.showConfirmDialog(frame, "Sell this card for " + sellValue + " points?", "Confirm Sell", JOptionPane.YES_NO_OPTION);
                if (confirmSell == JOptionPane.YES_OPTION) {
                    // Remove the card from inventory.
                    boolean removed = main.getInventory().removeOneCard(cardToSell);
                    if (removed) {
                        main.addPlayerPoints(sellValue);
                        updatePointsLabel();
                        main.log("Sold card: " + cardToSell.getName() + " for " + sellValue + " points.");
                    } else {
                        main.log("Error selling card.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No card selected.", "Sell Card", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
