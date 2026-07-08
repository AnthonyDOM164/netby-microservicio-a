package ec.com.banco.creditorchestrator.infrastructure.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(name = "ProblemResponse", description = "Formato simple de error para consumidores REST.")
public record ProblemResponse(
        @Schema(example = "Solicitud invalida")
        String title,

        @Schema(example = "La cedula ecuatoriana no es valida: 1234567890")
        String detail,

        @Schema(example = "422")
        int status,

        OffsetDateTime timestamp
) {
    public static ProblemResponse of(String title, String detail, int status) {
        return new ProblemResponse(title, detail, status, OffsetDateTime.now());
    }
}
