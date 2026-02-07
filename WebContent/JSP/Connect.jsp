<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Tableau de bord</title>
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
        
        .section-header {
            text-align: center;
            color: #1e3a8a;
            font-size: 1.5rem;
            font-weight: 600;
            margin: 2rem 0 1.5rem 0;
        }
        
        .account-badge {
            padding: 0.375rem 0.875rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            font-weight: 500;
        }
        
        .badge-overdraft {
            background: #dbeafe;
            color: #1e40af;
        }
        
        .badge-simple {
            background: #f1f5f9;
            color: #475569;
        }
        
        .info-tip {
            margin: 2rem auto;
            padding: 1.5rem;
            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
            border-radius: 12px;
            max-width: 90%;
            text-align: center;
            color: #92400e;
        }
    </style>
</head>
<body>
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
        <s:form action="changePasswordPage" method="POST" style="display:inline; margin-left:10px;">
            <s:submit value="🔑 Changer mot de passe" />
        </s:form>
    </div>
    
    <h1>💼 Tableau de bord</h1>
    
    <div class="welcome-box">
        <p style="margin: 0; font-size: 1.125rem;">
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
                        <s:a href="%{urlDetail}" style="font-weight: 600;">
                            🏦 <s:property value="key" />
                        </s:a>
                    </td>
                    <td>
                        <s:if test="%{value.className == \"CompteAvecDecouvert\"}">
                            <span class="account-badge badge-overdraft">Découvert possible</span>
                        </s:if>
                        <s:else>
                            <span class="account-badge badge-simple">Simple</span>
                        </s:else>
                    </td>
                    <td>
                        <s:if test="%{value.solde >= 0}">
                            <span style="color: #10b981; font-weight: 700; font-size: 1.125rem;">
                                <s:property value="value.solde" /> €
                            </span>
                        </s:if>
                        <s:else>
                            <span class="soldeNegatif" style="font-size: 1.125rem;">
                                <s:property value="value.solde" /> €
                            </span>
                        </s:else>
                    </td>
                </tr>
            </s:iterator>
        </tbody>
    </table>
    
    <div class="info-tip">
        💡 <strong>Astuce :</strong> Cliquez sur un numéro de compte pour voir les détails et effectuer des opérations
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
