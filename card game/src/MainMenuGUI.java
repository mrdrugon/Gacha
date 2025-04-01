import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton cardsButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JTextArea logArea;

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(null);

        // Player Name Label
        playerNameLabel = new JLabel("Player: " + playerName);
        playerNameLabel.setBounds(20, 10, 150, 30);
        add(playerNameLabel);

        // Currency Label
        currencyLabel = new JLabel("Currency: " + mainFrame.getPlayerPoints());
        currencyLabel.setBounds(430, 10, 150, 30);
        add(currencyLabel);

        // Battle Button (Large Center Button)
        battleButton = new JButton("Battle");
        battleButton.setFont(new Font("Arial", Font.BOLD, 28));
        battleButton.setBounds(100, 80, 250, 200);
        add(battleButton);

        // Cards Button
        cardsButton = new JButton("Cards");
        cardsButton.setBounds(400, 80, 120, 50);
        add(cardsButton);

        // Inventory Button
        inventoryButton = new JButton("Inventory");
        inventoryButton.setBounds(400, 140, 120, 50);
        add(inventoryButton);

        // Store Button
        storeButton = new JButton("Store");
        storeButton.setFont(new Font("Arial", Font.BOLD, 18));
        storeButton.setBounds(400, 200, 120, 80);
        add(storeButton);

        // Log area setup
        logArea = new JTextArea(0, 0);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBounds(20, 300, 0, 0);
        add(logScroll);

        mainFrame.setLogArea(logArea); // Set the log area in Main

        // Button Actions
        battleButton.addActionListener(e -> mainFrame.startBattle());
        cardsButton.addActionListener(e -> mainFrame.openPack());
        inventoryButton.addActionListener(e -> mainFrame.openInventory());
        storeButton.addActionListener(e -> new ShopGUI(mainFrame));
    }
}
