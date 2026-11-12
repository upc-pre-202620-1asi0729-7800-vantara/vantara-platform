package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.commands.SendVerificationCodeCommand;
import com.vantara.platform.iam.interfaces.rest.resources.SendVerificationCodeResource;

public final class SendVerificationCodeCommandFromResourceAssembler {

    private SendVerificationCodeCommandFromResourceAssembler() {
    }

    public static SendVerificationCodeCommand toCommandFromResource(SendVerificationCodeResource resource) {
        return new SendVerificationCodeCommand(resource.email());
    }
}
