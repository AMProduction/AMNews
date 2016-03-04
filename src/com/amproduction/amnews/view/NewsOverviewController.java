package com.amproduction.amnews.view;

import com.amproduction.amnews.MainApp;
import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.DBManager;
import com.amproduction.amnews.util.ShowAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *	Created by snooki on 02.16.
 *	@version 1.1 2016-03
 *	@author Andrii Malchyk
 */

public class NewsOverviewController {
	@FXML
    private TableView<News> newsTable;
    @FXML
    private TableColumn<News, Integer> idColumn;
    @FXML
    private TableColumn<News, String> subjectColumn;
    @FXML
    private TableColumn<News, LocalDateTime> lastModifiedDateColumn;
	
	@FXML
	private DatePicker datePicker;
	
	private MainApp mainApp;

	private  final int CLICK_COUNT = 2;
	
	final DBManager INSTANCE_DB_MANAGER = DBManager.getInstance();
	
	public NewsOverviewController()
	{	
		
    }
	
	@FXML
    private void initialize()
    {	
		//магія Java 8 і Java FX
		//задаємо, що буде відображатись у кожному стовбчику
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
		lastModifiedDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastModifiedDateProperty());
    }
		
	public void setMainApp(MainApp mainApp)
	{
		this.mainApp = mainApp;
		newsTable.setItems(mainApp.getNewsData());

		//магія Java 8 і Java FX
		//задаємо дію на вибір дати
		datePicker.setOnAction(event -> filterNewsByDate());

		//магія Java 8 і Java FX
		//задаємо дію на подвійний клік на новині
        newsTable.setRowFactory( dblClick -> {
            TableRow<News> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == CLICK_COUNT && !row.isEmpty() ) {
                    handleEditNews();
                }
            });
            return row ;
        });
	}

	/**
	 * Обробник кнопки Додати
	 */
	@FXML
	private void handleNewNews()
	{
		mainApp.showNewsAddDialogNew();
	}

	/**
	 * Обробник кнопки Скинути
	 * Його ж юзаємо в класі NewsEditDialogController
	 */
	@FXML
	public void clearAndRefresh()
	{	
		mainApp.clearData();
		mainApp.getData();
		newsTable.setItems(mainApp.getNewsData());
	}

	/**
	 * Обробник кнопки Видалити
	 */
	@FXML
	private void handleDeleteNews()
	{	
		//отримуємо індекс виділеного
		int selectedIndex = newsTable.getSelectionModel().getSelectedIndex();

		//Якщо щось виділене
		if (selectedIndex >= 0)
		{
			//отримуємо виділений елемент
			News selectedNews = newsTable.getSelectionModel().getSelectedItem();
			//і видаляємо його
			try
			{
				INSTANCE_DB_MANAGER.deleteRecord(selectedNews);
			}
			catch (SQLException e)
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(mainApp.getPrimaryStage());
	            alert.setTitle("AMNews");
	            alert.setHeaderText("Помилка видалення запису");
	            alert.setContentText("Перевірте з\'єднання з базою даних\n"
	            		+ "Перевірте правльність даних\n"
	            		+ "Дані не видалені з бази даних");
	
	            alert.showAndWait();
			}
			catch (IOException e)
			{
				ShowAlert.IOAlert(mainApp.getPrimaryStage());
			}

			clearAndRefresh();
		}
		//якщо нічого не вибрано
		else
		{
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(mainApp.getPrimaryStage());
	        alert.setTitle("AMNews");
	        alert.setHeaderText("Новина не вибрана");
	        alert.setContentText("Будь ласка, виберіть новину зі списку");

	        alert.showAndWait();
		}
	}

	/**
	 * Обробник кнопки Редагувати
	 */
	@FXML
	private void handleEditNews()
	{
		//отримуємо індекс виділеного
		int selectedIndex = newsTable.getSelectionModel().getSelectedIndex();

		//Якщо щось виділене
		if (selectedIndex >= 0)
		{
			//отримуємо виділений елемент
			News selectedNews = newsTable.getSelectionModel().getSelectedItem();
			// і викликаємо вікно редагування, передавши туди вибрану новину
			mainApp.showNewsAddDialogEdit(selectedNews);
		}
		//якщо нічого не вибрано
		else
		{
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(mainApp.getPrimaryStage());
	        alert.setTitle("AMNews");
	        alert.setHeaderText("Не вибрана новина");
	        alert.setContentText("Будь ласка, виберіть новину зі списку");

	        alert.showAndWait();
		}
		
	}

	/**
	 * Пошук новини за датою створення
	 */
	@FXML
	private void filterNewsByDate()
	{		
		ObservableList<News> newsData = FXCollections.observableArrayList();    
		//отримуємо дату з DatePicker
		LocalDate date = datePicker.getValue();
		//отримуємо сьогоднішню дату
		LocalDate dateNow = LocalDate.now();

		//Якщо юзер вибрав дату
		if (date != null)
		{			
			//Якщо вибрана дата більша за теперішню
			if (date.isAfter(dateNow))
			{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("AMNews");
				alert.setHeaderText("Дата з майбутнього");
				alert.setContentText("Виберіть іншу дату");

				alert.showAndWait();
			}
			else
			{
				//якщо з дато все ок пробуємо пошукати
				try
				{
					newsData = INSTANCE_DB_MANAGER.filterNews(date);
				}
				catch (SQLException e)
				{
					Alert alert = new Alert(AlertType.ERROR);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setTitle("AMNews");
					alert.setHeaderText("Помилка роботи з базою даних");
					alert.setContentText("Перевірте з\'єднання з базою даних");

					alert.showAndWait();
				}
				catch (IOException e)
				{
					ShowAlert.IOAlert(mainApp.getPrimaryStage());
				}
				//Якщо нічого не знайшли
				if (newsData.isEmpty())
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setTitle("AMNews");
					alert.setHeaderText("Дані не знайдені");
					alert.setContentText("Виберіть іншу дату");

					alert.showAndWait();
				}
				//Якщо знайшли, то виводимо знайдене у таблицю
				else
				{
					mainApp.clearData();
					mainApp.getData();
					newsTable.setItems(newsData);
				}
			}
		
		}
		//якщо дату не вибрали, а на кнопку тицнюли, чи ще якась фігня
		//Примітка: Кнопка відключена, вручну не запустиш пошук
		//тільки через DatePicker
		else
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("AMNews");
			alert.setHeaderText("Дата не вибрана");
			alert.setContentText("Операція не може бути здійснена");

			alert.showAndWait();
		}
		
	}
}
