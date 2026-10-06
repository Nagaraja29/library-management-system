package com.library.screens;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchBookScreen {

    private final SupabaseService supabaseService;
    private final ObjectMapper objectMapper;

    public SearchBookScreen() {

        supabaseService = new SupabaseService();
        objectMapper = new ObjectMapper();
    }

    public void show(Stage stage, boolean fromStudent) {

        Label title =
                new Label("Search Book");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Enter Book ID or Book Title"
        );

        searchField.setPrefWidth(400);
        searchField.setPrefHeight(40);

        Button searchButton =
                new Button("Search");

        searchButton.setPrefWidth(150);
        searchButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);

        TextArea resultArea =
                new TextArea();

        resultArea.setEditable(false);
        resultArea.setPrefHeight(300);
        resultArea.setWrapText(true);

        Label messageLabel =
                new Label();

        searchButton.setOnAction(event -> {

            String searchText =
                    searchField.getText().trim();

            if (searchText.isEmpty()) {

                messageLabel.setText(
                        "Please enter a Book ID or Title."
                );

                return;
            }

            try {

                String response =
                        supabaseService.searchBooks(
                                searchText
                        );

                JsonNode books =
                        objectMapper.readTree(response);

                if (!books.isArray()
                        || books.isEmpty()) {

                    resultArea.setText(
                            "No books found."
                    );

                    return;
                }

                StringBuilder result =
                        new StringBuilder();

                for (JsonNode book : books) {

                    result.append(
                            "Book ID: "
                    ).append(
                            book.get("book_id").asText()
                    ).append("\n");

                    result.append(
                            "Title: "
                    ).append(
                            book.get("title").asText()
                    ).append("\n");

                    result.append(
                            "Author: "
                    ).append(
                            book.get("author").asText()
                    ).append("\n");

                    result.append(
                            "ISBN: "
                    ).append(
                            getValue(book, "isbn")
                    ).append("\n");

                    result.append(
                            "Category: "
                    ).append(
                            getValue(book, "category")
                    ).append("\n");

                    result.append(
                            "Total Quantity: "
                    ).append(
                            book.get("quantity").asInt()
                    ).append("\n");

                    result.append(
                            "Available Quantity: "
                    ).append(
                            book.get(
                                    "available_quantity"
                            ).asInt()
                    ).append("\n");

                    result.append(
                            "--------------------------------\n"
                    );
                }

                resultArea.setText(
                        result.toString()
                );

                messageLabel.setText(
                        "Search completed."
                );

            } catch (Exception e) {

                resultArea.setText("");

                messageLabel.setText(
                        "Error: " + e.getMessage()
                );
            }
        });

        backButton.setOnAction(event -> {

            if (fromStudent) {

                StudentDashboard dashboard =
                    new StudentDashboard();

                dashboard.show(stage, "STU001");

            } else {

                AdminDashboard dashboard =
                        new AdminDashboard();

                dashboard.show(stage);
            }
        });

        VBox root = new VBox(
                15,
                title,
                searchField,
                searchButton,
                messageLabel,
                resultArea,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Search Book"
        );

        stage.setScene(scene);
        stage.show();
    }

    private String getValue(
            JsonNode node,
            String field
    ) {

        if (node.has(field)
                && !node.get(field).isNull()) {

            return node.get(field).asText();
        }

        return "N/A";
    }
}