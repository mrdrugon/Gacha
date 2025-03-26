import javax.swing.SwingUtilities;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginSignupGUI loginGUI = new LoginSignupGUI();
            loginGUI.setVisible(true);
        });
    }
}
