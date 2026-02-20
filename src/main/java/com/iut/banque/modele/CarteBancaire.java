package com.iut.banque.modele;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entité représentant une carte bancaire associée à un compte.
 *
 * <p>Fonctionnalités supportées :</p>
 * <ul>
 *   <li>Paiement immédiat ou différé (débit différé en fin de mois)</li>
 *   <li>Plafond de paiement mensuel</li>
 *   <li>Plafond de retrait ATM mensuel</li>
 *   <li>Blocage / déblocage de la carte</li>
 * </ul>
 *
 * <p>Stratégie de persistance : SINGLE_TABLE partagée avec {@code Compte} serait
 * trop couplée – on utilise ici une table dédiée {@code carte_bancaire}.</p>
 */
@Entity
@Table(name = "carte_bancaire")
public class CarteBancaire {

    // ------------------------------------------------------------------ //
    //  Constantes métier                                                   //
    // ------------------------------------------------------------------ //

    /** Plafond de paiement par défaut (€). */
    public static final double PLAFOND_PAIEMENT_DEFAUT = 3000.0;

    /** Plafond de retrait ATM par défaut (€). */
    public static final double PLAFOND_RETRAIT_DEFAUT = 1000.0;

    // ------------------------------------------------------------------ //
    //  Champs persistés                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Numéro de la carte (16 chiffres, format PAN masqué en mémoire).
     * Généré automatiquement à la création.
     */
    @Id
    @Column(name = "numero_carte", length = 16, nullable = false)
    private String numeroCarte;

    /**
     * Compte auquel la carte est rattachée.
     * Plusieurs cartes peuvent appartenir au même compte.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "numero_compte", nullable = false)
    private Compte compte;

    /**
     * {@code true} → paiement différé (débit groupé en fin de mois).
     * {@code false} → débit immédiat à chaque transaction.
     */
    @Column(name = "paiement_differe", nullable = false)
    private boolean paiementDiffere;

    /**
     * Plafond de paiement mensuel autorisé (€).
     * Doit être strictement positif.
     */
    @Column(name = "plafond_paiement", nullable = false)
    private double plafondPaiement;

    /**
     * Plafond de retrait ATM mensuel autorisé (€).
     * Doit être strictement positif.
     */
    @Column(name = "plafond_retrait", nullable = false)
    private double plafondRetrait;

    /**
     * Montant total des paiements effectués depuis le début du mois courant.
     * Réinitialisé à 0 chaque début de mois (batch externe ou à la demande).
     */
    @Column(name = "paiements_mois_courant", nullable = false)
    private double paiementsMoisCourant;

    /**
     * Montant total des retraits ATM effectués depuis le début du mois courant.
     */
    @Column(name = "retraits_mois_courant", nullable = false)
    private double retraitsMoisCourant;

    /**
     * Montant en attente de débit (mode différé uniquement).
     * Sera débité du compte lors du passage en fin de mois.
     */
    @Column(name = "montant_differe_en_attente", nullable = false)
    private double montantDiffereEnAttente;

    /**
     * {@code true} → carte bloquée (aucune opération autorisée).
     */
    @Column(name = "bloquee", nullable = false)
    private boolean bloquee;

    /**
     * Date d'expiration de la carte.
     */
    @Column(name = "date_expiration", nullable = false)
    private LocalDate dateExpiration;

    // ------------------------------------------------------------------ //
    //  Constructeurs                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Constructeur complet.
     *
     * @param compte           compte propriétaire
     * @param paiementDiffere  mode de paiement (différé = true)
     * @param plafondPaiement  plafond mensuel de paiement (doit être &gt; 0)
     * @param plafondRetrait   plafond mensuel de retrait (doit être &gt; 0)
     * @throws IllegalArgumentException si un plafond est négatif ou nul
     */
    public CarteBancaire(Compte compte, boolean paiementDiffere,
                         double plafondPaiement, double plafondRetrait) {
        if (compte == null) {
            throw new IllegalArgumentException("Le compte ne peut pas être null.");
        }
        validatePlafond(plafondPaiement, "paiement");
        validatePlafond(plafondRetrait, "retrait");

        this.numeroCarte        = genererNumeroCarte();
        this.compte             = compte;
        this.paiementDiffere    = paiementDiffere;
        this.plafondPaiement    = plafondPaiement;
        this.plafondRetrait     = plafondRetrait;
        this.paiementsMoisCourant    = 0.0;
        this.retraitsMoisCourant     = 0.0;
        this.montantDiffereEnAttente = 0.0;
        this.bloquee            = false;
        this.dateExpiration     = LocalDate.now().plusYears(3);
    }

    /**
     * Constructeur par défaut requis par Hibernate.
     */
    public CarteBancaire() {
        // Hibernate uniquement
    }

    // ------------------------------------------------------------------ //
    //  Méthodes métier                                                     //
    // ------------------------------------------------------------------ //

    /**
     * Effectue un paiement par carte.
     *
     * <p>En mode immédiat : débite directement le compte.</p>
     * <p>En mode différé : accumule dans {@code montantDiffereEnAttente}.</p>
     *
     * @param montant montant du paiement (doit être &gt; 0)
     * @throws CarteBloqueException       si la carte est bloquée
     * @throws PlafondDepasseException    si le plafond mensuel est dépassé
     * @throws SoldeInsuffisantException  si le solde (+ découvert) est insuffisant (mode immédiat)
     * @throws IllegalArgumentException   si le montant est négatif ou nul
     */
    public void effectuerPaiement(double montant)
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        validerMontant(montant);
        verifierNonBloquee();
        verifierPlafondPaiement(montant);

        if (paiementDiffere) {
            montantDiffereEnAttente += montant;
        } else {
            debiterCompte(montant);
        }
        paiementsMoisCourant += montant;
    }

    /**
     * Effectue un retrait ATM.
     *
     * <p>Les retraits sont toujours débités immédiatement, même en mode différé.</p>
     *
     * @param montant montant du retrait (doit être &gt; 0)
     * @throws CarteBloqueException       si la carte est bloquée
     * @throws PlafondDepasseException    si le plafond ATM mensuel est dépassé
     * @throws SoldeInsuffisantException  si le solde est insuffisant
     * @throws IllegalArgumentException   si le montant est négatif ou nul
     */
    public void effectuerRetrait(double montant)
            throws CarteBloqueException, PlafondDepasseException, SoldeInsuffisantException {

        validerMontant(montant);
        verifierNonBloquee();
        verifierPlafondRetrait(montant);
        debiterCompte(montant);
        retraitsMoisCourant += montant;
    }

    /**
     * Débit du montant différé en attente sur le compte.
     *
     * <p>À appeler en fin de mois pour les cartes en mode différé.
     * Sans effet si la carte est en mode immédiat ou si aucun montant n'est en attente.</p>
     *
     * @throws SoldeInsuffisantException si le compte ne peut pas couvrir le montant différé
     */
    public void debiterMontantDiffere() throws SoldeInsuffisantException {
        if (!paiementDiffere || montantDiffereEnAttente == 0.0) {
            return;
        }
        debiterCompte(montantDiffereEnAttente);
        montantDiffereEnAttente = 0.0;
    }

    /**
     * Réinitialise les compteurs mensuels (paiements et retraits).
     * À appeler en début de chaque mois.
     */
    public void reinitialiserCompteursMensuels() {
        paiementsMoisCourant = 0.0;
        retraitsMoisCourant  = 0.0;
    }

    /**
     * Bloque la carte. Sans effet si déjà bloquée.
     */
    public void bloquer() {
        this.bloquee = true;
    }

    /**
     * Débloque la carte. Sans effet si déjà débloquée.
     */
    public void debloquer() {
        this.bloquee = false;
    }

    /**
     * Bascule le mode de paiement (immédiat ↔ différé).
     *
     * <p>Si un montant différé est en attente, il sera conservé et débité
     * lors du prochain appel à {@link #debiterMontantDiffere()}.</p>
     */
    public void basculerModePaiement() {
        this.paiementDiffere = !this.paiementDiffere;
    }

    /**
     * Modifie le plafond de paiement mensuel.
     *
     * @param nouveauPlafond nouveau plafond (doit être &gt; 0)
     * @throws IllegalArgumentException si le plafond est invalide
     */
    public void modifierPlafondPaiement(double nouveauPlafond) {
        validatePlafond(nouveauPlafond, "paiement");
        this.plafondPaiement = nouveauPlafond;
    }

    /**
     * Modifie le plafond de retrait mensuel.
     *
     * @param nouveauPlafond nouveau plafond (doit être &gt; 0)
     * @throws IllegalArgumentException si le plafond est invalide
     */
    public void modifierPlafondRetrait(double nouveauPlafond) {
        validatePlafond(nouveauPlafond, "retrait");
        this.plafondRetrait = nouveauPlafond;
    }

    /**
     * Indique si la carte est expirée à la date d'aujourd'hui.
     *
     * @return {@code true} si expirée
     */
    public boolean estExpiree() {
        return LocalDate.now().isAfter(dateExpiration);
    }

    // ------------------------------------------------------------------ //
    //  Méthodes privées utilitaires                                       //
    // ------------------------------------------------------------------ //

    /**
     * Génère un numéro de carte de 16 chiffres pseudo-aléatoire.
     * En production, utiliser un générateur conforme PCI-DSS.
     */
    private static String genererNumeroCarte() {
        long raw = Math.abs(UUID.randomUUID().getMostSignificantBits());
        String s = Long.toString(raw);
        // Padder ou tronquer pour obtenir exactement 16 chiffres
        while (s.length() < 16) {
            s = s + "0";
        }
        return s.substring(0, 16);
    }

    private void validerMontant(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif, reçu : " + montant);
        }
    }

    private void verifierNonBloquee() throws CarteBloqueException {
        if (bloquee) {
            throw new CarteBloqueException("La carte " + numeroCarte + " est bloquée.");
        }
    }

    private void verifierPlafondPaiement(double montant) throws PlafondDepasseException {
        if (paiementsMoisCourant + montant > plafondPaiement) {
            throw new PlafondDepasseException(
                    String.format("Plafond paiement dépassé. Consommé : %.2f€ / Plafond : %.2f€ / Demandé : %.2f€",
                            paiementsMoisCourant, plafondPaiement, montant));
        }
    }

    private void verifierPlafondRetrait(double montant) throws PlafondDepasseException {
        if (retraitsMoisCourant + montant > plafondRetrait) {
            throw new PlafondDepasseException(
                    String.format("Plafond retrait dépassé. Consommé : %.2f€ / Plafond : %.2f€ / Demandé : %.2f€",
                            retraitsMoisCourant, plafondRetrait, montant));
        }
    }

    private void debiterCompte(double montant) throws SoldeInsuffisantException {
        try {
            compte.debiter(montant);
        } catch (com.iut.banque.exceptions.InsufficientFundsException e) {
            throw new SoldeInsuffisantException(
                    "Solde insuffisant pour débiter " + montant + "€ du compte " + compte.getNumeroCompte(), e);
        } catch (com.iut.banque.exceptions.IllegalFormatException e) {
            // Ne devrait jamais arriver car on a déjà validé que montant > 0
            throw new IllegalStateException("Erreur interne lors du débit", e);
        }
    }

    private static void validatePlafond(double plafond, String type) {
        if (plafond <= 0) {
            throw new IllegalArgumentException(
                    "Le plafond de " + type + " doit être strictement positif, reçu : " + plafond);
        }
    }

    // ------------------------------------------------------------------ //
    //  Getters / Setters                                                  //
    // ------------------------------------------------------------------ //

    public String getNumeroCarte() { return numeroCarte; }

    public Compte getCompte() { return compte; }

    public boolean isPaiementDiffere() { return paiementDiffere; }

    public double getPlafondPaiement() { return plafondPaiement; }

    public double getPlafondRetrait() { return plafondRetrait; }

    public double getPaiementsMoisCourant() { return paiementsMoisCourant; }

    public double getRetraitsMoisCourant() { return retraitsMoisCourant; }

    public double getMontantDiffereEnAttente() { return montantDiffereEnAttente; }

    public boolean isBloquee() { return bloquee; }

    public LocalDate getDateExpiration() { return dateExpiration; }

    /** Setter Hibernate uniquement. */
    void setNumeroCarte(String numeroCarte) { this.numeroCarte = numeroCarte; }

    /** Setter Hibernate uniquement. */
    void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    // ------------------------------------------------------------------ //
    //  Exceptions internes (classes imbriquées statiques)                 //
    // ------------------------------------------------------------------ //

    /** Levée quand la carte est bloquée. */
    public static class CarteBloqueException extends Exception {
        public CarteBloqueException(String message) { super(message); }
    }

    /** Levée quand un plafond mensuel est dépassé. */
    public static class PlafondDepasseException extends Exception {
        public PlafondDepasseException(String message) { super(message); }
    }

    /** Levée quand le solde du compte est insuffisant. */
    public static class SoldeInsuffisantException extends Exception {
        public SoldeInsuffisantException(String message, Throwable cause) { super(message, cause); }
        public SoldeInsuffisantException(String message) { super(message); }
    }

    // ------------------------------------------------------------------ //
    //  toString                                                           //
    // ------------------------------------------------------------------ //

    @Override
    public String toString() {
        return String.format(
                "CarteBancaire[numero=%s, compte=%s, differe=%b, plafondPaiement=%.2f, plafondRetrait=%.2f, bloquee=%b, expiration=%s]",
                numeroCarte,
                compte != null ? compte.getNumeroCompte() : "null",
                paiementDiffere, plafondPaiement, plafondRetrait, bloquee, dateExpiration);
    }
}