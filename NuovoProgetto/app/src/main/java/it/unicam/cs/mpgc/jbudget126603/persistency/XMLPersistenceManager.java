package it.unicam.cs.mpgc.jbudget126603.persistency;

import it.unicam.cs.mpgc.jbudget126603.model.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PersistenceManager that provides
 * XML-based persistence for transactions and tags.
 * Data is stored in two XML files:
 * one for transactions and one for tags. Each transaction may
 * reference multiple tags by their ID.
 */
public class XMLPersistenceManager implements PersistenceManager {

    private final String transactionsFilePath;
    private final String tagsFilePath;

    /**
     * Creates a new XMLPersistenceManager.
     *
     * @param transactionsFilePath the path of the XML file where transactions are stored
     * @param tagsFilePath         the path of the XML file where tags are stored
     */
    public XMLPersistenceManager(String transactionsFilePath, String tagsFilePath) {
        this.transactionsFilePath = transactionsFilePath;
        this.tagsFilePath = tagsFilePath;
    }

    /**
     * Saves the given list of transactions to the transactions XML file.
     * Each Transaction is serialized with its attributes,
     * including optional description, recurrence type, and associated tags.
     *
     * @param transactions the list of transactions to save
     */
    @Override
    public void saveTransactions(List<TransactionBase> transactions) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("transactions");
            doc.appendChild(root);

            for (TransactionBase tb : transactions) {
                if (tb instanceof Transaction t) {
                    Element txEl = doc.createElement("transaction");

                    txEl.setAttribute("id", t.id());
                    txEl.setAttribute("amount", Double.toString(t.amount().toDouble()));
                    txEl.setAttribute("date", t.date().toString());
                    txEl.setAttribute("type", t.type().name());
                    txEl.setAttribute("recurrence", t.recurrenceType() != null ? t.recurrenceType().name() : "");

                    if (t.description() != null) {
                        txEl.setAttribute("description", t.description());
                    }

                    // Tags
                    Element tagsEl = doc.createElement("tags");
                    for (Tag tag : t.tags()) {
                        Element tagEl = doc.createElement("tag");
                        tagEl.setAttribute("id", tag.id());
                        tagsEl.appendChild(tagEl);
                    }
                    txEl.appendChild(tagsEl);

                    root.appendChild(txEl);
                }
            }

            saveDocument(doc, transactionsFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all transactions from the transactions XML file.
     * Each transaction is reconstructed with its attributes and associated tags.
     * Tags are resolved against the tag list loaded from the tags XML file.
     *
     * @return the list of loaded transactions, or an empty list if the file does not exist
     */
    @Override
    public List<TransactionBase> loadTransactions() {
        List<TransactionBase> transactions = new ArrayList<>();
        try {
            List<Tag> allTags = loadTags(); // Load tags first
            File file = new File(transactionsFilePath);
            if (!file.exists()) return transactions;

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList txNodes = doc.getElementsByTagName("transaction");

            for (int i = 0; i < txNodes.getLength(); i++) {
                Element txEl = (Element) txNodes.item(i);

                String id = txEl.getAttribute("id");
                double amount = Double.parseDouble(txEl.getAttribute("amount"));
                LocalDate date = LocalDate.parse(txEl.getAttribute("date"));
                Type type = Type.valueOf(txEl.getAttribute("type"));
                String recurrenceStr = txEl.getAttribute("recurrence");
                RecurrenceType recurrence = recurrenceStr.isEmpty() ? null : RecurrenceType.valueOf(recurrenceStr);
                String description = txEl.hasAttribute("description") ? txEl.getAttribute("description") : null;

                List<Tag> txTags = new ArrayList<>();
                NodeList tagNodes = txEl.getElementsByTagName("tag");
                for (int j = 0; j < tagNodes.getLength(); j++) {
                    Element tagEl = (Element) tagNodes.item(j);
                    String tagId = tagEl.getAttribute("id");
                    allTags.stream().filter(t -> t.id().equals(tagId)).findFirst().ifPresent(txTags::add);
                }

                transactions.add(new Transaction(id, new Money(amount), date, description, type, txTags, recurrence));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Saves the given list of tags to the tags XML file.
     *
     * @param tags the list of tags to save
     */
    @Override
    public void saveTags(List<Tag> tags) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("tags");
            doc.appendChild(root);

            for (Tag tag : tags) {
                Element tagEl = doc.createElement("tag");
                tagEl.setAttribute("id", tag.id());
                tagEl.setAttribute("name", tag.name());
                if (tag.parentId() != null) {
                    tagEl.setAttribute("parentId", tag.parentId());
                }
                root.appendChild(tagEl);
            }

            saveDocument(doc, tagsFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all tags from the tags XML file.
     * Each tag is reconstructed with its ID, name, and optional parent ID.
     *
     * @return the list of loaded tags, or an empty list if the file does not exist
     */
    @Override
    public List<Tag> loadTags() {
        List<Tag> tags = new ArrayList<>();
        try {
            File file = new File(tagsFilePath);
            if (!file.exists()) return tags;

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList tagNodes = doc.getElementsByTagName("tag");

            for (int i = 0; i < tagNodes.getLength(); i++) {
                Element tagEl = (Element) tagNodes.item(i);
                String id = tagEl.getAttribute("id");
                String name = tagEl.getAttribute("name");
                String parentId = tagEl.hasAttribute("parentId") ? tagEl.getAttribute("parentId") : null;
                tags.add(new Tag(id, name, parentId));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    /**
     * Saves the given XML document to the specified file path.
     *
     * @param doc      the XML document to save
     * @param filePath the file path where the document will be saved
     * @throws TransformerException if an error occurs during the transformation
     */
    private void saveDocument(Document doc, String filePath) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
