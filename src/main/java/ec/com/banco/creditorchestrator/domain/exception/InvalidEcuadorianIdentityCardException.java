package ec.com.banco.creditorchestrator.domain.exception;

public class InvalidEcuadorianIdentityCardException extends DomainException {
    public InvalidEcuadorianIdentityCardException(String identityCard) {
        super("La cedula ecuatoriana no es valida: " + identityCard);
    }
}
