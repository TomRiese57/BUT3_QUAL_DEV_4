package com.iut.banque.test.cryptage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.cryptage.PasswordHasher;

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
        // Vérifie que le hash n'est pas nul
        assertTrue(hash != null && !hash.isEmpty());
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
        // vérifie qu'un mot de mot de passe null ne fonctionne pas
        assertFalse(PasswordHasher.verify(null, hash));
        assertFalse(PasswordHasher.verify(password, null));
        assertFalse(PasswordHasher.verify("", hash));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPasswordEmptyString() {
        // vérifie que hasher un mot de passe vide renvoie un exeption
        PasswordHasher.hashPassword("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashPasswordNullString() {
        // Vérifie que le hash renvoi une exeption sur un mot de passe null
        PasswordHasher.hashPassword(null);
    }


}
