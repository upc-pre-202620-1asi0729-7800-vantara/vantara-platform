package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.notifications.application.internal.commandservices.NotificationCommandServiceImpl;
import com.vantara.platform.notifications.application.internal.queryservices.NotificationQueryServiceImpl;
import com.vantara.platform.notifications.domain.model.aggregates.Notification;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationStatus;
import com.vantara.platform.notifications.domain.model.valueobjects.NotificationType;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
import com.vantara.platform.shared.interfaces.rest.GlobalExceptionHandler;
import org.springframework.http.MediaType;
import com.vantara.platform.notifications.domain.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
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
        var users = mock(UserRepository.class);
        var user = mock(User.class);
        when(user.getId()).thenReturn(OWNER.value());
        when(users.findByEmail(new EmailAddress(EMAIL))).thenReturn(Optional.of(user));
        var beans = new StaticListableBeanFactory();
        beans.addBean("users", users);
        var recipients = new AuthenticatedNotificationRecipient(beans.getBeanProvider(UserRepository.class));
        var commands = new NotificationCommandServiceImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));
        var queries = new NotificationQueryServiceImpl(repository);
        mvc = MockMvcBuilders.standaloneSetup(new NotificationsController(commands, queries, recipients))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void anonymousRequestsDoNotReachPersistence() throws Exception {
        mvc.perform(get(PATH)).andExpect(status().isUnauthorized());
        verifyNoInteractions(repository);
    }

    @Test
    void listIsScopedToTheAuthenticatedUser() throws Exception {
        when(repository.findAll(OWNER, null, null)).thenReturn(List.of());
        mvc.perform(get(PATH).principal(() -> EMAIL).param("recipientUserId", "99"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(repository).findAll(OWNER, null, null);
    }

    @Test
    void unknownOrForeignNotificationReturnsNotFound() throws Exception {
        var id = UUID.randomUUID();
        when(repository.markAsRead(id, OWNER, NOW)).thenReturn(Optional.empty());
        mvc.perform(patch(PATH + "/{id}", id).principal(() -> EMAIL))
                .andExpect(status().isNotFound());
        verify(repository).markAsRead(id, OWNER, NOW);
    }

    @Test
    void createsAnUnreadNotificationForTheAuthenticatedUser() throws Exception {
        when(repository.add(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mvc.perform(post(PATH).principal(() -> EMAIL)
                        .contentType(MediaType.APPLICATION_JSON).content(CREATE_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.recipientUserId").value("7"))
                .andExpect(jsonPath("$.type").value("HEALTH_ALERT"))
                .andExpect(jsonPath("$.channel").value("in_app"))
                .andExpect(jsonPath("$.status").value("unread"))
                .andExpect(jsonPath("$.readAt").isEmpty());
        verify(repository).add(argThat(notification -> notification.recipient().equals(OWNER)
                && NOW.equals(notification.sentAt())));
    }

    @Test
    void rejectsCreationForADifferentRecipient() throws Exception {
        var payload = CREATE_JSON.replace("{", "{\"recipientUserId\":\"99\",");
        mvc.perform(post(PATH).principal(() -> EMAIL)
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());
        verifyNoInteractions(repository);
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
        mvc.perform(post(PATH).principal(() -> EMAIL).contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_JSON.replace("Animal requires attention", " ")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test
    void filtersByTypeAndStatusForTheAuthenticatedUser() throws Exception {
        when(repository.findAll(OWNER, NotificationType.HEALTH_ALERT, NotificationStatus.UNREAD))
                .thenReturn(List.of());
        mvc.perform(get(PATH).principal(() -> EMAIL).param("type", "HEALTH_ALERT").param("status", "unread"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(repository).findAll(OWNER, NotificationType.HEALTH_ALERT, NotificationStatus.UNREAD);
    }

    @Test
    void rejectsInvalidFiltersAndIdentifiers() throws Exception {
        mvc.perform(get(PATH).principal(() -> EMAIL).param("status", "other"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(PATH).principal(() -> EMAIL).param("type", "other"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(PATH + "/{id}", "invalid").principal(() -> EMAIL))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test
    void lookupReturnsNotFoundForAnAbsentOrForeignNotification() throws Exception {
        var id = UUID.randomUUID();
        when(repository.findById(id, OWNER)).thenReturn(Optional.empty());
        mvc.perform(get(PATH + "/{id}", id).principal(() -> EMAIL))
                .andExpect(status().isNotFound());
        verify(repository).findById(id, OWNER);
    }
}
