package com.vantara.platform.reproductive.infrastructure.persistence.jpa.assemblers;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.model.valueobjects.Calving;
import com.vantara.platform.reproductive.domain.model.valueobjects.CalvingOutcome;
import com.vantara.platform.reproductive.domain.model.valueobjects.Weaning;
import com.vantara.platform.reproductive.domain.model.valueobjects.WeightInKg;
import com.vantara.platform.reproductive.infrastructure.persistence.jpa.entities.ReproductiveRecordPersistenceEntity;

/**
 * Static assembler between the reproductive record domain aggregate and its persistence representation.
 */
public final class ReproductiveRecordPersistenceAssembler {

    private ReproductiveRecordPersistenceAssembler() {
    }

    public static ReproductiveRecord toDomainFromPersistence(ReproductiveRecordPersistenceEntity entity) {
        if (entity == null) return null;

        Calving calving = null;
        if (entity.getCalvedOn() != null) {
            calving = new Calving(
                    entity.getCalvedOn(),
                    new CalvingOutcome(entity.getCalvingOutcome()),
                    entity.getCalfId() == null ? null : new AnimalId(entity.getCalfId()),
                    WeightInKg.ofNullable(entity.getBirthWeightKg()));
        }
        Weaning weaning = entity.getWeanedOn() == null
                ? null
                : new Weaning(entity.getWeanedOn(), WeightInKg.ofNullable(entity.getWeaningWeightKg()));

        return ReproductiveRecord.reconstitute(
                entity.getId(),
                new AnimalId(entity.getAnimalId()),
                entity.getConfirmedOn(),
                entity.getExpectedCalvingOn(),
                entity.getDryOffOn(),
                calving,
                weaning,
                entity.getStatus());
    }

    public static ReproductiveRecordPersistenceEntity toPersistenceFromDomain(ReproductiveRecord record) {
        if (record == null) return null;

        var entity = new ReproductiveRecordPersistenceEntity();
        // Only set the id when the record already exists; new records let JPA generate it.
        if (record.getId() != null) {
            entity.setId(record.getId());
        }
        entity.setAnimalId(record.getAnimalId().animalId());
        entity.setConfirmedOn(record.getConfirmedOn());
        entity.setExpectedCalvingOn(record.getExpectedCalvingOn());
        entity.setDryOffOn(record.getDryOffOn());
        entity.setStatus(record.getStatus());

        var calving = record.getCalving();
        if (calving != null) {
            entity.setCalvedOn(calving.calvedOn());
            entity.setCalvingOutcome(calving.outcome().description());
            entity.setCalfId(calving.calfId() == null ? null : calving.calfId().animalId());
            entity.setBirthWeightKg(calving.birthWeight() == null ? null : calving.birthWeight().kilograms());
        }
        var weaning = record.getWeaning();
        if (weaning != null) {
            entity.setWeanedOn(weaning.weanedOn());
            entity.setWeaningWeightKg(weaning.weight() == null ? null : weaning.weight().kilograms());
        }
        return entity;
    }
}
