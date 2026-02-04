package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.AppController;
import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.Transaction;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import it.unicam.cs.mpgc.jbudget126603.model.Type;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * View displaying statistics of transactions by parent tag and date range.
 */
public class StatisticsView extends VBox {

    public StatisticsView(AppController controller) {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Statistics");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        // ComboBox for selecting parent tag
        ComboBox<Tag> tagCombo = new ComboBox<>();
        List<Tag> parentTags = controller.getAllTags().stream()
                .filter(t -> t.parentId() == null)
                .toList();
        tagCombo.getItems().add(null); // optional: no filter
        tagCombo.getItems().addAll(parentTags);
        tagCombo.setPromptText("Select Parent Tag");

        tagCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "<All>" : item.name());
            }
        });
        tagCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "<All>" : item.name());
            }
        });

        Button generateBtn = new Button("Generate Chart");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tag");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);

        generateBtn.setOnAction(e -> {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            Tag selectedParent = tagCombo.getValue();

            List<TransactionBase> filtered = controller.getAllTransactions().stream()
                    .filter(t -> (start == null || !t.date().isBefore(start)) &&
                            (end == null || !t.date().isAfter(end)) &&
                            (selectedParent == null || (t instanceof Transaction tx && !tx.tags().isEmpty() &&
                                    tx.tags().stream().anyMatch(tag ->
                                            tag.parentId() != null && tag.parentId().equals(selectedParent.id())
                                                    || tag.id().equals(selectedParent.id())
                                    ))))
                    .toList();

            Map<String, Double> totalsByTag = filtered.stream()
                    .collect(Collectors.groupingBy(
                            t -> {
                                if (t instanceof Transaction tx && !tx.tags().isEmpty()) {
                                    Tag tag = tx.tags().get(0);
                                    if (tag.parentId() != null) {
                                        Tag parent = controller.getAllTags().stream()
                                                .filter(p -> p.id().equals(tag.parentId()))
                                                .findFirst().orElse(tag);
                                        return parent.name();
                                    } else return tag.name();
                                }
                                return "Other";
                            },
                            Collectors.summingDouble(t -> t.type() == Type.INCOME ? t.amount().toDouble() : -t.amount().toDouble())
                    ));

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            totalsByTag.forEach((tagName, amount) -> series.getData().add(new XYChart.Data<>(tagName, amount)));
            barChart.getData().add(series);

            // Color the bars: green for income, red for expense
            Platform.runLater(() -> {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String tagName = data.getXValue();
                    double total = data.getYValue().doubleValue();
                    String color = total >= 0 ? "green" : "red";
                    data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                }
            });
        });

        HBox filters = new HBox(10, startDatePicker, endDatePicker, tagCombo, generateBtn);
        filters.setPadding(new Insets(10));

        getChildren().addAll(title, filters, barChart);
    }
}
