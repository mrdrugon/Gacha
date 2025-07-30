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

        ImageIcon backIcon = new ImageIcon(getClass().getClassLoader().getResource("card_game/cards/Battle.png"));
        JButton backButton = new JButton(backIcon);
        backButton.setBounds(30, 20, 100, 40);
        add(backButton);
        backButton.addActionListener(e -> main.openMainMenu());

        // Display single pack image
        JLabel packImage = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("card_game/cards/pack.png")));
        packImage.setBounds((screenWidth - 300) / 2, 100, 300, 400);
        add(packImage);

        // Buy 1 card button
        JButton buyOneButton = new JButton("1 Card (10 Point)");
        buyOneButton.setFont(new Font("Arial", Font.BOLD, 22));
        buyOneButton.setBounds((screenWidth / 2) - 220, 550, 200, 60);
        add(buyOneButton);

        buyOneButton.addActionListener(e -> {
            if (main.getTowerManager().getCurrentPoints() >= 10) {
                main.getTowerManager().spendPoints(10);
                updatePoints();

                main.openPack(PackType.NORMAL, 1); // ✅ fixed
            }
        });

        // Buy 10 cards button
        JButton buyTenButton = new JButton("10 Cards (100 Points)");
        buyTenButton.setFont(new Font("Arial", Font.BOLD, 22));
        buyTenButton.setBounds((screenWidth / 2) + 20, 550, 250, 60);
        add(buyTenButton);

        buyTenButton.addActionListener(e -> {
            if (main.getTowerManager().getCurrentPoints() >= 100) {
                main.getTowerManager().spendPoints(100);
                updatePoints();

                main.openPack(PackType.NORMAL, 10); // ✅ fixed
            }
        });
    }

    private void updatePoints() {
        pointsLabel.setText("Points: " + main.getTowerManager().getCurrentPoints());
    }
}
