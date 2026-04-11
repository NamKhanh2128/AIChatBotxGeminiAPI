module com.training.studyfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    //requires org.slf4j.simple;
    requires java.net.http;
    requires org.json;

    opens com.training.studyfx to javafx.fxml;
    opens com.training.studyfx.controller to javafx.fxml;

    exports com.training.studyfx;
    exports com.training.studyfx.controller;
    exports com.training.studyfx.model;
    exports com.training.studyfx.service;
    exports com.training.studyfx.server;
    opens com.training.studyfx.service to javafx.fxml;
    opens com.training.studyfx.server to javafx.fxml;
}