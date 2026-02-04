package it.unicam.cs.mpgc.jbudget126603.model;

/**
 * Enumeration of possible recurrence patterns for a recurring transaction.
 */
public enum RecurrenceType {

    /** Transaction repeats every day. */
    DAILY,

    /** Transaction repeats every week. */
    WEEKLY,

    /** Transaction repeats every month. */
    MONTHLY,

    /** Transaction repeats every year. */
    YEARLY
}