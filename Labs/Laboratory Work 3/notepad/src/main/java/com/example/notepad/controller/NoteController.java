package com.example.notepad.controller;

import com.example.notepad.model.Note;
import com.example.notepad.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // Демонстрація анотації @Controller
public class NoteController {

    private final NoteService noteService;

    // Ін'єкція сервісу через конструктор
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // Головна сторінка - показує всі нотатки
    @GetMapping("/")
    public String listNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());

        // Викликаємо метод для демонстрації скоупів (результат в консолі)
        noteService.checkScopes();

        return "notes-list"; // Повертає назву HTML шаблону
    }

    // Показати форму для створення нової нотатки
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        return "note-form";
    }

    // Показати форму для редагування існуючої нотатки
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + id));
        model.addAttribute("note", note);
        return "note-form";
    }

    // Зберегти нотатку (нову або оновлену)
    @PostMapping("/save")
    public String saveNote(@ModelAttribute("note") Note note) {
        noteService.saveNote(note);
        return "redirect:/"; // Повернутись на головну сторінку
    }

    // Видалити нотатку
    @GetMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return "redirect:/";
    }
}