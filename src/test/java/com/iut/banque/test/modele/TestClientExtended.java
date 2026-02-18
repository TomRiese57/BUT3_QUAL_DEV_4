package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.*;
import com.iut.banque.modele.*;

import java.util.HashMap;
import java.util.Map;

public class TestClientExtended {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client("Doe", "John", "1 rue", true, "j.doe1", "password", "123456789");
    }

    @Test
    public void testGetNumeroClient() {
        assertEquals("123456789", client.getNumeroClient());
    }

    @Test
    public void testSetNumeroClientValide() throws Exception {
        client.setNumeroClient("987654321");
        assertEquals("987654321", client.getNumeroClient());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumeroClientNull() throws Exception {
        client.setNumeroClient(null);
    }

    @Test(expected = IllegalFormatException.class)
    public void testSetNumeroClientInvalide() throws Exception {
        client.setNumeroClient("BADFORMAT");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUserIdInvalideFormat() throws Exception {
        client.setUserId("invalidformat");
    }

    @Test
    public void testConstructeurSansParametres() {
        Client c = new Client();
        assertNotNull(c);
    }

    @Test
    public void testToString() {
        String str = client.toString();
        assertTrue(str.contains("j.doe1"));
        assertTrue(str.contains("123456789"));
    }

    @Test
    public void testGetIdentity() {
        String identity = client.getIdentity();
        assertTrue(identity.contains("John"));
        assertTrue(identity.contains("Doe"));
        assertTrue(identity.contains("123456789"));
    }

    @Test
    public void testGetAndSetAccounts() {
        Map<String, Compte> accounts = new HashMap<>();
        client.setAccounts(accounts);
        assertEquals(accounts, client.getAccounts());
    }

    @Test
    public void testAddAccount() throws Exception {
        Compte compte = new CompteSansDecouvert("FR0123456789", 0, client);
        client.addAccount(compte);
        assertTrue(client.getAccounts().containsKey("FR0123456789"));
    }

    @Test
    public void testCheckFormatUserIdClientValide() {
        assertTrue(Client.checkFormatUserIdClient("j.doe1"));
        assertTrue(Client.checkFormatUserIdClient("a.utilisateur928"));
    }

    @Test
    public void testCheckFormatUserIdClientInvalide() {
        assertFalse(Client.checkFormatUserIdClient(""));
        assertFalse(Client.checkFormatUserIdClient("nodot1"));
        assertFalse(Client.checkFormatUserIdClient("a.01"));
    }

    @Test
    public void testCheckFormatNumeroClientValide() {
        assertTrue(Client.checkFormatNumeroClient("123456789"));
    }

    @Test
    public void testCheckFormatNumeroClientInvalide() {
        assertFalse(Client.checkFormatNumeroClient("12345678"));   // 8 chiffres
        assertFalse(Client.checkFormatNumeroClient("1234567890")); // 10 chiffres
        assertFalse(Client.checkFormatNumeroClient("12345678a"));  // lettre
    }

    @Test
    public void testGetComptesAvecSoldeNonNulVide() {
        assertTrue(client.getComptesAvecSoldeNonNul().isEmpty());
    }

    @Test
    public void testPossedeComptesADecouvert_Faux() {
        assertFalse(client.possedeComptesADecouvert());
    }
}