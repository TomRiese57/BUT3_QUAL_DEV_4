<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>

<html lang="fr" xml:lang="fr">
<head>
    <title>Tableau de bord - Gestionnaire</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .welcome-box {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            border-left: 4px solid #3b82f6;
            padding: 1.5rem;
            border-radius: 12px;
            margin: 2rem auto;
            max-width: 90%;
            text-align: center;
        }
        
        .welcome-box b {
            color: #1e40af;
        }
        
        .subtitle {
            color: #64748b;
            font-size: 0.9375rem;
            margin-top: 0.5rem;
        }
        
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .menu-card {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            padding: 2rem;
            text-align: center;
            transition: all 0.3s ease;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
        }
        
        .menu-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            border-color: #3b82f6;
        }
        
        .menu-icon {
            font-size: 3.5rem;
            margin-bottom: 1rem;
        }
        
        .menu-card a {
            color: #1e3a8a;
            font-size: 1.25rem;
            font-weight: 600;
            text-decoration: none;
        }
        
        .menu-card a:hover {
            color: #3b82f6;
            text-decoration: none;
        }
        
        .menu-description {
            color: #64748b;
            font-size: 0.875rem;
            margin-top: 0.75rem;
        }
        
        .admin-notice {
            margin: 2rem auto;
            padding: 1.5rem;
            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
            border-radius: 12px;
            border-left: 4px solid #f59e0b;
            max-width: 90%;
            text-align: center;
            color: #92400e;
        }
    </style>
</head>
<body>
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="Déconnexion" />
        </s:form>
    </div>
    
    <h1>⚙️ Tableau de bord - Gestionnaire</h1>
    
    <div class="welcome-box">
        <p style="margin: 0; font-size: 1.125rem;">
            Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b> !
        </p>
        <p class="subtitle">Espace d'administration et de gestion</p>
    </div>
    
    <p style="text-align: center; color: #1e3a8a; font-size: 1.5rem; font-weight: 600; margin: 2rem 0;">
        Que voulez-vous faire ?
    </p>
    
    <div class="menu-grid">
        <div class="menu-card">
            <div class="menu-icon">🏦</div>
            <s:url action="listeCompteManager" var="urlListeCompteManager">
                <s:param name="aDecouvert">false</s:param>
            </s:url>
            <s:a href="%{urlListeCompteManager}">
                Liste des comptes de la banque
            </s:a>
            <p class="menu-description">
                Consulter tous les comptes clients
            </p>
        </div>
        
        <div class="menu-card">
            <div class="menu-icon">⚠️</div>
            <s:url action="listeCompteManager" var="urlListeCompteManager">
                <s:param name="aDecouvert">true</s:param>
            </s:url>
            <s:a href="%{urlListeCompteManager}">
                Liste des comptes à découvert de la banque
            </s:a>
            <p class="menu-description">
                Surveiller les comptes en négatif
            </p>
        </div>
        
        <div class="menu-card">
            <div class="menu-icon">👤</div>
            <s:url action="urlAjoutUtilisateur" var="urlAjoutUtilisateur"></s:url>
            <s:a href="%{urlAjoutUtilisateur}">
                Ajout d'un utilisateur
            </s:a>
            <p class="menu-description">
                Créer un nouveau compte utilisateur
            </p>
        </div>
    </div>
    
    <div class="admin-notice">
        🛡️ <strong>Accès administrateur :</strong> Vous disposez de privilèges étendus pour gérer la plateforme
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
