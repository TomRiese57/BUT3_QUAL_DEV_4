package com.iut.banque.test.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.modele.*;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Tests unitaires pour {@link DaoHibernate} avec Mockito (sans base de données).
 * Complète les tests d'intégration existants pour les méthodes non couvertes.
 */
public class TestDaoHibernateUnit {

    private DaoHibernate dao;
    private SessionFactory mockSessionFactory;
    private Session mockSession;

    @Before
    public void setUp() throws Exception {
        dao = new DaoHibernate();

        mockSessionFactory = mock(SessionFactory.class);
        mockSession = mock(Session.class);
        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);

        Field f = DaoHibernate.class.getDeclaredField("sessionFactory");
        f.setAccessible(true);
        f.set(dao, mockSessionFactory);
    }

    // ------------------------------------------------------------------ //
    //  reloadUser                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void testReloadUser_utilisateurInexistant() {
        when(mockSession.get(Utilisateur.class, "inconnu")).thenReturn(null);
        // Ne doit pas lever d'exception
        dao.reloadUser("inconnu");
        verify(mockSession).get(Utilisateur.class, "inconnu");
    }

    @Test
    public void testReloadUser_gestionnaire() throws Exception {
        Gestionnaire manager = new Gestionnaire("A", "B", "a", true, "admin", "pwd");
        when(mockSession.get(Utilisateur.class, "admin")).thenReturn(manager);

        dao.reloadUser("admin");

        verify(mockSession).refresh(manager);
        // Pas d'appel pour les comptes (gestionnaire)
    }

    @Test
    public void testReloadUser_clientAvecComptes() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, client);
        client.addAccount(compte);

        when(mockSession.get(Utilisateur.class, "j.doe1")).thenReturn(client);

        dao.reloadUser("j.doe1");

        verify(mockSession).refresh(client);
        verify(mockSession).refresh(compte);
    }

    // ------------------------------------------------------------------ //
    //  disconnect                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void testDisconnect() {
        // Ne doit pas lever d'exception
        dao.disconnect();
        // Aucune interaction avec la session
        verifyNoInteractions(mockSession);
    }

    // ------------------------------------------------------------------ //
    //  getAccountsByClientId                                             //
    // ------------------------------------------------------------------ //

    @Test
    public void testGetAccountsByClientId_clientTrouve() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, client);
        client.addAccount(compte);

        when(mockSession.get(Client.class, "j.doe1")).thenReturn(client);

        Map<String, Compte> result = dao.getAccountsByClientId("j.doe1");
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("FR0123456789"));
    }

    @Test
    public void testGetAccountsByClientId_clientIntrouvable() {
        when(mockSession.get(Client.class, "inconnu")).thenReturn(null);

        Map<String, Compte> result = dao.getAccountsByClientId("inconnu");
        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------ //
    //  updateAccount                                                     //
    // ------------------------------------------------------------------ //

    @Test
    public void testUpdateAccount() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, client);

        dao.updateAccount(compte);
        verify(mockSession).update(compte);
    }

    // ------------------------------------------------------------------ //
    //  updateUser                                                        //
    // ------------------------------------------------------------------ //

    @Test
    public void testUpdateUser() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");

        dao.updateUser(client);
        verify(mockSession).update(client);
    }

    // ------------------------------------------------------------------ //
    //  isUserAllowed — cas limites                                       //
    // ------------------------------------------------------------------ //

    @Test
    public void testIsUserAllowed_espacesDansId() {
        assertFalse(dao.isUserAllowed("  ", "password"));
    }

    @Test
    public void testIsUserAllowed_espacesDansPassword() {
        // Le trim est sur userId, pas sur password — l'espace dans le mot de passe est intentionnel
        // Le test vérifie que la méthode ne plante pas et retourne false si user inexistant
        when(mockSession.get(Utilisateur.class, "user")).thenReturn(null);
        assertFalse(dao.isUserAllowed("user", "  "));
    }

    // ------------------------------------------------------------------ //
    //  deleteAccount                                                     //
    // ------------------------------------------------------------------ //

    @Test(expected = com.iut.banque.exceptions.TechnicalException.class)
    public void testDeleteAccount_null() throws Exception {
        dao.deleteAccount(null);
    }

    @Test
    public void testDeleteAccount_ok() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 0, client);

        dao.deleteAccount(compte);
        verify(mockSession).delete(compte);
    }

    // ------------------------------------------------------------------ //
    //  deleteUser                                                        //
    // ------------------------------------------------------------------ //

    @Test(expected = com.iut.banque.exceptions.TechnicalException.class)
    public void testDeleteUser_null() throws Exception {
        dao.deleteUser(null);
    }

    @Test
    public void testDeleteUser_ok() throws Exception {
        Client client = new Client("D", "J", "a", true, "j.doe1", "pwd", "123456789");
        dao.deleteUser(client);
        verify(mockSession).delete(client);
    }
}