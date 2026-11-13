package com.vantara.platform.livestockmanagement.application.internal.eventhandlers;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalLotChangedEvent;
import com.vantara.platform.livestockmanagement.interfaces.events.AnimalLotChangedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates {@link AnimalLotChangedEvent} into {@link AnimalLotChangedIntegrationEvent} for other bounded contexts.
 *
 * <p>It runs inside the publishing transaction. Consumers that must only react to committed
 * changes should listen with {@code @TransactionalEventListener}.</p>
 */
@Service
public class AnimalLotChangedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public AnimalLotChangedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the integration event that mirrors the given domain event.
     *
     * @param event the domain event raised by the livestock context
     */
    @EventListener
    public void on(AnimalLotChangedEvent event) {
        eventPublisher.publishEvent(AnimalLotChangedIntegrationEvent.from(event));
    }
}
