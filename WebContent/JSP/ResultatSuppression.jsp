<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr" xml:lang="fr">
<head>
    <title>Résultat de la suppression</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .result-container {
            max-width: 700px;
            margin: 3rem auto;
            background: white;
            padding: 3rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .result-icon {
            font-size: 5rem;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        
        .result-title {
            font-size: 1.75rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        
        .result-message {
            font-size: 1.125rem;
            margin-bottom: 2rem;
            text-align: center;
            line-height: 1.8;
        }
        
        .account-list {
            background: #fef2f2;
            padding: 1.5rem;
            border-radius: 12px;
            border-left: 4px solid #ef4444;
            margin: 1.5rem 0;
        }
        
        .account-list ul {
            list-style: none;
            padding: 0;
            margin: 0.5rem 0 0 0;
        }
        
        .account-list li {
            padding: 0.5rem 0;
            color: #991b1b;
        }
    </style>
</head>
<body>
    <h1>📋 Résultat de la suppression</h1>

    <div class="result-container">
        <s:if test="!error">
            <div class="result-icon">✅</div>
            <h2 class="result-title" style="color: #10b981;">Suppression réussie</h2>
            <div class="result-message">
                <s:if test="isAccount()">
                    <div class="success">
                        Le compte <strong><s:property value="compteInfo"/></strong> du client 
                        <strong><s:property value="client" /></strong> a bien été supprimé.
                    </div>
                </s:if>
                <s:else>
                    <div class="success">
                        Le client <strong><s:property value="userInfo"/></strong> a bien été supprimé.
                    </div>
                </s:else>
            </div>
        </s:if>
        <s:else>
            <div class="result-icon">❌</div>
            <h2 class="result-title" style="color: #ef4444;">Erreur de suppression</h2>
            <div class="failure">
                <s:if test="%{errorMessage == \"NONEMPTYACCOUNT\"}">
                    <s:if test="isAccount()">
                        <p>
                            Une erreur est survenue lors de la suppression du compte 
                            <strong><s:property value="compte" /></strong> du client 
                            <strong><s:property value="client" /></strong>.
                        </p>
                        <div class="account-list">
                            <strong>⚠️ Problème :</strong> Le compte a un solde différent de zéro :
                            <strong style="color: #991b1b; font-size: 1.25rem;">
                                <s:property value="compte.solde" /> €
                            </strong>
                        </div>
                    </s:if>
                    <s:else>
                        <p>
                            Une erreur est survenue lors de la suppression du client 
                            <strong><s:property value="client" /></strong>.
                        </p>
                        <div class="account-list">
                            <strong>⚠️ Problème :</strong> Les comptes suivants ont un solde différent de zéro :
                            <ul>
                                <s:iterator value="client.comptesAvecSoldeNonNul">
                                    <li>
                                        🏦 <strong><s:property value="value" /></strong> 
                                        (<span style="font-weight: 700;"><s:property value="value.solde" /> €</span>)
                                    </li>	
                                </s:iterator>
                            </ul>
                        </div>
                    </s:else>
                </s:if>
                <s:else>
                    <s:if test="isAccount()">
                        <p>
                            Une erreur est survenue lors de la suppression du compte 
                            <strong><s:property value="compte" /></strong> du client 
                            <strong><s:property value="client" /></strong>.
                        </p>
                        <p>Veuillez réessayer.</p>
                    </s:if>
                    <s:else>
                        <p>
                            Une erreur est survenue lors de la suppression du client 
                            <strong><s:property value="client" /></strong>.
                        </p>
                        <p>Veuillez réessayer.</p>
                    </s:else>
                </s:else>
            </div>
        </s:else>
        
        <div style="margin-top: 2rem; text-align: center;">
            <s:form name="myForm" action="listeCompteManager" method="POST">
                <s:submit name="Retour" value="← Retour à la liste" 
                         style="width: 100%; padding: 1rem;" />
            </s:form>
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
