package com.vantara.platform.livestockmanagement.interfaces.rest;

import tools.jackson.databind.ObjectMapper;
import com.vantara.platform.iam.interfaces.acl.AuthenticatedUserIdentity;
import com.vantara.platform.livestockmanagement.application.commandservices.LivestockManagementCommandService;
import com.vantara.platform.livestockmanagement.application.queries.GetAllAnimalsQuery;
import com.vantara.platform.livestockmanagement.application.queryservices.LivestockManagementQueryService;
import com.vantara.platform.livestockmanagement.domain.model.aggregates.Animal;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.AnimalWeight;
import com.vantara.platform.livestockmanagement.domain.model.valueobjects.EarTag;
import com.vantara.platform.livestockmanagement.interfaces.acl.LivestockContextFacade;
import org.junit.jupiter.api.Test;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LivestockControllerOwnershipTest {

    @Test
    void eachAuthenticatedRancherOnlySeesTheirAnimalsAndCannotFetchAnotherRanchersAnimal() {
        var queries = mock(LivestockManagementQueryService.class);
        var commands = mock(LivestockManagementCommandService.class);
        var identity = mock(AuthenticatedUserIdentity.class);
        var facade = mock(LivestockContextFacade.class);
        var animalA = animalOwnedBy("a", "animal-a");
        var animalB = animalOwnedBy("b", "animal-b");
        when(queries.handle(any(GetAllAnimalsQuery.class))).thenReturn(List.of(animalA, animalB));
        var principalA = principal("a@example.test");
        var principalB = principal("b@example.test");
        when(identity.rancherId(principalA)).thenReturn(Optional.of("a"));
        when(identity.rancherId(principalB)).thenReturn(Optional.of("b"));

        var controller = new LivestockController(commands, queries, new ObjectMapper(), identity, facade);
        var resultsA = (List<?>) controller.getAnimals(principalA).getBody();
        var resultsB = (List<?>) controller.getAnimals(principalB).getBody();

        assertEquals(1, resultsA.size());
        assertEquals("animal-a", ((com.vantara.platform.livestockmanagement.interfaces.rest.resources.AnimalResource) resultsA.get(0)).id());
        assertEquals(1, resultsB.size());
        assertEquals("animal-b", ((com.vantara.platform.livestockmanagement.interfaces.rest.resources.AnimalResource) resultsB.get(0)).id());
        assertNotEquals(200, controller.getAnimal("animal-b", principalA).getStatusCode().value());
    }

    private static Animal animalOwnedBy(String owner, String id) {
        var animal = mock(Animal.class);
        when(animal.getRancherId()).thenReturn(owner);
        when(animal.getId()).thenReturn(id);
        when(animal.getLotId()).thenReturn("lot-" + owner);
        var earTag = mock(EarTag.class);
        when(earTag.value()).thenReturn("tag-" + owner);
        when(animal.getEarTag()).thenReturn(earTag);
        when(animal.getName()).thenReturn("Animal " + owner);
        when(animal.getSpecies()).thenReturn("bovine");
        when(animal.getBreed()).thenReturn("breed");
        when(animal.getSex()).thenReturn("female");
        when(animal.getBirthDate()).thenReturn(LocalDate.of(2020, 1, 1));
        var weight = mock(AnimalWeight.class);
        when(weight.value()).thenReturn(BigDecimal.TEN);
        when(animal.getWeight()).thenReturn(weight);
        when(animal.getStatus()).thenReturn("active");
        when(animal.getRegisteredAt()).thenReturn(LocalDate.of(2020, 1, 1));
        return animal;
    }

    private static Principal principal(String name) {
        return () -> name;
    }
}
