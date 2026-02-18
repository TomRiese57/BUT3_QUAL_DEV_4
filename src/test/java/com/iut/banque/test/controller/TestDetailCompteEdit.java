package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.DetailCompteEdit;
import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.*;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;

public class TestDetailCompteEdit {

    private DetailCompteEdit action;
    private BanqueFacade mockFacade;
    private Client client;
    private CompteAvecDecouvert compteAvecDecouvert;
    private CompteSansDecouvert compteSansDecouvert;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        action = new ObjenesisStd().newInstance(DetailCompteEdit.class);

        mockFacade = mock(BanqueFacade.class);
        // banque est dans la classe parente DetailCompte
        Field f = DetailCompteEdit.class.getSuperclass().getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        client = new Client("Doe", "John", "addr", true, "j.doe1", "pwd", "123456789");
        compteAvecDecouvert = new CompteAvecDecouvert("FR0123456789", 0, 100, client);
        compteSansDecouvert = new CompteSansDecouvert("FR9999999999", 0, client);
        client.addAccount(compteAvecDecouvert);
        client.addAccount(compteSansDecouvert);
    }

    @Test
    public void testGetSetDecouvertAutorise() {
        action.setDecouvertAutorise("200");
        assertEquals("200", action.getDecouvertAutorise());
    }

    @Test
    public void testChangementDecouvertSuccess() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("200");

        assertEquals("SUCCESS", action.changementDecouvert());
        verify(mockFacade).changeDecouvert(compteAvecDecouvert, 200.0);
    }

    @Test
    public void testChangementDecouvertNotCompteAvecDecouvert() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compteSansDecouvert);
        action.setDecouvertAutorise("200");
        assertEquals("ERROR", action.changementDecouvert());
    }

    @Test
    public void testChangementDecouvertNumberFormatException() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("notanumber");
        assertEquals("ERROR", action.changementDecouvert());
    }

    @Test
    public void testChangementDecouvertIllegalFormat() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("-50");
        doThrow(new IllegalFormatException("neg")).when(mockFacade).changeDecouvert(any(), anyDouble());
        assertEquals("NEGATIVEOVERDRAFT", action.changementDecouvert());
    }

    @Test
    public void testChangementDecouvertIllegalOperation() throws Exception {
        when(mockFacade.getConnectedUser()).thenReturn(client);
        action.setCompte(compteAvecDecouvert);
        action.setDecouvertAutorise("10");
        doThrow(new IllegalOperationException("incomp")).when(mockFacade).changeDecouvert(any(), anyDouble());
        assertEquals("INCOMPATIBLEOVERDRAFT", action.changementDecouvert());
    }
}