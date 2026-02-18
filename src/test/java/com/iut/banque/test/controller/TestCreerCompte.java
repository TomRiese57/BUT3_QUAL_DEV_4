package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.CreerCompte;
import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;

public class TestCreerCompte {

    private CreerCompte action;
    private BanqueFacade mockFacade;
    private Client client;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        action = new ObjenesisStd().newInstance(CreerCompte.class);

        mockFacade = mock(BanqueFacade.class);
        Field f = CreerCompte.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        action.setClient(client);
        action.setNumeroCompte("FR0123456789");
    }

    @Test
    public void testCreationCompteSansDecouvertSuccess() throws Exception {
        action.setAvecDecouvert(false);
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 0, client);
        when(mockFacade.getCompte("FR0123456789")).thenReturn(compte);

        assertEquals("SUCCESS", action.creationCompte());
        verify(mockFacade).createAccount("FR0123456789", client);
    }

    @Test
    public void testCreationCompteAvecDecouvertSuccess() throws Exception {
        action.setAvecDecouvert(true);
        action.setDecouvertAutorise(100.0);
        CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 0, 100, client);
        when(mockFacade.getCompte("FR0123456789")).thenReturn(compte);

        assertEquals("SUCCESS", action.creationCompte());
        verify(mockFacade).createAccount("FR0123456789", client, 100.0);
    }

    @Test
    public void testCreationCompteNonUniqueId() throws Exception {
        action.setAvecDecouvert(false);
        doThrow(new TechnicalException("dup")).when(mockFacade).createAccount(anyString(), any(Client.class));
        assertEquals("NONUNIQUEID", action.creationCompte());
    }

    @Test
    public void testCreationCompteInvalidFormat() throws Exception {
        action.setAvecDecouvert(false);
        doThrow(new IllegalFormatException("fmt")).when(mockFacade).createAccount(anyString(), any(Client.class));
        assertEquals("INVALIDFORMAT", action.creationCompte());
    }

    @Test
    public void testCreationCompteIllegalOperation() throws Exception {
        action.setAvecDecouvert(true);
        action.setDecouvertAutorise(100.0);
        doThrow(new IllegalOperationException("op")).when(mockFacade).createAccount(anyString(), any(Client.class), anyDouble());
        assertEquals("ERROR", action.creationCompte());
    }

    @Test
    public void testGettersSetters() {
        action.setNumeroCompte("FR9999999999");
        assertEquals("FR9999999999", action.getNumeroCompte());

        action.setAvecDecouvert(true);
        assertTrue(action.isAvecDecouvert());

        action.setDecouvertAutorise(200.0);
        assertEquals(200.0, action.getDecouvertAutorise(), 0.001);

        action.setError(true);
        assertTrue(action.isError());

        action.setResult(true);
        assertTrue(action.isResult());

        assertEquals(client, action.getClient());
    }

    @Test
    public void testSetMessageNONUNIQUEID() {
        action.setMessage("NONUNIQUEID");
        assertTrue(action.getMessage().contains("existe déjà"));
    }

    @Test
    public void testSetMessageINVALIDFORMAT() {
        action.setMessage("INVALIDFORMAT");
        assertTrue(action.getMessage().contains("format valide"));
    }

    @Test
    public void testSetMessageSUCCESS() throws Exception {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 0, client);
        action.setCompte(compte);
        action.setMessage("SUCCESS");
        assertTrue(action.getMessage().contains("FR0123456789"));
    }

    @Test
    public void testSetMessageDefault() {
        action.setMessage("UNKNOWN");
        assertNull(action.getMessage());
    }
}