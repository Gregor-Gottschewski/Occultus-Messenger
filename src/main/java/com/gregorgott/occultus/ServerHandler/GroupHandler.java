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

import com.google.gson.JsonArray;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.User;

import java.io.IOException;
import java.rmi.RemoteException;

public class GroupHandler extends ServerConnector {
    private final User user;

    public GroupHandler(User user) throws IOException {
        this.user = user;
    }

    public String getGroupName(int groupId) {
        ServerRequest request = new ServerRequest(ServerRequest.GET_METHOD, ServerRequest.GROUP_ACTION);
        request.addUserLogin(user);
        request.getParametersObject().addProperty("group_id", groupId);

        ServerAnswer answer = requestServer(request);
        if (answer.isSuccess()) {
            return answer.getReturnElement().getAsString();
        }

        return "";
    }

    public int createGroup(String groupName, JsonArray membersArray) throws RemoteException {
        ServerRequest request = new ServerRequest(ServerRequest.POST_METHOD, ServerRequest.GROUP_ACTION);
        request.addUserLogin(user);
        request.getParametersObject().addProperty("group_name", groupName);
        request.getParametersObject().add("members", membersArray);

        ServerAnswer answer = requestServer(request);
        if (answer.isSuccess()) {
            return answer.getReturnElement().getAsInt();
        }

        throw new RemoteException(answer.getErrorMessage());
    }

    public void deleteGroup(int groupId) throws RemoteException{
        ServerRequest request = new ServerRequest(ServerRequest.DELETE_METHOD, ServerRequest.GROUP_ACTION);
        request.addUserLogin(user);
        request.getParametersObject().addProperty("group_id", groupId);

        ServerAnswer answer = requestServer(request);
        if (!answer.isSuccess()) {
            throw new RemoteException(answer.getErrorMessage());
        }
    }
}
