package ec.com.banco.creditorchestrator.infrastructure.rest.problem;

import ec.com.banco.creditorchestrator.application.port.out.RiskProfileUnavailableException;
import ec.com.banco.creditorchestrator.infrastructure.rest.dto.ProblemResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RiskProfileExceptionMapper implements ExceptionMapper<RiskProfileUnavailableException> {
    @Override
    public Response toResponse(RiskProfileUnavailableException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(ProblemResponse.of("Dependencia no disponible", exception.getMessage(), 503))
                .build();
    }
}
