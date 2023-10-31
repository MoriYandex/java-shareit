package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDtoExtended {
    private Integer id;
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
    private List<ItemDto> items;

    public RequestDtoExtended(@JsonProperty("id") Integer id,
                              @JsonProperty("description") String description,
                              @JsonProperty("requestorId") Integer requestorId,
                              @JsonProperty("created") LocalDateTime created,
                              @JsonProperty("items") List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
        this.items = items;
    }
}
