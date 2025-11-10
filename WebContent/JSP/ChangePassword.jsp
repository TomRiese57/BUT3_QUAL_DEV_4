<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Changer le mot de passe</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>
<body>
<div class="btnLogout">
    <s:form name="backForm" action="retourTableauDeBordClient" method="POST">
        <s:submit name="Retour" value="Retour au tableau de bord" />
    </s:form>
</div>

<h1>Modification du mot de passe</h1>

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

<p>Utilisateur : <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b></p>

<s:form name="changePasswordForm" action="changePassword" method="POST">
    <table>
        <thead>
        <tr>
            <th>Champ</th>
            <th>Valeur</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><label for="oldPassword">Ancien mot de passe :</label></td>
            <td><s:password name="oldPassword" id="oldPassword" required="true" /></td>
        </tr>
        <tr>
            <td><label for="newPassword">Nouveau mot de passe :</label></td>
            <td><s:password name="newPassword" id="newPassword" required="true" /></td>
        </tr>
        <tr>
            <td><label for="confirmPassword">Confirmer le nouveau mot de passe :</label></td>
            <td><s:password name="confirmPassword" id="confirmPassword" required="true" /></td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: center;">
                <s:submit value="Modifier le mot de passe" />
            </td>
        </tr>
        </tbody>
    </table>
</s:form>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>