package com.library.screens;
import com.library.screens.AdminDashboard;
import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {

    private final SupabaseService supabaseService;

    public LoginScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title = new Label("Library Management System");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Login to continue");

        subtitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #666666;"
        );

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setPrefHeight(40);

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(250);
        loginButton.setPrefHeight(40);

        Label messageLabel = new Label();

        messageLabel.setStyle(
                "-fx-text-fill: red;" +
                "-fx-font-size: 14px;"
        );

        // LOGIN BUTTON
        loginButton.setOnAction(event -> {

            String email = emailField.getText().trim();
            String password = passwordField.getText();

            // Check empty fields
            if (email.isEmpty() || password.isEmpty()) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        "Please enter email and password."
                );

                return;
            }

            try {

                // Verify email and password with Supabase
                String role =
                        supabaseService.login(email, password);

                // Invalid login
                if (role == null) {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;" +
                            "-fx-font-size: 14px;"
                    );

                    messageLabel.setText(
                            "Invalid email or password."
                    );

                }

                else if (role.equalsIgnoreCase("ADMIN")) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;" +
                            "-fx-font-size: 14px;"
                    );

                    messageLabel.setText(
                            "Login successful! Admin account verified."
                    );

                    // Open Admin Dashboard
                    AdminDashboard dashboard =
                            new AdminDashboard();

                    dashboard.show(stage);
                } else if (role.equalsIgnoreCase("STUDENT")) {

                    StudentDashboard dashboard =
                    new StudentDashboard();

                    dashboard.show(stage, "STU001");
                }

            } catch (Exception e) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        "Connection error: " + e.getMessage()
                );
            }
        });

        VBox root = new VBox(
                15,
                title,
                subtitle,
                emailField,
                passwordField,
                loginButton,
                messageLabel
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(
                root,
                900,
                600
        );

        stage.setTitle(
                "Library Management System - Login"
        );

        stage.setScene(scene);
        stage.show();
    }
}