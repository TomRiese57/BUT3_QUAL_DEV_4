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
    
    <h1>💳 Détail du compte <s:property value="compte" /></h1>
    
    <div class="detail-container">
        <div class="btnBack">
            <s:form name="myForm" action="retourTableauDeBordClient" method="POST">
                <s:submit name="Retour" value="← Retour au tableau de bord" />
            </s:form>
        </div>

        <jsp:include page="/JSP/includes/InfosCompte.jsp" />

        <div class="operations-section">
            <div class="section-title">💰 Opérations</div>
            
            <div class="operations-grid">
                <!-- Créditer -->
                <div class="operation-card">
                    <div class="card-icon">💵</div>
                    <h3 class="card-title credit">Créditer le compte</h3>
                    <s:form name="formOperation" action="creditAction" method="post">
                        <s:textfield label="Montant (€)" name="montant" 
                                    placeholder="Ex: 100.00"
                                    style="width: 100%; margin-bottom: 1rem;" />
                        <input type="hidden" name="compte" value="<s:property value='compte' />">
                        <s:submit value="✓ Créditer" 
                                 style="width: 100%; background: linear-gradient(135deg, #10b981 0%, #059669 100%);" />
                    </s:form>
                </div>

                <!-- Débiter -->
                <div class="operation-card">
                    <div class="card-icon">💸</div>
                    <h3 class="card-title debit">Débiter le compte</h3>
                    <s:form name="formOperation" action="debitAction" method="post">
                        <s:textfield label="Montant (€)" name="montant" 
                                    placeholder="Ex: 50.00"
                                    style="width: 100%; margin-bottom: 1rem;" />
                        <input type="hidden" name="compte" value="<s:property value='compte' />">
                        <s:submit value="✓ Débiter" 
                                 style="width: 100%; background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);" />
                    </s:form>
                </div>
            </div>
        </div>

        <s:if test="%{error != \"\"}">
            <div class="errors" style="margin-top: 2rem;">
                <strong>Erreur :</strong>
                <s:property value="error" />
            </div>
        </s:if>

        <div class="info-tip">
            💡 <strong>Rappel :</strong> Les montants doivent être positifs. Assurez-vous de respecter votre découvert autorisé lors des débits.
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
