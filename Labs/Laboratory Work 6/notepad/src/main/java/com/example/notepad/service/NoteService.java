package com.example.notepad.service;

import com.example.notepad.dto.*;
import com.example.notepad.exception.ResourceNotFoundException;
import com.example.notepad.mapper.NoteMapper;
import com.example.notepad.model.AuditLog;
import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import com.example.notepad.repository.AuditLogRepository;
import com.example.notepad.repository.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        if (priority.isPresent()) {
            // Використовуємо наш Derived Query метод
            return noteRepository.findByPriority(priority.get()).stream()
                    .map(noteMapper::toNoteResponse)
                    .collect(Collectors.toList());
        }

        // Використовуємо вбудовану пагінацію Spring Data JPA
        Page<Note> notePage = noteRepository.findAll(PageRequest.of(page, size));

        return notePage.stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());
    }

    // Демонстрація методу з @Query
    public List<NoteResponse> searchByTitle(String keyword) {
        return noteRepository.searchByTitle(keyword).stream()
                .map(noteMapper::toNoteResponse)
                .toList();
    }

    public NoteResponse getNoteById(Long id) {
        return noteRepository.findById(id)
                .map(noteMapper::toNoteResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
    }

    @Transactional
    public NoteResponse createNote(CreateNoteRequest request) {
        Note note = noteMapper.toNoteEntity(request);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        Note savedNote = noteRepository.save(note);

        // Зберігаємо лог, прив'язуючи його до об'єкта Note
        auditLogRepository.save(new AuditLog("CREATED", savedNote));

        return noteMapper.toNoteResponse(savedNote);
    }

    @Transactional
    public NoteResponse updateNote(Long id, UpdateNoteRequest request) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());
        existingNote.setPriority(request.getPriority());
        existingNote.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepository.save(existingNote);
        auditLogRepository.save(new AuditLog("UPDATED", updatedNote));

        return noteMapper.toNoteResponse(updatedNote);
    }

    @Transactional
    public NoteResponse patchNote(Long id, PatchNoteRequest request) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));

        request.getTitle().ifPresent(existingNote::setTitle);
        request.getContent().ifPresent(existingNote::setContent);
        request.getPriority().ifPresent(existingNote::setPriority);
        existingNote.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepository.save(existingNote);
        auditLogRepository.save(new AuditLog("PATCHED", updatedNote));

        return noteMapper.toNoteResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note not found");
        }
        noteRepository.deleteById(id);
    }
}