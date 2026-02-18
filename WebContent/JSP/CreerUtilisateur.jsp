<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Formulaire de création d'utilisateur</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <script src="/_00_ASBank2023/js/jquery.js"></script>
    <script src="/_00_ASBank2023/js/jsCreerUtilisateur.js"></script>
    <style>
        .form-container {
            max-width: 700px;
            margin: 2rem auto;
            background: white;
            padding: 2.5rem;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .form-section {
            margin: 1.5rem 0;
        }
        
        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }
        
        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="btnLogout">
        <s:form name="myForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>
    
    <h1>👤 Créer un nouvel utilisateur</h1>
    
    <div class="form-container">
        <s:form id="myForm" name="myForm" action="ajoutUtilisateur" method="POST">
            <div class="form-grid">
                <div class="form-section">
                    <s:textfield label="Code utilisateur" name="userId" 
                                placeholder="Numéro unique (exemple : c.dupont4)"
                                style="width: 100%;" />
                </div>
                
                <div class="form-section">
                    <s:textfield label="Nom" name="nom" 
                                placeholder="Nom de famille"
                                style="width: 100%;" />
                </div>
                
                <div class="form-section">
                    <s:textfield label="Prénom" name="prenom" 
                                placeholder="Prénom"
                                style="width: 100%;" />
                </div>
                
                <div class="form-section">
                    <s:password label="Mot de passe" name="userPwd" 
                               placeholder="Mot de passe"
                               style="width: 100%;" />
                </div>
            </div>
            
            <div class="form-section">
                <s:textfield label="Adresse" name="adresse" 
                            placeholder="Adresse complète"
                            style="width: 100%;" />
            </div>
            
            <div class="form-section">
                <label><input type="text" />Sexe :</label>
                <s:radio label="" name="male" 
                        list="#{true:'Homme',false:'Femme'}"
                        value="true" />
            </div>
            
            <div class="form-section">
                <label><input type="text" />Type :</label>
                <s:radio label="" name="client"
                        list="#{true:'Client',false:'Manager'}" 
                        value="true" />
            </div>
            
            <div class="form-section">
                <s:textfield label="Numéro de client" name="numClient" 
                            placeholder="Numéro de client (9 chiffres)"
                            style="width: 100%;" />
            </div>
            
            <div style="margin-top: 2rem;">
                <s:submit name="submit" value="✓ Créer l'utilisateur" 
                         style="width: 100%; padding: 1rem;" />
            </div>
        </s:form>
        
        <div style="margin-top: 1rem;">
            <s:form name="myForm" action="retourTableauDeBordManager" method="POST">
                <s:submit name="Retour" value="← Retour" 
                         style="width: 100%; background: linear-gradient(135deg, #64748b 0%, #475569 100%);" />
            </s:form>
        </div>

        <s:if test="(result == \"SUCCESS\")">
            <div class="success" style="margin-top: 2rem;">
                <s:property value="message" />
            </div>
        </s:if>
        <s:else>
            <div class="failure" style="margin-top: 2rem;">
                <s:property value="message" />
            </div>
        </s:else>
    </div>
    
    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
