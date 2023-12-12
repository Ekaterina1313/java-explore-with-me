package ru.practicum.mainService.controller.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.dto.UserDto;
import ru.practicum.mainService.error.InvalidRequestException;
import ru.practicum.mainService.service.Admin.AdminUserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
public class AdminUserController {
    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("ADMIN-controller: Поступил запрос на добавление нового пользователя с email = " + userDto.getEmail());
        String email = userDto.getEmail();
        if (userDto.getName() == null || userDto.getName().isBlank() ||
                userDto.getName().length() < 2 || userDto.getName().length() > 254) {
            throw new InvalidRequestException("Field: name. Error: must not be null or blank. Value: null");
        }
        if (email == null || email.isBlank() ||
                email.length() < 6 || email.length() > 64) {
            throw new InvalidRequestException("Field: email. Error: must not be null or blank. Value: null");
        }
        if (!email.contains("@")) {
            throw new InvalidRequestException("Field: email. Error: must contain the @ symbol. Value: " + email);
        }
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[1].length() > 63) {
            throw new InvalidRequestException("Field: email. Error: after @ should be no more than 63 characters." +
                    " Value: " + email);
        }
        if (emailParts[0].length() > 64) {
            throw new InvalidRequestException("Field: email. Error: before @ should be no more than 63 characters." +
                    " Value: " + email);
        }
        try {
            return userService.create(userDto);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error creating user", ex);
            throw ex;
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get(@RequestParam(name = "from", defaultValue = "0") int from,
                             @RequestParam(name = "size", defaultValue = "10") int size,
                             @RequestParam(name = "ids", required = false) List<Integer> ids) {
        log.info("ADMIN-controller: Поступил запрос на получение пользователей с id = " + ids);
        return userService.get(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable Integer userId) {
        log.info("Поступил запрос на удаление пользователя с id = {}.", userId);
        userService.delete(userId);
        return ResponseEntity.status(204).body("Пользователь удален");
    }
}