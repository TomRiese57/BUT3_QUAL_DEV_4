package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import org.junit.Test;

import com.iut.banque.exceptions.*;
import com.iut.banque.modele.*;

/**
 * Tests supplémentaires pour couvrir les exceptions et les cas limites
 * des classes de modèle non encore couverts à 100%.
 */
public class TestModeleEdgeCases {

    // ================================================================== //
    //  Exceptions — constructeurs                                        //
    // ================================================================== //

    @Test
    public void testIllegalFormatException_constructeurs() {
        assertNotNull(new IllegalFormatException());
        assertNotNull(new IllegalFormatException("msg"));
        assertNotNull(new IllegalFormatException(new RuntimeException()));
        assertNotNull(new IllegalFormatException("msg", new RuntimeException()));
        assertNotNull(new IllegalFormatException("msg", new RuntimeException(), true, true));
    }

    @Test
    public void testIllegalOperationException_constructeurs() {
        assertNotNull(new IllegalOperationException());
        assertNotNull(new IllegalOperationException("msg"));
        assertNotNull(new IllegalOperationException(new RuntimeException()));
        assertNotNull(new IllegalOperationException("msg", new RuntimeException()));
        assertNotNull(new IllegalOperationException("msg", new RuntimeException(), true, true));
    }

    @Test
    public void testInsufficientFundsException_constructeurs() {
        assertNotNull(new InsufficientFundsException());
        assertNotNull(new InsufficientFundsException("msg"));
        assertNotNull(new InsufficientFundsException(new RuntimeException()));
        assertNotNull(new InsufficientFundsException("msg", new RuntimeException()));
        assertNotNull(new InsufficientFundsException("msg", new RuntimeException(), true, true));
    }

    @Test
    public void testTechnicalException_constructeurs() {
        assertNotNull(new TechnicalException());
        assertNotNull(new TechnicalException("msg"));
        assertNotNull(new TechnicalException(new RuntimeException()));
        assertNotNull(new TechnicalException("msg", new RuntimeException()));
        assertNotNull(new TechnicalException("msg", new RuntimeException(), true, true));
    }

    // ================================================================== //
    //  Compte — debiter avec montant 0                                  //
    // ================================================================== //

    @Test
    public void testDebiterMontantZero_compteSans() throws Exception {
        Client c = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, c);
        compte.debiter(0);
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    @Test
    public void testDebiterMontantZero_compteAvec() throws Exception {
        Client c = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 100, 100, c);
        compte.debiter(0);
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    // ================================================================== //
    //  Compte.debiter — montant strictement négatif                      //
    // ================================================================== //

    @Test
    public void testCompteBaseDebiter_negatif() throws Exception {
        Client c = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        // CompteSansDecouvert hérite de la méthode debiter de Compte pour le cas négatif
        // mais redéfinit aussi. Testons la branche de Compte via CompteSansDecouvert
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, c);
        try {
            compte.debiter(-10);
            fail("Exception attendue");
        } catch (IllegalFormatException e) {
            // attendu
        }
    }

    // ================================================================== //
    //  Client.checkFormatUserIdClient — null                            //
    // ================================================================== //

    @Test
    public void testCheckFormatUserIdClient_null() {
        assertFalse(Client.checkFormatUserIdClient(null));
    }

    // ================================================================== //
    //  Utilisateur.setUserId — null via Client                         //
    // ================================================================== //

    @Test
    public void testUtilisateurSetUserIdNull_viaClient() throws Exception {
        Client c = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        try {
            c.setUserId(null);
            fail("Exception attendue");
        } catch (IllegalArgumentException e) {
            // normal : Client.setUserId vérifie le format via checkFormatUserIdClient
            // qui retourne false pour null
        }
    }

    // ================================================================== //
    //  Banque — deleteUser pour un utilisateur inexistant (no-op)      //
    // ================================================================== //

    @Test
    public void testBanque_deleteUserInexistant() {
        Banque banque = new Banque();
        java.util.Map<String, Client> clients = new java.util.HashMap<>();
        banque.setClients(clients);

        // Appel de la méthode
        banque.deleteUser("inconnu");

        // ASSERTION : On vérifie que la map des clients est toujours vide
        assertTrue("La liste des clients devrait toujours être vide", banque.getClients().isEmpty());
        // ou : assertEquals(0, banque.getClients().size());
    }

    // ================================================================== //
    //  CompteAvecDecouvert — toString                                   //
    // ================================================================== //

    @Test
    public void testCompteAvecDecouvert_toStringOwnerNull() {
        // Constructeur sans owner : via le constructeur sans paramètre
        CompteAvecDecouvert c = new CompteAvecDecouvert();
        // toString ne doit pas planter même si owner est null
        // (owner.getUserId() planterait dans Compte.toString(), mais CompteAvecDecouvert a son propre toString)
        String str = c.toString();
        assertNotNull(str);
    }

    // ================================================================== //
    //  Compte.getClassName                                              //
    // ================================================================== //

    @Test
    public void testGetClassNameAvecDecouvert() throws Exception {
        Client c = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 0, 100, c);
        assertEquals("CompteAvecDecouvert", compte.getClassName());
    }
}