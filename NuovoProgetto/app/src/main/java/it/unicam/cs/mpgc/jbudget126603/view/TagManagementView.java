package it.unicam.cs.mpgc.jbudget126603.view;

import it.unicam.cs.mpgc.jbudget126603.controller.TagManager;
import it.unicam.cs.mpgc.jbudget126603.model.Tag;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * View for managing hierarchical tags with support for parent and child tags.
 */
public class TagManagementView extends VBox {

    private final TagManager tagManager;
    private final ListView<Tag> parentListView;
    private final ListView<Tag> childListView;

    /**
     * Constructs the tag management view.
     *
     * @param tagManager the tag manager responsible for persistence and retrieval
     */
    public TagManagementView(TagManager tagManager) {
        this.tagManager = tagManager;

        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Tag Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        parentListView = new ListView<>();
        childListView = new ListView<>();

        parentListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });

        childListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });

        TextField tagNameField = new TextField();
        tagNameField.setPromptText("Tag name");

        Button addParentBtn = new Button("Add Parent");
        Button addChildBtn = new Button("Add Child");

        addParentBtn.setOnAction(e -> {
            String name = tagNameField.getText();
            if (!name.isBlank()) {
                tagManager.createTag(name, null);
                refresh();
                tagNameField.clear();
            }
        });

        addChildBtn.setOnAction(e -> {
            Tag parent = parentListView.getSelectionModel().getSelectedItem();
            String name = tagNameField.getText();
            if (parent != null && !name.isBlank()) {
                tagManager.createTag(name, parent.id());
                refresh();
                tagNameField.clear();
            }
        });

        HBox inputBox = new HBox(10, tagNameField, addParentBtn, addChildBtn);
        inputBox.setAlignment(Pos.CENTER);

        HBox listsBox = new HBox(20,
                new VBox(new Label("Parent Tags"), parentListView),
                new VBox(new Label("Child Tags"), childListView));

        getChildren().addAll(title, inputBox, listsBox);
        refresh();
    }

    private void refresh() {
        parentListView.setItems(FXCollections.observableArrayList(tagManager.getRootTags()));
        Tag selectedParent = parentListView.getSelectionModel().getSelectedItem();
        if (selectedParent != null) {
            childListView.setItems(FXCollections.observableArrayList(tagManager.getChildrenTags(selectedParent.id())));
        } else {
            childListView.setItems(FXCollections.emptyObservableList());
        }

        parentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                childListView.setItems(FXCollections.observableArrayList(tagManager.getChildrenTags(newSel.id())));
            }
        });
    }
}