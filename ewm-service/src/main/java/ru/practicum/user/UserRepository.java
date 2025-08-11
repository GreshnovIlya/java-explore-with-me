package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
            SELECT *
            FROM users
            LIMIT ?2
            OFFSET ?1;
            """, nativeQuery = true)
    List<User> findByFromAndLimit(Integer from, Integer size);

    @Query(value = """
            SELECT *
            FROM users
            WHERE id in ?1
            LIMIT ?3
            OFFSET ?2;
            """, nativeQuery = true)
    List<User> findByFromAndLimitAndIds(List<Integer> ids, Integer from, Integer size);

    boolean existsByEmail(String email);
}
