package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestLoginManager {

    private LoginManager loginManager;
    private IDao mockDao;

    @Before
    public void setUp() {
        loginManager = new LoginManager();
        mockDao = mock(IDao.class);
        loginManager.setDao(mockDao);
    }

    @Test
    public void testTryLoginAsClient() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "hashedpwd", "123456789");
        when(mockDao.isUserAllowed("j.doe1", "password")).thenReturn(true);
        when(mockDao.getUserById("j.doe1")).thenReturn(client);

        int result = loginManager.tryLogin("j.doe1", "password");

        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        assertEquals(client, loginManager.getConnectedUser());
    }

    @Test
    public void testTryLoginAsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("Admin", "Admin", "addr", true, "admin", "hashedpwd");
        when(mockDao.isUserAllowed("admin", "password")).thenReturn(true);
        when(mockDao.getUserById("admin")).thenReturn(manager);

        int result = loginManager.tryLogin("admin", "password");

        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
    }

    @Test
    public void testTryLoginFailed() {
        when(mockDao.isUserAllowed("j.doe1", "wrongpwd")).thenReturn(false);

        int result = loginManager.tryLogin("j.doe1", "wrongpwd");

        assertEquals(LoginConstants.LOGIN_FAILED, result);
        assertNull(loginManager.getConnectedUser());
    }

    @Test
    public void testGetConnectedUserInitiallyNull() {
        assertNull(loginManager.getConnectedUser());
    }

    @Test
    public void testSetCurrentUser() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "hashedpwd", "123456789");
        loginManager.setCurrentUser(client);
        assertEquals(client, loginManager.getConnectedUser());
    }

    @Test
    public void testLogout() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "hashedpwd", "123456789");
        loginManager.setCurrentUser(client);

        loginManager.logout();

        assertNull(loginManager.getConnectedUser());
        verify(mockDao).disconnect();
    }
}