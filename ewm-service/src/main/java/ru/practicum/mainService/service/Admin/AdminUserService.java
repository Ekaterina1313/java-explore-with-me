package ru.practicum.mainService.service.Admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.dto.UserDto;
import ru.practicum.mainService.mapper.UserMapper;
import ru.practicum.mainService.model.User;
import ru.practicum.mainService.repository.UserRepository;
import ru.practicum.mainService.service.ValidationById;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final ValidationById validationById;

    @Autowired
    public AdminUserService(UserRepository userRepository, ValidationById validationById) {
        this.userRepository = userRepository;
        this.validationById = validationById;
    }

    public UserDto create(UserDto userDto) {
        User createdUser = userRepository.save(UserMapper.fromUserDto(userDto));
        return UserMapper.toUserDto(createdUser);
    }

    public List<UserDto> get(int from, int size, List<Integer> ids) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> page;
        if (ids == null || ids.isEmpty()) {
            page = userRepository.findAll(pageable);
        } else {
            page = userRepository.findUsersById(pageable, ids);
        }
        return page.getContent()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void delete(Integer userId) {
        validationById.getUserById(userId);
        userRepository.deleteById(userId);
    }
}