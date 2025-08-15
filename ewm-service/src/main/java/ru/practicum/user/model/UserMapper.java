package ru.practicum.user.model;

import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

public class UserMapper {
    public static User toUser(NewUserDto userDto) {
        return new User(0L,
                userDto.getName(),
                userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(),
                user.getName());
    }
}
