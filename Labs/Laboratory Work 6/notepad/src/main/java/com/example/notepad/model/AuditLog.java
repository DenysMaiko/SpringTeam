package com.example.notepad.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    private LocalDateTime timestamp;

    // Зв'язок Many-to-One (Багато логів належать одній нотатці)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id") // Це колонка зовнішнього ключа в БД
    private Note note;

    public AuditLog() {}

    public AuditLog(String action, Note note) {
        this.action = action;
        this.note = note;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
}