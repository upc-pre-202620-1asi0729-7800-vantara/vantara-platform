package com.vantara.platform.shared.domain.aggregates;

import com.vantara.platform.shared.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for domain aggregate roots that collect pending domain events.
 *
 * <p>Events are registered in memory and must be published explicitly by the
 * application or infrastructure layer once delivery semantics are defined.</p>
 */
public abstract class AbstractDomainAggregateRoot {

    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    /**
     * Registers a domain event to be published later.
     *
     * @param event event to register
     */
    protected final void registerEvent(DomainEvent event) {
        pendingEvents.add(Objects.requireNonNull(event));
    }

    /**
     * Returns and clears the pending domain events.
     *
     * @return immutable copy of the pending events
     */
    public final List<DomainEvent> pullDomainEvents() {
        var copy = List.copyOf(pendingEvents);
        pendingEvents.clear();
        return copy;
    }
}
