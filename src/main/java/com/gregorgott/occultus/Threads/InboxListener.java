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

import java.util.ArrayList;

public class InboxListener {
    private static final ArrayList<InboxListener> inboxListeners = new ArrayList<>();
    private final Runnable runnable;
    private final String runnableThreadName;
    private int chatId = -1;
    private boolean onlyNewChats;

    public static ArrayList<InboxListener> getInboxListeners() {
        return inboxListeners;
    }

    public InboxListener(Runnable runnable, String runnableThreadName) {
        this.runnable = runnable;
        this.runnableThreadName = runnableThreadName;

        onlyNewChats = false;

        inboxListeners.add(this);
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public boolean isOnlyNewChats() {
        return onlyNewChats;
    }

    public void onlyNewChats(boolean onlyNewChats) {
        this.onlyNewChats = onlyNewChats;
    }

    public void run() {
        Thread t = new Thread(runnable, runnableThreadName);
        t.setDaemon(true);
        t.start();
    }

    public void removeThisInboxListener() {
        inboxListeners.remove(this);
    }
}
