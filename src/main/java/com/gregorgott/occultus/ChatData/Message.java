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

package com.gregorgott.occultus.ChatData;

import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private final JsonObject messageObject;

    public Message() {
        messageObject = new JsonObject();
    }

    public Message(JsonObject messageObject) {
        this.messageObject = messageObject;
    }

    public int getMessageId() {
        return messageObject.get("message_id").getAsInt();
    }
    public void setMessageId(int id) {
        messageObject.addProperty("message_id", id);
    }

    public String getSender() {
        return messageObject.get("sender").getAsString();
    }

    public void setSender(String s) {
        messageObject.addProperty("sender", s);
    }

    public String getMessage() {
        return messageObject.get("message").getAsString();
    }

    public void setMessage(String message) {
        messageObject.addProperty("message", message);
    }

    public String getDate() {
        LocalDateTime dateTime = LocalDateTime.parse(messageObject.get("date").getAsString());
        return dateTime.getDayOfMonth() + "." + dateTime.getMonthValue() + "." + dateTime.getYear() + " " +
                dateTime.getHour() + ":" + dateTime.getMinute();
    }

    public void setDate() {
        messageObject.addProperty("date", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    public JsonObject getMessageObject() {
        return messageObject;
    }
}
