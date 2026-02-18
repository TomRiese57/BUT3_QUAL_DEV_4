package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.DetailCompte;
import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;

public class TestDetailCompte {

    private DetailCompte action;
    private BanqueFacade mockFacade;
    private Client client;
    private CompteSansDecouvert compte;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        action = new ObjenesisStd().newInstance(DetailCompte.class);

        mockFacade = mock(BanqueFacade.class);

        mockFacade = mock(BanqueFacade.class);
        Field f = DetailCompte.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        compte = new CompteSansDecouvert("FR0123456789", 100, client);
        client.addAccount(compte);

        action.setCompte(compte);
        action.setMontant("50");
    }

    @Test
    public void testGetErrorTechnical() {
        action.setError("TECHNICAL");
        assertTrue(action.getError().contains("Erreur interne"));
    }

    @Test
    public void testGetErrorBusiness() {
        action.setError("BUSINESS");
        assertTrue(action.getError().contains("insuffisants"));
    }

    @Test
    public void testGetErrorNegativeAmount() {
        action.setError("NEGATIVEAMOUNT");
        assertTrue(action.getError().contains("positif"));
    }

    @Test
    public void testGetErrorNegativeOverdraft() {
        action.setError("NEGATIVEOVERDRAFT");
        assertTrue(action.getError().contains("découvert positif"));
    }

    @Test
    public void testGetErrorIncompatibleOverdraft() {
        action.setError("INCOMPATIBLEOVERDRAFT");
        assertTrue(action.getError().contains("incompatible"));
    }

    @Test
    public void testGetErrorDefault() {
        action.setError("UNKNOWN");
        assertEquals("", action.getError());
    }

    @Test
    public void testSetErrorNull() {
        action.setError(null);
        assertEquals("", action.getError());
    }

    @Test
    public void testGetCompteAsClient() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        assertEquals(compte, action.getCompte());
    }

    @Test
    public void testGetCompteAsClientNotOwned() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        Client autreClient = new Client("X", "Y", "a", true, "x.y1", "pwd", "987654321");
        CompteSansDecouvert compteAutre = new CompteSansDecouvert("FR9999999999", 0, autreClient);
        action.setCompte(compteAutre);
        assertNull(action.getCompte());
    }

    @Test
    public void testGetCompteAsManager() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "a", true, "admin", "pwd");
        when(mockFacade.getConnectedUser()).thenReturn(manager);
        action.setCompte(compte);
        assertEquals(compte, action.getCompte());
    }

    @Test
    public void testGetCompteNullUser() {
        when(mockFacade.getConnectedUser()).thenReturn(null);
        assertNull(action.getCompte());
    }

    @Test
    public void testCreditSuccess() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        assertEquals("SUCCESS", action.credit());
        verify(mockFacade).crediter(compte, 50.0);
    }

    @Test
    public void testCreditNegativeAmount() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        doThrow(new IllegalFormatException("neg")).when(mockFacade).crediter(any(), anyDouble());
        assertEquals("NEGATIVEAMOUNT", action.credit());
    }

    @Test
    public void testCreditNumberFormatException() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setMontant("abc");
        assertEquals("ERROR", action.credit());
    }

    @Test
    public void testDebitSuccess() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        assertEquals("SUCCESS", action.debit());
        verify(mockFacade).debiter(compte, 50.0);
    }

    @Test
    public void testDebitInsufficientFunds() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        doThrow(new InsufficientFundsException("fonds")).when(mockFacade).debiter(any(), anyDouble());
        assertEquals("NOTENOUGHFUNDS", action.debit());
    }

    @Test
    public void testDebitNegativeAmount() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        doThrow(new IllegalFormatException("neg")).when(mockFacade).debiter(any(), anyDouble());
        assertEquals("NEGATIVEAMOUNT", action.debit());
    }

    @Test
    public void testDebitNumberFormatException() {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setMontant("notanumber");
        assertEquals("ERROR", action.debit());
    }

    @Test
    public void testGetSetMontant() {
        action.setMontant("100");
        assertEquals("100", action.getMontant());
    }
}