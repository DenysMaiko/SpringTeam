package com.example.notepad.repository;

import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class NoteRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Note> noteRowMapper = (rs, rowNum) -> {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setPriority(Priority.valueOf(rs.getString("priority")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) note.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) note.setUpdatedAt(updatedAt.toLocalDateTime());

        return note;
    };

    public Note save(Note note) {
        if (note.getId() == null) {
            return create(note);
        } else {
            return update(note);
        }
    }

    private Note create(Note note) {
        if (note.getCreatedAt() == null) note.setCreatedAt(LocalDateTime.now());
        if (note.getUpdatedAt() == null) note.setUpdatedAt(LocalDateTime.now());

        String sql = "INSERT INTO notes (title, content, priority, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.setString(3, note.getPriority().name());
            ps.setTimestamp(4, Timestamp.valueOf(note.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.valueOf(note.getUpdatedAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            note.setId(key.longValue());
        }
        return note;
    }

    private Note update(Note note) {
        note.setUpdatedAt(LocalDateTime.now());

        String sql = "UPDATE notes SET title=?, content=?, priority=?, updated_at=? WHERE id=?";
        jdbcTemplate.update(sql,
                note.getTitle(),
                note.getContent(),
                note.getPriority().name(),
                Timestamp.valueOf(note.getUpdatedAt()),
                note.getId());
        return note;
    }

    public Optional<Note> findById(Long id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
        List<Note> notes = jdbcTemplate.query(sql, noteRowMapper, id);
        return notes.stream().findFirst();
    }

    public Collection<Note> findAll() {
        return jdbcTemplate.query("SELECT * FROM notes", noteRowMapper);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM notes WHERE id = ?", id);
    }

    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM notes WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public List<Note> findByPriority(Priority priority) {
        String sql = "SELECT * FROM notes WHERE priority = ?";
        return jdbcTemplate.query(sql, noteRowMapper, priority.name());
    }
}