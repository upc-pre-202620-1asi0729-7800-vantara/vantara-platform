package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.interfaces.acl.IamContextFacade;
import com.vantara.platform.notifications.application.internal.commandservices.NotificationCommandServiceImpl;
import com.vantara.platform.notifications.application.internal.queryservices.NotificationQueryServiceImpl;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationContent;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationId;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import com.vantara.platform.notifications.domain.model.valueobjects.RelatedEntity;
import com.vantara.platform.shared.interfaces.rest.GlobalExceptionHandler;
import org.springframework.http.MediaType;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationsControllerTest {
    private static final String PATH = "/api/v1/notifications";
    private static final String EMAIL = "owner@example.com";
    private static final RecipientId OWNER = new RecipientId(7);
    private static final Instant NOW = Instant.parse("2026-11-12T14:00:00Z");

    private static final String CREATE_JSON = """
            {
              "type": "HEALTH_ALERT",
              "title": "Animal requires attention",
              "description": "Check animal A1",
              "relatedEntityType": "Animal",
              "relatedEntityId": "A1"
            }
            """;

    private NotificationRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationRepository.class);
        var iam = mock(IamContextFacade.class);
        when(iam.fetchUserIdByEmail(EMAIL)).thenReturn(Optional.of(OWNER.value()));
        when(iam.fetchUserIdByEmail("unknown@example.com")).thenReturn(Optional.empty());
        var commands = new NotificationCommandServiceImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));
        var queries = new NotificationQueryServiceImpl(repository);
        mvc = MockMvcBuilders.standaloneSetup(new NotificationsController(commands, queries, iam))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static UsernamePasswordAuthenticationToken as(String name) {
        return new UsernamePasswordAuthenticationToken(name, null);
    }

    @Test
    void anonymousRequestsDoNotReachPersistence() throws Exception {
        mvc.perform(get(PATH)).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository);
    }

    @Test
    void unknownOrBlankAuthenticatedNameFailsClosed() throws Exception {
        mvc.perform(get(PATH).principal(as("unknown@example.com"))).andExpect(status().isUnauthorized());
        mvc.perform(get(PATH).principal(as(" "))).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository);
    }

    @Test
    void listIsScopedToTheAuthenticatedUser() throws Exception {
        when(repository.findAll(OWNER, null, null)).thenReturn(List.of());
        mvc.perform(get(PATH).principal(as(EMAIL)).param("recipientUserId", "99"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(repository).findAll(OWNER, null, null);
    }

    @Test
    void unknownOrForeignNotificationReturnsNotFound() throws Exception {
        var id = UUID.randomUUID();
        when(repository.findByIdForUpdate(new NotificationId(id), OWNER)).thenReturn(Optional.empty());
        mvc.perform(patch(PATH + "/{id}", id).principal(as(EMAIL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_FOUND"));
        verify(repository).findByIdForUpdate(new NotificationId(id), OWNER);
        verify(repository, never()).save(any());
    }

    @Test
    void createsAnUnreadNotificationForTheAuthenticatedUser() throws Exception {
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post(PATH).principal(as(EMAIL))
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.recipientUserId").value("7"))
                .andExpect(jsonPath("$.type").value("HEALTH_ALERT"))
                .andExpect(jsonPath("$.channel").value("in_app"))
                .andExpect(jsonPath("$.status").value("unread"))
                .andExpect(jsonPath("$.readAt").isEmpty());
        verify(repository).save(argThat(notification -> notification.getRecipient().equals(OWNER)
                && NOW.equals(notification.getSentAt())));
    }

    @Test
    void ignoresARecipientSuppliedByTheClient() throws Exception {
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var payload = CREATE_JSON.replace("{", "{\"recipientUserId\":\"99\",");
        mvc.perform(post(PATH).principal(as(EMAIL))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipientUserId").value("7"));
        verify(repository).save(argThat(notification -> notification.getRecipient().equals(OWNER)));
    }

    @Test
    void clientSuppliedIdentityDoesNotAuthenticateARequest() throws Exception {
        var payload = CREATE_JSON.replace("{", "{\"recipientUserId\":\"7\",");
        mvc.perform(post(PATH).header("X-User-Id", "7").header("Authorization", "Bearer invalid")
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsBlankTitleBeforePersisting() throws Exception {
        mvc.perform(post(PATH).principal(as(EMAIL)).contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_JSON.replace("Animal requires attention", " ")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test
    void filtersByTypeAndStatusForTheAuthenticatedUser() throws Exception {
        when(repository.findAll(OWNER, NotificationType.HEALTH_ALERT, NotificationStatus.UNREAD))
                .thenReturn(List.of());
        mvc.perform(get(PATH).principal(as(EMAIL)).param("type", "HEALTH_ALERT").param("status", "unread"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(repository).findAll(OWNER, NotificationType.HEALTH_ALERT, NotificationStatus.UNREAD);
    }

    @Test
    void rejectsInvalidFiltersAndIdentifiers() throws Exception {
        mvc.perform(get(PATH).principal(as(EMAIL)).param("status", "other"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        mvc.perform(get(PATH).principal(as(EMAIL)).param("type", "other"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(PATH + "/{id}", "invalid").principal(as(EMAIL)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test
    void marksTheNotificationAsReadAndKeepsTheFirstReadTime() throws Exception {
        var notification = Notification.create(OWNER, NotificationType.HEALTH_ALERT,
                new NotificationContent("Title", "Description"), new RelatedEntity("Animal", "A1"),
                NOW.minusSeconds(60), null);
        when(repository.findByIdForUpdate(notification.getId(), OWNER)).thenReturn(Optional.of(notification));
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(patch(PATH + "/{id}", notification.getId().value()).principal(as(EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("read"))
                .andExpect(jsonPath("$.readAt").value(NOW.toString()));
    }

    @Test
    void aScheduledNotificationCannotBeReadYet() throws Exception {
        var notification = Notification.create(OWNER, NotificationType.VACCINE_REMINDER,
                new NotificationContent("Title", "Description"), new RelatedEntity("Animal", "A1"),
                NOW, NOW.plusSeconds(3600));
        when(repository.findByIdForUpdate(notification.getId(), OWNER)).thenReturn(Optional.of(notification));

        mvc.perform(patch(PATH + "/{id}", notification.getId().value()).principal(as(EMAIL)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"));
        verify(repository, never()).save(any());
    }

    @Test
    void lookupReturnsNotFoundForAnAbsentOrForeignNotification() throws Exception {
        var id = UUID.randomUUID();
        when(repository.findById(new NotificationId(id), OWNER)).thenReturn(Optional.empty());
        mvc.perform(get(PATH + "/{id}", id).principal(as(EMAIL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_FOUND"));
        verify(repository).findById(new NotificationId(id), OWNER);
    }
}
