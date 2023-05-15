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

package com.gregorgott.occultus;

import com.gregorgott.occultus.FileHandler.ServerInfoFileHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.Threads.ServerThread;
import com.gregorgott.occultus.Threads.TimerManager;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.LoginWindow;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.fxmisc.cssfx.CSSFX;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class starts the application and creates non-existing folders.
 *
 * @author Gregor Gottschewski
 * @version 1.0.0
 * @since 2023-02-27 (YYYY-MM-DD)
 */
public class Main extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        FileManager fileManager = new FileManager();
        File workDirFile = fileManager.getWorkDir();
        // check main folder
        if (!workDirFile.exists()) {
            if (!fileManager.createFolders()) {
                OMAlert alert = new OMAlert(Alert.AlertType.ERROR, stage);
                alert.setHeaderText(I18N.getString("alert.error.create.folders.message"));
                alert.setContentText(I18N.getString("alert.error.create.folders.details"));
                alert.showAndWait();
                System.exit(-1);
            }
        }

        Scene scene;

        ServerInfoFileHandler serverInfoFileHandler = new ServerInfoFileHandler();
        UserInfoFileHandler userInfoFileHandler = new UserInfoFileHandler();

        if (userInfoFileHandler.checkForCompleteness() && serverInfoFileHandler.checkForCompleteness()) {
            scene = new Scene(new MessengerWindow());
            connectToServer(serverInfoFileHandler, userInfoFileHandler);
        } else {
            scene = new Scene(new LoginWindow());
        }

        scene.getStylesheets().add(Main.class.getResource("UserInterface/classic_style.css").toString());

        stage.setTitle("Occultus Messenger");
        stage.setMinHeight(500);
        stage.setMinWidth(650);
        stage.setScene(scene);
        stage.show();

        // arguments
        Parameters parameters = getParameters();
        Map<String, String> namedParameters = parameters.getNamed();
        for (Map.Entry<String, String> param : namedParameters.entrySet()) {
            if (param.getKey().equals("mode")) {
                if (param.getValue().equals("devMode")) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "Developer mode enabled!");
                    CSSFX.start();
                }
            }
        }
    }

    @Override
    public void stop() {
        for (Timer t : TimerManager.getTimers()) {
            t.cancel();
        }
    }

    public static void connectToServer(ServerInfoFileHandler serverInfo, UserInfoFileHandler userInfo) throws IOException {
        ServerThread serverThread = new ServerThread(serverInfo, userInfo.getPrivateKey());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(serverThread, 5500, 2500);
        TimerManager.addTimer(timer);
    }
}
