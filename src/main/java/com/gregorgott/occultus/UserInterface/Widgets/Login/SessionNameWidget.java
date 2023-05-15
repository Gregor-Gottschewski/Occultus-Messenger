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
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.Main;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.ServerConnection;
import com.gregorgott.occultus.User;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.LoginWindow;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.Widget;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SessionNameWidget extends Widget {
    private final LoginWindow loginWindow;
    private final TextField sessionTextField;

    public SessionNameWidget(LoginWindow loginWindow) {
        super();
        this.loginWindow = loginWindow;

        /* Sitzungsname Box */
        Label sessionNameLabel = new Label(I18N.getString("label.session.name"));
        sessionNameLabel.setId("info-label");

        sessionTextField = new TextField(getHostname());
        sessionTextField.setPromptText(I18N.getString("field.session.device.session.name"));
        sessionNameLabel.setLabelFor(sessionTextField);

        VBox sessionNameBox = new VBox(sessionNameLabel, sessionTextField);
        sessionNameBox.setSpacing(3);
        /* Sitzungsname Box */

        this.setCenter(sessionNameBox);
    }

    private boolean onOkButton(String sessionName) {
        try {
            UserInfoFileHandler userData = new UserInfoFileHandler();
            ServerAnswer sessionAnswer = sendSessionNameToServer(userData.getUser(), sessionName);

            if (!sessionAnswer.isSuccess()) {
                return false;
            }

            userData.setSessionName(sessionName);
            userData.writeChanges();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return I18N.getString("my.device");
        }
    }

    private ServerAnswer sendSessionNameToServer(User user, String s) throws IOException {
        ServerRequest request = new ServerRequest(ServerRequest.POST_METHOD, ServerRequest.SESSION_ACTION);
        request.addUserLogin(user);
        request.getParametersObject().addProperty("session_name", s);

        ServerConnection connection = new ServerConnection(new ServerInfoFileHandler());
        ServerAnswer answer = connection.callServer(request.toString());
        connection.close();
        return answer;
    }

    @Override
    public void setWindowUI() {
        loginWindow.setHeadline(I18N.getString("label.session.name"));
        loginWindow.setNextButtonAction(x -> {
            String session = sessionTextField.getText();
            disableUi(true, loginWindow);

            Thread t = new Thread(() -> {
                boolean b = onOkButton(session);
                Platform.runLater(() -> disableUi(false, loginWindow));
                if (b) {
                    Platform.runLater(() -> {
                        Scene scene = new Scene(new MessengerWindow());
                        scene.getStylesheets().add(MessengerWindow.class.getResource("classic_style.css").toString());
                        ((Stage) this.getScene().getWindow()).setScene(scene);
                    });
                }
            });
            t.start();
        });
    }
}
