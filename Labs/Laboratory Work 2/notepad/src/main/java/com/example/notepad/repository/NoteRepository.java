package com.example.notepad.repository;

import com.example.notepad.model.Note;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class NoteRepository {

    // Наша база даних у пам'яті
    private final Map<Long, Note> noteStorage = new ConcurrentHashMap<>();

    // Ін'єктуємо наш singleton бін лічильника
    private final AtomicLong idCounter;

    // Демонстрація ін'єкції через конструктор (для idCounter)
    public NoteRepository(AtomicLong idCounter) {
        this.idCounter = idCounter;
    }

    public Note save(Note note) {
        if (note.getId() == null) {
            long newId = idCounter.incrementAndGet();
            note.setId(newId);
            note.setCreatedAt(LocalDateTime.now());
            note.setUpdatedAt(LocalDateTime.now());
            noteStorage.put(newId, note);
            return note;
        } else {
            Note existingNote = noteStorage.get(note.getId());

            if (existingNote != null) {
                existingNote.setTitle(note.getTitle());
                existingNote.setContent(note.getContent());
                existingNote.setUpdatedAt(LocalDateTime.now());

                noteStorage.put(existingNote.getId(), existingNote);
                return existingNote;
            } else {
                note.setCreatedAt(LocalDateTime.now());
                note.setUpdatedAt(LocalDateTime.now());
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