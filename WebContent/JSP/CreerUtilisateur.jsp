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
        /*
         * Struts2 s:radio génère un input[type=text] parasite avant les vrais radios.
         * On le cache sans casser les vraies saisies de texte du form.
         */
        #myForm > table input[type="text"][readonly],
        #myForm input[type="text"].struts-fake,
        .radio-wrapper input[type="text"] {
            display: none !important;
        }

        /* Conteneur du formulaire */
        .user-form-wrapper {
            background: var(--white);
            border: 1px solid var(--border);
            border-radius: var(--radius-xl);
            padding: 2rem 2.25rem;
            max-width: 660px;
            margin: 0 auto 2rem;
            box-shadow: var(--shadow-xl);
        }

        /* Grille 2 colonnes */
        .field-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem 1.5rem;
            margin-bottom: 1rem;
        }

        @media (max-width: 600px) {
            .field-grid { grid-template-columns: 1fr; }
        }

        .field-block {
            display: flex;
            flex-direction: column;
            gap: 0.3rem;
        }

        .field-block label {
            font-size: 0.8125rem;
            font-weight: 600;
            color: var(--gray-700);
            text-transform: uppercase;
            letter-spacing: 0.04em;
            margin: 0;
        }

        /* Radios en ligne */
        .radio-row {
            display: flex;
            flex-wrap: wrap;
            gap: 0.75rem 1.5rem;
            margin-top: 0.25rem;
        }

        .radio-row label {
            display: flex;
            align-items: center;
            gap: 0.4rem;
            font-size: 0.9375rem;
            font-weight: 400;
            text-transform: none;
            letter-spacing: 0;
            cursor: pointer;
            color: var(--text);
        }

        /* Séparateur section */
        .section-sep {
            border: none;
            border-top: 1px solid var(--border);
            margin: 1.25rem 0;
        }

        .btn-row {
            display: flex;
            gap: 0.75rem;
            margin-top: 1.5rem;
        }

        .btn-row input[type="submit"] {
            flex: 1;
        }

        .btn-secondary {
            background: var(--gray-700) !important;
        }
    </style>
</head>
<body>

    <div class="btnLogout">
        <s:form name="logoutForm" action="logout" method="POST">
            <s:submit name="Retour" value="🚪 Déconnexion" />
        </s:form>
    </div>

    <h1>👤 Créer un nouvel utilisateur</h1>

    <div class="user-form-wrapper">

        <s:if test="result == 'SUCCESS'">
            <div class="success" style="margin-bottom:1.25rem;">
                <s:property value="message" />
            </div>
        </s:if>
        <s:elseif test="result != null && result != ''">
            <div class="failure" style="margin-bottom:1.25rem;">
                <s:property value="message" />
            </div>
        </s:elseif>

        <s:form id="myForm" name="myForm" action="ajoutUtilisateur" method="POST" theme="simple">

            <div class="field-grid">
                <div class="field-block">
                    <label for="userId">Code utilisateur</label>
                    <input type="text" name="userId" id="userId"
                           placeholder="Ex : c.dupont4"
                           style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
                </div>
                <div class="field-block">
                    <label for="nom">Nom</label>
                    <input type="text" name="nom" id="nom"
                           placeholder="Nom de famille"
                           style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
                </div>
                <div class="field-block">
                    <label for="prenom">Prénom</label>
                    <input type="text" name="prenom" id="prenom"
                           placeholder="Prénom"
                           style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
                </div>
                <div class="field-block">
                    <label for="userPwd">Mot de passe</label>
                    <input type="password" name="userPwd" id="userPwd"
                           placeholder="Mot de passe"
                           style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
                </div>
            </div>

            <div class="field-block" style="margin-bottom:1rem;">
                <label for="adresse">Adresse</label>
                <input type="text" name="adresse" id="adresse"
                       placeholder="Adresse complète"
                       style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
            </div>

            <hr class="section-sep" />

            <!-- Sexe — radios HTML natifs pour éviter le bug Struts -->
            <div class="field-block" style="margin-bottom:1rem;">
                <span class="group-label">Sexe</span>
                <div class="radio-row">
                    <label>
                        <input type="radio" name="male" value="true" checked="checked" />
                        Homme
                    </label>
                    <label>
                        <input type="radio" name="male" value="false" />
                        Femme
                    </label>
                </div>
            </div>

            <!-- Type — radios HTML natifs -->
            <div class="field-block" style="margin-bottom:1rem;">
                <span class="group-label">Type d'utilisateur</span>
                <div class="radio-row">
                    <label>
                        <input type="radio" name="client" value="true" checked="checked" />
                        Client
                    </label>
                    <label>
                        <input type="radio" name="client" value="false" />
                        Manager
                    </label>
                </div>
            </div>

            <div class="field-block" style="margin-bottom:0.5rem;">
                <label for="numClient">Numéro de client</label>
                <input type="text" name="numClient" id="numClient"
                       placeholder="9 chiffres"
                       style="width:100%;padding:.625rem .875rem;border:1.5px solid var(--gray-300);border-radius:var(--radius);font-size:.9375rem;font-family:inherit;background:var(--white);"/>
            </div>

            <div class="btn-row">
                <input type="submit" name="submit" value="✓ Créer l'utilisateur" />
            </div>

        </s:form>

        <div style="margin-top:0.75rem;">
            <s:form name="retourForm" action="retourTableauDeBordManager" method="POST">
                <s:submit name="Retour" value="← Retour"
                          style="width:100%;background:var(--gray-700);" />
            </s:form>
        </div>

    </div>

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
