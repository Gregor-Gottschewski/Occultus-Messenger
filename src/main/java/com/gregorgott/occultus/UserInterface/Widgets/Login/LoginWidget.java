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

import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.ServerHandler.UserManager;
import com.gregorgott.occultus.User;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.LoginWindow;
import com.gregorgott.occultus.UserInterface.Widget;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginWidget extends Widget {
    private final LoginWindow loginWindow;
    private final TextField usernameTextField;
    private final PasswordField passwordTextField;
    private final Label infoLabel;

    public LoginWidget(LoginWindow loginWindow) {
        super();
        this.loginWindow = loginWindow;

        /* Center */
        /* Username Box */
        Label usernameLabel = new Label(I18N.getString("label.username"));
        usernameLabel.setId("info-label");

        usernameTextField = new TextField();
        usernameTextField.setPromptText(I18N.getString("label.username"));
        usernameLabel.setLabelFor(usernameTextField);

        VBox usernameBox = new VBox(usernameLabel, usernameTextField);
        usernameBox.setSpacing(3);
        /* Username Box */
        /* Password Box */
        Label passwordLabel = new Label(I18N.getString("label.password"));
        passwordLabel.setId("info-label");

        passwordTextField = new PasswordField();
        passwordTextField.setPromptText(I18N.getString("label.password"));
        passwordLabel.setLabelFor(passwordTextField);

        VBox passwordBox = new VBox(passwordLabel, passwordTextField);
        passwordBox.setSpacing(3);
        /* Password Box */
        /* Buttons Box */
        infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setId("warning-label");

        Button registerButton = new Button(I18N.getString("button.register.now"));
        registerButton.setOnAction(x -> register());
        /* Buttons Box */

        VBox centerBox = new VBox(usernameBox, passwordBox, infoLabel, registerButton);
        centerBox.setMaxWidth(200);
        centerBox.setSpacing(10);
        /* Center */

        this.setCenter(centerBox);

        BorderPane.setAlignment(centerBox, Pos.TOP_LEFT);
    }

    private boolean login(String username, String password) {
        User user = new User(username, password);

        try (UserManager userManager = new UserManager(user)) {
            if (!userManager.loginServerRequest()) {
                return false;
            }

            // save user data
            UserInfoFileHandler userDataFile = new UserInfoFileHandler();
            userDataFile.setUsername(username);
            userDataFile.setPassword(user.getPassword());
            userDataFile.writeChanges();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void register() {
        loginWindow.setContent(this, new RegisterWidget(loginWindow));
    }

    @Override
    public void setWindowUI() {
        loginWindow.setHeadline(I18N.getString("label.userdata"));

        loginWindow.setNextButtonLabel(I18N.getString("button.login"));
        loginWindow.setNextButtonAction(x -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            disableUi(true, loginWindow);
            Thread t = new Thread(() -> {
                boolean b = login(username, password);
                Platform.runLater(() -> disableUi(false, loginWindow));
                if (b) {
                    Platform.runLater(() -> loginWindow.setContent(new LoginWidget(loginWindow), new SessionNameWidget(loginWindow)));
                } else {
                    Platform.runLater(() -> infoLabel.setText(I18N.getString("label.check.input")));
                }
            });
            t.start();
        });
    }
}
