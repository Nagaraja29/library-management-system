package com.library.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard {

    public void show(Stage stage) {

        Label title = new Label("Admin Dashboard");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Library Management System"
        );

        subtitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #666666;"
        );

        Button searchBookButton =
                new Button("Search Book");

        Button addBookButton =
                new Button("Add New Book");

        Button upgradeQuantityButton =
                new Button("Upgrade Book Quantity");

        Button registerStudentButton =
                new Button("Register Student");

        Button displayStudentsButton =
                new Button("Display All Students");

        Button logoutButton =
                new Button("Logout");

        Button[] buttons = {
                searchBookButton,
                addBookButton,
                upgradeQuantityButton,
                registerStudentButton,
                displayStudentsButton,
                logoutButton
        };

        for (Button button : buttons) {

            button.setPrefWidth(300);
            button.setPrefHeight(45);

            button.setStyle(
                    "-fx-font-size: 15px;"
            );
        }

        // Temporary actions
        searchBookButton.setOnAction(event -> {

            SearchBookScreen searchBookScreen =
                new SearchBookScreen();

            searchBookScreen.show(stage, false);
        });

        addBookButton.setOnAction(event -> {

            AddBookScreen addBookScreen =
                new AddBookScreen();

            addBookScreen.show(stage);
        });

       upgradeQuantityButton.setOnAction(event -> {

            UpgradeQuantityScreen screen =
                    new UpgradeQuantityScreen();

            screen.show(stage);
        });

        registerStudentButton.setOnAction(event -> {

            RegisterStudentScreen screen =
                    new RegisterStudentScreen();

            screen.show(stage);
        });

       displayStudentsButton.setOnAction(event -> {

            DisplayStudentsScreen screen =
                    new DisplayStudentsScreen();

            screen.show(stage);
        });

        // Logout
        logoutButton.setOnAction(event -> {

            LoginScreen loginScreen =
                    new LoginScreen();

            loginScreen.show(stage);
        });

        VBox root = new VBox(
                15,
                title,
                subtitle,
                searchBookButton,
                addBookButton,
                upgradeQuantityButton,
                registerStudentButton,
                displayStudentsButton,
                logoutButton
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Admin"
        );

        stage.setScene(scene);
        stage.show();
    }
}