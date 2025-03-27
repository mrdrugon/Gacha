import javax.swing.*;
import java.awt.*;

public class ShopGUI {
    private JFrame frame;
    private Main main;

    public ShopGUI(Main main) {
        this.main = main;
        frame = new JFrame("Shop");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        addPackButton("Basic Pack (10 points)", 10);
        addPackButton("Rare Pack (30 points)", 30);
        addPackButton("Legendary Pack (50 points)", 50);

        frame.setVisible(true);
    }

    private void addPackButton(String name, int cost) {
        JButton button = new JButton(name);
        button.addActionListener(e -> buyPack(cost));
        frame.add(button);
    }

    private void buyPack(int cost) {
        if (main.getPlayerPoints() >= cost) {
            main.addPlayerPoints(-cost);
            java.util.List<ICard> newCards = new NormalPack().openPack();
            main.getInventory().addCards(newCards);
            main.log("You bought a pack and got: " + newCards);
        } else {
            main.log("Not enough points!");
        }
    }
}
