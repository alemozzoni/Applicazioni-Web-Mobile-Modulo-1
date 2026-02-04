package it.unicam.cs.mpgc.jbudget126603.model;

/**
 * Interface representing a recurring transaction.
 * Extends TransactionBase and adds a recurrence pattern.
 * This allows for transactions that repeat daily, weekly, monthly, or yearly.
 */
public interface RecurringTransaction extends TransactionBase {

    /**
     * Returns the recurrence pattern of this transaction.
     *
     * @return the recurrence type
     */
    RecurrenceType recurrencePattern();
}