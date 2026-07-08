package ec.com.banco.creditorchestrator.application.port.in;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ListCreditEvaluationsUseCase {
    Uni<List<CreditEvaluationResult>> findRecent(int limit);
}
