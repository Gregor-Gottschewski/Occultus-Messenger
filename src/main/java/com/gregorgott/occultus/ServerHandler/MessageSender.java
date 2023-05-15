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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.gregorgott.occultus.ChatData.Message;
import com.gregorgott.occultus.ChatData.MessageType;
import com.gregorgott.occultus.Encryption.EncryptionHandler;
import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.User;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

public class MessageSender extends ServerConnector {
    private final ChatFileHandler chatFileHandler;
    private final User user;
    private final byte[] privateKey;

    public MessageSender(ChatFileHandler chatFileHandler) throws IOException {
        super();

        this.chatFileHandler = chatFileHandler;

        UserInfoFileHandler userInfo = new UserInfoFileHandler();
        user = userInfo.getUser();

        privateKey = userInfo.getPrivateKey().getBytes();
    }

    public void sendMessage(String messageContent) throws IOException, PGPException {
        // erstelle Nachricht
        Message message = new Message();
        message.setMessage(messageContent);
        message.setDate();

        if (chatFileHandler.getChatType() == MessageType.PRIVATE) {
            sendPrivateMessage(message, chatFileHandler.getChatname(), MessageType.PRIVATE);
        } else {
            sendGroupMessage(message, chatFileHandler.getGroupId());
        }

        message.setSender("Du");
        // add message to local files
        chatFileHandler.addMessage(message);
        chatFileHandler.writeChanges();
    }

    private void sendPrivateMessage(Message message, String recipient, MessageType messageType) throws IOException, PGPException {
        // bekomme alle Schlüssel von Nutzer:in
        ServerKeyHandler serverKeyHandler = new ServerKeyHandler();
        byte[] recipientPublicKey = serverKeyHandler.getPublicKeyOf(recipient).getBytes();
        serverKeyHandler.close();

        EncryptionHandler encHandler = new EncryptionHandler(message.getMessageObject().toString(),
                privateKey, recipientPublicKey);

        ServerRequest serverRequest = buildServerRequest(encHandler.getEncryptedMessage(), recipient, messageType);

        ServerAnswer answer = requestServer(serverRequest);
        if (!answer.isSuccess()) {
            throw new RemoteException(answer.getErrorMessage());
        }
    }

    private void sendGroupMessage(Message message, int groupId) throws IOException, PGPException {
        ServerRequest serverRequest = new ServerRequest(ServerRequest.GET_METHOD, "GROUP_MEMBERS");
        serverRequest.addUserLogin(user);
        serverRequest.getParametersObject().addProperty("group_id", groupId);

        ServerAnswer answer = requestServer(serverRequest);
        if (!answer.isSuccess()) {
            throw new RemoteException(answer.getErrorMessage());
        }

        JsonArray membersArray = new Gson().fromJson(answer.getReturnElement(), JsonArray.class);
        for (int  i = 0; i < membersArray.size(); i++) {
            String member = membersArray.get(i).getAsString();
            if (!Objects.equals(member, user.getUsername())) {
                sendPrivateMessage(message, membersArray.get(i).getAsString(), MessageType.GROUP);
            }
        }
    }

    private ServerRequest buildServerRequest(String message, String recipient, MessageType messageType) {
        String messageTypeString = switch (messageType) {
            case PRIVATE -> "private";
            case GROUP -> "group";
        };

        ServerRequest serverRequest = new ServerRequest(ServerRequest.POST_METHOD, ServerRequest.MESSAGE_ACTION);
        serverRequest.addUserLogin(user);
        serverRequest.getParametersObject().addProperty("message_type", messageTypeString);
        serverRequest.getParametersObject().addProperty("recipient", recipient);
        serverRequest.getParametersObject().addProperty("message", message);

        if (messageType == MessageType.GROUP) {
            serverRequest.getParametersObject().addProperty("group_id", chatFileHandler.getGroupId());
        }

        return serverRequest;
    }
}
