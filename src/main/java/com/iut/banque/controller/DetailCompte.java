package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;
import com.opensymphony.xwork2.ActionSupport;

public class DetailCompte extends ActionSupport {

	private static final long serialVersionUID = 1L;
	protected transient BanqueFacade banque;
	private String montant;
	private String error;
	protected transient Compte compte;

    private static final String SUCCESS_RESULT = "SUCCESS";
    private static final String ERROR_RESULT = "ERROR";
    private static final String NEGATIVE_AMOUNT = "NEGATIVEAMOUNT";
    private static final String NOT_ENOUGH_FUNDS = "NOTENOUGHFUNDS";

    // Codes d'erreur
    private static final String ERROR_TECHNICAL = "TECHNICAL";
    private static final String ERROR_BUSINESS = "BUSINESS";
    private static final String ERROR_NEGATIVE_AMOUNT = "NEGATIVEAMOUNT";
    private static final String ERROR_NEGATIVE_OVERDRAFT = "NEGATIVEOVERDRAFT";
    private static final String ERROR_INCOMPATIBLE_OVERDRAFT = "INCOMPATIBLEOVERDRAFT";

    // Messages correspondants
    private static final String MSG_TECHNICAL = "Erreur interne. Verifiez votre saisie puis réessayer. Contactez votre conseiller si le problème persiste.";
    private static final String MSG_BUSINESS = "Fonds insuffisants.";
    private static final String MSG_NEGATIVE_AMOUNT = "Veuillez rentrer un montant positif.";
    private static final String MSG_NEGATIVE_OVERDRAFT = "Veuillez rentrer un découvert positif.";
    private static final String MSG_INCOMPATIBLE_OVERDRAFT = "Le nouveau découvert est incompatible avec le solde actuel.";

    /**
	 * Constructeur du controlleur DetailCompte
	 * 
	 * Récupère l'ApplicationContext
	 * 
	 * @return un nouvel objet DetailCompte avec une BanqueFacade provenant de
	 *         la factory
	 */
	public DetailCompte() {
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	/**
	 * Retourne sous forme de string le message d'erreur basé sur le champ
	 * "error" actuellement défini dans la classe
	 * 
	 * @return String, le string avec le détail du message d'erreur
	 */
    public String getError() {
        switch (error) {
            case ERROR_TECHNICAL:
                return MSG_TECHNICAL;
            case ERROR_BUSINESS:
                return MSG_BUSINESS;
            case ERROR_NEGATIVE_AMOUNT:
                return MSG_NEGATIVE_AMOUNT;
            case ERROR_NEGATIVE_OVERDRAFT:
                return MSG_NEGATIVE_OVERDRAFT;
            case ERROR_INCOMPATIBLE_OVERDRAFT:
                return MSG_INCOMPATIBLE_OVERDRAFT;
            default:
                return "";
        }
    }


    /**
	 * Permet de définir le champ error de la classe avec le string passé en
	 * paramètre. Si jamais on passe un objet null, on adapte le string
	 * automatiquement en "EMPTY"
	 * 
	 * @param error
	 *            : Un String correspondant à celui qu'on veut définir dans le
	 *            champ error
	 */
	public void setError(String error) {
		if (error == null) {
			this.error = "EMPTY";
		} else {
			this.error = error;
		}
	}

	/**
	 * Getter du champ montant
	 * 
	 * @return String : valeur du champ montant
	 */
	public String getMontant() {
		return montant;
	}

	/**
	 * Setter du champ montant
	 * 
	 * @param montant
	 *            un String correspondant au montant à définir
	 */
	public void setMontant(String montant) {
		this.montant = montant;
	}

	/**
	 * Getter du compte actuellement sélectionné. Récupère la liste des comptes
	 * de l'utilisateur connecté dans un premier temps. Récupère ensuite dans la
	 * HashMap la clé qui comporte le string provenant de idCompte. Renvoie donc
	 * null si le compte n'appartient pas à l'utilisateur
	 * 
	 * @return Compte : l'objet compte après s'être assuré qu'il appartient à
	 *         l'utilisateur
	 */
    public Compte getCompte() {
        Utilisateur user = banque.getConnectedUser();

        if (user instanceof Client) {
            Client client = (Client) user;
            // On vérifie que le compte appartient bien au client
            if (client.getAccounts().containsKey(compte.getNumeroCompte())) {
                return compte;
            } else {
                return null; // le client n'a pas ce compte
            }
        }
        else if (user instanceof Gestionnaire) {
            // Le gestionnaire peut voir tous les comptes
            return compte;
        }

        return null; // aucun utilisateur connecté
    }



    public void setCompte(Compte compte) {
		this.compte = compte;
	}

	/**
	 * Méthode débit pour débter le compte considéré en cours
	 * 
	 * @return String : Message correspondant à l'état du débit (si il a réussi
	 *         ou pas)
	 */
    public String debit() {
        Compte compte1 = getCompte();
        try {
            banque.debiter(compte1, Double.parseDouble(montant.trim()));
            return SUCCESS_RESULT;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ERROR_RESULT;
        } catch (InsufficientFundsException ife) {
            ife.printStackTrace();
            return NOT_ENOUGH_FUNDS;
        } catch (IllegalFormatException e) {
            e.printStackTrace();
            return NEGATIVE_AMOUNT;
        }
    }

	/**
	 * Méthode crédit pour créditer le compte considéré en cours
	 * 
	 * @return String : Message correspondant à l'état du crédit (si il a réussi
	 *         ou pas)
	 */
    public String credit() {
        Compte compte2 = getCompte();
        try {
            banque.crediter(compte2, Double.parseDouble(montant.trim()));
            return SUCCESS_RESULT;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return ERROR_RESULT;
        } catch (IllegalFormatException e) {
            e.printStackTrace();
            return NEGATIVE_AMOUNT;
        }
    }
}
