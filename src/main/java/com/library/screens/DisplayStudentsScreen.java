package com.library.screens;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.service.SupabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayStudentsScreen {

    private final SupabaseService supabaseService;
    private final ObjectMapper objectMapper;

    public DisplayStudentsScreen() {
        supabaseService = new SupabaseService();
        objectMapper = new ObjectMapper();
    }

    public void show(Stage stage) {

        Label title =
                new Label("Registered Students");

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        TableView<Student> table =
                new TableView<>();

        TableColumn<Student, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        TableColumn<Student, String> registerColumn =
                new TableColumn<>("Register Number");

        registerColumn.setCellValueFactory(
                new PropertyValueFactory<>("registerNumber")
        );

        TableColumn<Student, String> emailColumn =
                new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        TableColumn<Student, String> departmentColumn =
                new TableColumn<>("Department");

        departmentColumn.setCellValueFactory(
                new PropertyValueFactory<>("department")
        );

        TableColumn<Student, String> phoneColumn =
                new TableColumn<>("Phone");

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        table.getColumns().addAll(
                nameColumn,
                registerColumn,
                emailColumn,
                departmentColumn,
                phoneColumn
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        Button refreshButton =
                new Button("Refresh");

        refreshButton.setPrefWidth(150);
        refreshButton.setPrefHeight(40);

        Button backButton =
                new Button("Back");

        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);

        Label messageLabel =
                new Label();

        Runnable loadStudents = () -> {

            try {

                String response =
                        supabaseService.getAllStudents();

                JsonNode students =
                        objectMapper.readTree(response);

                ObservableList<Student> studentList =
                        FXCollections.observableArrayList();

                for (JsonNode student : students) {

                    studentList.add(
                            new Student(
                                    getValue(
                                            student,
                                            "name"
                                    ),
                                    getValue(
                                            student,
                                            "register_number"
                                    ),
                                    getValue(
                                            student,
                                            "email"
                                    ),
                                    getValue(
                                            student,
                                            "department"
                                    ),
                                    getValue(
                                            student,
                                            "phone"
                                    )
                            )
                    );
                }

                table.setItems(studentList);

                messageLabel.setStyle(
                        "-fx-text-fill: green;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        studentList.size()
                                + " student(s) found."
                );

            } catch (Exception e) {

                messageLabel.setStyle(
                        "-fx-text-fill: red;" +
                        "-fx-font-size: 14px;"
                );

                messageLabel.setText(
                        "Error: " + e.getMessage()
                );
            }
        };

        refreshButton.setOnAction(
                event -> loadStudents.run()
        );

        backButton.setOnAction(event -> {

            AdminDashboard dashboard =
                    new AdminDashboard();

            dashboard.show(stage);
        });

        VBox root =
                new VBox(
                        15,
                        title,
                        table,
                        refreshButton,
                        messageLabel,
                        backButton
                );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene =
                new Scene(root, 1000, 650);

        stage.setTitle(
                "Library Management System - Students"
        );

        stage.setScene(scene);
        stage.show();

        // Load students automatically
        loadStudents.run();
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

    public static class Student {

        private final String name;
        private final String registerNumber;
        private final String email;
        private final String department;
        private final String phone;

        public Student(
                String name,
                String registerNumber,
                String email,
                String department,
                String phone
        ) {

            this.name = name;
            this.registerNumber = registerNumber;
            this.email = email;
            this.department = department;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getRegisterNumber() {
            return registerNumber;
        }

        public String getEmail() {
            return email;
        }

        public String getDepartment() {
            return department;
        }

        public String getPhone() {
            return phone;
        }
    }
}