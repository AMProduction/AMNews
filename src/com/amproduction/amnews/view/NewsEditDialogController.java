package com.amproduction.amnews.view;

import java.io.IOException;
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

		if ((subjectTextArea.getText().equals("") || subjectTextArea.getText().isEmpty()) ||
				textNewsTextArea.getText().equals("") || textNewsTextArea.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("AMNews");
			alert.setHeaderText("Порожні поля");
			alert.setContentText("Поле теми і/або тексту новини не може бути порожнім!");

			alert.showAndWait();
		}
		else
		{
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
				alert.setHeaderText("Помилка додавання запису");
				alert.setContentText("Перевірте з\'єднання з базою даних\n"
					+ "Перевірте правльність даних\n"
					+ "Дані не збережені !");

				alert.showAndWait();
			}
			catch (IOException e)
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("AMNews");
				alert.setHeaderText("Помилка файлу конфігурації");
				alert.setContentText("Перевірте наявність файлу конфігурації");

				alert.showAndWait();
			}

			controller.clearAndRefresh();
			dialogStage.close();
		}
	}
	
	public void setText (News aNews)
	{
		if (aNews != null)
		{
			subjectTextArea.setText(aNews.getSubject());
			textPresenterTextArea.setText(aNews.getTextPresenter());
			textNewsTextArea.setText(aNews.getTextNews());
		}
	}
	
	public void setNews (News aNews)
	{
		this.news = aNews;
	}
	
	@FXML
	private void handleUpdateNews()
	{
		LocalDateTime lastModifiedDate = LocalDateTime.now();

		if ((subjectTextArea.getText().equals("") || subjectTextArea.getText().isEmpty()) ||
				textNewsTextArea.getText().equals("") || textNewsTextArea.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("AMNews");
			alert.setHeaderText("Порожні поля");
			alert.setContentText("Поле теми і/або тексту новини не може бути порожнім !");

			alert.showAndWait();
		}
		else {

			News updateNews = new News(this.news.getId(), subjectTextArea.getText(),
					textPresenterTextArea.getText(), textNewsTextArea.getText(),
					this.news.getCreatedDate(), lastModifiedDate);

			try {
				instanceDBManager.updateRecord(updateNews);
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("AMNews");
				alert.setHeaderText("Помилка оновлення запису");
				alert.setContentText("Перевірте з\'єднання з базою даних\n"
						+ "Перевірте правльність даних\n"
						+ "Дані не збережені !");

				alert.showAndWait();
			}
			catch (IOException e)
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("AMNews");
				alert.setHeaderText("Помилка файлу конфігурації");
				alert.setContentText("Перевірте наявність файлу конфігурації");

				alert.showAndWait();
			}


			controller.clearAndRefresh();
			dialogStage.close();
		}
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
