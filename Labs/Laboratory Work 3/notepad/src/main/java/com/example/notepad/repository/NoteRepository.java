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
    private final AtomicLong idCounter;

    public NoteRepository(AtomicLong idCounter) {
        this.idCounter = idCounter;
    }

    public Note save(Note note) {
        if (note.getId() == null) {
            // Логіка створення картки з пріоритетом
            long newId = idCounter.incrementAndGet();
            note.setId(newId);
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());

            // Якщо пріоритет не встановлено, ставимо середній
            if (note.getPriority() == null) {
                note.setPriority(Priority.MEDIUM);
            }
            noteStorage.put(newId, note);
            return note;
        } else {
            // Логіка оновлення існуючої картки
            Note existingNote = noteStorage.get(note.getId());

            if (existingNote != null) {
                existingNote.setTitle(note.getTitle());
                existingNote.setContent(note.getContent());
                existingNote.setUpdatedAt(LocalDateTime.now());
                existingNote.setPriority(note.getPriority()); // Оновлюємо пріоритет

                noteStorage.put(existingNote.getId(), existingNote);
                return existingNote;
            } else {
                note.setCreatedAt(LocalDateTime.now());
                note.setUpdatedAt(LocalDateTime.now());
                if (note.getPriority() == null) {
                    note.setPriority(Priority.MEDIUM);
                }
                noteStorage.put(note.getId(), note);
                return note;
            }
        }
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
}