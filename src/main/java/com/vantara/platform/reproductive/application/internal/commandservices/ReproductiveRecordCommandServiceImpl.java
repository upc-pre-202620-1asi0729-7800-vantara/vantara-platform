package com.vantara.platform.reproductive.application.internal.commandservices;

import com.vantara.platform.reproductive.application.commandservices.ReproductiveRecordCommandService;
import com.vantara.platform.reproductive.application.internal.outboundservices.acl.ExternalLivestockService;
import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveEventDateException;
import com.vantara.platform.reproductive.domain.exceptions.InvalidReproductiveStateException;
import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.commands.RegisterPregnancyCommand;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.repositories.ReproductiveRecordRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Application service that executes reproductive record commands.
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
        try {
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
        } catch (IllegalArgumentException | InvalidReproductiveEventDateException e) {
            return Result.failure(ApplicationError.validationError(RECORD, e.getMessage()));
        } catch (InvalidReproductiveStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation(RECORD, e.getMessage()));
        }
    }
}
