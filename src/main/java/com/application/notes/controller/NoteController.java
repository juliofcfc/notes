package com.application.notes.controller;

import com.application.notes.model.Note;
import com.application.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;


    @PostMapping
    public Note createNote(@RequestBody @Valid Note note) {
        return noteService.createNote(note);
    }

    @GetMapping
    @PageableAsQueryParam
    public Page<Note> findNotes(@Parameter(hidden = true) Pageable pageable,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String text,
                                @RequestParam(required = false) String userId) {
        return noteService.findNotes(pageable, title, text, userId);
    }

    @GetMapping("/{id}")
    public Note findNoteById(@PathVariable String id) {
        return noteService.findNoteById(id);
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable String id, @RequestBody @Valid Note note) {
        note.setId(id);
        return noteService.updateNote(note);
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable String id) {
        noteService.deleteNote(id);
    }

}
