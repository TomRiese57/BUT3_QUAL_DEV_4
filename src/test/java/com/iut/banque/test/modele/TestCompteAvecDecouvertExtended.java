package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.*;
import com.iut.banque.modele.*;

public class TestCompteAvecDecouvertExtended {

    private CompteAvecDecouvert compte;
    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        compte = new CompteAvecDecouvert("FR0123456789", 100, 100, client);
    }

    @Test
    public void testGetDecouvertAutorise() {
        assertEquals(100.0, compte.getDecouvertAutorise(), 0.001);
    }

    @Test
    public void testSetDecouvertAutoriseValide() throws Exception {
        compte.setDecouverAutorise(200);
        assertEquals(200.0, compte.getDecouvertAutorise(), 0.001);
    }

    @Test(expected = IllegalFormatException.class)
    public void testSetDecouvertAutoriseNegatif() throws Exception {
        compte.setDecouverAutorise(-50);
    }

    @Test(expected = IllegalOperationException.class)
    public void testSetDecouvertAutoriseIncompatibleAvecSolde() throws Exception {
        // Compte avec solde négatif -50, découvert à 100
        CompteAvecDecouvert c = new CompteAvecDecouvert("FR0000000001", -50, 100, client);
        // Tenter de réduire le découvert à 30 (< abs(-50))
        c.setDecouverAutorise(30);
    }

    @Test
    public void testConstructeurSansParametres() {
        CompteAvecDecouvert c = new CompteAvecDecouvert();
        assertNotNull(c);
    }

    @Test
    public void testToString() {
        String str = compte.toString();
        assertTrue(str.contains("FR0123456789"));
        assertTrue(str.contains("CompteAvecDecouvert"));
    }

    @Test
    public void testDebiterJusquAuDecouvert() throws Exception {
        // solde=100, decouvert=100 → peut débiter jusqu'à 200
        compte.debiter(200);
        assertEquals(-100.0, compte.getSolde(), 0.001);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterAuDela() throws Exception {
        compte.debiter(201);
    }

    @Test(expected = IllegalFormatException.class)
    public void testDebiterMontantNegatif() throws Exception {
        compte.debiter(-10);
    }

    @Test
    public void testGetOwner() {
        assertEquals(client, compte.getOwner());
    }
}