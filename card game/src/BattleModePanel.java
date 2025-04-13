import javax.swing.*;
import java.awt.*;

public class BattleModePanel extends JPanel {
    private Main main;

    public BattleModePanel(Main main){
        this.main = main;
        setLayout(new GridBagLayout());

        JButton pveButton = createModeButton("PvE");
        JButton pvpButton = createModeButton("Pvp");
        JButton backButton = createModeButton("Back");

        pveButton.addActionListener(e -> main.startTowerBattle());
        pvpButton.addActionListener(e -> main.startPvE());
        backButton.addActionListener(e -> main.openMainMenu());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridy = 0;
        add(pveButton, gbc);
        gbc.gridy = 1;
        add(pvpButton, gbc);
        gbc.gridy = 2;
        add(backButton, gbc);
    }

    private JButton createModeButton(String text){
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 80));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        return button;
    }
}
