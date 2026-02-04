package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.AppController;
import it.unicam.cs.mpgc.jbudget126603.model.Transaction;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import it.unicam.cs.mpgc.jbudget126603.model.Type;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * View displaying the list of transactions with colored amounts, expiration column, and legend.
 * Recurrence transactions are blue, future transactions are orange,
 * incomes are green, and expenses are red.
 */
public class TransactionListView extends VBox implements ViewRefreshable {

    private final AppController controller;
    private final TableView<Transaction> table = new TableView<>();

    /**
     * Constructs the transaction list view.
     *
     * @param controller the application controller providing transactions
     */
    public TransactionListView(AppController controller) {
        this.controller = controller;
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Transactions List");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Transaction, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().id()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().type().toString()));

        TableColumn<Transaction, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(d -> {
            List<?> tags = d.getValue().tags();
            String name = tags.isEmpty() ? "Other" : ((it.unicam.cs.mpgc.jbudget126603.model.Tag) tags.get(0)).name();
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().amount().toString()));
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Transaction t = getTableView().getItems().get(getIndex());
                    setText(item);
                    if (t.recurrenceType() != null) {
                        setStyle("-fx-text-fill: blue;"); // recurrence
                    } else if (t.date().isAfter(LocalDateTime.now().toLocalDate())) {
                        setStyle("-fx-text-fill: orange;"); // future
                    } else if (t.type() == Type.INCOME) {
                        setStyle("-fx-text-fill: green;"); // income
                    } else if (t.type() == Type.EXPENSE) {
                        setStyle("-fx-text-fill: red;"); // expense
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        TableColumn<Transaction, String> expirationCol = new TableColumn<>("Expiration");
        expirationCol.setCellValueFactory(d -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime txDate = d.getValue().date().atStartOfDay();
            if (txDate.isAfter(now)) {
                Duration diff = Duration.between(now, txDate);
                long days = diff.toDays();
                long hours = diff.toHours() % 24;
                long minutes = diff.toMinutes() % 60;
                String expiration = days + "d " + hours + "h " + minutes + "m";
                return new javafx.beans.property.SimpleStringProperty(expiration);
            } else {
                return new javafx.beans.property.SimpleStringProperty("-");
            }
        });
        expirationCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
                }
            }
        });

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().date().toString()));

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().description() != null ? d.getValue().description() : ""));

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox box = new HBox(5, editBtn, delBtn);

            {
                editBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    new AddTransactionView(controller, TransactionListView.this, t).showModal();
                });

                delBtn.setOnAction(e -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    controller.removeTransaction(t.id());
                    refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, typeCol, tagCol, amountCol, expirationCol, dateCol, descCol, actionCol);

        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10));
        legend.getChildren().addAll(
                createLegendItem(Color.GREEN, "Income"),
                createLegendItem(Color.RED, "Expense"),
                createLegendItem(Color.ORANGE, "Future"),
                createLegendItem(Color.BLUE, "Recurrence")
        );

        getChildren().addAll(title, table, legend);

        refresh();
    }

    private HBox createLegendItem(Color color, String text) {
        Rectangle rect = new Rectangle(15, 15, color);
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        HBox box = new HBox(5, rect, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void refresh() {
        List<TransactionBase> baseList = controller.getAllTransactions();
        table.setItems(FXCollections.observableArrayList(baseList.stream()
                .map(t -> (Transaction) t)
                .sorted((a, b) -> {
                    String aTag = a.tags().isEmpty() ? "Other" : a.tags().get(0).name();
                    String bTag = b.tags().isEmpty() ? "Other" : b.tags().get(0).name();
                    return aTag.compareToIgnoreCase(bTag);
                })
                .toList()));
    }
}
