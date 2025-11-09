package com.iut.banque.controller;

import java.util.logging.Logger;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.cryptage.PasswordHasher;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;

public class ChangePasswordAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private transient BanqueFacade banque;
    private transient Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Constructeur de la classe ChangePasswordAction
     */
    public ChangePasswordAction() {
        logger.info("In Constructor from ChangePasswordAction class");
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque = (BanqueFacade) context.getBean("banqueFacade");
    }

    /**
     * Affiche la page de changement de mot de passe
     */
    public String showPage() {
        logger.info("showPage() appelée");

        Utilisateur connectedUser = banque.getConnectedUser();

        if (connectedUser == null) {
            logger.warning("Utilisateur non connecté");
            addActionError("Vous devez être connecté pour accéder à cette page.");
            return ERROR;
        }

        logger.info("Utilisateur trouvé: " + connectedUser.getUserId());
        return SUCCESS;
    }

    /**
     * Traite la modification du mot de passe
     */
    public String execute() {
        logger.info("execute() appelée - Modification du mot de passe");

        Utilisateur connectedUser = banque.getConnectedUser();

        if (connectedUser == null) {
            logger.warning("Utilisateur non connecté");
            addActionError("Vous devez être connecté pour modifier votre mot de passe.");
            return ERROR;
        }

        // Validation des champs
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            addActionError("L'ancien mot de passe est obligatoire.");
            return INPUT;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            addActionError("Le nouveau mot de passe est obligatoire.");
            return INPUT;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            addActionError("La confirmation du mot de passe est obligatoire.");
            return INPUT;
        }

        // Vérification que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            addActionError("Les nouveaux mots de passe ne correspondent pas.");
            return INPUT;
        }

        // Vérification de l'ancien mot de passe
        if (!PasswordHasher.verify(oldPassword, connectedUser.getUserPwd())) {
            addActionError("L'ancien mot de passe est incorrect.");
            return INPUT;
        }

        // Vérification que le nouveau mot de passe est différent de l'ancien
        if (oldPassword.equals(newPassword)) {
            addActionError("Le nouveau mot de passe doit être différent de l'ancien.");
            return INPUT;
        }

        // Vérification de la longueur du nouveau mot de passe
        if (newPassword.length() < 6) {
            addActionError("Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return INPUT;
        }

        try {
            // Modification du mot de passe
            // Note: setUserPwd() hache automatiquement le mot de passe
            connectedUser.setUserPwd(newPassword);

            // Mise à jour dans la base de données
            banque.updateUser(connectedUser);

            logger.info("Mot de passe modifié avec succès pour l'utilisateur: " + connectedUser.getUserId());

            addActionMessage("Votre mot de passe a été modifié avec succès.");

            // Réinitialisation des champs pour sécurité
            this.oldPassword = null;
            this.newPassword = null;
            this.confirmPassword = null;

            return SUCCESS;

        } catch (Exception e) {
            logger.severe("Erreur lors de la modification du mot de passe: " + e.getMessage());
            e.printStackTrace();
            addActionError("Une erreur s'est produite lors de la modification du mot de passe : " + e.getMessage());
            return ERROR;
        }
    }

    /**
     * Getter de l'utilisateur connecté (utilisé pour afficher les infos dans la JSP)
     */
    public Utilisateur getConnectedUser() {
        return banque.getConnectedUser();
    }

    // Getters et Setters pour les champs du formulaire
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}