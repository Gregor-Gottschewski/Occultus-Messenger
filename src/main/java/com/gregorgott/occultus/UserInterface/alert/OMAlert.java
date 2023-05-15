/*
 * Copyright (c) 2023 Gregor Gottschewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gregorgott.occultus.UserInterface.alert;

import com.gregorgott.occultus.UserInterface.I18N.I18N;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class OMAlert extends Alert {
    public OMAlert(AlertType alertType) {
        this(alertType, null);
    }

    public OMAlert(AlertType alertType, Stage owner) {
        super(alertType);

        setTitle(switch (getAlertType()) {
            case INFORMATION -> I18N.getString("alert.information.title");
            case WARNING -> I18N.getString("alert.warning.title");
            case ERROR -> I18N.getString("alert.error.title");
            default -> "";
        });

        getDialogPane().getStyleClass().add("OM-alert");
        getDialogPane().getStylesheets().add(OMAlert.class.getResource("Alert.css").toString());

        if (alertType != null) {
            initOwner(owner);
            Stage alertStage = (Stage) getDialogPane().getScene().getWindow();
            alertStage.getIcons().setAll(owner.getIcons());
        }
    }
}
