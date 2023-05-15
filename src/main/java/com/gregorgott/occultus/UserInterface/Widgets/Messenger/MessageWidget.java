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
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MessageWidget extends BorderPane {
    public MessageWidget(Message message, Pane parent) {
        this.getStylesheets().add(MessageWidget.class.getResource("MessageWidget.css").toString());
        this.getStyleClass().add("MW");

        Label senderDateLabel = new Label(I18N.getString("at", message.getSender(), message.getDate()));
        senderDateLabel.setId("sender-info");

        Label messageLabel = new Label(message.getMessage());
        messageLabel.maxWidthProperty().bind(parent.widthProperty().divide(3).multiply(2));
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(7));

        this.setCenter(messageLabel);
        this.getCenter().setId("message");
        this.setBottom(senderDateLabel);

        if (message.getSender().equals("Du")) {
            BorderPane.setAlignment(messageLabel, Pos.CENTER_RIGHT);
            BorderPane.setAlignment(senderDateLabel, Pos.CENTER_RIGHT);
        } else {
            BorderPane.setAlignment(messageLabel, Pos.CENTER_LEFT);
        }
    }
}
