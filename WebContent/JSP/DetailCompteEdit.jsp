<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <title>Détail du Compte <s:property value="compte" /></title>
</head>
<body>
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>
    
    <h1>⚙️ Détail du compte <s:property value="compte" /></h1>
    
    <div class="detail-container">
        <div class="btnBack">
            <s:form name="myForm" action="listeCompteManager" method="POST">
                <s:submit name="Retour" value="← Retour" />
            </s:form>
        </div>

        <jsp:include page="/JSP/includes/InfosCompte.jsp" />

        <p style="text-align: center; color: #1e3a8a; font-size: 1.5rem; font-weight: 600; margin: 2rem 0;">
            💰 Opérations
        </p>

        <div class="operations-grid">
            <!-- Créditer -->
            <div class="operation-card">
                <div class="card-icon">💵</div>
                <h3 class="card-title" style="color: #10b981;">Créditer le compte</h3>
                <s:form name="formOperation" action="creditActionEdit" method="post">
                    <s:textfield label="Montant (€)" name="montant" 
                                placeholder="Ex: 100.00"
                                style="width: 100%; margin-bottom: 1rem;" />
                    <input type="hidden" name="compte" value="<s:property value='compte.numeroCompte' />">
                    <s:submit value="✓ Créditer" 
                             style="width: 100%; background: linear-gradient(135deg, #10b981 0%, #059669 100%);" />
                </s:form>
            </div>

            <!-- Débiter -->
            <div class="operation-card">
                <div class="card-icon">💸</div>
                <h3 class="card-title" style="color: #ef4444;">Débiter le compte</h3>
                <s:form name="formOperation" action="debitActionEdit" method="post">
                    <s:textfield label="Montant (€)" name="montant" 
                                placeholder="Ex: 50.00"
                                style="width: 100%; margin-bottom: 1rem;" />
                    <input type="hidden" name="compte" value="<s:property value='compte.numeroCompte' />">
                    <s:submit value="✓ Débiter" 
                             style="width: 100%; background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);" />
                </s:form>
            </div>

            <!-- Modifier découvert -->
            <s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
                <div class="operation-card">
                    <div class="card-icon">⚙️</div>
                    <h3 class="card-title" style="color: #3b82f6;">Modifier découvert autorisé</h3>
                    <s:form name="formChangeDecouvertAutorise" action="changerDecouvertAutoriseAction" method="post">
                        <input type="hidden" name="compte" value="<s:property value='compte.numeroCompte' />">
                        <s:textfield label="Découvert autorisé (€)" name="decouvertAutorise"
                                    value="%{compte.decouvertAutorise}"
                                    style="width: 100%; margin-bottom: 1rem;" />
                        <s:submit value="✓ Mettre à jour" style="width: 100%;" />
                    </s:form>
                </div>
            </s:if>
        </div>

        <s:if test="%{error != \"\"}">
            <div class="errors" style="margin-top: 2rem;">
                <strong>Erreur :</strong>
                <s:property value="error" />
            </div>
        </s:if>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
