package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class RequestDto {
    private Integer id;
    private String description;
    private Integer requestorId;
    private LocalDateTime created;
}
