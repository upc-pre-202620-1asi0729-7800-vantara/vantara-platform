package com.vantara.platform.reproductive.interfaces.rest.transform;

import com.vantara.platform.reproductive.domain.model.commands.RegisterPregnancyCommand;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterPregnancyResource;

/**
 * Assembler that translates {@link RegisterPregnancyResource} into {@link RegisterPregnancyCommand}.
 */
public final class RegisterPregnancyCommandFromResourceAssembler {

    private RegisterPregnancyCommandFromResourceAssembler() {
    }

    public static RegisterPregnancyCommand toCommandFromResource(RegisterPregnancyResource resource) {
        return new RegisterPregnancyCommand(resource.animalId(), resource.confirmedOn(), resource.expectedCalvingOn());
    }
}
