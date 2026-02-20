package com.iut.banque.controller;

import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.CarteManager;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.CarteBancaire.CarteBloqueException;
import com.iut.banque.modele.CarteBancaire.PlafondDepasseException;
import com.iut.banque.modele.CarteBancaire.SoldeInsuffisantException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur Struts 2 pour la gestion des cartes bancaires côté client.
 *
 * <p>Injecté par Spring via {@code @Autowired} sur le setter —
 * pattern identique aux autres controllers du projet.</p>
 */
public class CarteAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------ //
    //  Codes de résultat spécifiques métier                              //
    // ------------------------------------------------------------------ //
    public static final String CARTE_BLOQUEE        = "CARTE_BLOQUEE";
    public static final String PLAFOND_DEPASSE      = "PLAFOND_DEPASSE";
    public static final String SOLDE_INSUFFISANT    = "SOLDE_INSUFFISANT";

    // ------------------------------------------------------------------ //
    //  Injection Spring                                                  //
    // ------------------------------------------------------------------ //

    private CarteManager carteManager;
    private BanqueFacade banqueFacade;

    @Autowired
    public void setCarteManager(CarteManager carteManager) {
        this.carteManager = carteManager;
    }

    @Autowired
    public void setBanqueFacade(BanqueFacade banqueFacade) {
        this.banqueFacade = banqueFacade;
    }

    // ------------------------------------------------------------------ //
    //  Session Struts                                                    //
    // ------------------------------------------------------------------ //

    private Map<String, Object> session;

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    // ------------------------------------------------------------------ //
    //  Paramètres d'entrée (liés par Struts depuis les formulaires)     //
    // ------------------------------------------------------------------ //

    /** Numéro de carte (16 chiffres). */
    private String numeroCarte;

    /** Numéro de compte pour la création d'une carte. */
    private String numeroCompte;

    /** Montant saisi (paiement ou retrait). */
    private double montant;

    /** Nouveau plafond de paiement. */
    private double nouveauPlafondPaiement;

    /** Nouveau plafond de retrait. */
    private double nouveauPlafondRetrait;

    /** true = mode différé demandé lors de la création. */
    private boolean paiementDiffere;

    // ------------------------------------------------------------------ //
    //  Données exposées à la vue                                        //
    // ------------------------------------------------------------------ //

    /** Liste des cartes à afficher dans la vue. */
    private List<CarteBancaire> cartes = new ArrayList<>();

    /** Carte ciblée par l'opération courante. */
    private CarteBancaire carte;

    /** Message de confirmation ou d'erreur affiché dans la JSP. */
    private String messageRetour;

    /** Indicateur d'erreur pour la vue. */
    private boolean erreur;

    // ================================================================== //
    //  Helpers privés                                                    //
    // ================================================================== //

    /**
     * Retourne le client actuellement connecté depuis la session Struts.
     *
     * @return le {@link Client} connecté, ou {@code null} si absent.
     */
    private Client getClientConnecte() {
        Object user = session.get("connectedUser");
        if (user instanceof Client) {
            return (Client) user;
        }
        return null;
    }

    /**
     * Charge les cartes du client connecté dans {@link #cartes}.
     * Parcourt tous ses comptes et agrège leurs cartes via le manager.
     */
    private void chargerCartesClient() {
        Client client = getClientConnecte();
        if (client == null) {
            return;
        }
        cartes = new ArrayList<>();
        for (Compte compte : client.getAccounts().values()) {
            List<CarteBancaire> cartesCompte = carteManager.getCartesParCompte(compte);
            if (cartesCompte != null) {
                cartes.addAll(cartesCompte);
            }
        }
    }

    /**
     * Recherche et retourne la carte identifiée par {@link #numeroCarte}.
     *
     * @return la carte trouvée, ou {@code null}.
     */
    private CarteBancaire trouverCarte() {
        return carteManager.getCarteParNumero(numeroCarte);
    }

    // ================================================================== //
    //  Actions publiques                                                 //
    // ================================================================== //

    /**
     * Affiche la liste des cartes du client connecté.
     * Accessible via {@code listerCartes.action}.
     */
    public String listerCartes() {
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Crée une nouvelle carte pour le compte {@link #numeroCompte} du client.
     * Plafonds par défaut si non renseignés.
     */
    public String creerCarte() {
        Client client = getClientConnecte();
        if (client == null) {
            addActionError("Session expirée. Veuillez vous reconnecter.");
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
        } catch (Exception e) {
            addActionError("Erreur lors de la création de la carte : " + e.getMessage());
        }

        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Bloque la carte identifiée par {@link #numeroCarte}.
     */
    public String bloquerCarte() {
        carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.bloquerCarte(carte);
            addActionMessage("Carte " + masquer(numeroCarte) + " bloquée.");
        } catch (Exception e) {
            addActionError("Impossible de bloquer la carte : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Débloque la carte identifiée par {@link #numeroCarte}.
     */
    public String debloquerCarte() {
        carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.debloquerCarte(carte);
            addActionMessage("Carte " + masquer(numeroCarte) + " débloquée.");
        } catch (Exception e) {
            addActionError("Impossible de débloquer la carte : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Bascule le mode de paiement (immédiat ↔ différé).
     */
    public String basculerMode() {
        carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.basculerModePaiement(carte);
            String nouveauMode = carte.isPaiementDiffere() ? "différé" : "immédiat";
            addActionMessage("Mode de paiement changé en : " + nouveauMode + ".");
        } catch (Exception e) {
            addActionError("Impossible de changer le mode : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Modifie le plafond de paiement mensuel.
     */
    public String modifierPlafondPaiement() {
        carte = trouverCarte();
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
        } catch (Exception e) {
            addActionError("Erreur technique : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Modifie le plafond de retrait mensuel.
     */
    public String modifierPlafondRetrait() {
        carte = trouverCarte();
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
        } catch (Exception e) {
            addActionError("Erreur technique : " + e.getMessage());
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Effectue un paiement par carte.
     */
    public String effectuerPaiement() {
        carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.effectuerPaiement(carte, montant);
            addActionMessage("Paiement de " + montant + " € effectué avec la carte "
                    + masquer(numeroCarte) + ".");
        } catch (CarteBloqueException e) {
            addActionError("La carte est bloquée.");
            chargerCartesClient();
            return CARTE_BLOQUEE;
        } catch (PlafondDepasseException e) {
            addActionError("Plafond mensuel dépassé.");
            chargerCartesClient();
            return PLAFOND_DEPASSE;
        } catch (SoldeInsuffisantException e) {
            addActionError("Solde insuffisant.");
            chargerCartesClient();
            return SOLDE_INSUFFISANT;
        } catch (Exception e) {
            addActionError("Erreur technique : " + e.getMessage());
            chargerCartesClient();
            return ERROR;
        }
        chargerCartesClient();
        return SUCCESS;
    }

    /**
     * Effectue un retrait ATM par carte.
     */
    public String effectuerRetrait() {
        carte = trouverCarte();
        if (carte == null) {
            addActionError("Carte introuvable.");
            chargerCartesClient();
            return ERROR;
        }
        try {
            carteManager.effectuerRetrait(carte, montant);
            addActionMessage("Retrait de " + montant + " € effectué avec la carte "
                    + masquer(numeroCarte) + ".");
        } catch (CarteBloqueException e) {
            addActionError("La carte est bloquée.");
            chargerCartesClient();
            return CARTE_BLOQUEE;
        } catch (PlafondDepasseException e) {
            addActionError("Plafond de retrait mensuel dépassé.");
            chargerCartesClient();
            return PLAFOND_DEPASSE;
        } catch (SoldeInsuffisantException e) {
            addActionError("Solde insuffisant.");
            chargerCartesClient();
            return SOLDE_INSUFFISANT;
        } catch (Exception e) {
            addActionError("Erreur technique : " + e.getMessage());
            chargerCartesClient();
            return ERROR;
        }
        chargerCartesClient();
        return SUCCESS;
    }

    // ================================================================== //
    //  Utilitaires                                                       //
    // ================================================================== //

    /**
     * Masque les 8 chiffres centraux d'un numéro de carte.
     * Ex : {@code 1234567890123456} → {@code 1234 **** **** 3456}
     */
    private String masquer(String num) {
        if (num == null || num.length() < 16) return num;
        return num.substring(0, 4) + " **** **** " + num.substring(12);
    }

    // ================================================================== //
    //  Getters / Setters (nécessaires pour Struts)                      //
    // ================================================================== //

    public String getNumeroCarte()                    { return numeroCarte; }
    public void   setNumeroCarte(String numeroCarte)  { this.numeroCarte = numeroCarte; }

    public String getNumeroCompte()                   { return numeroCompte; }
    public void   setNumeroCompte(String numeroCompte){ this.numeroCompte = numeroCompte; }

    public double getMontant()                        { return montant; }
    public void   setMontant(double montant)          { this.montant = montant; }

    public double getNouveauPlafondPaiement()         { return nouveauPlafondPaiement; }
    public void   setNouveauPlafondPaiement(double v) { this.nouveauPlafondPaiement = v; }

    public double getNouveauPlafondRetrait()          { return nouveauPlafondRetrait; }
    public void   setNouveauPlafondRetrait(double v)  { this.nouveauPlafondRetrait = v; }

    public boolean isPaiementDiffere()                { return paiementDiffere; }
    public void    setPaiementDiffere(boolean v)      { this.paiementDiffere = v; }

    public List<CarteBancaire> getCartes()            { return cartes; }
    public CarteBancaire       getCarte()             { return carte; }

    public String  getMessageRetour()                 { return messageRetour; }
    public boolean isErreur()                         { return erreur; }
}