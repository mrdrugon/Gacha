import java.util.Base64;

public class Player {
    private String username;
    private String passwordHash;
    private String salt;  // Store salt as a Base64 encoded string

    public Player(String username, String password) {
        this.username = username;
        // Generate salt and hash the password
        byte[] saltBytes = PasswordUtil.generateSalt();
        this.salt = Base64.getEncoder().encodeToString(saltBytes);
        this.passwordHash = PasswordUtil.hashPassword(password, saltBytes);
    }

    // Method to verify a password attempt
    public boolean verifyPassword(String passwordAttempt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        String attemptedHash = PasswordUtil.hashPassword(passwordAttempt, saltBytes);
        return attemptedHash.equals(passwordHash);
    }

    // Getters and setters as needed...
}
