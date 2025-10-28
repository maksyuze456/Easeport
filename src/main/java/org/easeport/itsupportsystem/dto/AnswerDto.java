package org.easeport.itsupportsystem.dto;

public class AnswerDto {
    private String message;

    public AnswerDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
