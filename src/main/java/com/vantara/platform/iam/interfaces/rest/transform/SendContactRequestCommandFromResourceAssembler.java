package com.vantara.platform.iam.interfaces.rest.transform;

import com.vantara.platform.iam.domain.model.commands.SendContactRequestCommand;
import com.vantara.platform.iam.interfaces.rest.resources.ContactRequestResource;

public final class SendContactRequestCommandFromResourceAssembler {

    private SendContactRequestCommandFromResourceAssembler() {
    }

    public static SendContactRequestCommand toCommandFromResource(ContactRequestResource resource) {
        return new SendContactRequestCommand(resource.fullName(), resource.email(), resource.message());
    }
}
