package com.application.notes.controller;

import com.application.notes.model.Note;
import com.application.notes.repository.NoteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NoteControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    public void beforeEach() {
        noteRepository.deleteAll();
    }

    @Test
    public void testCreateNote() throws Exception {
        Note note = buildNote();
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(note))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Note createdNote = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), Note.class);
        assertNotNull(createdNote.getId());
        assertNotNull(createdNote.getCreatedAt());
        assertNotNull(createdNote.getUpdateAt());
        assertEquals(note.getTitle(), createdNote.getTitle());
        assertEquals(note.getText(), createdNote.getText());
    }

    @Test
    public void testCreateNoteEmptyTitle() throws Exception {
        Note note = Note.builder()
                .text("This is my note used for testing")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(note))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateNoteEmptyText() throws Exception {
        Note note = Note.builder()
                .title("My note")
                .build();
        mvc.perform(MockMvcRequestBuilders.post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(note))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateNote() throws Exception {
        Note note = buildNote();
        noteRepository.insert(note);
        note.setTitle("My updated note");
        note.setText("This is my note used for testing after the update");
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/notes/" + note.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(note))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Note createdNote = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), Note.class);
        assertEquals(note.getTitle(), createdNote.getTitle());
        assertEquals(note.getText(), createdNote.getText());
    }

    @Test
    public void testUpdateNoteNotFound() throws Exception {
        Note note = buildNote();
        noteRepository.insert(note);
        note.setTitle("My updated note");
        note.setText("This is my note used for testing after the update");
        mvc.perform(MockMvcRequestBuilders.put("/notes/invalidId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(note))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNoteNotFound() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/notes/invalidId")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteNote() throws Exception {
        Note note = buildNote();
        noteRepository.insert(note);
        mvc.perform(MockMvcRequestBuilders.delete("/notes/" + note.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
       assertFalse(noteRepository.existsById(note.getId()));
    }

    @Test
    public void testFindNoteById() throws Exception {
        Note note = buildNote();
        noteRepository.insert(note);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/notes/" + note.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Note createdNote = OBJECT_MAPPER.readValue(result.getResponse().getContentAsString(), Note.class);
        assertEquals(note.getTitle(), createdNote.getTitle());
        assertEquals(note.getText(), createdNote.getText());
    }

    @Test
    public void testFindNoteByIdNotFound() throws Exception {
        Note note = buildNote();
        noteRepository.insert(note);
        mvc.perform(MockMvcRequestBuilders.get("/notes/invalidId")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindNotes() throws Exception {
        int totalNotes = 20;
        for (int i = 0; i < totalNotes; i++) {
            Note note = Note.builder()
                    .title("My Note " + i)
                    .text("This is my note used for testing " + i)
                    .userId("user-" + i)
                    .build();
            noteRepository.insert(note);
        }
        testFindPagedNotes(totalNotes, 0, 5, "");
        testFindPagedNotes(totalNotes, 2, 5, "&title=My");
        testFindPagedNotes(totalNotes, 0, 10, "&text=note");
        testFindPagedNotes(totalNotes, 0, 100, "&title=Note&text=UseD");
        testFindPagedNotes(1, 0, 10, "&title=10");
        testFindPagedNotes(1, 0, 10, "&text=15");
        testFindPagedNotes(2, 0, 10, "&text=3");
        testFindPagedNotes(11, 0, 20, "&title=1");
        testFindPagedNotes(1, 0, 10, "&userId=user-1");
    }

    private void testFindPagedNotes(int totalNotes, int page, int size, String params) throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get(String.format("/notes?page=%d&size=%d%s", page, size, params))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        JsonNode notesPage = OBJECT_MAPPER.readTree(result.getResponse().getContentAsString());
        Assertions.assertEquals(Math.min(size, totalNotes), notesPage.get("content").size());
        Assertions.assertEquals(Math.max(totalNotes/size, 1), notesPage.get("totalPages").asInt());
        Assertions.assertEquals(totalNotes, notesPage.get("totalElements").asInt());
        Assertions.assertEquals(page, notesPage.get("number").asInt());
    }

    private static Note buildNote() {
        return Note.builder()
                .title("My Note")
                .text("This is my note used for testing")
                .userId("user-1")
                .build();
    }

}
