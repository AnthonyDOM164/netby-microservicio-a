package ec.com.banco.creditorchestrator.infrastructure.rest.problem;

import ec.com.banco.creditorchestrator.infrastructure.rest.dto.ProblemResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceException;

@Provider
public class FaultToleranceExceptionMapper implements ExceptionMapper<FaultToleranceException> {
    @Override
    public Response toResponse(FaultToleranceException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(ProblemResponse.of(
                        "Dependencia no disponible",
                        "El Microservicio B no respondio a tiempo o el circuito esta abierto.",
                        503
                ))
                .build();
    }
}
