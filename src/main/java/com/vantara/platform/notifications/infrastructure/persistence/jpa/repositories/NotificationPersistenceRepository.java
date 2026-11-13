package com.vantara.platform.notifications.infrastructure.persistence.jpa.repositories;

import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.infrastructure.persistence.jpa.entities.NotificationPersistenceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPersistenceRepository extends JpaRepository<NotificationPersistenceEntity, UUID> {
    Optional<NotificationPersistenceEntity> findByIdAndRecipientUserId(UUID id, long recipient);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NotificationPersistenceEntity n where n.id = :id and n.recipientUserId = :recipient")
    Optional<NotificationPersistenceEntity> findForUpdateByIdAndRecipientUserId(
            @Param("id") UUID id, @Param("recipient") long recipient);

    @Query("""
            select n from NotificationPersistenceEntity n
            where n.recipientUserId = :recipient and (:type is null or n.type = :type)
              and (:status is null
                   or (:status = 'READ' and n.readAt is not null)
                   or (:status = 'UNREAD' and n.sentAt is not null and n.readAt is null)
                   or (:status = 'SCHEDULED' and n.sentAt is null))
            order by n.createdAt desc, n.id desc
            """)
    List<NotificationPersistenceEntity> findForRecipient(@Param("recipient") long recipient,
                                                         @Param("type") NotificationType type,
                                                         @Param("status") String status);
}
