import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuGUI extends JPanel {
    private JLabel playerNameLabel;
    private JLabel currencyLabel;
    private JButton battleButton;
    private JButton inventoryButton;
    private JButton storeButton;
    private JButton exitButton;
    private JTextArea logArea;

    public MainMenuGUI(String playerName, Main mainFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        // Player Name & Currency Label
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        playerNameLabel = new JLabel("[PLAYER]");
        currencyLabel = new JLabel("[CURRENCY]");
        topPanel.add(playerNameLabel);
        topPanel.add(currencyLabel);

        gbc.gridwidth = 2;
        add(topPanel, gbc);

        // Battle Button (Large Center Button)
        battleButton = createStyledButton("BATTLE", new Dimension(300, 100), 28);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(battleButton, gbc);

        //Inventory & Store
        inventoryButton = createStyledButton("INVENTORY", new Dimension(180, 80), 20);
        storeButton = createStyledButton("STORE", new Dimension(180, 80), 20);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(inventoryButton);
        buttonPanel.add(storeButton);

        add(buttonPanel, gbc);

        // Exit Button
        exitButton = createStyledButton("EXIT", new Dimension(180, 80), 20);
        add(exitButton, gbc);

        battleButton.addActionListener(e -> mainFrame.startBattle());
        inventoryButton.addActionListener(e -> mainFrame.openInventory());
        storeButton.addActionListener(e -> new ShopGUI(mainFrame));
        exitButton.addActionListener(e -> System.exit(0));

        //log
        logArea = new JTextArea(0, 0);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(0, 0));
        add(logScroll);

        mainFrame.setLogArea(logArea);
    }

    private JButton createStyledButton(String text, Dimension size, int fontSize){
        JButton button = new JButton(text);
        button.setFont(new Font("Airal", Font.BOLD, fontSize));
        button.setPreferredSize(size);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setPreferredSize(new Dimension(size.width + 10, size.height + 10));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setPreferredSize(size);
                button.revalidate();
            }
        });
        return button;
    }
}
