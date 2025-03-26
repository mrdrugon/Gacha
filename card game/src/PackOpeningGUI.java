import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Random;

public class PackOpeningGUI extends JFrame {
    private JPanel panel;
    private Timer timer;
    private List<Card> cards;
    private int cardIndex = 0;
    private boolean packOpened = false;
    private boolean showingFinalScreen = false;

    public PackOpeningGUI(List<Card> cards) {
        this.cards = cards;

        setTitle("Pack Opening");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new BorderLayout());
        JLabel displayLabel = new JLabel(new ImageIcon("card game/cards/pack.png"));
        displayLabel.setHorizontalAlignment(JLabel.CENTER);

        // Click listener for pack opening and cycling through cards
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (!packOpened) {
                    openPackAnimation();
                } else if (!showingFinalScreen) {
                    showNextCard();
                }
            }
        });

        panel.add(displayLabel, BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }

    private void openPackAnimation() {
        packOpened = true;
        panel.removeAll();
        JLabel openingLabel = new JLabel("Opening...", JLabel.CENTER);
        panel.add(openingLabel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();

        Timer delay = new Timer(1000, e -> {
            ((Timer) e.getSource()).stop();
            showNextCard();
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void showNextCard() {
        if (cardIndex < cards.size()) {
            Card card = cards.get(cardIndex);
            panel.removeAll();

            JPanel cardPanel = new JPanel();
            cardPanel.setBackground(getCardColor(card));
            cardPanel.setPreferredSize(new Dimension(250, 180));
            cardPanel.setLayout(new BorderLayout());

            JLabel cardLabel = new JLabel(card.getName(), JLabel.CENTER);
            cardLabel.setForeground(Color.WHITE);
            cardLabel.setFont(new Font("Arial", Font.BOLD, 16));
            cardPanel.add(cardLabel, BorderLayout.CENTER);

            panel.add(cardPanel, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();

            cardIndex++;

            timer = new Timer(1000, e -> {
                timer.stop();
                showNextCard();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showFinalScreen();
        }
    }

    private void showFinalScreen() {
        showingFinalScreen = true;
        panel.removeAll();

        JPanel finalPanel = new JPanel(new GridLayout(0, 3, 10, 10));

        for (Card card : cards) {
            JPanel cardPanel = new JPanel();
            cardPanel.setBackground(getCardColor(card));
            cardPanel.setPreferredSize(new Dimension(100, 150));
            cardPanel.setLayout(new BorderLayout());

            JLabel cardLabel = new JLabel(card.getName(), JLabel.CENTER);
            cardLabel.setForeground(Color.WHITE);
            cardLabel.setFont(new Font("Arial", Font.BOLD, 12));
            cardPanel.add(cardLabel, BorderLayout.CENTER);

            finalPanel.add(cardPanel);
        }

        panel.add(finalPanel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private Color getCardColor(Card card) {
        switch (card.getRarity()) {
            case "Common": return Color.GRAY;
            case "Uncommon": return Color.GREEN;
            case "Rare": return Color.BLUE;
            case "Epic": return Color.MAGENTA;
            case "Legendary": return Color.ORANGE;
            default: return getRandomColor();
        }
    }

    private Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}