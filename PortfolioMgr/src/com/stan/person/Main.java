package com.stan.person;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.fxml.FXMLLoader;


public class Main extends Application {
	   private static Stage window;
	    @Override
	    public void start(Stage primaryStage) throws Exception{
	        window = primaryStage;
	        Parent root = FXMLLoader.load(getClass().getResource("view/Bucketizer.fxml"));
	        primaryStage.setTitle("Bucketizer");
	        primaryStage.setScene(new Scene(root, 1300, 1000));
	        primaryStage.show();
	    }

	    public static Stage getWindow() {
	        return window;
	    }
	    public static void main(String[] args) {
	        launch(args);
	    }
	}
