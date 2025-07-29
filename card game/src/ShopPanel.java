import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ShopPanel extends JPanel {
    private final Main main;
    private JLabel pointsLabel;

    public ShopPanel(Main main, Inventory inventory) {
        this.main = main;
        setLayout(null);

        // Fullscreen resolution
        int screenWidth = 1920;
        int screenHeight = 1080;

        // Top UI: Points + Back Button
        pointsLabel = new JLabel("Points: " + main.getTowerManager().getCurrentPoints(), SwingConstants.CENTER);
        pointsLabel.setForeground(Color.BLACK);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        pointsLabel.setBounds(0, 20, screenWidth, 40);
        add(pointsLabel);

        ImageIcon backIcon = new ImageIcon("card game/cards/Battle.png");
        JButton backButton = new JButton(backIcon);
        backButton.setBounds(30, 20, 100, 40);
        add(backButton);
        backButton.addActionListener(e -> main.openMainMenu());

        // Display single pack image
        JLabel packImage = new JLabel(new ImageIcon("card game/cards/pack.png"));
        packImage.setBounds((screenWidth - 300) / 2, 100, 300, 400);
        add(packImage);

        // Buy 1 card button
        JButton buyOneButton = new JButton("Buy 1 Card (1 Point)");
        buyOneButton.setFont(new Font("Arial", Font.BOLD, 22));
        buyOneButton.setBounds((screenWidth / 2) - 220, 550, 200, 60);
        add(buyOneButton);

        buyOneButton.addActionListener(e -> {
            if (main.getTowerManager().getCurrentPoints() >= 1) {
                main.getTowerManager().spendPoints(1);
                updatePoints();

                Pack pack = PackFactory.createPack(PackType.NORMAL);
                List<ICard> newCards = pack.openPack(); // 1 card
                main.getInventory().addCards(newCards);
                main.log("Bought 1 card for 1 point.");
                main.openCards(newCards);
            } else {
                main.log("Not enough points.");
            }
        });

        // Buy 10 cards button
        JButton buyTenButton = new JButton("Buy 10 Cards (9 Points)");
        buyTenButton.setFont(new Font("Arial", Font.BOLD, 22));
        buyTenButton.setBounds((screenWidth / 2) + 20, 550, 250, 60);
        add(buyTenButton);

        buyTenButton.addActionListener(e -> {
            if (main.getTowerManager().getCurrentPoints() >= 9) {
                main.getTowerManager().spendPoints(9);
                updatePoints();

                Pack pack = PackFactory.createPack(PackType.NORMAL);
                List<ICard> newCards = pack.openPack(); // 10 cards
                main.getInventory().addCards(newCards);
                main.log("Bought 10 cards for 9 points.");
                main.openCards(newCards);
            } else {
                main.log("Not enough points.");
            }
        });
    }

    private void updatePoints() {
        pointsLabel.setText("Points: " + main.getTowerManager().getCurrentPoints());
    }
}
