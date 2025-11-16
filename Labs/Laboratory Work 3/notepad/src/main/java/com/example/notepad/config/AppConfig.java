package com.example.notepad.config;

import com.example.notepad.model.Note;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class AppConfig {

    /**
     * Демонстрація анотації @Bean.
     * Ми створюємо бін AtomicLong, який буде 'singleton' (за замовчуванням).
     */
    @Bean
    public AtomicLong idCounter() {
        return new AtomicLong(0);
    }

    /**
     * Демонстрація скоупу 'prototype'.
     * Кожен раз, коли ми будемо просити у Spring бін "prototypeNote",
     * він буде створювати новий екземпляр класу Note.
     */
    @Bean
    @Scope("prototype")
    public Note prototypeNote() {
        return new Note();
    }
}