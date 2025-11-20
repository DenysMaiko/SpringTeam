package com.example.notepad.dto;

import com.example.notepad.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateNoteRequest {

    @NotBlank(message = "Заголовок не може бути порожнім")
    @Size(min = 3, max = 100, message = "Довжина заголовка має бути від 3 до 100 символів")
    private String title;

    private String content;

    @NotNull(message = "Пріоритет не може бути null")
    private Priority priority;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}