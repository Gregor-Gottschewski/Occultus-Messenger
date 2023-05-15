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

import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.Widget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class Popup extends Widget {
    private final Button okButton;

    public Popup(MessengerWindow messengerWindow, String header) {
        this.getStyleClass().add("widget-popup");
        this.getStylesheets().add(NewChatWidget.class.getResource("WidgetPopup.css").toString());

        messengerWindow.getFirstStackElement().setDisable(true);

        Label headline = new Label(header);
        headline.setId("headline");

        Button cancelButton = new Button(I18N.getString("cancel"));
        cancelButton.setOnAction(x -> close(messengerWindow));

        okButton = new Button(I18N.getString("ok"));
        okButton.setDisable(true);
        okButton.setDefaultButton(true);

        HBox bottomBox = new HBox(cancelButton, okButton);
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER_RIGHT);

        this.setMaxHeight(300);
        this.setMaxWidth(450);
        this.setTop(headline);
        this.setBottom(bottomBox);
        this.setPadding(new Insets(15));

        BorderPane.setAlignment(this.getBottom(), Pos.CENTER_RIGHT);
    }

    protected void close(MessengerWindow messengerWindow) {
        messengerWindow.getChatsSideBarWidget().updateSideBar();
        messengerWindow.removeFromStackPane();
        messengerWindow.getFirstStackElement().setDisable(false);
    }

    public Button getOkButton() {
        return okButton;
    }

    @Override
    protected void setWindowUI() {

    }
}
