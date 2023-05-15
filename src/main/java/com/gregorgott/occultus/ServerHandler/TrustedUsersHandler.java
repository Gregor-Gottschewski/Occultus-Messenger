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

package com.gregorgott.occultus.ServerHandler;

import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.User;

import java.io.IOException;

public class TrustedUsersHandler extends ServerConnector {
    private final User user;

    public TrustedUsersHandler() throws IOException {
        super();

        user = new UserInfoFileHandler().getUser();
    }

    public ServerAnswer addTrustedUser(String username) {
        ServerRequest serverRequest = new ServerRequest(ServerRequest.POST_METHOD, ServerRequest.TRUSTED_USER);
        serverRequest.addUserLogin(user);
        serverRequest.getParametersObject().addProperty("email", username);

        return requestServer(serverRequest);
    }

    public ServerAnswer removeTrustedUser(String username) {
        ServerRequest serverRequest = new ServerRequest(ServerRequest.DELETE_METHOD, ServerRequest.TRUSTED_USER);
        serverRequest.addUserLogin(user);
        serverRequest.getParametersObject().addProperty("email", username);

        return requestServer(serverRequest);
    }

    public ServerAnswer getTrustedUsers() {
        ServerRequest serverRequest = new ServerRequest(ServerRequest.GET_METHOD, ServerRequest.TRUSTED_USER);
        serverRequest.addUserLogin(user);

        return requestServer(serverRequest);
    }
}
