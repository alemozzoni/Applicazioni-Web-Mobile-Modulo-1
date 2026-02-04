package it.unicam.cs.mpgc.jbudget126603.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents an immutable monetary value with two decimal precision.
 * All operations return new Money instances, ensuring immutability.
 */
public class Money {
    /** Internal representation of the monetary value. */
    private final BigDecimal amount;

    /**
     * Creates a new Money from a double value.
     * Value is rounded to 2 decimal places using RoundingModeHALF_UP.
     *
     * @param value the numeric amount
     */
    public Money(double value) {
        this.amount = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Creates a newe Money from a BigDecimal.
     * Value is rounded to 2 decimal places using RoundingModeHALF_UP.
     *
     * @param value the monetary value (cannot be null)
     * @throws NullPointerException if value is null
     */
    public Money(BigDecimal value) {
        this.amount = Objects.requireNonNull(value, "Money value cannot be null")
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the monetary value as BigDecimal.
     *
     * @return monetary value
     */
    public BigDecimal value() {
        return amount;
    }

    /**
     * Returns the monetary value as a primitive double.
     *
     * @return monetary value as double
     */
    public double toDouble() {
        return amount.doubleValue();
    }

    /**
     * Returns a new Money that is the sum of this and another.
     *
     * @param other the money to add
     * @return a new Money representing the result
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }


    /**
     * Returns a string representation of the monetary value without scientific notation.
     *
     * @return string representation of amount
     */
    @Override
    public String toString() {
        return amount.toPlainString();
    }

    /**
     * Checks equality based on the monetary value.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Objects.equals(amount, money.amount);
    }

    /**
     * Returns the hash code for this object.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}