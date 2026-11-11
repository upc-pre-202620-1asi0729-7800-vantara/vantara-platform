package com.vantara.platform.reproductive.application.internal.queryservices;

import com.vantara.platform.reproductive.application.queryservices.ReproductiveRecordQueryService;
import com.vantara.platform.reproductive.domain.model.aggregates.ReproductiveRecord;
import com.vantara.platform.reproductive.domain.model.queries.GetReproductiveRecordByIdQuery;
import com.vantara.platform.reproductive.domain.repositories.ReproductiveRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
