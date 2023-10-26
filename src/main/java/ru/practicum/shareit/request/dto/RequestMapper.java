package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    public RequestDto toDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorId(request.getRequestor().getId())
                .build();
    }

    public Request fromDto(RequestDto requestDto, User requestor) {
        return Request.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requestor(requestor)
                .build();
    }

}
