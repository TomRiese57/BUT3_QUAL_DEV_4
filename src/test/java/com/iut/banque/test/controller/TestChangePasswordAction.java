package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ChangePasswordAction;
import com.iut.banque.cryptage.PasswordHasher;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;

import org.objenesis.ObjenesisStd;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAwareSupport;

import java.lang.reflect.Field;

public class TestChangePasswordAction {

    private ChangePasswordAction action;
    private BanqueFacade mockFacade;
    private Client clientWithHashedPwd;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        // 1. Création de l'instance
        action = new ObjenesisStd().newInstance(ChangePasswordAction.class);

        // 2. Injection de la façade
        mockFacade = mock(BanqueFacade.class);
        Field f = ChangePasswordAction.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        // 3. Injection du ValidationAwareSupport (Struts)
        Field vaField = ActionSupport.class.getDeclaredField("validationAware");
        vaField.setAccessible(true);
        vaField.set(action, new ValidationAwareSupport());

        // 4. Initialisation du client mocké
        clientWithHashedPwd = new Client();
        // On définit le mot de passe hashé pour que "oldpassword" soit validé par le PasswordHasher
        clientWithHashedPwd.setUserPwd("oldpassword");

        // On configure le mock pour renvoyer ce client initialisé
        when(mockFacade.getConnectedUser()).thenReturn(clientWithHashedPwd);
    }

    @Test
    public void testShowPageNotConnected() {
        when(mockFacade.getConnectedUser()).thenReturn(null);
        assertEquals("error", action.showPage());
    }

    @Test
    public void testShowPageConnected() {
        // Le when est déjà fait dans le setUp, mais on peut le refaire pour la clarté
        when(mockFacade.getConnectedUser()).thenReturn(clientWithHashedPwd);
        assertEquals("success", action.showPage());
    }

    @Test
    public void testExecuteNotConnected() {
        when(mockFacade.getConnectedUser()).thenReturn(null);
        assertEquals("error", action.execute());
    }

    @Test
    public void testExecuteOldPasswordNull() {
        action.setOldPassword(null);
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteOldPasswordEmpty() {
        action.setOldPassword("   ");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteNewPasswordEmpty() {
        action.setOldPassword("oldpassword");
        action.setNewPassword("");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteConfirmPasswordEmpty() {
        action.setOldPassword("oldpassword");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecutePasswordsDoNotMatch() {
        action.setOldPassword("oldpassword");
        action.setNewPassword("newpwd1");
        action.setConfirmPassword("newpwd2");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteOldPasswordIncorrect() {
        action.setOldPassword("wrongpassword");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteNewSameAsOld() {
        action.setOldPassword("oldpassword");
        action.setNewPassword("oldpassword");
        action.setConfirmPassword("oldpassword");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteNewPasswordTooShort() {
        action.setOldPassword("oldpassword");
        action.setNewPassword("abc");
        action.setConfirmPassword("abc");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteSuccess() throws Exception {
        action.setOldPassword("oldpassword");
        action.setNewPassword("newpassword123");
        action.setConfirmPassword("newpassword123");

        assertEquals("success", action.execute());
        verify(mockFacade).updateUser(clientWithHashedPwd);

        assertNull(action.getOldPassword());
        assertNull(action.getNewPassword());
        assertNull(action.getConfirmPassword());
    }

    @Test
    public void testExecuteException() throws Exception {
        doThrow(new RuntimeException("db error")).when(mockFacade).updateUser(any());
        action.setOldPassword("oldpassword");
        action.setNewPassword("newpassword123");
        action.setConfirmPassword("newpassword123");

        assertEquals("error", action.execute());
    }

    @Test
    public void testGetConnectedUser() {
        assertEquals(clientWithHashedPwd, action.getConnectedUser());
    }

    @Test
    public void testGettersSetters() {
        action.setOldPassword("old");
        assertEquals("old", action.getOldPassword());

        action.setNewPassword("new");
        assertEquals("new", action.getNewPassword());

        action.setConfirmPassword("confirm");
        assertEquals("confirm", action.getConfirmPassword());
    }
}