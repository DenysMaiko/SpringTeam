package com.example.notepad.dto;

import com.example.notepad.model.Priority;
import jakarta.validation.constraints.Size;
import java.util.Optional;

public class PatchNoteRequest {

    @Size(min = 3, max = 100, message = "Довжина заголовка має бути від 3 до 100 символів")
    private String title;

    private String content;

    private Priority priority;

    // Спеціальні Getters
    public Optional<String> getTitle() {
        return Optional.ofNullable(title);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<Priority> getPriority() {
        return Optional.ofNullable(priority);
    }

    // Звичайні Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}