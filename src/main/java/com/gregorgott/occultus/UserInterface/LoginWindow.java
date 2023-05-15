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

package com.gregorgott.occultus.UserInterface;

import com.gregorgott.occultus.UserInterface.Widgets.Login.ServerWidget;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;

public class LoginWindow extends BorderPane {
    private final Dictionary<String, String> relatedWindows;
    private final Stack<Widget> pages;
    private final Button backButton;
    private final Button nextButton;
    private final Label headline;

    public LoginWindow() {
        super();

        relatedWindows = new Hashtable<>();
        relatedWindows.put("main", MessengerWindow.class.getName());

        pages = new Stack<>();

        backButton = new Button("Back");
        backButton.setDisable(true);
        backButton.setOnAction(x -> oneWidgetBack());

        headline = new Label("Occultus Messenger");
        headline.setId("header-1");

        HBox topBox = new HBox(backButton, headline);
        topBox.setSpacing(15);
        topBox.setAlignment(Pos.CENTER_LEFT);

        nextButton = new Button("Weiter");
        nextButton.setDefaultButton(true);

        setContent(null, new ServerWidget(this));

        this.setTop(topBox);
        this.setBottom(nextButton);
        BorderPane.setAlignment(this.getBottom(), Pos.CENTER_RIGHT);
        BorderPane.setMargin(this.getBottom(), new Insets(10));
        BorderPane.setMargin(this.getTop(), new Insets(10));
    }

    public void setHeadline(String var1) {
        headline.setText(var1);
    }

    public void setContent(Widget oldVar, Widget newVar) {
        if (oldVar != null) {
            pages.push(oldVar);
            backButton.setDisable(false);
        }

        setNextButtonLabel("Weiter");

        newVar.setWindowUI();
        this.setCenter(newVar);
        BorderPane.setMargin(this.getCenter(), new Insets(10));
    }

    public void setNextButtonAction(EventHandler<ActionEvent> eventHandler) {
        nextButton.setOnAction(eventHandler);
    }

    public void setNextButtonLabel(String var1) {
        nextButton.setText(var1);
    }

    private void oneWidgetBack() {
        Widget widget = pages.pop();
        widget.setWindowUI();
        setNextButtonLabel("Weiter");
        this.setCenter(widget);
        if (pages.isEmpty()) {
            backButton.setDisable(true);
        }
    }
}
