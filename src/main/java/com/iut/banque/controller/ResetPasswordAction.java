package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;

public class ResetPasswordAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String userCde;
    private String newPassword;
    private String confirmPassword;
    private transient BanqueFacade banque;

    /**
     * Constructeur
     */
    public ResetPasswordAction() {
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque = (BanqueFacade) context.getBean("banqueFacade");
    }

    /**
     * Affiche la page de réinitialisation du mot de passe
     */
    public String showPage() {
        return SUCCESS;
    }

    /**
     * Traite la réinitialisation du mot de passe
     */
    @Override
    public String execute() {

        // Validation des champs
        if (isEmpty(userCde)) {
            addActionError("Le code utilisateur est obligatoire.");
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
            addActionError("Les mots de passe ne correspondent pas.");
            return INPUT;
        }

        // Longueur minimale
        if (newPassword.length() < 6) {
            addActionError("Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return INPUT;
        }

        // Recherche de l'utilisateur
        Utilisateur user = banque.getUserById(userCde.trim());
        if (user == null) {
            addActionError("Aucun utilisateur trouvé avec ce code.");
            return INPUT;
        }

        try {
            // Mise à jour du mot de passe
            user.setUserPwd(newPassword);
            banque.updateUser(user);

            addActionMessage("Le mot de passe a été réinitialisé avec succès. Vous pouvez maintenant vous connecter.");

            // Nettoyage des champs
            userCde = null;
            newPassword = null;
            confirmPassword = null;

            return SUCCESS;

        } catch (Exception e) {
            addActionError("Une erreur est survenue lors de la réinitialisation du mot de passe.");
            return ERROR;
        }
    }

    // Helper
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters & Setters
    public String getUserCde() {
        return userCde;
    }

    public void setUserCde(String userCde) {
        this.userCde = userCde;
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
