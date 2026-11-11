package com.vantara.platform.reproductive.interfaces.rest.transform;

import com.vantara.platform.reproductive.domain.model.commands.CalfRegistration;
import com.vantara.platform.reproductive.domain.model.commands.RegisterCalvingCommand;
import com.vantara.platform.reproductive.domain.model.valueobjects.CalfSex;
import com.vantara.platform.reproductive.interfaces.rest.resources.RegisterCalvingResource;

/**
 * Assembler that translates {@link RegisterCalvingResource} into {@link RegisterCalvingCommand}.
 */
public final class RegisterCalvingCommandFromResourceAssembler {

    private RegisterCalvingCommandFromResourceAssembler() {
    }

    public static RegisterCalvingCommand toCommandFromResource(Long recordId, RegisterCalvingResource resource) {
        var calf = resource.calf() == null
                ? null
                : new CalfRegistration(
                        resource.calf().name(),
                        resource.calf().earTag(),
                        CalfSex.from(resource.calf().sex()),
                        resource.calf().weightKg());
        return new RegisterCalvingCommand(recordId, resource.calvedOn(), resource.outcome(), calf);
    }
}
