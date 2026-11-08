package com.vantara.platform.iam.domain.model.aggregates;

import com.vantara.platform.iam.domain.model.events.ContactRequestSentEvent;
import com.vantara.platform.iam.domain.model.valueobjects.ContactMessage;
import com.vantara.platform.iam.domain.model.valueobjects.EmailAddress;
import com.vantara.platform.iam.domain.model.valueobjects.FullName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactRequestTest {

    private static final Instant NOW = Instant.parse("2026-11-07T10:00:00Z");

    @Test
    void sendCreatesRequestAndRecordsEvent() {
        var request = ContactRequest.send(
                new FullName("Ana Torres"),
                new EmailAddress("ana@mail.com"),
                new ContactMessage("Quisiera saber mas del plan."),
                NOW);

        assertThat(request.getSentAt()).isEqualTo(NOW);
        assertThat(request.domainEvents())
                .singleElement()
                .isEqualTo(new ContactRequestSentEvent("Ana Torres", "ana@mail.com", NOW));
    }

    @Test
    void sendRejectsMissingData() {
        assertThatThrownBy(() -> ContactRequest.send(
                null, new EmailAddress("ana@mail.com"), new ContactMessage("Mensaje de prueba."), NOW))
                .isInstanceOf(NullPointerException.class);
    }
}
