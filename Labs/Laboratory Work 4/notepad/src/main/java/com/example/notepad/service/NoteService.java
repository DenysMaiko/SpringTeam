package com.example.notepad.service;

import com.example.notepad.dto.CreateNoteRequest;
import com.example.notepad.dto.NoteResponse;
import com.example.notepad.dto.PatchNoteRequest;
import com.example.notepad.dto.UpdateNoteRequest;
import com.example.notepad.exception.ResourceNotFoundException;
import com.example.notepad.mapper.NoteMapper;
import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import com.example.notepad.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper; // Додаємо маппер

    // Ін'єктуємо і репозиторій, і маппер
    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
    }

    // CRUD: READ з Фільтрацією та Пагінацією
    public List<NoteResponse> getAllNotes(Optional<Priority> priority, int page, int size) {

        Collection<Note> allNotes = noteRepository.findAll();

        // Фільтрація
        List<Note> filteredNotes = allNotes.stream()
                .filter(note -> priority.isEmpty() || note.getPriority() == priority.get())
                .collect(Collectors.toList());

        // Пагінація
        int fromIndex = page * size;

        if (fromIndex >= filteredNotes.size()) {
            return List.of(); // Повертаємо порожній список, якщо сторінка за межами
        }

        int toIndex = Math.min(fromIndex + size, filteredNotes.size());

        return filteredNotes.subList(fromIndex, toIndex).stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());
    }

    // CRUD: READ (by ID)
    public NoteResponse getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));
        return noteMapper.toNoteResponse(note);
    }

    // CRUD: CREATE (POST)
    public NoteResponse createNote(CreateNoteRequest request) {
        Note noteToSave = noteMapper.toNoteEntity(request);
        Note savedNote = noteRepository.save(noteToSave);
        return noteMapper.toNoteResponse(savedNote);
    }

    // CRUD: UPDATE (PUT)
    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        // Перевіряємо, чи існує нотатка
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));

        // Оновлюємо всі поля
        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());
        existingNote.setPriority(request.getPriority());
        // createdAt не чіпаємо, updatedAt оновиться в репозиторії

        Note updatedNote = noteRepository.save(existingNote);
        return noteMapper.toNoteResponse(updatedNote);
    }

    // CRUD: PARTIAL UPDATE (PATCH)
    public NoteResponse patchNote(Long id, PatchNoteRequest request) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));

        // Оновлюємо поля, тільки якщо вони прийшли в запиті
        request.getTitle().ifPresent(existingNote::setTitle);
        request.getContent().ifPresent(existingNote::setContent);
        request.getPriority().ifPresent(existingNote::setPriority);

        Note updatedNote = noteRepository.save(existingNote);
        return noteMapper.toNoteResponse(updatedNote);
    }

    // CRUD: DELETE
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Нотатку з id " + id + " не знайдено");
        }
        noteRepository.deleteById(id);
    }
}