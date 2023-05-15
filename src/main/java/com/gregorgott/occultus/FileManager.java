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

import java.io.File;

/**
 * Die Klasse {@code FileManager} gibt alle, im Arbeitsverzeichnis gespeicherten Dateien zurück.
 * Ordnernamen von Unterordnern des Arbeitsverzeichnisses werden hier als statische Variable definiert.
 *
 * @author Gregor Gottschewski
 * @since 21-12-2022 (DD-MM-YYYY)
 * @version 1.0.0
 */
public class FileManager {
    private static final String MAIN_FOLDER = "EKS_Messenger";
    private static final String CHAT_FOLDER = "Chats";
    private static final String SETTINGS_FOLDER = "Settings";
    private final File workDir;

    public FileManager() {
        String os = System.getProperty("os.name").toUpperCase();

        if (os.startsWith("MAC")) {
            workDir = new File(System.getProperty("user.home") + File.separator + "Library" + File.separator +
                    "Application Support" + File.separator + MAIN_FOLDER);
        } else if (os.startsWith("WINDOWS")) {
            workDir = new File(System.getenv("APPDATA") + File.separator + MAIN_FOLDER);
        } else if (os.startsWith("LINUX")) {
            workDir = new File(System.getProperty("user.home") + File.separator + "." + MAIN_FOLDER);
        } else {
            workDir = new File(System.getProperty("user.home") + File.separator + MAIN_FOLDER);
        }
    }

    public File getWorkDir() {
        return workDir;
    }

    public boolean createFolders() {
        if (!workDir.mkdir()) {
            return false;
        }

        String[] folders = {CHAT_FOLDER, SETTINGS_FOLDER};
        for (String folder : folders) {
            File file = new File(workDir + File.separator + folder);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    return false;
                }
            }
        }

        return true;
    }

    public File getSettingsFolder() {
        return new File(workDir + File.separator + SETTINGS_FOLDER);
    }

    public File getUserDataFile() {
        return new File(getSettingsFolder() + File.separator + "userdata.json");
    }

    public File getServerSettingsFile() {
        return new File(getSettingsFolder() + File.separator + "server_info.json");
    }

    public File getChatsFolder() {
        return new File(workDir + File.separator + CHAT_FOLDER);
    }

    public File getChatsFile() {
        return new File(getChatsFolder() + File.separator + "chats.json");
    }
}
