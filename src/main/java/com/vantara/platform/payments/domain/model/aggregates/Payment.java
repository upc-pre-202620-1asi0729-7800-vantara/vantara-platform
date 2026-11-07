package com.vantara.platform.payments.domain.model.aggregates;

import com.vantara.platform.payments.domain.model.valueobjects.PaymentMethod;
import com.vantara.platform.payments.domain.model.valueobjects.PaymentStatus;
import com.vantara.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Payment aggregate root.
 *
 * <p>A payment can optionally be linked to an appointment. When
 * {@code appointmentId} is {@code null} the payment represents a subscription
 * or another standalone payment.</p>
 */
public final class Payment extends AbstractDomainAggregateRoot {

    private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");

    private final String id;
    private final String rancherId;
    private final String appointmentId;
    private final String concept;
    private final BigDecimal amount;
    private final String currency;
    private final PaymentStatus status;
    private final PaymentMethod paymentMethod;
    private final Instant createdAt;
    private final Instant confirmedAt;
    private final Instant cancelledAt;

    private Payment(String id,
                    String rancherId,
                    String appointmentId,
                    String concept,
                    BigDecimal amount,
                    String currency,
                    PaymentStatus status,
                    PaymentMethod paymentMethod,
                    Instant createdAt,
                    Instant confirmedAt,
                    Instant cancelledAt) {
        this.id = id;
        this.rancherId = rancherId;
        this.appointmentId = appointmentId;
        this.concept = concept;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    /**
     * Creates a new simulated payment. The payment is immediately marked as
     * paid because the current checkout uses a fake gateway.
     *
     * @param rancherId     owner of the payment
     * @param appointmentId optional appointment reference
     * @param concept       payment concept
     * @param amount        positive amount
     * @param currency      ISO-4217 currency code
     * @param paymentMethod optional non-sensitive payment method metadata
     * @param now           current instant
     * @return new payment
     */
    public static Payment create(String rancherId,
                                 String appointmentId,
                                 String concept,
                                 BigDecimal amount,
                                 String currency,
                                 PaymentMethod paymentMethod,
                                 Instant now) {
        Objects.requireNonNull(now, "payment.error.createdAt.required");
        return new Payment(
                UUID.randomUUID().toString(),
                requireText(rancherId, "payment.error.rancherId.required"),
                normalizeOptionalText(appointmentId),
                requireText(concept, "payment.error.concept.required"),
                normalizeAmount(amount),
                normalizeCurrency(currency),
                PaymentStatus.PAID,
                paymentMethod,
                now,
                now,
                null
        );
    }

    /**
     * Rehydrates a payment from persistence.
     *
     * @param id            payment identifier
     * @param rancherId     owner of the payment
     * @param appointmentId optional appointment reference
     * @param concept       payment concept
     * @param amount        positive amount
     * @param currency      ISO-4217 currency code
     * @param status        payment status
     * @param paymentMethod optional non-sensitive payment method metadata
     * @param createdAt     creation instant
     * @param confirmedAt   confirmation instant
     * @param cancelledAt   cancellation instant
     * @return rehydrated payment
     */
    public static Payment rehydrate(String id,
                                    String rancherId,
                                    String appointmentId,
                                    String concept,
                                    BigDecimal amount,
                                    String currency,
                                    PaymentStatus status,
                                    PaymentMethod paymentMethod,
                                    Instant createdAt,
                                    Instant confirmedAt,
                                    Instant cancelledAt) {
        return new Payment(
                requireText(id, "payment.error.id.required"),
                requireText(rancherId, "payment.error.rancherId.required"),
                normalizeOptionalText(appointmentId),
                requireText(concept, "payment.error.concept.required"),
                normalizeAmount(amount),
                normalizeCurrency(currency),
                Objects.requireNonNull(status, "payment.error.status.required"),
                paymentMethod,
                Objects.requireNonNull(createdAt, "payment.error.createdAt.required"),
                confirmedAt,
                cancelledAt
        );
    }

    private static String requireText(String value, String messageKey) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(messageKey);
        }
        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("payment.error.amount.positive");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("payment.error.currency.required");
        }
        var normalized = currency.trim().toUpperCase(Locale.ROOT);
        if (!CURRENCY_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("payment.error.currency.invalid");
        }
        return normalized;
    }

    public String getId() {
        return id;
    }

    public String getRancherId() {
        return rancherId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getConcept() {
        return concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }
}
