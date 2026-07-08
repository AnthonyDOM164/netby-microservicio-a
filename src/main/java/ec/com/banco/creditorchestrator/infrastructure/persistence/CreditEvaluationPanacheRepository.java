package ec.com.banco.creditorchestrator.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CreditEvaluationPanacheRepository implements PanacheRepositoryBase<CreditEvaluationEntity, UUID> {
}
