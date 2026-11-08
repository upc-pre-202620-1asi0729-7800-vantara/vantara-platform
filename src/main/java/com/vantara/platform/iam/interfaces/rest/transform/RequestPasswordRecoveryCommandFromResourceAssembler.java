package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.commands.RequestPasswordRecoveryCommand;
import com.vantara.platform.iam.interfaces.rest.resources.RequestPasswordRecoveryResource;

public final class RequestPasswordRecoveryCommandFromResourceAssembler {

    private RequestPasswordRecoveryCommandFromResourceAssembler() {
    }

    public static RequestPasswordRecoveryCommand toCommandFromResource(RequestPasswordRecoveryResource resource) {
        return new RequestPasswordRecoveryCommand(resource.email());
    }
}
