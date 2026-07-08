package ec.com.banco.creditorchestrator.application.port.in;

import io.smallrye.mutiny.Uni;

public interface EvaluateCreditUseCase {
    Uni<CreditEvaluationResult> evaluate(CreditEvaluationCommand command);
}
