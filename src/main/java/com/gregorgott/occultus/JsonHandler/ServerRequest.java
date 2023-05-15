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

package com.gregorgott.occultus.JsonHandler;

import com.google.gson.JsonObject;
import com.gregorgott.occultus.User;

public class ServerRequest {
    public final static String POST_METHOD = "POST";
    public final static String DELETE_METHOD = "DELETE";
    public final static String GET_METHOD = "GET";
    public final static String KEY_ACTION = "KEY";
    public final static String INBOX_ACTION = "INBOX";
    public final static String MESSAGE_ACTION = "MESSAGE";
    public final static String GROUP_ACTION = "GROUP";
    public final static String SESSION_ACTION = "SESSION";
    public final static String LOGIN_ACTION = "LOGIN";
    public final static String TRUSTED_USER = "TRUSTED_USER";
    private final JsonObject serverRequestObject;
    private final JsonObject parametersObject;

    public ServerRequest(String method, String action) {
        serverRequestObject = new JsonObject();
        parametersObject = new JsonObject();

        serverRequestObject.addProperty("method", method);
        serverRequestObject.addProperty("action", action);
        serverRequestObject.add("parameters", parametersObject);
    }

    public JsonObject getParametersObject() {
        return parametersObject;
    }

    public void addUserLogin(User user) {
        getParametersObject().addProperty("user", user.getUsername());
        getParametersObject().addProperty("password", user.getPassword());
    }

    @Override
    public String toString() {
        return serverRequestObject.toString();
    }
}
