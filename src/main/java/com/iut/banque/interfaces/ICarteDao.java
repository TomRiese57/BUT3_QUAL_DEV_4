package com.iut.banque.interfaces;

import com.iut.banque.modele.CarteBancaire;
import com.iut.banque.modele.Compte;

import java.util.List;

/**
 * Interface DAO pour la gestion des cartes bancaires.
 */
public interface ICarteDao {

    /**
     * Persiste une nouvelle carte bancaire.
     *
     * @param carte carte à sauvegarder (ne doit pas être null, ni déjà persistée)
     */
    void saveCart(CarteBancaire carte);

    /**
     * Met à jour une carte bancaire existante (soldes, plafonds, état de blocage…).
     *
     * @param carte carte à mettre à jour
     */
    void updateCarte(CarteBancaire carte);

    /**
     * Supprime une carte bancaire.
     *
     * @param carte carte à supprimer
     */
    void deleteCarte(CarteBancaire carte);

    /**
     * Recherche une carte par son numéro PAN.
     *
     * @param numeroCarte numéro de la carte (16 chiffres)
     * @return la carte, ou {@code null} si introuvable
     */
    CarteBancaire getCarteByNumero(String numeroCarte);

    /**
     * Retourne toutes les cartes rattachées à un compte donné.
     *
     * @param compte le compte propriétaire
     * @return liste des cartes (jamais null, peut être vide)
     */
    List<CarteBancaire> getCartesByCompte(Compte compte);

    /**
     * Retourne toutes les cartes en mode paiement différé
     * dont le montant en attente est &gt; 0 (utile pour le batch de fin de mois).
     *
     * @return liste des cartes à débiter
     */
    List<CarteBancaire> getCartesDiffereesADebiter();
}