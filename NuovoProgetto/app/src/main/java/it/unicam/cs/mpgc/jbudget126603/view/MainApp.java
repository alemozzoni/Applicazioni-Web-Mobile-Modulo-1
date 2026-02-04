package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.AppController;
import it.unicam.cs.mpgc.jbudget126603.controller.TagManager;
import it.unicam.cs.mpgc.jbudget126603.persistency.XMLPersistenceManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Family Budget Management system.
 * Initializes the "it.unicam.cs.mpgc.jbudget126603.controller.AppController",
 * sets up XML persistence, and constructs the main application window with navigation buttons.
 * The view supports adding transactions, listing transactions, viewing total balance,
 * viewing statistics, and managing tags.
 */
public class MainApp extends Application {

    /** Main application controller */
    private AppController controller;

    /** Root layout of the JavaFX scene */
    private BorderPane root;

    /**
     * Starts the JavaFX application.
     * Initializes the main layout, navigation buttons, and central views.
     * Configures button actions to open different views such as AddTransactionView,
     * TransactionListView, FinalBalanceView, StatisticsView, and TagManagementView.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        XMLPersistenceManager persistenceManager =
                new XMLPersistenceManager("transactions.xml", "tags.xml");

        controller = new AppController(persistenceManager);

        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #d0e7ff, #a8d0ff);");

        VBox nav = new VBox(15);
        nav.setPadding(new Insets(20));
        nav.setStyle("-fx-background-color: linear-gradient(to bottom, #b0d4ff, #80bfff);");

        Button addBtn = new Button("Add Transaction");
        Button listBtn = new Button("Transactions List");
        Button balanceBtn = new Button("Total Balance");
        Button statsBtn = new Button("Statistics");
        Button tagsBtn = new Button("Manage Tags");

        for (Button btn : new Button[]{addBtn, listBtn, balanceBtn, statsBtn, tagsBtn}) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        }

        nav.getChildren().addAll(addBtn, listBtn, balanceBtn, statsBtn, tagsBtn);
        root.setLeft(nav);

        TransactionListView listView = new TransactionListView(controller);
        root.setCenter(listView);

        addBtn.setOnAction(e -> {
            AddTransactionView addView = new AddTransactionView(controller, listView);
            addView.showModal();
        });

        listBtn.setOnAction(e -> {
            listView.refresh();
            root.setCenter(listView);
        });

        balanceBtn.setOnAction(e -> root.setCenter(new FinalBalanceView(controller)));
        statsBtn.setOnAction(e -> root.setCenter(new StatisticsView(controller)));

        tagsBtn.setOnAction(e -> {
            TagManagementView tagView = new TagManagementView((TagManager)controller.getTagController());
            root.setCenter(tagView);
        });

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Family Budget Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
