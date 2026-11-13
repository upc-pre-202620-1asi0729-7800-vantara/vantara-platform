package com.vantara.platform.livestockmanagement.application.internal.eventhandlers;

import com.vantara.platform.livestockmanagement.domain.model.events.MovementRecordedEvent;
import com.vantara.platform.livestockmanagement.interfaces.events.MovementRecordedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates {@link MovementRecordedEvent} into {@link MovementRecordedIntegrationEvent} for other bounded contexts.
 *
 * <p>It runs inside the publishing transaction. Consumers that must only react to committed
 * changes should listen with {@code @TransactionalEventListener}.</p>
 */
@Service
public class MovementRecordedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public MovementRecordedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the integration event that mirrors the given domain event.
     *
     * @param event the domain event raised by the livestock context
     */
    @EventListener
    public void on(MovementRecordedEvent event) {
        eventPublisher.publishEvent(MovementRecordedIntegrationEvent.from(event));
    }
}
