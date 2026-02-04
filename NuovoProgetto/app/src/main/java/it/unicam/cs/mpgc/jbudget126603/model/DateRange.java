package it.unicam.cs.mpgc.jbudget126603.model;

import java.time.LocalDate;

/**
 * Represents an immutable range of dates with an optional start and end.
 * Useful for filtering transactions within a specific period.
 * If start or end is null, the range is considered unbounded
 * in that direction.
 */
public final class DateRange {
    /** Start date of the range (inclusive), may be null. */
    private final LocalDate start;

    /** End date of the range (inclusive), may be null. */
    private final LocalDate end;

    /**
     * Creates a new DateRange.
     *
     * @param start start date (nullable, inclusive)
     * @param end   end date (nullable, inclusive)
     * @throws IllegalArgumentException if both are non-null and start is after end
     */
    public DateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Checks if a given date falls within this range.
     *
     * @param date the date to test (cannot be null)
     * @return true if the date is within the range, false otherwise
     */
    public boolean contains(LocalDate date) {
        return (start == null || !date.isBefore(start)) &&
                (end == null || !date.isAfter(end));
    }
}
