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
        .client-header {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            padding: 0.75rem 1.5rem;
            font-weight: 600;
            color: #1e40af;
        }
        
        .action-icons {
            display: flex;
            gap: 0.5rem;
            justify-content: center;
            align-items: center;
        }
        
        .action-icons img {
            width: 20px;
            height: 20px;
            opacity: 0.7;
            transition: opacity 0.2s ease;
        }
        
        .action-icons a:hover img {
            opacity: 1;
        }
        
        .icon-wrapper {
            display: inline-flex;
            padding: 0.5rem;
            border-radius: 0.5rem;
            transition: background-color 0.2s ease;
        }
        
        .icon-wrapper:hover {
            background-color: #f1f5f9;
        }
    </style>
</head>
<body>
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>
    
    <s:if test="aDecouvert">
        <h1>⚠️ Liste des comptes à découvert de la banque</h1>
    </s:if>
    <s:else>
        <h1>🏦 Liste des comptes de la banque</h1>
    </s:else>
    
    <div style="text-align: center; margin: 2rem 0;">
        <s:form name="myForm" action="retourTableauDeBordManager" method="POST">
            <s:submit name="Retour" value="← Retour" />
        </s:form>
    </div>

    <s:if test="aDecouvert">
        <p style="text-align: center; color: #1e3a8a; font-size: 1.25rem;">
            Voici les comptes à découvert de la banque :
        </p>
    </s:if>
    <s:else>
        <p style="text-align: center; color: #1e3a8a; font-size: 1.25rem;">
            Voici l'état des comptes de la banque :
        </p>
    </s:else>
    
    <table>
        <s:iterator value="allClients">
            <s:if test="(value.possedeComptesADecouvert() || !aDecouvert)">
                <tr class="client-header">
                    <th colspan="3" style="text-align: left;">
                        👤 <strong>Client :</strong> <s:property value="value.prenom" /> <s:property value="value.nom" /> 
                        (n°<s:property value="value.numeroClient" />)
                    </th>
                    <s:if test="(!aDecouvert)">
                        <th style="text-align: center;">
                            <s:url action="deleteUser" var="deleteUser">
                                <s:param name="client">
                                    <s:property value="value.userId" />
                                </s:param>
                            </s:url>
                            <s:a href="%{deleteUser}" 
                                 onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?')"
                                 cssClass="icon-wrapper">
                                <img src="https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/trash-.png"
                                     alt="Supprimer ce client"
                                     title="Supprimer ce client et tous ses comptes associés" />
                            </s:a>
                        </th>
                    </s:if>
                </tr>
                
                <s:iterator value="value.accounts">
                    <s:if test="(value.solde < 0 || !aDecouvert)">
                        <tr>
                            <td style="width: 25%;">🏦 <s:property value="key" /></td>
                            <td style="width: 25%;">
                                <s:if test="%{value.className == \"CompteAvecDecouvert\"}">
                                    <span style="background: #dbeafe; color: #1e40af; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem;">
                                        Découvert possible
                                    </span>
                                </s:if>
                                <s:else>
                                    <span style="background: #f1f5f9; color: #475569; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.875rem;">
                                        Simple
                                    </span>
                                </s:else>
                            </td>
                            <td style="width: 30%;">
                                <s:if test="%{value.solde >= 0}">
                                    <span style="color: #10b981; font-weight: 700;">
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
                                <td style="width: 20%;">
                                    <div class="action-icons">
                                        <s:url action="editAccount" var="editAccount">
                                            <s:param name="compte">
                                                <s:property value="value.numeroCompte" />
                                            </s:param>
                                        </s:url>
                                        <s:a href="%{editAccount}" cssClass="icon-wrapper">
                                            <img src="http://freeflaticons.com/wp-content/uploads/2014/10/write-copy-14138051958gn4k.png"
                                                 alt="Editer ce compte"
                                                 title="Editer ce compte" />
                                        </s:a>
                                        
                                        <s:url action="deleteAccount" var="deleteAccount">
                                            <s:param name="compte">
                                                <s:property value="value.numeroCompte" />
                                            </s:param>
                                            <s:param name="client">
                                                <s:property value="value.owner.userId" />
                                            </s:param>
                                        </s:url>
                                        <s:a href="%{deleteAccount}" 
                                             onclick="return confirm('Voulez-vous vraiment supprimer ce compte ?')"
                                             cssClass="icon-wrapper">
                                            <img src="https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/trash-.png"
                                                 alt="Supprimer ce compte"
                                                 title="Supprimer ce compte" />
                                        </s:a>
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
