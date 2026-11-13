package com.vantara.platform.livestockmanagement.application.internal.eventhandlers;

import com.vantara.platform.livestockmanagement.domain.model.events.FeedingPlanCreatedEvent;
import com.vantara.platform.livestockmanagement.interfaces.events.FeedingPlanCreatedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates {@link FeedingPlanCreatedEvent} into {@link FeedingPlanCreatedIntegrationEvent} for other bounded contexts.
 *
 * <p>It runs inside the publishing transaction. Consumers that must only react to committed
 * changes should listen with {@code @TransactionalEventListener}.</p>
 */
@Service
public class FeedingPlanCreatedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public FeedingPlanCreatedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the integration event that mirrors the given domain event.
     *
     * @param event the domain event raised by the livestock context
     */
    @EventListener
    public void on(FeedingPlanCreatedEvent event) {
        eventPublisher.publishEvent(FeedingPlanCreatedIntegrationEvent.from(event));
    }
}
