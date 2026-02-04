package it.unicam.cs.mpgc.jbudget126603.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single financial transaction such as an income or an expense.
 * Each transaction has an ID, an amount, a date, a description,
 * a type (INCOME or EXPENSE), an optional recurrence type, and a list of associated tags.
 */
public class Transaction implements TransactionBase {

    private final String id;
    private Money amount;
    private LocalDate date;
    private String description;
    private Type type;
    private final List<Tag> tags;
    private final RecurrenceType recurrenceType;

    /**
     * Creates a new transaction without recurrence.
     *
     * @param id          unique identifier for the transaction
     * @param amount      amount of money involved
     * @param date        date of the transaction
     * @param description description of the transaction
     * @param type        type of transaction (INCOME or EXPENSE)
     * @param tags        list of tags associated with the transaction
     */
    public Transaction(String id,
                       Money amount,
                       LocalDate date,
                       String description,
                       Type type,
                       List<Tag> tags) {
        this(id, amount, date, description, type, tags, null);
    }

    /**
     * Creates a new transaction with optional recurrence.
     *
     * @param id             unique identifier for the transaction
     * @param amount         amount of money involved
     * @param date           date of the transaction
     * @param description    description of the transaction
     * @param type           type of transaction (INCOME or EXPENSE)
     * @param tags           list of tags associated with the transaction
     * @param recurrenceType recurrence type (can be null if not recurring)
     */
    public Transaction(String id,
                       Money amount,
                       LocalDate date,
                       String description,
                       Type type,
                       List<Tag> tags,
                       RecurrenceType recurrenceType) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.amount = Objects.requireNonNull(amount, "amount cannot be null");
        this.date = Objects.requireNonNull(date, "date cannot be null");
        this.description = Objects.requireNonNull(description, "description cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.tags = new ArrayList<>(tags);
        this.recurrenceType = recurrenceType;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Money amount() {
        return amount;
    }

    @Override
    public LocalDate date() {
        return date;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Type type() {
        return type;
    }


    @Override
    public List<Tag> tags() {
        return new ArrayList<>(tags);
    }

    /**
     * Returns the recurrence type of the transaction (null if none).
     *
     * @return recurrence type or null
     */
    public RecurrenceType recurrenceType() {
        return recurrenceType;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", tags=" + tags +
                ", recurrenceType=" + recurrenceType +
                '}';
    }
}
