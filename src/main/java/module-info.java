module pl.bsk.project.bsk_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.bsk.project.bsk_project to javafx.fxml;
    exports pl.bsk.project.bsk_project;
}