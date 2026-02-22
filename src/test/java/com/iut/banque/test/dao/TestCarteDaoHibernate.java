package com.iut.banque.test.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;

import com.iut.banque.dao.CarteDaoHibernate;
import com.iut.banque.modele.*;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Tests unitaires pour {@link CarteDaoHibernate}.
 * Utilise Mockito pour éviter Hibernate et la base de données.
 */
public class TestCarteDaoHibernate {

    private CarteDaoHibernate dao;
    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private CarteBancaire carte;
    private Compte mockCompte;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Before
    public void setUp() throws Exception {
        dao = new CarteDaoHibernate();

        mockSessionFactory = mock(SessionFactory.class);
        mockSession = mock(Session.class);
        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);

        Field f = CarteDaoHibernate.class.getDeclaredField("sessionFactory");
        f.setAccessible(true);
        f.set(dao, mockSessionFactory);

        // Carte de test
        carte = mock(CarteBancaire.class);
        when(carte.getNumeroCarte()).thenReturn("1234567890123456");

        mockCompte = mock(Compte.class);
        when(mockCompte.getNumeroCompte()).thenReturn("FR0123456789");
    }

    @Test
    public void testSetSessionFactory() throws Exception {
        // 1. Création d'un mock différent de celui du setUp
        SessionFactory anotherFactory = mock(SessionFactory.class);

        // 2. Appel de la méthode à tester (le setter)
        dao.setSessionFactory(anotherFactory);

        // 3. Récupération du champ privé via Réflexion (car pas de getter public)
        // Note : Si le champ est dans la classe mère, utilisez dao.getClass().getSuperclass().getDeclaredField(...)
        Field f = CarteDaoHibernate.class.getDeclaredField("sessionFactory");
        f.setAccessible(true);
        Object factoryInjectee = f.get(dao);

        // 4. ASSERTION : On vérifie que le champ privé a bien pris la nouvelle valeur
        assertEquals("La SessionFactory n'a pas été correctement injectée", anotherFactory, factoryInjectee);
    }

    @Test
    public void testSaveCart() {
        dao.saveCart(carte);
        verify(mockSession).save(carte);
    }

    @Test
    public void testUpdateCarte() {
        dao.updateCarte(carte);
        verify(mockSession).update(carte);
    }

    @Test
    public void testDeleteCarte() {
        dao.deleteCarte(carte);
        verify(mockSession).delete(carte);
    }

    @Test
    public void testGetCarteByNumero_found() {
        when(mockSession.get(CarteBancaire.class, "1234567890123456")).thenReturn(carte);
        CarteBancaire result = dao.getCarteByNumero("1234567890123456");
        assertEquals(carte, result);
    }

    @Test
    public void testGetCarteByNumero_notFound() {
        when(mockSession.get(CarteBancaire.class, "9999999999999999")).thenReturn(null);
        CarteBancaire result = dao.getCarteByNumero("9999999999999999");
        assertNull(result);
    }

    @Test
    public void testGetCartesByCompte_nullCompte() {
        List<CarteBancaire> result = dao.getCartesByCompte(null);
        assertTrue(result.isEmpty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testGetCartesByCompte_avecCompte() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<CarteBancaire> cq = mock(CriteriaQuery.class);
        Root<CarteBancaire> root = mock(Root.class);
        Path path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        Query<CarteBancaire> query = mock(Query.class);

        when(mockSession.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(CarteBancaire.class)).thenReturn(cq);
        when(cq.from(CarteBancaire.class)).thenReturn(root);
        when(root.get("compte")).thenReturn(path);
        when(cb.equal(path, mockCompte)).thenReturn(predicate);
        when(cq.where(predicate)).thenReturn(cq);
        when(mockSession.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(carte));

        List<CarteBancaire> result = dao.getCartesByCompte(mockCompte);
        assertEquals(1, result.size());
        assertEquals(carte, result.get(0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testGetCartesDiffereesADebiter() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<CarteBancaire> cq = mock(CriteriaQuery.class);
        Root<CarteBancaire> root = mock(Root.class);
        Path pathDiffere = mock(Path.class);
        Path pathMontant = mock(Path.class);
        Predicate predTrue = mock(Predicate.class);
        Predicate predGt = mock(Predicate.class);
        Predicate predAnd = mock(Predicate.class);
        Query<CarteBancaire> query = mock(Query.class);

        when(mockSession.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(CarteBancaire.class)).thenReturn(cq);
        when(cq.from(CarteBancaire.class)).thenReturn(root);
        when(root.get("paiementDiffere")).thenReturn(pathDiffere);
        when(root.get("montantDiffereEnAttente")).thenReturn(pathMontant);
        when(cb.isTrue(pathDiffere)).thenReturn(predTrue);
        when(cb.gt(pathMontant, 0.0)).thenReturn(predGt);
        when(cb.and(predTrue, predGt)).thenReturn(predAnd);
        when(cq.where(predAnd)).thenReturn(cq);
        when(mockSession.createQuery(cq)).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(carte));

        List<CarteBancaire> result = dao.getCartesDiffereesADebiter();
        assertEquals(1, result.size());
    }
}