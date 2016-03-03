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

/**
 *	@version 1.0 2016-02
 *	@author Andrii Malchyk
 */

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
		// CR: bController ?
		this.controller = aController;
    }

	/**
	 * Обробник кнопки Зберегти (нова новина)
	 */
	@FXML
	private void handleSaveNews()
	{
		//отримуємо дати створення і останнього редагування
		//у даному методі вони співпадають
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		//перевіряємо чи не порожні поля Теми і Тексту новини

		// CR: equals("") та isEmpty() це те саме
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
		//якщо все ОК, то зберігаємо
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
				// CR: копі паст )
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

	/**
	 * ініціалізуємо текстові поля
	 * @param aNews отримуємо дані з параметра
     */
	public void setText (News aNews)
	{
		if (aNews != null) // CR: а якщо null то все ок ? юзай Objects.requireNotNull - чим швидше впаде ти легше причину знайти
		{
			subjectTextArea.setText(aNews.getSubject());
			textPresenterTextArea.setText(aNews.getTextPresenter());
			textNewsTextArea.setText(aNews.getTextNews());
		}
	}

	/**
	 * Ініціалізуємо змінну класу, щоби отримати id новини і дату створення
	 * Потрібно для методу handleUpdateNews()
	 * @param aNews звідси отримуємо id
     */
	public void setNews (News aNews)
	{
		this.news = aNews;
	}

	/**
	 * Обробник кнопки Оновити (редагуємо новину)
	 */
	@FXML
	private void handleUpdateNews()
	{
		//отримуємо дату останнього редагування
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		//перевіряємо чи не порожні поля Теми і Тексту новини
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
		//якщо все ОК
		else {
			//id та дату створення отримуємо з переданого параметра
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

			//оновлюємо таблицю
			controller.clearAndRefresh();
			dialogStage.close();
		}
	}

	/**
	 * відключаємо кнопку Зберегти
	 */
	public void setSaveButtonOff()
	{
		saveButton.setDisable(true);
	}

	/**
	 * відключаємо кнопку Оновити
	 */
	public void setUpdateButtonOff()
	{
		updateButton.setDisable(true);
	}
}
