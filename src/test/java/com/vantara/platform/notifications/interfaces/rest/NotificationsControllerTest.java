package com.vantara.platform.notifications.interfaces.rest;

import com.vantara.platform.iam.domain.model.aggregates.User;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.repositories.UserRepository;
import com.vantara.platform.notifications.application.internal.commandservices.NotificationCommandServiceImpl;
import com.vantara.platform.notifications.application.internal.queryservices.NotificationQueryServiceImpl;
import com.vantara.platform.notifications.domain.model.valueobjects.RecipientId;
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
        mvc = MockMvcBuilders.standaloneSetup(new NotificationsController(commands, queries, recipients)).build();
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
}
