package com.vantara.platform.reports.domain.exceptions;

/**
 * Raised when the livestock data needed by a report cannot be read. Reports fail closed: they never answer
 * with partial or invented data.
 */
public class ReportSourceUnavailableException extends RuntimeException {

    public ReportSourceUnavailableException() {
        super("Livestock data is not available to generate reports");
    }
}
