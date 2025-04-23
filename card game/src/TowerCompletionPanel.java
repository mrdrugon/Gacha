import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TowerCompletionPanel extends JPanel {
    public TowerCompletionPanel(int pointsPreview, List<Pack> packsPreview, Runnable onContinue, Runnable onExit) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // translucent black overlay

        JLabel title = new JLabel("Victory!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 800, 50);
        add(title);

        JLabel pointsLabel = new JLabel("Points If You Exit: " + pointsPreview, SwingConstants.CENTER);
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setBounds(0, 120, 800, 40);
        add(pointsLabel);

        int x = 100;
        int y = 200;

        for (Pack pack : packsPreview) {
            JLabel packIcon = new JLabel(new ImageIcon("card game/cards/pack.png")); // Replace if needed
            packIcon.setBounds(x, y, 100, 150);
            add(packIcon);
            x += 120;
        }

        JButton continueButton = new JButton("Continue");
        continueButton.setBounds(200, 400, 150, 40);
        continueButton.addActionListener(e -> onContinue.run());
        add(continueButton);

        JButton exitButton = new JButton("Exit Tower");
        exitButton.setBounds(400, 400, 150, 40);
        exitButton.addActionListener(e -> onExit.run());
        add(exitButton);
    }
}
