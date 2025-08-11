package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NewBadRequestException;
import ru.practicum.exception.NewConstraintViolationException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null) {
            users = repository.findByFromAndLimit(from, size);
        } else {
            users = repository.findByFromAndLimitAndIds(ids, from, size);
        }
        log.info("Получены пользователи: {}", users);
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public UserDto createUser(NewUserDto userDto) {
        if (userDto.getName() == null) {
            throw new NewBadRequestException("Field: name. Error: must not be blank. Value: null");
        } else if (userDto.getName().length() > 250 || userDto.getName().length() < 2 || userDto.getName().isBlank()) {
            throw new NewBadRequestException(String.format("Field: name. Error: it must be between 2 and 250 " +
                    "characters long. Long: %s", userDto.getName().length()));
        }
        if (userDto.getEmail() == null) {
            throw new NewBadRequestException("Field: email. Error: must not be blank. Value: null");
        } else if (userDto.getEmail().length() > 254 || userDto.getEmail().length() < 6 || userDto.getEmail().isBlank()) {
            throw new NewBadRequestException(String.format("Field: name. Error: it must be between 6 and 254 " +
                    "characters long. Long: %s", userDto.getEmail().length()));
        }
        if (!Pattern.compile("^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,63}\\.[A-Za-z0-9.-]{2,254}$")
                .matcher(userDto.getEmail()).matches()) {
            throw new NewBadRequestException("Field: email. Error: must be email.");
        }
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new NewConstraintViolationException("could not execute statement; SQL [n/a]; constraint " +
                    "[uq_category_name]; nested exception is org.hibernate.exception.ConstraintViolationException: " +
                    "could not execute statement", "CONFLICT", "Integrity constraint has been violated.");
        }
        User user = repository.save(UserMapper.toUser(userDto));
        log.info("Админ создает нового пользователя: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(Long userId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%s was not found", userId)));
        repository.delete(user);
        log.info("Админ удалил пользователя: {}", user);
    }
}
