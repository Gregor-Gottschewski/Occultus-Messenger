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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection extends Socket {
    private final String serverUrl;
    private final PrintWriter out;
    private final BufferedReader in;

    public ServerConnection(ServerInfoFileHandler serverInfo) throws IOException {
        this(serverInfo.getServerUrl(), serverInfo.getPort());
    }

    @Deprecated
    public ServerConnection(String serverUrl, int portNumber) throws IOException {
        super(serverUrl, portNumber);
        this.serverUrl = serverUrl;
        out = new PrintWriter(getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public ServerAnswer callServer(String input) {
        try {
            out.println(input);
            return new ServerAnswer(in.readLine());
        } catch (UnknownHostException e) {
            System.err.println("Host " + serverUrl + " nicht gefunden.");
        } catch (IOException e) {
            System.err.println("Es konnte keine I/O Verbindung zum Host " + serverUrl + " hergestellt werden.");
        }

        return null;
    }
}
