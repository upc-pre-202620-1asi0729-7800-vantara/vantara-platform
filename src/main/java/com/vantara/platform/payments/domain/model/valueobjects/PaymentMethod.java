package com.vantara.platform.payments.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Non-sensitive metadata of the payment method used for a payment.
 *
 * <p>Full card numbers and security codes are deliberately not part of this
 * value object.</p>
 */
public record PaymentMethod(String brand, String last4, String holderName) {

    private static final Pattern BRAND_PATTERN = Pattern.compile("[A-Z0-9 ]{2,20}");
    private static final Pattern LAST4_PATTERN = Pattern.compile("\\d{4}");

    public PaymentMethod {
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("payment.error.paymentMethod.brand.required");
        }
        brand = brand.trim().toUpperCase(Locale.ROOT);
        if (!BRAND_PATTERN.matcher(brand).matches()) {
            throw new IllegalArgumentException("payment.error.paymentMethod.brand.invalid");
        }

        if (last4 == null || !LAST4_PATTERN.matcher(last4).matches()) {
            throw new IllegalArgumentException("payment.error.paymentMethod.last4.invalid");
        }

        if (holderName != null) {
            holderName = holderName.trim();
            if (holderName.isEmpty()) {
                holderName = null;
            } else if (holderName.length() > 160) {
                throw new IllegalArgumentException("payment.error.paymentMethod.holder.invalid");
            }
        }
    }

    /**
     * Creates a payment method value object.
     *
     * @param brand      card brand, for example {@code VISA}
     * @param last4      last four digits of the card
     * @param holderName optional card holder name
     * @return payment method
     */
    public static PaymentMethod of(String brand, String last4, String holderName) {
        return new PaymentMethod(brand, last4, holderName);
    }

    /**
     * @return a masked representation suitable for logs and receipts
     */
    public String masked() {
        return brand + " **** " + last4;
    }
}
