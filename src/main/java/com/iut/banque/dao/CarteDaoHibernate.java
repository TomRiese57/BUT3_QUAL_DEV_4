package com.iut.banque.dao;

import com.iut.banque.interfaces.ICarteDao;
import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.Compte;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;

@Transactional
public class CarteDaoHibernate implements ICarteDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveCart(CarteBancaire carte) {
        sessionFactory.getCurrentSession().save(carte);
    }

    @Override
    public void updateCarte(CarteBancaire carte) {
        sessionFactory.getCurrentSession().update(carte);
    }

    @Override
    public void deleteCarte(CarteBancaire carte) {
        sessionFactory.getCurrentSession().delete(carte);
    }

    @Override
    public CarteBancaire getCarteByNumero(String numeroCarte) {
        return sessionFactory.getCurrentSession().get(CarteBancaire.class, numeroCarte);
    }

    @Override
    public List<CarteBancaire> getCartesByCompte(Compte compte) {
        if (compte == null) return Collections.emptyList();
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarteBancaire> cq = cb.createQuery(CarteBancaire.class);
        Root<CarteBancaire> root = cq.from(CarteBancaire.class);
        cq.where(cb.equal(root.get("compte"), compte));
        return session.createQuery(cq).getResultList();
    }

    @Override
    public List<CarteBancaire> getCartesDiffereesADebiter() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<CarteBancaire> cq = cb.createQuery(CarteBancaire.class);
        Root<CarteBancaire> root = cq.from(CarteBancaire.class);
        cq.where(
                cb.and(
                        cb.isTrue(root.get("paiementDiffere")),
                        cb.gt(root.get("montantDiffereEnAttente"), 0.0)
                )
        );
        return session.createQuery(cq).getResultList();
    }
}