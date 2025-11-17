package com.example.notepad.mapper;

import com.example.notepad.dto.CreateNoteRequest;
import com.example.notepad.dto.NoteResponse;
import com.example.notepad.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapper {

    // Конвертує Entity -> Response DTO
    public NoteResponse toNoteResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setPriority(note.getPriority());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        return response;
    }

    // Конвертує Create DTO -> Entity
    public Note toNoteEntity(CreateNoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setPriority(request.getPriority());
        return note;
    }
}