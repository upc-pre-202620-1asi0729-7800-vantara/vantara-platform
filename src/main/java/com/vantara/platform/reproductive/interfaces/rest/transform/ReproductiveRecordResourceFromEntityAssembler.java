package com.vantara.platform.reproductive.interfaces.rest.transform;

import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.interfaces.rest.resources.ReproductiveRecordResource;

import java.util.Locale;

/**
 * Assembler that converts {@link ReproductiveRecord} aggregates into REST {@link ReproductiveRecordResource} objects.
 */
public final class ReproductiveRecordResourceFromEntityAssembler {

    private ReproductiveRecordResourceFromEntityAssembler() {
    }

    public static ReproductiveRecordResource toResourceFromEntity(ReproductiveRecord record) {
        var calving = record.getCalving();
        var weaning = record.getWeaning();
        return new ReproductiveRecordResource(
                record.getId(),
                record.getAnimalId().animalId(),
                calving == null || calving.calfId() == null ? null : calving.calfId().animalId(),
                record.getConfirmedOn(),
                record.getExpectedCalvingOn(),
                record.getDryOffOn(),
                calving == null ? null : calving.calvedOn(),
                calving == null ? null : calving.outcome().description(),
                calving == null || calving.birthWeight() == null ? null : calving.birthWeight().kilograms(),
                weaning == null ? null : weaning.weanedOn(),
                weaning == null || weaning.weight() == null ? null : weaning.weight().kilograms(),
                record.getStatus().name().toLowerCase(Locale.ROOT));
    }
}
