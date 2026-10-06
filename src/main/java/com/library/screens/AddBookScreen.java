package com.library.screens;

import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddBookScreen {

    private final SupabaseService supabaseService;

    public AddBookScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title = new Label("Add New Book");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");
        bookIdField.setPrefHeight(40);

        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");
        titleField.setPrefHeight(40);

        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        authorField.setPrefHeight(40);

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        isbnField.setPrefHeight(40);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        categoryField.setPrefHeight(40);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setPrefHeight(40);

        Button saveButton =
                new Button("Save Book");

        saveButton.setPrefWidth(200);
        saveButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(200);
        backButton.setPrefHeight(40);

        Label messageLabel = new Label();

        saveButton.setOnAction(event -> {

            String bookId =
                    bookIdField.getText().trim();

            String bookTitle =
                    titleField.getText().trim();

            String author =
                    authorField.getText().trim();

            String isbn =
                    isbnField.getText().trim();

            String category =
                    categoryField.getText().trim();

            String quantityText =
                    quantityField.getText().trim();

            if (bookId.isEmpty()
                    || bookTitle.isEmpty()
                    || author.isEmpty()
                    || quantityText.isEmpty()) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;"
                );

                messageLabel.setText(
                        "Please fill all required fields."
                );

                return;
            }

            int quantity;

            try {

                quantity =
                        Integer.parseInt(quantityText);

                if (quantity <= 0) {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;"
                    );

                    messageLabel.setText(
                            "Quantity must be greater than 0."
                    );

                    return;
                }

            } catch (NumberFormatException e) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;"
                );

                messageLabel.setText(
                        "Quantity must be a number."
                );

                return;
            }

            try {

                boolean success =
                        supabaseService.addBook(
                                bookId,
                                bookTitle,
                                author,
                                isbn,
                                category,
                                quantity
                        );

                if (success) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;"
                    );

                    messageLabel.setText(
                            "Book added successfully!"
                    );

                    bookIdField.clear();
                    titleField.clear();
                    authorField.clear();
                    isbnField.clear();
                    categoryField.clear();
                    quantityField.clear();

                } else {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;"
                    );

                    messageLabel.setText(
                            "Failed to add book."
                    );
                }

            } catch (Exception e) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;"
                );

                messageLabel.setText(
                        "Error: " + e.getMessage()
                );
            }
        });

        backButton.setOnAction(event -> {

            AdminDashboard dashboard =
                    new AdminDashboard();

            dashboard.show(stage);
        });

        VBox root = new VBox(
                12,
                title,
                bookIdField,
                titleField,
                authorField,
                isbnField,
                categoryField,
                quantityField,
                saveButton,
                messageLabel,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 650);

        stage.setTitle(
                "Library Management System - Add Book"
        );

        stage.setScene(scene);
        stage.show();
    }
}