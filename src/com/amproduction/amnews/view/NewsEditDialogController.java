package com.amproduction.amnews.view;

import com.amproduction.amnews.model.News;
import com.amproduction.amnews.util.AlertUtils;
import com.amproduction.amnews.util.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *	Created by snooki on 02.16.
 *	@version 1.1 2016-03
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
	
	private NewsOverviewController controller;

	private final String HEADER_TEXT_IO_ERROR = "Помилка файлу конфігурації";
	private final String CONTENT_TEXT_IO_ERROR = "Перевірте наявність файлу конфігурації";
    private final String HEADER_TEXT_EMPTY_FIELD_ERROR = "Порожні поля";
    private final String CONTENT_TEXT_EMPTY_FIELD_ERROR = "Поле теми і/або тексту новини не може бути порожнім!";

	private static final DBManager instanceDBManager = DBManager.getInstance();
	
	@FXML
	private void initialize() {
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
    }
	
	public void setNewsOverviewController(NewsOverviewController aController) {
		this.controller = aController;
    }

	/**
	 * Обробник кнопки Зберегти (нова новина)
	 */
	@FXML
	private void handleSaveNews() {
		//отримуємо дати створення і останнього редагування
		//у даному методі вони співпадають
		LocalDateTime createdDate = LocalDateTime.now();
		LocalDateTime lastModifiedDate = LocalDateTime.now();
		//перевіряємо чи не порожні поля Теми і Тексту новини
		if ((subjectTextArea.getText().equals("")) || textNewsTextArea.getText().equals("") ) {
            AlertUtils.showErrorAlert(dialogStage, HEADER_TEXT_EMPTY_FIELD_ERROR, CONTENT_TEXT_EMPTY_FIELD_ERROR);
		}
		//якщо все ОК, то зберігаємо
		else {
			News news = new News(subjectTextArea.getText(), textPresenterTextArea.getText(),
					textNewsTextArea.getText(), createdDate, lastModifiedDate);
			try {
				instanceDBManager.addRecord(news);
			}
			catch (SQLException e) {
                AlertUtils.showErrorAlert(dialogStage, "Помилка додавання запису", "Перевірте з\'єднання з базою даних\n"
                        + "Перевірте правльність даних\n"
                        + "Дані не збережені !");
			}
			catch (IOException e) {
                AlertUtils.showErrorAlert(dialogStage, HEADER_TEXT_IO_ERROR, CONTENT_TEXT_IO_ERROR);
			}

			controller.clearAndRefresh();
			dialogStage.close();
		}
	}

	/**
	 * ініціалізуємо текстові поля
	 * @param aNews отримуємо дані з параметра
     */
	public void setText (News aNews) {
		subjectTextArea.setText(Objects.requireNonNull(aNews).getSubject());
		textPresenterTextArea.setText(Objects.requireNonNull(aNews).getTextPresenter());
		textNewsTextArea.setText(Objects.requireNonNull(aNews).getTextNews());
	}

	/**
	 * Ініціалізуємо змінну класу, щоби отримати id новини і дату створення
	 * Потрібно для методу handleUpdateNews()
	 * @param aNews звідси отримуємо id
     */
	public void setNews (News aNews) {
		this.news = Objects.requireNonNull(aNews);
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
		if (subjectTextArea.getText().equals("") ||
				textNewsTextArea.getText().equals("")) {
            AlertUtils.showErrorAlert(dialogStage, HEADER_TEXT_EMPTY_FIELD_ERROR, CONTENT_TEXT_EMPTY_FIELD_ERROR);
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
                AlertUtils.showErrorAlert(dialogStage, "Помилка оновлення запису",
                        "Перевірте з\'єднання з базою даних\n"
                                + "Перевірте правльність даних\n"
                                + "Дані не збережені !");
			}
			catch (IOException e) {
                AlertUtils.showErrorAlert(dialogStage, HEADER_TEXT_IO_ERROR, CONTENT_TEXT_IO_ERROR);
			}

			//оновлюємо таблицю
			controller.clearAndRefresh();
			dialogStage.close();
		}
	}

	/**
	 * відключаємо кнопку Зберегти
	 */
	public void setSaveButtonOff() {
		saveButton.setDisable(true);
	}

	/**
	 * відключаємо кнопку Оновити
	 */
	public void setUpdateButtonOff() {
		updateButton.setDisable(true);
	}
}
