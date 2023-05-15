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
import com.gregorgott.occultus.User;

import java.io.IOException;
import java.util.NoSuchElementException;

public class UserInfoFileHandler extends JsonFileHandler {
    private JsonElement username;
    private JsonElement password;
    private JsonElement session;
    private JsonElement publicKey;
    private JsonElement privateKey;

    public UserInfoFileHandler() throws IOException {
        super(new FileManager().getUserDataFile());

        username = getJsonObject().get("username");
        password = getJsonObject().get("password");
        session = getJsonObject().get("session_name");
        publicKey = getJsonObject().get("public_key");
        privateKey = getJsonObject().get("private_key");
    }

    public User getUser() throws NoSuchElementException {
        if (username == null || password == null) {
            throw new NoSuchElementException("Error while reading user data.");
        }

        String username = this.username.getAsString();
        String password = this.password.getAsString();

        User user = new User(username, password);
        user.setSession(getSessionName());

        return user;
    }

    public void setUsername(String username) {
        getJsonObject().addProperty("username", username);
        this.username = getJsonObject().get("username");
    }

    public void setPassword(String password) {
        getJsonObject().addProperty("password", password);
        this.password = getJsonObject().get("password");
    }

    public String getSessionName() {
        if (session == null) {
            return "";
        }

        return session.getAsString();
    }

    public void setSessionName(String session) {
        getJsonObject().addProperty("session_name", session);
        this.session = getJsonObject().get("session_name");
    }

    public boolean checkForCompleteness() {
        try {
            User user = getUser();
            String[] userdata = new String[]{user.getUsername(), user.getPassword(), user.getSession()};

            for (String data : userdata) {
                if (data == null) {
                    return false;
                }
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        return true;
    }

    public String getPublicKey() throws NoSuchElementException{
        if (publicKey == null) {
            throw new NoSuchElementException("Error while reading public key.");
        }

        return publicKey.getAsString();
    }

    public void setPublicKey(String publicKey) {
        getJsonObject().addProperty("public_key", publicKey);
        this.publicKey = getJsonObject().get("public_key");
    }

    public String getPrivateKey() throws NoSuchElementException{
        if (privateKey == null) {
            throw new NoSuchElementException("Error while reading private key.");
        }

        return privateKey.getAsString();
    }

    public void setPrivateKey(String privateKey) {
        getJsonObject().addProperty("private_key", privateKey);
        this.privateKey = getJsonObject().get("private_key");
    }
}
