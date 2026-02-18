package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.modele.*;

import java.util.HashMap;
import java.util.Map;

public class TestBanqueFacade {

    private BanqueFacade facade;
    private BanqueManager mockBanqueManager;
    private LoginManager mockLoginManager;

    @Before
    public void setUp() {
        mockBanqueManager = mock(BanqueManager.class);
        mockLoginManager = mock(LoginManager.class);
        facade = new BanqueFacade(mockLoginManager, mockBanqueManager);
    }

    @Test
    public void testGetConnectedUser() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);

        Utilisateur user = facade.getConnectedUser();
        assertEquals(client, user);
    }

    @Test
    public void testTryLoginUser() throws Exception {
        when(mockLoginManager.tryLogin("j.doe1", "pwd")).thenReturn(LoginConstants.USER_IS_CONNECTED);

        int result = facade.tryLogin("j.doe1", "pwd");
        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        verify(mockBanqueManager, never()).loadAllClients();
    }

    @Test
    public void testTryLoginManagerLoadsClients() throws Exception {
        when(mockLoginManager.tryLogin("admin", "pwd")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);

        int result = facade.tryLogin("admin", "pwd");
        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        verify(mockBanqueManager).loadAllClients();
    }

    @Test
    public void testCrediter() throws Exception {
        Compte compte = mock(Compte.class);
        facade.crediter(compte, 100.0);
        verify(mockBanqueManager).crediter(compte, 100.0);
    }

    @Test
    public void testDebiter() throws Exception {
        Compte compte = mock(Compte.class);
        facade.debiter(compte, 50.0);
        verify(mockBanqueManager).debiter(compte, 50.0);
    }

    @Test
    public void testGetAllClients() {
        Map<String, Client> clients = new HashMap<>();
        when(mockBanqueManager.getAllClients()).thenReturn(clients);

        Map<String, Client> result = facade.getAllClients();
        assertEquals(clients, result);
    }

    @Test
    public void testLogout() {
        facade.logout();
        verify(mockLoginManager).logout();
    }

    @Test
    public void testCreateAccountWithoutDecouvert_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        Client client = mock(Client.class);
        facade.createAccount("FR0123456789", client);
        verify(mockBanqueManager).createAccount("FR0123456789", client);
    }

    @Test
    public void testCreateAccountWithoutDecouvert_NotManager() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);

        facade.createAccount("FR0123456789", client);
        verify(mockBanqueManager, never()).createAccount(anyString(), any(Client.class));
    }

    @Test
    public void testCreateAccountWithDecouvert_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        Client client = mock(Client.class);
        facade.createAccount("FR0123456789", client, 100.0);
        verify(mockBanqueManager).createAccount("FR0123456789", client, 100.0);
    }

    @Test
    public void testDeleteAccount_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        Compte compte = mock(Compte.class);
        facade.deleteAccount(compte);
        verify(mockBanqueManager).deleteAccount(compte);
    }

    @Test
    public void testDeleteAccount_NotManager() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);

        Compte compte = mock(Compte.class);
        facade.deleteAccount(compte);
        verify(mockBanqueManager, never()).deleteAccount(any());
    }

    @Test
    public void testCreateManager_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        facade.createManager("g.new", "pwd", "N", "P", "addr", true);
        verify(mockBanqueManager).createManager("g.new", "pwd", "N", "P", "addr", true);
    }

    @Test
    public void testCreateClient_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        facade.createClient("c.new1", "pwd", "N", "P", "addr", true, "123456789");
        verify(mockBanqueManager).createClient("c.new1", "pwd", "N", "P", "addr", true, "123456789");
    }

    @Test
    public void testDeleteUser_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        Utilisateur user = mock(Client.class);
        facade.deleteUser(user);
        verify(mockBanqueManager).deleteUser(user);
    }

    @Test
    public void testLoadClients_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        facade.loadClients();
        verify(mockBanqueManager).loadAllClients();
    }

    @Test
    public void testLoadClients_NotManager() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);

        facade.loadClients();
        verify(mockBanqueManager, never()).loadAllClients();
    }

    @Test
    public void testGetCompte() {
        Compte compte = mock(Compte.class);
        when(mockBanqueManager.getAccountById("FR0123456789")).thenReturn(compte);

        Compte result = facade.getCompte("FR0123456789");
        assertEquals(compte, result);
    }

    @Test
    public void testChangeDecouvert_AsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        CompteAvecDecouvert compte = mock(CompteAvecDecouvert.class);
        facade.changeDecouvert(compte, 200.0);
        verify(mockBanqueManager).changeDecouvert(compte, 200.0);
    }

    @Test
    public void testChangeDecouvert_NotManager() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);

        CompteAvecDecouvert compte = mock(CompteAvecDecouvert.class);
        facade.changeDecouvert(compte, 200.0);
        verify(mockBanqueManager, never()).changeDecouvert(any(), anyDouble());
    }

    @Test
    public void testUpdateUser() throws Exception {
        Utilisateur user = mock(Client.class);
        facade.updateUser(user);
        verify(mockBanqueManager).updateUser(user);
    }

    @Test
    public void testReloadClient() throws Exception {
        Client client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        when(mockLoginManager.getConnectedUser()).thenReturn(client);
        when(mockBanqueManager.getUserById("j.doe1")).thenReturn(client);

        facade.reloadClient("j.doe1");

        verify(mockBanqueManager).reloadClient("j.doe1");
        verify(mockLoginManager).setCurrentUser(client);
    }

    @Test
    public void testReloadClient_NotClient() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "addr", true, "admin", "pwd");
        when(mockLoginManager.getConnectedUser()).thenReturn(manager);

        facade.reloadClient("admin");

        verify(mockBanqueManager, never()).reloadClient(anyString());
    }
}