package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.*;
import com.iut.banque.modele.*;

import java.util.HashMap;
import java.util.Map;

public class TestBanque {

    private Banque banque;

    @Before
    public void setUp() {
        banque = new Banque();
    }

    @Test
    public void testSetAndGetClients() {
        Map<String, Client> clients = new HashMap<>();
        banque.setClients(clients);
        assertEquals(clients, banque.getClients());
    }

    @Test
    public void testSetAndGetGestionnaires() {
        Map<String, Gestionnaire> gestionnaires = new HashMap<>();
        banque.setGestionnaires(gestionnaires);
        assertEquals(gestionnaires, banque.getGestionnaires());
    }

    @Test
    public void testSetAndGetAccounts() {
        Map<String, Compte> accounts = new HashMap<>();
        banque.setAccounts(accounts);
        assertEquals(accounts, banque.getAccounts());
    }

    @Test
    public void testCrediter() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);

        banque.crediter(compte, 50);

        assertEquals(150.0, compte.getSolde(), 0.001);
    }

    @Test(expected = IllegalFormatException.class)
    public void testCrediterNegativeAmount() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);
        banque.crediter(compte, -50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrediterNullCompte() throws Exception {
        banque.crediter(null, 50);
    }

    @Test
    public void testDebiter() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);

        banque.debiter(compte, 30);

        assertEquals(70.0, compte.getSolde(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDebiterNullCompte() throws Exception {
        banque.debiter(null, 50);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterInsufficientFunds() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 10, client);
        banque.debiter(compte, 100);
    }

    @Test
    public void testDeleteUser() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Map<String, Client> clients = new HashMap<>();
        clients.put("j.doe1", client);
        banque.setClients(clients);

        banque.deleteUser("j.doe1");

        assertNull(banque.getClients().get("j.doe1"));
    }

    @Test
    public void testChangeDecouvert() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 0, 100, client);

        banque.changeDecouvert(compte, 200);

        assertEquals(200.0, compte.getDecouvertAutorise(), 0.001);
    }

    @Test(expected = IllegalFormatException.class)
    public void testChangeDecouvertNegative() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 0, 100, client);
        banque.changeDecouvert(compte, -50);
    }
}