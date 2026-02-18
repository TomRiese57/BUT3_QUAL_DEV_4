package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TestListeCompteManager {

    private ListeCompteManager action;
    private BanqueFacade mockFacade;
    private Client client;
    private CompteSansDecouvert compte;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        action = new ObjenesisStd().newInstance(ListeCompteManager.class);

        mockFacade = mock(BanqueFacade.class);

        Field f = ListeCompteManager.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        compte = new CompteSansDecouvert("FR0123456789", 0, client);
        client.addAccount(compte);
    }

    @Test
    public void testGetAllClients() {
        Map<String, Client> clients = new HashMap<>();
        clients.put("j.doe1", client);
        when(mockFacade.getAllClients()).thenReturn(clients);

        Map<String, Client> result = action.getAllClients();
        verify(mockFacade).loadClients();
        assertEquals(clients, result);
    }

    @Test
    public void testGetSetClient() {
        action.setClient(client);
        assertEquals(client, action.getClient());
    }

    @Test
    public void testGetSetCompte() {
        action.setCompte(compte);
        assertEquals(compte, action.getCompte());
    }

    @Test
    public void testGetSetaDecouvert() {
        action.setaDecouvert(true);
        assertTrue(action.isaDecouvert());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        action.setClient(client);
        assertEquals("SUCCESS", action.deleteUser());
        verify(mockFacade).deleteUser(client);
        // userInfo = "J D (123456789)"
        assertNotNull(action.getUserInfo());
    }

    @Test
    public void testDeleteUserTechnicalException() throws Exception {
        action.setClient(client);
        doThrow(new TechnicalException("err")).when(mockFacade).deleteUser(client);
        assertEquals("ERROR", action.deleteUser());
    }

    @Test
    public void testDeleteUserIllegalOperation() throws Exception {
        action.setClient(client);
        doThrow(new IllegalOperationException("nonempty")).when(mockFacade).deleteUser(client);
        assertEquals("NONEMPTYACCOUNT", action.deleteUser());
    }

    @Test
    public void testDeleteAccountSuccess() throws Exception {
        action.setCompte(compte);
        assertEquals("SUCCESS", action.deleteAccount());
        verify(mockFacade).deleteAccount(compte);
        assertEquals("FR0123456789", action.getCompteInfo());
    }

    @Test
    public void testDeleteAccountIllegalOperation() throws Exception {
        action.setCompte(compte);
        doThrow(new IllegalOperationException("nonempty")).when(mockFacade).deleteAccount(compte);
        assertEquals("NONEMPTYACCOUNT", action.deleteAccount());
    }

    @Test
    public void testDeleteAccountTechnicalException() throws Exception {
        action.setCompte(compte);
        doThrow(new TechnicalException("err")).when(mockFacade).deleteAccount(compte);
        assertEquals("ERROR", action.deleteAccount());
    }
}