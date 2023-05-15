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

package com.gregorgott.occultus.Threads.ServerDatatypes;

import com.google.gson.JsonObject;
import com.gregorgott.occultus.ChatData.MessageType;

public class RawMessage {
    private final JsonObject jsonObject;
    private final String message;
    private final MessageType messageType;

    public RawMessage(JsonObject jsonObject) {
        this.jsonObject = jsonObject;

        message = jsonObject.get("message").getAsString();
        if (jsonObject.get("message_type").getAsString().equals("group")) {
            messageType = MessageType.GROUP;
        } else {
            messageType = MessageType.PRIVATE;
        }
    }

    public String getSender() {
        return jsonObject.get("sender").getAsString();
    }

    public int getGroupId() {
        if (messageType != MessageType.GROUP) {
            throw new UnsupportedOperationException("Die Nachricht ist keine Gruppennachricht.");
        }

        return jsonObject.get("group_id").getAsInt();
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
