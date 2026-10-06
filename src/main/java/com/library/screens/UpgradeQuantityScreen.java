package com.library.screens;

import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpgradeQuantityScreen {

    private final SupabaseService supabaseService;

    public UpgradeQuantityScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title =
                new Label("Upgrade Book Quantity");

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

        TextField quantityField =
                new TextField();

        quantityField.setPromptText(
                "Enter Additional Quantity"
        );

        quantityField.setPrefHeight(40);

        Button upgradeButton =
                new Button("Upgrade Quantity");

        upgradeButton.setPrefWidth(220);
        upgradeButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(220);
        backButton.setPrefHeight(40);

        Label messageLabel =
                new Label();

        upgradeButton.setOnAction(event -> {

            String bookId =
                    bookIdField.getText().trim();

            String quantityText =
                    quantityField.getText().trim();

            if (bookId.isEmpty()
                    || quantityText.isEmpty()) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;"
                );

                messageLabel.setText(
                        "Please fill all fields."
                );

                return;
            }

            int additionalQuantity;

            try {

                additionalQuantity =
                        Integer.parseInt(quantityText);

                if (additionalQuantity <= 0) {

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
                        supabaseService
                                .upgradeBookQuantity(
                                        bookId,
                                        additionalQuantity
                                );

                if (success) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;"
                    );

                    messageLabel.setText(
                            "Book quantity upgraded successfully!"
                    );

                    bookIdField.clear();
                    quantityField.clear();

                } else {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;"
                    );

                    messageLabel.setText(
                            "Book ID not found."
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
                15,
                title,
                bookIdField,
                quantityField,
                upgradeButton,
                messageLabel,
                backButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Upgrade Quantity"
        );

        stage.setScene(scene);
        stage.show();
    }
}