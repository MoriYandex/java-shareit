package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Integer id;
    @NotBlank
    private String text;
    private Integer itemId;
    private Integer authorId;
    private String authorName;
    private LocalDateTime created;
}
