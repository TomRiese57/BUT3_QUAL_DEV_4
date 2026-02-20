<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr" xml:lang="fr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Mes cartes bancaires</title>
    <link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
    <style>
        /* ── Mise en page principale ─────────────────────────────────── */
        .cards-page {
            max-width: 1100px;
            margin: 0 auto;
            padding: 0 1rem 3rem;
        }

        /* ── Bannière de bienvenue ───────────────────────────────────── */
        .welcome-box {
            background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
            border-left: 4px solid #3b82f6;
            padding: 1.25rem 1.75rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
        }
        .welcome-box b { color: #1e40af; }

        /* ── Section "Nouvelle carte" ────────────────────────────────── */
        .create-section {
            background: white;
            border: 2px dashed #3b82f6;
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2.5rem;
            box-shadow: 0 4px 6px rgba(0,0,0,.06);
        }
        .create-section h2 {
            color: #1e3a8a;
            font-size: 1.25rem;
            margin: 0 0 1.25rem;
            display: flex;
            align-items: center;
            gap: .5rem;
        }
        .create-form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            align-items: end;
        }
        .form-field label {
            display: block;
            font-size: .875rem;
            font-weight: 600;
            color: #374151;
            margin-bottom: .4rem;
        }
        .form-field select,
        .form-field input[type="text"],
        .form-field input[type="number"] {
            width: 100%;
            padding: .625rem .875rem;
            border: 1px solid #d1d5db;
            border-radius: 8px;
            font-size: .9375rem;
            background: #f9fafb;
        }
        .form-field select:focus,
        .form-field input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59,130,246,.15);
            background: white;
        }
        .btn-create {
            background: linear-gradient(135deg, #1e3a8a, #1e40af);
            color: white;
            border: none;
            padding: .75rem 1.5rem;
            border-radius: 10px;
            font-size: .9375rem;
            font-weight: 600;
            cursor: pointer;
            width: 100%;
            transition: all .2s;
        }
        .btn-create:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(30,58,138,.3); }

        /* ── Grille de cartes ────────────────────────────────────────── */
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
            gap: 2rem;
        }

        /* ── Carte physique ──────────────────────────────────────────── */
        .card-wrapper { display: flex; flex-direction: column; gap: 1rem; }

        .bank-card {
            width: 100%;
            aspect-ratio: 1.586; /* ratio ISO */
            border-radius: 18px;
            padding: 1.5rem 1.75rem;
            color: white;
            background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 60%, #3b82f6 100%);
            box-shadow: 0 8px 24px rgba(30,58,138,.4);
            position: relative;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform .25s, box-shadow .25s;
        }
        .bank-card:hover { transform: translateY(-4px); box-shadow: 0 14px 32px rgba(30,58,138,.45); }

        /* Carte bloquée → rouge */
        .bank-card.bloquee {
            background: linear-gradient(135deg, #7f1d1d 0%, #991b1b 60%, #dc2626 100%);
            box-shadow: 0 8px 24px rgba(127,29,29,.45);
        }
        .bank-card.bloquee:hover { box-shadow: 0 14px 32px rgba(127,29,29,.5); }

        /* Reflet décoratif */
        .bank-card::before {
            content: '';
            position: absolute; top: -40%; right: -20%;
            width: 200px; height: 200px;
            background: rgba(255,255,255,.08);
            border-radius: 50%;
        }

        .card-top { display: flex; justify-content: space-between; align-items: flex-start; }
        .card-bank-name { font-size: 1rem; font-weight: 700; opacity: .9; letter-spacing: .5px; }
        .card-badges { display: flex; gap: .4rem; flex-wrap: wrap; justify-content: flex-end; }

        .badge {
            padding: .25rem .65rem;
            border-radius: 999px;
            font-size: .7rem;
            font-weight: 700;
            letter-spacing: .4px;
        }
        .badge-bloquee  { background: rgba(255,255,255,.2);  color: white; border: 1px solid rgba(255,255,255,.5); }
        .badge-actif    { background: rgba(16,185,129,.25);  color: #a7f3d0; border: 1px solid rgba(16,185,129,.4); }
        .badge-differe  { background: rgba(139,92,246,.3);   color: #ddd6fe; border: 1px solid rgba(139,92,246,.5); }
        .badge-immediat { background: rgba(59,130,246,.3);   color: #bfdbfe; border: 1px solid rgba(59,130,246,.5); }
        .badge-expire   { background: rgba(245,158,11,.3);   color: #fde68a; border: 1px solid rgba(245,158,11,.5); }

        /* Puce */
        .card-chip {
            width: 42px; height: 32px;
            background: linear-gradient(135deg, #d4a017 0%, #f5c842 50%, #d4a017 100%);
            border-radius: 6px;
            margin: .5rem 0;
            box-shadow: inset 0 0 0 1px rgba(0,0,0,.15);
        }

        /* Numéro */
        .card-number {
            font-size: 1.35rem;
            letter-spacing: .18em;
            font-family: 'Courier New', monospace;
            text-shadow: 0 1px 3px rgba(0,0,0,.3);
        }

        /* Bas de carte */
        .card-bottom {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
        }
        .card-account { font-size: .75rem; opacity: .7; }
        .card-expiry  { text-align: right; }
        .card-expiry .label { font-size: .65rem; opacity: .7; text-transform: uppercase; letter-spacing: .05em; }
        .card-expiry .value { font-size: .9rem; font-weight: 600; }

        /* ── Panneau d'infos & actions ───────────────────────────────── */
        .card-panel {
            background: white;
            border: 1px solid #e2e8f0;
            border-radius: 14px;
            padding: 1.25rem 1.5rem;
            box-shadow: 0 2px 8px rgba(0,0,0,.06);
        }

        /* Barres de progression */
        .limit-row { margin-bottom: .85rem; }
        .limit-label {
            display: flex; justify-content: space-between;
            font-size: .8rem; font-weight: 600; color: #374151;
            margin-bottom: .3rem;
        }
        .progress-bar {
            height: 8px; background: #f1f5f9; border-radius: 999px; overflow: hidden;
        }
        .progress-fill {
            height: 100%; border-radius: 999px;
            transition: width .4s ease;
        }
        .fill-ok     { background: linear-gradient(90deg, #10b981, #34d399); }
        .fill-warn   { background: linear-gradient(90deg, #f59e0b, #fbbf24); }
        .fill-danger { background: linear-gradient(90deg, #ef4444, #f87171); }

        /* Montant différé */
        .differe-info {
            margin-top: .5rem; margin-bottom: .85rem;
            background: #f5f3ff; border-left: 3px solid #8b5cf6;
            border-radius: 6px; padding: .5rem .75rem;
            font-size: .8rem; color: #5b21b6; font-weight: 500;
        }

        /* Boutons d'action */
        .actions-row {
            display: flex; flex-wrap: wrap; gap: .5rem;
            margin-top: 1rem;
        }
        .btn-action {
            flex: 1 1 auto;
            padding: .5rem .75rem;
            border: none; border-radius: 8px;
            font-size: .8rem; font-weight: 600;
            cursor: pointer; text-align: center;
            transition: all .2s; color: white;
        }
        .btn-block   { background: #dc2626; }
        .btn-unblock { background: #10b981; }
        .btn-mode    { background: #7c3aed; }
        .btn-action:hover { transform: translateY(-1px); filter: brightness(1.1); }

        /* Formulaires inline (plafonds) */
        .inline-form {
            display: flex; gap: .4rem; align-items: center;
            margin-top: .4rem;
        }
        .inline-form input[type="number"] {
            flex: 1; padding: .4rem .6rem;
            border: 1px solid #d1d5db; border-radius: 7px;
            font-size: .85rem; min-width: 0;
        }
        .btn-small {
            padding: .4rem .75rem;
            background: #1e3a8a; color: white;
            border: none; border-radius: 7px;
            font-size: .8rem; font-weight: 600; cursor: pointer;
            white-space: nowrap; transition: all .15s;
        }
        .btn-small:hover { background: #1e40af; }

        .plafond-section { margin-top: .9rem; }
        .plafond-section h4 {
            font-size: .8rem; font-weight: 700; color: #6b7280;
            text-transform: uppercase; letter-spacing: .05em;
            margin: 0 0 .5rem;
        }

        /* Aucune carte */
        .empty-state {
            text-align: center; padding: 3rem 1rem;
            background: white; border-radius: 16px;
            border: 2px dashed #e2e8f0;
            color: #9ca3af;
        }
        .empty-state .empty-icon { font-size: 4rem; margin-bottom: 1rem; }
        .empty-state p { font-size: 1rem; margin: 0; }
    </style>
</head>
<body>

    <!-- ── Barre de navigation ── -->
    <div class="btnLogout">
        <s:form name="backForm" action="retourTableauDeBordClient" method="POST">
            <s:submit value="← Tableau de bord" />
        </s:form>
        <s:form name="logoutForm" action="logout" method="POST">
            <s:submit value="🚪 Déconnexion" />
        </s:form>
    </div>

    <h1>💳 Mes cartes bancaires</h1>

    <div class="cards-page">

        <!-- ── Messages / Erreurs ── -->
        <s:if test="hasActionErrors()">
            <div class="errors"><s:actionerror /></div>
        </s:if>
        <s:if test="hasActionMessages()">
            <div class="messages"><s:actionmessage /></div>
        </s:if>

        <!-- ── Bienvenue ── -->
        <div class="welcome-box">
            <p style="margin:0;">
                Bonjour <b><s:property value="connectedUser.prenom" /> <s:property value="connectedUser.nom" /></b> —
                gérez ici vos cartes bancaires.
            </p>
        </div>

        <!-- ══════════════════════════════════════════════════════════ -->
        <!-- Section : Créer une nouvelle carte                       -->
        <!-- ══════════════════════════════════════════════════════════ -->
        <div class="create-section">
            <h2>➕ Commander une nouvelle carte</h2>
            <s:form action="carteCreer" method="POST" theme="simple">
                <div class="create-form-grid">

                    <div class="form-field">
                        <label for="numeroCompte">Compte bancaire</label>
                        <select name="numeroCompte" id="numeroCompte">
                            <s:iterator value="connectedUser.accounts">
                                <option value="<s:property value='key' />">
                                    <s:property value="key" />
                                    (solde&nbsp;: <s:property value="value.solde" />&nbsp;€)
                                </option>
                            </s:iterator>
                        </select>
                    </div>

                    <div class="form-field">
                        <label for="paiementDiffere">Mode de paiement</label>
                        <select name="paiementDiffere" id="paiementDiffere">
                            <option value="false">Immédiat</option>
                            <option value="true">Différé (fin de mois)</option>
                        </select>
                    </div>

                    <div class="form-field">
                        <label for="nouveauPlafondPaiement">Plafond paiements (€)</label>
                        <input type="number" name="nouveauPlafondPaiement" id="nouveauPlafondPaiement"
                               value="3000" min="1" step="100" />
                    </div>

                    <div class="form-field">
                        <label for="nouveauPlafondRetrait">Plafond retraits (€)</label>
                        <input type="number" name="nouveauPlafondRetrait" id="nouveauPlafondRetrait"
                               value="1000" min="1" step="100" />
                    </div>

                    <div class="form-field">
                        <label>&nbsp;</label>
                        <button type="submit" class="btn-create">🆕 Commander</button>
                    </div>
                </div>
            </s:form>
        </div>

        <!-- ══════════════════════════════════════════════════════════ -->
        <!-- Grille des cartes existantes                             -->
        <!-- ══════════════════════════════════════════════════════════ -->
        <s:if test="cartes.isEmpty()">
            <div class="empty-state">
                <div class="empty-icon">💳</div>
                <p>Vous n'avez pas encore de carte bancaire.<br>Commandez-en une ci-dessus !</p>
            </div>
        </s:if>
        <s:else>
            <div class="cards-grid">
                <s:iterator value="cartes" var="c">
                    <div class="card-wrapper">

                        <!-- ─── Visuel carte ─── -->
                        <div class="bank-card <s:if test='#c.bloquee'>bloquee</s:if>">
                            <div class="card-top">
                                <span class="card-bank-name">IUT Bank</span>
                                <div class="card-badges">
                                    <s:if test="#c.estExpiree()">
                                        <span class="badge badge-expire">EXPIRÉE</span>
                                    </s:if>
                                    <s:if test="#c.bloquee">
                                        <span class="badge badge-bloquee">BLOQUÉE</span>
                                    </s:if>
                                    <s:else>
                                        <span class="badge badge-actif">ACTIVE</span>
                                    </s:else>
                                    <s:if test="#c.paiementDiffere">
                                        <span class="badge badge-differe">DIFFÉRÉ</span>
                                    </s:if>
                                    <s:else>
                                        <span class="badge badge-immediat">IMMÉDIAT</span>
                                    </s:else>
                                </div>
                            </div>

                            <div class="card-chip"></div>

                            <div class="card-number">
                                <s:property value="#c.numeroCarte.substring(0,4)" />&#160;
                                ****&#160;
                                ****&#160;
                                <s:property value="#c.numeroCarte.substring(12)" />
                            </div>

                            <div class="card-bottom">
                                <div class="card-account">
                                    Compte : <s:property value="#c.compte.numeroCompte" />
                                </div>
                                <div class="card-expiry">
                                    <div class="label">Expire le</div>
                                    <div class="value"><s:property value="#c.dateExpiration" /></div>
                                </div>
                            </div>
                        </div><!-- /bank-card -->

                        <!-- ─── Panneau d'infos & actions ─── -->
                        <div class="card-panel">

                            <%-- Barre : paiements du mois --%>
                            <div class="limit-row">
                                <div class="limit-label">
                                    <span>Paiements ce mois</span>
                                    <span>
                                        <s:property value="#c.paiementsMoisCourant" /> €
                                        / <s:property value="#c.plafondPaiement" /> €
                                    </span>
                                </div>
                                <div class="progress-bar">
                                    <%-- Calcul du pourcentage via OGNL : on limite à 100 --%>
                                    <s:set var="pctP"
                                           value="#c.plafondPaiement > 0
                                                  ? (#c.paiementsMoisCourant * 100 / #c.plafondPaiement)
                                                  : 0" />
                                    <div class="progress-fill
                                        <s:if test='#pctP >= 90'>fill-danger</s:if>
                                        <s:elseif test='#pctP >= 70'>fill-warn</s:elseif>
                                        <s:else>fill-ok</s:else>"
                                         style="width: <s:if test='#pctP > 100'>100</s:if><s:else><s:property value='#pctP'/></s:else>%;">
                                    </div>
                                </div>
                            </div>

                            <%-- Barre : retraits du mois --%>
                            <div class="limit-row">
                                <div class="limit-label">
                                    <span>Retraits ce mois</span>
                                    <span>
                                        <s:property value="#c.retraitsMoisCourant" /> €
                                        / <s:property value="#c.plafondRetrait" /> €
                                    </span>
                                </div>
                                <div class="progress-bar">
                                    <s:set var="pctR"
                                           value="#c.plafondRetrait > 0
                                                  ? (#c.retraitsMoisCourant * 100 / #c.plafondRetrait)
                                                  : 0" />
                                    <div class="progress-fill
                                        <s:if test='#pctR >= 90'>fill-danger</s:if>
                                        <s:elseif test='#pctR >= 70'>fill-warn</s:elseif>
                                        <s:else>fill-ok</s:else>"
                                         style="width: <s:if test='#pctR > 100'>100</s:if><s:else><s:property value='#pctR'/></s:else>%;">
                                    </div>
                                </div>
                            </div>

                            <%-- Montant différé en attente --%>
                            <s:if test="#c.paiementDiffere && #c.montantDiffereEnAttente > 0">
                                <div class="differe-info">
                                    💜 Débit différé en attente :
                                    <strong><s:property value="#c.montantDiffereEnAttente" /> €</strong>
                                    (prélevé en fin de mois)
                                </div>
                            </s:if>

                            <!-- ─ Boutons bloquer / débloquer / mode ─ -->
                            <div class="actions-row">
                                <s:if test="#c.bloquee">
                                    <s:url action="carteDebloquer" var="urlDebloquer">
                                        <s:param name="numeroCarte"><s:property value="#c.numeroCarte" /></s:param>
                                    </s:url>
                                    <a href="<s:property value='#urlDebloquer'/>" class="btn-action btn-unblock">
                                        🔓 Débloquer
                                    </a>
                                </s:if>
                                <s:else>
                                    <s:url action="carteBloquer" var="urlBloquer">
                                        <s:param name="numeroCarte"><s:property value="#c.numeroCarte" /></s:param>
                                    </s:url>
                                    <a href="<s:property value='#urlBloquer'/>"
                                       class="btn-action btn-block"
                                       onclick="return confirm('Bloquer cette carte ?')">
                                        🔒 Bloquer
                                    </a>
                                </s:else>

                                <s:url action="carteBasculerMode" var="urlMode">
                                    <s:param name="numeroCarte"><s:property value="#c.numeroCarte" /></s:param>
                                </s:url>
                                <a href="<s:property value='#urlMode'/>" class="btn-action btn-mode">
                                    🔄 <s:if test="#c.paiementDiffere">→ Immédiat</s:if><s:else>→ Différé</s:else>
                                </a>
                            </div>

                            <!-- ─ Modifier les plafonds ─ -->
                            <div class="plafond-section">
                                <h4>Modifier les plafonds</h4>

                                <s:form action="carteModifierPlafondPaiement" method="POST" theme="simple">
                                    <input type="hidden" name="numeroCarte"
                                           value="<s:property value='#c.numeroCarte' />" />
                                    <div class="inline-form">
                                        <label style="font-size:.8rem;white-space:nowrap;">💳 Paiement</label>
                                        <input type="number" name="nouveauPlafondPaiement"
                                               value="<s:property value='#c.plafondPaiement' />"
                                               min="1" step="100" />
                                        <button type="submit" class="btn-small">OK</button>
                                    </div>
                                </s:form>

                                <s:form action="carteModifierPlafondRetrait" method="POST" theme="simple">
                                    <input type="hidden" name="numeroCarte"
                                           value="<s:property value='#c.numeroCarte' />" />
                                    <div class="inline-form">
                                        <label style="font-size:.8rem;white-space:nowrap;">🏧 Retrait</label>
                                        <input type="number" name="nouveauPlafondRetrait"
                                               value="<s:property value='#c.plafondRetrait' />"
                                               min="1" step="100" />
                                        <button type="submit" class="btn-small">OK</button>
                                    </div>
                                </s:form>
                            </div>

                        </div><!-- /card-panel -->
                    </div><!-- /card-wrapper -->
                </s:iterator>
            </div><!-- /cards-grid -->
        </s:else>

    </div><!-- /cards-page -->

    <jsp:include page="/JSP/Footer.jsp" />
</body>
</html>
