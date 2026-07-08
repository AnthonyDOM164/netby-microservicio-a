package ec.com.banco.creditorchestrator.application.port.in;

import ec.com.banco.creditorchestrator.domain.model.CreditEvaluation;
import ec.com.banco.creditorchestrator.domain.model.CreditEvaluationStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreditEvaluationResult(
        UUID evaluationId,
        String identityCard,
        BigDecimal requestedAmount,
        OffsetDateTime evaluatedAt,
        CreditEvaluationStatus status
) {
    public static CreditEvaluationResult fromDomain(CreditEvaluation evaluation) {
        return new CreditEvaluationResult(
                evaluation.id(),
                evaluation.identityCard().value(),
                evaluation.requestedAmount(),
                evaluation.evaluatedAt(),
                evaluation.status()
        );
    }
}
