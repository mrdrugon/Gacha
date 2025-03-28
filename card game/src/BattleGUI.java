import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BattleGUI {
    private JFrame frame;
    private JTextArea battleLog;
    private JLayeredPane playerDeckPanel;
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

        playerDeckPanel = new JLayeredPane();
        playerDeckPanel.setPreferredSize(new Dimension(400, 120));
        frame.add(playerDeckPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        log("Battle started! Choose a card to play.");
        updatePlayerDeckUI();
    }

    private void updatePlayerDeckUI() {
        playerDeckPanel.removeAll();
        cardButtonsMap.clear();

        int xOffset = 100;
        int cardWidth = 80;
        int cardHeight = 120;

        List<ICard> aliveCards = new ArrayList<>();
        for (ICard card : playerDeck){
            if (card.getHealth() > 0){
                aliveCards.add(card);
            }
        }

        int totalWidth = (aliveCards.size() - 1) * xOffset + cardWidth;
        int startX = (playerDeckPanel.getWidth() - totalWidth) / 2;

        for (int i = 0; i < aliveCards.size(); i++) {
            ICard card = aliveCards.get(i);
            CardPanel cardPanel = new CardPanel(card);
            cardPanel.setBounds(startX + i * xOffset, 0, cardWidth, cardHeight);

            final int index = i;

            cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    playRound(card);
                }
            });

            int layer = i;
            playerDeckPanel.add(cardPanel, Integer.valueOf(layer));
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
            frame.dispose();
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
            frame.dispose();
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


    public class CardPanel extends JPanel {
        private final ICard card;

        public CardPanel(ICard card) {
            this.card = card;
            setPreferredSize(new Dimension(80, 120));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            CardRenderer.renderCard(g2d, card, getWidth(), getHeight());
        }
    }
}
