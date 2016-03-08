package com.amproduction.amnews.util;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *  Created by snooki on 04.03.16.
 *  @version 1.1 2016-03
 *  @author Andrii Malchyk
 */
public class AlertUtils {

    private AlertUtils() {

    }
    public static void showErrorAlert(Stage aStage, String aHeaderText, String aContentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(aStage);
        alert.setTitle("AMNews");
        alert.setHeaderText(aHeaderText);
        alert.setContentText(aContentText);

        alert.showAndWait();
    }
}