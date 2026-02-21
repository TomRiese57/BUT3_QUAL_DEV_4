<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>

<html lang="fr" xml:lang="fr">
<head>
    <title>Tableau de bord - Gestionnaire</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 1.25rem;
            max-width: 1000px;
            margin: 1.75rem auto;
            padding: 0 1.25rem;
        }

        .menu-card {
            background: var(--white);
            border: 1.5px solid var(--border);
            border-radius: var(--radius-lg);
            padding: 1.75rem 1.5rem;
            text-align: center;
            transition: border-color 0.18s, box-shadow 0.18s, transform 0.18s;
            box-shadow: var(--shadow-sm);
        }

        .menu-card:hover {
            border-color: var(--blue-600);
            box-shadow: var(--shadow-md);
            transform: translateY(-3px);
        }

        .menu-icon { font-size: 2.5rem; margin-bottom: 0.875rem; }

        .menu-card a {
            font-size: 1rem;
            font-weight: 600;
            color: var(--blue-800);
        }

        .menu-card a:hover { color: var(--blue-600); text-decoration: none; }

        .menu-description {
            font-size: 0.85rem;
            color: var(--gray-500);
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>

    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>

    <h1>⚙️ Tableau de bord — Gestionnaire</h1>

    <div class="welcome-box">
        <p style="margin:0; font-size:1.0625rem;">
            Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b> !
        </p>
        <p style="margin:.25rem 0 0; font-size:.875rem; opacity:.75;">Espace d'administration et de gestion</p>
    </div>

    <p style="text-align:center; color:var(--blue-800); font-size:1rem; font-weight:600; margin:1.5rem 0 0;">
        Que voulez-vous faire ?
    </p>

    <div class="menu-grid">
        <div class="menu-card">
            <div class="menu-icon">🏦</div>
            <s:url action="listeCompteManager" var="urlListeCompteManager">
                <s:param name="aDecouvert">false</s:param>
            </s:url>
            <s:a href="%{urlListeCompteManager}">Liste des comptes de la banque</s:a>
            <p class="menu-description">Consulter tous les comptes clients</p>
        </div>

        <div class="menu-card">
            <div class="menu-icon">⚠️</div>
            <s:url action="listeCompteManager" var="urlListeDecouvert">
                <s:param name="aDecouvert">true</s:param>
            </s:url>
            <s:a href="%{urlListeDecouvert}">Comptes à découvert</s:a>
            <p class="menu-description">Surveiller les comptes en négatif</p>
        </div>

        <div class="menu-card">
            <div class="menu-icon">👤</div>
            <s:url action="urlAjoutUtilisateur" var="urlAjoutUtilisateur"></s:url>
            <s:a href="%{urlAjoutUtilisateur}">Ajout d'un utilisateur</s:a>
            <p class="menu-description">Créer un nouveau compte utilisateur</p>
        </div>
    </div>

    <div class="admin-notice">
        🛡️ <strong>Accès administrateur :</strong> Vous disposez de privilèges étendus pour gérer la plateforme
    </div>

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
