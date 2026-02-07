<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Page de connexion</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        .login-container {
            max-width: 500px;
            margin: 3rem auto;
            background: white;
            padding: 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .login-header h1 {
            margin: 0;
            font-size: 1.75rem;
        }
        
        .login-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .security-note {
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
    <h1>🔐 Connexion à votre espace</h1>
    
    <div class="login-container">
        <div class="login-header">
            <div class="login-icon">🏦</div>
            <h2 style="color: #1e3a8a; margin: 0;">Identifiez-vous</h2>
        </div>
        
        <s:form name="myForm" action="controller.Connect.login.action" method="POST">
            <div class="form-group">
                <s:textfield label="Code utilisateur" name="userCde" 
                            placeholder="Votre code utilisateur" />
            </div>
            
            <div class="form-group">
                <s:password label="Mot de passe" name="userPwd" 
                           placeholder="Votre mot de passe" />
            </div>
            
            <div style="margin-top: 2rem;">
                <s:submit name="submit" value="Se connecter" 
                         style="width: 100%; padding: 1rem;" />
            </div>
        </s:form>
        
        <div style="margin-top: 1.5rem;">
            <s:form name="myFormRetour" action="retourAccueil" method="POST">
                <s:submit name="Retour" value="← Retour à l'accueil" 
                         style="width: 100%; background: linear-gradient(135deg, #64748b 0%, #475569 100%);" />
            </s:form>
        </div>
        
        <div class="security-note">
            🔒 Connexion sécurisée - Vos données sont protégées
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
