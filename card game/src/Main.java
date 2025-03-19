import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main {
    private JFrame frame;
    private JPanel panel;
    private JButton openPackButton, inventoryButton, battleButton;
    private JTextArea logArea;
    private List<Card> playerDeck;
    private Map<Card, Integer> playerInventory;
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

        playerInventory = new HashMap<>();
        playerDeck = new ArrayList<>();
        playerPoints = 0;
    }

    private void openPack() {
        List<Card> newCards = Pack.openPack();
        for (Card card : newCards) {
            playerInventory.put(card, playerInventory.getOrDefault(card, 0) + 1);
        }
        log("You opened a pack and got: " + newCards +"\n");
    }

    private void openInventory() {
        new InventoryGUI(playerInventory, playerDeck);
    }

    private void startBattle() {
        if (playerDeck.size() < 5) {
            log("You need 5 cards in your deck to battle!");
            return;
        }
        List<Card> enemyDeck = Pack.openPack();
        new BattleGUI(playerDeck, enemyDeck, this);
    }

    public void battleResult(boolean won) {
        if (won) {
            log("You won! You get a new pack.");
            openPack();
        } else {
            log("You lost! You earn points.");
            playerPoints += 10;
        }
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new Main();
    }
}
