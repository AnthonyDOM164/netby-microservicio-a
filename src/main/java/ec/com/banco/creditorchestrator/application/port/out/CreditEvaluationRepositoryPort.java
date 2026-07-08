package ec.com.banco.creditorchestrator.application.port.out;

import ec.com.banco.creditorchestrator.domain.model.CreditEvaluation;

import java.util.List;

public interface CreditEvaluationRepositoryPort {
    CreditEvaluation save(CreditEvaluation evaluation);

    List<CreditEvaluation> findRecent(int limit);
}
