<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Tableau de bord</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .account-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 999px;
            font-size: 0.8125rem;
            font-weight: 500;
            border: 1px solid transparent;
        }
        .badge-overdraft {
            background: var(--blue-50);
            color: var(--blue-800);
            border-color: var(--blue-100);
        }
        .badge-simple {
            background: var(--gray-100);
            color: var(--gray-700);
            border-color: var(--gray-200);
        }
    </style>
</head>
<body>

    <!-- Barre de navigation : en haut, avant le h1 -->
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
        <s:form action="changePasswordPage" method="POST">
            <s:submit value="🔑 Changer mot de passe" />
        </s:form>
        <s:form action="mesCartes" method="POST">
            <s:submit value="💳 Mes cartes" />
        </s:form>
    </div>

    <h1>💼 Tableau de bord</h1>

    <div class="welcome-box">
        <p style="margin:0; font-size:1.0625rem;">
            Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b> !
        </p>
    </div>

    <p class="section-header">📊 Vos comptes bancaires</p>

    <table>
        <thead>
            <tr>
                <th>Numéro de compte</th>
                <th>Type de compte</th>
                <th>Solde actuel</th>
            </tr>
        </thead>
        <tbody>
            <s:iterator value="accounts">
                <tr>
                    <td>
                        <s:url action="urlDetail" var="urlDetail">
                            <s:param name="compte"><s:property value="key" /></s:param>
                        </s:url>
                        <s:a href="%{urlDetail}" style="font-weight:600;">
                            🏦 <s:property value="key" />
                        </s:a>
                    </td>
                    <td>
                        <s:if test="%{value.className == 'CompteAvecDecouvert'}">
                            <span class="account-badge badge-overdraft">Découvert possible</span>
                        </s:if>
                        <s:else>
                            <span class="account-badge badge-simple">Simple</span>
                        </s:else>
                    </td>
                    <td>
                        <s:if test="%{value.solde >= 0}">
                            <span style="color:var(--success);font-weight:700;font-size:1.0625rem;">
                                <s:property value="value.solde" /> €
                            </span>
                        </s:if>
                        <s:else>
                            <span class="soldeNegatif">
                                <s:property value="value.solde" /> €
                            </span>
                        </s:else>
                    </td>
                </tr>
            </s:iterator>
        </tbody>
    </table>

    <div class="info-tip" style="text-align:center;">
        💡 <strong>Astuce :</strong> Cliquez sur un numéro de compte pour voir les détails et effectuer des opérations
    </div>

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
