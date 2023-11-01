package ru.practicum.shareit.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public CommentDto(@JsonProperty("id") Integer id,
                      @JsonProperty("text") String text,
                      @JsonProperty("itemId") Integer itemId,
                      @JsonProperty("authorId") Integer authorId,
                      @JsonProperty("authorName") String authorName,
                      @JsonProperty("created") LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.created = created;
    }
}
