package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = """
            SELECT *
            FROM categories
            LIMIT ?2
            OFFSET ?1;
            """, nativeQuery = true)
    List<Category> findByFromAndLimit(Integer from, Integer size);

    List<Category> findByName(String name);
}
