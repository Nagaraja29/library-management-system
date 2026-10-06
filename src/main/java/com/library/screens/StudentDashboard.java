package com.library.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StudentDashboard {

    public void show(
        Stage stage,
        String registerNumber
) {

        Label title =
                new Label("Student Dashboard");

        title.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label("Library Management System");

        subtitle.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #666666;"
        );

        Button searchBookButton =
                new Button("Search Book");

        Button borrowingHistoryButton =
                new Button("My Borrowing History");

        Button checkInButton =
                new Button("Check In Book");

        Button checkOutButton =
                new Button("Check Out Book");

        Button logoutButton =
                new Button("Logout");

        Button[] buttons = {
                searchBookButton,
                checkInButton,
                checkOutButton,
                borrowingHistoryButton,
                logoutButton
};

        

        for (Button button : buttons) {

            button.setPrefWidth(300);
            button.setPrefHeight(45);
            button.setStyle(
                    "-fx-font-size: 15px;"
            );
        }

        searchBookButton.setOnAction(event -> {

                    SearchBookScreen screen =
                new SearchBookScreen();

        screen.show(stage, true);   
        });
        borrowingHistoryButton.setOnAction(event -> {

            BorrowingHistoryScreen screen =
                    new BorrowingHistoryScreen();

            screen.show(
                    stage,
                    registerNumber
            );
        });

        checkInButton.setOnAction(event -> {

            CheckInBookScreen screen =
                    new CheckInBookScreen();

            screen.show(stage);
        });

        checkOutButton.setOnAction(event -> {

            CheckOutBookScreen screen =
                    new CheckOutBookScreen();

            screen.show(stage);
        });

        logoutButton.setOnAction(event -> {

            LoginScreen loginScreen =
                    new LoginScreen();

            loginScreen.show(stage);
        });

        VBox root =
                new VBox(
                        15,
                        title,
                        subtitle,
                        searchBookButton,
                        checkInButton,
                        checkOutButton,
                        logoutButton
                );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 900, 600);

        stage.setTitle(
                "Library Management System - Student"
        );

        stage.setScene(scene);
        stage.show();
    }
}