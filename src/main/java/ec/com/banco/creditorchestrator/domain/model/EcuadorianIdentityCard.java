package ec.com.banco.creditorchestrator.domain.model;

import ec.com.banco.creditorchestrator.domain.exception.InvalidEcuadorianIdentityCardException;

import java.util.Objects;

public record EcuadorianIdentityCard(String value) {
    private static final int[] MODULO_10_FACTORS = {2, 1, 2, 1, 2, 1, 2, 1, 2};

    public EcuadorianIdentityCard {
        String normalized = Objects.requireNonNullElse(value, "").trim();
        if (!isValid(normalized)) {
            throw new InvalidEcuadorianIdentityCardException(normalized);
        }
        value = normalized;
    }

    private static boolean isValid(String identityCard) {
        if (!identityCard.matches("\\d{10}")) {
            return false;
        }

        int provinceCode = Integer.parseInt(identityCard.substring(0, 2));
        int thirdDigit = Character.digit(identityCard.charAt(2), 10);
        if (provinceCode < 1 || provinceCode > 24 || thirdDigit >= 6) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < MODULO_10_FACTORS.length; i++) {
            int product = Character.digit(identityCard.charAt(i), 10) * MODULO_10_FACTORS[i];
            if (product >= 10) {
                product -= 9;
            }
            sum += product;
        }

        int expectedVerifier = sum % 10 == 0 ? 0 : 10 - (sum % 10);
        int actualVerifier = Character.digit(identityCard.charAt(9), 10);
        return expectedVerifier == actualVerifier;
    }
}
