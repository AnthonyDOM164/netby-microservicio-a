package ec.com.banco.creditorchestrator.domain.exception;

import java.math.BigDecimal;

public class InvalidCreditAmountException extends DomainException {
    public InvalidCreditAmountException(BigDecimal amount) {
        super("El monto solicitado debe ser mayor a cero: " + amount);
    }
}
