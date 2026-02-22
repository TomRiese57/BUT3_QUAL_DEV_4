<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Application IUT Bank</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <link href="/_00_ASBank2023/style/favicon.ico" rel="icon" type="image/x-icon" />
    <style>
        .index-container {
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 2rem;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .index-card {
            background: white;
            border-radius: 20px;
            padding: 3rem;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 600px;
            width: 100%;
            text-align: center;
        }
        
        .index-card h1 {
            background: none;
            color: #1e3a8a;
            font-size: 2.5rem;
            margin: 0 0 1rem 0;
            padding: 0;
            box-shadow: none;
        }
        
        .subtitle {
            color: #64748b;
            font-size: 1.125rem;
            margin-bottom: 2rem;
        }
        
        .logo-container {
            margin: 2rem 0;
        }
        
        .logo-container img {
            max-width: 250px;
            height: auto;
            transition: transform 0.3s ease;
        }
        
        .logo-container img:hover {
            transform: scale(1.05);
        }
        
        .action-buttons {
            margin-top: 2rem;
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .action-buttons a,
        .action-buttons input[type="button"] {
            padding: 1rem 2rem;
            font-size: 1.125rem;
            width: 100%;
            text-align: center;
        }
        
        .footer-info {
            margin-top: 2rem;
            padding-top: 2rem;
            border-top: 1px solid #e2e8f0;
            color: #94a3b8;
            font-size: 0.875rem;
        }
    </style>
    <script type="text/javascript">
        function DisplayMessage() {
            alert('Ce TD a été donné pour les AS dans le cadre du cours de CO Avancé (Promotion 2017-2018)');
        }
    </script>
</head>
<body>
    <div class="index-container">
        <div class="index-card">
            <h1>Bienvenue sur l'application IUT Bank 2026</h1>
            <p class="subtitle">Plateforme de gestion bancaire sécurisée</p>
            
            <div class="logo-container">
                <a href="https://www.univ-lorraine.fr/" target="_blank" rel="noopener noreferrer">
                    <img src="./style/logo_univ.png" alt="logo" />
                </a>
            </div>
            
            <div class="action-buttons">
                <s:url action="redirectionLogin" var="redirectionLogin"></s:url>
                <s:a href="%{redirectionLogin}" cssStyle="background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%); color: white; padding: 1rem 2rem; border-radius: 12px; font-weight: 600; text-decoration: none; display: block; transition: all 0.2s ease;">
                    🔐 Page de Login
                </s:a>
                
                <input type="button" value="ℹ️ Information" name="info" onClick="DisplayMessage()" />
            </div>
            
            <div class="footer-info">
                <p>Projet BUT-3A / 2025-2026</p>
            </div>
        </div>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
