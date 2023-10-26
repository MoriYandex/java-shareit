package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private static final String REQUESTOR_NOT_FOUND_MESSAGE = "Не найден автор запроса!";
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final ItemService itemService;

    @Override
    public RequestDto add(RequestDto requestDto) {
        User requestor = userRepository.findById(requestDto.getRequestorId()).orElse(null);
        if (requestor == null) {
            log.error(REQUESTOR_NOT_FOUND_MESSAGE);
            throw new NotFoundException(REQUESTOR_NOT_FOUND_MESSAGE);
        }
        return requestMapper.toDto(requestRepository.save(requestMapper.fromDto(requestDto, requestor)));
    }

    @Override
    public RequestDto update(RequestDto requestDto, Integer requestId) {
        Request request = requestRepository.findById(requestId).orElse(null);
        if (request == null) {
            log.error("Не найден запрос по идентификатору {}", requestId);
            throw new NotFoundException(String.format("Не найден запрос по идентификатору %d!", requestId));
        }
        if (!Strings.isBlank(requestDto.getDescription()))
            request.setDescription(request.getDescription());
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto get(Integer requestId) {
        Request request = requestRepository.findById(requestId).orElse(null);
        if (request == null) {
            log.error("Не найден запрос по идентификатору {}", requestId);
            throw new NotFoundException(String.format("Не найден запрос по идентификатору %d!", requestId));
        }
        return requestMapper.toDto(request);
    }

    @Override
    public void delete(Integer requestId) {
        itemService.deleteItemRequests(requestId);
        requestRepository.deleteById(requestId);
    }

    @Override
    public List<RequestDto> getAllByUserId(Integer userId) {
        User requestor = userRepository.findById(userId).orElse(null);
        if (requestor == null) {
            log.error(REQUESTOR_NOT_FOUND_MESSAGE);
            throw new NotFoundException(REQUESTOR_NOT_FOUND_MESSAGE);
        }
        return requestRepository.findAllByRequestor(requestor).stream().map(requestMapper::toDto).collect(Collectors.toList());
    }
}
