package com.iut.banque.controller;

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

    /**
     * Constructeur
     */
    public ChangePasswordAction() {
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque = (BanqueFacade) context.getBean("banqueFacade");
    }

    /**
     * Affiche la page de changement du mot de passe
     */
    public String showPage() {

        Utilisateur connectedUser = banque.getConnectedUser();

        if (connectedUser == null) {
            addActionError("Vous devez être connecté pour accéder à cette page.");
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * Traite la modification du mot de passe
     */
    @Override
    public String execute() {

        Utilisateur connectedUser = banque.getConnectedUser();

        if (connectedUser == null) {
            addActionError("Vous devez être connecté pour modifier votre mot de passe.");
            return ERROR;
        }

        // Validation des champs
        if (isEmpty(oldPassword)) {
            addActionError("L'ancien mot de passe est obligatoire.");
            return INPUT;
        }

        if (isEmpty(newPassword)) {
            addActionError("Le nouveau mot de passe est obligatoire.");
            return INPUT;
        }

        if (isEmpty(confirmPassword)) {
            addActionError("La confirmation du mot de passe est obligatoire.");
            return INPUT;
        }

        // Vérification correspondance
        if (!newPassword.equals(confirmPassword)) {
            addActionError("Les nouveaux mots de passe ne correspondent pas.");
            return INPUT;
        }

        // Vérification ancien mot de passe
        if (!PasswordHasher.verify(oldPassword, connectedUser.getUserPwd())) {
            addActionError("L'ancien mot de passe est incorrect.");
            return INPUT;
        }

        // Vérification nouveau différent de l'ancien
        if (oldPassword.equals(newPassword)) {
            addActionError("Le nouveau mot de passe doit être différent de l'ancien.");
            return INPUT;
        }

        // Longueur minimale
        if (newPassword.length() < 6) {
            addActionError("Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return INPUT;
        }

        try {
            // Mise à jour du mot de passe
            connectedUser.setUserPwd(newPassword);
            banque.updateUser(connectedUser);

            addActionMessage("Votre mot de passe a été modifié avec succès.");

            // Nettoyage des champs
            oldPassword = null;
            newPassword = null;
            confirmPassword = null;

            return SUCCESS;

        } catch (Exception e) {
            // Pas de printStackTrace() → conforme Sonar
            addActionError("Une erreur est survenue lors de la modification du mot de passe.");
            return ERROR;
        }
    }

    /**
     * Getter utilisateur connecté
     */
    public Utilisateur getConnectedUser() {
        return banque.getConnectedUser();
    }

    // Helper
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters & Setters
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
