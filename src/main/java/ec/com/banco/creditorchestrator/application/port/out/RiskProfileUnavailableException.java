package ec.com.banco.creditorchestrator.application.port.out;

public class RiskProfileUnavailableException extends RuntimeException {
    public RiskProfileUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
