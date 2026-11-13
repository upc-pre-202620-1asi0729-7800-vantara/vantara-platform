package com.vantara.platform.livestockmanagement.application.internal.eventhandlers;

import com.vantara.platform.livestockmanagement.domain.model.events.AnimalWeanedEvent;
import com.vantara.platform.livestockmanagement.interfaces.events.AnimalWeanedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates {@link AnimalWeanedEvent} into {@link AnimalWeanedIntegrationEvent} for other bounded contexts.
 *
 * <p>It runs inside the publishing transaction. Consumers that must only react to committed
 * changes should listen with {@code @TransactionalEventListener}.</p>
 */
@Service
public class AnimalWeanedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public AnimalWeanedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publishes the integration event that mirrors the given domain event.
     *
     * @param event the domain event raised by the livestock context
     */
    @EventListener
    public void on(AnimalWeanedEvent event) {
        eventPublisher.publishEvent(AnimalWeanedIntegrationEvent.from(event));
    }
}
