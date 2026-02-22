package com.iut.banque.test.facade;

import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.CarteManager;
import com.iut.banque.interfaces.ICarteDao;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Compte;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour {@link CarteManager}.
 *
 * <p>Utilise Mockito pour isoler le DAO ({@link ICarteDao}) et
 * un stub interne de {@link Compte} pour isoler la couche BDD.</p>
 */
public class CarteManagerTest {

    // ------------------------------------------------------------------ //
    //  FakeCompte — même stub que dans CarteBancaireTest                 //
    // ------------------------------------------------------------------ //

    private static class FakeCompte extends Compte {

        private static final String NUMERO_TEST = "TS0000000002";

        FakeCompte(double solde) throws com.iut.banque.exceptions.IllegalFormatException {
            super(NUMERO_TEST, solde, null);
        }

        @Override
        public void debiter(double montant)
                throws com.iut.banque.exceptions.InsufficientFundsException,
                com.iut.banque.exceptions.IllegalFormatException {
            if (montant < 0) {
                throw new com.iut.banque.exceptions.IllegalFormatException(
                        "Le montant ne peut pas être négatif");
            }
            if (montant > this.solde) {
                throw new com.iut.banque.exceptions.InsufficientFundsException(
                        "Solde insuffisant : " + this.solde + " < " + montant);
            }
            this.solde -= montant;
        }
    }

    // ------------------------------------------------------------------ //
    //  Fixtures                                                          //
    // ------------------------------------------------------------------ //

    private ICarteDao    carteDao;
    private CarteManager carteManager;
    private Compte       compte;

    @Before
    public void setUp() throws Exception {
        carteDao     = mock(ICarteDao.class);
        carteManager = new CarteManager();
        carteManager.setCarteDao(carteDao);
        compte = new FakeCompte(10_000.0);
    }

    // ================================================================== //
    //  1. Création                                                       //
    // ================================================================== //

    @Test
    public void testCreerCarte_defaut_saveAppele() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);

        assertNotNull(carte);
        assertFalse(carte.isPaiementDiffere());
        assertEquals(CarteBancaire.PLAFOND_PAIEMENT_DEFAUT, carte.getPlafondPaiement(), 0.001);
        verify(carteDao, times(1)).saveCart(carte);
    }

    @Test
    public void testCreerCarte_avecPlafonds_sauvegardee() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, true, 500.0, 200.0);

        assertTrue(carte.isPaiementDiffere());
        assertEquals(500.0, carte.getPlafondPaiement(), 0.001);
        assertEquals(200.0, carte.getPlafondRetrait(),  0.001);
        verify(carteDao).saveCart(carte);
    }

    @Test(expected = TechnicalException.class)
    public void testCreerCarte_compteNull_lanceException() throws TechnicalException {
        carteManager.creerCarte(null, false);
    }

    // ================================================================== //
    //  2. Suppression                                                    //
    // ================================================================== //

    @Test
    public void testSupprimerCarte_deleteAppele() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.supprimerCarte(carte);

        verify(carteDao).deleteCarte(carte);
    }

    @Test(expected = TechnicalException.class)
    public void testSupprimerCarte_null_lanceException() throws TechnicalException {
        carteManager.supprimerCarte(null);
    }

    // ================================================================== //
    //  3. Paiement                                                       //
    // ================================================================== //

    @Test
    public void testEffectuerPaiement_updateAppele()
            throws TechnicalException, CarteBloqueException,
            PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.effectuerPaiement(carte, 100.0);

        verify(carteDao, times(1)).updateCarte(carte); // saveCart à la création, updateCarte au paiement
        verify(carteDao, times(1)).saveCart(carte);
        assertEquals(100.0, carte.getPaiementsMoisCourant(), 0.001);
    }

    @Test(expected = TechnicalException.class)
    public void testEffectuerPaiement_carteNull_lanceException()
            throws TechnicalException, CarteBloqueException,
            PlafondDepasseException, SoldeInsuffisantException {

        carteManager.effectuerPaiement(null, 100.0);
    }

    @Test(expected = CarteBloqueException.class)
    public void testEffectuerPaiement_carteBloquee_lanceException()
            throws TechnicalException, CarteBloqueException,
            PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire carte = carteManager.creerCarte(compte, false);
        carteManager.bloquerCarte(carte);
        carteManager.effectuerPaiement(carte, 100.0);
    }

    @Test(expected = PlafondDepasseException.class)
    public void testEffectuerPaiement_plafondDepasse_lanceException()
            throws TechnicalException, CarteBloqueException,
            PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire carte = carteManager.creerCarte(compte, false, 50.0, 1000.0);
        carteManager.effectuerPaiement(carte, 200.0); // 200 > plafond 50
    }

    // ================================================================== //
    //  4. Retrait                                                        //
    // ================================================================== //

    @Test
    public void testEffectuerRetrait_updateAppele()
            throws TechnicalException, CarteBloqueException,
            PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.effectuerRetrait(carte, 200.0);

        verify(carteDao, atLeast(1)).updateCarte(carte);
        assertEquals(200.0, carte.getRetraitsMoisCourant(), 0.001);
    }

    // ================================================================== //
    //  5. Blocage                                                        //
    // ================================================================== //

    @Test
    public void testBloquerCarte_estBloquee() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.bloquerCarte(carte);

        assertTrue(carte.isBloquee());
        verify(carteDao, atLeast(1)).updateCarte(carte);
    }

    @Test
    public void testDebloquerCarte_estDebloquee() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);
        carteManager.bloquerCarte(carte);

        carteManager.debloquerCarte(carte);

        assertFalse(carte.isBloquee());
    }

    @Test(expected = TechnicalException.class)
    public void testBloquerCarte_null_lanceException() throws TechnicalException {
        carteManager.bloquerCarte(null);
    }

    // ================================================================== //
    //  6. Plafonds                                                       //
    // ================================================================== //

    @Test
    public void testModifierPlafondPaiement_ok() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.modifierPlafondPaiement(carte, 1500.0);

        assertEquals(1500.0, carte.getPlafondPaiement(), 0.001);
        verify(carteDao, atLeast(1)).updateCarte(carte);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModifierPlafondPaiement_invalide_lanceException() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);
        carteManager.modifierPlafondPaiement(carte, 0.0);
    }

    @Test
    public void testModifierPlafondRetrait_ok() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);

        carteManager.modifierPlafondRetrait(carte, 800.0);

        assertEquals(800.0, carte.getPlafondRetrait(), 0.001);
    }

    // ================================================================== //
    //  7. Mode de paiement                                               //
    // ================================================================== //

    @Test
    public void testBasculerModePaiement_immediat_versDiffere() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);
        assertFalse(carte.isPaiementDiffere());

        carteManager.basculerModePaiement(carte);

        assertTrue(carte.isPaiementDiffere());
    }

    @Test
    public void testBasculerModePaiement_differe_versImmediat() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, true);
        assertTrue(carte.isPaiementDiffere());

        carteManager.basculerModePaiement(carte);

        assertFalse(carte.isPaiementDiffere());
    }

    // ================================================================== //
    //  8. Débit différé en lot                                           //
    // ================================================================== //

    @Test
    public void testDebiterTousMontantsDifferes_debiteCartesEnAttente() throws Exception {
        FakeCompte c1Compte = new FakeCompte(5000.0);
        FakeCompte c2Compte = new FakeCompte(5000.0);
        CarteBancaire c1 = new CarteBancaire(c1Compte, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT, CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
        CarteBancaire c2 = new CarteBancaire(c2Compte, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT, CarteBancaire.PLAFOND_RETRAIT_DEFAUT);

        c1.effectuerPaiement(500.0);
        c2.effectuerPaiement(300.0);

        when(carteDao.getCartesDiffereesADebiter()).thenReturn(Arrays.asList(c1, c2));

        int succes = carteManager.debiterTousMontantsDifferes();

        assertEquals(2, succes);
        assertEquals(0.0, c1.getMontantDiffereEnAttente(), 0.001);
        assertEquals(0.0, c2.getMontantDiffereEnAttente(), 0.001);
        verify(carteDao).updateCarte(c1);
        verify(carteDao).updateCarte(c2);
    }

    @Test
    public void testDebiterTousMontantsDifferes_erreurSolde_continueLesAutres() throws Exception {
        FakeCompte c1ComptePlein = new FakeCompte(100.0);
        CarteBancaire c1bis = new CarteBancaire(c1ComptePlein, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT, CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
        c1bis.effectuerPaiement(100.0); // accumule 100 en différé, compte non débité
        // Maintenant on vide le compte directement via debiter de Compte
        c1ComptePlein.debiter(100.0); // solde = 0, mais 100 en attente de débit → va échouer

        // c2 : solde ok
        FakeCompte c2Compte = new FakeCompte(5000.0);
        CarteBancaire c2 = new CarteBancaire(c2Compte, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT, CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
        c2.effectuerPaiement(200.0);

        when(carteDao.getCartesDiffereesADebiter()).thenReturn(Arrays.asList(c1bis, c2));

        int succes = carteManager.debiterTousMontantsDifferes();

        // c1bis échoue (solde 0 < 100), c2 réussit
        assertEquals(1, succes);
        verify(carteDao, times(1)).updateCarte(c2);
    }

    @Test
    public void testDebiterTousMontantsDifferes_aucuneCarte_retourneZero() {
        when(carteDao.getCartesDiffereesADebiter()).thenReturn(Collections.emptyList());

        int succes = carteManager.debiterTousMontantsDifferes();

        assertEquals(0, succes);
    }

    // ================================================================== //
    //  9. Lecture                                                        //
    // ================================================================== //

    @Test
    public void testGetCartesParCompte_delegueAuDao() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);
        when(carteDao.getCartesByCompte(compte)).thenReturn(Collections.singletonList(carte));

        List<CarteBancaire> result = carteManager.getCartesParCompte(compte);

        assertEquals(1, result.size());
        assertSame(carte, result.get(0));
        verify(carteDao).getCartesByCompte(compte);
    }

    @Test
    public void testGetCarteParNumero_delegueAuDao() throws TechnicalException {
        CarteBancaire carte = carteManager.creerCarte(compte, false);
        String num = carte.getNumeroCarte();
        when(carteDao.getCarteByNumero(num)).thenReturn(carte);

        CarteBancaire result = carteManager.getCarteParNumero(num);

        assertSame(carte, result);
        verify(carteDao).getCarteByNumero(num);
    }

    @Test
    public void testGetCarteParNumero_inconnue_retourneNull() {
        when(carteDao.getCarteByNumero("9999999999999999")).thenReturn(null);

        CarteBancaire result = carteManager.getCarteParNumero("9999999999999999");

        assertNull(result);
    }

    // ================================================================== //
    //  10. ArgumentCaptor — vérification du contenu sauvegardé          //
    // ================================================================== //

    @Test
    public void testCreerCarte_contenuPersistenceCorrect() throws TechnicalException {
        ArgumentCaptor<CarteBancaire> captor = ArgumentCaptor.forClass(CarteBancaire.class);

        carteManager.creerCarte(compte, true, 2000.0, 800.0);

        verify(carteDao).saveCart(captor.capture());
        CarteBancaire persistee = captor.getValue();

        assertTrue(persistee.isPaiementDiffere());
        assertEquals(2000.0, persistee.getPlafondPaiement(), 0.001);
        assertEquals(800.0,  persistee.getPlafondRetrait(),  0.001);
        assertFalse(persistee.isBloquee());
        assertSame(compte, persistee.getCompte());
    }
}