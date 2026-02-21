<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="aDecouvertTag" value="aDecouvert" />

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Liste des comptes de la banque</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .client-header-row th {
            background: linear-gradient(90deg, var(--blue-50), var(--white));
            color: var(--blue-800);
            font-size: 0.875rem;
            font-weight: 600;
            padding: 0.75rem 1.25rem;
            border-top: 2px solid var(--blue-100);
            border-bottom: 1px solid var(--blue-100);
            text-align: left;
        }

        /* Forcer la taille des icônes : on ne peut pas compter sur l'image externe */
        .tbl-icon {
            width: 20px;
            height: 20px;
            object-fit: contain;
            display: block;
            opacity: 0.7;
        }

        .tbl-icon:hover { opacity: 1; }

        .icon-btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 32px;
            height: 32px;
            border-radius: var(--radius-sm);
            transition: background var(--transition);
            text-decoration: none;
        }

        .icon-btn:hover {
            background: var(--gray-100);
            text-decoration: none;
        }

        .icons-cell {
            display: flex;
            gap: 0.25rem;
            justify-content: center;
            align-items: center;
        }

        .badge-type {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 999px;
            font-size: 0.8125rem;
            font-weight: 500;
        }

        .badge-decouvert {
            background: var(--blue-50);
            color: var(--blue-800);
            border: 1px solid var(--blue-100);
        }

        .badge-simple {
            background: var(--gray-100);
            color: var(--gray-700);
            border: 1px solid var(--gray-200);
        }

        .btn-center {
            display: flex;
            justify-content: center;
            margin: 1.5rem 0;
        }
        .btn-center form { margin: 0; }
    </style>
</head>
<body>

    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>

    <s:if test="aDecouvert">
        <h1>⚠️ Comptes à découvert</h1>
    </s:if>
    <s:else>
        <h1>🏦 Liste des comptes de la banque</h1>
    </s:else>

    <div class="btn-center">
        <s:form name="myForm" action="retourTableauDeBordManager" method="POST">
            <s:submit name="Retour" value="← Retour au tableau de bord" />
        </s:form>
    </div>

    <table>
        <s:iterator value="allClients">
            <s:if test="(value.possedeComptesADecouvert() || !aDecouvert)">

                <!-- En-tête client -->
                <tr class="client-header-row">
                    <th colspan="3" style="text-align:left;">
                        👤 <strong><s:property value="value.prenom" /> <s:property value="value.nom" /></strong>
                        &nbsp;<span style="font-weight:400;color:var(--gray-500);">(n°<s:property value="value.numeroClient" />)</span>
                    </th>
                    <s:if test="(!aDecouvert)">
                        <th style="text-align:center;">
                            <s:url action="deleteUser" var="deleteUser">
                                <s:param name="client"><s:property value="value.userId" /></s:param>
                            </s:url>
                            <a href="<s:property value='%{deleteUser}'/>"
                               class="icon-btn"
                               onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?')"
                               title="Supprimer ce client">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                                     stroke="#dc2626" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="3 6 5 6 21 6"/>
                                    <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                                    <path d="M10 11v6M14 11v6"/>
                                    <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                                </svg>
                            </a>
                        </th>
                    </s:if>
                </tr>

                <!-- Lignes des comptes -->
                <s:iterator value="value.accounts">
                    <s:if test="(value.solde < 0 || !aDecouvert)">
                        <tr>
                            <td style="width:28%; text-align:left; padding-left:2rem;">
                                🏦 <span style="font-family:'DM Mono',monospace;font-size:.9rem;">
                                    <s:property value="key" />
                                </span>
                            </td>
                            <td style="width:26%;">
                                <s:if test="%{value.className == 'CompteAvecDecouvert'}">
                                    <span class="badge-type badge-decouvert">Découvert possible</span>
                                </s:if>
                                <s:else>
                                    <span class="badge-type badge-simple">Simple</span>
                                </s:else>
                            </td>
                            <td style="width:26%;">
                                <s:if test="%{value.solde >= 0}">
                                    <span style="color:var(--success);font-weight:700;">
                                        <s:property value="value.solde" /> €
                                    </span>
                                </s:if>
                                <s:else>
                                    <span class="soldeNegatif">
                                        <s:property value="value.solde" /> €
                                    </span>
                                </s:else>
                            </td>
                            <s:if test="(!aDecouvert)">
                                <td style="width:20%;">
                                    <div class="icons-cell">
                                        <!-- Bouton éditer (SVG inline, pas de dépendance externe) -->
                                        <s:url action="editAccount" var="editAccount">
                                            <s:param name="compte"><s:property value="value.numeroCompte" /></s:param>
                                        </s:url>
                                        <a href="<s:property value='%{editAccount}'/>"
                                           class="icon-btn"
                                           title="Éditer ce compte">
                                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                                                 stroke="#374151" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                                            </svg>
                                        </a>

                                        <!-- Bouton supprimer -->
                                        <s:url action="deleteAccount" var="deleteAccount">
                                            <s:param name="compte"><s:property value="value.numeroCompte" /></s:param>
                                            <s:param name="client"><s:property value="value.owner.userId" /></s:param>
                                        </s:url>
                                        <a href="<s:property value='%{deleteAccount}'/>"
                                           class="icon-btn"
                                           onclick="return confirm('Voulez-vous vraiment supprimer ce compte ?')"
                                           title="Supprimer ce compte">
                                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
                                                 stroke="#dc2626" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                <polyline points="3 6 5 6 21 6"/>
                                                <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
                                                <path d="M10 11v6M14 11v6"/>
                                                <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
                                            </svg>
                                        </a>
                                    </div>
                                </td>
                            </s:if>
                        </tr>
                    </s:if>
                </s:iterator>

            </s:if>
        </s:iterator>
    </table>

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
