package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.commands.RecoverPasswordCommand;
import com.vantara.platform.iam.interfaces.rest.resources.RecoverPasswordResource;

public final class RecoverPasswordCommandFromResourceAssembler {

    private RecoverPasswordCommandFromResourceAssembler() {
    }

    public static RecoverPasswordCommand toCommandFromResource(RecoverPasswordResource resource) {
        return new RecoverPasswordCommand(resource.email(), resource.code(), resource.newPassword());
    }
}
