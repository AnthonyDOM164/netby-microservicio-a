package ec.com.banco.creditorchestrator.domain.service;

import ec.com.banco.creditorchestrator.domain.model.CreditEvaluationStatus;
import ec.com.banco.creditorchestrator.domain.model.RiskProfile;

import java.math.BigDecimal;
import java.util.Objects;

public class CreditEvaluationPolicy {
    private static final BigDecimal MAX_DEBT_RATIO = new BigDecimal("0.40");

    public CreditEvaluationStatus evaluate(
            RiskProfile profile,
            BigDecimal requestedAmount,
            BigDecimal monthlySalary
    ) {
        Objects.requireNonNull(profile, "profile is required");
        Objects.requireNonNull(requestedAmount, "requestedAmount is required");
        Objects.requireNonNull(monthlySalary, "monthlySalary is required");

        BigDecimal projectedDebt = profile.monthlyDebt().add(requestedAmount);
        BigDecimal maxAllowedDebt = monthlySalary.multiply(MAX_DEBT_RATIO);

        boolean approved = profile.score() > 70 && projectedDebt.compareTo(maxAllowedDebt) < 0;
        return approved ? CreditEvaluationStatus.APROBADO : CreditEvaluationStatus.RECHAZADO;
    }
}
