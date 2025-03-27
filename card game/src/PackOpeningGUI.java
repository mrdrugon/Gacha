import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PackOpeningGUI extends JFrame {
    private JPanel panel;
    private JLabel displayLabel;
    private Timer timer;
    private List<ICard> cards; // Changed from List<Card> to List<ICard>
    private int cardIndex = 0;
    private boolean packOpened = false;
    private boolean showingFinalScreen = false;
    private ImageIcon packIcon;

    public PackOpeningGUI(List<ICard> cards) { // Parameter now List<ICard>
        this.cards = cards;
        setTitle("Pack Opening");
        setSize(300, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new BorderLayout());
        displayLabel = new JLabel();
        displayLabel.setHorizontalAlignment(JLabel.CENTER);
        packIcon = new ImageIcon("card game/cards/pack.png");

        resizePackImage();

        panel.add(displayLabel, BorderLayout.CENTER);
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

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizePackImage();
            }
        });

        add(panel);
        setVisible(true);
    }

    private void resizePackImage() {
        int width = getWidth();
        int height = getHeight();
        Image scaledImage = packIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        displayLabel.setIcon(new ImageIcon(scaledImage));
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
            ICard card = cards.get(cardIndex);
            panel.removeAll();

            JPanel cardPanel = new JPanel();
            cardPanel.setBackground(getCardColor(card));
            cardPanel.setPreferredSize(new Dimension(100, 150));
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

        for (ICard card : cards) {
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

    private Color getCardColor(ICard card) {
        switch (card.getName()) {
            case "Red":
                return Color.RED;
            case "Blue":
                return Color.BLUE;
            case "Green":
                return Color.GREEN;
            case "Yellow":
                return Color.YELLOW;
            case "Silver":
                return new Color(192, 192, 192);
            case "Gold":
                return new Color(255, 215, 0);
            case "Rainbow":
                return Color.MAGENTA;
            default:
                return new Color(100, 100, 100);
        }
    }
}
