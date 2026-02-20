package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ResetPasswordAction;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;

import org.objenesis.ObjenesisStd;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAwareSupport;

import java.lang.reflect.Field;

public class TestResetPasswordAction {

    private ResetPasswordAction action;
    private BanqueFacade mockFacade;
    private Client testUser;

    @Before
    public void setUp() throws Exception {
        // 1. Création de l'instance sans appeler le constructeur (pas de servlet context)
        action = new ObjenesisStd().newInstance(ResetPasswordAction.class);

        // 2. Injection de la façade mockée
        mockFacade = mock(BanqueFacade.class);
        Field f = ResetPasswordAction.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        // 3. Injection du ValidationAwareSupport (Struts)
        Field vaField = ActionSupport.class.getDeclaredField("validationAware");
        vaField.setAccessible(true);
        vaField.set(action, new ValidationAwareSupport());

        // 4. Initialisation d'un utilisateur test
        testUser = new Client();
        testUser.setUserPwd("existingpassword");

        // Par défaut, getUserById retourne l'utilisateur test
        when(mockFacade.getUserById("testuser")).thenReturn(testUser);
        when(mockFacade.getUserById("unknownuser")).thenReturn(null);
    }

    @Test
    public void testShowPage() {
        assertEquals("success", action.showPage());
    }

    @Test
    public void testExecuteUserCdeNull() {
        action.setUserCde(null);
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteUserCdeEmpty() {
        action.setUserCde("   ");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteNewPasswordEmpty() {
        action.setUserCde("testuser");
        action.setNewPassword("");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteConfirmPasswordEmpty() {
        action.setUserCde("testuser");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecutePasswordsDoNotMatch() {
        action.setUserCde("testuser");
        action.setNewPassword("newpwd1");
        action.setConfirmPassword("newpwd2");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecutePasswordTooShort() {
        action.setUserCde("testuser");
        action.setNewPassword("abc");
        action.setConfirmPassword("abc");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteUserNotFound() {
        action.setUserCde("unknownuser");
        action.setNewPassword("newpwd123");
        action.setConfirmPassword("newpwd123");
        assertEquals("input", action.execute());
    }

    @Test
    public void testExecuteSuccess() throws Exception {
        action.setUserCde("testuser");
        action.setNewPassword("newpassword123");
        action.setConfirmPassword("newpassword123");

        assertEquals("success", action.execute());
        verify(mockFacade).updateUser(testUser);

        // Les champs doivent être nettoyés après succès
        assertNull(action.getUserCde());
        assertNull(action.getNewPassword());
        assertNull(action.getConfirmPassword());
    }

    @Test
    public void testExecuteException() throws Exception {
        doThrow(new RuntimeException("db error")).when(mockFacade).updateUser(any());
        action.setUserCde("testuser");
        action.setNewPassword("newpassword123");
        action.setConfirmPassword("newpassword123");

        assertEquals("error", action.execute());
    }

    @Test
    public void testGettersSetters() {
        action.setUserCde("user1");
        assertEquals("user1", action.getUserCde());

        action.setNewPassword("pwd1");
        assertEquals("pwd1", action.getNewPassword());

        action.setConfirmPassword("pwd2");
        assertEquals("pwd2", action.getConfirmPassword());
    }
}
