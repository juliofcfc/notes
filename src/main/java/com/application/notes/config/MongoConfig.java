package com.application.notes.config;

import com.application.notes.model.Note;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Collation;

import java.util.Locale;

import static java.util.List.of;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void createIndexes() {
        mongoTemplate.indexOps(Note.class).ensureIndex(
                new Index("userId", Sort.Direction.ASC));
        mongoTemplate.indexOps(Note.class).ensureIndex(
                new Index("title", Sort.Direction.ASC)
                        .collation(Collation.of(Locale.US).strength(Collation.ComparisonLevel.primary())));
        mongoTemplate.indexOps(Note.class).ensureIndex(
                new Index("text", Sort.Direction.ASC)
                        .collation(Collation.of(Locale.US).strength(Collation.ComparisonLevel.primary())));
        mongoTemplate.indexOps(Note.class).ensureIndex(
                new Index()
                        .on("title", Sort.Direction.ASC)
                        .on("text", Sort.Direction.ASC)
                        .collation(Collation.of(Locale.US).strength(Collation.ComparisonLevel.primary())));
    }

}
