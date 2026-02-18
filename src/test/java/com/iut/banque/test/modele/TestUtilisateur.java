package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Client;

public class TestUtilisateur {

    private Client user;

    @Before
    public void setUp() throws Exception {
        user = new Client("Doe", "John", "1 rue", true, "j.doe1", "password", "123456789");
    }

    @Test
    public void testGetSetNom() {
        user.setNom("Dupont");
        assertEquals("Dupont", user.getNom());
    }

    @Test
    public void testGetSetPrenom() {
        user.setPrenom("Marie");
        assertEquals("Marie", user.getPrenom());
    }

    @Test
    public void testGetSetAdresse() {
        user.setAdresse("2 boulevard");
        assertEquals("2 boulevard", user.getAdresse());
    }

    @Test
    public void testGetSetMale() {
        user.setMale(false);
        assertFalse(user.isMale());
    }

    @Test
    public void testGetUserId() {
        assertEquals("j.doe1", user.getUserId());
    }

    @Test
    public void testSetUserIdValide() throws IllegalFormatException {
        user.setUserId("j.test1");
        assertEquals("j.test1", user.getUserId());
    }

    // Client.setUserId lève IllegalArgumentException (format invalide)
    @Test(expected = IllegalArgumentException.class)
    public void testSetUserIdNull() throws IllegalFormatException {
        user.setUserId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserIdVide() throws IllegalFormatException {
        user.setUserId("");
    }

    // Le constructeur de Utilisateur stocke le mot de passe tel quel (pas de hachage).
    // C'est setUserPwd() qui hache. Donc après new Client(..., "password", ...), getUserPwd() == "password".
    @Test
    public void testGetUserPwdIsNotNull() {
        assertNotNull(user.getUserPwd());
        assertEquals("password", user.getUserPwd());
    }

    @Test
    public void testSetUserPwdHashes() {
        user.setUserPwd("newpassword");
        assertNotEquals("newpassword", user.getUserPwd());
        assertNotNull(user.getUserPwd());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserPwdNull() {
        user.setUserPwd(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserPwdVide() {
        user.setUserPwd("");
    }

    @Test
    public void testToString() {
        String str = user.toString();
        assertNotNull(str);
        assertTrue(str.contains("j.doe1"));
    }
}