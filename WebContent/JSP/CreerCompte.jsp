<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Formulaire de création d'un compte</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <script src="/_00_ASBank2023/js/jquery.js"></script>
    <script src="/_00_ASBank2023/js/jsCreerCompte.js"></script>
    <style>
        .form-container {
            max-width: 600px;
            margin: 2rem auto;
            background: white;
            padding: 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .client-info {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            border-left: 4px solid #3b82f6;
            padding: 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
        }
        
        .form-section {
            margin: 1.5rem 0;
        }
        
        .radio-group {
            display: flex;
            gap: 1rem;
            align-items: center;
            margin: 0.5rem 0;
        }
    </style>
</head>
<body>
    <h1>🏦 Créer un nouveau compte</h1>

    <div class="form-container">
        <div class="client-info">
            <p style="margin: 0;">
                <strong>Client choisi :</strong> <s:property value="client" />
            </p>
        </div>

        <s:form id="myForm" name="myForm" action="addAccount" method="POST">
            <input type="hidden" name="client" value="<s:property value='client.userId' />">
            
            <div class="form-section">
                <s:textfield label="Numéro de compte" name="numeroCompte" 
                            placeholder="Ex: 123456789"
                            style="width: 100%;" />
            </div>
            
            <div class="form-section">
                <label><input type="text" />Type de compte :</label>
                <s:radio label="" name="avecDecouvert"
                    list="#{false:'Compte sans découvert',true:'Compte avec découvert'}"
                    value="false" />
            </div>
            
            <div class="form-section">
                <s:textfield label="Découvert autorisé" name="decouvertAutorise" 
                            placeholder="Montant du découvert autorisé"
                            style="width: 100%;" />
            </div>
            
            <div style="margin-top: 2rem;">
                <s:submit name="submit" value="✓ Créer le compte" 
                         style="width: 100%; padding: 1rem;" />
            </div>
        </s:form>
        
        <div style="margin-top: 1rem;">
            <s:form name="myForm" action="listeCompteManager" method="POST">
                <s:submit name="Retour" value="← Retour" 
                         style="width: 100%; background: linear-gradient(135deg, #64748b 0%, #475569 100%);" />
            </s:form>
        </div>

        <s:if test="result"> 
            <s:if test="!error">
                <div class="success" style="margin-top: 2rem;">
                    <s:property value="message" />
                </div>
            </s:if>
            <s:else>
                <div class="failure" style="margin-top: 2rem;">
                    <s:property value="message" />
                </div>
            </s:else>
        </s:if>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
