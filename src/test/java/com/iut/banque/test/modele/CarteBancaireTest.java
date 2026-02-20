package com.iut.banque.test.modele;

import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Compte;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests unitaires pour {@link CarteBancaire}.
 *
 * <p>Couverture :</p>
 * <ul>
 *   <li>Création (paramètres valides et invalides)</li>
 *   <li>Paiement immédiat et différé</li>
 *   <li>Retrait ATM</li>
 *   <li>Plafonds (paiement et retrait)</li>
 *   <li>Blocage / déblocage</li>
 *   <li>Débit différé en fin de mois</li>
 *   <li>Réinitialisation des compteurs mensuels</li>
 * </ul>
 *
 * <p>
 * {@link FakeCompte} est un stub minimal qui étend directement {@link Compte}
 * (classe abstraite). On utilise {@link Compte#getSolde()} via l'API publique
 * de {@code Compte} — pas d'accès au champ {@code protected solde} directement.
 * </p>
 */
public class CarteBancaireTest {

    // ------------------------------------------------------------------ //
    //  FakeCompte — stub sans Hibernate ni Spring                        //
    // ------------------------------------------------------------------ //

    /**
     * Compte factice pour les tests : étend la classe abstraite {@link Compte}
     * directement afin de ne dépendre ni d'Hibernate ni de la base de données.
     *
     * <p>Implémente {@code debiter()} avec une règle simple : le solde ne peut
     * pas descendre en dessous de 0 (comme un CompteSansDecouvert).</p>
     *
     * <p>On expose {@code getSolde()} via la méthode publique héritée de
     * {@link Compte} — aucun accès direct au champ {@code protected solde}.</p>
     */
    private static class FakeCompte extends Compte {

        /**
         * Numéro de compte de test fixe valide (2 lettres + 10 chiffres).
         */
        private static final String NUMERO_TEST = "TS0000000001";

        FakeCompte(double soldeInitial) throws com.iut.banque.exceptions.IllegalFormatException {
            super(NUMERO_TEST, soldeInitial, null);
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
    //  Fixtures                                                           //
    // ------------------------------------------------------------------ //

    private FakeCompte compteRiche;      // solde 10 000 €
    private FakeCompte comptePauvre;     // solde 50 €
    private CarteBancaire carteImmediate;
    private CarteBancaire carteDifferee;

    @Before
    public void setUp() throws Exception {
        compteRiche  = new FakeCompte(10_000.0);
        comptePauvre = new FakeCompte(50.0);

        carteImmediate = new CarteBancaire(compteRiche, false,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);

        carteDifferee = new CarteBancaire(compteRiche, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
    }

    // ================================================================== //
    //  1. Construction                                                    //
    // ================================================================== //

    @Test
    public void testCreation_numeroCarte_16Chiffres() {
        String num = carteImmediate.getNumeroCarte();
        assertNotNull("Le numéro de carte ne doit pas être null", num);
        assertEquals("Le numéro doit avoir 16 chiffres", 16, num.length());
        assertTrue("Le numéro doit être numérique", num.matches("\\d{16}"));
    }

    @Test
    public void testCreation_valeursParDefaut() {
        assertFalse("Carte immédiate : paiementDiffere doit être false",
                carteImmediate.isPaiementDiffere());
        assertFalse("Carte neuve ne doit pas être bloquée",
                carteImmediate.isBloquee());
        assertEquals(0.0, carteImmediate.getPaiementsMoisCourant(),    0.001);
        assertEquals(0.0, carteImmediate.getRetraitsMoisCourant(),     0.001);
        assertEquals(0.0, carteImmediate.getMontantDiffereEnAttente(), 0.001);
        assertFalse("Carte non expirée à la création", carteImmediate.estExpiree());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreation_plafondPaiementNegatif_lanceException() throws Exception {
        new CarteBancaire(compteRiche, false, -100, 500);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreation_plafondRetraitZero_lanceException() throws Exception {
        new CarteBancaire(compteRiche, false, 3000, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreation_compteNull_lanceException() {
        new CarteBancaire(null, false, 3000, 1000);
    }

    // ================================================================== //
    //  2. Paiement immédiat                                              //
    // ================================================================== //

    @Test
    public void testPaiementImmediat_debiteCompte()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteImmediate.effectuerPaiement(200.0);

        assertEquals(soldeAvant - 200.0, compteRiche.getSolde(), 0.001);
        assertEquals(200.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
        assertEquals(0.0,   carteImmediate.getMontantDiffereEnAttente(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaiementImmediat_montantNegatif_lanceException()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {
        carteImmediate.effectuerPaiement(-50.0);
    }

    @Test(expected = SoldeInsuffisantException.class)
    public void testPaiementImmediat_soldeInsuffisant_lanceException() throws Exception {
        CarteBancaire carteComptePauvre = new CarteBancaire(
                comptePauvre, false,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
        carteComptePauvre.effectuerPaiement(200.0); // solde = 50, insuffisant
    }

    // ================================================================== //
    //  3. Paiement différé                                               //
    // ================================================================== //

    @Test
    public void testPaiementDiffere_neDebitePassImmediatement()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteDifferee.effectuerPaiement(500.0);

        // Compte non touché
        assertEquals(soldeAvant, compteRiche.getSolde(), 0.001);
        // Montant en attente
        assertEquals(500.0, carteDifferee.getMontantDiffereEnAttente(), 0.001);
        // Compteur mensuel mis à jour
        assertEquals(500.0, carteDifferee.getPaiementsMoisCourant(), 0.001);
    }

    @Test
    public void testPaiementDiffere_multiplesPaiementsAccumules()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteDifferee.effectuerPaiement(100.0);
        carteDifferee.effectuerPaiement(200.0);
        carteDifferee.effectuerPaiement(300.0);

        assertEquals(600.0, carteDifferee.getMontantDiffereEnAttente(), 0.001);
        assertEquals(600.0, carteDifferee.getPaiementsMoisCourant(),    0.001);
    }

    @Test
    public void testDebiterMontantDiffere_debiteLeCompte()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteDifferee.effectuerPaiement(750.0);
        carteDifferee.debiterMontantDiffere();

        assertEquals(soldeAvant - 750.0, compteRiche.getSolde(), 0.001);
        assertEquals(0.0, carteDifferee.getMontantDiffereEnAttente(), 0.001);
    }

    @Test
    public void testDebiterMontantDiffere_carteImmediate_sansEffet()
            throws SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteImmediate.debiterMontantDiffere();
        assertEquals(soldeAvant, compteRiche.getSolde(), 0.001);
    }

    @Test
    public void testDebiterMontantDiffere_montantZero_sansEffet()
            throws SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteDifferee.debiterMontantDiffere();
        assertEquals(soldeAvant, compteRiche.getSolde(), 0.001);
    }

    @Test(expected = SoldeInsuffisantException.class)
    public void testDebiterMontantDiffere_soldeInsuffisant_lanceException() throws Exception {
        // comptePauvre = 50€
        CarteBancaire cartePauvre = new CarteBancaire(
                comptePauvre, true,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);

        // Accumule 60€ en différé (le compte n'est pas débité encore)
        cartePauvre.effectuerPaiement(30.0);
        cartePauvre.effectuerPaiement(30.0);

        // Fin de mois : tente de débiter 60€ sur un compte à 50€ → exception
        cartePauvre.debiterMontantDiffere();
    }

    // ================================================================== //
    //  4. Retrait ATM                                                    //
    // ================================================================== //

    @Test
    public void testRetrait_debiteCompteImmediatement()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteImmediate.effectuerRetrait(300.0);

        assertEquals(soldeAvant - 300.0, compteRiche.getSolde(), 0.001);
        assertEquals(300.0, carteImmediate.getRetraitsMoisCourant(), 0.001);
    }

    @Test
    public void testRetrait_carteDifferee_toujursImmediat()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        double soldeAvant = compteRiche.getSolde();
        carteDifferee.effectuerRetrait(200.0);

        // Débit immédiat même en mode différé
        assertEquals(soldeAvant - 200.0, compteRiche.getSolde(), 0.001);
        // Aucun montant différé accumulé
        assertEquals(0.0, carteDifferee.getMontantDiffereEnAttente(), 0.001);
    }

    // ================================================================== //
    //  5. Plafonds                                                       //
    // ================================================================== //

    @Test(expected = PlafondDepasseException.class)
    public void testPlafondPaiement_depasse_lanceException()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire cartePlafondBas = null;
        try {
            cartePlafondBas = new CarteBancaire(compteRiche, false, 100.0, 1000.0);
        } catch (Exception e) {
            fail("La création de la carte ne devrait pas échouer : " + e.getMessage());
        }
        cartePlafondBas.effectuerPaiement(150.0); // dépasse 100 €
    }

    @Test
    public void testPlafondPaiement_modificationOK()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.modifierPlafondPaiement(500.0);
        assertEquals(500.0, carteImmediate.getPlafondPaiement(), 0.001);

        carteImmediate.effectuerPaiement(499.0);
        assertEquals(499.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlafondPaiement_modificationNegative_lanceException() {
        carteImmediate.modifierPlafondPaiement(-200.0);
    }

    @Test(expected = PlafondDepasseException.class)
    public void testPlafondRetrait_depasse_lanceException()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        CarteBancaire carteRetraitBas = null;
        try {
            carteRetraitBas = new CarteBancaire(compteRiche, false, 3000.0, 50.0);
        } catch (Exception e) {
            fail("La création de la carte ne devrait pas échouer : " + e.getMessage());
        }
        carteRetraitBas.effectuerRetrait(100.0); // dépasse 50 €
    }

    @Test
    public void testPlafondRetrait_modificationOK() {
        carteImmediate.modifierPlafondRetrait(2000.0);
        assertEquals(2000.0, carteImmediate.getPlafondRetrait(), 0.001);
    }

    @Test
    public void testPlafondCumul_deuxPaiementsRespectentPlafond()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        // Plafond = 3 000 € → 1 400 + 1 400 = 2 800 → OK
        carteImmediate.effectuerPaiement(1400.0);
        carteImmediate.effectuerPaiement(1400.0);
        assertEquals(2800.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
    }

    @Test(expected = PlafondDepasseException.class)
    public void testPlafondCumul_troisiemePaiementDepassePlafond()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.effectuerPaiement(1500.0);
        carteImmediate.effectuerPaiement(1500.0);
        carteImmediate.effectuerPaiement(1.0); // plafond atteint → exception
    }

    // ================================================================== //
    //  6. Blocage                                                        //
    // ================================================================== //

    @Test
    public void testBloquer_carteEstBloquee() {
        assertFalse(carteImmediate.isBloquee());
        carteImmediate.bloquer();
        assertTrue("La carte doit être bloquée", carteImmediate.isBloquee());
    }

    @Test
    public void testDebloquer_carteEstDebloquee() {
        carteImmediate.bloquer();
        carteImmediate.debloquer();
        assertFalse("La carte doit être débloquée", carteImmediate.isBloquee());
    }

    @Test(expected = CarteBloqueException.class)
    public void testPaiement_carteBloquee_lanceException()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.bloquer();
        carteImmediate.effectuerPaiement(100.0);
    }

    @Test(expected = CarteBloqueException.class)
    public void testRetrait_carteBloquee_lanceException()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.bloquer();
        carteImmediate.effectuerRetrait(100.0);
    }

    @Test
    public void testBloquer_puisDebloquer_paiementFonctionne()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.bloquer();
        carteImmediate.debloquer();

        double soldeAvant = compteRiche.getSolde();
        carteImmediate.effectuerPaiement(50.0);
        assertEquals(soldeAvant - 50.0, compteRiche.getSolde(), 0.001);
    }

    // ================================================================== //
    //  7. Bascule mode paiement                                          //
    // ================================================================== //

    @Test
    public void testBasculerModePaiement_immediat_versDiffere() {
        assertFalse(carteImmediate.isPaiementDiffere());
        carteImmediate.basculerModePaiement();
        assertTrue(carteImmediate.isPaiementDiffere());
    }

    @Test
    public void testBasculerModePaiement_differe_versImmediat() {
        assertTrue(carteDifferee.isPaiementDiffere());
        carteDifferee.basculerModePaiement();
        assertFalse(carteDifferee.isPaiementDiffere());
    }

    // ================================================================== //
    //  8. Réinitialisation des compteurs                                 //
    // ================================================================== //

    @Test
    public void testReinitialiserCompteurs_remiseAZero()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        carteImmediate.effectuerPaiement(200.0);
        carteImmediate.effectuerRetrait(100.0);
        assertEquals(200.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
        assertEquals(100.0, carteImmediate.getRetraitsMoisCourant(),  0.001);

        carteImmediate.reinitialiserCompteursMensuels();

        assertEquals(0.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
        assertEquals(0.0, carteImmediate.getRetraitsMoisCourant(),  0.001);
    }

    @Test
    public void testReinitialiserCompteurs_permetNouveauxPaiements()
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        // Consomme tout le plafond
        carteImmediate.effectuerPaiement(CarteBancaire.PLAFOND_PAIEMENT_DEFAUT);

        // Nouveau mois
        carteImmediate.reinitialiserCompteursMensuels();

        // Doit pouvoir payer à nouveau
        carteImmediate.effectuerPaiement(100.0);
        assertEquals(100.0, carteImmediate.getPaiementsMoisCourant(), 0.001);
    }

    // ================================================================== //
    //  9. Scénario de bout en bout                                       //
    // ================================================================== //

    @Test
    public void testScenarioComplet_moisDiffere() throws Exception {
        // -- Mois courant --
        // Plusieurs achats en différé
        carteDifferee.effectuerPaiement(500.0);
        carteDifferee.effectuerPaiement(300.0);
        // Un retrait immédiat même en mode différé
        carteDifferee.effectuerRetrait(200.0);

        double soldeApresRetrait = compteRiche.getSolde(); // 10 000 - 200 = 9 800
        assertEquals(9800.0, soldeApresRetrait, 0.001);
        assertEquals(800.0,  carteDifferee.getMontantDiffereEnAttente(), 0.001);
        assertEquals(800.0,  carteDifferee.getPaiementsMoisCourant(),    0.001);
        assertEquals(200.0,  carteDifferee.getRetraitsMoisCourant(),     0.001);

        // -- Fin de mois : débit différé --
        carteDifferee.debiterMontantDiffere();
        assertEquals(9800.0 - 800.0, compteRiche.getSolde(), 0.001); // 9 000
        assertEquals(0.0, carteDifferee.getMontantDiffereEnAttente(), 0.001);

        // -- Début de mois suivant : réinitialisation --
        carteDifferee.reinitialiserCompteursMensuels();
        assertEquals(0.0, carteDifferee.getPaiementsMoisCourant(), 0.001);
        assertEquals(0.0, carteDifferee.getRetraitsMoisCourant(),  0.001);
    }
}