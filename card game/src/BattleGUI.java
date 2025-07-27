import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BattleGUI extends JPanel{
    private JLayeredPane playerDeckPanel;
    private JLayeredPane opponentDeckPanel;
    private Map<Integer, JButton> cardButtonsMap;
    private Battle battle;
    private Main main;
    private List<ICard> playerDeck;
    private Set<Integer> deadCardsIds;
    private Map<Integer, Integer> originalHealthMap;
    private boolean isTurnActive = true;

    public BattleGUI(List<ICard> playerDeck, List<ICard> enemyDeck, Main main, BattleTowerManager towerManager, MainMenuGUI mainMenuGUI) {
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

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 500));

        opponentDeckPanel = new JLayeredPane();
        opponentDeckPanel.setPreferredSize(new Dimension(400, 220));
        add(opponentDeckPanel, BorderLayout.NORTH);

        // --- Player deck panel (bottom)
        playerDeckPanel = new JLayeredPane();
        playerDeckPanel.setPreferredSize(new Dimension(400, 220));
        add(playerDeckPanel, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::updatePlayerDeckUI);
        SwingUtilities.invokeLater(this::updateOpponentDeckUI);
    }

    private void updatePlayerDeckUI() {
        playerDeckPanel.removeAll();
        cardButtonsMap.clear();
        deadCardsIds.clear();

        int xOffset = 200;
        int cardWidth = 145;
        int cardHeight = 220;

        List<ICard> aliveCards = new ArrayList<>();
        for (ICard card : playerDeck) {
            if (card.getHealth() > 0) {
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
                    if (!isTurnActive) return; // block clicks during battle
                    playRound(card, cardPanel);
                }
            });

            int layer = i;
            playerDeckPanel.add(cardPanel, Integer.valueOf(layer));
        }

        playerDeckPanel.revalidate();
        playerDeckPanel.repaint();
    }

    private void updateOpponentDeckUI() {
        opponentDeckPanel.removeAll();

        List<ICard> aliveCards = new ArrayList<>();
        for (ICard card : battle.getOpponentDeck()) {
            if (card.getHealth() > 0) {
                aliveCards.add(card);
            }
        }

        Set<ICard> revealed = battle.getRevealedOpponentCards();

        int xOffset = 200;
        int cardWidth = 145;
        int cardHeight = 220;

        int totalWidth = (aliveCards.size() - 1) * xOffset + cardWidth;
        int startX = (opponentDeckPanel.getWidth() - totalWidth) / 2;

        for (int i = 0; i < aliveCards.size(); i++) {
            ICard card = aliveCards.get(i);
            JPanel cardPanel; // ✅ Use JPanel instead of CardPanel

            if (revealed.contains(card)) {
                cardPanel = new CardPanel(card); // Show actual card
            } else {
                cardPanel = new CardBackPanel(); // Show card back
            }

            cardPanel.setBounds(startX + i * xOffset, 0, cardWidth, cardHeight);
            opponentDeckPanel.add(cardPanel, Integer.valueOf(i));
        }

        opponentDeckPanel.revalidate();
        opponentDeckPanel.repaint();
    }

    private void playRound(ICard selectedCard, CardPanel selectedCardPanel) {
        if (selectedCard == null) {
            return;
        }

        if (!battle.playerSelectedCard(selectedCard)) {
            return;
        }

        isTurnActive = false; // Disable clicks

        ICard opponentCard = battle.getNextOpponentCard();
        if (opponentCard == null) {
            return;
        }

        BattlePanel battlePanel = new BattlePanel();
        CardPanel playerCardPanel = new CardPanel(selectedCard);
        CardPanel opponentCardPanel = new CardPanel(opponentCard);
        BattlePanel.setCards(battlePanel, playerCardPanel, opponentCardPanel);

        add(battlePanel, BorderLayout.CENTER);

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            animateBattle(playerCardPanel, opponentCardPanel, () -> {
                boolean battleOver = battle.playNextRound();
                updatePlayerDeckUI();
                updateOpponentDeckUI();
                checkForBattleEnd();

                isTurnActive = true; // Re-enable clicks after battle

                if (battleOver) {
                    disableAllButtons();
                    resetPlayerCards();
                }
            });
        });
    }

    private void animateBattle(CardPanel playerCard, CardPanel opponentCard, Runnable onComplete) {
        int moveDistance = 60; // adjust for better animation balance
        int shakeDistance = 10;// Shake distance
        int shakeDuration = 100;// Shake duration in milliseconds
        int animationDuration = 400; // Duration for the move forward

        playerCard.setShowHealthBar(true);
        opponentCard.setShowHealthBar(true);

        // Timer for hiding health bars (no onComplete call here).
        Timer completeTimer = new Timer(1200, e -> {
            playerCard.setShowHealthBar(false);
            opponentCard.setShowHealthBar(false);
        });
        completeTimer.setRepeats(false);
        completeTimer.start();

        Container parent = playerCard.getParent();
        parent.setLayout(null);

        int cardWidth = playerCard.getWidth();
        int cardHeight = playerCard.getHeight();
        int spacing = 80; // Space between the two cards

        // Calculate center based on parent size
        int parentWidth = parent.getWidth();
        int centerX = parentWidth / 2;
        int centerY = parent.getHeight() / 2;

        // Position cards centered horizontally with spacing
        Point playerStart = new Point(centerX - spacing - cardWidth, centerY - cardHeight / 2);
        Point opponentStart = new Point(centerX + spacing, centerY - cardHeight / 2);

        playerCard.setBounds(playerStart.x, playerStart.y, playerCard.getWidth(), playerCard.getHeight());
        opponentCard.setBounds(opponentStart.x, opponentStart.y, opponentCard.getWidth(), opponentCard.getHeight());

        // Positions for movement.
        Point playerMoveForward = new Point(playerStart.x + moveDistance, playerStart.y);
        Point opponentMoveForward = new Point(opponentStart.x - moveDistance, opponentStart.y);
        Point playerMoveBack = new Point(playerStart.x, playerStart.y);
        Point opponentMoveBack = new Point(opponentStart.x, opponentStart.y);

        // Shake animation.
        Timer shakeOpponent = new Timer(shakeDuration, e -> {
            int opponentX = opponentCard.getLocation().x;
            int opponentY = opponentCard.getLocation().y;
            int playerX = playerCard.getLocation().x;
            int playerY = playerCard.getLocation().y;

            opponentCard.setBounds(opponentX - shakeDistance, opponentY, opponentCard.getWidth(), opponentCard.getHeight());
            playerCard.setBounds(playerX - shakeDistance, playerY, playerCard.getWidth(), playerCard.getHeight());
            parent.revalidate();
            parent.repaint();

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

        // Move cards forward.
        Timer playerMoveForwardTimer = new Timer(animationDuration, e -> {
            playerCard.setBounds(playerMoveForward.x, playerMoveForward.y, playerCard.getWidth(), playerCard.getHeight());
            opponentCard.setBounds(opponentMoveForward.x, opponentMoveForward.y, opponentCard.getWidth(), opponentCard.getHeight());
            parent.revalidate();
            parent.repaint();
            shakeOpponent.start();
        });
        playerMoveForwardTimer.setRepeats(false);
        playerMoveForwardTimer.start();

        // Move cards back.
        Timer playerMoveBackTimer = new Timer(animationDuration * 2, e -> {
            playerCard.setBounds(playerMoveBack.x, playerMoveBack.y, playerCard.getWidth(), playerCard.getHeight());
            opponentCard.setBounds(opponentMoveBack.x, opponentMoveBack.y, opponentCard.getWidth(), opponentCard.getHeight());
            parent.revalidate();
            parent.repaint();
        });
        playerMoveBackTimer.setRepeats(false);
        playerMoveBackTimer.setInitialDelay(animationDuration * 2);
        playerMoveBackTimer.start();

        // Final timer: hide cards and complete the animation.
        Timer hideCards = new Timer(1500, e -> {
            playerCard.setVisible(false);
            opponentCard.setVisible(false);
            parent.revalidate();
            parent.repaint();
            onComplete.run();
        });
        hideCards.setRepeats(false);
        hideCards.setInitialDelay(1500);
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
            disableAllButtons();
            main.battleResult("tie"); // <-- Add this
        } else if (playerLost) {
            disableAllButtons();
            main.battleResult("lose"); // <-- And this
        } else if (opponentLost) {
            disableAllButtons();
            main.battleResult("win");
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

    // Custom JPanel to render a card.
    public class CardPanel extends JPanel {
        private final ICard card;
        private boolean showHealthBar = false;

        public CardPanel(ICard card) {
            this.card = card;
            setPreferredSize(new Dimension(145, 220));
            setOpaque(false);
        }

        public void setShowHealthBar(boolean show) {
            this.showHealthBar = show;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            CardRenderer.renderCard(g2d, card, getWidth(), getHeight() - 20);

            if (showHealthBar) {
                int barHeight = 10;
                int barWidth = (int) ((card.getHealth() / (double) card.getOriginalHealth()) * getWidth());
                int barY = getHeight() - barHeight;

                g2d.setColor(Color.RED);
                g2d.fillRect(0, barY, barWidth, barHeight);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString(card.getHealth() + "/" + card.getOriginalHealth(), 5, barY + 8);
            }
        }
    }

    // Panel used during a battle round to display both cards.
    public class BattlePanel extends JPanel {
        private CardPanel playerCardPanel;
        private CardPanel opponentCardPanel;

        public static void setCards(BattlePanel battlePanel, CardPanel playerCard, CardPanel opponentCard) {
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
        }
    }

    public class CardBackPanel extends JPanel {
        public CardBackPanel() {
            setPreferredSize(new Dimension(145, 220));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Draw a placeholder or card back
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            String hiddenText = "?";
            int x = (getWidth() - fm.stringWidth(hiddenText)) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 10;
            g2d.drawString(hiddenText, x, y);
        }
    }

}