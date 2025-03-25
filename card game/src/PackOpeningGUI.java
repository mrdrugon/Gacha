import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class PackOpeningGUI extends JFrame {
    private JPanel panel;
    private JPanel displayPanel;
    private Timer timer;
    private List<Card> cards;
    private int cardIndex = 0;
    private boolean packOpened = false;

    public PackOpeningGUI(List<Card> cards){
        this.cards = cards;

        setTitle("Pack Opening");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new BorderLayout());

        displayPanel = new JPanel();
        displayPanel.setPreferredSize(new Dimension(200, 500));
        displayPanel.setLayout(new BorderLayout());

        JLabel packLabel = new JLabel(new ImageIcon("card game/cards/pack.png"));
        packLabel.setHorizontalAlignment(JLabel.CENTER);
        packLabel.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
                if (!packOpened){
                    openPackAnimation();
                }
            }
        });

        panel.add(packLabel, BorderLayout.CENTER);
        add(panel);

        setVisible(true);
    }

    private void openPackAnimation(){
        packOpened = true;

        Timer delay = new Timer(1000, e -> {
                ((Timer) e.getSource()).stop();
                showNextCard();
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void showNextCard(){
        if (cardIndex < cards.size()){
            Card card = cards.get(cardIndex);
            panel.removeAll();

            displayPanel.setBackground(getCardColor(card));
            displayPanel.removeAll();

            JLabel cardLabel = new JLabel(card.getName(), JLabel.CENTER);
            cardLabel.setForeground(Color.WHITE);
            cardLabel.setFont(new Font("Arial", Font.BOLD, 16));
            displayPanel.add(cardLabel, BorderLayout.CENTER);

            panel.add(displayPanel, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

            cardIndex++;

            timer = new Timer(1500, e -> {
                timer.stop();
                showNextCard();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            dispose();
        }
    }

    private Color getCardColor(Card card){
        switch (card.getRarity()){
            case "Common":
                return Color.GRAY;
            case "Uncommon":
                return Color.GREEN;
            case "Rare":
                return Color.BLUE;
            case "Epic":
                return Color.MAGENTA;
            case "Legendary":
                return Color.ORANGE;
            default:
                return getRandomColor();
        }
    }

    private Color getRandomColor(){
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}