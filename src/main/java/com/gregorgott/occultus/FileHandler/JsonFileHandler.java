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

package com.gregorgott.occultus.FileHandler;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gregorgott.occultus.JsonHandler.JsonFileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonFileHandler {
    private final File file;
    private JsonObject jsonObject;

    public JsonFileHandler(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new RuntimeException("Error while creating new file!");
            }
        }
        readFile();
    }

    public void writeChanges() throws IOException {
        File backupFile = null;

        // create backup file
        if (file.exists()) {
            backupFile = new File(file + ".backup_" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
            Files.move(file, backupFile);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        new Gson().toJson(jsonObject, bw);
        bw.close();

        // delete backup
        if (backupFile != null) {
            backupFile.delete();
        }

        // sync
        readFile();
    }

    public void readFile() throws IOException {
        JsonFileReader jsonFR = new JsonFileReader(file);
        if (jsonFR.getJsonObject() != null) {
            jsonObject = jsonFR.getJsonObject();
        } else {
            jsonObject = new JsonObject();
        }
    }

    protected JsonObject getJsonObject() {
        return jsonObject;
    }
}
