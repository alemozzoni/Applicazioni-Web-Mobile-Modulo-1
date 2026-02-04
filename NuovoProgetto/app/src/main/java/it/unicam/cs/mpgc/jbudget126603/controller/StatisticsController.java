package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import java.util.List;
import java.util.Map;

/**
 * Defines the operations for generating statistics from transactions.
 */
public interface StatisticsController {

    /**
     * Generates statistics for total income and expenses.
     *
     * @param transactions the list of transactions to analyze
     * @return a map with keys "Income" and "Expense"
     */
    Map<String, Double> generateIncomeExpenseStats(List<TransactionBase> transactions);

    /**
     * Generates statistics grouped by tags.
     *
     * @param transactions the list of transactions to analyze
     * @return a map with tag names as keys and totals as values
     */
    Map<String, Double> generateStatsByTag(List<TransactionBase> transactions);

    /**
     * Generates statistics about average, minimum, and maximum transaction amounts.
     *
     * @param transactions the list of transactions to analyze
     * @return a map with keys "average", "min", and "max"
     */
    Map<String, Double> generateAmountStatistics(List<TransactionBase> transactions);

    /**
     * Generates the number of transactions grouped by tags.
     *
     * @param transactions the list of transactions to analyze
     * @return a map with tag names as keys and counts as values
     */
    Map<String, Long> generateTransactionCountByTag(List<TransactionBase> transactions);
}
