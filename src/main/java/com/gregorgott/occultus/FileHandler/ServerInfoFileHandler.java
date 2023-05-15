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

import com.google.gson.JsonElement;
import com.gregorgott.occultus.FileManager;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ServerInfoFileHandler extends JsonFileHandler {
    private JsonElement serverUrl;
    private JsonElement port;

    public ServerInfoFileHandler() throws IOException {
        super(new FileManager().getServerSettingsFile());

        serverUrl = getJsonObject().get("url");
        port = getJsonObject().get("port");
    }

    public void setServerUrl(String serverUrl) {
        getJsonObject().addProperty("url", serverUrl);
        this.serverUrl = getJsonObject().get("url");
    }

    public void setServerPort(int port) {
        getJsonObject().addProperty("port", port);
        this.port = getJsonObject().get("port");
    }

    public String getServerUrl() throws NoSuchElementException {
        if (serverUrl == null) {
            throw new NoSuchElementException("Error while reading file: ULR not found");
        }

        return serverUrl.getAsString();
    }

    public int getPort() throws NoSuchElementException {
        if (port == null) {
            throw new NoSuchElementException("Error while reading file: Port not found");
        }

        return port.getAsInt();
    }

    public boolean checkForCompleteness() {
        try {
            String s = getServerUrl();
            int i = getPort();

            return !s.isEmpty() && i != 0;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
