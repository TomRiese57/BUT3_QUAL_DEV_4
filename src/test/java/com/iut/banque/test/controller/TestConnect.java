package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.controller.Connect;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TestConnect {

    private Connect action;
    private BanqueFacade mockFacade;
    private Client client;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        // 1. Création de l'instance sans constructeur (comme avant)
        action = new ObjenesisStd().newInstance(Connect.class);

        // 2. Injection du Mock pour BanqueFacade (comme avant)
        mockFacade = mock(BanqueFacade.class);
        Field f = Connect.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        // 3. Injection manuelle du Logger car le constructeur n'est pas appelé
        Field l = Connect.class.getDeclaredField("logger");
        l.setAccessible(true);
        // On injecte un vrai logger pour que les appels logger.info() fonctionnent
        l.set(action, java.util.logging.Logger.getLogger(Connect.class.getName()));

        client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
    }

    @Test
    public void testLoginSuccess() {
        action.setUserCde("j.doe1");
        action.setUserPwd("password");
        when(mockFacade.tryLogin("j.doe1", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);
        assertEquals("SUCCESS", action.login());
    }

    @Test
    public void testLoginSuccessManager() {
        action.setUserCde("admin");
        action.setUserPwd("password");
        when(mockFacade.tryLogin("admin", "password")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);
        assertEquals("SUCCESSMANAGER", action.login());
    }

    @Test
    public void testLoginFailed() {
        action.setUserCde("j.doe1");
        action.setUserPwd("wrongpwd");
        when(mockFacade.tryLogin("j.doe1", "wrongpwd")).thenReturn(LoginConstants.LOGIN_FAILED);
        assertEquals("ERROR", action.login());
    }

    @Test
    public void testLoginErrorConstant() {
        action.setUserCde("j.doe1");
        action.setUserPwd("password");
        when(mockFacade.tryLogin("j.doe1", "password")).thenReturn(LoginConstants.ERROR);
        assertEquals("ERROR", action.login());
    }

    @Test
    public void testLoginNullUserCde() {
        action.setUserCde(null);
        action.setUserPwd("password");
        assertEquals("ERROR", action.login());
    }

    @Test
    public void testLoginNullPassword() {
        action.setUserCde("j.doe1");
        action.setUserPwd(null);
        assertEquals("ERROR", action.login());
    }

    @Test
    public void testLoginException() {
        action.setUserCde("j.doe1");
        action.setUserPwd("password");
        when(mockFacade.tryLogin(anyString(), anyString())).thenThrow(new RuntimeException("err"));
        assertEquals("ERROR", action.login());
    }

    @Test
    public void testLoginTrimsUserCde() {
        action.setUserCde("  j.doe1  ");
        action.setUserPwd("password");
        when(mockFacade.tryLogin("j.doe1", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);
        assertEquals("SUCCESS", action.login());
    }

    @Test
    public void testLogout() {
        assertEquals("SUCCESS", action.logout());
        verify(mockFacade).logout();
    }

    @Test
    public void testGetConnectedUser() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        assertEquals(client, action.getConnectedUser());
    }

    @Test
    public void testGetSetUserCde() {
        action.setUserCde("test");
        assertEquals("test", action.getUserCde());
    }

    @Test
    public void testGetSetUserPwd() {
        action.setUserPwd("secret");
        assertEquals("secret", action.getUserPwd());
    }

    @Test
    public void testGetAccounts() {
        Map<String, Compte> accounts = new HashMap<>();
        Client mockClient = mock(Client.class);
        when(mockClient.getUserId()).thenReturn("j.doe1");
        when(mockClient.getAccounts()).thenReturn(accounts);
        when(mockFacade.getConnectedUser()).thenReturn(mockClient);

        Map<String, Compte> result = action.getAccounts();

        verify(mockFacade).reloadClient("j.doe1");
        assertEquals(accounts, result);
    }
}