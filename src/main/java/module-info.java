module com.training.studyfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.logging;
    requires java.net.http;
    requires com.google.gson;

    opens com.training.studyfx to javafx.fxml;
    opens com.training.studyfx.controller to javafx.fxml;
    opens com.training.studyfx.model to javafx.base;

    exports com.training.studyfx;
    exports com.training.studyfx.controller;
    exports com.training.studyfx.model;
    exports com.training.studyfx.service;
    exports com.training.studyfx.server;
    exports com.training.studyfx.util;
    exports com.training.studyfx.exception;
}