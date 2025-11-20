package com.example.notepad.service;

import com.example.notepad.dto.*;
import com.example.notepad.exception.ResourceNotFoundException;
import com.example.notepad.mapper.NoteMapper;
import com.example.notepad.model.AuditLog;
import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import com.example.notepad.repository.AuditLogRepository;
import com.example.notepad.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final AuditLogRepository auditLogRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, AuditLogRepository auditLogRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.auditLogRepository = auditLogRepository;
        this.noteMapper = noteMapper;
    }

    public List<NoteResponse> getAllNotes(Optional<Priority> priority, int page, int size) {
        Collection<Note> allNotes;
        if (priority.isPresent()) {
            allNotes = noteRepository.findByPriority(priority.get());
        } else {
            allNotes = noteRepository.findAll();
        }

        List<Note> notesList = allNotes.stream().collect(Collectors.toList());

        int fromIndex = page * size;
        if (fromIndex >= notesList.size()) return List.of();
        int toIndex = Math.min(fromIndex + size, notesList.size());

        return notesList.subList(fromIndex, toIndex).stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse getNoteById(Long id) {
        return noteRepository.findById(id)
                .map(noteMapper::toNoteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));
    }

    // Транзакція: Або все збережеться (нотатка + лог), або нічого
    @Transactional
    public NoteResponse createNote(CreateNoteRequest request) {
        Note note = noteMapper.toNoteEntity(request);
        Note savedNote = noteRepository.save(note);

        // Записуємо дію в журнал аудиту
        auditLogRepository.save(new AuditLog("CREATED", savedNote.getId()));

        return noteMapper.toNoteResponse(savedNote);
    }

    @Transactional
    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));
        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());
        existingNote.setPriority(request.getPriority());
        Note updatedNote = noteRepository.save(existingNote);

        auditLogRepository.save(new AuditLog("UPDATED", updatedNote.getId()));

        return noteMapper.toNoteResponse(updatedNote);
    }

    @Transactional
    public NoteResponse patchNote(Long id, PatchNoteRequest request) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Нотатку з id " + id + " не знайдено"));
        request.getTitle().ifPresent(existingNote::setTitle);
        request.getContent().ifPresent(existingNote::setContent);
        request.getPriority().ifPresent(existingNote::setPriority);
        Note updatedNote = noteRepository.save(existingNote);

        auditLogRepository.save(new AuditLog("PATCHED", updatedNote.getId()));

        return noteMapper.toNoteResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Нотатку з id " + id + " не знайдено");
        }
        noteRepository.deleteById(id);
        auditLogRepository.save(new AuditLog("DELETED", id));
    }
}