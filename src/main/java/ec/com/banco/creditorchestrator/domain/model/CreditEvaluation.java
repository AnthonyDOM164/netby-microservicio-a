package ec.com.banco.creditorchestrator.domain.model;

import ec.com.banco.creditorchestrator.domain.exception.InvalidCreditAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

public record CreditEvaluation(
        UUID id,
        EcuadorianIdentityCard identityCard,
        BigDecimal requestedAmount,
        OffsetDateTime evaluatedAt,
        CreditEvaluationStatus status
) {
    public CreditEvaluation {
        id = id == null ? UUID.randomUUID() : id;
        identityCard = Objects.requireNonNull(identityCard, "identityCard is required");
        requestedAmount = normalizeAmount(requestedAmount);
        evaluatedAt = evaluatedAt == null ? OffsetDateTime.now(ZoneOffset.UTC) : evaluatedAt;
        status = Objects.requireNonNull(status, "status is required");
    }

    public static CreditEvaluation newEvaluation(
            EcuadorianIdentityCard identityCard,
            BigDecimal requestedAmount,
            CreditEvaluationStatus status
    ) {
        return new CreditEvaluation(null, identityCard, requestedAmount, null, status);
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidCreditAmountException(amount);
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
