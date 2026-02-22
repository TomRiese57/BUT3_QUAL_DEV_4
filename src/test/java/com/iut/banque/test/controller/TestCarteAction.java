package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.CarteAction;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.CarteManager;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAwareSupport;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Tests unitaires pour {@link CarteAction}.
 * Utilise Objenesis pour instancier sans servlet context et Mockito pour
 * isoler BanqueFacade et CarteManager.
 */
public class TestCarteAction {

    private CarteAction action;
    private BanqueFacade mockFacade;
    private CarteManager mockCarteManager;
    private Client client;
    private CompteSansDecouvert compte;
    private CarteBancaire carte;

    @Before
    public void setUp() throws Exception {
        // 1. Instanciation sans constructeur (pas de servlet context)
        action = new ObjenesisStd().newInstance(CarteAction.class);

        // 2. Injection des mocks
        mockFacade = mock(BanqueFacade.class);
        mockCarteManager = mock(CarteManager.class);

        Field fBanque = CarteAction.class.getDeclaredField("banque");
        fBanque.setAccessible(true);
        fBanque.set(action, mockFacade);

        Field fCarte = CarteAction.class.getDeclaredField("carteManager");
        fCarte.setAccessible(true);
        fCarte.set(action, mockCarteManager);

        // 3. Injection du ValidationAwareSupport (Struts)
        Field vaField = ActionSupport.class.getDeclaredField("validationAware");
        vaField.setAccessible(true);
        vaField.set(action, new ValidationAwareSupport());

        // 4. Initialisation des objets de test
        client = new Client("Doe", "John", "1 rue", true, "j.doe1", "password", "123456789");
        compte = new CompteSansDecouvert("FR0123456789", 1000, client);
        client.addAccount(compte);

        // Carte bancaire fictive (on utilise Objenesis car le constructeur génère un numéro)
        carte = new ObjenesisStd().newInstance(CarteBancaire.class);
        Field numField = CarteBancaire.class.getDeclaredField("numeroCarte");
        numField.setAccessible(true);
        numField.set(carte, "1234567890123456");

        // 5. Initialisation du champ "cartes" (List)
        Field cartesField = CarteAction.class.getDeclaredField("cartes");
        cartesField.setAccessible(true);
        cartesField.set(action, new java.util.ArrayList<>());
    }

    // ------------------------------------------------------------------ //
    //  listerCartes                                                      //
    // ------------------------------------------------------------------ //

    @Test
    public void testListerCartes_clientConnecte() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(compte)).thenReturn(Collections.singletonList(carte));

        String result = action.listerCartes();

        assertEquals("success", result);
        assertEquals(1, action.getCartes().size());
    }

    @Test
    public void testListerCartes_gestionnnaireConnecte() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "a", true, "admin", "pwd");
        when(mockFacade.getConnectedUser()).thenReturn(manager);

        String result = action.listerCartes();

        assertEquals("success", result);
        assertTrue(action.getCartes().isEmpty());
    }

    @Test
    public void testListerCartes_sessionExpiree() {
        when(mockFacade.getConnectedUser()).thenReturn(null);

        String result = action.listerCartes();

        assertEquals("success", result);
        assertTrue(action.getCartes().isEmpty());
    }

    @Test
    public void testListerCartes_carteManagerRetourneNull() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(compte)).thenReturn(null);

        String result = action.listerCartes();
        assertEquals("success", result);
        assertTrue(action.getCartes().isEmpty());
    }

    // ------------------------------------------------------------------ //
    //  creerCarte                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void testCreerCarte_sessionExpiree() {
        when(mockFacade.getConnectedUser()).thenReturn(null);

        String result = action.creerCarte();
        assertEquals("error", result);
    }

    @Test
    public void testCreerCarte_compteIntrouvable() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setNumeroCompte("INEXISTANT");

        String result = action.creerCarte();
        assertEquals("input", result);
    }

    @Test
    public void testCreerCarte_succes() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setNumeroCompte("FR0123456789");
        action.setNouveauPlafondPaiement(0); // va utiliser valeurs par défaut
        action.setNouveauPlafondRetrait(0);

        when(mockCarteManager.creerCarte(eq(compte), anyBoolean(), anyDouble(), anyDouble()))
                .thenReturn(carte);
        when(mockCarteManager.getCartesParCompte(compte)).thenReturn(Collections.singletonList(carte));

        String result = action.creerCarte();
        assertEquals("success", result);
    }

    @Test
    public void testCreerCarte_avecPlafonds() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setNumeroCompte("FR0123456789");
        action.setNouveauPlafondPaiement(500.0);
        action.setNouveauPlafondRetrait(200.0);

        when(mockCarteManager.creerCarte(eq(compte), anyBoolean(), eq(500.0), eq(200.0)))
                .thenReturn(carte);
        when(mockCarteManager.getCartesParCompte(compte)).thenReturn(Collections.singletonList(carte));

        String result = action.creerCarte();
        assertEquals("success", result);
        verify(mockCarteManager).creerCarte(compte, false, 500.0, 200.0);
    }

    @Test
    public void testCreerCarte_technicalException() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setNumeroCompte("FR0123456789");

        doThrow(new TechnicalException("err")).when(mockCarteManager)
                .creerCarte(any(), anyBoolean(), anyDouble(), anyDouble());
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        String result = action.creerCarte();
        assertEquals("success", result); // SUCCESS mais avec message d'erreur
    }

    @Test
    public void testCreerCarte_illegalArgumentException() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setNumeroCompte("FR0123456789");

        doThrow(new IllegalArgumentException("bad")).when(mockCarteManager)
                .creerCarte(any(), anyBoolean(), anyDouble(), anyDouble());
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        String result = action.creerCarte();
        assertEquals("success", result);
    }

    // ------------------------------------------------------------------ //
    //  bloquerCarte                                                      //
    // ------------------------------------------------------------------ //

    @Test
    public void testBloquerCarte_carteIntrouvable() {
        action.setNumeroCarte("0000000000000000");
        when(mockCarteManager.getCarteParNumero("0000000000000000")).thenReturn(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        String result = action.bloquerCarte();
        assertEquals("error", result);
    }

    @Test
    public void testBloquerCarte_numerCarteNull() {
        action.setNumeroCarte(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        String result = action.bloquerCarte();
        assertEquals("error", result);
    }

    @Test
    public void testBloquerCarte_numerCarteVide() {
        action.setNumeroCarte("   ");
        when(mockFacade.getConnectedUser()).thenReturn(client);

        String result = action.bloquerCarte();
        assertEquals("error", result);
    }

    @Test
    public void testBloquerCarte_succes() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        String result = action.bloquerCarte();
        assertEquals("success", result);
        verify(mockCarteManager).bloquerCarte(carte);
    }

    @Test
    public void testBloquerCarte_technicalException() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new TechnicalException("err")).when(mockCarteManager).bloquerCarte(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        String result = action.bloquerCarte();
        assertEquals("success", result); // retourne success mais avec erreur action
    }

    // ------------------------------------------------------------------ //
    //  debloquerCarte                                                    //
    // ------------------------------------------------------------------ //

    @Test
    public void testDebloquerCarte_carteIntrouvable() {
        action.setNumeroCarte("0000000000000000");
        when(mockCarteManager.getCarteParNumero("0000000000000000")).thenReturn(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        assertEquals("error", action.debloquerCarte());
    }

    @Test
    public void testDebloquerCarte_succes() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.debloquerCarte());
        verify(mockCarteManager).debloquerCarte(carte);
    }

    @Test
    public void testDebloquerCarte_technicalException() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new TechnicalException("err")).when(mockCarteManager).debloquerCarte(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.debloquerCarte());
    }

    // ------------------------------------------------------------------ //
    //  basculerMode                                                      //
    // ------------------------------------------------------------------ //

    @Test
    public void testBasculerMode_carteIntrouvable() {
        action.setNumeroCarte("0000000000000000");
        when(mockCarteManager.getCarteParNumero("0000000000000000")).thenReturn(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        assertEquals("error", action.basculerMode());
    }

    @Test
    public void testBasculerMode_succes_immediat() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        // carte.isPaiementDiffere() retourne false par défaut (champ non initialisé = false)
        assertEquals("success", action.basculerMode());
        verify(mockCarteManager).basculerModePaiement(carte);
    }

    @Test
    public void testBasculerMode_succes_differe() throws Exception {
        action.setNumeroCarte("1234567890123456");
        // Mettre la carte en mode différé
        Field differeField = CarteBancaire.class.getDeclaredField("paiementDiffere");
        differeField.setAccessible(true);
        differeField.set(carte, true);

        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.basculerMode());
    }

    @Test
    public void testBasculerMode_technicalException() throws Exception {
        action.setNumeroCarte("1234567890123456");
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new TechnicalException("err")).when(mockCarteManager).basculerModePaiement(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.basculerMode());
    }

    // ------------------------------------------------------------------ //
    //  modifierPlafondPaiement                                           //
    // ------------------------------------------------------------------ //

    @Test
    public void testModifierPlafondPaiement_carteIntrouvable() {
        action.setNumeroCarte("0000000000000000");
        when(mockCarteManager.getCarteParNumero("0000000000000000")).thenReturn(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        assertEquals("error", action.modifierPlafondPaiement());
    }

    @Test
    public void testModifierPlafondPaiement_succes() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondPaiement(1500.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondPaiement());
        verify(mockCarteManager).modifierPlafondPaiement(carte, 1500.0);
    }

    @Test
    public void testModifierPlafondPaiement_illegalArgument() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondPaiement(-100.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new IllegalArgumentException("bad")).when(mockCarteManager)
                .modifierPlafondPaiement(carte, -100.0);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondPaiement());
    }

    @Test
    public void testModifierPlafondPaiement_technicalException() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondPaiement(1000.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new TechnicalException("err")).when(mockCarteManager)
                .modifierPlafondPaiement(carte, 1000.0);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondPaiement());
    }

    // ------------------------------------------------------------------ //
    //  modifierPlafondRetrait                                            //
    // ------------------------------------------------------------------ //

    @Test
    public void testModifierPlafondRetrait_carteIntrouvable() {
        action.setNumeroCarte("0000000000000000");
        when(mockCarteManager.getCarteParNumero("0000000000000000")).thenReturn(null);
        when(mockFacade.getConnectedUser()).thenReturn(client);

        assertEquals("error", action.modifierPlafondRetrait());
    }

    @Test
    public void testModifierPlafondRetrait_succes() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondRetrait(800.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondRetrait());
        verify(mockCarteManager).modifierPlafondRetrait(carte, 800.0);
    }

    @Test
    public void testModifierPlafondRetrait_illegalArgument() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondRetrait(-50.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new IllegalArgumentException("bad")).when(mockCarteManager)
                .modifierPlafondRetrait(carte, -50.0);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondRetrait());
    }

    @Test
    public void testModifierPlafondRetrait_technicalException() throws Exception {
        action.setNumeroCarte("1234567890123456");
        action.setNouveauPlafondRetrait(500.0);
        when(mockCarteManager.getCarteParNumero("1234567890123456")).thenReturn(carte);
        doThrow(new TechnicalException("err")).when(mockCarteManager)
                .modifierPlafondRetrait(carte, 500.0);
        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(any())).thenReturn(Collections.emptyList());

        assertEquals("success", action.modifierPlafondRetrait());
    }

    // ------------------------------------------------------------------ //
    //  Getters / Setters                                                 //
    // ------------------------------------------------------------------ //

    @Test
    public void testGetConnectedUser() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        assertEquals(client, action.getConnectedUser());
    }

    @Test
    public void testGettersSetters() {
        action.setNumeroCarte("1234567890123456");
        assertEquals("1234567890123456", action.getNumeroCarte());

        action.setNumeroCompte("FR0123456789");
        assertEquals("FR0123456789", action.getNumeroCompte());

        action.setNouveauPlafondPaiement(1000.0);
        assertEquals(1000.0, action.getNouveauPlafondPaiement(), 0.001);

        action.setNouveauPlafondRetrait(500.0);
        assertEquals(500.0, action.getNouveauPlafondRetrait(), 0.001);

        action.setPaiementDiffere(true);
        assertTrue(action.isPaiementDiffere());

        assertNotNull(action.getCartes());
    }

    // ------------------------------------------------------------------ //
    //  chargerCartesClient — carte retournée null (aucun ajout)         //
    // ------------------------------------------------------------------ //

    @Test
    public void testListerCartes_avecPlusieursComptes() throws Exception {
        // Ajouter un 2e compte au client
        CompteSansDecouvert compte2 = new CompteSansDecouvert("FR9876543210", 500, client);
        client.addAccount(compte2);

        when(mockFacade.getConnectedUser()).thenReturn(client);
        when(mockCarteManager.getCartesParCompte(compte)).thenReturn(Collections.singletonList(carte));
        when(mockCarteManager.getCartesParCompte(compte2)).thenReturn(Collections.emptyList());

        String result = action.listerCartes();
        assertEquals("success", result);
        assertEquals(1, action.getCartes().size());
    }
}