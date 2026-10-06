package com.library.screens;

import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CheckInBookScreen {

    private final SupabaseService supabaseService;

    public CheckInBookScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title =
                new Label("Check In Book");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TextField bookIdField =
                new TextField();

        bookIdField.setPromptText(
                "Enter Book ID"
        );

        bookIdField.setPrefWidth(350);
        bookIdField.setPrefHeight(40);

        TextField registerNumberField =
                new TextField();

        registerNumberField.setPromptText(
                "Enter Student Register Number"
        );

        registerNumberField.setPrefWidth(350);
        registerNumberField.setPrefHeight(40);

        Button checkInButton =
                new Button("Check In Book");

        checkInButton.setPrefWidth(250);
        checkInButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(250);
        backButton.setPrefHeight(40);

        Label messageLabel =
                new Label();

        checkInButton.setOnAction(event -> {

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
                        supabaseService.checkInBook(
                                bookId,
                                registerNumber
                        );

                if (success) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;"
                    );

                    messageLabel.setText(
                            "Book checked in successfully!"
                    );

                    bookIdField.clear();

                } else {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;"
                    );

                    messageLabel.setText(
                            "Book unavailable or student not found."
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
                        checkInButton,
                        messageLabel,
                        backButton
                );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Check In"
        );

        stage.setScene(scene);
        stage.show();
    }
}