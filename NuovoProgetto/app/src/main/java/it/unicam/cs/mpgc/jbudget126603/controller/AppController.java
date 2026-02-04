package it.unicam.cs.mpgc.jbudget126603.controller;

import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import it.unicam.cs.mpgc.jbudget126603.persistency.PersistenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central application controller (facade).
 * Loads data at construction time and is the single point that persists state.
 */
public class AppController {

    private final PersistenceManager persistenceManager;
    private final List<TransactionBase> allTransactions;
    private final TagController tagController;
    private final BudgetController budgetController;
    private final StatisticsController statisticsController;

    /**
     * Constructs the AppController and loads persisted data.
     *
     * @param persistenceManager the persistence manager to use
     */
    public AppController(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        // load all transactions once and keep in-memory
        List<TransactionBase> loaded = persistenceManager.loadTransactions();
        this.allTransactions = new ArrayList<>(loaded == null ? List.of() : loaded);

        // load tags into the TagManager (assumes TagManager has a constructor with PersistenceManager)
        this.tagController = new TagManager(persistenceManager);
        // Managers for budget/statistics can be simple instances using in-memory data
        this.budgetController = new BudgetManager();
        this.statisticsController = new StatisticsManager();
    }

    /**
     * Adds a transaction if not already present (by id). Persists all transactions on success.
     *
     * @param transaction transaction to add
     * @return true if added, false if a transaction with same id already exists
     */
    public synchronized boolean addTransaction(TransactionBase transaction) {
        Optional<TransactionBase> found = getTransactionById(transaction.id());
        if (found.isPresent()) return false;
        allTransactions.add(transaction);
        persistTransactions();
        return true;
    }

    /**
     * Updates an existing transaction (matched by id). Persists all transactions on success.
     *
     * @param id                 id of the transaction to update
     * @param updatedTransaction new transaction replacing the old one
     * @return true if updated (existing), false otherwise
     */
    public synchronized boolean updateTransaction(String id, TransactionBase updatedTransaction) {
        for (int i = 0; i < allTransactions.size(); i++) {
            if (allTransactions.get(i).id().equals(id)) {
                allTransactions.set(i, updatedTransaction);
                persistTransactions();
                return true;
            }
        }
        return false;
    }

    /**
     * Removes transaction by id. Persists all transactions on success.
     *
     * @param id id of the transaction to remove
     * @return true if removed, false if not found
     */
    public synchronized boolean removeTransaction(String id) {
        boolean removed = allTransactions.removeIf(t -> t.id().equals(id));
        if (removed) persistTransactions();
        return removed;
    }

    /**
     * Retrieves a transaction by id.
     *
     * @param id the transaction id
     * @return optional with found transaction or empty
     */
    public Optional<TransactionBase> getTransactionById(String id) {
        return allTransactions.stream().filter(t -> t.id().equals(id)).findFirst();
    }

    /**
     * Returns a defensive copy of all transactions.
     *
     * @return list of transactions
     */
    public List<TransactionBase> getAllTransactions() {
        return new ArrayList<>(allTransactions);
    }

    private void persistTransactions() {
        // Persist everything; persistenceManager is responsible to write to disk
        persistenceManager.saveTransactions(new ArrayList<>(allTransactions));
    }


    public TagController getTagController() {
        return tagController;
    }


    public List<Tag> getAllTags() {
        return tagController.getAllTags();
    }

}

