package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.CarteManager;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.exceptions.TechnicalException;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.List;

/**
 * Action Struts pour la gestion des cartes bancaires.
 *
 * <p>Toutes les actions qui modifient une carte (blocage, plafond, paiement…)
 * sont réservées aux gestionnaires, sauf {@code effectuerPaiement} et
 * {@code effectuerRetrait} accessibles au client propriétaire du compte.</p>
 */
public class CarteAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ //
    //  Attributs injectés / liés au formulaire                           //
    // ------------------------------------------------------------------ //

    private transient CarteManager carteManager;
    private transient BanqueFacade banque;

    /** Numéro de compte (pour créer une carte ou lister les cartes d'un compte). */
    private transient Compte compte;

    /** Carte sélectionnée (conversions Struts via AccountConverter étendu). */
    private transient CarteBancaire carte;

    /** Montant saisi dans le formulaire. */
    private String montant;

    /** Nouveau plafond saisi dans le formulaire. */
    private String plafond;

    /** Mode paiement différé (checkbox / radio). */
    private boolean paiementDiffere;

    /** Message de retour à afficher dans la JSP. */
    private String message;

    /** Indicateur d'erreur. */
    private boolean error;

    /** Liste des cartes pour affichage. */
    private List<CarteBancaire> cartes;

    // ------------------------------------------------------------------ //
    //  Constructeur                                                       //
    // ------------------------------------------------------------------ //

    public CarteAction() {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque       = (BanqueFacade) ctx.getBean("banqueFacade");
        this.carteManager = (CarteManager)  ctx.getBean("carteManager");
    }

    // ------------------------------------------------------------------ //
    //  Actions                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Affiche la liste des cartes d'un compte.
     * Accessible : gestionnaire uniquement.
     */
    public String listerCartes() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (compte == null) {
            addActionError("Compte non trouvé.");
            return ERROR;
        }
        cartes = carteManager.getCartesParCompte(compte);
        return SUCCESS;
    }

    /**
     * Crée une nouvelle carte pour le compte sélectionné.
     * Accessible : gestionnaire uniquement.
     */
    public String creerCarte() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (compte == null) {
            addActionError("Compte introuvable.");
            return ERROR;
        }
        try {
            double plafondPaiement = parseMontant(plafond, CarteBancaire.PLAFOND_PAIEMENT_DEFAUT);
            double plafondRetrait  = CarteBancaire.PLAFOND_RETRAIT_DEFAUT;

            CarteBancaire nouvelleCarte = carteManager.creerCarte(
                    compte, paiementDiffere, plafondPaiement, plafondRetrait);

            message = "Carte créée avec succès. Numéro : " + nouvelleCarte.getNumeroCarte();
            error = false;
            return SUCCESS;

        } catch (TechnicalException | IllegalArgumentException e) {
            addActionError(e.getMessage());
            error = true;
            return INPUT;
        }
    }

    /**
     * Supprime une carte bancaire.
     * Accessible : gestionnaire uniquement.
     */
    public String supprimerCarte() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            carteManager.supprimerCarte(carte);
            message = "Carte supprimée avec succès.";
            error = false;
            return SUCCESS;
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    /**
     * Bloque une carte bancaire.
     * Accessible : gestionnaire uniquement.
     */
    public String bloquerCarte() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            carteManager.bloquerCarte(carte);
            message = "Carte " + carte.getNumeroCarte() + " bloquée avec succès.";
            error = false;
            return SUCCESS;
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    /**
     * Débloque une carte bancaire.
     * Accessible : gestionnaire uniquement.
     */
    public String debloquerCarte() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            carteManager.debloquerCarte(carte);
            message = "Carte " + carte.getNumeroCarte() + " débloquée avec succès.";
            error = false;
            return SUCCESS;
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    /**
     * Modifie le plafond de paiement mensuel.
     * Accessible : gestionnaire uniquement.
     */
    public String modifierPlafondPaiement() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            double montantPlafond = parseMontantStrict(plafond);
            carteManager.modifierPlafondPaiement(carte, montantPlafond);
            message = String.format("Plafond paiement mis à jour : %.2f€", montantPlafond);
            error = false;
            return SUCCESS;
        } catch (NumberFormatException e) {
            addActionError("Montant invalide : " + plafond);
            error = true;
            return INPUT;
        } catch (TechnicalException | IllegalArgumentException e) {
            addActionError(e.getMessage());
            error = true;
            return INPUT;
        }
    }

    /**
     * Modifie le plafond de retrait mensuel.
     * Accessible : gestionnaire uniquement.
     */
    public String modifierPlafondRetrait() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            double montantPlafond = parseMontantStrict(plafond);
            carteManager.modifierPlafondRetrait(carte, montantPlafond);
            message = String.format("Plafond retrait mis à jour : %.2f€", montantPlafond);
            error = false;
            return SUCCESS;
        } catch (NumberFormatException e) {
            addActionError("Montant invalide : " + plafond);
            error = true;
            return INPUT;
        } catch (TechnicalException | IllegalArgumentException e) {
            addActionError(e.getMessage());
            error = true;
            return INPUT;
        }
    }

    /**
     * Bascule le mode de paiement (immédiat ↔ différé).
     * Accessible : gestionnaire uniquement.
     */
    public String basculerModePaiement() {
        if (!isGestionnaire()) {
            addActionError("Accès réservé aux gestionnaires.");
            return ERROR;
        }
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            carteManager.basculerModePaiement(carte);
            String mode = carte.isPaiementDiffere() ? "différé" : "immédiat";
            message = "Mode de paiement basculé : " + mode;
            error = false;
            return SUCCESS;
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    /**
     * Effectue un paiement par carte.
     * Accessible : client (propriétaire du compte) ou gestionnaire.
     */
    public String effectuerPaiement() {
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            double montantValue = parseMontantStrict(montant);
            carteManager.effectuerPaiement(carte, montantValue);
            message = String.format("Paiement de %.2f€ effectué avec succès.", montantValue);
            error = false;
            return SUCCESS;
        } catch (NumberFormatException e) {
            addActionError("Montant invalide : " + montant);
            error = true;
            return INPUT;
        } catch (CarteBloqueException e) {
            addActionError("Carte bloquée : " + e.getMessage());
            error = true;
            return "CARTE_BLOQUEE";
        } catch (PlafondDepasseException e) {
            addActionError("Plafond dépassé : " + e.getMessage());
            error = true;
            return "PLAFOND_DEPASSE";
        } catch (SoldeInsuffisantException e) {
            addActionError("Solde insuffisant : " + e.getMessage());
            error = true;
            return "SOLDE_INSUFFISANT";
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    /**
     * Effectue un retrait ATM.
     * Accessible : client (propriétaire du compte) ou gestionnaire.
     */
    public String effectuerRetrait() {
        if (carte == null) {
            addActionError("Carte introuvable.");
            return ERROR;
        }
        try {
            double montantValue = parseMontantStrict(montant);
            carteManager.effectuerRetrait(carte, montantValue);
            message = String.format("Retrait de %.2f€ effectué avec succès.", montantValue);
            error = false;
            return SUCCESS;
        } catch (NumberFormatException e) {
            addActionError("Montant invalide : " + montant);
            error = true;
            return INPUT;
        } catch (CarteBloqueException e) {
            addActionError("Carte bloquée : " + e.getMessage());
            error = true;
            return "CARTE_BLOQUEE";
        } catch (PlafondDepasseException e) {
            addActionError("Plafond retrait dépassé : " + e.getMessage());
            error = true;
            return "PLAFOND_DEPASSE";
        } catch (SoldeInsuffisantException e) {
            addActionError("Solde insuffisant : " + e.getMessage());
            error = true;
            return "SOLDE_INSUFFISANT";
        } catch (TechnicalException e) {
            addActionError(e.getMessage());
            error = true;
            return ERROR;
        }
    }

    // ------------------------------------------------------------------ //
    //  Utilitaires                                                        //
    // ------------------------------------------------------------------ //

    private boolean isGestionnaire() {
        Utilisateur user = banque.getConnectedUser();
        return user instanceof Gestionnaire;
    }

    private double parseMontant(String valeur, double defaut) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return defaut;
        }
        return Double.parseDouble(valeur.trim());
    }

    private double parseMontantStrict(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new NumberFormatException("Montant absent.");
        }
        return Double.parseDouble(valeur.trim());
    }

    // ------------------------------------------------------------------ //
    //  Getters / Setters                                                  //
    // ------------------------------------------------------------------ //

    public Compte getCompte()                        { return compte; }
    public void setCompte(Compte compte)             { this.compte = compte; }

    public CarteBancaire getCarte()                  { return carte; }
    public void setCarte(CarteBancaire carte)        { this.carte = carte; }

    public String getMontant()                       { return montant; }
    public void setMontant(String montant)           { this.montant = montant; }

    public String getPlafond()                       { return plafond; }
    public void setPlafond(String plafond)           { this.plafond = plafond; }

    public boolean isPaiementDiffere()               { return paiementDiffere; }
    public void setPaiementDiffere(boolean v)        { this.paiementDiffere = v; }

    public String getMessage()                       { return message; }
    public boolean isError()                         { return error; }

    public List<CarteBancaire> getCartes()           { return cartes; }

    public Utilisateur getConnectedUser()            { return banque.getConnectedUser(); }
}