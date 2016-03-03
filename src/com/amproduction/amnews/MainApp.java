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

/**
 *	@version 1.0 2016-02
 *	@author Andrii Malchyk
 */
public class MainApp extends Application {

	// CR: Використовуй відступи замість табів - забезпечить однакове відображення на різних платформах (поміняй на налаштуваннях IDE)
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	NewsEditDialogController controllerNewsEditDialog;
	NewsOverviewController controllerNewsOverview;

	// CR: просто dbManager - важливо правильно давати імена
	// CR: тут можна зробити static final константу INSTANCE_DB_MANAGER
	DBManager instanceDBManager = DBManager.getInstance();

	// CR: для чого тут ініціалізовувати якщо потім ти все перетираєш значення ?
	private ObservableList<News> newsData = FXCollections.observableArrayList();

	public MainApp()
	{
		try
		{
			// CR: ця змінна ніде не використовується
			Connection c = instanceDBManager.ConnectionToDB();
			// CR: перенеси ініціалізацію данних в start метод
			newsData = instanceDBManager.getData(); // CR: getNews ???
		}
		catch (SQLException e)
		{
			// CR: не копіпасть - винеси в окремий метод який приймає alert type, title & text
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
            alert.setTitle("AMNews");
            alert.setHeaderText("Помилка отримання даних");
            alert.setContentText("Перевірте з\'єднання з базою даних");

            alert.showAndWait();
		}
		catch (IOException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
			alert.setTitle("AMNews");
			alert.setHeaderText("Помилка файлу конфігурації");
			alert.setContentText("Перевірте наявність файлу конфігурації");

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
			// CR: приведення не потрібне
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
			// CR: що тобі дасть стек трейс тут ? або падай повністю (прокидай на верх завернуте в RuntimeException) або роби recovery
			e.printStackTrace();
		}
	}
	
	/**
     * Відображаємо NewsOverview всередині RootLayout
     */
	public void showNewsOverview()
	{
		try
		{
			 // Load news overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/NewsOverview.fxml"));
			// CR: Не потрібне приведення
			AnchorPane newsOverview = (AnchorPane) loader.load();
			
			// Set news overview into the center of root layout.
			rootLayout.setCenter(newsOverview);

			// CR: не бачу змісту виносити controllerNewsOverview в поле істанса
			controllerNewsOverview = loader.getController();
			// CR: контроллер не повинен нічого знати про main app - почитай про принципи SOLID
			// CR: додай метод setNewsData
			controllerNewsOverview.setMainApp(this);
		}
		catch (IOException e)
		{
			// CR: прокидай на верх
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

	/**
	 * Показуємо вікно додавання нової новини
	 */
	// CR: в java використовується тільки camel case це не php
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


					// CR: ти можеш не виносити controllerNewsEditDialog в поле інстанса вистачить і локальної змінної
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
		// CR: у тебе тут жодна стрічка не кидає IOException
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Очищаємо колецію і наповнюємо її новленими даними
	 */
	// CR: розділи на два методи - clear & refresh дотримуйся принципу single responsibility (SOLID)
	public void clearAndGetData()
	{
		this.newsData.removeAll(newsData);
		try
		{
			// this.newsData.addAll ???
			this.newsData = instanceDBManager.getData();		
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
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(primaryStage);
			alert.setTitle("AMNews");
			alert.setHeaderText("Помилка файлу конфігурації");
			alert.setContentText("Перевірте наявність файлу конфігурації");

			alert.showAndWait();
		}
	}
}
