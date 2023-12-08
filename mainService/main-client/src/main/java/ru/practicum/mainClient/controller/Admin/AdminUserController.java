package ru.practicum.mainClient.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.UserDto;
import ru.practicum.common.error.InvalidRequestException;
import ru.practicum.mainClient.client.Admin.AdminUserClient;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserClient userClient;

    public AdminUserController(AdminUserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление нового пользователя с email = " + userDto.getEmail());
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new InvalidRequestException("Field: name. Error: must not be blank. Value: null");
        }
        try {
            return userClient.create(userDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating user", ex);
            throw ex;
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@RequestParam(name = "from", defaultValue = "0") int from,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      @RequestParam(name = "ids", required = false) List<Integer> ids) {
        log.info("ADMIN-controller: Поступил запрос на получение пользователей с id = " + ids);
        return userClient.get(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> delete(@PathVariable Integer userId) {
        log.info("Поступил запрос на удаление пользователя с id = {}.", userId);
        return userClient.delete(userId);
    }
}