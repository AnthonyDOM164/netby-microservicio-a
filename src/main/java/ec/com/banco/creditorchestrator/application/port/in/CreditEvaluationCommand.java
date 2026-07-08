package ec.com.banco.creditorchestrator.application.port.in;

import java.math.BigDecimal;

public record CreditEvaluationCommand(
        String identityCard,
        BigDecimal requestedAmount,
        Integer termYears,
        BigDecimal monthlySalary
) {
}
