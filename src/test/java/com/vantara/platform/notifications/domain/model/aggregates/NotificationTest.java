package com.vantara.platform.notifications.domain.model.aggregates;

import com.vantara.platform.notifications.domain.exceptions.NotificationNotSentException;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    private static final Instant NOW = Instant.parse("2026-11-12T14:00:00Z");

    @Test
    void recordsTheFirstReadTime() {
        var notification = create(null);

        assertEquals(NotificationStatus.UNREAD, notification.status());
        notification.markAsRead(NOW.plusSeconds(1));
        notification.markAsRead(NOW.plusSeconds(2));

        assertEquals(NotificationStatus.READ, notification.status());
        assertEquals(NOW.plusSeconds(1), notification.readAt());
    }

    @Test
    void scheduledNotificationMustBeSentBeforeItCanBeRead() {
        Instant due = NOW.plusSeconds(60);
        var notification = create(due);

        assertEquals(NotificationStatus.SCHEDULED, notification.status());
        assertThrows(NotificationNotSentException.class, () -> notification.markAsRead(due));
        assertThrows(IllegalArgumentException.class, () -> notification.send(NOW));
        notification.send(due);

        assertEquals(NotificationStatus.UNREAD, notification.status());
        assertThrows(IllegalArgumentException.class, () -> notification.markAsRead(NOW));
        notification.markAsRead(due);
        assertEquals(NotificationStatus.READ, notification.status());
    }

    @Test
    void rejectsAnInvalidRecipientAndPastSchedule() {
        assertThrows(IllegalArgumentException.class, () -> new RecipientId(0));
        assertThrows(IllegalArgumentException.class, () -> create(NOW.minusSeconds(1)));
    }

    private Notification create(Instant scheduledAt) {
        return Notification.create(new RecipientId(1), NotificationType.HEALTH_ALERT,
                new NotificationContent("Health alert", "Animal requires attention"),
                new RelatedEntity("Animal", "1"), NOW, scheduledAt);
    }
}
