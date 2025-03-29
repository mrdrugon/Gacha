import javax.swing.*;
import javax.swing.Timer;
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

            cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    playRound(card, cardPanel);
                }
            });

            int layer = i;
            playerDeckPanel.add(cardPanel, Integer.valueOf(layer));
        }

        playerDeckPanel.revalidate();
        playerDeckPanel.repaint();
    }

    private void playRound(ICard selectedCard, CardPanel selectedCardPanel) {
        if (selectedCard == null) {
            log("You must select a card first!");
            return;
        }

        // Make sure the card is selected before the round starts
        if (!battle.playerSelectedCard(selectedCard)) {
            log("Failed to select the card!");
            return;
        }

        ICard opponentCard = battle.getNextOpponentCard();
        if (opponentCard == null) {
            log("No more opponent cards left!");
            return;
        }

        BattlePanel battlePanel = new BattlePanel();

        // Set the cards for the battle
        CardPanel playerCardPanel = new CardPanel(selectedCard);
        CardPanel opponentCardPanel = new CardPanel(opponentCard);
        BattlePanel.setCards(battlePanel, playerCardPanel, opponentCardPanel);

        // Add the battle panel to the frame
        frame.getContentPane().add(battlePanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        // Schedule the animation in the Event Dispatch Thread (EDT) for smooth UI updates
        SwingUtilities.invokeLater(() -> {
            animateBattle(playerCardPanel, opponentCardPanel, () -> {
                // Proceed to the next round after the animation
                boolean battleOver = battle.playNextRound(selectedCard);

                updatePlayerDeckUI();
                checkForBattleEnd();

                if (battleOver) {
                    log("Battle Over!");
                    disableAllButtons();
                    resetPlayerCards();
                    frame.dispose();
                }
            });
        });
    }

    private void animateBattle(CardPanel playerCard, CardPanel opponentCard, Runnable onComplete) {
        int moveDistance = 160; // Distance the cards move forward/backward
        int shakeDistance = 10; // Shake distance
        int shakeDuration = 100; // Shake duration in milliseconds
        int animationDuration = 400; // Total duration for player moving

        Container parent = playerCard.getParent();
        parent.setLayout(null); // Ensure absolute positioning

        // Set initial positions for player and opponent cards with enough gap
        Point playerStart = new Point(100, 100); // Start position of the player
        Point opponentStart = new Point(500, 100); // Start position of the opponent

        playerCard.setBounds(playerStart.x, playerStart.y, playerCard.getWidth(), playerCard.getHeight());
        opponentCard.setBounds(opponentStart.x, opponentStart.y, opponentCard.getWidth(), opponentCard.getHeight());

        // Save initial positions
        Point playerMoveForward = new Point(playerStart.x + moveDistance, playerStart.y); // Player moves forward
        Point OpponentMoveForward = new Point(opponentStart.x - moveDistance, opponentStart.y);
        Point playerMoveBack = new Point(playerStart.x, playerStart.y); // Player moves back to original position
        Point OpponentMoveBack = new Point(opponentStart.x, opponentStart.y);



        // Shake animation for cards
        Timer shakeOpponent = new Timer(shakeDuration, e -> {
            int opponentX = opponentCard.getLocation().x;
            int opponentY = opponentCard.getLocation().y;

            int playerX = playerCard.getLocation().x;
            int playerY = playerCard.getLocation().y;

            // Shake cards left and right by a small amount
            opponentCard.setBounds(opponentX - shakeDistance, opponentY, opponentCard.getWidth(), opponentCard.getHeight());
            playerCard.setBounds(playerX - shakeDistance, playerY, playerCard.getWidth(), playerCard.getHeight());
            parent.revalidate();  // Revalidate the parent to update the layout
            parent.repaint();  // Repaint the parent to reflect the changes

            // After shake, move back to original position
            Timer returnOpponent = new Timer(shakeDuration, event -> {
                opponentCard.setBounds(opponentX + shakeDistance, opponentY, opponentCard.getWidth(), opponentCard.getHeight());
                playerCard.setBounds(playerX + shakeDistance, playerY, playerCard.getWidth(), playerCard.getHeight());
                parent.revalidate();
                parent.repaint();
            });
            returnOpponent.setRepeats(false);
            returnOpponent.start();
        });
        shakeOpponent.setRepeats(false);

        // Step 1: Cards moves forward
        Timer playerMoveForwardTimer = new Timer(animationDuration, e -> {
            playerCard.setBounds(playerMoveForward.x, playerMoveForward.y, playerCard.getWidth(), playerCard.getHeight());
            opponentCard.setBounds(OpponentMoveForward.x, OpponentMoveForward.y, opponentCard.getWidth(), opponentCard.getHeight());
            parent.revalidate();  // Revalidate the parent to update the layout
            parent.repaint();  // Repaint the parent to reflect the changes

            // Step 2: Opponent shakes after player moves forward
            shakeOpponent.start();
        });
        playerMoveForwardTimer.setRepeats(false);
        playerMoveForwardTimer.start();

        // Step 3: Cards moves back after shaking
        Timer playerMoveBackTimer = new Timer(animationDuration * 2, e -> {
            playerCard.setBounds(playerMoveBack.x, playerMoveBack.y, playerCard.getWidth(), playerCard.getHeight());
            opponentCard.setBounds(OpponentMoveBack.x, OpponentMoveBack.y,opponentCard.getWidth(), opponentCard.getHeight());
            parent.revalidate();  // Revalidate the parent to update the layout
            parent.repaint();  // Repaint the parent to reflect the changes
        });

        playerMoveBackTimer.setRepeats(false);
        playerMoveBackTimer.setInitialDelay(animationDuration * 2); // Start after card moves back
        playerMoveBackTimer.start();

        Timer hideCards = new Timer(1500, e -> {
            playerCard.setVisible(false);
            opponentCard.setVisible(false);
            parent.revalidate();  // Revalidate the parent to update the layout
            parent.repaint();  // Repaint the parent to reflect the changes
            onComplete.run(); // Call the onComplete action
        });
        hideCards.setRepeats(false);
        hideCards.setInitialDelay(1500); // Wait until after the full animation is done
        hideCards.start();
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

            // Draw health bar on top of the card
            int barHeight = 10;
            Integer originalHealth = originalHealthMap.get(card.getId());

            if (originalHealth == null) {
                // Handle the error (e.g., log an error or use current health as fallback)
                originalHealth = card.getHealth();  // Fallback to current health
            }

            int barWidth = (int) ((card.getHealth() / (double) originalHealth) * getWidth());
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, barWidth, barHeight);  // Health bar
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, getWidth(), barHeight);  // Border around health bar
        }
    }

    public class BattlePanel extends JPanel {
        private CardPanel playerCardPanel;
        private CardPanel opponentCardPanel;

        // Static method to set the cards
        public static void setCards(BattlePanel battlePanel, CardPanel playerCard, CardPanel opponentCard) {
            // Set player and opponent cards on the panel
            battlePanel.playerCardPanel = playerCard;
            battlePanel.opponentCardPanel = opponentCard;

            battlePanel.add(playerCard);
            battlePanel.add(opponentCard);
            battlePanel.revalidate();
            battlePanel.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Custom rendering logic (optional)
        }
    }
}