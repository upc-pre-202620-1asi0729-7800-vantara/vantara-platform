package com.vantara.platform.notifications.infrastructure.persistence.jpa.adapters;

import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.*;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationJpaEntity;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories.NotificationJpaRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationRepositoryAdapterTest {
    private static final Instant SENT = Instant.parse("2026-11-12T14:00:00Z");
    private static final RecipientId OWNER = new RecipientId(7);

    private final NotificationJpaRepository jpa = mock(NotificationJpaRepository.class);
    private final NotificationRepositoryAdapter adapter = new NotificationRepositoryAdapter(jpa);

    @Test
    void roundTripsTheNotificationThroughTheJpaMapping() {
        var notification = create();
        when(jpa.save(any(NotificationJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var saved = adapter.add(notification);

        assertEquals(notification.id(), saved.id());
        assertEquals(OWNER, saved.recipient());
        assertEquals(notification.type(), saved.type());
        assertEquals(notification.content(), saved.content());
        assertEquals(notification.relatedEntity(), saved.relatedEntity());
        assertEquals(SENT, saved.sentAt());
        assertEquals(NotificationStatus.UNREAD, saved.status());
    }

    @Test
    void updateUsesTheOwnerScopedLockAndPreservesTheStoredReadTimeOnRetry() {
        var notification = create();
        var entity = new NotificationJpaEntity(notification.id(), OWNER.value(), notification.type(),
                "Title", "Description", "Animal", "A1", SENT, null, SENT, null);
        when(jpa.lockForReadUpdate(notification.id(), OWNER.value())).thenReturn(Optional.of(entity));
        Instant firstRead = SENT.plusSeconds(1);

        assertEquals(firstRead, adapter.markAsRead(notification.id(), OWNER, firstRead).orElseThrow().readAt());
        assertEquals(firstRead, adapter.markAsRead(notification.id(), OWNER, firstRead.plusSeconds(1))
                .orElseThrow().readAt());
        assertEquals(firstRead, entity.getReadAt());
        verify(jpa, times(2)).lockForReadUpdate(notification.id(), OWNER.value());
        verifyNoMoreInteractions(jpa);
    }

    @Test
    void aDifferentRecipientCannotReadOrModifyTheNotification() {
        var notification = create();
        var other = new RecipientId(8);
        when(jpa.findByIdAndRecipientUserId(notification.id(), other.value())).thenReturn(Optional.empty());
        when(jpa.lockForReadUpdate(notification.id(), other.value())).thenReturn(Optional.empty());

        assertTrue(adapter.findById(notification.id(), other).isEmpty());
        assertTrue(adapter.markAsRead(notification.id(), other, SENT.plusSeconds(1)).isEmpty());

        verify(jpa).findByIdAndRecipientUserId(notification.id(), other.value());
        verify(jpa).lockForReadUpdate(notification.id(), other.value());
        verifyNoMoreInteractions(jpa);
    }

    private Notification create() {
        return Notification.create(OWNER, NotificationType.HEALTH_ALERT,
                new NotificationContent("Title", "Description"), new RelatedEntity("Animal", "A1"), SENT, null);
    }
}
