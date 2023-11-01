package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {
    private Integer id;
    @NotBlank
    private String description;
    private Integer requestorId;
    private LocalDateTime created;

    public RequestDto(@JsonProperty("id") Integer id,
                      @JsonProperty("description") String description,
                      @JsonProperty("requestorId") Integer requestorId,
                      @JsonProperty("created") LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }
}
