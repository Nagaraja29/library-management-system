package com.library.screens;

import com.library.service.SupabaseService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterStudentScreen {

    private final SupabaseService supabaseService;

    public RegisterStudentScreen() {
        supabaseService = new SupabaseService();
    }

    public void show(Stage stage) {

        Label title =
                new Label("Register Student");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TextField nameField =
                new TextField();
        nameField.setPromptText("Student Name");
        nameField.setPrefHeight(40);

        TextField registerNumberField =
                new TextField();
        registerNumberField.setPromptText("Register Number");
        registerNumberField.setPrefHeight(40);

        TextField emailField =
                new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefHeight(40);

        PasswordField passwordField =
                new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);

        TextField departmentField =
                new TextField();
        departmentField.setPromptText("Department");
        departmentField.setPrefHeight(40);

        TextField phoneField =
                new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setPrefHeight(40);

        Button registerButton =
                new Button("Register Student");

        registerButton.setPrefWidth(250);
        registerButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(250);
        backButton.setPrefHeight(40);

        Label messageLabel =
                new Label();

        registerButton.setOnAction(event -> {

            String name =
                    nameField.getText().trim();

            String registerNumber =
                    registerNumberField.getText().trim();

            String email =
                    emailField.getText().trim();

            String password =
                    passwordField.getText();

            String department =
                    departmentField.getText().trim();

            String phone =
                    phoneField.getText().trim();

            // Validate required fields
            if (name.isEmpty()
                    || registerNumber.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        "Please fill all required fields."
                );

                return;
            }

            if (password.length() < 6) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        "Password must be at least 6 characters."
                );

                return;
            }

            try {

                boolean success =
                        supabaseService.registerStudent(
                                name,
                                registerNumber,
                                email,
                                password,
                                department,
                                phone
                        );

                if (success) {

                    messageLabel.setStyle(
                            "-fx-text-fill: green;" +
                            "-fx-font-size: 14px;"
                    );

                    messageLabel.setText(
                            "Student registered successfully!"
                    );

                    nameField.clear();
                    registerNumberField.clear();
                    emailField.clear();
                    passwordField.clear();
                    departmentField.clear();
                    phoneField.clear();

                } else {

                    messageLabel.setStyle(
                            "-fx-text-fill: red;" +
                            "-fx-font-size: 14px;"
                    );

                    messageLabel.setText(
                            "Failed to register student."
                    );
                }

            } catch (Exception e) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
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

        VBox root =
                new VBox(
                        12,
                        title,
                        nameField,
                        registerNumberField,
                        emailField,
                        passwordField,
                        departmentField,
                        phoneField,
                        registerButton,
                        backButton,
                        messageLabel
                );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 650);

        stage.setTitle(
                "Library Management System - Register Student"
        );

        stage.setScene(scene);
        stage.show();
    }
}