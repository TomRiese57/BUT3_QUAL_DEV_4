package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Test;

import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.*;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * Tests supplémentaires pour {@link CarteBancaire} couvrant les cas non traités
 * dans CarteBancaireTest : constructeur sans paramètres, getters Hibernate,
 * toString, estExpiree(), etc.
 */
public class TestCarteBancaireExtra {

    // ------------------------------------------------------------------ //
    //  Constructeur sans paramètre (Hibernate)                          //
    // ------------------------------------------------------------------ //

    @Test
    public void testConstructeurSansParametres() {
        CarteBancaire c = new CarteBancaire();
        assertNotNull(c);
    }

    // ------------------------------------------------------------------ //
    //  Setters Hibernate (package-private)                              //
    // ------------------------------------------------------------------ //

    @Test
    public void testSetNumeroCarte() throws Exception {
        CarteBancaire c = new CarteBancaire();
        // setNumeroCarte est package-private ; on l'appelle via réflexion
        java.lang.reflect.Method m = CarteBancaire.class.getDeclaredMethod("setNumeroCarte", String.class);
        m.setAccessible(true);
        m.invoke(c, "9999999999999999");
        assertEquals("9999999999999999", c.getNumeroCarte());
    }

    @Test
    public void testSetDateExpiration() throws Exception {
        CarteBancaire c = new CarteBancaire();
        LocalDate date = LocalDate.of(2030, 1, 1);
        java.lang.reflect.Method m = CarteBancaire.class.getDeclaredMethod("setDateExpiration", LocalDate.class);
        m.setAccessible(true);
        m.invoke(c, date);
        assertEquals(date, c.getDateExpiration());
    }

    // ------------------------------------------------------------------ //
    //  estExpiree()                                                     //
    // ------------------------------------------------------------------ //

    @Test
    public void testEstExpiree_pasExpiree() throws Exception {
        CarteBancaire c = new CarteBancaire();
        java.lang.reflect.Method m = CarteBancaire.class.getDeclaredMethod("setDateExpiration", LocalDate.class);
        m.setAccessible(true);
        m.invoke(c, LocalDate.now().plusYears(1));
        assertFalse(c.estExpiree());
    }

    @Test
    public void testEstExpiree_expiree() throws Exception {
        CarteBancaire c = new CarteBancaire();
        java.lang.reflect.Method m = CarteBancaire.class.getDeclaredMethod("setDateExpiration", LocalDate.class);
        m.setAccessible(true);
        m.invoke(c, LocalDate.now().minusDays(1));
        assertTrue(c.estExpiree());
    }

    // ------------------------------------------------------------------ //
    //  toString()                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void testToString_avecCompte() throws Exception {
        // Construire une carte via réflexion pour éviter le servlet context
        com.iut.banque.modele.Client client =
                new com.iut.banque.modele.Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        com.iut.banque.modele.CompteSansDecouvert compte =
                new com.iut.banque.modele.CompteSansDecouvert("FR0123456789", 100, client);

        CarteBancaire c = new CarteBancaire(compte, false,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);

        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("FR0123456789"));
        assertTrue(str.contains("CarteBancaire"));
    }

    @Test
    public void testToString_sansCompte() {
        CarteBancaire c = new CarteBancaire();
        // compte est null → toString ne doit pas planter
        String str = c.toString();
        assertNotNull(str);
        assertTrue(str.contains("null"));
    }

    // ------------------------------------------------------------------ //
    //  Exceptions imbriquées                                            //
    // ------------------------------------------------------------------ //

    @Test
    public void testCarteBloqueException() {
        CarteBloqueException ex = new CarteBloqueException("bloquee");
        assertEquals("bloquee", ex.getMessage());
    }

    @Test
    public void testPlafondDepasseException() {
        PlafondDepasseException ex = new PlafondDepasseException("plafond");
        assertEquals("plafond", ex.getMessage());
    }

    @Test
    public void testSoldeInsuffisantException_avecMessage() {
        SoldeInsuffisantException ex = new SoldeInsuffisantException("solde insuf");
        assertEquals("solde insuf", ex.getMessage());
    }

    @Test
    public void testSoldeInsuffisantException_avecCause() {
        Exception cause = new RuntimeException("cause");
        SoldeInsuffisantException ex = new SoldeInsuffisantException("solde insuf", cause);
        assertEquals("solde insuf", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    // ------------------------------------------------------------------ //
    //  getCompte() retourne null quand pas initialisé                   //
    // ------------------------------------------------------------------ //

    @Test
    public void testGetCompte_null() {
        CarteBancaire c = new CarteBancaire();
        assertNull(c.getCompte());
    }
}