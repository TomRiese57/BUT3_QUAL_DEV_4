package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.*;

import java.util.HashMap;
import java.util.Map;

public class TestBanqueManagerUnit {

    private BanqueManager banqueManager;
    private IDao mockDao;

    @Before
    public void setUp() {
        banqueManager = new BanqueManager();
        mockDao = mock(IDao.class);
        banqueManager.setDao(mockDao);
    }

    @Test
    public void testCrediter() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);

        banqueManager.crediter(compte, 50.0);

        assertEquals(150.0, compte.getSolde(), 0.001);
        verify(mockDao).updateAccount(compte);
    }

    @Test
    public void testDebiter() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);

        banqueManager.debiter(compte, 30.0);

        assertEquals(70.0, compte.getSolde(), 0.001);
        verify(mockDao).updateAccount(compte);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testDebiterInsufficientFunds() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 10, client);

        banqueManager.debiter(compte, 100.0);
    }

    @Test
    public void testGetAccountById() {
        Compte compte = mock(Compte.class);
        when(mockDao.getAccountById("FR0123456789")).thenReturn(compte);

        Compte result = banqueManager.getAccountById("FR0123456789");
        assertEquals(compte, result);
    }

    @Test
    public void testGetUserById() {
        Utilisateur user = mock(Client.class);
        when(mockDao.getUserById("j.doe1")).thenReturn(user);

        Utilisateur result = banqueManager.getUserById("j.doe1");
        assertEquals(user, result);
    }

    @Test
    public void testCreateAccountSansDecouvert() throws Exception {
        Client client = mock(Client.class);
        banqueManager.createAccount("FR0123456789", client);
        verify(mockDao).createCompteSansDecouvert(0, "FR0123456789", client);
    }

    @Test
    public void testCreateAccountAvecDecouvert() throws Exception {
        Client client = mock(Client.class);
        banqueManager.createAccount("FR0123456789", client, 100.0);
        verify(mockDao).createCompteAvecDecouvert(0, "FR0123456789", 100.0, client);
    }

    @Test(expected = IllegalOperationException.class)
    public void testDeleteAccountWithNonZeroSolde() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 100, client);
        banqueManager.deleteAccount(compte);
    }

    @Test
    public void testDeleteAccountWithZeroSolde() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 0, client);
        banqueManager.deleteAccount(compte);
        verify(mockDao).deleteAccount(compte);
    }

    @Test
    public void testCreateManager() throws Exception {
        banqueManager.createManager("g.new", "pwd", "N", "P", "addr", true);
        verify(mockDao).createUser("N", "P", "addr", true, "g.new", "pwd", true, null);
    }

    @Test
    public void testCreateClient() throws Exception {
        Map<String, Client> clients = new HashMap<>();
        when(mockDao.getAllClients()).thenReturn(clients);
        banqueManager.loadAllClients();

        banqueManager.createClient("c.new1", "pwd", "N", "P", "addr", true, "123456789");
        verify(mockDao).createUser("N", "P", "addr", true, "c.new1", "pwd", false, "123456789");
    }

    @Test(expected = IllegalOperationException.class)
    public void testCreateClientDuplicateNumeroClient() throws Exception {
        Client existing = new Client("E", "E", "a", true, "c.exist1", "pwd", "123456789");
        Map<String, Client> clients = new HashMap<>();
        clients.put("c.exist1", existing);
        when(mockDao.getAllClients()).thenReturn(clients);
        banqueManager.loadAllClients();

        banqueManager.createClient("c.new1", "pwd", "N", "P", "addr", true, "123456789");
    }

    @Test
    public void testUpdateUser() throws Exception {
        Utilisateur user = mock(Client.class);
        banqueManager.updateUser(user);
        verify(mockDao).updateUser(user);
    }

    @Test(expected = TechnicalException.class)
    public void testUpdateUserNull() throws Exception {
        banqueManager.updateUser(null);
    }

    @Test
    public void testReloadClient() {
        banqueManager.reloadClient("j.doe1");
        verify(mockDao).reloadUser("j.doe1");
    }

    @Test
    public void testGetAllClients() {
        Map<String, Client> clients = new HashMap<>();
        when(mockDao.getAllClients()).thenReturn(clients);
        banqueManager.loadAllClients();

        Map<String, Client> result = banqueManager.getAllClients();
        assertEquals(clients, result);
    }

    @Test
    public void testDeleteUserClient_WithZeroSoldeAccounts() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        Compte compte = new CompteSansDecouvert("FR0123456789", 0, client);
        client.addAccount(compte);

        Map<String, Client> clients = new HashMap<>();
        clients.put("j.doe1", client);
        when(mockDao.getAllClients()).thenReturn(clients);
        banqueManager.loadAllClients();

        banqueManager.deleteUser(client);
        verify(mockDao).deleteAccount(compte);
        verify(mockDao).deleteUser(client);
    }

    @Test(expected = IllegalOperationException.class)
    public void testDeleteLastManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "a", true, "admin", "pwd");
        Map<String, Gestionnaire> managers = new HashMap<>();
        managers.put("admin", manager);

        Map<String, com.iut.banque.modele.Gestionnaire> gMap = new HashMap<>();
        gMap.put("admin", manager);
        when(mockDao.getAllGestionnaires()).thenReturn(gMap);
        banqueManager.loadAllGestionnaires();

        banqueManager.deleteUser(manager);
    }

    @Test
    public void testChangeDecouvert() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 0, 100, client);

        banqueManager.changeDecouvert(compte, 200.0);

        assertEquals(200.0, compte.getDecouvertAutorise(), 0.001);
        verify(mockDao).updateAccount(compte);
    }
}