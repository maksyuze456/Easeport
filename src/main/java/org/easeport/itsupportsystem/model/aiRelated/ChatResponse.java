package org.easeport.itsupportsystem.model.aiRelated;

import java.util.List;

public class ChatResponse {
    private List<ChatChoice> choices;

    public ChatResponse(List<ChatChoice> choices) {
        this.choices = choices;
    }

    public List<ChatChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ChatChoice> choices) {
        this.choices = choices;
    }
}
