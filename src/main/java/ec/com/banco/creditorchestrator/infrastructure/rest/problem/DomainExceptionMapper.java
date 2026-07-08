package ec.com.banco.creditorchestrator.infrastructure.rest.problem;

import ec.com.banco.creditorchestrator.domain.exception.DomainException;
import ec.com.banco.creditorchestrator.infrastructure.rest.dto.ProblemResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {
    @Override
    public Response toResponse(DomainException exception) {
        return Response.status(422)
                .entity(ProblemResponse.of("Regla de dominio invalida", exception.getMessage(), 422))
                .build();
    }
}
