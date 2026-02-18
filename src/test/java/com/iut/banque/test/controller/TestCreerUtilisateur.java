package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.CreerUtilisateur;
import com.iut.banque.exceptions.*;
import com.iut.banque.facade.BanqueFacade;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;

public class TestCreerUtilisateur {

    private CreerUtilisateur action;
    private BanqueFacade mockFacade;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        action = new ObjenesisStd().newInstance(CreerUtilisateur.class);

        mockFacade = mock(BanqueFacade.class);
        Field f = CreerUtilisateur.class.getDeclaredField("banque");
        f.setAccessible(true);
        f.set(action, mockFacade);

        action.setUserId("c.new1");
        action.setUserPwd("password");
        action.setNom("Doe");
        action.setPrenom("John");
        action.setAdresse("1 rue");
        action.setMale(true);
        action.setNumClient("123456789");
        action.setClient(true);
    }

    @Test
    public void testCreationClientSuccess() throws Exception {
        assertEquals("SUCCESS", action.creationUtilisateur());
        assertTrue(action.getMessage().contains("c.new1"));
        assertEquals("SUCCESS", action.getResult());
    }

    @Test
    public void testCreationManagerSuccess() throws Exception {
        action.setClient(false);
        assertEquals("SUCCESS", action.creationUtilisateur());
        verify(mockFacade).createManager(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    public void testCreationClientIllegalOperation() throws Exception {
        doThrow(new IllegalOperationException("dup")).when(mockFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
        assertEquals("ERROR", action.creationUtilisateur());
        assertTrue(action.getMessage().contains("assigné"));
    }

    @Test
    public void testCreationClientTechnicalException() throws Exception {
        doThrow(new TechnicalException("dup")).when(mockFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
        assertEquals("ERROR", action.creationUtilisateur());
        assertTrue(action.getMessage().contains("numéro de client"));
    }

    @Test
    public void testCreationClientIllegalArgument() throws Exception {
        doThrow(new IllegalArgumentException("fmt")).when(mockFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
        assertEquals("ERROR", action.creationUtilisateur());
        assertTrue(action.getMessage().contains("identifiant"));
    }

    @Test
    public void testCreationClientIllegalFormat() throws Exception {
        doThrow(new IllegalFormatException("fmt")).when(mockFacade)
                .createClient(anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyString());
        assertEquals("ERROR", action.creationUtilisateur());
        assertTrue(action.getMessage().contains("numéro de client incorrect"));
    }

    @Test
    public void testGettersSetters() {
        action.setUserId("t.test1");
        assertEquals("t.test1", action.getUserId());

        action.setNom("Martin");
        assertEquals("Martin", action.getNom());

        action.setPrenom("Sophie");
        assertEquals("Sophie", action.getPrenom());

        action.setAdresse("2 bd");
        assertEquals("2 bd", action.getAdresse());

        action.setMale(false);
        assertFalse(action.isMale());

        action.setClient(false);
        assertFalse(action.isClient());

        action.setNumClient("987654321");
        assertEquals("987654321", action.getNumClient());

        action.setMessage("msg");
        assertEquals("msg", action.getMessage());

        action.setResult("RES");
        assertEquals("RES", action.getResult());
    }

    @Test
    public void testSetUserPwdIsHashed() {
        action.setUserPwd("plaintext");
        assertNotNull(action.getUserPwd());
        assertNotEquals("plaintext", action.getUserPwd());
    }
}