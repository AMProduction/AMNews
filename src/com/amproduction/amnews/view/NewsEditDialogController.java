package com.amproduction.amnews.view;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.DBManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class NewsEditDialogController
{
	@FXML
	private TextArea subjectTextArea;
	@FXML
	private TextArea textPresenterTextArea;
	@FXML
	private TextArea textNewsTextArea;
	
	@FXML
	private Button saveButton;
	@FXML
	private Button updateButton;
	
	private News news;
	private Stage dialogStage;
	
	NewsOverviewController controller;
	
	DBManager instanceDBManager = DBManager.getInstance();
	
	@FXML
	private void initialize()
	{
	}
	
	public void setDialogStage(Stage dialogStage)
	{
		this.dialogStage = dialogStage;
    }
	
	public void setNewsOverviewController(NewsOverviewController aController)
	{
		this.controller = aController;
    }

	@FXML
	private void handleSaveNews()
	{
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime lastModifiedDate = LocalDateTime.now();
				
		News news = new News(subjectTextArea.getText(), textPresenterTextArea.getText(), 
				textNewsTextArea.getText(), createdDate, lastModifiedDate);
		try
		{
			instanceDBManager.addRecord(news);
		}
		catch (SQLException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
            alert.setTitle("AMNews");
            alert.setHeaderText("Error adding records to the database");
            alert.setContentText("Check database connection\n"
            		+ "Check the correctness of data\n"
            		+ "The data is not recorded in the database");

            alert.showAndWait();
		}
		
		controller.clearAndRefresh();
		dialogStage.close();
	}
	
	public void setText (News aNews)
	{
		subjectTextArea.setText(aNews.getSubject());
		textPresenterTextArea.setText(aNews.getTextPresenter());
		textNewsTextArea.setText(aNews.getTextNews());
	}
	
	public void setNews (News aNews)
	{
		this.news = aNews;
	}
	
	@FXML
	private void handleUpdateNews()
	{
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		
		News updateNews = new News(this.news.getId(), subjectTextArea.getText(),
				textPresenterTextArea.getText(), textNewsTextArea.getText(),
				this.news.getCreatedDate(), lastModifiedDate);
		
		try
		{
			instanceDBManager.updateRecord(updateNews);
		}
		catch (SQLException e)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
            alert.setTitle("AMNews");
            alert.setHeaderText("Failed to update records in the database");
            alert.setContentText("Check database connection\n"
            		+ "Check the correctness of data\n"
            		+ "The data is not updated in the database");

            alert.showAndWait();
		}
		
		controller.clearAndRefresh();
		dialogStage.close();
	}
	
	public void setSaveButtonOff()
	{
		saveButton.setDisable(true);
	}

	public void setUpdateButtonOff()
	{
		updateButton.setDisable(true);
	}
}
