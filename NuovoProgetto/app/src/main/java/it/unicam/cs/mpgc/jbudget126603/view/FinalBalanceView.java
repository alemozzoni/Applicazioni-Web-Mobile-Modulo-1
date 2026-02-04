package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.AppController;
import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import it.unicam.cs.mpgc.jbudget126603.model.TransactionBase;
import it.unicam.cs.mpgc.jbudget126603.model.Type;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View displaying the total balance and a pie chart of transactions grouped by parent tag.
 * Incomes are shown in green, expenses in red, future transactions in orange.
 */
public class FinalBalanceView extends VBox {

    /**
     * Constructs the FinalBalanceView with current balance and pie chart.
     *
     * @param controller the application controller providing access to transactions
     */
    public FinalBalanceView(AppController controller) {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Total Balance");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<TransactionBase> transactions = controller.getAllTransactions();

        double balance = transactions.stream()
                .mapToDouble(t -> t.type() == Type.INCOME ? t.amount().toDouble() : -t.amount().toDouble())
                .sum();

        Label balanceLabel = new Label("Current Balance: " + balance);
        balanceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                (balance >= 0 ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));

        PieChart pieChart = new PieChart();
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(true);

        Map<String, Double> grouped = new HashMap<>();
        Map<String, String> colorMap = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (TransactionBase t : transactions) {
            String tagName = "Other";
            if (!t.tags().isEmpty()) {
                Tag tag = t.tags().get(0);
                if (tag.parentId() != null) {
                    // Find parent tag
                    Tag parent = controller.getAllTags().stream()
                            .filter(p -> p.id().equals(tag.parentId()))
                            .findFirst()
                            .orElse(tag);
                    tagName = parent.name();
                } else {
                    tagName = tag.name();
                }
            }

            double value = Math.abs(t.type() == Type.INCOME ? t.amount().toDouble() : -t.amount().toDouble());
            grouped.put(tagName, grouped.getOrDefault(tagName, 0.0) + value);

            // Determine color based on last transaction of that tag
            String color;
            if (t.date().isAfter(today)) {
                color = "orange";
            } else if (t.type() == Type.INCOME) {
                color = "green";
            } else {
                color = "red";
            }
            colorMap.put(tagName, color);
        }

        grouped.forEach((tag, value) -> pieChart.getData().add(new PieChart.Data(tag, value)));

        Platform.runLater(() -> {
            for (PieChart.Data data : pieChart.getData()) {
                String color = colorMap.getOrDefault(data.getName(), "gray");
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
        });

        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10));
        legend.getChildren().addAll(
                createLegendItem(Color.GREEN, "Incomes"),
                createLegendItem(Color.RED, "Expenses"),
                createLegendItem(Color.ORANGE, "Future Transactions")
        );

        getChildren().addAll(title, balanceLabel, pieChart, legend);
    }

    private HBox createLegendItem(Color color, String text) {
        Rectangle rect = new Rectangle(15, 15, color);
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        HBox box = new HBox(5, rect, label);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }
}