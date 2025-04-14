import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TowerCompletionPanel extends JPanel {
    public TowerCompletionPanel(int pointsEarned, List<Pack> packsEarned, Runnable onClose) {
        setLayout(null);
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 180)); // translucent black overlay

        JLabel title = new JLabel("Tower Complete!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 800, 50);
        add(title);

        JLabel pointsLabel = new JLabel("Points Earned: " + pointsEarned, SwingConstants.CENTER);
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setBounds(0, 120, 800, 40);
        add(pointsLabel);

        int x = 100;
        int y = 200;

        for (Pack pack : packsEarned) {
            JLabel packIcon = new JLabel(new ImageIcon("path/to/pack/image.png")); // Replace with actual pack image
            packIcon.setBounds(x, y, 100, 150);
            add(packIcon);
            x += 120;
        }

        JButton returnButton = new JButton("Return to Main Menu");
        returnButton.setBounds(300, 400, 200, 40);
        returnButton.addActionListener(e -> onClose.run());
        add(returnButton);
    }
}
