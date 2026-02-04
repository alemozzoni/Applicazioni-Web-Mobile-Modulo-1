package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import it.unicam.cs.mpgc.jbudget126603.model.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of StatisticsController.
 * Provides statistical analysis on transactions, including totals, averages, and counts.
 */
public class StatisticsManager implements StatisticsController {

    /**
     * Generates total income and total expense from a list of transactions.
     *
     * @param transactions the list of transactions
     * @return a map with keys "Income" and "Expense" containing total amounts
     */
    @Override
    public Map<String, Double> generateIncomeExpenseStats(List<TransactionBase> transactions) {
        double income = transactions.stream()
                .filter(t -> t.type() == Type.INCOME)
                .mapToDouble(t -> t.amount().toDouble())
                .sum();

        double expense = transactions.stream()
                .filter(t -> t.type() == Type.EXPENSE)
                .mapToDouble(t -> t.amount().toDouble())
                .sum();

        Map<String, Double> stats = new HashMap<>();
        stats.put("Income", income);
        stats.put("Expense", expense);
        return stats;
    }

    /**
     * Generates statistics grouped by tag, summing the amounts of all transactions with each tag.
     *
     * @param transactions the list of transactions
     * @return a map where keys are tag names and values are total amounts
     */
    @Override
    public Map<String, Double> generateStatsByTag(List<TransactionBase> transactions) {
        return transactions.stream()
                .flatMap(t -> t.tags().stream()
                        .map(tag -> Map.entry(tag.name(), t.amount().toDouble())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingDouble(Map.Entry::getValue)));
    }

    /**
     * Generates basic amount statistics (average, min, max) from a list of transactions.
     *
     * @param transactions the list of transactions
     * @return a map containing "average", "min", and "max" values
     */
    @Override
    public Map<String, Double> generateAmountStatistics(List<TransactionBase> transactions) {
        DoubleSummaryStatistics stats = transactions.stream()
                .mapToDouble(t -> t.amount().toDouble())
                .summaryStatistics();

        Map<String, Double> result = new HashMap<>();
        result.put("average", stats.getAverage());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        return result;
    }

    /**
     * Counts how many transactions are associated with each tag.
     *
     * @param transactions the list of transactions
     * @return a map where keys are tag names and values are counts
     */
    @Override
    public Map<String, Long> generateTransactionCountByTag(List<TransactionBase> transactions) {
        return transactions.stream()
                .flatMap(t -> t.tags().stream().map(tag -> tag.name()))
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
    }
}
