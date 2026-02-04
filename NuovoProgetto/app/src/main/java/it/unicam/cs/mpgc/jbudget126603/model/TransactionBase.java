package it.unicam.cs.mpgc.jbudget126603.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Base interface that defines the contract for all transactions.
 * Provides the fundamental methods common to any transaction,
 * whether single or recurring.
 */
public interface TransactionBase {

    /** Returns the unique identifier of the transaction. */
    String id();

    /** Returns the amount of the transaction. */
    Money amount();

    /** Returns the date of the transaction. */
    LocalDate date();

    /** Returns the description of the transaction. */
    String description();

    /** Returns the type of the transaction (INCOME or EXPENSE). */
    Type type();

    /** Returns the tags associated with the transaction. */
    List<Tag> tags();
}