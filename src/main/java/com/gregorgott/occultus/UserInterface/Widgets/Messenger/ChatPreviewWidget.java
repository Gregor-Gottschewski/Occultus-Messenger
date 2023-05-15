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

import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.Threads.InboxListener;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatPreviewWidget extends Button {
    private final Label chatNameLabel;
    private final Label latestMessageLabel;

    public ChatPreviewWidget(ChatFileHandler chatFileHandler) {
        this.getStylesheets().add(getClass().getResource("ChatPreviewWidget.css").toString());
        this.getStyleClass().add("CPW");

        chatNameLabel = new Label();
        chatNameLabel.setId("chat-name");
        latestMessageLabel = new Label();
        latestMessageLabel.setId("newest-message");

        VBox vBox = new VBox(chatNameLabel, latestMessageLabel);
        vBox.setSpacing(5);

        if (chatFileHandler != null) {
            setChatFileHandler(chatFileHandler);
        }

        this.setGraphic(vBox);
        this.setPrefHeight(65);
        this.setPrefWidth(Integer.MAX_VALUE);
    }

    private void setChatFileHandler(ChatFileHandler chatFileHandler) {
        assert chatFileHandler != null;

        setChatNameLabel(chatFileHandler.getChatname());

        try {
            setLatestMessageLabel(chatFileHandler.getLatestMessageText());
        } catch (IOException e) {
            setLatestMessageLabel("");
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error while reading chat file!");
        }

        InboxListener inboxListener = new InboxListener(() -> Platform.runLater(() -> {
            try {
                setLatestMessageLabel(chatFileHandler.getLatestMessageText());
            } catch (IOException e) {
                setLatestMessageLabel("");
            }
        }), "ChatPreviewWidgetChangeListener");
        inboxListener.setChatId(chatFileHandler.getChatId());
    }

    public void setChatNameLabel(String s) {
        chatNameLabel.setText(s);
    }

    public void setLatestMessageLabel(String s) {
        latestMessageLabel.setText(s);
    }
}
