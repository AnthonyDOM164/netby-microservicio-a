package ec.com.banco.creditorchestrator.infrastructure.rest;

import ec.com.banco.creditorchestrator.application.port.in.EvaluateCreditUseCase;
import ec.com.banco.creditorchestrator.infrastructure.rest.dto.CreditEvaluationRequest;
import ec.com.banco.creditorchestrator.infrastructure.rest.dto.CreditEvaluationResponse;
import ec.com.banco.creditorchestrator.infrastructure.rest.dto.ProblemResponse;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ec.com.banco.creditorchestrator.application.port.in.ListCreditEvaluationsUseCase;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/v1/credit-evaluations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(
        name = "Evaluaciones de credito",
        description = "Orquesta la consulta de riesgo, aplica la politica de dominio y persiste la decision."
)
public class CreditEvaluationResource {
    private final EvaluateCreditUseCase useCase;
    private final ListCreditEvaluationsUseCase listUseCase;

    @Inject
    public CreditEvaluationResource(EvaluateCreditUseCase useCase, ListCreditEvaluationsUseCase listUseCase) {
        this.useCase = useCase;
        this.listUseCase = listUseCase;
    }

    @POST
    @Operation(
            summary = "Evaluar una solicitud de credito",
            description = """
                    Valida la cedula ecuatoriana, consulta el perfil de riesgo en el Microservicio B via gRPC,
                    aplica la regla de dominio y guarda cedula, monto, fecha y estado en PostgreSQL.
                    La decision es APROBADO solo si score > 70 y deuda mensual proyectada < 40% del salario.
                    """
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreditEvaluationRequest.class),
                    examples = @ExampleObject(
                            name = "Solicitud aprobable",
                            value = """
                                    {
                                      "cedula": "1710034065",
                                      "montoSolicitado": 1000.00,
                                      "tiempoAnios": 3,
                                      "salarioMensual": 4000.00
                                    }
                                    """
                    )
            )
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Evaluacion procesada y persistida.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = CreditEvaluationResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "JSON incompleto o con campos invalidos.",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))
            ),
            @APIResponse(
                    responseCode = "422",
                    description = "La cedula o el monto no cumplen reglas de dominio.",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))
            ),
            @APIResponse(
                    responseCode = "503",
                    description = "Microservicio B no disponible, con timeout o circuito abierto.",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    public Uni<Response> evaluate(@Valid CreditEvaluationRequest request) {
        return useCase.evaluate(request.toCommand())
                .onItem().transform(result -> Response.status(Response.Status.CREATED)
                        .entity(CreditEvaluationResponse.from(result))
                        .build());
    }

    @GET
    @Operation(
            summary = "Listar evaluaciones recientes",
            description = "Devuelve las ultimas evaluaciones persistidas en PostgreSQL, ordenadas por fecha descendente."
    )
    @APIResponse(
            responseCode = "200",
            description = "Evaluaciones recientes.",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = CreditEvaluationResponse.class)
            )
    )
    public Uni<List<CreditEvaluationResponse>> findRecent(
            @QueryParam("limit") @DefaultValue("20") int limit
    ) {
        return listUseCase.findRecent(limit)
                .onItem().transform(results -> results.stream()
                        .map(CreditEvaluationResponse::from)
                        .toList());
    }
}
