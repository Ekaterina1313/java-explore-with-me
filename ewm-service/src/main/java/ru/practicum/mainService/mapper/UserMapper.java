package ru.practicum.mainService.mapper;

import ru.practicum.mainService.dto.UserDto;
import ru.practicum.mainService.dto.UserShortDto;
import ru.practicum.mainService.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getEmail(),
                user.getId(),
                user.getName()
        );
    }

    public static User fromUserDto(UserDto userDto) {
        return new User(
                userDto.getEmail(),
                userDto.getId(),
                userDto.getName()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}