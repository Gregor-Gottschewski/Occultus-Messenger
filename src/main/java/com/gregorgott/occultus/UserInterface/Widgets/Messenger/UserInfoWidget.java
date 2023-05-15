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

package com.gregorgott.occultus.UserInterface.Widgets.Messenger;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.ServerHandler.TrustedUsersHandler;
import com.gregorgott.occultus.UserInterface.ServerMessageAlert;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class UserInfoWidget extends StackPane {
    private static final String DONT_TRUST_USER_ANYMORE = "Nicht mehr vertrauen";
    private static final String TRUST_USER = "Vertrauen";
    private final String chatname;
    private final Button closeButton;
    private final Button userTrustButton;

    public UserInfoWidget(ChatFileHandler chatFile) {
        chatname = chatFile.getChatname();

        closeButton = new Button("Close");
        closeButton.setOnAction(x -> this.setVisible(false));
        closeButton.setAlignment(Pos.TOP_RIGHT);

        Label usernameLabel = new Label(chatFile.getChatname());

        userTrustButton = new Button();
        userTrustButton.setOnAction(x -> {
            if (getTrustedStatus()) {
                removeTrustedUser();
            } else {
                addTrustedUser();
            }
        });
        if (getTrustedStatus()) {
            userTrustButton.setText(DONT_TRUST_USER_ANYMORE);
        } else {
            userTrustButton.setText(TRUST_USER);
        }

        VBox vBox = new VBox(closeButton, usernameLabel, userTrustButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        StackPane.setAlignment(vBox, Pos.TOP_RIGHT);

        this.setMaxSize(200, 350);
        this.getChildren().add(vBox);
    }

    public Button getCloseButton() {
        return closeButton;
    }

    private boolean getTrustedStatus() {
        // Status in einer Datei speichern, damit der Status auch im Online Status abgerufen werden kann
        try (TrustedUsersHandler trustedUsersHandler = new TrustedUsersHandler()) {
            ServerAnswer answer = trustedUsersHandler.getTrustedUsers();

            if (!answer.isSuccess()) {
                return false;
            }

            JsonArray trustedUsers = answer.getReturnElement().getAsJsonArray();
            return trustedUsers.contains(new JsonPrimitive(chatname));
        } catch (IOException e) {
            return false;
        }
    }

    private void addTrustedUser() {
        try (TrustedUsersHandler trustedUsersHandler = new TrustedUsersHandler()) {
            ServerAnswer answer = trustedUsersHandler.addTrustedUser(chatname);

            if (!answer.isSuccess()) {
                ServerMessageAlert alert = new ServerMessageAlert(answer);
                alert.show();
            } else {
                userTrustButton.setText(DONT_TRUST_USER_ANYMORE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeTrustedUser() {
        try (TrustedUsersHandler trustedUsersHandler = new TrustedUsersHandler()) {
            ServerAnswer answer = trustedUsersHandler.removeTrustedUser(chatname);

            if (!answer.isSuccess()) {
                ServerMessageAlert alert = new ServerMessageAlert(answer);
                alert.show();
            } else {
                userTrustButton.setText(TRUST_USER);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
