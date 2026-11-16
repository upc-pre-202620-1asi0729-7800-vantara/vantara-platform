package com.vantara.platform.livestockmanagement.application.acl;

import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.commands.CreateAnimalCommand;
import com.vantara.platform.livestockmanagement.application.commands.UpdateAnimalCommand;
import com.vantara.platform.livestockmanagement.application.queries.ExistsAnimalByEarTagQuery;
import com.vantara.platform.livestockmanagement.application.queries.GetAnimalByIdQuery;
import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.FeedingPlan;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Lot;
import com.vantara.platform.livestockmanagement.domain.repositories.AnimalRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingLogRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.FeedingPlanRepository;
import com.vantara.platform.livestockmanagement.domain.repositories.LotRepository;
import com.vantara.platform.livestockmanagement.interfaces.acl.AnimalRecord;
import com.vantara.platform.livestockmanagement.interfaces.acl.FeedingLogRecord;
import com.vantara.platform.livestockmanagement.interfaces.acl.FeedingPlanItemRecord;
import com.vantara.platform.livestockmanagement.interfaces.acl.FeedingPlanRecord;
import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import com.vantara.platform.livestockmanagement.interfaces.acl.LotRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements {@link LivestockContextFacade} on top of the livestock command and query services.
 *
 * <p>It adds no transaction of its own: writes join the transaction of the calling context.</p>
 */
@Service
public class LivestockContextFacadeImpl implements LivestockContextFacade {

    private static final String ACTIVE_STATUS = "active";
    private static final int CALF_AGE_MONTHS = 6;

    private final LivestockManagementCommandService commandService;
    private final LivestockManagementQueryService queryService;
    private final AnimalRepository animalRepository;
    private final LotRepository lotRepository;
    private final FeedingPlanRepository feedingPlanRepository;
    private final FeedingLogRepository feedingLogRepository;
    private final Clock clock;

    public LivestockContextFacadeImpl(
            LivestockManagementCommandService commandService,
            LivestockManagementQueryService queryService,
            AnimalRepository animalRepository,
            LotRepository lotRepository,
            FeedingPlanRepository feedingPlanRepository,
            FeedingLogRepository feedingLogRepository,
            Clock clock
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.animalRepository = animalRepository;
        this.lotRepository = lotRepository;
        this.feedingPlanRepository = feedingPlanRepository;
        this.feedingLogRepository = feedingLogRepository;
        this.clock = clock;
    }

    @Override
    public boolean existsAnimal(String animalId) {
        return findAnimal(animalId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnimalRecord> findAnimalById(String animalId) {
        return findAnimal(animalId)
                .map(animal -> new AnimalRecord(
                        animal.getId(),
                        animal.getRancherId(),
                        animal.getLotId(),
                        animal.getEarTag().value(),
                        animal.getName(),
                        animal.getSpecies(),
                        animal.getBreed(),
                        animal.getSex(),
                        animal.getBirthDate(),
                        animal.getMotherId(),
                        animal.getFatherId(),
                        animal.getStatus(),
                        animal.getWeight().value(),
                        animal.getRegisteredAt(),
                        animal.getPhotoUrl(),
                        animal.getWeanedOn()
                ));
    }

    @Override
    public boolean isReproductiveFemale(String animalId) {
        return findAnimal(animalId)
                .filter(this::isActive)
                .filter(this::isFemale)
                .filter(animal -> !isCalf(animal))
                .isPresent();
    }

    @Override
    public boolean existsEarTagInRancherOf(String animalId, String earTag) {
        if (isBlank(earTag)) {
            return false;
        }
        return findAnimal(animalId)
                .map(animal -> queryService.handle(
                        new ExistsAnimalByEarTagQuery(animal.getRancherId(), earTag)
                ))
                .orElse(false);
    }

    @Override
    public Optional<String> registerCalf(
            String motherId,
            String name,
            String earTag,
            String sex,
            BigDecimal weightKg,
            LocalDate birthDate
    ) {
        var mother = findAnimal(motherId);

        if (mother.isEmpty()) {
            return Optional.empty();
        }

        var dam = mother.orElseThrow();

        var command = new CreateAnimalCommand(
                dam.getRancherId(),
                dam.getLotId(),
                earTag,
                name,
                dam.getSpecies(),
                dam.getBreed(),
                sex == null ? null : sex.trim().toLowerCase(),
                birthDate,
                dam.getId(),
                null,
                weightKg,
                ACTIVE_STATUS,
                LocalDate.now(clock),
                null,
                null
        );

        // A business failure (duplicate ear tag, missing lot) is reported as empty; rule
        // violations raise IllegalArgumentException and are deliberately not swallowed.
        return commandService.handle(command)
                .toOptional()
                .map(Animal::getId);
    }

    @Override
    public boolean registerCalfWeaning(
            String calfId,
            String motherId,
            LocalDate weanedOn,
            BigDecimal weightKg
    ) {
        if (weanedOn == null) {
            throw new IllegalArgumentException("weanedOn cannot be null");
        }

        var found = findAnimal(calfId);

        if (found.isEmpty()) {
            return false;
        }

        var calf = found.orElseThrow();

        if (motherId == null
                || !motherId.trim().equals(calf.getMotherId())
                || !isActive(calf)
                || calf.getWeanedOn() != null) {
            return false;
        }

        var command = new UpdateAnimalCommand(
                calf.getId(),
                calf.getLotId(),
                calf.getName(),
                calf.getSpecies(),
                calf.getBreed(),
                calf.getSex(),
                calf.getBirthDate(),
                calf.getMotherId(),
                calf.getFatherId(),
                weightKg == null ? calf.getWeight().value() : weightKg,
                calf.getStatus(),
                calf.getPhotoUrl(),
                weanedOn
        );

        return commandService.handle(command).isSuccess();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnimalRecord> listAnimalsOf(String rancherId) {
        if (isBlank(rancherId)) {
            return List.of();
        }

        return animalRepository.findAllByRancherId(rancherId).stream()
                .filter(animal -> isOwnedBy(rancherId, animal.getRancherId()))
                .map(animal -> new AnimalRecord(
                        animal.getId(),
                        animal.getRancherId(),
                        animal.getLotId(),
                        animal.getEarTag().value(),
                        animal.getName(),
                        animal.getSpecies(),
                        animal.getBreed(),
                        animal.getSex(),
                        animal.getBirthDate(),
                        animal.getMotherId(),
                        animal.getFatherId(),
                        animal.getStatus(),
                        animal.getWeight().value(),
                        animal.getRegisteredAt(),
                        animal.getPhotoUrl(),
                        animal.getWeanedOn()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotRecord> listLotsOf(String rancherId) {
        return ownedLots(rancherId).stream()
                .map(lot -> new LotRecord(lot.getId(), lot.getRancherId(), lot.getName()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedingPlanRecord> listFeedingPlansOf(String rancherId) {
        return ownedPlans(rancherId).stream()
                .map(plan -> new FeedingPlanRecord(
                        plan.getId(), plan.getLotId(), plan.getAnimalId(), plan.getName(), plan.getStartsOn()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedingPlanItemRecord> listFeedingPlanItemsOf(String rancherId) {
        return ownedPlans(rancherId).stream()
                .flatMap(plan -> plan.getItems().stream())
                .map(item -> new FeedingPlanItemRecord(item.getId(), item.getFeedingPlanId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedingLogRecord> listFeedingLogsOf(String rancherId) {
        var lotIds = ownedLotIds(rancherId);
        if (lotIds.isEmpty()) {
            return List.of();
        }
        return feedingLogRepository.findAllByLotIds(lotIds).stream()
                .filter(log -> lotIds.contains(log.getLotId()))
                .map(log -> new FeedingLogRecord(
                        log.getPlanItemId(), log.getLotId(), log.getFeedName(),
                        log.getFeedAt(), log.getQuantity(), log.getUnit()))
                .toList();
    }

    private List<Lot> ownedLots(String rancherId) {
        if (isBlank(rancherId)) {
            return List.of();
        }
        return lotRepository.findAllByRancherId(rancherId).stream()
                .filter(lot -> isOwnedBy(rancherId, lot.getRancherId()))
                .toList();
    }

    private Set<String> ownedLotIds(String rancherId) {
        return ownedLots(rancherId).stream().map(Lot::getId).collect(Collectors.toSet());
    }

    private List<FeedingPlan> ownedPlans(String rancherId) {
        var lotIds = ownedLotIds(rancherId);
        return feedingPlanRepository.findAllByLotIds(lotIds);
    }

    private static boolean isOwnedBy(String rancherId, String owner) {
        return !isBlank(rancherId) && rancherId.equals(owner);
    }

    private Optional<Animal> findAnimal(String animalId) {
        if (isBlank(animalId)) {
            return Optional.empty();
        }
        return queryService.handle(new GetAnimalByIdQuery(animalId.trim()));
    }

    private boolean isActive(Animal animal) {
        return ACTIVE_STATUS.equalsIgnoreCase(animal.getStatus());
    }

    private boolean isFemale(Animal animal) {
        return "female".equalsIgnoreCase(animal.getSex())
                || "hembra".equalsIgnoreCase(animal.getSex());
    }

    private boolean isCalf(Animal animal) {
        return LocalDate.now(clock).isBefore(
                animal.getBirthDate().plusMonths(CALF_AGE_MONTHS)
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
