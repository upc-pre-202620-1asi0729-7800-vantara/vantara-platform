package com.vantara.platform.iam.domain.repositories;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;

/**
 * Domain repository contract for {@link ContactRequest} aggregates.
 */
public interface ContactRequestRepository {

    ContactRequest save(ContactRequest contactRequest);
}
