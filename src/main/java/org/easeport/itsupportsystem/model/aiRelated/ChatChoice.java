package org.easeport.itsupportsystem.model.aiRelated;

public class ChatChoice {
    private int index;
    private ChatMessage message;

    public ChatChoice(int index, ChatMessage message) {
        this.index = index;
        this.message = message;
    }

    public ChatChoice() {
    }

    public ChatChoice(ChatMessage message) {
        this.message = message;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }
}
