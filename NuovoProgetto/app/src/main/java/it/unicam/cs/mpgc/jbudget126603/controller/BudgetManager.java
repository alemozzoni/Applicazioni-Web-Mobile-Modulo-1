package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.DateRange;
import it.unicam.cs.mpgc.jbudget126603.model.Money;
import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import java.util.List;

/**
 * Implementation of BudgetController.
 * Provides methods to calculate balances over all transactions,
 * filtered by tag, date range, or both.
 */
public class BudgetManager implements BudgetController {

    /**
     * Calculates the total balance from a list of transactions.
     *
     * @param transactions the list of transactions
     * @return the resulting balance as Money
     */
    @Override
    public Money calculateBalance(List<TransactionBase> transactions) {
        return transactions.stream()
                .map(TransactionBase::amount)
                .reduce(new Money(0), Money::add);
    }

    /**
     * Calculates the balance of transactions associated with a specific tag.
     *
     * @param transactions the list of transactions
     * @param tag          the tag used to filter transactions
     * @return the resulting balance as Money
     */
    @Override
    public Money calculateBalanceByTag(List<TransactionBase> transactions, Tag tag) {
        return transactions.stream()
                .filter(t -> t.tags().contains(tag))
                .map(TransactionBase::amount)
                .reduce(new Money(0), Money::add);
    }

    /**
     * Calculates the balance of transactions within a given date range.
     *
     * @param transactions the list of transactions
     * @param range        the date range filter
     * @return the resulting balance as Money
     */
    @Override
    public Money calculateBalanceByPeriod(List<TransactionBase> transactions, DateRange range) {
        return transactions.stream()
                .filter(t -> range.contains(t.date()))
                .map(TransactionBase::amount)
                .reduce(new Money(0), Money::add);
    }

    /**
     * Calculates the balance of transactions that match both a date range and a tag.
     *
     * @param transactions the list of transactions
     * @param range        the date range filter
     * @param tag          the tag filter
     * @return the resulting balance as Money
     */
    @Override
    public Money calculateBalanceByPeriodAndTag(List<TransactionBase> transactions, DateRange range, Tag tag) {
        return transactions.stream()
                .filter(t -> range.contains(t.date()) && t.tags().contains(tag))
                .map(TransactionBase::amount)
                .reduce(new Money(0), Money::add);
    }
}
