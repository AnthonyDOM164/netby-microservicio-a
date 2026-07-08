package ec.com.banco.creditorchestrator.infrastructure.rest.dto;

import ec.com.banco.creditorchestrator.application.port.in.CreditEvaluationResult;
import ec.com.banco.creditorchestrator.domain.model.CreditEvaluationStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(name = "CreditEvaluationResponse", description = "Resultado persistido de la evaluacion.")
public record CreditEvaluationResponse(
        @Schema(description = "Identificador unico de la evaluacion.", example = "d8d2d8a7-4aa7-4b5c-bf38-6ac0658af9fb")
        UUID evaluationId,

        @Schema(description = "Cedula evaluada.", example = "1710034065")
        String identityCard,

        @Schema(description = "Monto solicitado.", example = "1000.00")
        BigDecimal requestedAmount,

        @Schema(description = "Fecha UTC en la que se evaluo y persistio la solicitud.")
        OffsetDateTime evaluatedAt,

        @Schema(description = "Estado final de negocio.", example = "APROBADO")
        CreditEvaluationStatus status
) {
    public static CreditEvaluationResponse from(CreditEvaluationResult result) {
        return new CreditEvaluationResponse(
                result.evaluationId(),
                result.identityCard(),
                result.requestedAmount(),
                result.evaluatedAt(),
                result.status()
        );
    }
}
