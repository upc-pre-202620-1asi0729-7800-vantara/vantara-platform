package com.vantara.platform.notifications.domain.model.aggregates;

import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.events.NotificationCreatedEvent;
import com.vantara.platform.notifications.domain.model.events.NotificationReadEvent;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    private static final Instant NOW = Instant.parse("2026-11-12T14:00:00Z");

    @Test
    void recordsTheFirstReadTime() {
        var notification = create(null);

        assertEquals(NotificationStatus.UNREAD, notification.getStatus());
        notification.markAsRead(NOW.plusSeconds(1));
        notification.markAsRead(NOW.plusSeconds(2));

        assertEquals(NotificationStatus.READ, notification.getStatus());
        assertEquals(NOW.plusSeconds(1), notification.getReadAt());
    }

    @Test
    void scheduledNotificationMustBeSentBeforeItCanBeRead() {
        Instant due = NOW.plusSeconds(60);
        var notification = create(due);

        assertEquals(NotificationStatus.SCHEDULED, notification.getStatus());
        assertThrows(NotificationNotSentException.class, () -> notification.markAsRead(due));
        assertThrows(IllegalArgumentException.class, () -> notification.send(NOW));
        notification.send(due);

        assertEquals(NotificationStatus.UNREAD, notification.getStatus());
        assertThrows(IllegalArgumentException.class, () -> notification.markAsRead(NOW));
        notification.markAsRead(due);
        assertEquals(NotificationStatus.READ, notification.getStatus());
    }

    @Test
    void rejectsAnInvalidRecipientAndPastSchedule() {
        assertThrows(IllegalArgumentException.class, () -> new RecipientId(0));
        assertThrows(IllegalArgumentException.class, () -> create(NOW.minusSeconds(1)));
    }

    @Test
    void recordsACreatedEventAndOneReadEventForTheFirstReadOnly() {
        var notification = create(null);

        assertEquals(List.of(new NotificationCreatedEvent(notification.getId().value(), 1,
                NotificationType.HEALTH_ALERT, NOW)), List.copyOf(notification.domainEvents()));

        notification.clearDomainEvents();
        notification.markAsRead(NOW.plusSeconds(1));
        notification.markAsRead(NOW.plusSeconds(2));

        assertEquals(List.of(new NotificationReadEvent(notification.getId().value(), 1, NOW.plusSeconds(1))),
                List.copyOf(notification.domainEvents()));
    }

    @Test
    void reconstitutingDoesNotRecordEvents() {
        var notification = create(null);
        var rebuilt = Notification.reconstitute(notification.getId(), notification.getRecipient(),
                notification.getType(), notification.getContent(), notification.getRelatedEntity(),
                notification.getCreatedAt(), notification.getScheduledAt(), notification.getSentAt(), null);

        assertTrue(rebuilt.domainEvents().isEmpty());
    }

    private Notification create(Instant scheduledAt) {
        return Notification.create(new RecipientId(1), NotificationType.HEALTH_ALERT,
                new NotificationContent("Health alert", "Animal requires attention"),
                new RelatedEntity("Animal", "1"), NOW, scheduledAt);
    }
}
