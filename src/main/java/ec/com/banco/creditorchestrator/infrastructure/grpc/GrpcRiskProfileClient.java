package ec.com.banco.creditorchestrator.infrastructure.grpc;

import ec.com.banco.riesgos.grpc.CedulaRequest;
import ec.com.banco.riesgos.grpc.DeudaResponse;
import ec.com.banco.riesgos.grpc.DeudasResponse;
import ec.com.banco.riesgos.grpc.MutinyDeudasRiesgoGrpc;
import ec.com.banco.riesgos.grpc.MutinyScoreRiesgoGrpc;
import ec.com.banco.riesgos.grpc.ScoreResponse;
import ec.com.banco.creditorchestrator.application.port.out.RiskProfilePort;
import ec.com.banco.creditorchestrator.application.port.out.RiskProfileUnavailableException;
import ec.com.banco.creditorchestrator.domain.model.EcuadorianIdentityCard;
import ec.com.banco.creditorchestrator.domain.model.RiskProfile;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.math.BigDecimal;

@ApplicationScoped
public class GrpcRiskProfileClient implements RiskProfilePort {
    private final MutinyScoreRiesgoGrpc.MutinyScoreRiesgoStub scoreClient;
    private final MutinyDeudasRiesgoGrpc.MutinyDeudasRiesgoStub deudasClient;

    @Inject
    public GrpcRiskProfileClient(
            @GrpcClient("score") MutinyScoreRiesgoGrpc.MutinyScoreRiesgoStub scoreClient,
            @GrpcClient("deudas") MutinyDeudasRiesgoGrpc.MutinyDeudasRiesgoStub deudasClient
    ) {
        this.scoreClient = scoreClient;
        this.deudasClient = deudasClient;
    }

    @Override
    @Timeout(3_000)
    @CircuitBreaker(requestVolumeThreshold = 8, failureRatio = 0.5, delay = 10_000)
    public Uni<RiskProfile> findBy(EcuadorianIdentityCard identityCard) {
        var request = CedulaRequest.newBuilder()
                .setCedula(identityCard.value())
                .build();

        Uni<ScoreResponse> score = scoreClient.obtenerScore(request);
        Uni<DeudasResponse> deudas = deudasClient.obtenerDeudas(request);

        return Uni.combine()
                .all()
                .unis(score, deudas)
                .asTuple()
                .onItem().transform(tuple -> new RiskProfile(
                        tuple.getItem1().getScore(),
                        sumMonthlyDebt(tuple.getItem2())
                ))
                .onFailure().transform(failure -> new RiskProfileUnavailableException(
                        "Microservicio B no esta disponible, excedio el timeout o respondio fuera del contrato.",
                        failure
                ));
    }

    private BigDecimal sumMonthlyDebt(DeudasResponse response) {
        return response.getDeudasList()
                .stream()
                .map(this::parseDebtAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal parseDebtAmount(DeudaResponse debt) {
        try {
            return new BigDecimal(debt.getMonto());
        } catch (NumberFormatException exception) {
            throw new RiskProfileUnavailableException(
                    "Microservicio B retorno una deuda con monto invalido: " + debt.getMonto(),
                    exception
            );
        }
    }
}
