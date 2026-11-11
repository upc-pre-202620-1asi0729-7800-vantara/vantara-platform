package com.vantara.platform.reproductive.application.internal.eventhandlers;

import com.vantara.platform.reproductive.domain.model.events.BirthRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.DryOffRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.PregnancyRegisteredEvent;
import com.vantara.platform.reproductive.domain.model.events.WeaningRegisteredEvent;
import com.vantara.platform.reproductive.interfaces.events.BirthRegisteredIntegrationEvent;
import com.vantara.platform.reproductive.interfaces.events.DryOffRegisteredIntegrationEvent;
import com.vantara.platform.reproductive.interfaces.events.PregnancyRegisteredIntegrationEvent;
import com.vantara.platform.reproductive.interfaces.events.WeaningRegisteredIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Translates the internal domain events of the reproductive record into the integration events that form the
 * published language of the {@code reproductive} bounded context.
 *
 * <p>This is the only place where a reproductive domain event crosses the boundary of the context. Other contexts
 * subscribe to the integration events, never to the domain events.</p>
 */
@Service
public class ReproductiveRecordEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public ReproductiveRecordEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(PregnancyRegisteredEvent event) {
        eventPublisher.publishEvent(new PregnancyRegisteredIntegrationEvent(
                event.recordId(), event.animalId(), event.confirmedOn(), event.expectedCalvingOn(), event.occurredAt()));
    }

    @EventListener
    public void on(BirthRegisteredEvent event) {
        eventPublisher.publishEvent(new BirthRegisteredIntegrationEvent(
                event.recordId(), event.animalId(), event.calfId(), event.calvedOn(), event.outcome(), event.occurredAt()));
    }

    @EventListener
    public void on(DryOffRegisteredEvent event) {
        eventPublisher.publishEvent(new DryOffRegisteredIntegrationEvent(
                event.recordId(), event.animalId(), event.dryOffOn(), event.occurredAt()));
    }

    @EventListener
    public void on(WeaningRegisteredEvent event) {
        eventPublisher.publishEvent(new WeaningRegisteredIntegrationEvent(
                event.recordId(), event.animalId(), event.calfId(), event.weanedOn(), event.occurredAt()));
    }
}
