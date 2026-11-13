package com.vantara.platform.reproductive.application.internal.queryservices;

import com.vantara.platform.reproductive.application.queryservices.ReproductiveRecordQueryService;
import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.queries.GetAllReproductiveRecordsQuery;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordByIdQuery;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordsByAnimalIdQuery;
import com.vantara.platform.reproductive.domain.model.valueobjects.AnimalId;
import com.vantara.platform.reproductive.domain.repositories.ReproductiveRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service that resolves reproductive record read queries.
 */
@Service
@Transactional(readOnly = true)
public class ReproductiveRecordQueryServiceImpl implements ReproductiveRecordQueryService {

    private final ReproductiveRecordRepository recordRepository;

    public ReproductiveRecordQueryServiceImpl(ReproductiveRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public Optional<ReproductiveRecord> handle(GetReproductiveRecordByIdQuery query) {
        return recordRepository.findById(query.recordId());
    }

    @Override
    public List<ReproductiveRecord> handle(GetReproductiveRecordsByAnimalIdQuery query) {
        return recordRepository.findAllByAnimalId(new AnimalId(query.animalId()));
    }

    @Override
    public List<ReproductiveRecord> handle(GetAllReproductiveRecordsQuery query) {
        return recordRepository.findAll();
    }
}
