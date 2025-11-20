package com.example.notepad.controller;

import com.example.notepad.dto.CreateNoteRequest;
import com.example.notepad.dto.NoteResponse;
import com.example.notepad.dto.PatchNoteRequest;
import com.example.notepad.dto.UpdateNoteRequest;
import com.example.notepad.model.Priority;
import com.example.notepad.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notepad API", description = "API для керування нотатками")
public class NoteRestController {

    private final NoteService noteService;

    public NoteRestController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Отримати всі нотатки (з пагінацією та фільтром)", responses = @ApiResponse(responseCode = "200"))
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(
            @Parameter(description = "Фільтр за пріоритетом") @RequestParam Optional<Priority> priority,
            @Parameter(description = "Номер сторінки (з 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Розмір сторінки") @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(noteService.getAllNotes(priority, page, size));
    }

    @Operation(summary = "Отримати нотатку за ID", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Не знайдено", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @Operation(summary = "Створити нову нотатку", responses = {
            @ApiResponse(responseCode = "201", description = "Створено"),
            @ApiResponse(responseCode = "400", description = "Помилка валідації", content = @Content)
    })
    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody CreateNoteRequest request) {
        NoteResponse createdNote = noteService.createNote(request);
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @Operation(summary = "Повністю оновити нотатку (PUT)", responses = {
            @ApiResponse(responseCode = "200", description = "Оновлено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено", content = @Content),
            @ApiResponse(responseCode = "400", description = "Помилка валідації", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long id, @Valid @RequestBody UpdateNoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(id, request));
    }

    @Operation(summary = "Частково оновити нотатку (PATCH)", responses = {
            @ApiResponse(responseCode = "200", description = "Оновлено"),
            @ApiResponse(responseCode = "404", description = "Не знайдено", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<NoteResponse> patchNote(@PathVariable Long id, @Valid @RequestBody PatchNoteRequest request) {
        return ResponseEntity.ok(noteService.patchNote(id, request));
    }

    @Operation(summary = "Видалити нотатку", responses = {
            @ApiResponse(responseCode = "204", description = "Видалено", content = @Content),
            @ApiResponse(responseCode = "404", description = "Не знайдено", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }
}