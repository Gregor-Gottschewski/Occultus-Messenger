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
import com.gregorgott.occultus.FileHandler.ChatsHandler;
import com.gregorgott.occultus.Threads.InboxListener;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.Widgets.Messenger.Popup.NewChatWidget;
import com.gregorgott.occultus.UserInterface.Widgets.Messenger.Popup.NewGroupWidget;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChatsSideBarWidget extends VBox {
    private final MessengerWindow messengerWindow;
    private final VBox chatsVBox;

    private static ImageView createIconView(String s) {
        Image image = new Image(Objects.requireNonNull(ChatsSideBarWidget.class.getResourceAsStream("icons/" + s)));
        return new ImageView(image);
    }

    public ChatsSideBarWidget(MessengerWindow messengerWindow) {
        this.messengerWindow = messengerWindow;
        this.getStylesheets().add(getClass().getResource("ChatsSideBarWidget.css").toString());
        this.getStyleClass().add("CSW");

        chatsVBox = new VBox();
        chatsVBox.setSpacing(10);

        MenuItem messageMenuItems = new MenuItem(I18N.getString("menu.item.message"));
        messageMenuItems.setGraphic(createIconView("chat_bubble.png"));
        messageMenuItems.setOnAction(x ->  {
            stopListeners();
            messengerWindow.addToStackPane(new NewChatWidget(messengerWindow));
        });
        MenuItem newGroupMenuItem = new MenuItem(I18N.getString("menu.item.group"));
        newGroupMenuItem.setGraphic(createIconView("group.png"));
        newGroupMenuItem.setOnAction(x -> {
            stopListeners();
            messengerWindow.addToStackPane(new NewGroupWidget(messengerWindow));
        });

        MenuButton newMenuButton = new MenuButton(I18N.getString("menu.button.new"));
        newMenuButton.setGraphic(createIconView("chat_bubble.png"));
        newMenuButton.getItems().addAll(messageMenuItems, newGroupMenuItem);

        updateSideBar();
        InboxListener inboxListener = new InboxListener(() -> {
            Platform.runLater(this::updateSideBar);
        }, "SideBarUpdater");
        inboxListener.onlyNewChats(true);

        ScrollPane scrollPane = new ScrollPane(chatsVBox);
        scrollPane.setFitToWidth(true);

        VBox leftSideBox = new VBox(newMenuButton, scrollPane);
        leftSideBox.setSpacing(10);

        this.setPadding(new Insets(10));
        this.setPrefWidth(230);
        this.getChildren().addAll(leftSideBox);
    }

    public ChatPreviewWidget createChatPreview(ChatFileHandler chatFileHandler) {
        ChatPreviewWidget chatPreviewWidget = new ChatPreviewWidget(chatFileHandler);
        chatPreviewWidget.setOnAction(x -> {
            stopListeners();
            messengerWindow.setCenter(new ChatWidget(chatFileHandler, chatPreviewWidget));
        });
        return chatPreviewWidget;
    }

    private void stopListeners() {
        if (messengerWindow.getCenter() != null) {
            if (messengerWindow.getCenter() instanceof ChatWidget chatWidget) {
                chatWidget.removeInboxListener();
            }
        }
    }

    public void updateSideBar() {
        chatsVBox.getChildren().clear();
        try {
            ChatsHandler chatsHandler = new ChatsHandler();
            for (ChatFileHandler chatFileHandler : chatsHandler.getChats()) {
                chatsVBox.getChildren().add(createChatPreview(chatFileHandler));
            }
        } catch (IOException e) {
            OMAlert alert = new OMAlert(Alert.AlertType.ERROR, (Stage) messengerWindow.getScene().getWindow());
            alert.setHeaderText(I18N.getString("alert.error.read.file.message"));
            alert.setContentText(I18N.getString("alert.error.read.file.details"));
            alert.show();
        }
    }
}
