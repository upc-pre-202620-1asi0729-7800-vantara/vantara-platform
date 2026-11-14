package com.vantara.platform.reports.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Inclusive period used to filter a report. A missing bound leaves that side open.
 *
 * @param startsOn first day of the period, or null
 * @param endsOn   last day of the period, or null
 */
public record ReportPeriod(LocalDate startsOn, LocalDate endsOn) {

    public ReportPeriod {
        if (startsOn != null && endsOn != null && endsOn.isBefore(startsOn)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }

    public boolean contains(LocalDate date) {
        if (date == null) {
            return false;
        }
        return (startsOn == null || !date.isBefore(startsOn))
                && (endsOn == null || !date.isAfter(endsOn));
    }
}
