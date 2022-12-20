package app.services;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class EncryptionService {

    public static String hashPassword(String password) {
        try {
            //generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            // Use PBKDF2 to hash the password
            int iterations = 10000;
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            // Encode the salt and hash as base64 strings and return them as a single string
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hashString = Base64.getEncoder().encodeToString(hash);
            return iterations + ":" + saltString + ":" + hashString;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkPassword(String enteredPassword, String passwordHash) {
        try {
            // Split the stored password hash into its components
            String[] parts = passwordHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            passwordHash = parts[2];

            // Hash the given password using PBKDF2
            PBEKeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, iterations, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String enteredPasswordHash = Base64.getEncoder().encodeToString(hash);

            // Check if the password hash matches the stored password hash
            return enteredPasswordHash.equals(passwordHash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
