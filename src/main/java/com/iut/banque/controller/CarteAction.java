package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.CarteManager;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.exceptions.TechnicalException;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur Struts 2 pour la gestion des cartes bancaires côté client.
 *
 * <p>Suit le même pattern que les autres controllers du projet :
 * récupération des beans Spring via le contexte web dans le constructeur,
 * et accès à l'utilisateur connecté via {@link BanqueFacade#getConnectedUser()}.</p>
 *
 * <p>Toutes les actions de cette classe sont accessibles au client connecté
 * (pas de restriction gestionnaire).</p>
 */
public class CarteAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ //
    //  Beans Spring récupérés dans le constructeur                       //
    // ------------------------------------------------------------------ //

    private transient CarteManager carteManager;
    private transient BanqueFacade banque;

    // ------------------------------------------------------------------ //
    //  Paramètres liés par Struts depuis les formulaires                 //
    // ------------------------------------------------------------------ //

    /** Numéro de carte ciblée par l'opération (16 chiffres). */
    private String numeroCarte;

    /** Numéro de compte pour la création d'une carte. */
    private String numeroCompte;

    /** Nouveau plafond de paiement saisi. */
    private double nouveauPlafondPaiement;

    /** Nouveau plafond de retrait saisi. */
    private double nouveauPlafondRetrait;

    /** Mode paiement différé (true = différé, false = immédiat). */
    private boolean paiementDiffere;

    // ------------------------------------------------------------------ //
    //  Données exposées à la vue                                         //
    // ------------------------------------------------------------------ //

    /** Liste de toutes les cartes du client connecté. */
    private List<CarteBancaire> cartes = new ArrayList<>();

    // ------------------------------------------------------------------ //
    //  Constructeur — injection Spring                                   //
    // ------------------------------------------------------------------ //

    public CarteAction() {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(
                        ServletActionContext.getServletContext());
        this.banque       = (BanqueFacade) ctx.getBean("banqueFacade");
        this.carteManager = (CarteManager)  ctx.getBean("carteManager");
    }

    // ------------------------------------------------------------------ //
    //  Helpers privés                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Retourne le client connecté, ou {@code null} si l'utilisateur n'est
     * pas un {@link Client} (ex: gestionnaire ou session expirée).
     */
    private Client getClientConnecte() {
        Utilisateur user = banque.getConnectedUser();
        if (user instanceof Client) {
            return (Client) user;
        }
        return null;
    }

    /**
     * Charge dans {@link #cartes} toutes les cartes de tous les comptes
     * du client actuellement connecté.
     */
    private void chargerCartesClient() {
        Client client = getClientConnecte();
        cartes = new ArrayList<>();
        if (client == null) {
            return;
        }
        for (Compte c : client.getAccounts().values()) {
            List<CarteBancaire> cartesCompte = carteManager.getCartesParCompte(c);
            if (cartesCompte != null) {
                cartes.addAll(cartesCompte);
            }
        }
    }

    /**
     * Trouve la carte correspondant à {@link #numeroCarte} dans la DAO.
     *
     * @return la carte trouvée, ou {@code null}.
     */
    private CarteBancaire trouverCarte() {
        if (numeroCarte == null || numeroCarte.trim().isEmpty()) {
            return null;
        }
        return carteManager.getCarteParNumero(numeroCarte.trim());
    }

    // ================================================================== //
    //  Actions publiques                                                  //
    // ================================================================== //

    /**
     * Affiche la liste de toutes les cartes du client connecté.
     * Point d'entrée de la page {@code GestionCartes.jsp}.
     */
    public String listerCartes() {
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Crée une nouvelle carte pour le compte {@link #numeroCompte}
     * du client connecté.
     */
    public String creerCarte() {
        Client client = getClientConnecte();
        if (client == null) {
            addActionError("Session expirée. Veuillez vous reconnecter.");
            chargerCartesClient();
            return ERROR;
        }

        Compte compte = client.getAccounts().get(numeroCompte);
        if (compte == null) {
            addActionError("Compte introuvable : " + numeroCompte);
            chargerCartesClient();
            return INPUT;
        }

        try {
            double plafondP = (nouveauPlafondPaiement > 0)
                    ? nouveauPlafondPaiement
                    : CarteBancaire.PLAFOND_PAIEMENT_DEFAUT;
            double plafondR = (nouveauPlafondRetrait > 0)
                    ? nouveauPlafondRetrait
                    : CarteBancaire.PLAFOND_RETRAIT_DEFAUT;

            carteManager.creerCarte(compte, paiementDiffere, plafondP, plafondR);
            addActionMessage("Carte créée avec succès pour le compte " + numeroCompte + ".");
        } catch (TechnicalException | IllegalArgumentException e) {
            addActionError("Erreur lors de la création : " + e.getMessage());
        }

        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Bloque la carte identifiée par {@link #numeroCarte}.
     */
    public String bloquerCarte() {
        CarteBancaire carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.bloquerCarte(carte);
            addActionMessage("Carte bloquée avec succès.");
        } catch (TechnicalException e) {
            addActionError("Impossible de bloquer la carte : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Débloque la carte identifiée par {@link #numeroCarte}.
     */
    public String debloquerCarte() {
        CarteBancaire carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.debloquerCarte(carte);
            addActionMessage("Carte débloquée avec succès.");
        } catch (TechnicalException e) {
            addActionError("Impossible de débloquer la carte : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Bascule le mode de paiement (immédiat ↔ différé) de la carte.
     */
    public String basculerMode() {
        CarteBancaire carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.basculerModePaiement(carte);
            String mode = carte.isPaiementDiffere() ? "différé" : "immédiat";
            addActionMessage("Mode de paiement changé en : " + mode + ".");
        } catch (TechnicalException e) {
            addActionError("Impossible de changer le mode : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Modifie le plafond mensuel de paiement de la carte.
     */
    public String modifierPlafondPaiement() {
        CarteBancaire carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.modifierPlafondPaiement(carte, nouveauPlafondPaiement);
            addActionMessage("Plafond de paiement mis à jour : " + nouveauPlafondPaiement + " €.");
        } catch (IllegalArgumentException e) {
            addActionError("Plafond invalide : " + e.getMessage());
        } catch (TechnicalException e) {
            addActionError("Erreur technique : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Modifie le plafond mensuel de retrait de la carte.
     */
    public String modifierPlafondRetrait() {
        CarteBancaire carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.modifierPlafondRetrait(carte, nouveauPlafondRetrait);
            addActionMessage("Plafond de retrait mis à jour : " + nouveauPlafondRetrait + " €.");
        } catch (IllegalArgumentException e) {
            addActionError("Plafond invalide : " + e.getMessage());
        } catch (TechnicalException e) {
            addActionError("Erreur technique : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    // ================================================================== //
    //  Getters / Setters (requis par Struts pour le binding)            //
    // ================================================================== //

    /** Exposé à la JSP via {@code connectedUser} pour afficher le nom. */
    public Utilisateur getConnectedUser() {
        return banque.getConnectedUser();
    }

    public String getNumeroCarte()                         { return numeroCarte; }
    public void   setNumeroCarte(String numeroCarte)       { this.numeroCarte = numeroCarte; }

    public String getNumeroCompte()                        { return numeroCompte; }
    public void   setNumeroCompte(String numeroCompte)     { this.numeroCompte = numeroCompte; }

    public double getNouveauPlafondPaiement()              { return nouveauPlafondPaiement; }
    public void   setNouveauPlafondPaiement(double v)      { this.nouveauPlafondPaiement = v; }

    public double getNouveauPlafondRetrait()               { return nouveauPlafondRetrait; }
    public void   setNouveauPlafondRetrait(double v)       { this.nouveauPlafondRetrait = v; }

    public boolean isPaiementDiffere()                     { return paiementDiffere; }
    public void    setPaiementDiffere(boolean v)           { this.paiementDiffere = v; }

    public List<CarteBancaire> getCartes()                 { return cartes; }
}