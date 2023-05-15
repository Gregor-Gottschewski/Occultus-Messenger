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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregorgott.occultus.ChatData.Message;
import com.gregorgott.occultus.ChatData.MessageType;
import com.gregorgott.occultus.FileManager;

import java.io.File;
import java.io.IOException;

public class ChatFileHandler extends JsonFileHandler {

    public ChatFileHandler(String chatFile) throws IOException {
        super(new File(new FileManager().getChatsFolder() + File.separator + chatFile));
    }

    public void createGroupChatFile(int chatId, int groupId, String groupName) throws IOException {
        getJsonObject().addProperty("type", "group");
        getJsonObject().addProperty("group_id", groupId);
        createBasicFile(chatId, groupName);
    }

    public void createPrivateChatFile(int chatId, String chatname) throws IOException {
        getJsonObject().addProperty("type", "private");
        createBasicFile(chatId, chatname);
    }

    private void createBasicFile(int chatId, String chatname) throws IOException {
        getJsonObject().addProperty("chat_id", chatId);
        getJsonObject().addProperty("chatname", chatname);
        getJsonObject().addProperty("newest_message", -1);
        getJsonObject().add("messages", new JsonArray());
        writeChanges();
    }

    public int getChatId() {
        return getJsonObject().get("chat_id").getAsInt();
    }

    public MessageType getChatType() throws IllegalArgumentException {
        return switch (getJsonObject().get("type").getAsString()) {
            case "private" -> MessageType.PRIVATE;
            case "group" -> MessageType.GROUP;
            default -> throw new IllegalArgumentException("MessageType should only be 'private' or 'group'.");
        };
    }

    public int getGroupId() throws IllegalStateException {
        if (getChatType() == MessageType.GROUP) {
            return getJsonObject().get("group_id").getAsInt();
        }

        throw new IllegalStateException("The chat is not a group chat");
    }

    public String getChatname() {
        return getJsonObject().get("chatname").getAsString();
    }

    public int getNewestMessageId() {
        return getJsonObject().get("newest_message").getAsInt();
    }

    public void addMessage(Message message) {
        int messageId = getNewestMessageId() + 1;
        message.setMessageId(messageId);
        getJsonObject().addProperty("newest_message", messageId);
        getMessagesArray().add(message.getMessageObject());
    }

    public JsonArray getMessagesArray() {
        return getJsonObject().get("messages").getAsJsonArray();
    }

    public Message getMessageById(int i) {
        for (int y = 0; y < getMessagesArray().size(); y++) {
            Message message = new Message(getMessagesArray().get(y).getAsJsonObject());
            if (message.getMessageId() == i)
                return message;
        }
        return null;
    }

    public String getLatestMessageText() throws IOException {
        readFile();
        if (getJsonObject().get("newest_message").getAsInt() > -1) {
            JsonObject messageObject = getMessagesArray().get(getMessagesArray().size() - 1).getAsJsonObject();
            return new Message(messageObject).getMessage();
        }
        return null;
    }
}
