package com.vantara.platform.reproductive.domain.model.aggregates;

import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveEventDateException;
import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveStateException;
import com.vantara.platform.reproductive.domain.model.events.BirthRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.DryOffRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.PregnancyRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.WeaningRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.model.valueobjects.Calving;
import com.vantara.platform.reproductive.domain.model.valueobjects.CalvingOutcome;
import com.vantara.platform.reproductive.domain.model.valueobjects.ReproductiveStatus;
import com.vantara.platform.reproductive.domain.model.valueobjects.Weaning;
import com.vantara.platform.reproductive.domain.model.valueobjects.WeightInKg;
import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Aggregate root of the reproductive cycle of one animal: from the pregnancy confirmation to the weaning of the calf.
 *
 * <p>Lifecycle: {@code CONFIRMED -> [DRY_OFF] -> ENDED (calving) -> WEANED}. Every transition validates the status
 * and the chronological order of the dates, and records the matching domain event.</p>
 */
public class ReproductiveRecord extends AbstractDomainAggregateRoot<ReproductiveRecord> {

    private Long id;
    private AnimalId animalId;
    private LocalDate confirmedOn;
    private LocalDate expectedCalvingOn;
    private LocalDate dryOffOn;
    private Calving calving;
    private Weaning weaning;
    private ReproductiveStatus status;

    private ReproductiveRecord() {
    }

    /**
     * Factory method that registers a confirmed pregnancy.
     *
     * <p>The creation event is not recorded here because the identifier is assigned on persistence; the repository
     * adapter calls {@link #onRegistered(Instant)} after saving.</p>
     *
     * @param animalId          the pregnant animal
     * @param confirmedOn       date the pregnancy was confirmed, not in the future
     * @param expectedCalvingOn expected calving date, or {@code null}; cannot precede the confirmation
     * @param now               registration instant
     * @return the new record with status {@link ReproductiveStatus#CONFIRMED}
     * @throws InvalidReproductiveEventDateException when a date is in the future or out of order
     */
    public static ReproductiveRecord confirmPregnancy(
            AnimalId animalId,
            LocalDate confirmedOn,
            LocalDate expectedCalvingOn,
            Instant now
    ) {
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(confirmedOn, "Confirmation date cannot be null");
        Objects.requireNonNull(now, "Registration instant cannot be null");
        if (confirmedOn.isAfter(today(now))) {
            throw new InvalidReproductiveEventDateException("The pregnancy confirmation date cannot be in the future");
        }
        if (expectedCalvingOn != null && expectedCalvingOn.isBefore(confirmedOn)) {
            throw new InvalidReproductiveEventDateException(
                    "The expected calving date cannot precede the pregnancy confirmation date");
        }

        var record = new ReproductiveRecord();
        record.animalId = animalId;
        record.confirmedOn = confirmedOn;
        record.expectedCalvingOn = expectedCalvingOn;
        record.status = ReproductiveStatus.CONFIRMED;
        return record;
    }

    /**
     * Rebuilds a record from its persisted state. It does not record any domain event.
     */
    public static ReproductiveRecord reconstitute(
            Long id,
            AnimalId animalId,
            LocalDate confirmedOn,
            LocalDate expectedCalvingOn,
            LocalDate dryOffOn,
            Calving calving,
            Weaning weaning,
            ReproductiveStatus status
    ) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("Invalid reproductive record id");
        }
        Objects.requireNonNull(animalId, "Animal id cannot be null");
        Objects.requireNonNull(confirmedOn, "Confirmation date cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");

        var record = new ReproductiveRecord();
        record.id = id;
        record.animalId = animalId;
        record.confirmedOn = confirmedOn;
        record.expectedCalvingOn = expectedCalvingOn;
        record.dryOffOn = dryOffOn;
        record.calving = calving;
        record.weaning = weaning;
        record.status = status;
        return record;
    }

    /**
     * Records the "pregnancy registered" event once the record has its persistent identifier.
     *
     * @param now registration instant
     */
    public void onRegistered(Instant now) {
        if (id == null) {
            throw new IllegalStateException("The record must be persisted before announcing its registration");
        }
        registerDomainEvent(new PregnancyRegisteredEvent(id, animalId.animalId(), confirmedOn, expectedCalvingOn, now));
    }

    /**
     * Registers the dry-off of the animal while the pregnancy is in progress.
     *
     * @param on  date of the dry-off, between the confirmation and today
     * @param now registration instant
     * @throws InvalidReproductiveStateException     when the pregnancy ended or the dry-off was already registered
     * @throws InvalidReproductiveEventDateException when the date is out of range
     */
    public void registerDryOff(LocalDate on, Instant now) {
        Objects.requireNonNull(on, "Dry-off date cannot be null");
        if (status != ReproductiveStatus.CONFIRMED) {
            throw new InvalidReproductiveStateException(status == ReproductiveStatus.DRY_OFF
                    ? "The dry-off was already registered for this pregnancy"
                    : "The dry-off requires a pregnancy in progress");
        }
        requireBetweenAndToday(on, confirmedOn, "dry-off", "pregnancy confirmation", now);

        this.dryOffOn = on;
        this.status = ReproductiveStatus.DRY_OFF;
        registerDomainEvent(new DryOffRegisteredEvent(id, animalId.animalId(), on, now));
    }

    /**
     * Checks, without changing the record, that a calving can be registered on the given date.
     *
     * <p>Lets callers validate before triggering side effects in other contexts (such as creating the calf).</p>
     *
     * @param on  date of the calving
     * @param now registration instant
     * @throws InvalidReproductiveStateException     when the pregnancy already ended
     * @throws InvalidReproductiveEventDateException when the date is out of range
     */
    public void ensureCanRegisterCalving(LocalDate on, Instant now) {
        Objects.requireNonNull(on, "Calving date cannot be null");
        if (!status.isPregnancyInProgress()) {
            throw new InvalidReproductiveStateException("The calving was already registered for this pregnancy");
        }
        requireBetweenAndToday(on, confirmedOn, "calving", "pregnancy confirmation", now);
    }

    /**
     * Registers the calving that ends the pregnancy.
     *
     * @param on          date of the calving, between the confirmation and today
     * @param outcome     result of the calving
     * @param calfId      identifier of the registered calf, or {@code null}
     * @param birthWeight birth weight of the calf, required when a calf is registered
     * @param now         registration instant
     * @throws InvalidReproductiveStateException     when the pregnancy already ended
     * @throws InvalidReproductiveEventDateException when the date is out of range
     */
    public void registerCalving(LocalDate on, CalvingOutcome outcome, AnimalId calfId, WeightInKg birthWeight, Instant now) {
        ensureCanRegisterCalving(on, now);

        this.calving = new Calving(on, outcome, calfId, birthWeight);
        this.status = ReproductiveStatus.ENDED;
        registerDomainEvent(new BirthRegisteredEvent(
                id, animalId.animalId(), calfId == null ? null : calfId.animalId(), on, outcome.description(), now));
    }

    /**
     * Registers the weaning of the calf born in the calving.
     *
     * @param on     date of the weaning, between the calving and today
     * @param weight weight of the calf at weaning, or {@code null}
     * @param now    registration instant
     * @throws InvalidReproductiveStateException     when there is no calving with a calf pending weaning
     * @throws InvalidReproductiveEventDateException when the date is out of range
     */
    public void registerWeaning(LocalDate on, WeightInKg weight, Instant now) {
        Objects.requireNonNull(on, "Weaning date cannot be null");
        if (status != ReproductiveStatus.ENDED || !calving.hasCalf()) {
            throw new InvalidReproductiveStateException(status == ReproductiveStatus.WEANED
                    ? "The weaning was already registered for this calf"
                    : "The weaning requires a calving with a registered calf");
        }
        requireBetweenAndToday(on, calving.calvedOn(), "weaning", "calving", now);

        this.weaning = new Weaning(on, weight);
        this.status = ReproductiveStatus.WEANED;
        registerDomainEvent(new WeaningRegisteredEvent(id, animalId.animalId(), calving.calfId().animalId(), on, now));
    }

    /**
     * @return {@code true} while the pregnancy has not ended in a calving
     */
    public boolean isPregnancyInProgress() {
        return status.isPregnancyInProgress();
    }

    private static void requireBetweenAndToday(LocalDate on, LocalDate notBefore, String event, String reference, Instant now) {
        if (on.isAfter(today(now))) {
            throw new InvalidReproductiveEventDateException("The %s date cannot be in the future".formatted(event));
        }
        if (on.isBefore(notBefore)) {
            throw new InvalidReproductiveEventDateException(
                    "The %s date cannot precede the %s date".formatted(event, reference));
        }
    }

    private static LocalDate today(Instant now) {
        return LocalDate.ofInstant(now, ZoneOffset.UTC);
    }

    public Long getId() {
        return id;
    }

    public AnimalId getAnimalId() {
        return animalId;
    }

    public LocalDate getConfirmedOn() {
        return confirmedOn;
    }

    public LocalDate getExpectedCalvingOn() {
        return expectedCalvingOn;
    }

    public LocalDate getDryOffOn() {
        return dryOffOn;
    }

    public Calving getCalving() {
        return calving;
    }

    public Weaning getWeaning() {
        return weaning;
    }

    public ReproductiveStatus getStatus() {
        return status;
    }
}
