import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private static Map<String, Player> players = new HashMap<>();

    // Adds a player; returns false if the username is already taken.
    public static boolean addPlayer(Player player) {
        if (players.containsKey(player.getUsername())) {
            return false;
        }
        players.put(player.getUsername(), player);
        return true;
    }

    // Retrieves a player by username.
    public static Player getPlayer(String username) {
        return players.get(username);
    }

    // Checks if a username already exists.
    public static boolean playerExists(String username) {
        return players.containsKey(username);
    }
}
