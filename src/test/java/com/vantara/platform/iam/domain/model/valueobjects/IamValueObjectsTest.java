package com.vantara.platform.iam.domain.model.valueobjects;

import com.vantara.platform.iam.domain.model.entities.Role;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IamValueObjectsTest {

    @Nested
    class EmailAddressTest {

        @Test
        void isNormalized() {
            assertThat(new EmailAddress("  Rancher@Vantara.PE ").address()).isEqualTo("rancher@vantara.pe");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "   ", "sin-arroba", "a@b", "a@@b.com"})
        void rejectsInvalidAddresses(String address) {
            assertThatThrownBy(() -> new EmailAddress(address)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class RawPasswordTest {

        @Test
        void acceptsValidPasswordAndNeverPrintsIt() {
            var password = new RawPassword("Clave1234");

            assertThat(password.toString()).doesNotContain("Clave1234");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "corta1", "sinnumeros", "12345678"})
        void rejectsWeakPasswords(String value) {
            assertThatThrownBy(() -> new RawPassword(value)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void limitsLengthInBytesNotInCharacters() {
            // 40 characters of 2 bytes each = 80 bytes, above the BCrypt limit of 72 bytes
            var value = "ñ".repeat(39) + "1";

            assertThatThrownBy(() -> new RawPassword(value)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class VerificationCodeValueTest {

        @Test
        void acceptsSixDigits() {
            assertThat(new VerificationCodeValue(" 042891 ").value()).isEqualTo("042891");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {"", "12345", "1234567", "12a456"})
        void rejectsOtherFormats(String value) {
            assertThatThrownBy(() -> new VerificationCodeValue(value)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class IdentifiersAndTextsTest {

        @Test
        void userIdMustBePositive() {
            assertThat(new UserId(1L).userId()).isEqualTo(1L);
            assertThatThrownBy(() -> new UserId(0L)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new UserId(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void fullNameAndContactMessageAreTrimmedAndBounded() {
            assertThat(new FullName("  Ana Torres ").value()).isEqualTo("Ana Torres");
            assertThatThrownBy(() -> new FullName("A")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new ContactMessage("corto")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new ContactMessage("x".repeat(1001))).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void passwordHashMustNotBeBlank() {
            assertThatThrownBy(() -> new PasswordHash(" ")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class RoleTest {

        @Test
        void buildsRoleFromNameAndFallsBackToDefault() {
            assertThat(Role.toRoleFromName("role_veterinarian").getName()).isEqualTo(Roles.ROLE_VETERINARIAN);
            assertThat(Role.toRoleFromName(null)).isEqualTo(Role.getDefaultRole());
            assertThatThrownBy(() -> Role.toRoleFromName("ROLE_ADMIN")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rolesAreEqualByName() {
            assertThat(new Role(Roles.ROLE_RANCHER)).isEqualTo(Role.reconstitute(9L, Roles.ROLE_RANCHER));
        }
    }
}
