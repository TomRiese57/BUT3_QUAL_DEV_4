<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Changer le mot de passe</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .password-container {
            max-width: 700px;
            margin: 2rem auto;
            background: white;
            padding: 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .welcome-box {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            border-left: 4px solid #3b82f6;
            padding: 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
        }
        
        .welcome-box b {
            color: #1e40af;
        }
        
        .form-table {
            width: 100%;
            margin: 1.5rem 0;
        }
        
        .form-table td {
            padding: 1rem 0.5rem;
            border: none;
        }
        
        .form-table tr {
            border: none;
        }
        
        .form-table th {
            background: transparent;
            color: #1e3a8a;
            text-align: left;
            padding: 1rem 0;
            font-weight: 600;
        }
        
        .security-tips {
            margin-top: 2rem;
            padding: 1.5rem;
            background: #f0f9ff;
            border-radius: 12px;
            border-left: 4px solid #0ea5e9;
        }
        
        .security-tips h3 {
            color: #0369a1;
            margin: 0 0 1rem 0;
            font-size: 1rem;
        }
        
        .security-tips ul {
            color: #0c4a6e;
            margin: 0;
            padding-left: 1.5rem;
            text-align: left;
        }
        
        .security-tips li {
            margin: 0.5rem 0;
        }
    </style>
</head>
<body>
    <div class="btnLogout">
        <s:form name="backForm" action="retourTableauDeBordClient" method="POST">
            <s:submit name="Retour" value="← Retour au tableau de bord" />
        </s:form>
    </div>

    <h1>🔑 Modification du mot de passe</h1>

    <div class="password-container">
        <s:if test="hasActionErrors()">
            <div class="errors">
                <s:actionerror />
            </div>
        </s:if>

        <s:if test="hasActionMessages()">
            <div class="messages">
                <s:actionmessage />
            </div>
        </s:if>

        <div class="welcome-box">
            <p style="margin: 0;">Utilisateur : <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b></p>
        </div>

        <s:form name="changePasswordForm" action="changePassword" method="POST">
            <table class="form-table">
                <thead>
                    <tr>
                        <th>Champ</th>
                        <th>Valeur</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <label for="oldPassword">Ancien mot de passe :</label>
                        </td>
                        <td>
                            <s:password name="oldPassword" id="oldPassword" required="true" 
                                       placeholder="Votre mot de passe actuel"
                                       style="width: 100%;" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="newPassword">Nouveau mot de passe :</label>
                        </td>
                        <td>
                            <s:password name="newPassword" id="newPassword" required="true" 
                                       placeholder="Votre nouveau mot de passe"
                                       style="width: 100%;" />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="confirmPassword">Confirmer le nouveau mot de passe :</label>
                        </td>
                        <td>
                            <s:password name="confirmPassword" id="confirmPassword" required="true" 
                                       placeholder="Confirmez votre mot de passe"
                                       style="width: 100%;" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" style="text-align: center; padding-top: 1.5rem;">
                            <s:submit value="🔒 Modifier le mot de passe" style="width: 100%; padding: 1rem;" />
                        </td>
                    </tr>
                </tbody>
            </table>
        </s:form>

        <div class="security-tips">
            <h3>🛡️ Conseils de sécurité</h3>
            <ul>
                <li>Utilisez au moins 8 caractères</li>
                <li>Combinez majuscules, minuscules, chiffres et symboles</li>
                <li>Évitez les mots courants ou informations personnelles</li>
                <li>Ne réutilisez pas vos anciens mots de passe</li>
            </ul>
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
