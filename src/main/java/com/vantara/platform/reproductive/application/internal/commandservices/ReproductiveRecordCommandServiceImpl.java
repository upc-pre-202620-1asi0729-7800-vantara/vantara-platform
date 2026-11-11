package com.vantara.platform.reproductive.application.internal.commandservices;

import com.vantara.platform.reproductive.application.commandservices.ReproductiveRecordCommandService;
import com.vantara.platform.reproductive.application.internal.outboundservices.acl.ExternalLivestockService;
import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveEventDateException;
import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveStateException;
import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.commands.RegisterCalvingCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterDryOffCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterPregnancyCommand;
import com.vantara.platform.reproductive.domain.model.commands.RegisterWeaningCommand;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.model.valueobjects.CalvingOutcome;
import com.vantara.platform.reproductive.domain.model.valueobjects.WeightInKg;
import com.vantara.platform.reproductive.domain.repositories.ReproductiveRecordRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.function.Supplier;

/**
 * Application service that executes reproductive record commands.
 *
 * <p>Each command runs in one transaction. Aggregate checks that can fail run before any side effect in another
 * context, and unexpected failures propagate so the transaction rolls back.</p>
 */
@Service
@Transactional
public class ReproductiveRecordCommandServiceImpl implements ReproductiveRecordCommandService {

    private static final String RECORD = "ReproductiveRecord";

    private final ReproductiveRecordRepository recordRepository;
    private final ExternalLivestockService externalLivestockService;
    private final Clock clock;

    public ReproductiveRecordCommandServiceImpl(
            ReproductiveRecordRepository recordRepository,
            ExternalLivestockService externalLivestockService,
            Clock clock) {
        this.recordRepository = recordRepository;
        this.externalLivestockService = externalLivestockService;
        this.clock = clock;
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterPregnancyCommand command) {
        return execute(() -> {
            var animalId = new AnimalId(command.animalId());
            if (!externalLivestockService.existsAnimal(animalId)) {
                return Result.failure(ApplicationError.notFound("Animal", animalId.animalId().toString()));
            }
            if (!externalLivestockService.isReproductiveFemale(animalId)) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "reproductive-animal", "Only active adult females can have a pregnancy"));
            }
            if (recordRepository.existsPregnancyInProgressByAnimalId(animalId)) {
                return Result.failure(ApplicationError.conflict(
                        RECORD, "The animal already has a pregnancy in progress"));
            }
            var record = ReproductiveRecord.confirmPregnancy(
                    animalId, command.confirmedOn(), command.expectedCalvingOn(), clock.instant());
            return Result.success(recordRepository.save(record).getId());
        });
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterCalvingCommand command) {
        return execute(() -> {
            var found = recordRepository.findById(command.recordId());
            if (found.isEmpty()) {
                return Result.failure(ApplicationError.notFound(RECORD, command.recordId().toString()));
            }
            var record = found.get();
            var now = clock.instant();
            var outcome = new CalvingOutcome(command.outcome());

            // Validate everything the aggregate can check before creating the calf in another context.
            record.ensureCanRegisterCalving(command.calvedOn(), now);
            if (!externalLivestockService.isReproductiveFemale(record.getAnimalId())) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "reproductive-animal", "The mother must be an active adult female"));
            }

            AnimalId calfId = null;
            WeightInKg birthWeight = null;
            if (command.calf() != null) {
                var calf = command.calf();
                birthWeight = new WeightInKg(calf.weightKg());
                if (externalLivestockService.existsAnimalByEarTag(calf.earTag())) {
                    return Result.failure(ApplicationError.conflict(
                            "Animal", "The ear tag '%s' already belongs to another animal".formatted(calf.earTag())));
                }
                var registeredCalf = externalLivestockService.registerCalf(
                        record.getAnimalId(), calf, command.calvedOn());
                if (registeredCalf.isEmpty()) {
                    return Result.failure(ApplicationError.unexpected(
                            "register-calving", "Livestock could not register the calf"));
                }
                calfId = registeredCalf.get();
            }

            record.registerCalving(command.calvedOn(), outcome, calfId, birthWeight, now);
            return Result.success(recordRepository.save(record).getId());
        });
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterDryOffCommand command) {
        return execute(() -> {
            var found = recordRepository.findById(command.recordId());
            if (found.isEmpty()) {
                return Result.failure(ApplicationError.notFound(RECORD, command.recordId().toString()));
            }
            var record = found.get();
            record.registerDryOff(command.dryOffOn(), clock.instant());
            return Result.success(recordRepository.save(record).getId());
        });
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterWeaningCommand command) {
        return execute(() -> {
            var found = recordRepository.findById(command.recordId());
            if (found.isEmpty()) {
                return Result.failure(ApplicationError.notFound(RECORD, command.recordId().toString()));
            }
            var record = found.get();
            var now = clock.instant();
            var weight = WeightInKg.ofNullable(command.weightKg());

            // Validate the aggregate before updating the calf in another context.
            record.ensureCanRegisterWeaning(command.weanedOn(), now);
            var calfId = record.getCalving().calfId();
            if (!externalLivestockService.registerCalfWeaning(calfId, record.getAnimalId(), command.weanedOn(), weight)) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "calf-weaning", "The calf is not linked to this mother, is inactive or was already weaned"));
            }

            record.registerWeaning(command.weanedOn(), weight, now);
            return Result.success(recordRepository.save(record).getId());
        });
    }

    /**
     * Runs a command translating the expected domain failures into application errors.
     */
    private Result<Long, ApplicationError> execute(Supplier<Result<Long, ApplicationError>> action) {
        try {
            return action.get();
        } catch (IllegalArgumentException | InvalidReproductiveEventDateException e) {
            return Result.failure(ApplicationError.validationError(RECORD, e.getMessage()));
        } catch (InvalidReproductiveStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(RECORD, e.getMessage()));
        }
    }
}
