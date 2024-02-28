package com.application.notes.repository;


import com.application.notes.model.Note;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NoteCustomRepository {

    private final MongoTemplate mongoTemplate;

    public Page<Note> findNotes(Pageable pageable, String title, String text, String userId) {
        Query query = new Query();
        if (StringUtils.isNotBlank(userId)) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (StringUtils.isNotBlank(title)) {
            query.addCriteria(Criteria.where("title").regex(".*" + title + ".*", "i"));
        }
        if (StringUtils.isNotBlank(text)) {
            query.addCriteria(Criteria.where("text").regex(".*" + text + ".*", "i"));
        }
        long count = mongoTemplate.count(query, Note.class);
        List<Note> notes = mongoTemplate.find(query.with(pageable), Note.class);
        return PageableExecutionUtils.getPage(notes, pageable, () -> count);
    }

}
