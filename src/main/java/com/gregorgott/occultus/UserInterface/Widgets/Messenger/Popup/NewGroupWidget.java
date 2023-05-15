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

package com.gregorgott.occultus.UserInterface.Widgets.Messenger.Popup;

import com.google.gson.JsonArray;
import com.gregorgott.occultus.FileHandler.ChatFileHandler;
import com.gregorgott.occultus.FileHandler.ChatsHandler;
import com.gregorgott.occultus.FileHandler.UserInfoFileHandler;
import com.gregorgott.occultus.ServerHandler.GroupHandler;
import com.gregorgott.occultus.ServerHandler.ServerKeyHandler;
import com.gregorgott.occultus.UserInterface.I18N.I18N;
import com.gregorgott.occultus.UserInterface.MessengerWindow;
import com.gregorgott.occultus.UserInterface.Widgets.Messenger.ChatPreviewWidget;
import com.gregorgott.occultus.UserInterface.Widgets.Messenger.ChatWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewGroupWidget extends Popup {
    static class MemberLabel extends HBox {
        private final Button removeButton;

        public MemberLabel(String member) {
            this.getStylesheets().add(MemberLabel.class.getResource("MemberLabel.css").toString());
            this.getStyleClass().add("member-label");

            Label label = new Label(member);
            removeButton = new Button("x");

            this.getChildren().addAll(label, removeButton);
            this.setAlignment(Pos.CENTER_LEFT);
            this.setPadding(new Insets(5));
        }

        public Button getRemoveButton() {
            return removeButton;
        }
    }

    private final TextField groupNameTextField;

    public NewGroupWidget(MessengerWindow messengerWindow) {
        super(messengerWindow, I18N.getString("new.group"));

        ArrayList<String> members = new ArrayList<>();

        Label groupNameLabel = new Label(I18N.getString("label.group.name"));
        groupNameTextField = new TextField();
        groupNameTextField.setPrefWidth(250);
        groupNameTextField.setOnKeyReleased(x -> {
            getOkButton().setDisable(groupNameTextField.getText().isEmpty());
        });
        groupNameLabel.setLabelFor(groupNameTextField);

        HBox groupNameBox = new HBox(groupNameLabel, groupNameTextField);
        groupNameBox.setSpacing(8);
        groupNameBox.setAlignment(Pos.CENTER_LEFT);

        Label addParticipantsLabel = new Label(I18N.getString("label.add.participants"));

        FlowPane participantsFlowPane = new FlowPane();
        participantsFlowPane.setHgap(3);
        participantsFlowPane.setVgap(3);

        TextField textField = new TextField();
        textField.setPromptText(I18N.getString("text.field.add.new.participant"));
        textField.setPrefWidth(250);

        Button addNewParticipantsButton = new Button(I18N.getString("add"));
        addNewParticipantsButton.setOnAction(x -> {
            String participant = textField.getText();

            if (!members.contains(participant)) {
                try {
                    if (checkRecipient(participant)) {
                        MemberLabel memberLabel = new MemberLabel(participant);
                        memberLabel.getRemoveButton().setOnAction(y -> {
                            participantsFlowPane.getChildren().remove(memberLabel);
                        });

                        members.add(participant);
                        participantsFlowPane.getChildren().add(memberLabel);
                        textField.clear();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        HBox addParticipantBox = new HBox(textField, addNewParticipantsButton);
        addParticipantBox.setSpacing(10);

        VBox participantsBox = new VBox(addParticipantsLabel, participantsFlowPane, addParticipantBox);
        participantsBox.setSpacing(5);

        VBox centerBox = new VBox(groupNameBox, participantsBox);
        centerBox.setAlignment(Pos.CENTER_LEFT);
        centerBox.setSpacing(10);

        getOkButton().setOnAction(x -> {
            try {
                ChatFileHandler chatFileHandler = createGroup(members, groupNameTextField.getText());
                ChatPreviewWidget chatPreviewWidget = messengerWindow.getChatsSideBarWidget().createChatPreview(chatFileHandler);
                messengerWindow.setCenter(new ChatWidget(chatFileHandler, chatPreviewWidget));

                close(messengerWindow);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.setCenter(centerBox);
    }

    private boolean checkRecipient(String recipient) throws IOException {
        ServerKeyHandler serverKeyManager = new ServerKeyHandler();
        String key = serverKeyManager.getPublicKeyOf(recipient);
        serverKeyManager.close();

        return !key.isEmpty();
    }

    private ChatFileHandler createGroup(List<String> members, String groupName) throws IOException {
        JsonArray membersJsonArray = new JsonArray();
        for (String s : members) {
            membersJsonArray.add(s);
        }

        GroupHandler groupHandler = new GroupHandler(new UserInfoFileHandler().getUser());
        int groupId = groupHandler.createGroup(groupNameTextField.getText(), membersJsonArray);
        groupHandler.close();

        ChatsHandler chatsHandler = new ChatsHandler();
        String filename = chatsHandler.addGroupChat(groupId);
        chatsHandler.writeChanges();

        ChatFileHandler chatFileHandler = new ChatFileHandler(filename);
        chatFileHandler.createGroupChatFile(chatsHandler.getBiggestChatId(), groupId, groupName);
        chatFileHandler.writeChanges();

        return chatFileHandler;
    }

    @Override
    protected void setWindowUI() {

    }
}
