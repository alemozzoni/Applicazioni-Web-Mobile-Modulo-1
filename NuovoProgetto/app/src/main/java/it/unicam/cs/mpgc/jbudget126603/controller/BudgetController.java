package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.DateRange;
import it.unicam.cs.mpgc.jbudget126603.model.Money;
import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import java.util.List;

/**
 * Defines the operations for calculating and analyzing the family budget.
 */
public interface BudgetController {

    /**
     * Calculates the total balance of all transactions.
     *
     * @param transactions the list of transactions to process
     * @return the resulting balance
     */
    Money calculateBalance(List<TransactionBase> transactions);

    /**
     * Calculates the balance filtered by a specific tag.
     *
     * @param transactions the list of transactions to process
     * @param tag the tag used for filtering
     * @return the resulting balance for the tag
     */
    Money calculateBalanceByTag(List<TransactionBase> transactions, Tag tag);

    /**
     * Calculates the balance within a given date range.
     *
     * @param transactions the list of transactions to process
     * @param range the date range used for filtering
     * @return the resulting balance for the period
     */
    Money calculateBalanceByPeriod(List<TransactionBase> transactions, DateRange range);

    /**
     * Calculates the balance for a tag within a given date range.
     *
     * @param transactions the list of transactions to process
     * @param range the date range used for filtering
     * @param tag the tag used for filtering
     * @return the resulting balance for the period and tag
     */
    Money calculateBalanceByPeriodAndTag(List<TransactionBase> transactions, DateRange range, Tag tag);
}
