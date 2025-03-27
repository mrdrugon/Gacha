import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BattleGUI {
    private JFrame frame;
    private JTextArea battleLog;
    private JPanel playerDeckPanel;
    private Map<Integer, JButton> cardButtonsMap;
    private Battle battle;
    private Main main;
    private List<ICard> playerDeck;
    private Set<Integer> deadCardsIds;
    private Map<Integer, Integer> originalHealthMap;

    public BattleGUI(List<ICard> playerDeck, List<ICard> enemyDeck, Main main) {
        this.main = main;
        this.playerDeck = playerDeck;
        this.battle = new Battle(playerDeck, enemyDeck);
        this.cardButtonsMap = new HashMap<>();
        this.deadCardsIds = new HashSet<>();
        this.originalHealthMap = new HashMap<>();

        for (ICard card : playerDeck) {
            originalHealthMap.put(card.getId(), card.getHealth());
        }

        resetPlayerCards();

        frame = new JFrame("Battle");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        panel.add(new JScrollPane(battleLog), BorderLayout.CENTER);

        playerDeckPanel = new JPanel(new GridLayout(0, 5));
        panel.add(playerDeckPanel, BorderLayout.NORTH);

        frame.add(panel);
        frame.setVisible(true);

        log("Battle started! Choose a card to play.");
        updatePlayerDeckUI();
    }

    private void updatePlayerDeckUI() {
        playerDeckPanel.removeAll();
        cardButtonsMap.clear();

        for (ICard card : playerDeck) {
            int cardId = card.getId();
            JButton cardButton = new JButton("<html>" + card.getName() + "<br>ATK: " + card.getAttack() + "<br>HP: " + card.getHealth() + "</html>");

            if (card.getHealth() <= 0) {
                deadCardsIds.add(cardId);
            }

            if (deadCardsIds.contains(cardId)) {
                cardButton.setEnabled(false);
                cardButton.setBackground(Color.GRAY);
            } else {
                cardButton.addActionListener(e -> playRound(card));
                cardButtonsMap.put(cardId, cardButton);
            }
            playerDeckPanel.add(cardButton);
        }

        playerDeckPanel.revalidate();
        playerDeckPanel.repaint();
    }

    private void playRound(ICard selectedCard) {
        if (selectedCard == null) {
            log("You must select a card first!");
            return;
        }

        int selectedCardId = selectedCard.getId();

        if (deadCardsIds.contains(selectedCardId)) {
            log("This card is already defeated!");
            return;
        }

        if (!battle.playerSelectedCard(selectedCard)) {
            log("Invalid card selection! that card is defeated.");
            deadCardsIds.add(selectedCardId);
            disableCard(selectedCardId);
            checkForBattleEnd();
            return;
        }

        boolean battleOver = battle.playNextRound(selectedCard);
        log(battle.getLastRoundResult());

        if (selectedCard.getHealth() <= 0) {
            deadCardsIds.add(selectedCardId);
            disableCard(selectedCardId);
        }

        updatePlayerDeckUI();
        checkForBattleEnd();

        if (battleOver) {
            log("Battle Over!");
            disableAllButtons();
            resetPlayerCards();
        }
    }

    private boolean isPlayerDefeated() {
        for (ICard card : playerDeck) {
            if (card.getHealth() > 0) {
                return false;
            }
        }
        return true;
    }

    private void checkForBattleEnd() {
        boolean playerLost = isPlayerDefeated();
        boolean opponentLost = battle.isOpponentDefeated();

        if (playerLost && opponentLost) {
            log("The battle ended in a tie!");
            disableAllButtons();
            main.battleResult("tie");
        } else if (playerLost) {
            log("All your cards are defeated! Battle over.");
            disableAllButtons();
            main.battleResult("loss");
        } else if (opponentLost) {
            log("You won the battle! Congratulations!");
            disableAllButtons();
            main.battleResult("win");
        }
        if (playerLost || opponentLost) {
            resetPlayerCards();
        }
    }

    private void enableCard(int cardId) {
        if (cardButtonsMap.containsKey(cardId)) {
            JButton button = cardButtonsMap.get(cardId);
            button.setEnabled(true);
            button.setBackground(null);
        }
    }

    private void disableCard(int cardId) {
        if (cardButtonsMap.containsKey(cardId)) {
            JButton button = cardButtonsMap.get(cardId);
            button.setEnabled(false);
            button.setBackground(Color.GRAY);
        }
    }

    private void disableAllButtons() {
        for (JButton button : cardButtonsMap.values()) {
            button.setEnabled(false);
        }
    }

    private void resetPlayerCards() {
        for (ICard card : playerDeck) {
            card.resetHealth();
        }
    }

    private void log(String message) {
        battleLog.append(message + "\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }
}
