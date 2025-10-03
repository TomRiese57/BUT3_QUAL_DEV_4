package com.iut.banque.cryptage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    public static String hashPassword(String password) {
        try {
            // Choisir l’algorithme de hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Appliquer le hash
            byte[] encodedHash = digest.digest(password.getBytes());

            // Convertir en hexadécimal lisible
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur: Algorithme SHA-256 non trouvé", e);
        }
    }

    // Vérifie si un mot de passe correspond à un hash existant
    public static boolean verify(String password, String storedHash) {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }

    public static void main(String[] args) {
        String password = "WRONG PASS";
        String storedHash = hashPassword(password);
        System.out.println(storedHash);
    }
}

