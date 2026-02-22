<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Réinitialisation du mot de passe</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>
<body>
    <h1>🔑 Réinitialisation du mot de passe</h1>

    <div class="login-container">

        <div class="login-header">
            <div class="login-icon">🔓</div>
            <h2>Nouveau mot de passe</h2>
        </div>

        <s:actionerror cssClass="errors" />
        <s:actionmessage cssClass="messages" />

        <s:form name="resetForm" action="resetPassword" method="POST" theme="simple">

            <div class="field-block" style="margin-bottom:1rem;">
                <label for="userCde">Code utilisateur</label>
                <s:textfield name="userCde" id="userCde"
                             placeholder="Votre code utilisateur"
                             cssStyle="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);" />
            </div>

            <div class="field-block" style="margin-bottom:1rem;">
                <label for="newPassword">Nouveau mot de passe</label>
                <s:password name="newPassword" id="newPassword"
                            placeholder="Minimum 6 caractères"
                            cssStyle="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);" />
            </div>

            <div class="field-block" style="margin-bottom:1.5rem;">
                <label for="confirmPassword">Confirmer le mot de passe</label>
                <s:password name="confirmPassword" id="confirmPassword"
                            placeholder="Confirmez votre mot de passe"
                            cssStyle="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);" />
            </div>

            <%-- name="btnReset" au lieu de name="submit" (mot réservé Struts → erreur "Error setting expression 'submit'") --%>
            <s:submit name="btnReset" value="Réinitialiser le mot de passe"
                      cssStyle="width:100%;padding:.75rem;" />

        </s:form>

        <div style="margin-top:0.75rem;">
            <s:form name="retourLogin" action="redirectionLogin" method="POST">
                <s:submit name="btnRetour" value="← Retour à la connexion"
                          cssStyle="width:100%;background:var(--gray-700);" />
            </s:form>
        </div>

        <div class="security-note">
            🔒 Saisissez votre code utilisateur et choisissez un nouveau mot de passe
        </div>

    </div>

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
