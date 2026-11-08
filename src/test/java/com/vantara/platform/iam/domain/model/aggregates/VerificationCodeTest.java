package com.vantara.platform.iam.domain.model.aggregates;

import com.vantara.platform.iam.domain.model.events.VerificationCodeIssuedEvent;
import com.vantara.platform.iam.domain.model.valueobjects.UserId;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationCodeValue;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationOutcome;
import com.vantara.platform.iam.domain.model.valueobjects.VerificationPurpose;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationCodeTest {

    private static final Instant NOW = Instant.parse("2026-11-07T10:00:00Z");
    private static final VerificationCodeValue CODE = new VerificationCodeValue("123456");
    private static final VerificationCodeValue WRONG = new VerificationCodeValue("654321");

    private static VerificationCode newCode() {
        return VerificationCode.issue(
                new UserId(1L), CODE, VerificationPurpose.EMAIL_VERIFICATION, NOW, Duration.ofMinutes(15));
    }

    @Test
    void issueRecordsEventWithoutExposingTheCode() {
        var code = newCode();

        assertThat(code.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofMinutes(15)));
        assertThat(code.domainEvents()).singleElement().isEqualTo(new VerificationCodeIssuedEvent(
                1L, "EMAIL_VERIFICATION", NOW.plus(Duration.ofMinutes(15)), NOW));
    }

    @Test
    void issueRejectsNonPositiveTimeToLive() {
        assertThatThrownBy(() -> VerificationCode.issue(
                new UserId(1L), CODE, VerificationPurpose.PASSWORD_RECOVERY, NOW, Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void correctCodeIsVerifiedAndCanOnlyBeUsedOnce() {
        var code = newCode();

        assertThat(code.verify(CODE, NOW.plusSeconds(30))).isEqualTo(VerificationOutcome.VERIFIED);
        assertThat(code.isConsumed()).isTrue();
        assertThat(code.verify(CODE, NOW.plusSeconds(31))).isEqualTo(VerificationOutcome.ALREADY_USED);
    }

    @Test
    void expiredCodeIsRejected() {
        var code = newCode();

        assertThat(code.verify(CODE, NOW.plus(Duration.ofMinutes(15)))).isEqualTo(VerificationOutcome.EXPIRED);
        assertThat(code.isConsumed()).isFalse();
    }

    @Test
    void wrongCodeCountsAttemptsAndBlocksAfterTheLimit() {
        var code = newCode();

        for (int attempt = 1; attempt <= VerificationCode.MAX_FAILED_ATTEMPTS; attempt++) {
            assertThat(code.verify(WRONG, NOW)).isEqualTo(VerificationOutcome.INVALID);
            assertThat(code.getFailedAttempts()).isEqualTo(attempt);
        }

        assertThat(code.verify(CODE, NOW)).isEqualTo(VerificationOutcome.ATTEMPTS_EXCEEDED);
        assertThat(code.isConsumed()).isFalse();
    }

    @Test
    void nullCandidateIsAnInvalidAttempt() {
        var code = newCode();

        assertThat(code.verify(null, NOW)).isEqualTo(VerificationOutcome.INVALID);
    }

    @Test
    void reconstituteKeepsPersistedStateWithoutEvents() {
        var code = VerificationCode.reconstitute(
                3L, new UserId(1L), CODE, VerificationPurpose.PASSWORD_RECOVERY,
                NOW, NOW.plusSeconds(900), null, 2);

        assertThat(code.getFailedAttempts()).isEqualTo(2);
        assertThat(code.domainEvents()).isEmpty();
    }
}
