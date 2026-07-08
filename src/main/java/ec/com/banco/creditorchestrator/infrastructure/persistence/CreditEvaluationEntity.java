package ec.com.banco.creditorchestrator.infrastructure.persistence;

import ec.com.banco.creditorchestrator.domain.model.CreditEvaluation;
import ec.com.banco.creditorchestrator.domain.model.CreditEvaluationStatus;
import ec.com.banco.creditorchestrator.domain.model.EcuadorianIdentityCard;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_evaluations")
public class CreditEvaluationEntity {
    @Id
    public UUID id;

    @Column(name = "identity_card", nullable = false, length = 10)
    public String identityCard;

    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    public BigDecimal requestedAmount;

    @Column(name = "evaluated_at", nullable = false)
    public OffsetDateTime evaluatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public CreditEvaluationStatus status;

    public static CreditEvaluationEntity fromDomain(CreditEvaluation evaluation) {
        var entity = new CreditEvaluationEntity();
        entity.id = evaluation.id();
        entity.identityCard = evaluation.identityCard().value();
        entity.requestedAmount = evaluation.requestedAmount();
        entity.evaluatedAt = evaluation.evaluatedAt();
        entity.status = evaluation.status();
        return entity;
    }

    public CreditEvaluation toDomain() {
        return new CreditEvaluation(
                id,
                new EcuadorianIdentityCard(identityCard),
                requestedAmount,
                evaluatedAt,
                status
        );
    }
}
