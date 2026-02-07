<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Erreur de Connexion</title>
    <link rel="stylesheet" href="/ASBank203/style/style.css" />
    <style>
        .error-container {
            max-width: 600px;
            margin: 3rem auto;
            background: white;
            padding: 3rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .error-icon {
            font-size: 5rem;
            margin-bottom: 1.5rem;
        }
        
        .error-title {
            color: #991b1b;
            font-size: 1.75rem;
            font-weight: 700;
            margin-bottom: 1rem;
        }
        
        .error-message {
            color: #7f1d1d;
            font-size: 1.125rem;
            margin-bottom: 2rem;
        }
        
        .help-section {
            margin-top: 2rem;
            padding: 1.5rem;
            background: #fef3c7;
            border-radius: 12px;
            border-left: 4px solid #f59e0b;
            text-align: left;
        }
        
        .help-section h3 {
            color: #92400e;
            margin: 0 0 1rem 0;
            font-size: 1rem;
        }
        
        .help-section ul {
            color: #78350f;
            margin: 0;
            padding-left: 1.5rem;
        }
        
        .help-section li {
            margin: 0.5rem 0;
        }
        
        .contact-box {
            margin-top: 2rem;
            padding: 1.5rem;
            background: #f8fafc;
            border-radius: 12px;
            color: #64748b;
        }
    </style>
</head>
<body>
    <h1>🔐 Erreur de connexion</h1>
    
    <div class="error-container">
        <div class="error-icon">🚫</div>
        <h2 class="error-title">Échec de connexion</h2>
        <p class="error-message">
            <b>Erreur de connection !</b> Vous avez probablement entré de mauvais identifiants
        </p>
        
        <s:url action="redirectionLogin" var="redirectionLogin"></s:url>
        <s:a href="%{redirectionLogin}" style="display: inline-block; background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%); color: white; padding: 1rem 2rem; border-radius: 12px; font-weight: 600; text-decoration: none; margin: 1rem 0;">
            🔄 Cliquez ici pour revenir à l'écran de login
        </s:a>
        
        <div class="help-section">
            <h3>💡 Besoin d'aide ?</h3>
            <ul>
                <li>Vérifiez que vous avez correctement saisi votre code utilisateur</li>
                <li>Assurez-vous que la touche Majuscule n'est pas activée</li>
                <li>Si le problème persiste, veuillez contacter votre conseiller</li>
            </ul>
        </div>
        
        <div class="contact-box">
            <p style="margin: 0;">
                <span style="font-size: 1.5rem;">📞</span><br>
                <strong>Problème technique ?</strong><br>
                Contactez votre conseiller pour obtenir de l'aide
            </p>
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
