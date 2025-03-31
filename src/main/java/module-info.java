module pl.bsk.project.bsk_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;


    exports pl.bsk.project.bsk_project.view;
    opens pl.bsk.project.bsk_project.view to javafx.fxml;
    exports pl.bsk.project.bsk_project.component;
    opens pl.bsk.project.bsk_project.component to javafx.fxml;
}