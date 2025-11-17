package com.example.notepad.repository;

import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NoteRepository {

    private final Map<Long, Note> noteStorage = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Note save(Note note) {
        // Час "updatedAt" ставиться завжди
        // Час "createdAt" ставиться лише при створенні

        if (note.getId() == null) {
            // --- ЛОГІКА СТВОРЕННЯ ---
            long newId = idCounter.incrementAndGet();
            note.setId(newId);
            note.setCreatedAt(LocalDateTime.now());
            if (note.getPriority() == null) {
                note.setPriority(Priority.MEDIUM);
            }
        } else {
            // Оновлюємо поля, які прийшли.
            // createdAt вже існує і ми його не чіпаємо.
        }

        note.setUpdatedAt(LocalDateTime.now()); // Ставимо час оновлення
        noteStorage.put(note.getId(), note);
        return note;
    }

    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(noteStorage.get(id));
    }

    public Collection<Note> findAll() {
        return noteStorage.values();
    }

    public void deleteById(Long id) {
        noteStorage.remove(id);
    }

    public boolean existsById(Long id) {
        return noteStorage.containsKey(id);
    }
}