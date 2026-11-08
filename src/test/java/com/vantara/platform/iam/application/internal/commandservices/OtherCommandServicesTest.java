package com.vantara.platform.iam.application.internal.commandservices;

import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.model.commands.SeedRolesCommand;
import com.vantara.platform.iam.domain.model.commands.SendContactRequestCommand;
import com.vantara.platform.iam.domain.model.entities.Role;
import com.vantara.platform.iam.domain.model.valueobjects.Roles;
import com.vantara.platform.iam.domain.repositories.ContactRequestRepository;
import com.vantara.platform.iam.domain.repositories.RoleRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OtherCommandServicesTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void seedRolesCreatesOnlyTheMissingRoles() {
        var roleRepository = mock(RoleRepository.class);
        when(roleRepository.existsByName(Roles.ROLE_RANCHER)).thenReturn(true);
        when(roleRepository.existsByName(Roles.ROLE_VETERINARIAN)).thenReturn(false);
        when(roleRepository.existsByName(Roles.ROLE_FINAL_USER)).thenReturn(false);
        var service = new RoleCommandServiceImpl(roleRepository);

        service.handle(new SeedRolesCommand());

        var saved = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository, times(2)).save(saved.capture());
        assertEquals(Roles.ROLE_VETERINARIAN, saved.getAllValues().get(0).getName());
        assertEquals(Roles.ROLE_FINAL_USER, saved.getAllValues().get(1).getName());
    }

    @Test
    void seedRolesIsIdempotent() {
        var roleRepository = mock(RoleRepository.class);
        when(roleRepository.existsByName(any())).thenReturn(true);

        new RoleCommandServiceImpl(roleRepository).handle(new SeedRolesCommand());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void sendContactRequestStoresTheRequest() {
        var repository = mock(ContactRequestRepository.class);
        when(repository.save(any(ContactRequest.class))).thenAnswer(i -> i.getArgument(0));
        var service = new ContactRequestCommandServiceImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));

        var result = service.handle(new SendContactRequestCommand(
                "Ana Perez", "Ana@Example.com", "I want to know more about Vantara"));

        assertTrue(result.isSuccess());
        var saved = ((Result.Success<ContactRequest, ApplicationError>) result).value();
        assertEquals("ana@example.com", saved.getEmail().address());
        assertEquals(NOW, saved.getSentAt());
    }

    @Test
    void sendContactRequestRejectsInvalidFields() {
        var repository = mock(ContactRequestRepository.class);
        var service = new ContactRequestCommandServiceImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));

        var result = service.handle(new SendContactRequestCommand("Ana Perez", "ana@example.com", "short"));

        assertTrue(result.isFailure());
        assertEquals("VALIDATION_ERROR", ((Result.Failure<ContactRequest, ApplicationError>) result).error().code());
        verify(repository, never()).save(any());
    }
}
