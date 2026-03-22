package com.mumbai.ui;

import com.mumbai.dao.StudentDAO;
import com.mumbai.model.*;
import com.mumbai.service.PerformancePredictor;
import com.mumbai.util.DBConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import java.sql.*;
import java.util.List;

public class MainApp extends Application {

    // ── Colours ───────────────────────────────────────────
    private static final String BLUE   = "#1565C0";
    private static final String ORANGE = "#E65100";
    private static final String GREEN  = "#2E7D32";
    private static final String BG     = "#EEF2FF";
    private static final String WHITE  = "#FFFFFF";
    private static final String SAFFRON = "#FF6F00";

    private ComboBox<Student> studentBox = new ComboBox<>();
    private LineChart<Number, Number> sgpaChart;
    private BarChart<String, Number>  subjectChart;

    // prediction labels
    private Label lblCgpa      = new Label("--");
    private Label lblPct       = new Label("--");
    private Label lblGrade     = new Label("--");
    private Label lblClass     = new Label("--");
    private Label lblTrend     = new Label("--");
    private Label lblConf      = new Label("--");
    private TextArea recArea   = new TextArea("Select a student and click Predict.");

    @Override
    public void start(Stage stage) {
        stage.setTitle("Mumbai University — Engineering Performance Predictor");
        stage.setMaximized(true);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        tabs.getTabs().addAll(
            makeTab("🏠  Dashboard",       buildDashboard()),
            makeTab("👨‍🏫  Teacher Panel",   buildTeacherPanel()),
            makeTab("🎓  Student Self-Check", buildSelfCheck()),
            makeTab("📊  Marks Report",     buildMarksReport())
        );

        Scene scene = new Scene(tabs, Color.web(BG));
        stage.setScene(scene);
        stage.show();
        loadStudents();
    }

    private Tab makeTab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title, content);
        return t;
    }

    // ══════════════════════════════════════════════════════
    // TAB 1 — DASHBOARD
    // ══════════════════════════════════════════════════════
    private BorderPane buildDashboard() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setTop(buildHeader());
        root.setCenter(buildDashCenter());
        root.setBottom(buildRecPanel());
        return root;
    }

    private VBox buildHeader() {
        VBox hdr = new VBox(6);
        hdr.setStyle("-fx-background-color: " + BLUE + "; -fx-padding: 14;");

        Label title = lbl("🎓  Mumbai University — B.E. Performance Predictor", 20, true, Color.WHITE);
        Label sub   = lbl("Powered by Machine Learning (Linear Regression) | All India Rank System", 12, false, Color.LIGHTBLUE);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label sl = lbl("Student:", 13, true, Color.WHITE);

        studentBox.setPrefWidth(380);
        studentBox.setOnAction(e -> onStudentSelected());

        Button predictBtn = btn("🔮  Predict Performance", ORANGE);
        predictBtn.setOnAction(e -> doPrediction());

        Button addBtn = btn("➕  Add Student", GREEN);
        addBtn.setOnAction(e -> showAddStudentDialog());

        Button refreshBtn = btn("🔄  Refresh", "#546E7A");
        refreshBtn.setOnAction(e -> { studentBox.getItems().clear(); loadStudents(); });

        row.getChildren().addAll(sl, studentBox, predictBtn, addBtn, refreshBtn);
        hdr.getChildren().addAll(title, sub, row);
        return hdr;
    }

    private HBox buildDashCenter() {
        HBox center = new HBox(12);
        center.setPadding(new Insets(10));

        // Left: SGPA chart
        VBox chartBox = buildSgpaChartBox();

        // Right: Prediction results
        VBox resultsBox = buildResultsBox();

        HBox.setHgrow(chartBox, Priority.ALWAYS);
        center.getChildren().addAll(chartBox, resultsBox);
        return center;
    }

    private VBox buildSgpaChartBox() {
        NumberAxis xAxis = new NumberAxis(0.5, 8.5, 1);
        xAxis.setLabel("Semester");
        NumberAxis yAxis = new NumberAxis(0, 10, 1);
        yAxis.setLabel("SGPA (out of 10)");

        sgpaChart = new LineChart<>(xAxis, yAxis);
        sgpaChart.setTitle("Semester-wise SGPA Trend");
        sgpaChart.setAnimated(true);
        sgpaChart.setPrefHeight(400);
        VBox.setVgrow(sgpaChart, Priority.ALWAYS);

        VBox box = card();
        box.getChildren().add(sgpaChart);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox buildResultsBox() {
        VBox box = card();
        box.setPrefWidth(340);
        box.setSpacing(10);

        box.getChildren().add(lbl("📊 Prediction Results", 15, true, Color.web(BLUE)));
        box.getChildren().add(new Separator());

        box.getChildren().add(resultRow("Predicted CGPA (out of 10):", lblCgpa));
        box.getChildren().add(resultRow("Predicted Percentage:",        lblPct));
        box.getChildren().add(resultRow("Grade:",                       lblGrade));
        box.getChildren().add(resultRow("Class Category:",              lblClass));
        box.getChildren().add(resultRow("Performance Trend:",           lblTrend));
        box.getChildren().add(resultRow("Model Confidence:",            lblConf));

        return box;
    }

    private HBox resultRow(String label, Label value) {
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", 12));
        l.setMinWidth(200);
        value.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        value.setTextFill(Color.web(BLUE));
        HBox row = new HBox(8, l, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildRecPanel() {
        VBox panel = new VBox(6);
        panel.setStyle("-fx-background-color: #FFF8E1; -fx-background-radius: 8; -fx-padding: 12;");
        panel.setPadding(new Insets(8, 10, 8, 10));

        Label h = lbl("🎯  AI Recommendations & Study Tips", 13, true, Color.web(SAFFRON));
        recArea.setWrapText(true);
        recArea.setPrefHeight(90);
        recArea.setEditable(false);
        recArea.setStyle("-fx-font-size: 12;");

        panel.getChildren().addAll(h, recArea);
        return panel;
    }

    // ══════════════════════════════════════════════════════
    // TAB 2 — TEACHER PANEL
    // ══════════════════════════════════════════════════════
    private VBox buildTeacherPanel() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: " + BG + ";");

        root.getChildren().add(lbl("👨‍🏫  Teacher Dashboard — Manage Students & Marks", 18, true, Color.web(BLUE)));
        root.getChildren().add(new Separator());

        // Student list table
        TableView<Student> table = buildStudentTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox btnRow = new HBox(10);
        Button addStu  = btn("➕ Add New Student", GREEN);
        Button addMark = btn("📝 Add Marks for Student", BLUE);
        Button addAtt  = btn("📋 Add Attendance",  "#7B1FA2");
        Button refresh = btn("🔄 Refresh", "#546E7A");

        addStu.setOnAction(e  -> showAddStudentDialog());
        addMark.setOnAction(e -> showAddMarksDialog(table));
        addAtt.setOnAction(e  -> showAddAttendanceDialog(table));
        refresh.setOnAction(e -> refreshStudentTable(table));

        btnRow.getChildren().addAll(addStu, addMark, addAtt, refresh);
        root.getChildren().addAll(btnRow, table);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<Student> buildStudentTable() {
        TableView<Student> table = new TableView<>();
        table.setStyle("-fx-font-size: 12;");

        TableColumn<Student, String> cName   = col("Name",        "name",             180);
        TableColumn<Student, String> cRoll   = col("Roll No.",    "rollNumber",       120);
        TableColumn<Student, String> cBranch = col("Branch",      "branch",           160);
        TableColumn<Student, Integer>cSem    = col("Sem",         "currentSem",        60);
        TableColumn<Student, Double> cCgpa   = col("CGPA (/10)",  "cgpa",              90);
        TableColumn<Student, Double> cPct    = col("Percentage",  "totalPercentage",  100);

        table.getColumns().addAll(cName, cRoll, cBranch, cSem, cCgpa, cPct);
        refreshStudentTable(table);
        return table;
    }

    private void refreshStudentTable(TableView<Student> table) {
        table.setItems(FXCollections.observableArrayList(StudentDAO.getAllStudents()));
    }

    // ══════════════════════════════════════════════════════
    // TAB 3 — STUDENT SELF-CHECK
    // ══════════════════════════════════════════════════════
    private VBox buildSelfCheck() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + BG + ";");

        root.getChildren().add(lbl("🎓  Student Self-Check Portal", 18, true, Color.web(BLUE)));
        root.getChildren().add(lbl("Enter your Roll Number to check your performance & get predictions", 13, false, Color.GRAY));
        root.getChildren().add(new Separator());

        // Roll number entry
        HBox searchRow = new HBox(10);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        TextField rollField = new TextField();
        rollField.setPromptText("Enter Roll Number (e.g. 2021CE001)");
        rollField.setPrefWidth(280);
        rollField.setStyle("-fx-font-size: 13;");
        Button searchBtn = btn("🔍  Check My Performance", BLUE);

        searchRow.getChildren().addAll(
            lbl("Roll Number:", 13, true, Color.web(BLUE)),
            rollField, searchBtn
        );

        // Result area
        VBox resultArea = new VBox(12);
        resultArea.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 16;");
        resultArea.setMinHeight(400);
        resultArea.getChildren().add(lbl("👆 Enter your roll number above to see your results.", 13, false, Color.GRAY));

        searchBtn.setOnAction(e -> {
            String roll = rollField.getText().trim();
            if (roll.isEmpty()) { showAlert("Please enter your roll number!"); return; }
            Student s = StudentDAO.getByRoll(roll);
            if (s == null) { showAlert("Student not found! Check your roll number."); return; }
            showSelfCheckResult(resultArea, s);
        });

        ScrollPane scroll = new ScrollPane(resultArea);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(searchRow, scroll);
        return root;
    }

    private void showSelfCheckResult(VBox area, Student s) {
        area.getChildren().clear();

        // Student info card
        VBox infoCard = card();
        infoCard.getChildren().add(lbl("👤  " + s.getName(), 16, true, Color.web(BLUE)));
        infoCard.getChildren().add(lbl("Roll: " + s.getRollNumber() + "  |  Branch: " + s.getBranch() +
            "  |  Sem: " + s.getCurrentSem() + "  |  Year: " + s.getYearOfStudy(), 12, false, Color.GRAY));
        infoCard.getChildren().add(new Separator());

        // CGPA / Percentage
        HBox stats = new HBox(30);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
            statBox("Current CGPA", String.format("%.2f / 10", s.getCgpa()), BLUE),
            statBox("Percentage",   String.format("%.1f%%", s.getTotalPercentage()), GREEN),
            statBox("Class",        PredictionResult.getClassCategory(s.getCgpa()), ORANGE)
        );
        infoCard.getChildren().add(stats);

        // Subject-wise marks for current semester
        List<SubjectMark> marks = StudentDAO.getMarksBySemester(s.getStudentId(), s.getCurrentSem());
        if (!marks.isEmpty()) {
            infoCard.getChildren().add(lbl("\n📚 Semester " + s.getCurrentSem() + " Subject-wise Performance:", 13, true, Color.web(BLUE)));

            // Subject progress bars
            for (SubjectMark m : marks) {
                HBox subRow = new HBox(10);
                subRow.setAlignment(Pos.CENTER_LEFT);
                Label subName = new Label(m.getSubjectName());
                subName.setMinWidth(220);
                subName.setFont(Font.font("Segoe UI", 12));

                ProgressBar pb = new ProgressBar(m.getTotalMarks() / 100.0);
                pb.setPrefWidth(200);
                pb.setPrefHeight(18);
                String color = m.getTotalMarks() >= 70 ? "#2E7D32" : m.getTotalMarks() >= 50 ? "#E65100" : "#C62828";
                pb.setStyle("-fx-accent: " + color + ";");

                Label pct = new Label(String.format("%.0f%%  %s (%.1f)", m.getTotalMarks(), m.getGrade(), m.getGradePoint()));
                pct.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

                subRow.getChildren().addAll(subName, pb, pct);
                infoCard.getChildren().add(subRow);
            }
        }

        // Prediction for next semester
        List<Double> sgpas = StudentDAO.getSgpaPerSemester(s.getStudentId());
        if (sgpas.size() >= 2) {
            infoCard.getChildren().add(new Separator());
            infoCard.getChildren().add(lbl("🔮 Prediction for Next Semester:", 13, true, Color.web(BLUE)));

            double att = StudentDAO.getAvgAttendance(s.getStudentId());
            PerformancePredictor predictor = new PerformancePredictor();
            PredictionResult result = predictor.predict(sgpas, att, 8.0);

            HBox predStats = new HBox(20);
            predStats.getChildren().addAll(
                statBox("Predicted CGPA", String.format("%.2f / 10", result.getPredictedCgpa()), BLUE),
                statBox("Predicted %",    String.format("%.1f%%", result.getPredictedPercentage()), GREEN),
                statBox("Expected Class", result.getGradeCategory(), ORANGE)
            );
            infoCard.getChildren().add(predStats);

            TextArea rec = new TextArea(result.getRecommendation());
            rec.setWrapText(true);
            rec.setPrefHeight(120);
            rec.setEditable(false);
            rec.setStyle("-fx-font-size: 12; -fx-background-color: #FFF8E1;");
            infoCard.getChildren().add(lbl("💡 Personalized Recommendations:", 12, true, Color.web(SAFFRON)));
            infoCard.getChildren().add(rec);
        }

        area.getChildren().add(infoCard);
    }

    // ══════════════════════════════════════════════════════
    // TAB 4 — MARKS REPORT
    // ══════════════════════════════════════════════════════
    private VBox buildMarksReport() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: " + BG + ";");

        root.getChildren().add(lbl("📊  Detailed Marks Report", 18, true, Color.web(BLUE)));

        HBox searchRow = new HBox(10);
        ComboBox<Student> repStudentBox = new ComboBox<>();
        repStudentBox.setPrefWidth(320);
        repStudentBox.getItems().addAll(StudentDAO.getAllStudents());
        ComboBox<String> semBox = new ComboBox<>();
        semBox.getItems().addAll("All Semesters", "Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8");
        semBox.setValue("All Semesters");
        Button viewBtn = btn("📋 View Report", BLUE);

        searchRow.getChildren().addAll(
            lbl("Student:", 13, true, Color.web(BLUE)), repStudentBox,
            lbl("Semester:", 13, true, Color.web(BLUE)), semBox, viewBtn
        );

        TableView<SubjectMark> markTable = buildMarkTable();
        VBox.setVgrow(markTable, Priority.ALWAYS);

        viewBtn.setOnAction(e -> {
            Student sel = repStudentBox.getSelectionModel().getSelectedItem();
            if (sel == null) { showAlert("Select a student!"); return; }
            List<SubjectMark> marks;
            String semStr = semBox.getValue();
            if ("All Semesters".equals(semStr)) {
                marks = StudentDAO.getAllMarks(sel.getStudentId());
            } else {
                int sem = Integer.parseInt(semStr.replace("Sem ", "").trim());
                marks = StudentDAO.getMarksBySemester(sel.getStudentId(), sem);
            }
            markTable.setItems(FXCollections.observableArrayList(marks));
        });

        root.getChildren().addAll(searchRow, markTable);
        return root;
    }

    @SuppressWarnings("unchecked")
    private TableView<SubjectMark> buildMarkTable() {
        TableView<SubjectMark> t = new TableView<>();
        t.setStyle("-fx-font-size: 12;");
        t.getColumns().addAll(
            col("Sem",          "semester",       50),
            col("Subject",      "subjectName",    200),
            col("Code",         "subjectCode",     80),
            col("IA1 (/20)",    "ia1Marks",        80),
            col("IA2 (/20)",    "ia2Marks",        80),
            col("ESE",          "eseMarks",        70),
            col("Practical",    "practicalMarks",  80),
            col("Total (/100)", "totalMarks",      90),
            col("Grade Pt",     "gradePoint",      80),
            col("Grade",        "grade",           70),
            col("Credits",      "credits",         70)
        );
        return t;
    }

    // ══════════════════════════════════════════════════════
    // ADD STUDENT DIALOG
    // ══════════════════════════════════════════════════════
    private void showAddStudentDialog() {
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("➕ Add New Student");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + BG + ";");

        layout.getChildren().add(lbl("➕ Register New Student", 16, true, Color.web(BLUE)));
        layout.getChildren().add(new Separator());

        TextField fName   = field("Full Name (e.g. Rahul Sharma)");
        TextField fRoll   = field("Roll Number (e.g. 2024CE042)");
        TextField fEmail  = field("Email Address");
        TextField fPhone  = field("Mobile Number");

        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.getItems().addAll(
            "Computer Engineering", "Information Technology",
            "Electronics & TC", "Mechanical Engineering",
            "Civil Engineering", "Electrical Engineering",
            "Chemical Engineering", "Instrumentation Engineering"
        );
        branchBox.setPromptText("Select Branch");
        branchBox.setPrefWidth(Double.MAX_VALUE);

        ComboBox<String> semBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) semBox.getItems().add("Semester " + i);
        semBox.setValue("Semester 1");
        semBox.setPrefWidth(Double.MAX_VALUE);

        Label status = new Label("");
        status.setTextFill(Color.RED);

        Button saveBtn = btn("✅  Save Student", GREEN);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            if (fName.getText().isEmpty() || fRoll.getText().isEmpty() || branchBox.getValue() == null) {
                status.setText("⚠️ Name, Roll Number and Branch are required!");
                return;
            }
            Student s = new Student();
            s.setName(fName.getText().trim());
            s.setRollNumber(fRoll.getText().trim());
            s.setEmail(fEmail.getText().trim());
            s.setPhone(fPhone.getText().trim());
            s.setBranch(branchBox.getValue());
            int sem = Integer.parseInt(semBox.getValue().replace("Semester ", ""));
            s.setCurrentSem(sem);
            s.setYearOfStudy((sem + 1) / 2);

            int id = StudentDAO.addStudent(s);
            if (id > 0) {
                status.setTextFill(Color.GREEN);
                status.setText("✅ Student added! ID: " + id);
                studentBox.getItems().clear();
                loadStudents();
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) {}
                    Platform.runLater(dlg::close);
                }).start();
            } else {
                status.setText("❌ Error! Roll number may already exist.");
            }
        });

        Button cancelBtn = btn("Cancel", "#9E9E9E");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setOnAction(e -> dlg.close());

        layout.getChildren().addAll(
            lbl("Full Name:", 12, true, Color.DARKGRAY), fName,
            lbl("Roll Number:", 12, true, Color.DARKGRAY), fRoll,
            lbl("Email:", 12, true, Color.DARKGRAY), fEmail,
            lbl("Phone:", 12, true, Color.DARKGRAY), fPhone,
            lbl("Branch:", 12, true, Color.DARKGRAY), branchBox,
            lbl("Current Semester:", 12, true, Color.DARKGRAY), semBox,
            new Separator(), status, saveBtn, cancelBtn
        );

        dlg.setScene(new Scene(new ScrollPane(layout), 420, 520));
        dlg.show();
    }

    // ══════════════════════════════════════════════════════
    // ADD MARKS DIALOG
    // ══════════════════════════════════════════════════════
    private void showAddMarksDialog(TableView<Student> table) {
        Student sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Please select a student from the table first!"); return; }

        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("📝 Add Marks — " + sel.getName());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: " + BG + ";");

        layout.getChildren().add(lbl("📝 Add Subject Marks for " + sel.getName(), 15, true, Color.web(BLUE)));
        layout.getChildren().add(lbl("Branch: " + sel.getBranch() + " | Sem: " + sel.getCurrentSem(), 12, false, Color.GRAY));
        layout.getChildren().add(new Separator());

        TextField fSubject  = field("Subject Name (e.g. Data Structures)");
        TextField fCode     = field("Subject Code (e.g. DSA301)");
        TextField fIA1      = field("IA1 Marks (out of 20)");
        TextField fIA2      = field("IA2 Marks (out of 20)");
        TextField fESE      = field("ESE Marks (out of 80)");
        TextField fPractical = field("Practical Marks (out of 25, enter 0 if none)");
        TextField fCredits  = field("Credits (e.g. 4)");
        fCredits.setText("4");

        ComboBox<String> semBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) semBox.getItems().add("Semester " + i);
        semBox.setValue("Semester " + sel.getCurrentSem());
        semBox.setPrefWidth(Double.MAX_VALUE);

        Label status = new Label("");
        status.setTextFill(Color.RED);

        Button saveBtn = btn("✅  Save Marks", GREEN);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            try {
                double ia1  = Double.parseDouble(fIA1.getText());
                double ia2  = Double.parseDouble(fIA2.getText());
                double ese  = Double.parseDouble(fESE.getText());
                double prac = Double.parseDouble(fPractical.getText());
                int credits = Integer.parseInt(fCredits.getText());
                int sem     = Integer.parseInt(semBox.getValue().replace("Semester ", ""));

                // Calculate total out of 100
                // IA total = 20 (best of ia1, ia2 scaled), ESE = 60 or 80
                double iaBest = Math.max(ia1, ia2); // take best IA
                double total  = (iaBest / 20.0 * 20) + (ese / 80.0 * 60) + (prac / 25.0 * 20);
                total = Math.min(100, total);

                double gp    = PredictionResult.getGradePoint(total);
                String grade = PredictionResult.getGrade(total).split(" ")[0];

                SubjectMark m = new SubjectMark();
                m.setStudentId(sel.getStudentId());
                m.setSemester(sem);
                m.setSubjectName(fSubject.getText().trim());
                m.setSubjectCode(fCode.getText().trim());
                m.setIa1Marks(ia1); m.setIa2Marks(ia2);
                m.setEseMarks(ese); m.setEseTotal(80);
                m.setPracticalMarks(prac); m.setPracticalTotal(25);
                m.setTotalMarks(total);
                m.setGradePoint(gp);
                m.setGrade(grade);
                m.setCredits(credits);

                if (StudentDAO.addSubjectMark(m)) {
                    StudentDAO.updateCgpa(sel.getStudentId());
                    refreshStudentTable(table);
                    status.setTextFill(Color.GREEN);
                    status.setText("✅ Marks saved! Total: " + String.format("%.1f", total) + "% | Grade: " + grade + " | GP: " + gp);
                } else {
                    status.setText("❌ Error saving marks.");
                }
            } catch (NumberFormatException ex) {
                status.setText("⚠️ Please enter valid numbers in all mark fields!");
            }
        });

        Button closeBtn = btn("Close", "#9E9E9E");
        closeBtn.setMaxWidth(Double.MAX_VALUE);
        closeBtn.setOnAction(e -> dlg.close());

        layout.getChildren().addAll(
            lbl("Semester:", 12, true, Color.DARKGRAY), semBox,
            lbl("Subject Name:", 12, true, Color.DARKGRAY), fSubject,
            lbl("Subject Code:", 12, true, Color.DARKGRAY), fCode,
            lbl("IA1 (/20):", 12, true, Color.DARKGRAY), fIA1,
            lbl("IA2 (/20):", 12, true, Color.DARKGRAY), fIA2,
            lbl("ESE (/80):", 12, true, Color.DARKGRAY), fESE,
            lbl("Practical (/25):", 12, true, Color.DARKGRAY), fPractical,
            lbl("Credits:", 12, true, Color.DARKGRAY), fCredits,
            new Separator(), status, saveBtn, closeBtn
        );

        dlg.setScene(new Scene(new ScrollPane(layout), 420, 580));
        dlg.show();
    }

    // ══════════════════════════════════════════════════════
    // ADD ATTENDANCE DIALOG
    // ══════════════════════════════════════════════════════
    private void showAddAttendanceDialog(TableView<Student> table) {
        Student sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showAlert("Select a student first!"); return; }

        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle("📋 Add Attendance — " + sel.getName());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().add(lbl("📋 Attendance for " + sel.getName(), 15, true, Color.web(BLUE)));
        layout.getChildren().add(new Separator());

        TextField fSubject  = field("Subject Name");
        TextField fTotal    = field("Total Lectures Held");
        TextField fAttended = field("Lectures Attended");

        ComboBox<String> semBox = new ComboBox<>();
        for (int i = 1; i <= 8; i++) semBox.getItems().add("Semester " + i);
        semBox.setValue("Semester " + sel.getCurrentSem());
        semBox.setPrefWidth(Double.MAX_VALUE);

        Label status = new Label("");
        Button saveBtn = btn("✅ Save", GREEN);
        saveBtn.setOnAction(e -> {
            try {
                int total    = Integer.parseInt(fTotal.getText());
                int attended = Integer.parseInt(fAttended.getText());
                int sem      = Integer.parseInt(semBox.getValue().replace("Semester ", ""));
                double pct   = (attended * 100.0) / total;

                StudentDAO.addAttendance(sel.getStudentId(), sem, fSubject.getText().trim(), total, attended);
                status.setTextFill(Color.GREEN);
                status.setText(String.format("✅ Saved! Attendance: %.1f%%", pct));
                if (pct < 75) status.setText(status.getText() + " ⚠️ Below 75%!");
            } catch (NumberFormatException ex) {
                status.setTextFill(Color.RED);
                status.setText("⚠️ Enter valid numbers!");
            }
        });

        Button closeBtn = btn("Close", "#9E9E9E");
        closeBtn.setOnAction(e -> dlg.close());

        layout.getChildren().addAll(
            lbl("Semester:", 12, true, Color.DARKGRAY), semBox,
            lbl("Subject:", 12, true, Color.DARKGRAY), fSubject,
            lbl("Total Lectures:", 12, true, Color.DARKGRAY), fTotal,
            lbl("Attended:", 12, true, Color.DARKGRAY), fAttended,
            new Separator(), status, saveBtn, closeBtn
        );

        dlg.setScene(new Scene(layout, 380, 380));
        dlg.show();
    }

    // ══════════════════════════════════════════════════════
    // PREDICTION LOGIC
    // ══════════════════════════════════════════════════════
    private void onStudentSelected() {
        Student s = studentBox.getSelectionModel().getSelectedItem();
        if (s == null) return;
        refreshSgpaChart(s.getStudentId());
    }

    private void refreshSgpaChart(int studentId) {
        sgpaChart.getData().clear();
        List<Double> sgpas = StudentDAO.getSgpaPerSemester(studentId);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("SGPA");
        for (int i = 0; i < sgpas.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, sgpas.get(i)));
        }
        sgpaChart.getData().add(series);
    }

    private void doPrediction() {
        Student s = studentBox.getSelectionModel().getSelectedItem();
        if (s == null) { showAlert("Please select a student!"); return; }

        List<Double> sgpas = StudentDAO.getSgpaPerSemester(s.getStudentId());
        if (sgpas.size() < 2) { showAlert("Need data from at least 2 semesters to predict!"); return; }

        double att = StudentDAO.getAvgAttendance(s.getStudentId());
        PerformancePredictor predictor = new PerformancePredictor();
        PredictionResult result = predictor.predict(sgpas, att, 8.0);

        lblCgpa.setText(String.format("%.2f / 10.0", result.getPredictedCgpa()));
        lblPct.setText(String.format("%.1f%%", result.getPredictedPercentage()));
        lblGrade.setText(PredictionResult.getGrade(result.getPredictedPercentage()));
        lblClass.setText(result.getGradeCategory());
        lblConf.setText(String.format("%.1f%%", result.getConfidence()));
        lblTrend.setText(result.getTrend());
        recArea.setText(result.getRecommendation());

        // Colour trend
        switch (result.getTrend()) {
            case "IMPROVING":          lblTrend.setTextFill(Color.GREEN);      break;
            case "SLIGHTLY_IMPROVING": lblTrend.setTextFill(Color.LIMEGREEN);  break;
            case "DECLINING":          lblTrend.setTextFill(Color.RED);        break;
            case "SLIGHTLY_DECLINING": lblTrend.setTextFill(Color.ORANGERED);  break;
            default:                   lblTrend.setTextFill(Color.DARKORANGE); break;
        }

        // Add predicted point to chart
        if (!sgpaChart.getData().isEmpty()) {
            XYChart.Series<Number, Number> predSeries = new XYChart.Series<>();
            predSeries.setName("Predicted");
            predSeries.getData().add(new XYChart.Data<>(sgpas.size() + 1, result.getPredictedCgpa()));
            sgpaChart.getData().add(predSeries);
        }
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════
    private void loadStudents() {
        studentBox.getItems().addAll(StudentDAO.getAllStudents());
    }

    private Label lbl(String t, int size, boolean bold, Color c) {
        Label l = new Label(t);
        l.setFont(Font.font("Segoe UI", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        l.setTextFill(c);
        return l;
    }

    private Button btn(String text, String color) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;" +
                   "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        return b;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-font-size: 12;");
        return tf;
    }

    private VBox card() {
        VBox v = new VBox(8);
        v.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 14;");
        v.setPadding(new Insets(14));
        return v;
    }

    private VBox statBox(String label, String value, String color) {
        VBox b = new VBox(4);
        b.setAlignment(Pos.CENTER);
        b.setStyle("-fx-background-color: " + color + "22; -fx-background-radius: 8; -fx-padding: 10;");
        b.setMinWidth(140);
        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        v.setTextFill(Color.web(color));
        Label l = new Label(label);
        l.setFont(Font.font("Segoe UI", 11));
        l.setTextFill(Color.GRAY);
        b.getChildren().addAll(v, l);
        return b;
    }

    private <S, T> TableColumn<S, T> col(String title, String prop, int width) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Notice");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
