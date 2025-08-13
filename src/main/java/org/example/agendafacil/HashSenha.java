package org.example.agendafacil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class HashSenha {
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256; // bits
    private static final String ALGO = "PBKDF2WithHmacSHA256";
    private static final int SALT_BYTES = 16;

    private HashSenha() {}

    public static String hash(String plain) {
        try {
            byte[] salt = new byte[SALT_BYTES];
            // Evita getInstanceStrong() para não travar em alguns SOs
            new SecureRandom().nextBytes(salt);

            byte[] hash = pbkdf2(plain.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            return ITERATIONS + ":" +
                    Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Falha ao gerar hash de senha", e);
        }
    }

    public static boolean verify(String plain, String stored) {
        try {
            if (stored == null || stored.isBlank()) return false;

            String[] parts = stored.split(":");
            if (parts.length != 3) return false;

            int it = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);

            byte[] testHash = pbkdf2(plain.toCharArray(), salt, it, expected.length * 8);
            return constantTimeEquals(expected, testHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGO);
        return skf.generateSecret(spec).getEncoded();
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) result |= a[i] ^ b[i];
        return result == 0;
    }

}
