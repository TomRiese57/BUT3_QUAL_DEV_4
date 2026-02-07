<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

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

    .operations-section {
        margin-top: 3rem;
    }

    .section-title {
        text-align: center;
        color: #1e3a8a;
        font-size: 1.5rem;
        font-weight: 600;
        margin-bottom: 2rem;
    }

    .operations-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 2rem;
        margin-top: 2rem;
    }

    .operation-card {
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
    }

    .operation-card:hover {
        border-color: #3b82f6;
        box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
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

    .card-title.credit {
        color: #10b981;
    }

    .card-title.debit {
        color: #ef4444;
    }

    .info-tip {
        margin-top: 2rem;
        padding: 1.5rem;
        background: #fef3c7;
        border-radius: 12px;
        border-left: 4px solid #f59e0b;
        color: #92400e;
    }
</style>

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