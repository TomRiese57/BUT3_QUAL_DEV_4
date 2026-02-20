package com.iut.banque.facade;

import com.iut.banque.interfaces.ICarteDao;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Compte;
import com.iut.banque.exceptions.TechnicalException;

import java.util.List;

/**
 * Service métier pour la gestion des cartes bancaires.
 *
 * <p>Point d'entrée unique (façade) pour toutes les opérations sur les cartes :
 * création, paiement, retrait, blocage, gestion des plafonds et débit différé.</p>
 *
 * <p>Ce bean est injecté par Spring dans {@code applicationContext.xml}.</p>
 */
public class CarteManager {

    private ICarteDao carteDao;

    // ------------------------------------------------------------------ //
    //  Injection Spring                                                   //
    // ------------------------------------------------------------------ //

    public void setCarteDao(ICarteDao carteDao) {
        this.carteDao = carteDao;
    }

    // ------------------------------------------------------------------ //
    //  Création / Suppression                                             //
    // ------------------------------------------------------------------ //

    /**
     * Crée une carte bancaire avec les paramètres par défaut.
     *
     * @param compte          compte à associer
     * @param paiementDiffere {@code true} pour le mode différé
     * @return la carte créée et persistée
     * @throws TechnicalException si le compte est null
     */
    public CarteBancaire creerCarte(Compte compte, boolean paiementDiffere)
            throws TechnicalException {
        return creerCarte(compte, paiementDiffere,
                CarteBancaire.PLAFOND_PAIEMENT_DEFAUT,
                CarteBancaire.PLAFOND_RETRAIT_DEFAUT);
    }

    /**
     * Crée une carte bancaire avec des plafonds personnalisés.
     *
     * @param compte           compte à associer
     * @param paiementDiffere  {@code true} pour le mode différé
     * @param plafondPaiement  plafond mensuel de paiement (€, doit être &gt; 0)
     * @param plafondRetrait   plafond mensuel de retrait ATM (€, doit être &gt; 0)
     * @return la carte créée et persistée
     * @throws TechnicalException       si le compte est null
     * @throws IllegalArgumentException si un plafond est invalide
     */
    public CarteBancaire creerCarte(Compte compte, boolean paiementDiffere,
                                    double plafondPaiement, double plafondRetrait)
            throws TechnicalException {

        if (compte == null) {
            throw new TechnicalException("Impossible de créer une carte sur un compte null.");
        }
        CarteBancaire carte = new CarteBancaire(compte, paiementDiffere, plafondPaiement, plafondRetrait);
        carteDao.saveCart(carte);
        return carte;
    }

    /**
     * Supprime une carte bancaire.
     *
     * @param carte carte à supprimer
     * @throws TechnicalException si la carte est null
     */
    public void supprimerCarte(CarteBancaire carte) throws TechnicalException {
        if (carte == null) {
            throw new TechnicalException("La carte à supprimer ne peut pas être null.");
        }
        carteDao.deleteCarte(carte);
    }

    // ------------------------------------------------------------------ //
    //  Opérations de paiement / retrait                                  //
    // ------------------------------------------------------------------ //

    /**
     * Effectue un paiement par carte.
     *
     * @param carte   carte utilisée
     * @param montant montant (doit être &gt; 0)
     * @throws CarteBloqueException      si la carte est bloquée
     * @throws PlafondDepasseException   si le plafond mensuel est atteint
     * @throws SoldeInsuffisantException si le solde est insuffisant (mode immédiat)
     * @throws TechnicalException        si la carte est null
     */
    public void effectuerPaiement(CarteBancaire carte, double montant)
            throws CarteBloqueException, PlafondDepasseException,
            SoldeInsuffisantException, TechnicalException {

        validerCarte(carte);
        carte.effectuerPaiement(montant);
        carteDao.updateCarte(carte);
    }

    /**
     * Effectue un retrait ATM.
     *
     * @param carte   carte utilisée
     * @param montant montant (doit être &gt; 0)
     * @throws CarteBloqueException      si la carte est bloquée
     * @throws PlafondDepasseException   si le plafond retrait est atteint
     * @throws SoldeInsuffisantException si le solde est insuffisant
     * @throws TechnicalException        si la carte est null
     */
    public void effectuerRetrait(CarteBancaire carte, double montant)
            throws CarteBloqueException, PlafondDepasseException,
            SoldeInsuffisantException, TechnicalException {

        validerCarte(carte);
        carte.effectuerRetrait(montant);
        carteDao.updateCarte(carte);
    }

    // ------------------------------------------------------------------ //
    //  Gestion du différé                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Débite les montants différés de toutes les cartes en attente.
     * À appeler via un batch en fin de mois.
     *
     * <p>Les erreurs de solde insuffisant sont journalisées mais n'interrompent
     * pas le traitement des autres cartes.</p>
     *
     * @return nombre de cartes débitées avec succès
     */
    public int debiterTousMontantsDifferes() {
        List<CarteBancaire> cartes = carteDao.getCartesDiffereesADebiter();
        int succes = 0;
        for (CarteBancaire carte : cartes) {
            try {
                carte.debiterMontantDiffere();
                carteDao.updateCarte(carte);
                succes++;
            } catch (SoldeInsuffisantException e) {
                // En production : alerter le service de recouvrement
                // Ici on ne bloque pas les autres cartes
            }
        }
        return succes;
    }

    /**
     * Débite le montant différé d'une seule carte.
     *
     * @param carte carte à débiter
     * @throws SoldeInsuffisantException si le solde est insuffisant
     * @throws TechnicalException        si la carte est null
     */
    public void debiterMontantDiffere(CarteBancaire carte)
            throws SoldeInsuffisantException, TechnicalException {

        validerCarte(carte);
        carte.debiterMontantDiffere();
        carteDao.updateCarte(carte);
    }

    /**
     * Réinitialise les compteurs mensuels de toutes les cartes.
     * À appeler en début de mois (après le débit différé).
     */
    public void reinitialiserTousCompteurs() {
        List<CarteBancaire> cartes = carteDao.getCartesDiffereesADebiter();
        for (CarteBancaire carte : cartes) {
            carte.reinitialiserCompteursMensuels();
            carteDao.updateCarte(carte);
        }
    }

    // ------------------------------------------------------------------ //
    //  Blocage                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Bloque une carte.
     *
     * @param carte carte à bloquer
     * @throws TechnicalException si la carte est null
     */
    public void bloquerCarte(CarteBancaire carte) throws TechnicalException {
        validerCarte(carte);
        carte.bloquer();
        carteDao.updateCarte(carte);
    }

    /**
     * Débloque une carte.
     *
     * @param carte carte à débloquer
     * @throws TechnicalException si la carte est null
     */
    public void debloquerCarte(CarteBancaire carte) throws TechnicalException {
        validerCarte(carte);
        carte.debloquer();
        carteDao.updateCarte(carte);
    }

    // ------------------------------------------------------------------ //
    //  Plafonds                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Modifie le plafond de paiement mensuel d'une carte.
     *
     * @param carte           carte à modifier
     * @param nouveauPlafond  nouveau plafond (€, doit être &gt; 0)
     * @throws TechnicalException       si la carte est null
     * @throws IllegalArgumentException si le plafond est invalide
     */
    public void modifierPlafondPaiement(CarteBancaire carte, double nouveauPlafond)
            throws TechnicalException {
        validerCarte(carte);
        carte.modifierPlafondPaiement(nouveauPlafond);
        carteDao.updateCarte(carte);
    }

    /**
     * Modifie le plafond de retrait ATM mensuel d'une carte.
     *
     * @param carte           carte à modifier
     * @param nouveauPlafond  nouveau plafond (€, doit être &gt; 0)
     * @throws TechnicalException       si la carte est null
     * @throws IllegalArgumentException si le plafond est invalide
     */
    public void modifierPlafondRetrait(CarteBancaire carte, double nouveauPlafond)
            throws TechnicalException {
        validerCarte(carte);
        carte.modifierPlafondRetrait(nouveauPlafond);
        carteDao.updateCarte(carte);
    }

    /**
     * Bascule le mode de paiement (immédiat ↔ différé).
     *
     * @param carte carte à modifier
     * @throws TechnicalException si la carte est null
     */
    public void basculerModePaiement(CarteBancaire carte) throws TechnicalException {
        validerCarte(carte);
        carte.basculerModePaiement();
        carteDao.updateCarte(carte);
    }

    // ------------------------------------------------------------------ //
    //  Lecture                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Retourne toutes les cartes d'un compte.
     *
     * @param compte compte propriétaire
     * @return liste des cartes (jamais null)
     */
    public List<CarteBancaire> getCartesParCompte(Compte compte) {
        return carteDao.getCartesByCompte(compte);
    }

    /**
     * Recherche une carte par son numéro.
     *
     * @param numeroCarte numéro PAN (16 chiffres)
     * @return la carte, ou {@code null} si introuvable
     */
    public CarteBancaire getCarteParNumero(String numeroCarte) {
        return carteDao.getCarteByNumero(numeroCarte);
    }

    // ------------------------------------------------------------------ //
    //  Utilitaire                                                         //
    // ------------------------------------------------------------------ //

    private void validerCarte(CarteBancaire carte) throws TechnicalException {
        if (carte == null) {
            throw new TechnicalException("La carte ne peut pas être null.");
        }
    }
}