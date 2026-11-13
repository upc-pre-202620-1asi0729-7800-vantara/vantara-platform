package com.vantara.platform.reproductive.interfaces.rest.transform;

import com.vantara.platform.reproductive.domain.model.commands.RegisterDryOffCommand;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterDryOffResource;

/**
 * Assembler that translates {@link RegisterDryOffResource} into {@link RegisterDryOffCommand}.
 */
public final class RegisterDryOffCommandFromResourceAssembler {

    private RegisterDryOffCommandFromResourceAssembler() {
    }

    public static RegisterDryOffCommand toCommandFromResource(Long recordId, RegisterDryOffResource resource) {
        return new RegisterDryOffCommand(recordId, resource.dryOffOn());
    }
}
