package com.iut.banque.cryptage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

    private PasswordHasher() {}

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être null ou vide");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithme SHA-256 non disponible", e);
        }
    }

    public static boolean verify(String password, String storedHash) {
        if (password == null || password.isEmpty() || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        return hashPassword(password).equals(storedHash);
    }
}