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

package com.gregorgott.occultus.UserInterface.Widgets.Messenger.Popup;

import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.FileHandler.ChatsHandler;
import com.gregorgott.occultus.ServerHandler.ServerKeyHandler;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class NewChatWidget extends Popup {
    private final Image FOUND_ICON = new Image(NewChatWidget.class.getResourceAsStream("icons/check.png"));
    private final Image NOT_FOUND_ICON = new Image(NewChatWidget.class.getResourceAsStream("icons/cancel.png"));
    private final ImageView statusImageView;

    public NewChatWidget(MessengerWindow messengerWindow) {
        super(messengerWindow, I18N.getString("new.chat"));

        Label recipientLabel = new Label(I18N.getString("label.recipient"));

        statusImageView = new ImageView(NOT_FOUND_ICON);
        statusImageView.setFitHeight(20);
        statusImageView.setFitWidth(20);

        TextField recipientTextField = new TextField();
        recipientTextField.setPrefWidth(230);
        recipientTextField.setPromptText(I18N.getString("field.recipient"));
        recipientTextField.setOnKeyReleased(x -> {
            try {
                if (checkRecipient(recipientTextField.getText())) {
                    statusImageView.setImage(FOUND_ICON);
                    getOkButton().setDisable(false);
                } else {
                    statusImageView.setImage(NOT_FOUND_ICON);
                    getOkButton().setDisable(true);
                }
            } catch (IOException e) {
                showFileError();
            }
        });
        recipientLabel.setLabelFor(recipientTextField);

        HBox hBox = new HBox(recipientLabel, recipientTextField, statusImageView);
        hBox.setSpacing(8);
        hBox.setAlignment(Pos.CENTER_LEFT);

        getOkButton().setOnAction(x -> {
            try {
                ChatsHandler chatsHandler = new ChatsHandler();
                String filename = chatsHandler.addPrivateChat(recipientTextField.getText());
                chatsHandler.writeChanges();

                ChatFileHandler chatFileHandler = new ChatFileHandler(filename);
                chatFileHandler.createPrivateChatFile(chatsHandler.getBiggestChatId(), recipientTextField.getText());

                close(messengerWindow);
            } catch (IOException e) {
                showFileError();
            }
        });

        this.setCenter(hBox);
    }

    private boolean checkRecipient(String recipient) throws IOException {
        ServerKeyHandler serverKeyManager = new ServerKeyHandler();
        String key = serverKeyManager.getPublicKeyOf(recipient);
        serverKeyManager.close();

        return !key.isEmpty();
    }

    private void showFileError() {
        OMAlert alert = new OMAlert(Alert.AlertType.ERROR, (Stage) this.getScene().getWindow());
        alert.setHeaderText(I18N.getString("alert.error.read.file.message"));
        alert.setContentText(I18N.getString("alert.error.read.file.details"));
        alert.show();
    }
}
