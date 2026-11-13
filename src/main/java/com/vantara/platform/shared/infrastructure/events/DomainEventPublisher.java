package com.vantara.platform.shared.infrastructure.events;

import com.vantara.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Publishes the domain events recorded by an aggregate once its repository saved it.
 *
 * <p>Events go through Spring's {@link ApplicationEventPublisher}. Listeners that must only react
 * to committed changes should use {@code @TransactionalEventListener}.</p>
 */
@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public DomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Publishes every pending event of the aggregate and clears them so they are not published twice.
     */
    public void publishAndClear(AbstractDomainAggregateRoot<?> aggregate) {
        var events = new ArrayList<>(aggregate.domainEvents());
        aggregate.clearDomainEvents();
        events.forEach(publisher::publishEvent);
    }
}
