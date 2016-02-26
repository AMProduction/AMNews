package com.amproduction.amnews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.DBManager;
import com.amproduction.amnews.view.NewsEditDialogController;
import com.amproduction.amnews.view.NewsOverviewController;
import com.amproduction.amnews.view.RootLayoutController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	NewsEditDialogController controllerNewsEditDialog;
	NewsOverviewController controllerNewsOverview;
	
	DBManager instanceDBManager = DBManager.getInstance();
	
	private ObservableList<News> newsData = FXCollections.observableArrayList();
	
	public MainApp()
	{
		Connection c = instanceDBManager.ConnectionToDB();
		try
		{
			newsData = instanceDBManager.getData();		
		}
		catch (SQLException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
            alert.setTitle("Error");
            alert.setHeaderText("Error receiving data");
            alert.setContentText("Check database connection");

            alert.showAndWait();
		}
	}

	@Override
	public void start(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AMNews");
		
		initRootLayout();
		
		showNewsOverview();
	}
	
	/**
     * Initializes the root layout.
     */
	public void initRootLayout()
	{
		try
		{		
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// Give the controller access to the main app.
	        RootLayoutController controller = loader.getController();
	        controller.setMainApp(this);
	        
			primaryStage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Shows the news overview inside the root layout.
     */
	public void showNewsOverview()
	{
		try
		{
			 // Load news overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/NewsOverview.fxml"));
			AnchorPane newsOverview = (AnchorPane) loader.load();
			
			// Set news overview into the center of root layout.
			rootLayout.setCenter(newsOverview);
			
			controllerNewsOverview = loader.getController();
			controllerNewsOverview.setMainApp(this);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Returns the main stage.
     * @return
     */
	public Stage getPrimaryStage()
	{
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public ObservableList<News> getNewsData() {
        return newsData;
    }

	public void showNewsAddDialog_NEW()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/NewsEditDialog.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Add News");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        controllerNewsEditDialog = loader.getController();
	        controllerNewsEditDialog.setUpdateButtonOff();
	        controllerNewsEditDialog.setNewsOverviewController(controllerNewsOverview);
	        controllerNewsEditDialog.setDialogStage(dialogStage);
	        
	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();
	    }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void showNewsAddDialog_EDIT(News news)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/NewsEditDialog.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Add News");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        controllerNewsEditDialog = loader.getController();
	        controllerNewsEditDialog.setText(news);
	        controllerNewsEditDialog.setNews(news);
	        controllerNewsEditDialog.setNewsOverviewController(controllerNewsOverview);
	        controllerNewsEditDialog.setSaveButtonOff();
	        controllerNewsEditDialog.setDialogStage(dialogStage);
	        // Show the dialog and wait until the user closes it
	        dialogStage.showAndWait();
	    }
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void clearAndGetData()
	{
		this.newsData.removeAll(newsData);
		try
		{
			this.newsData = instanceDBManager.getData();		
		}
		catch (SQLException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update table");
            alert.setContentText("Check database connection");

            alert.showAndWait();
		}
	}
}
