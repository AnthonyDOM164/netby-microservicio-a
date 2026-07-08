package ec.com.banco.creditorchestrator.infrastructure.persistence;

import ec.com.banco.creditorchestrator.application.port.out.CreditEvaluationRepositoryPort;
import ec.com.banco.creditorchestrator.domain.model.CreditEvaluation;
import ec.com.banco.creditorchestrator.domain.model.EcuadorianIdentityCard;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.panache.common.Page;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PanacheCreditEvaluationRepository implements CreditEvaluationRepositoryPort {
    private final CreditEvaluationPanacheRepository panacheRepository;

    @Inject
    public PanacheCreditEvaluationRepository(CreditEvaluationPanacheRepository panacheRepository) {
        this.panacheRepository = panacheRepository;
    }

    @Override
    @Transactional
    public CreditEvaluation save(CreditEvaluation evaluation) {
        var entity = CreditEvaluationEntity.fromDomain(evaluation);
        panacheRepository.persist(entity);
        return entity.toDomain();
    }

    @Override
    public List<CreditEvaluation> findRecent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        return panacheRepository
                .find("order by evaluatedAt desc")
                .page(Page.ofSize(safeLimit))
                .list()
                .stream()
                .map(CreditEvaluationEntity::toDomain)
                .toList();
    }

    public List<CreditEvaluation> findEvaluationsByIdentityCard(EcuadorianIdentityCard identityCard) {
        return panacheRepository
                .list(
                        "identityCard = :identityCard",
                        Map.of("identityCard", identityCard.value())
                )
                .stream()
                .map(CreditEvaluationEntity::toDomain)
                .toList();
    }
}
