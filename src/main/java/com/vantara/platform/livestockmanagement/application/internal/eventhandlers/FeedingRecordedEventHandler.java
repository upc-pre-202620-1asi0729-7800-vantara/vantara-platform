package com.vantara.platform.livestockmanagement.application.internal.eventhandlers;

import com.vantara.platform.livestockmanagement.domain.model.events.FeedingRecordedEvent;
import com.vantara.platform.livestockmanagement.interfaces.events.FeedingRecordedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates {@link FeedingRecordedEvent} into {@link FeedingRecordedIntegrationEvent} for other bounded contexts.
 *
 * <p>It runs inside the publishing transaction. Consumers that must only react to committed
 * changes should listen with {@code @TransactionalEventListener}.</p>
 */
@Service
public class FeedingRecordedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public FeedingRecordedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the integration event that mirrors the given domain event.
     *
     * @param event the domain event raised by the livestock context
     */
    @EventListener
    public void on(FeedingRecordedEvent event) {
        eventPublisher.publishEvent(FeedingRecordedIntegrationEvent.from(event));
    }
}
