<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <title>Détail du Compte <s:property value="compte" /></title>
    <style>
        .detail-container {
            max-width: 1000px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .account-info {
            background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
            border-left: 4px solid #0ea5e9;
            padding: 2rem;
            border-radius: 12px;
            margin: 2rem auto;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 2rem;
            margin-top: 1rem;
        }
        
        .info-item {
            text-align: center;
        }
        
        .info-label {
            color: #64748b;
            font-size: 0.875rem;
            margin-bottom: 0.5rem;
        }
        
        .info-value {
            font-weight: 600;
            font-size: 1.25rem;
            color: #0f172a;
        }
        
        .operations-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin: 2rem 0;
        }
        
        .operation-card {
            background: white;
            border: 2px solid #e2e8f0;
            border-radius: 16px;
            padding: 2rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .card-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
            text-align: center;
        }
        
        .card-title {
            text-align: center;
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 1.5rem;
        }
    </style>
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

        <div class="account-info">
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Type de compte</div>
                    <div class="info-value">
                        <s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
                            🔓 Découvert autorisé
                        </s:if>
                        <s:else>
                            🔒 Compte simple
                        </s:else>
                    </div>
                </div>
                
                <div class="info-item">
                    <div class="info-label">Solde actuel</div>
                    <div class="info-value" style="font-size: 1.75rem;">
                        <s:if test="%{compte.solde >= 0}">
                            <span style="color: #10b981;"><s:property value="compte.solde" /> €</span>
                        </s:if>
                        <s:else>
                            <span class="soldeNegatif"><s:property value="compte.solde" /> €</span>
                        </s:else>
                    </div>
                </div>
                
                <s:if test="%{compte.className == \"CompteAvecDecouvert\"}">
                    <div class="info-item">
                        <div class="info-label">Découvert maximal autorisé</div>
                        <div class="info-value">
                            <s:property value="compte.decouvertAutorise" /> €
                        </div>
                    </div>
                </s:if>
            </div>
        </div>

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
