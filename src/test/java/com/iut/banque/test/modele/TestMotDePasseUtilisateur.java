package com.iut.banque.test.modele;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.modele.Client;

public class TestMotDePasseUtilisateur {

    Client c;

    @Before
    public void setUp() throws Exception {
        c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
    }

    /**
     * Tests de la méthode setUserPwd
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMethodeSetUserPwdNull() {
        c.setUserPwd(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodeSetUserPwdVide() {
        c.setUserPwd("");
    }

    @Test
    public void testMethodeSetUserPwdValide() {
        c.setUserPwd("MotDePasse123!");
        assertNotNull(c.getUserPwd());
    }

    @Test
    public void testMethodeSetUserPwdPasswordIsHashed() {
        String mdp = "motdepasse123!";
        c.setUserPwd(mdp);
        assertNotEquals(mdp, c.getUserPwd());
    }

    /**
     * Tests du mot de passe via constructeur de client
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructeurPasswordVide() throws com.iut.banque.exceptions.IllegalFormatException {
        new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "", "1234567890");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructeurPasswordNull() throws com.iut.banque.exceptions.IllegalFormatException {
        new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", null, "1234567890");
    }
}