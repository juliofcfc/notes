package com.application.notes.service;

import com.application.notes.model.Note;
import com.application.notes.repository.NoteCustomRepository;
import com.application.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteCustomRepository noteCustomRepository;

    public Note createNote(Note note) {
        log.info("Create note: {}", note);
        return noteRepository.insert(note);
    }

    public Note updateNote(Note note) {
        log.info("Update note: {}", note);
        if (note == null || StringUtils.isBlank(note.getId())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(400), "Note ID is required");
        }
        Note noteFromDb = findNoteById(note.getId());
        noteFromDb.setText(note.getText());
        noteFromDb.setTitle(note.getTitle());
        noteFromDb.setUserId(note.getUserId());
        return noteRepository.save(noteFromDb);
    }

    public void deleteNote(String id) {
        log.info("Delete note: {}", id);
        Note note = findNoteById(id);
        noteRepository.delete(note);
    }

    public Note findNoteById(String id) {
        log.info("Find note: {}", id);
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatusCode.valueOf(404), "Note not found"));
    }

    public Page<Note> findNotes(Pageable pageable, String title, String text, String userId) {
        log.info("Find notes. Filters: title={} | text={} | userId: {}", title, text, userId);
        return noteCustomRepository.findNotes(pageable, title, text, userId);
    }
}
