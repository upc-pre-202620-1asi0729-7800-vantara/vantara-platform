package com.vantara.platform.iam.application.internal.commandservices;

import com.vantara.platform.iam.application.commandservices.ContactRequestCommandService;
import com.vantara.platform.iam.domain.model.aggregates.ContactRequest;
import com.vantara.platform.iam.domain.model.commands.SendContactRequestCommand;
import com.vantara.platform.iam.domain.model.valueobjects.ContactMessage;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import com.vantara.platform.iam.domain.repositories.ContactRequestRepository;
import com.vantara.platform.shared.application.result.ApplicationError;
import com.vantara.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class ContactRequestCommandServiceImpl implements ContactRequestCommandService {

    private final ContactRequestRepository contactRequestRepository;
    private final Clock clock;

    public ContactRequestCommandServiceImpl(ContactRequestRepository contactRequestRepository, Clock clock) {
        this.contactRequestRepository = contactRequestRepository;
        this.clock = clock;
    }

    /**
     * Stores the contact request. The confirmation email is sent by an event handler after commit.
     */
    @Override
    @Transactional
    public Result<ContactRequest, ApplicationError> handle(SendContactRequestCommand command) {
        FullName fullName;
        EmailAddress email;
        ContactMessage message;
        try {
            fullName = new FullName(command.fullName());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("fullName", e.getMessage()));
        }
        try {
            email = new EmailAddress(command.email());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("email", e.getMessage()));
        }
        try {
            message = new ContactMessage(command.message());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("message", e.getMessage()));
        }
        var request = ContactRequest.send(fullName, email, message, clock.instant());
        return Result.success(contactRequestRepository.save(request));
    }
}
