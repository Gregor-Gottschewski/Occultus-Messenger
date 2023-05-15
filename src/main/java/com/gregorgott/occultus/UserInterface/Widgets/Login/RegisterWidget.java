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

import com.gregorgott.occultus.Encryption.KeyManager;
import com.gregorgott.occultus.FileHandler.ServerInfoFileHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.ServerConnection;
import com.gregorgott.occultus.ServerHandler.UserManager;
import com.gregorgott.occultus.User;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.LoginWindow;
import com.gregorgott.occultus.UserInterface.Widget;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

public class RegisterWidget extends Widget {
    private final LoginWindow loginWindow;
    private final TextField usernameTextField;
    private final PasswordField passwordField;
    private final PasswordField checkPasswordField;
    private final Label infoLabel;

    public RegisterWidget(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;

        /* Zentrum */
        /* Nutzername Box */
        Label usernameLabel = new Label(I18N.getString("label.username"));
        usernameLabel.setId("info-label");

        usernameTextField = new TextField();
        usernameTextField.setPromptText(I18N.getString("label.username"));
        usernameLabel.setLabelFor(usernameTextField);

        VBox usernameBox = new VBox(usernameLabel, usernameTextField);
        usernameBox.setSpacing(3);
        /* Nutzername Box */
        /* Passwort Box */
        Label passwordLabel = new Label(I18N.getString("label.password"));
        passwordLabel.setId("info-label");

        passwordField = new PasswordField();
        passwordField.setPromptText(I18N.getString("label.password"));

        VBox passwordBox = new VBox(passwordLabel, passwordField);
        passwordBox.setSpacing(3);
        /* Passwort Box */
        /* Passwort überprüfen Box */
        Label checkPasswordLabel = new Label(I18N.getString("label.check.password"));
        checkPasswordLabel.setId("info-label");

        checkPasswordField = new PasswordField();
        checkPasswordField.setPromptText(I18N.getString("label.password"));
        checkPasswordLabel.setLabelFor(checkPasswordField);

        VBox checkPasswordBox = new VBox(checkPasswordLabel, checkPasswordField);
        checkPasswordBox.setSpacing(3);
        /* Passwort überprüfen Box */
        infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setId("warning-label");

        VBox centerBox = new VBox(usernameBox, passwordBox, checkPasswordBox, infoLabel);
        centerBox.setMaxWidth(200);
        centerBox.setSpacing(10);
        /* Zentrum */

        this.setCenter(centerBox);

        BorderPane.setAlignment(centerBox, Pos.TOP_LEFT);
    }

    private boolean register(User user) throws RuntimeException {
        try (UserManager userManager = new UserManager(user)) {
            if (!userManager.newUserServerRequest().isSuccess()) {
                return false;
            }

            KeyManager keyManager = new KeyManager(user.getUsername());
            keyManager.generateKeyPair();
            if (!sendPublicKeyToServer(user, keyManager.getPublicKey()).isSuccess()) {
                return false;
            }

            UserInfoFileHandler userInfoFileHandler = new UserInfoFileHandler();
            userInfoFileHandler.setPublicKey(new String(keyManager.getPublicKey()));
            userInfoFileHandler.setPrivateKey(new String(keyManager.getPrivateKey()));
            userInfoFileHandler.writeChanges();
        } catch (IOException | PGPException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
            return false;
        }

        return true;
    }

    private ServerAnswer sendPublicKeyToServer(User user, byte[] publicKey) throws IOException {
        ServerRequest request = new ServerRequest(ServerRequest.POST_METHOD, ServerRequest.KEY_ACTION);
        request.addUserLogin(user);
        request.getParametersObject().addProperty("key", new String(publicKey));

        ServerInfoFileHandler serverInfo = new ServerInfoFileHandler();
        ServerConnection connection = new ServerConnection(serverInfo);
        ServerAnswer answer = connection.callServer(request.toString());
        connection.close();
        return answer;
    }

    @Override
    public void setWindowUI() {
        loginWindow.setHeadline(I18N.getString("label.register"));

        loginWindow.setNextButtonLabel(I18N.getString("label.register"));
        loginWindow.setNextButtonAction(x -> {
            String password = passwordField.getText();
            String checkPassword = checkPasswordField.getText();

            if (password.equals(checkPassword)) {
                String username = usernameTextField.getText();

                disableUi(true, loginWindow);

                Thread t = new Thread(() -> {
                    User user = new User(username, password);
                    user.hashPassword();
                    boolean b = register(user);
                    Platform.runLater(() -> disableUi(false, loginWindow));
                    if (b) {
                        Platform.runLater(() -> loginWindow.setContent(this, new LoginWidget(loginWindow)));
                    }

                    Platform.runLater(() -> infoLabel.setText(I18N.getString("label.check.input")));
                });
                t.start();
            } else {
                infoLabel.setText(I18N.getString("label.passwords.not.the.same"));
            }
        });
    }
}
