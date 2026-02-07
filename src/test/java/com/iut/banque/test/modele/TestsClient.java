package com.iut.banque.test.modele;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

public class TestsClient {

    @Test
    public void testPossedeComptesADecouvert_ClientSansCompte() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "123456789");
        assertFalse(c.possedeComptesADecouvert());
    }

    @Test
    public void testPossedeComptesADecouvert_ClientAvecComptesSansDecouvert() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "123456789");
        c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
        assertFalse(c.possedeComptesADecouvert());
    }

    @Test
    public void testPossedeComptesADecouvert_ClientAvecComptesAvecDecouvert() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "123456789");
        c.addAccount(new CompteAvecDecouvert("FR1234567892", -42, 100, c));
        assertTrue(c.possedeComptesADecouvert());
    }

    @Test
    public void testGetComptesAvecSoldeNonNul() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "123456789");
        c.addAccount(new CompteAvecDecouvert("FR1234567890", 0, 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 1, c));

        assertTrue(c.getComptesAvecSoldeNonNul().containsKey("FR1234567891"));
        assertFalse(c.getComptesAvecSoldeNonNul().containsKey("FR1234567890"));
    }
}