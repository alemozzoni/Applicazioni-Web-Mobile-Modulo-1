package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.AppController;
import it.unicam.cs.mpgc.jbudget126603.model.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modal view for adding or editing a transaction in the application.
 * Provides input fields for amount, date, description, type, tags, and recurrence.
 * Can be used to create a new transaction or edit an existing one.
 */
public class AddTransactionView {

    /** Main application controller */
    private final AppController controller;

    /** Parent transaction list view to refresh after adding or editing a transaction */
    private final TransactionListView parentView;

    /** Transaction being edited, null if creating a new one */
    private final Transaction editingTransaction;

    /** Input field for the transaction amount */
    private final TextField amountField = new TextField();

    /** Date picker for the transaction date */
    private final DatePicker datePicker = new DatePicker();

    /** Input field for the transaction description */
    private final TextField descriptionField = new TextField();

    /** ComboBox for selecting the transaction type */
    private final ComboBox<Type> typeCombo = new ComboBox<>();

    /** ComboBox for selecting the parent tag */
    private final ComboBox<Tag> parentTagCombo = new ComboBox<>();

    /** ComboBox for selecting the subtag */
    private final ComboBox<Tag> subtagCombo = new ComboBox<>();

    /** ComboBox for selecting the recurrence type */
    private final ComboBox<RecurrenceType> recurrenceCombo = new ComboBox<>();

    /** Stage for the modal dialog */
    private Stage stage;

    /**
     * Constructs an AddTransactionView for adding a new transaction.
     *
     * @param controller the main application controller
     * @param parentView the transaction list view to refresh after saving
     */
    public AddTransactionView(AppController controller, TransactionListView parentView) {
        this(controller, parentView, null);
    }

    /**
     * Constructs an AddTransactionView for editing an existing transaction.
     *
     * @param controller the main application controller
     * @param parentView the transaction list view to refresh after saving
     * @param transaction the transaction to edit, or null to create a new one
     */
    public AddTransactionView(AppController controller, TransactionListView parentView, Transaction transaction) {
        this.controller = controller;
        this.parentView = parentView;
        this.editingTransaction = transaction;
    }

    /**
     * Displays the modal dialog.
     * Initializes all input fields and pre-fills them if editing an existing transaction.
     * Blocks user interaction with other windows until the dialog is closed.
     */
    public void showModal() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(editingTransaction == null ? "Add Transaction" : "Edit Transaction");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);

        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);

        grid.add(new Label("Type:"), 0, 2);
        typeCombo.getItems().addAll(Type.values());
        grid.add(typeCombo, 1, 2);

        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        grid.add(new Label("Parent Tag:"), 0, 4);
        grid.add(parentTagCombo, 1, 4);

        grid.add(new Label("Subtag:"), 0, 5);
        grid.add(subtagCombo, 1, 5);

        parentTagCombo.setPromptText("Select Parent");
        subtagCombo.setPromptText("Select Subtag");

        updateTagCombos();

        grid.add(new Label("Recurrence:"), 0, 6);
        recurrenceCombo.getItems().addAll(RecurrenceType.values());
        recurrenceCombo.setPromptText("Optional");
        grid.add(recurrenceCombo, 1, 6);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> saveTransaction());
        grid.add(saveBtn, 1, 7);

        if (editingTransaction != null) {
            amountField.setText(String.valueOf(editingTransaction.amount().value()));
            datePicker.setValue(editingTransaction.date());
            descriptionField.setText(editingTransaction.description());
            typeCombo.setValue(editingTransaction.type());
            if (!editingTransaction.tags().isEmpty()) {
                Tag first = editingTransaction.tags().get(0);
                if (first.parentId() == null) {
                    parentTagCombo.setValue(first);
                    if (editingTransaction.tags().size() > 1)
                        subtagCombo.setValue(editingTransaction.tags().get(1));
                } else {
                    subtagCombo.setValue(first);
                    parentTagCombo.setValue(controller.getAllTags().stream()
                            .filter(t -> t.id().equals(first.parentId())).findFirst().orElse(null));
                }
            }
            recurrenceCombo.setValue(editingTransaction.recurrenceType());
        }

        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Updates the parent and sub-tag combo boxes with the current list of tags
     * from the controller.
     */
    private void updateTagCombos() {
        List<Tag> parentTags = controller.getAllTags().stream()
                .filter(t -> t.parentId() == null)
                .collect(Collectors.toList());
        parentTagCombo.setItems(FXCollections.observableArrayList(parentTags));

        List<Tag> subTags = controller.getAllTags().stream()
                .filter(t -> t.parentId() != null)
                .collect(Collectors.toList());
        subtagCombo.setItems(FXCollections.observableArrayList(subTags));
    }

    /**
     * Reads the data from the input fields, creates or updates a Transaction object,
     * and sends it to the controller. Closes the modal dialog after saving.
     */
    private void saveTransaction() {
        Money amount = new Money(Double.parseDouble(amountField.getText()));
        LocalDate date = datePicker.getValue();
        Type type = typeCombo.getValue();
        String desc = descriptionField.getText();

        Tag parent = parentTagCombo.getValue();
        Tag subtag = subtagCombo.getValue();

        List<Tag> tags;
        if (subtag != null) {
            tags = List.of(subtag);
        } else if (parent != null) {
            tags = List.of(parent);
        } else {
            Tag other = new Tag("other-id", "Other", null);
            tags = List.of(other);
        }

        RecurrenceType recurrence = recurrenceCombo.getValue();

        Transaction t = new Transaction(
                editingTransaction != null ? editingTransaction.id() : String.valueOf(System.currentTimeMillis()),
                amount, date, desc, type, tags, recurrence
        );

        if (editingTransaction != null) {
            controller.updateTransaction(editingTransaction.id(), t);
        } else {
            controller.addTransaction(t);
        }

        parentView.refresh();
        stage.close();
    }
}