package com.iut.banque.test.modele;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.Before;

import com.iut.banque.modele.Client;
import com.iut.banque.cryptage.PasswordHasher;
import com.iut.banque.modele.Utilisation;

public class TestMotDePasseUtilisateur {

    Client c;

    @Before
    public void setUp() throws Exception {
        c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
    }
    /**
     * Tests de la méthode SetUserPwd
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMethodeSetUserPwdNull() {
        // verifie qu'on ne peut pas mettre de mot de passe null
        c.setUserPwd(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodeSetUserPwdVide() {
        //verifie qu'on ne peut pas mettre de mot de passe vide
        c.setUserPwd("");
    }

    @Test
    public void testMethodeSetUserPwdValide() {
        // Verifie que le password est definie dans la classe client
        c.setUserPwd("MotDePasse123!");
        assertNotNull(c.getUserPwd());
    }

    @Test
    public void testMethodeSetUserPwdPasswordIsHashed() {
        // Test que l'on peut set un autre mot de passe
        String mdp = "motdepasse123!";
        c.setUserPwd(mdp);
        assertFalse("Le mot de passe stocké ne doit pas être en clair", mdp.equals(u.getUserPwd()));
    }

    /**
     * Tests du mot de passe via constructeur de client
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructeurPasswordVide(){
        // Verifie que creer un client avec un mot de passe vide renvoi une exeption
        Client cli = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "", "1234567890");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructeurPasswordNull(){
        // Verifie que creer un client avec un mot de passe null renvoi une exeption
        Client2 cli = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", null , "1234567890");
    }

    @Test
    public void testConstructeurPasswordNull(){
        // Verifie que creer un client avec un mot de passe null renvoi une exeption
        Client2 cli = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", null , "1234567890");
        AssertNotNull(cli.getUserPwd());
    }

}