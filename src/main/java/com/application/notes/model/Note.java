package com.application.notes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("notes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private String id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String text;

    @NotEmpty
    private String userId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @CreatedDate
    private Date createdAt;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @LastModifiedDate
    private Date updateAt;
}
