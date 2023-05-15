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

import com.gregorgott.occultus.ChatData.Message;
import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.ServerHandler.MessageSender;
import com.gregorgott.occultus.Threads.InboxListener;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.util.Objects;

public class ChatWidget extends BorderPane {
    private final ScrollPane messagesScrollPane;
    private final VBox messagesVBox;
    private final TextArea inputArea;
    private final ChatFileHandler chatFileHandler;
    private final InboxListener inboxListener;

    public ChatWidget(ChatFileHandler chatFileHandler, ChatPreviewWidget chatPreviewWidget) {
        this.chatFileHandler = chatFileHandler;
        this.getStylesheets().add(ChatWidget.class.getResource("ChatWidget.css").toString());
        this.getStyleClass().add("CW");

        Label usernameLabel = new Label(chatFileHandler.getChatname());
        usernameLabel.setId("username");

        /* Zentrum */
        messagesVBox = new VBox();
        messagesVBox.setSpacing(8);
        messagesVBox.setPadding(new Insets(10,0,10,0));

        messagesScrollPane = new ScrollPane(messagesVBox);
        messagesScrollPane.setFitToWidth(true);
        messagesScrollPane.setVvalue(1.0);
        /* Zentrum */

        /* Unten */
        /* InputArea */
        inputArea = new TextArea();
        inputArea.setWrapText(true);
        inputArea.setMaxHeight(35);
        inputArea.setPromptText(I18N.getString("field.new.message"));
        inputArea.setOnKeyPressed(x -> {
            if (x.getCode() == KeyCode.ENTER && x.isShiftDown()) {
                sendMessage(chatPreviewWidget);
                messagesScrollPane.setVvalue(1.0);
            }
        });
        HBox.setHgrow(inputArea, Priority.ALWAYS);
        /* InputArea */
        /* Senden Button */
        Image sendIcon = new Image(Objects.requireNonNull(ChatWidget.class.getResourceAsStream("icons/send.png")));
        Button sendButton = new Button("", new ImageView(sendIcon));
        sendButton.setPrefWidth(50);
        sendButton.setOnAction(x -> sendMessage(chatPreviewWidget));
        /* Senden Button */
        HBox bottomBar = new HBox(inputArea, sendButton);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setSpacing(5);
        /* Unten */

        for (int i = 0; i < chatFileHandler.getMessagesArray().size(); i++) {
            addMessageWidget(i);
        }

        inboxListener = new InboxListener(() -> {
            try {
                chatFileHandler.readFile();
                addMessageWidget(chatFileHandler.getNewestMessageId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "AddMessageToChatListener");

        this.setTop(usernameLabel);
        this.setCenter(messagesScrollPane);
        this.setBottom(bottomBar);
        this.setPadding(new Insets(10));
    }

    public void removeInboxListener() {
        inboxListener.removeThisInboxListener();
    }

    private void sendMessage(ChatPreviewWidget chatPreviewWidget) {
        Thread thread = new Thread(() -> {
            String text = inputArea.getText();
            try {
                MessageSender messageSender = new MessageSender(chatFileHandler);
                messageSender.sendMessage(text);
                messageSender.close();

                Platform.runLater(() -> {
                    chatPreviewWidget.setLatestMessageLabel(text);
                    addMessageWidget(chatFileHandler.getNewestMessageId());
                    inputArea.clear();
                });
            } catch (IOException | PGPException e) {
                Platform.runLater(() -> {
                    OMAlert alert = new OMAlert(Alert.AlertType.ERROR, (Stage) this.getScene().getWindow());
                    alert.setHeaderText(I18N.getString("alert.error.message.sending.failed.message"));
                    alert.setContentText(I18N.getString("alert.error.message.sending.failed.details"));
                    alert.show();
                });
            }
        });
        thread.start();
    }

    private void addMessageWidget(int i) {
        Message message = chatFileHandler.getMessageById(i);
        if (message != null) {
            MessageWidget messageWidget = new MessageWidget(message, messagesVBox);
            Platform.runLater(() -> messagesVBox.getChildren().add(messageWidget));
        }
    }
}
