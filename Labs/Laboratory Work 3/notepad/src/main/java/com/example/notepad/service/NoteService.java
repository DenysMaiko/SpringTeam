package com.example.notepad.service;

import com.example.notepad.model.Note;
import java.util.concurrent.atomic.AtomicLong;
import com.example.notepad.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class NoteService {

    // Ін'єкція у поле Field Injection
    // Використовується для ApplicationContext.
    @Autowired
    private ApplicationContext context;

    // Ін'єкція через конструктор
    // Використовується для обов'язкової залежності NoteRepository.
    private final NoteRepository noteRepository;

    // Ін'єкція через сетер
    // Використовується для опціональної залежності SomeOtherService.
    private SomeOtherService otherService;

    // Конструктор для ін'єкції NoteRepository
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // Сетер для ін'єкції SomeOtherService
    @Autowired
    public void setOtherService(SomeOtherService otherService) {
        this.otherService = otherService;
    }

    // Методи бізнес-логіки

    public Collection<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    public Note saveNote(Note note) {
        // Викликаємо опціональний сервіс, якщо він є
        if (otherService != null) {
            otherService.doSomething();
        }
        return noteRepository.save(note);
    }

    public void deleteNote(Long id) {
        noteRepository.deleteById(id);
    }

    /**
     * Метод демонструє різницю між singleton та prototype.
     */
    public void checkScopes() {
        // Запитуємо 'prototype' бін двічі
        Note noteA = context.getBean("prototypeNote", Note.class);
        Note noteB = context.getBean("prototypeNote", Note.class);

        // Запитуємо singleton бін двічі
        AtomicLong counterA = context.getBean("idCounter", AtomicLong.class);
        AtomicLong counterB = context.getBean("idCounter", AtomicLong.class);

        System.out.println("Перевірка Scope");
        System.out.println("Prototype Note A == Note B: " + (noteA == noteB)); // Має бути false
        System.out.println("Singleton Counter A == Counter B: " + (counterA == counterB)); // Має бути true
        System.out.println("----------------------");
    }
}