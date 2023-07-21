package ru.practicum.ewmservice.entities.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.entities.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
