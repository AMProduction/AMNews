package com.amproduction.amnews;

import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.DBManager;
import com.amproduction.amnews.util.ShowAlert;
import com.amproduction.amnews.view.NewsEditDialogController;
import com.amproduction.amnews.view.NewsOverviewController;
import com.amproduction.amnews.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 *  Created by snooki on 02.16.
 *	@version 1.1 2016-03
 *	@author Andrii Malchyk
 */

public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	private NewsEditDialogController controllerNewsEditDialog;
	private NewsOverviewController controllerNewsOverview;
	
	private final DBManager INSTANCE_DB_MANAGER = DBManager.getInstance();
	
	private ObservableList<News> newsData;
	
	public MainApp()
	{
	}

	@Override
	public void start(Stage primaryStage)
	{
        try
        {
            newsData = INSTANCE_DB_MANAGER.getNews();
        }
        catch (SQLException e)
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(primaryStage);
            alert.setTitle("AMNews");
            alert.setHeaderText("Помилка отримання даних");
            alert.setContentText("Перевірте з\'єднання з базою даних");

            alert.showAndWait();
        }
        catch (IOException e)
        {
            ShowAlert.IOAlert(primaryStage);
        }

        this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AMNews");
		
		initRootLayout();
		
		showNewsOverview();
	}
	
	/**
     * Initializes the root layout.
     */
	private void initRootLayout()
	{
        // Load root layout from fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
        try {
            rootLayout = loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Show the scene containing the root layout.
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
			
        // Give the controller access to the main app.
        RootLayoutController controller = loader.getController();
        controller.setMainApp(this);
	        
        primaryStage.show();
	}
	
	/**
     * Відображаємо NewsOverview всередині RootLayout
     */
	private void showNewsOverview()
	{
		try
		{
			 // Load news overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/NewsOverview.fxml"));
			AnchorPane newsOverview = loader.load();

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
     * @return Return the main stage
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

	/**
	 * Показуємо вікно додавання нової новини
	 */
	public void showNewsAddDialogNew()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/NewsEditDialog.fxml"));
	        AnchorPane page = loader.load();
	        
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Add News");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        controllerNewsEditDialog = loader.getController();
			//деактивовуємо кнопку Оновити
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

	/**
	 * Показуємо вікно редгування новини
	 * @param news передаємо обєкт у метод відображення для ініціалізації текстових полів
     */
	public void showNewsAddDialogEdit(News news)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/NewsEditDialog.fxml"));
	        AnchorPane page = loader.load();
	        
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Add News");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);
	        
	        controllerNewsEditDialog = loader.getController();
	        //ініціалізації текстових полів
			controllerNewsEditDialog.setText(news);
			//передаємо новину у контролер для отримання даних
	        controllerNewsEditDialog.setNews(news);
			//передаємо посилання на контролер NewsOverview
			//щоби працювали методи з нього
	        controllerNewsEditDialog.setNewsOverviewController(controllerNewsOverview);
			//декативовуємо кнопку Зберегти
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

	/**
	 * Очищаємо колецію і наповнюємо її новленими даними
	 */
	public void clearData()
	{
		this.newsData.removeAll(newsData);
	}

	public void getData()
	{
		try
		{
			this.newsData = INSTANCE_DB_MANAGER.getNews();
		}
		catch (SQLException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
			alert.setTitle("AMNews");
			alert.setHeaderText("Помилка оновлення таблиці");
			alert.setContentText("Перевірте з\'єднання з базою даних");

			alert.showAndWait();
		}
		catch (IOException e)
		{
			ShowAlert.IOAlert(primaryStage);
		}
	}
}
