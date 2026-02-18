package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Gestionnaire;

public class TestGestionnaire {

    @Test
    public void testConstructeurValide() throws IllegalFormatException {
        Gestionnaire g = new Gestionnaire("Dupont", "Jean", "1 rue", true, "admin", "pwd");
        assertEquals("Dupont", g.getNom());
        assertEquals("Jean", g.getPrenom());
        assertEquals("1 rue", g.getAdresse());
        assertTrue(g.isMale());
        assertEquals("admin", g.getUserId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructeurIdVide() throws IllegalFormatException {
        new Gestionnaire("Dupont", "Jean", "1 rue", true, "", "pwd");
    }

    @Test
    public void testConstructeurSansParametres() {
        Gestionnaire g = new Gestionnaire();
        assertNotNull(g);
    }

    @Test
    public void testToString() throws IllegalFormatException {
        Gestionnaire g = new Gestionnaire("Dupont", "Jean", "1 rue", true, "admin", "pwd");
        String str = g.toString();
        assertTrue(str.contains("admin"));
        assertTrue(str.contains("Dupont"));
    }

    @Test
    public void testFemme() throws IllegalFormatException {
        Gestionnaire g = new Gestionnaire("Martin", "Sophie", "2 av", false, "admin", "pwd");
        assertFalse(g.isMale());
    }
}