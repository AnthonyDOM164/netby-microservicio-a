package ec.com.banco.creditorchestrator.application.port.out;

import ec.com.banco.creditorchestrator.domain.model.EcuadorianIdentityCard;
import ec.com.banco.creditorchestrator.domain.model.RiskProfile;
import io.smallrye.mutiny.Uni;

public interface RiskProfilePort {
    Uni<RiskProfile> findBy(EcuadorianIdentityCard identityCard);
}
