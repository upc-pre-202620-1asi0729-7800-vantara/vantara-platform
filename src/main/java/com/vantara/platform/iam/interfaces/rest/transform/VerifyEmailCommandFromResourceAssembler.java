package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.commands.VerifyEmailCommand;
import com.vantara.platform.iam.interfaces.rest.resources.VerifyEmailResource;

public final class VerifyEmailCommandFromResourceAssembler {

    private VerifyEmailCommandFromResourceAssembler() {
    }

    public static VerifyEmailCommand toCommandFromResource(VerifyEmailResource resource) {
        return new VerifyEmailCommand(resource.email(), resource.code());
    }
}
