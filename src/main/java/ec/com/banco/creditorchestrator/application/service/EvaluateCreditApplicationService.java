package ec.com.banco.creditorchestrator.application.service;

import ec.com.banco.creditorchestrator.application.port.in.CreditEvaluationCommand;
import ec.com.banco.creditorchestrator.application.port.in.CreditEvaluationResult;
import ec.com.banco.creditorchestrator.application.port.in.EvaluateCreditUseCase;
import ec.com.banco.creditorchestrator.application.port.in.ListCreditEvaluationsUseCase;
import ec.com.banco.creditorchestrator.application.port.out.CreditEvaluationRepositoryPort;
import ec.com.banco.creditorchestrator.application.port.out.RiskProfilePort;
import ec.com.banco.creditorchestrator.domain.model.CreditEvaluation;
import ec.com.banco.creditorchestrator.domain.model.EcuadorianIdentityCard;
import ec.com.banco.creditorchestrator.domain.model.RiskProfile;
import ec.com.banco.creditorchestrator.domain.service.CreditEvaluationPolicy;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EvaluateCreditApplicationService implements EvaluateCreditUseCase, ListCreditEvaluationsUseCase {
    private final RiskProfilePort riskProfilePort;
    private final CreditEvaluationRepositoryPort repositoryPort;
    private final CreditEvaluationPolicy policy = new CreditEvaluationPolicy();

    @Inject
    public EvaluateCreditApplicationService(
            RiskProfilePort riskProfilePort,
            CreditEvaluationRepositoryPort repositoryPort
    ) {
        this.riskProfilePort = riskProfilePort;
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Uni<CreditEvaluationResult> evaluate(CreditEvaluationCommand command) {
        return Uni.createFrom()
                .item(() -> new EcuadorianIdentityCard(command.identityCard()))
                .chain(identityCard -> riskProfilePort.findBy(identityCard)
                        .onItem().transform(profile -> decide(identityCard, command, profile)))
                .chain(evaluation -> Uni.createFrom()
                        .item(() -> repositoryPort.save(evaluation))
                        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool()))
                .onItem().transform(CreditEvaluationResult::fromDomain);
    }

    @Override
    public Uni<List<CreditEvaluationResult>> findRecent(int limit) {
        return Uni.createFrom()
                .item(() -> repositoryPort.findRecent(limit))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(evaluations -> evaluations.stream()
                        .map(CreditEvaluationResult::fromDomain)
                        .toList());
    }

    private CreditEvaluation decide(
            EcuadorianIdentityCard identityCard,
            CreditEvaluationCommand command,
            RiskProfile profile
    ) {
        var status = policy.evaluate(profile, command.requestedAmount(), command.monthlySalary());
        return CreditEvaluation.newEvaluation(identityCard, command.requestedAmount(), status);
    }
}
