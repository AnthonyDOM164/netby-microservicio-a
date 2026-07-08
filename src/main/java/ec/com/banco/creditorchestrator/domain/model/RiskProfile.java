package ec.com.banco.creditorchestrator.domain.model;

import ec.com.banco.creditorchestrator.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.Objects;

public record RiskProfile(int score, BigDecimal monthlyDebt) {
    public RiskProfile {
        monthlyDebt = Objects.requireNonNull(monthlyDebt, "monthlyDebt is required");

        if (score < 0 || score > 100) {
            throw new DomainException("El score debe estar entre 0 y 100.");
        }
        if (monthlyDebt.signum() < 0) {
            throw new DomainException("La deuda mensual no puede ser negativa.");
        }
    }
}
