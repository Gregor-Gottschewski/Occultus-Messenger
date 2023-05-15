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

package com.gregorgott.occultus.UserInterface.Widgets.Login;

import com.gregorgott.occultus.FileHandler.ServerInfoFileHandler;
import com.gregorgott.occultus.ServerConnection;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.LoginWindow;
import com.gregorgott.occultus.UserInterface.Widget;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerWidget extends Widget {
    private final LoginWindow loginWindow;
    private final TextField serverUrlTextField;
    private final TextField serverPortTextField;

    public ServerWidget(LoginWindow loginWindow) {
        super();
        this.loginWindow = loginWindow;

        /* Center */
        /* Server URL Box */
        Label serverUrlLabel = new Label(I18N.getString("label.server.url"));
        serverUrlLabel.setId("info-label");

        serverUrlTextField = new TextField();
        serverUrlTextField.setPromptText(I18N.getString("label.server.url"));
        serverUrlLabel.setLabelFor(serverUrlTextField);

        VBox serverUrlBox = new VBox(serverUrlLabel, serverUrlTextField);
        serverUrlBox.setSpacing(3);
        /* Server URL Box */
        /* Server Port Box */
        Label serverPortLabel = new Label(I18N.getString("label.port"));
        serverPortLabel.setId("info-label");

        serverPortTextField = new TextField();
        serverPortTextField.setPromptText(I18N.getString("label.port"));
        serverPortTextField.setPrefWidth(70);
        serverUrlLabel.setLabelFor(serverPortTextField);

        VBox serverPortBox = new VBox(serverPortLabel, serverPortTextField);
        serverPortBox.setSpacing(3);
        /* Server Port Box */
        /* Bottom */

        /* Bottom */
        HBox textFieldsHBox = new HBox(serverUrlBox, serverPortBox);
        textFieldsHBox.setSpacing(10);
        /* Center */

        this.setCenter(textFieldsHBox);
    }

    private boolean connect(String serverUrl, int port) {
        try {
            ServerConnection serverConnection = new ServerConnection(serverUrl, port);
            //if (serverConnection.isConnected()) {
                // server info
                ServerInfoFileHandler serverInfo = new ServerInfoFileHandler();
                serverInfo.setServerUrl(serverUrl);
                serverInfo.setServerPort(port);
                serverInfo.writeChanges();
                serverConnection.close();

                return true;
            //}
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void setWindowUI() {
        loginWindow.setHeadline(I18N.getString("label.serverdata"));
        loginWindow.setNextButtonAction(x -> {
            if (!serverUrlTextField.getText().isEmpty() && !serverPortTextField.getText().isEmpty()) {
                String serverUrl = serverUrlTextField.getText();
                int port = Integer.parseInt(serverPortTextField.getText());

                disableUi(true, loginWindow);

                Thread t = new Thread(() -> {
                    boolean b = connect(serverUrl, port);
                    Platform.runLater(() -> disableUi(false, loginWindow));

                    if (b) {
                        Platform.runLater(() -> loginWindow.setContent(this, new LoginWidget(loginWindow)));
                    } else {
                        Platform.runLater(() -> {
                            OMAlert alert = new OMAlert(Alert.AlertType.ERROR, (Stage) loginWindow.getScene().getWindow());
                            alert.setHeaderText(I18N.getString("alert.error.server.connection.message"));
                            alert.setContentText(I18N.getString("alert.error.server.connection.details"));
                            alert.show();
                        });
                    }
                });
                t.start();
            }
        });
    }
}
