package it.unicam.cs.mpgc.jbudget126603.persistency;

import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import java.util.List;

/**
 * Defines the contract for persistence operations for transactions and tags.
 */
public interface PersistenceManager {

    /**
     * Saves the given list of transactions.
     *
     * @param transactions the list of transactions to save
     */
    void saveTransactions(List<TransactionBase> transactions);

    /**
     * Loads all transactions from persistence.
     *
     * @return the list of transactions
     */
    List<TransactionBase> loadTransactions();

    /**
     * Saves the given list of tags.
     *
     * @param tags the list of tags to save
     */
    void saveTags(List<Tag> tags);

    /**
     * Loads all tags from persistence.
     *
     * @return the list of tags
     */
    List<Tag> loadTags();
}
