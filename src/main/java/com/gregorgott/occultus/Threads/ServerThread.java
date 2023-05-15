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

package com.gregorgott.occultus.Threads;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregorgott.occultus.ChatData.Message;
import com.gregorgott.occultus.ChatData.MessageType;
import com.gregorgott.occultus.Encryption.DecryptionHandler;
import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.FileHandler.ChatsHandler;
import com.gregorgott.occultus.FileHandler.ServerInfoFileHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.JsonHandler.ServerRequest;
import com.gregorgott.occultus.ServerAnswer;
import com.gregorgott.occultus.ServerConnection;
import com.gregorgott.occultus.ServerHandler.GroupHandler;
import com.gregorgott.occultus.Threads.ServerDatatypes.RawMessage;
import com.gregorgott.occultus.User;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.alert.OMAlert;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.TimerTask;

public class ServerThread extends TimerTask {
    private final DecryptionHandler decryptionHandler;
    private final User user;
    private final ServerRequest serverRequest;
    private final ServerInfoFileHandler serverInfo;
    private ServerConnection serverConnection;

    private static void runListeners(int chatId) {
        for (InboxListener inboxListener : InboxListener.getInboxListeners()) {
            if (inboxListener.getChatId() == chatId) {
                inboxListener.run();
            }

            if (!inboxListener.isOnlyNewChats() && inboxListener.getChatId() == -1) {
                inboxListener.run();
            }
        }
    }

    public ServerThread(ServerInfoFileHandler serverInfo, String privateKey) throws IOException {
        this.serverInfo = serverInfo;

        decryptionHandler = new DecryptionHandler(privateKey.getBytes());

        UserInfoFileHandler userInfo = new UserInfoFileHandler();
        user = userInfo.getUser();

        setServerConnection();

        serverRequest = new ServerRequest(ServerRequest.GET_METHOD, ServerRequest.INBOX_ACTION);
        serverRequest.getParametersObject().addProperty("session_name", userInfo.getSessionName());
        serverRequest.addUserLogin(user);
    }

    private void setServerConnection() throws IOException {
        if (serverConnection != null && !serverConnection.isClosed()) {
            serverConnection.close();
        }
        serverConnection = new ServerConnection(serverInfo);
    }

    @Override
    public boolean cancel() {
        super.cancel();
        try {
            serverConnection.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void run() {
        // Serverantwort
        ServerAnswer serverAnswer = serverConnection.callServer(serverRequest.toString());

        if (!serverAnswer.isSuccess()) {
            try {
                setServerConnection();
                return;
            } catch (IOException e) {
                return;
            }
        }

        // Array mit allen neuen Nachrichten
        JsonArray messagesArray = serverAnswer.getReturnElement().getAsJsonArray();

        for (int i = 0; i < messagesArray.size(); i++) {
            RawMessage rawMessage = new RawMessage(messagesArray.get(i).getAsJsonObject());
            boolean newChat = false;
            try {
                ChatsHandler chatsHandler = new ChatsHandler();
                ChatFileHandler chatFileHandler = null;
                if (rawMessage.getMessageType() == MessageType.PRIVATE) {
                    String var1 = chatsHandler.getFilenameOf(rawMessage.getSender());

                    if (var1 == null) {
                        int chatId = chatsHandler.getBiggestChatId() + 1;

                        String var2 = chatsHandler.addPrivateChat(rawMessage.getSender());
                        chatFileHandler = new ChatFileHandler(var2);
                        chatFileHandler.createPrivateChatFile(chatId, rawMessage.getSender());
                        newChat = true;
                    } else {
                        chatFileHandler = new ChatFileHandler(var1);
                    }
                } else if (rawMessage.getMessageType() == MessageType.GROUP) {
                    String var1 = chatsHandler.getFilenameOf(rawMessage.getGroupId());

                    if (var1 == null) {
                        int chatId = chatsHandler.getBiggestChatId();
                        GroupHandler groupHandler = new GroupHandler(user);

                        String var2 = chatsHandler.addGroupChat(rawMessage.getGroupId());
                        chatFileHandler = new ChatFileHandler(var2);
                        chatFileHandler.createGroupChatFile(chatId, rawMessage.getGroupId(), groupHandler.getGroupName(rawMessage.getGroupId()));
                        newChat = true;
                    } else {
                        chatFileHandler = new ChatFileHandler(var1);
                    }
                }

                chatsHandler.writeChanges();

                if (chatFileHandler != null) {
                    Message message = new Message(decryptAsJsonObject(rawMessage.getMessage(), getSenderPublicKey(rawMessage.getSender())));
                    message.setSender(rawMessage.getSender());
                    chatFileHandler.addMessage(message);
                    chatFileHandler.writeChanges();
                    if (newChat) {
                        runOnlyNewChatsListeners();
                    }

                    runListeners(chatFileHandler.getChatId());
                }
            } catch (RemoteException e) {
                Platform.runLater(() -> {
                    OMAlert alert = new OMAlert(Alert.AlertType.WARNING);
                    alert.setHeaderText(I18N.getString("alert.warning.encryption.message"));
                    alert.setContentText(I18N.getString("alert.warning.encryption.details", rawMessage.getSender()));
                    alert.show();
                });
                cancel();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    OMAlert alert = new OMAlert(Alert.AlertType.ERROR);
                    alert.setHeaderText(I18N.getString("alert.error.read.file.message"));
                    alert.setContentText(I18N.getString("alert.error.read.file.details"));
                    alert.show();
                });
                cancel();
            } catch (PGPException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void runOnlyNewChatsListeners() {
        for (InboxListener inboxListener : InboxListener.getInboxListeners()) {
            if (inboxListener.isOnlyNewChats()) {
                inboxListener.run();
            }
        }
    }

    private byte[] getSenderPublicKey(String sender) throws RemoteException {
        if (sender == null || sender.isEmpty()) {
            throw new IllegalArgumentException("Illegal sender.");
        }

        ServerRequest serverRequest = new ServerRequest(ServerRequest.GET_METHOD, ServerRequest.KEY_ACTION);
        serverRequest.addUserLogin(user);
        serverRequest.getParametersObject().addProperty("search_id", sender);

        ServerAnswer answer = serverConnection.callServer(serverRequest.toString());
        if (!answer.isSuccess()) {
            throw new RemoteException(answer.getErrorMessage());
        }

        byte[] b = answer.getReturnElement().getAsString().getBytes();

        if (b.length == 0) {
            throw new RemoteException("Error while receiving key.");
        }

        return b;
    }

    private JsonObject decryptAsJsonObject(String s, byte[] senderKey) throws IOException, PGPException {
        String encrypted = decryptionHandler.getDecryptedMessage(senderKey, s);
        return new Gson().fromJson(encrypted, JsonObject.class);
    }
}
