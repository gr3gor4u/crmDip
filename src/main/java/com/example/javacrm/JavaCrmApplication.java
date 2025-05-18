package com.example.javacrm;

import com.example.javacrm.controller.LoginController;
import com.example.javacrm.service.ServiceInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaCrmApplication extends Application {
    private ServiceInitializer serviceInitializer;

    @Override
    public void init() {
        serviceInitializer = ServiceInitializer.getInstance();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();
        
        LoginController controller = loader.getController();
        controller.setUserService(serviceInitializer.getUserService());
        controller.setStage(primaryStage);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        primaryStage.setTitle("CarStore CRM");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 