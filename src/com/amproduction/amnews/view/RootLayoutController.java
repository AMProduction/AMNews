package com.amproduction.amnews.view;

import com.amproduction.amnews.MainApp;
import com.amproduction.amnews.model.News;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 *	@version 1.0 2016-02
 *	@author Andrii Malchyk
 */

public class RootLayoutController {
	private MainApp mainApp;
	
	public void setMainApp(MainApp mainApp)
	{
		this.mainApp = mainApp;
    }

	/**
	 * Пункт меню Вихід
	 */
	@FXML
    private void handleExit()
	{
        System.exit(0);
    }

	/**
	 * Пункт меню Про
	 */
	@FXML
    private void handleAbout()
	{
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("AMNews");
        alert.setHeaderText("Про продукт");
        alert.setContentText("Автор: А.В.Мальчик");

        alert.showAndWait();
    }

	/**
	 * Пункт меню Додати
	 */
	@FXML
	private void handleNewNews()
	{
		mainApp.showNewsAddDialog_NEW();
	}
	
}
