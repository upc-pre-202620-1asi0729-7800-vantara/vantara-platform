package com.vantara.platform.reproductive.interfaces.rest.transform;

import com.vantara.platform.reproductive.domain.model.commands.RegisterWeaningCommand;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterWeaningResource;

/**
 * Assembler that translates {@link RegisterWeaningResource} into {@link RegisterWeaningCommand}.
 */
public final class RegisterWeaningCommandFromResourceAssembler {

    private RegisterWeaningCommandFromResourceAssembler() {
    }

    public static RegisterWeaningCommand toCommandFromResource(Long recordId, RegisterWeaningResource resource) {
        return new RegisterWeaningCommand(recordId, resource.weanedOn(), resource.weaningWeightKg());
    }
}
