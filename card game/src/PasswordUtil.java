import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final int iterations = 10000;
    private static final int keyLength = 256;
    private static final String algorithm = "PBKDF2WithHmacSHA256";

    // Generates a secure random salt
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Hashes a password with the provided salt
    public static String hashPassword(String password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error while hashing a password: " + e.getMessage(), e);
        }
    }

    // Example usage
    public static void main(String[] args) {
        String password = "mySecretPassword";
        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        System.out.println("Salt (Base64): " + Base64.getEncoder().encodeToString(salt));
        System.out.println("Hashed Password: " + hashedPassword);
    }
}
