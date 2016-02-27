package com.amproduction.amnews.view;

import com.amproduction.amnews.MainApp;
import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Label connectionStatusLabel;
	
	@FXML
	private DatePicker datePicker;
	
	private MainApp mainApp;
	
	DBManager instanceDBManager = DBManager.getInstance();
	
	public NewsOverviewController()
	{	
		
    }
	
	@FXML
    private void initialize()
    {	
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
		lastModifiedDateColumn.setCellValueFactory(cellData -> cellData.getValue().lastModifiedDateProperty());
			
		boolean stat = instanceDBManager.getConnectionStatus();
		if (stat) 
		{
			connectionStatusLabel.setText("Під\'єднано");
		}
		
    }
		
	public void setMainApp(MainApp mainApp)
	{
		this.mainApp = mainApp;
		newsTable.setItems(mainApp.getNewsData());
		
		datePicker.setOnAction(event -> {
			newsDateFilter();
		});

        newsTable.setRowFactory( tv -> {
            TableRow<News> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    handleEditNews();
                }
            });
            return row ;
        });
	}
	
	@FXML
	private void handleNewNews()
	{
		mainApp.showNewsAddDialog_NEW();
	}
	
	@FXML
	public void clearAndRefresh()
	{	
		mainApp.clearAndGetData();
		newsTable.setItems(mainApp.getNewsData());
	}
	
	@FXML
	private void handleDeleteNews()
	{	
		int selectedIndex = newsTable.getSelectionModel().getSelectedIndex();
		
		if (selectedIndex >= 0)
		{
			News selectedNews = newsTable.getSelectionModel().getSelectedItem();
		
			try
			{
				instanceDBManager.deleteRecord(selectedNews);
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
			
			clearAndRefresh();
		}
		else
		{
			// Nothing selected.
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(mainApp.getPrimaryStage());
	        alert.setTitle("AMNews");
	        alert.setHeaderText("Новина не вибрана");
	        alert.setContentText("Будь ласка, виберіть новину зі списку");

	        alert.showAndWait();
		}
	}
	
	@FXML
	private void handleEditNews()
	{	
		int selectedIndex = newsTable.getSelectionModel().getSelectedIndex();
		
		if (selectedIndex >= 0)
		{
			News selectedNews = newsTable.getSelectionModel().getSelectedItem();
			mainApp.showNewsAddDialog_EDIT(selectedNews);
		}
		else
		{
			// Nothing selected.
	        Alert alert = new Alert(AlertType.WARNING);
	        alert.initOwner(mainApp.getPrimaryStage());
	        alert.setTitle("AMNews");
	        alert.setHeaderText("Не вибрана новина");
	        alert.setContentText("Будь ласка, виберіть новину зі списку");

	        alert.showAndWait();
		}
		
	}
	
	@FXML
	private void newsDateFilter()
	{		
		ObservableList<News> newsData = FXCollections.observableArrayList();    
		LocalDate date = datePicker.getValue();
		LocalDate dateNow = LocalDate.now();

		if (date != null)
		{			
			if (date.isAfter(dateNow))
			{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("AMNews");
				alert.setHeaderText("Дата з майбутнього");
				alert.setContentText("Виберіть іншу дату");

				alert.showAndWait();
			}
			else {

				try {
					newsData = instanceDBManager.filter(date);
				} catch (SQLException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setTitle("AMNews");
					alert.setHeaderText("Помилка роботи з базою даних");
					alert.setContentText("Перевірте з\'єднання з базою даних");

					alert.showAndWait();
				}

				if (newsData.isEmpty()) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setTitle("AMNews");
					alert.setHeaderText("Дані не знайдені");
					alert.setContentText("Виберіть іншу дату");

					alert.showAndWait();
				} else {
					mainApp.clearAndGetData();
					newsTable.setItems(newsData);
				}
			}
		
		}
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
