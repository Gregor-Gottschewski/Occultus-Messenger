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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gregorgott.occultus.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ChatsHandler extends JsonFileHandler {
    public ChatsHandler() throws IOException {
        super(new FileManager().getChatsFile());
    }

    public void createFile() throws IOException {
        getJsonObject().addProperty("biggest_chat_id", -1);
        getJsonObject().add("chats", new JsonArray());
        writeChanges();
    }

    public int getBiggestChatId() {
        JsonElement biggestChatIdElement = getJsonObject().get("biggest_chat_id");
        if (biggestChatIdElement == null) {
            return -1;
        } else {
            return biggestChatIdElement.getAsInt();
        }
    }

    private void setBiggestChatId(int id) {
        getJsonObject().addProperty("biggest_chat_id", id);
    }

    private JsonArray getChatsArray() {
        JsonElement chatsArrayElement = getJsonObject().get("chats");
        if (chatsArrayElement == null) {
            return new JsonArray();
        } else {
            return chatsArrayElement.getAsJsonArray();
        }
    }

    public ArrayList<ChatFileHandler> getChats() throws IOException {
        ArrayList<ChatFileHandler> chatFileHandlers = new ArrayList<>();

        for (int i = 0; i < getChatsArray().size(); i++) {
            JsonObject object = getChatsArray().get(i).getAsJsonObject();
            chatFileHandlers.add(new ChatFileHandler(object.get("filename").getAsString()));
        }

        return chatFileHandlers;
    }

    public String addPrivateChat(String chatname) {
        setBiggestChatId(getBiggestChatId() + 1);
        String filename = "chat_" + getBiggestChatId() + ".chatfile";

        JsonObject chatObject = new JsonObject();
        chatObject.addProperty("type", "private");
        chatObject.addProperty("chatname", chatname);
        chatObject.addProperty("filename", filename);

        getChatsArray().add(chatObject);

        return filename;
    }

    public String addGroupChat(int groupId) {
        setBiggestChatId(getBiggestChatId() + 1);
        String filename = "chat_" + getBiggestChatId() + ".chatfile";

        JsonObject chatObject = new JsonObject();
        chatObject.addProperty("type", "group");
        chatObject.addProperty("group_id", groupId);
        chatObject.addProperty("filename", filename);

        getChatsArray().add(chatObject);

        return filename;
    }

    public String getFilenameOf(String chatname) {
        for (int i = 0; i < getChatsArray().size(); i++) {
            JsonObject chatObject = getChatsArray().get(i).getAsJsonObject();
            if (Objects.equals(chatObject.get("chatname").getAsString(), chatname)) {
                return chatObject.get("filename").getAsString();
            }
        }
        return null;
    }

    public String getFilenameOf(int groupId) {
        for (int i = 0; i < getChatsArray().size(); i++) {
            JsonObject chatObject = getChatsArray().get(i).getAsJsonObject();
            if (chatObject.get("group_id") != null && chatObject.get("group_id").getAsInt() == groupId) {
                return chatObject.get("filename").getAsString();
            }
        }
        return null;
    }
}
