
package com.vantara.platform.veterinaryandhealth.application.internal.authorization;

import com.vantara.platform.livestockmanagement.interfaces.acl.AnimalRecord;

public record AccessibleAnimalRecord(
        String appointmentId,
        AnimalRecord animal
) {
}
