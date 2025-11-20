package com.example.notepad.repository;

import com.example.notepad.model.Note;
import com.example.notepad.model.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByPriority(Priority priority);

    @Query("SELECT n FROM Note n WHERE n.title LIKE %:keyword%")
    List<Note> searchByTitle(@Param("keyword") String keyword);

    // Spring автоматично знайде NamedQuery "Note.findByContentLike"
    List<Note> findByContentLike(@Param("content") String content);
}