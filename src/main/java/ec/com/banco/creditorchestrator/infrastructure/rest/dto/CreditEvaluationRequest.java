package ec.com.banco.creditorchestrator.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import ec.com.banco.creditorchestrator.application.port.in.CreditEvaluationCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "CreditEvaluationRequest", description = "Solicitud de evaluacion de credito.")
public record CreditEvaluationRequest(
        @Schema(description = "Cedula ecuatoriana de persona natural.", example = "1710034065")
        @NotBlank
        @JsonAlias({"identityCard"})
        String cedula,

        @Schema(description = "Monto solicitado por el cliente.", example = "1000.00")
        @NotNull
        @DecimalMin(value = "0.01")
        @JsonAlias({"requestedAmount", "monto"})
        BigDecimal montoSolicitado,

        @Schema(description = "Plazo del credito en anios. No modifica la regla solicitada, pero queda en el contrato para la UI.", example = "3")
        @NotNull
        @Min(1)
        @JsonAlias({"termYears", "tiempoAnios"})
        Integer tiempoAnios,

        @Schema(description = "Salario mensual declarado por el cliente.", example = "4000.00")
        @NotNull
        @DecimalMin(value = "0.01")
        @JsonAlias({"monthlySalary", "salario"})
        BigDecimal salarioMensual
) {
    public CreditEvaluationCommand toCommand() {
        return new CreditEvaluationCommand(cedula, montoSolicitado, tiempoAnios, salarioMensual);
    }
}
