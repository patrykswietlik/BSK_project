module pl.bsk.project.bsk_project {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.bsk.project.bsk_project to javafx.fxml;
    exports pl.bsk.project.bsk_project;
    exports pl.bsk.project.bsk_project.view;
    opens pl.bsk.project.bsk_project.view to javafx.fxml;
}