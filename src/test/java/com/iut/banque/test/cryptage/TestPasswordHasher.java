package com.iut.banque.test.cryptage;

import org.junit.Before;
import org.junit.Test;
import com.iut.banque.cryptage.PasswordHasher;

import static org.junit.Assert.*;

public class TestPasswordHasher {

    private String password;
    private String hash;

    @Before
    public void setUp() {
        password = "monSuperMotDePasse123!";
        hash = PasswordHasher.hashPassword(password);
    }

    @Test
    public void testHashPasswordNotNull() {
        // Vérifie que le hash n'est pas nul ni vide
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void testHashPasswordLength() {
        // SHA-256 → 64 caractères hexadécimaux
        assertEquals(64, hash.length());
    }

    @Test
    public void testVerifyPassword_OK() {
        // Vérifie que le mot de passe correct passe
        assertTrue(PasswordHasher.verify(password, hash));
    }

    @Test
    public void testVerifyPassword_Fail() {
        // Vérifie qu'un mot de passe incorrect échoue
        assertFalse(PasswordHasher.verify("motdepasseFaux", hash));
    }

    @Test
    public void testVerifyWithNullInputs() {
        // Vérifie qu'une comparaison avec null ou vide échoue proprement (false)
        assertFalse(PasswordHasher.verify(null, hash));
        assertFalse(PasswordHasher.verify(password, null));
        assertFalse(PasswordHasher.verify("", hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPasswordEmptyString() {
        PasswordHasher.hashPassword("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPasswordNullString() {
        PasswordHasher.hashPassword(null);
    }
}