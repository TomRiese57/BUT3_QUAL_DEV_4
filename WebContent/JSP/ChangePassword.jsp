<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Changer le mot de passe</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .pw-container {
            max-width: 600px;
            margin: 2rem auto;
            background: var(--white);
            padding: 2.25rem 2.5rem;
            border-radius: var(--radius-xl);
            box-shadow: var(--shadow-xl);
            border: 1px solid var(--border);
        }

        .field-block {
            margin-bottom: 1.125rem;
        }

        .field-block label {
            display: block;
            font-size: 0.8125rem;
            font-weight: 600;
            color: var(--gray-700);
            text-transform: uppercase;
            letter-spacing: 0.04em;
            margin-bottom: 0.375rem;
        }

        .field-block input {
            width: 100%;
            padding: 0.625rem 0.875rem;
            border: 1.5px solid var(--gray-300);
            border-radius: var(--radius);
            font-size: 0.9375rem;
            font-family: inherit;
            background: var(--white);
            transition: border-color 0.18s, box-shadow 0.18s;
        }

        .field-block input:focus {
            outline: none;
            border-color: var(--blue-600);
            box-shadow: 0 0 0 3px rgba(37,99,235,.12);
        }

        .security-tips {
            margin-top: 1.5rem;
            padding: 1.125rem 1.375rem;
            background: var(--blue-50);
            border: 1px solid var(--blue-100);
            border-left: 4px solid var(--blue-600);
            border-radius: var(--radius-lg);
        }

        .security-tips h3 {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--blue-800);
            margin-bottom: 0.625rem;
        }

        .security-tips ul {
            padding-left: 1.25rem;
            color: var(--blue-900);
            font-size: 0.875rem;
        }

        .security-tips li { margin: 0.3rem 0; }
    </style>
</head>
<body>

    <div class="btnLogout">
        <s:form name="backForm" action="retourTableauDeBordClient" method="POST">
            <s:submit name="Retour" value="← Tableau de bord" />
        </s:form>
    </div>

    <h1>🔑 Modification du mot de passe</h1>

    <div class="pw-container">

        <s:if test="hasActionErrors()">
            <div class="errors"><s:actionerror /></div>
        </s:if>
        <s:if test="hasActionMessages()">
            <div class="messages"><s:actionmessage /></div>
        </s:if>

        <div class="welcome-box" style="margin-bottom:1.75rem;">
            <p style="margin:0;">
                Utilisateur : <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b>
            </p>
        </div>

        <s:form name="changePasswordForm" action="changePassword" method="POST" theme="simple">

            <div class="field-block">
                <label for="oldPassword">Ancien mot de passe</label>
                <input type="password" name="oldPassword" id="oldPassword"
                       required placeholder="Votre mot de passe actuel" />
            </div>

            <div class="field-block">
                <label for="newPassword">Nouveau mot de passe</label>
                <input type="password" name="newPassword" id="newPassword"
                       required placeholder="Votre nouveau mot de passe" />
            </div>

            <div class="field-block">
                <label for="confirmPassword">Confirmer le nouveau mot de passe</label>
                <input type="password" name="confirmPassword" id="confirmPassword"
                       required placeholder="Confirmez votre mot de passe" />
            </div>

            <div style="margin-top:1.5rem;">
                <input type="submit" value="🔒 Modifier le mot de passe" style="width:100%; padding:.75rem;" />
            </div>

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
