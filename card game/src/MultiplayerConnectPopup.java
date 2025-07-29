import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiplayerConnectPopup {
    public static void show(Main main){
        String[] options = {"Host", "Join"};
        int choice = JOptionPane.showOptionDialog(null, "Multiplayer Mach", "Choose Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0){
            try {
                ServerSocket serverSocket = new ServerSocket(12345);
                JOptionPane.showMessageDialog(null, "Waiting for player to join...");
                Socket clientSocket = serverSocket.accept();
                main.setNetworkManager(new MultiplayerHandler(clientSocket));
                main.setGameMode(Main.GameMode.MULTIPLAYER_HOST);
                main.log("Player joined!");
                main.startMultiplayerBattle();
            } catch (IOException e){
                main.log("Failed to host: " + e.getMessage());
            }
        } else if (choice == 1){
            String ip = JOptionPane.showInputDialog("Enter Host IP:");
            try{
                Socket socket = new Socket(ip, 12345);
                main.setNetworkManager(new MultiplayerHandler(socket));
                main.setGameMode(Main.GameMode.MULTIPLAYER_CLIENT);
                main.log("Connection to host!");
                main.startMultiplayerBattle();
            } catch (IOException e){
                main.log("Failed to join: " + e.getMessage());
            }
        }
    }
}
