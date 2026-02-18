package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import com.iut.banque.exceptions.*;
import com.iut.banque.modele.*;

public class TestCompteSansDecouvertExtended {

    private CompteSansDecouvert compte;
    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        compte = new CompteSansDecouvert("FR0123456789", 100, client);
    }

    @Test
    public void testConstructeurSansParametres() {
        CompteSansDecouvert c = new CompteSansDecouvert();
        assertNotNull(c);
    }

    @Test
    public void testToString() {
        String str = compte.toString();
        assertTrue(str.contains("FR0123456789"));
        assertTrue(str.contains("CompteSansDecouvert"));
    }

    @Test
    public void testGetOwner() {
        assertEquals(client, compte.getOwner());
    }

    @Test
    public void testGetSolde() {
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    @Test
    public void testGetNumeroCompte() {
        assertEquals("FR0123456789", compte.getNumeroCompte());
    }

    @Test
    public void testGetClassName() {
        assertEquals("CompteSansDecouvert", compte.getClassName());
    }

    @Test(expected = IllegalFormatException.class)
    public void testConstructeurFormatInvalide() throws IllegalFormatException {
        new CompteSansDecouvert("INVALID", 0, client);
    }

    @Test
    public void testDebiterExact() throws Exception {
        compte.debiter(100);
        assertEquals(0.0, compte.getSolde(), 0.001);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterPlusThanSolde() throws Exception {
        compte.debiter(101);
    }
}