import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class BattleGUI {
    private JFrame frame;
    private JTextArea battleLog;
    private JPanel playerDeckPanel;
    private Map<Card, JButton> cardButtonsMap;
    private Battle battle;
    private Main main;
    private List<Card> playerDeck;
    private Set<Card> deadCards;

    public BattleGUI(List<Card> playerDeck, List<Card> enemyDeck, Main main) {
        this.main = main;
        this.playerDeck = playerDeck;
        this.battle = new Battle(playerDeck, enemyDeck);
        this.cardButtonsMap = new HashMap<>();
        this.deadCards = new HashSet<>();

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

    private void updatePlayerDeckUI(){
        playerDeckPanel.removeAll();
        cardButtonsMap.clear();

        for (Card card : playerDeck){
            JButton cardButton = new JButton("<html>"+card.getName()+"<br>ATK: "+card.getAttack()+"<br>HP: "+card.getHealth()+ "</html>");

            if (card.getHealth() <= 0) {
                deadCards.add(card);
            }
            if(deadCards.contains(card)){
                cardButton.setEnabled(false);
                cardButton.setBackground(Color.GRAY);
            } else {
                cardButton.addActionListener(e-> playRound(card));
                cardButtonsMap.put(card, cardButton);
            }
            playerDeckPanel.add(cardButton);
        }

        playerDeckPanel.revalidate();
        playerDeckPanel.repaint();
    }

    private void playRound(Card selectedCard) {
        if (!battle.playerSelectedCard(selectedCard)){
            log("Invalid card selection! that card is defeated.");
            deadCards.add(selectedCard);
            disableCard(selectedCard);
            return;
        }

        boolean battleOver = battle.playNextRound(selectedCard);
        log(battle.getLastRoundResult());

        if (selectedCard.getHealth() <= 0){
            deadCards.add(selectedCard);
            disableCard(selectedCard);
        }

        updatePlayerDeckUI();

        if (battleOver) {
            log("Battle Over!");
            disableAllButtons();
            main.battleResult(battle.didPlayerWin());
        }
    }

    private void disableCard(Card card){
        if (cardButtonsMap.containsKey(card)){
            JButton button = cardButtonsMap.get(card);
            button.setEnabled(false);
            button.setBackground(Color.GRAY);
        }
    }

    private void disableAllButtons(){
        for (JButton button : cardButtonsMap.values()){
            button.setEnabled(false);
        }
    }

    private void log(String message) {
        battleLog.append(message + "\n");
    }
}
