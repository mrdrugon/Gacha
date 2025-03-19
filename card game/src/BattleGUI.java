import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BattleGUI {
    private JFrame frame;
    private JTextArea battleLog;
    private JButton nextRoundButton;
    private JPanel playerDeckPanel;
    private Battle battle;
    private Main main;
    private List<Card> playerDeck;

    public BattleGUI(List<Card> playerDeck, List<Card> enemyDeck, Main main) {
        this.main = main;
        this.playerDeck = playerDeck;
        this.battle = new Battle(playerDeck, enemyDeck);

        frame = new JFrame("Battle");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        panel.add(new JScrollPane(battleLog), BorderLayout.CENTER);

        playerDeckPanel = new JPanel();
        playerDeckPanel.setLayout(new GridLayout(0, 5));
        panel.add(playerDeckPanel, BorderLayout.NORTH);

        updatePlayerDeckUI();

        frame.add(panel);
        frame.setVisible(true);

        log("Battle started! Choose a card to play.");
    }

    private void updatePlayerDeckUI(){
        playerDeckPanel.removeAll();

        for (Card card : playerDeck){
            JButton cardButton = new JButton("<html>"+card.getName()+"<br>ATK: "+card.getAttack()+"<br>HP: "+card.getHealth()+ "</html>");

            if (card.getHealth() <= 0){
                cardButton.setEnabled(false);
                cardButton.setBackground(Color.GRAY);
            } else {
                cardButton.addActionListener(e -> playRound(card));
            }
            playerDeckPanel.add(cardButton);
        }

        playerDeckPanel.revalidate();
        playerDeckPanel.repaint();
    }

    private void playRound(Card selectedCard) {

        if (!battle.playerSelectedCard(selectedCard)){
            log("Invalid card selection");
            return;
        }

        boolean battleOver = battle.playNextRound(selectedCard);
        log(battle.getLastRoundResult());
        updatePlayerDeckUI();

        if (battleOver) {
            for (Component component : playerDeckPanel.getComponents()){
                component.setEnabled(false);
            }
            boolean playerWon = battle.didPlayerWin();
            main.battleResult(playerWon);
        }
    }

    private void log(String message) {
        battleLog.append(message + "\n");
    }
}
