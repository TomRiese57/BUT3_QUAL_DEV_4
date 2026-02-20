<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Réinitialisation du mot de passe</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .reset-container {
            max-width: 500px;
            margin: 3rem auto;
            background: white;
            padding: 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .reset-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .reset-header h2 {
            color: #1e3a8a;
            margin: 0;
        }
        
        .reset-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .info-note {
            margin-top: 2rem;
            padding: 1rem;
            background: #f8fafc;
            border-radius: 12px;
            text-align: center;
            color: #64748b;
            font-size: 0.875rem;
        }
    </style>
</head>
<body>
    <h1>🔑 Réinitialisation du mot de passe</h1>
    
    <div class="reset-container">
        <div class="reset-header">
            <div class="reset-icon">🔓</div>
            <h2>Nouveau mot de passe</h2>
        </div>
        
        <s:actionerror cssClass="errors" />
        <s:actionmessage cssClass="messages" />
        
        <s:form name="resetForm" action="resetPassword" method="POST">
            <div class="form-group">
                <s:textfield label="Code utilisateur" name="userCde" 
                            placeholder="Votre code utilisateur" />
            </div>
            
            <div class="form-group">
                <s:password label="Nouveau mot de passe" name="newPassword" 
                           placeholder="Minimum 6 caractères" />
            </div>
            
            <div class="form-group">
                <s:password label="Confirmer le mot de passe" name="confirmPassword" 
                           placeholder="Confirmez votre mot de passe" />
            </div>
            
            <div style="margin-top: 2rem;">
                <s:submit name="submit" value="Réinitialiser le mot de passe" 
                         style="width: 100%; padding: 1rem;" />
            </div>
        </s:form>
        
        <div style="margin-top: 1.5rem;">
            <s:form name="retourLogin" action="redirectionLogin" method="POST">
                <s:submit name="Retour" value="← Retour à la connexion" 
                         style="width: 100%; background: linear-gradient(135deg, #64748b 0%, #475569 100%);" />
            </s:form>
        </div>
        
        <div class="info-note">
            🔒 Saisissez votre code utilisateur et choisissez un nouveau mot de passe
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
