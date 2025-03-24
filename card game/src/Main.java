import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private JFrame frame;
    private JPanel panel;
    private JButton openPackButton, inventoryButton, battleButton;
    private JTextArea logArea;
    // Use a global Inventory instance instead of a separate map.
    private Inventory inventory;
    private int playerPoints;

    public Main() {
        frame = new JFrame("Gacha Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        panel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        openPackButton = new JButton("Open Pack");
        inventoryButton = new JButton("Inventory");
        battleButton = new JButton("Battle");

        openPackButton.addActionListener(e -> openPack());
        inventoryButton.addActionListener(e -> openInventory());
        battleButton.addActionListener(e -> startBattle());

        buttonPanel.add(openPackButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(battleButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        inventory = new Inventory();
        playerPoints = 0;
    }

    private void openPack() {
        // Explicitly using java.util.List to avoid ambiguity with java.awt.List
        java.util.List<Card> newCards = Pack.openPack();
        inventory.addCards(newCards);
        log("You opened a pack and got: " + newCards + "\n");
    }

    private void openInventory() {
        new InventoryGUI(inventory);
    }

    private void startBattle() {
        if (inventory.getDeck().getDeck().size() < Deck.DECK_SIZE) {
            log("You need 5 cards in your deck to battle!");
            return;
        }
        java.util.List<Card> enemyDeck = Pack.openPack();
        new BattleGUI(inventory.getDeck().getDeck(), enemyDeck, this);
    }

    public void battleResult(String outcome) {
        if (outcome.equals("win")){
            log("You won! You get a new pack.");
            openPack();
        } else if (outcome.equals("loss")) {
            log("You lost! You earn points.");
            playerPoints += 10;
        } else if (outcome.equals("tie")) {
            log("The battle ended in a tie! You get half rewards.");
            playerPoints += 5;
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new Main();
    }
}
