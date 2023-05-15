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

import com.gregorgott.occultus.FileHandler.ChatsHandler;
import com.gregorgott.occultus.FileHandler.ServerInfoFileHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.FileManager;
import com.gregorgott.occultus.Threads.ServerThread;
import com.gregorgott.occultus.Threads.TimerManager;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.Widgets.Messenger.ChatsSideBarWidget;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;

public class MessengerWindow extends StackPane {
    private final Dictionary<String, String> relatedWindows;
    private final SplitPane splitPane;
    private final ChatsSideBarWidget chatsSideBarWidget;

    public MessengerWindow() {
        relatedWindows = new Hashtable<>();
        relatedWindows.put("login", LoginWindow.class.getName());

        File chatsFile = new FileManager().getChatsFile();
        if (!chatsFile.exists()) {
            try {
                ChatsHandler chatsHandler = new ChatsHandler();
                chatsHandler.createFile();
            } catch (IOException e) {
                OMAlert alert = new OMAlert(Alert.AlertType.ERROR, (Stage) this.getScene().getWindow());
                alert.setHeaderText(I18N.getString("alert.error.read.file.message"));
                alert.setContentText(I18N.getString("alert.error.read.file.details"));
                alert.showAndWait();
                System.exit(-1);
            }
        }

        chatsSideBarWidget = new ChatsSideBarWidget(this);
        chatsSideBarWidget.setMaxWidth(275);
        chatsSideBarWidget.setPrefWidth(250);
        splitPane = new SplitPane(chatsSideBarWidget);
        this.getChildren().add(splitPane);
    }

    public static void connectToServer(ServerInfoFileHandler serverInfo, UserInfoFileHandler userInfo) throws IOException {
        ServerThread serverThread = new ServerThread(serverInfo, userInfo.getPrivateKey());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(serverThread, 5500, 2500);
        TimerManager.addTimer(timer);
    }

    public ChatsSideBarWidget getChatsSideBarWidget() {
        return chatsSideBarWidget;
    }

    public Node getFirstStackElement() {
        return this.getChildren().get(0);
    }

    public void addToStackPane(Node n) {
        if (this.getChildren().size() < 2) {
            this.getChildren().add(n);
        }
    }

    public void removeFromStackPane() {
        if (this.getChildren().size() >= 2) {
            this.getChildren().remove(1);
        }
    }

    public void setCenter(Node n) {
        if (splitPane.getItems().size() < 2) {
            splitPane.getItems().add(n);
        } else {
            splitPane.getItems().set(1, n);
        }
    }

    public Node getCenter() {
        if (splitPane.getItems().size() <= 1) {
            return null;
        }

        return splitPane.getItems().get(1);
    }
}
