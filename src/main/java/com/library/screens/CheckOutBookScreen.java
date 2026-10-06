package com.library.screens;

import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckOutBookScreen {

    private final SupabaseService supabaseService;

    public CheckOutBookScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title =
                new Label("Check Out Book");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TextField bookIdField =
                new TextField();

        bookIdField.setPromptText(
                "Enter Book ID"
        );

        bookIdField.setPrefHeight(40);

        TextField registerNumberField =
                new TextField();

        registerNumberField.setPromptText(
                "Enter Student Register Number"
        );

        registerNumberField.setPrefHeight(40);

        Button checkOutButton =
                new Button("Check Out Book");

        checkOutButton.setPrefWidth(250);
        checkOutButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(250);
        backButton.setPrefHeight(40);

        Label messageLabel =
                new Label();

        checkOutButton.setOnAction(event -> {

            String bookId =
                    bookIdField.getText().trim();

            String registerNumber =
                    registerNumberField.getText().trim();

            if (bookId.isEmpty()
                    || registerNumber.isEmpty()) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;"
                );

                messageLabel.setText(
                        "Please enter Book ID and Register Number."
                );

                return;
            }

            try {

                boolean success =
                        supabaseService.checkOutBook(
                                bookId,
                                registerNumber
                        );

                if (success) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;"
                    );

                    messageLabel.setText(
                            "Book returned successfully!"
                    );

                    bookIdField.clear();

                } else {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;"
                    );

                    messageLabel.setText(
                            "No active borrowing found."
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

            StudentDashboard dashboard =
            new StudentDashboard();

            dashboard.show(stage, "STU001");
        });

        VBox root =
                new VBox(
                        15,
                        title,
                        bookIdField,
                        registerNumberField,
                        checkOutButton,
                        messageLabel,
                        backButton
                );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Check Out"
        );

        stage.setScene(scene);
        stage.show();
    }
}