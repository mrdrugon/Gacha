import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ShopPanel extends JPanel {
    private final Main main;
    private final List<PackType> packTypes = List.of(PackType.NORMAL, PackType.METAL, PackType.GEM_STONES, PackType.FOREST, PackType.OCEAN, PackType.BLACK_WHITE);
    private final String[] packImagePaths = {
            "card game/cards/pack.png",
            "card game/cards/pack.png",
            "card game/cards/pack.png",
            "card game/cards/pack.png",
            "card game/cards/pack.png",
            "card game/cards/pack.png"
    };
    private final int[] packPrices = {10, 10, 10, 10, 10, 10};

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

        // Scrollable panel to hold pack options
        JPanel packScrollPanel = new JPanel(null);
        int packWidth = 600;
        int packHeight = 900;
        int gap = 40;

        int totalWidth = packTypes.size() * (packWidth + gap);
        packScrollPanel.setPreferredSize(new Dimension(totalWidth, packHeight));

        for (int i = 0; i < packTypes.size(); i++) {
            int index = i;

            JPanel packPanel = new JPanel(null);
            packPanel.setBackground(new Color(50, 50, 50));
            packPanel.setBounds(i * (packWidth + gap), 0, packWidth, packHeight);

            JLabel imageLabel = new JLabel(new ImageIcon(packImagePaths[i]));
            imageLabel.setBounds(0, 0, packWidth, 800);
            packPanel.add(imageLabel);

            JLabel priceLabel = new JLabel(packPrices[i] + " Points", SwingConstants.CENTER);
            priceLabel.setForeground(Color.WHITE);
            priceLabel.setFont(new Font("Arial", Font.BOLD, 22));
            priceLabel.setBounds(0, 820, packWidth, 30);
            packPanel.add(priceLabel);

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int cost = packPrices[index];
                    if (main.getTowerManager().getCurrentPoints() >= cost) {
                        main.getTowerManager().spendPoints(cost);
                        updatePoints();

                        Pack pack = PackFactory.createPack(packTypes.get(index));
                        List<ICard> newCards = pack.openPack();
                        main.getInventory().addCards(newCards);
                        main.log("Bought " + packTypes.get(index) + " pack for " + cost + " points.");
                        main.openPack(packTypes.get(index));
                    } else {
                        main.log("Not enough points.");
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            });

            packScrollPanel.add(packPanel);
        }

        JScrollPane scrollPane = new JScrollPane(packScrollPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(0, 100, screenWidth, screenHeight - 100);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        add(scrollPane);
    }

    private void updatePoints() {
        pointsLabel.setText("Points: " + main.getTowerManager().getCurrentPoints());
    }
}